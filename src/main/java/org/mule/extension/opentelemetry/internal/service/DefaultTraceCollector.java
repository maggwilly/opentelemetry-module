package org.mule.extension.opentelemetry.internal.service;

import io.opentelemetry.api.trace.*;
import io.opentelemetry.context.Context;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import org.mule.extension.opentelemetry.internal.context.ContextManager;
import org.mule.extension.opentelemetry.trace.FlowSpan;
import org.mule.extension.opentelemetry.trace.SpanWrapper;
import org.mule.extension.opentelemetry.trace.Transaction;
import org.mule.extension.opentelemetry.trace.TransactionContext;
import org.mule.extension.opentelemetry.util.OplConstants;
import org.mule.extension.opentelemetry.util.OplUtils;
import org.mule.runtime.api.component.location.ComponentLocation;
import org.mule.runtime.api.exception.MuleException;
import org.mule.runtime.api.lifecycle.Stoppable;
import org.mule.runtime.api.util.MultiMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;


public class DefaultTraceCollector implements TraceCollector , Stoppable {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultTraceCollector.class);

    private final Map<String, Transaction> transactionMap = new ConcurrentHashMap<>();

    private final ContextManager contextManager;
    private final Tracer tracer;
    private final SdkTracerProvider tracerProvider;

    public DefaultTraceCollector(String configName, SdkTracerProvider tracerProvider, ContextManager contextManager) {
        this.contextManager = contextManager;
        this.tracerProvider = tracerProvider;
        tracer = this.tracerProvider.get(configName, "1.0.0");
    }

    @Override
    public void startTransaction(SpanWrapper spanWrapper) {
        LOGGER.trace("Opening transaction - {}", spanWrapper);
        String transactionId = OplUtils.createTransactionId(spanWrapper.getEventId(), spanWrapper.getComponentLocation());
        Optional<Transaction> optionalTransaction = getTransaction(transactionId);
        if (optionalTransaction.isPresent()) {
            Transaction transaction = optionalTransaction.get();
            this.updateTransaction(transaction, spanWrapper.getSpan());
            TransactionContext transactionContext = TransactionContext.of(transaction);
            contextManager.store(transactionContext.getContext(), spanWrapper.getEventId());
            return;
        }
        String parentTransactionId = OplUtils.getParentTransactionId(spanWrapper.getEventId());
        Context parent = contextManager.retrieve(parentTransactionId);
        Transaction transaction1 = createTransaction(spanWrapper, parent);
        TransactionContext transactionContext = TransactionContext.of(transaction1);
        contextManager.store(transactionContext.getContext(), spanWrapper.getEventId());
    }

    private Transaction createTransaction(SpanWrapper trace, Context context) {
        String transactionId = OplUtils.createTransactionId(trace.getEventId(), trace.getComponentLocation());
        LOGGER.info("Creating  Transaction  - {}", transactionId);
        SpanBuilder spanBuilder = createSpanBuilder(trace.getComponentLocation()).setParent(context);
        FlowSpan flowSpan = trace.getSpan();
        MultiMap<String, String> attributes = flowSpan.getAttributes();
        attributes.forEach(spanBuilder::setAttribute);
        Transaction transaction = new Transaction(transactionId, spanBuilder.startSpan());
        this.saveTransaction(transaction);
        return transaction;
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



    private void updateTransaction(Transaction transaction, FlowSpan flowSpan) {
        LOGGER.info("Updating transaction - {}", flowSpan);
        if (Objects.nonNull(transaction.getSpan())) {
            Span span = transaction.getSpan();
            MultiMap<String, String> attributes = flowSpan.getAttributes();
            attributes.forEach(span::setAttribute);
            if (Objects.nonNull(flowSpan.getName())) {
                span.updateName(flowSpan.getName());
            }
            this.saveTransaction(transaction);
        }
    }


    @Override
    public Optional<Transaction> endTransaction(String eventId, ComponentLocation componentLocation) {
        String transactionId = OplUtils.createTransactionId(eventId, componentLocation);
        LOGGER.trace("Ending transaction - {}", transactionId);
        return getTransaction(transactionId).map(transaction -> {
            Transaction remove = transactionMap.remove(transaction.getId());
            Span span = remove.getSpan();
            if (Objects.nonNull(span)) {
                transaction.setStatusCode(StatusCode.OK);
                span.end();
                LOGGER.trace("End of span - {} ", span);
            }
            return remove;
        });
    }

    @Override
    public Optional<Transaction> endTransaction(String eventId, ComponentLocation componentLocation, Exception exception) {
        String transactionId = OplUtils.createTransactionId(eventId, componentLocation);
        LOGGER.trace("Ending transaction - {} - error {}", transactionId, exception.getMessage());
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
        LOGGER.trace("Searching Transaction - {} ", transactionId);
        Optional<String> keyByLocation = getKeyByLocation(transactionId);
        return keyByLocation.map(key -> {
            Transaction transaction = transactionMap.get(key);
            transaction.setStatusCode(StatusCode.ERROR);
            return transaction;
        });
    }

    private Optional<String> getKeyByLocation(String transactionId) {
        String[] strings = transactionId.split(OplConstants.ID_BRIDGE);
        return transactionMap.keySet().stream().filter(key -> key.contains(strings[0])).findAny();
    }

    @Override
    public void stop() throws MuleException {
        this.tracerProvider.close();
    }
}
