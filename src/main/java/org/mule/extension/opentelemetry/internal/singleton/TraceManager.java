package org.mule.extension.opentelemetry.internal.singleton;

import io.opentelemetry.api.trace.*;
import io.opentelemetry.context.Context;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import org.mule.extension.opentelemetry.api.SpanContextHolder;
import org.mule.extension.opentelemetry.internal.OpenTelemetryConfiguration;
import org.mule.extension.opentelemetry.internal.provider.TracingManager;
import org.mule.extension.opentelemetry.trace.FlowSpan;
import org.mule.extension.opentelemetry.trace.SpanWrapper;
import org.mule.extension.opentelemetry.trace.Transaction;
import org.mule.extension.opentelemetry.trace.TransactionContext;
import org.mule.extension.opentelemetry.util.OplConstants;
import org.mule.extension.opentelemetry.util.OplUtils;
import org.mule.runtime.api.component.location.ComponentLocation;
import org.mule.runtime.api.store.ObjectStore;
import org.mule.runtime.api.store.ObjectStoreManager;
import org.mule.runtime.api.util.MultiMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;


public class TraceManager implements TracingManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(TraceManager.class);
    public static final String ID_BRIDGE = "::";
    private final Map<String, Transaction> transactionMap = new ConcurrentHashMap<>();
    @Inject
    private OpenTelemetryProvider openTelemetryProvider;
    @Inject
    private ObjectStoreManager objectStoreManager;
    @Inject
    private ContextService contextService;
    private Tracer tracer;

    @Override
    public void startTransaction(SpanContextHolder source, SpanWrapper trace) {
        LOGGER.trace("Setting context from   {}", source);
        // extract and store parent context
        Context context = contextService.extractContext(source);
        String parentTransactionId = OplUtils.getParentTransactionId(trace.getEventId());
        contextService.storeLocally(context, parentTransactionId);
        startTransaction(trace);
    }

    @Override
    public void startTransaction(SpanWrapper trace) {
        LOGGER.trace("Opening transaction - {}", trace);
        String transactionId = createTransactionId(trace.getEventId(), trace.getComponentLocation());
        getTransaction(transactionId).ifPresent(transaction -> {
            String parentTransactionId = OplUtils.getParentTransactionId(trace.getEventId());
            Context context = contextService.retrieveLocally(parentTransactionId);
            FlowSpan flowSpan = trace.getSpan();

            this.updateTransaction(transaction, context, trace.getSpan());
            TransactionContext transactionContext = TransactionContext.of(transaction);
            contextService.storeLocally(transactionContext.getContext(), trace.getEventId());

            ObjectStore objectStore = flowSpan.getPropagator();
            contextService.store(objectStore,transactionContext.getContext(),flowSpan.getContextId());
        });
    }

    @Override
    public void createTransaction(String eventId, ComponentLocation componentLocation) {
        String transactionId = createTransactionId(eventId, componentLocation);
        LOGGER.trace("Opening  Transaction  - {}", transactionId);
        SpanBuilder spanBuilder = createSpanBuilder(componentLocation);
        Transaction transaction = new Transaction(transactionId, spanBuilder);
        saveTransaction(transaction);
    }

    private void saveTransaction(Transaction transaction) {
        LOGGER.trace("Saving - {}", transaction);
        transactionMap.put(transaction.getId(), transaction);
    }

    private SpanBuilder createSpanBuilder(ComponentLocation componentLocation) {
        String rootContainerName = componentLocation.getRootContainerName();
        LOGGER.trace("Creating  Span builder for - {}", rootContainerName);
        return tracer.spanBuilder(rootContainerName)
                .setSpanKind(SpanKind.SERVER)
                .setStartTimestamp(Instant.now());
    }

    private static String createTransactionId(String eventId, ComponentLocation componentLocation) {
        String rootContainerName = componentLocation.getRootContainerName();
        return OplUtils.getParentTransactionId(eventId) + ID_BRIDGE + rootContainerName;
    }

    private void updateTransaction(Transaction transaction, Context context, FlowSpan flowSpan) {
        SpanBuilder spanBuilder = transaction.getSpanBuilder().setParent(context);
        MultiMap<String, String> attributes = flowSpan.getAttributes();
        attributes.forEach(spanBuilder::setAttribute);
        Span startSpan = spanBuilder.startSpan();
        LOGGER.trace("Creating  Span  - {} - started", startSpan);
        if(Objects.nonNull(flowSpan.getName())) {
            startSpan.updateName(flowSpan.getName());
        }
        this.saveTransaction(transaction.setSpan(startSpan));
    }


    @Override
    public Optional<Transaction> endTransaction(String eventId, ComponentLocation componentLocation) {
        String transactionId = createTransactionId(eventId, componentLocation);
        LOGGER.info("Ending transaction - {}", transactionId);
        return getTransaction(transactionId).map(transaction -> {
            Transaction remove = transactionMap.remove(transaction.getId());
            Span span = remove.getSpan();
            if (Objects.nonNull(span)) {
                span.end();
                LOGGER.trace("End of span - {} ", span);
            }
            return remove;
        });
    }

    @Override
    public Optional<Transaction> endTransaction(String eventId, ComponentLocation componentLocation, Exception exception) {
        String transactionId = createTransactionId(eventId, componentLocation);
        LOGGER.warn("Ending transaction - {} - error {}", transactionId, exception.getMessage());
        return getTransaction(transactionId).map(transaction -> {
            Transaction remove = transactionMap.remove(transaction.getId());
            Span span = remove.getSpan();
            if (Objects.nonNull(span)) {
                span.recordException(exception);
                span.setAttribute(OplConstants.ERROR_MESSAGE.getKey(), exception.getMessage());
                span.setStatus(transaction.getStatusCode(), exception.getMessage());
                span.end();
                LOGGER.trace("End of span - {} ", span);
            }
            return remove;
        });
    }

    private Optional<Transaction> getTransaction(String transactionId) {
        Transaction transaction = transactionMap.get(transactionId);
        if (Objects.nonNull(transaction)) {
            return Optional.of(transaction);
        }
        return searchTransaction(transactionId);
    }

    private Optional<Transaction> searchTransaction(String transactionId) {
        LOGGER.info("Searching Transaction - {} ", transactionId);
        Optional<String> keyByLocation = getKeyByLocation(transactionId);
        return keyByLocation.map(key -> {
            Transaction transaction = transactionMap.get(key);
            transaction.setStatusCode(StatusCode.ERROR);
            return transaction;
        });
    }

    private Optional<String> getKeyByLocation(String transactionId) {
        String[] strings = transactionId.split(ID_BRIDGE);
        return transactionMap.keySet().stream().filter(key -> key.contains(strings[0])).findAny();
    }

    @Override
    public void initialise(OpenTelemetryConfiguration configuration) {
        SdkTracerProvider tracerProvider = openTelemetryProvider.getTracerProvider();
        tracer = tracerProvider.get(configuration.getConfigName(), "1.0.0");
    }

}
