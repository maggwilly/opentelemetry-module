package org.mule.extension.opentelemetry.module.internal.singleton;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanBuilder;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.propagation.TextMapGetter;
import io.opentelemetry.context.propagation.TextMapPropagator;
import io.opentelemetry.context.propagation.TextMapSetter;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import org.mule.extension.opentelemetry.module.api.ObjectStoreContextHolder;
import org.mule.extension.opentelemetry.module.api.SpanContextHolder;
import org.mule.extension.opentelemetry.module.api.TextMapContextHolder;
import org.mule.extension.opentelemetry.module.internal.OpenTelemetryConfiguration;
import org.mule.extension.opentelemetry.module.internal.OplInitialisable;
import org.mule.extension.opentelemetry.module.internal.config.TracingConfig;
import org.mule.extension.opentelemetry.module.internal.provider.TracingManager;
import org.mule.extension.opentelemetry.module.trace.*;
import org.mule.runtime.api.store.ObjectStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class TraceManager implements TracingManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(TraceManager.class);
    private final Map<String, Transaction> transactionMap = new ConcurrentHashMap<>();
    @Inject
    private OpenTelemetryProvider openTelemetryProvider;
    private Tracer tracer;
    @Override
    public void openTransaction(SpanWrapper trace, TracingConfig tracingConfig) {
        LOGGER.info("Opening transaction - {}", trace);
        try {

        Context traceContext = this.getTraceContext(trace.getContextHolder(),tracingConfig);
        final Transaction transaction = this.createTransaction(trace, traceContext);
        transactionMap.put(transaction.getId(), transaction);
        TransactionContext transactionContext = TransactionContext.of(transaction.getSpan());
        storeContext(trace, tracingConfig, transactionContext);
        }catch (Exception e){
            LOGGER.error("Error creating transaction - {}", e, e);
        }
    }

    private void storeContext(SpanWrapper trace, TracingConfig tracingConfig, TransactionContext transactionContext) {
        LOGGER.info("Storing  transaction Context - {}", transactionContext);
        ObjectStore objectStore = tracingConfig.getObjectStore();
        String contextId = trace.getSpan().getContextId();
        ContextObjectStoreSetter contextObjectStoreSetter = new ContextObjectStoreSetter(contextId);
        this.injectTraceContext(transactionContext.getContext(), objectStore, contextObjectStoreSetter);
    }

    private Context getTraceContext(SpanContextHolder contextHolder, TracingConfig tracingConfig) {
        LOGGER.info("Getting  parent context - {}", contextHolder);
        if(Objects.nonNull(contextHolder)) {
            if (contextHolder instanceof TextMapContextHolder) {
                Map<String, String> stringMap = ((TextMapContextHolder) contextHolder).getValue();
                return getTraceContext(stringMap, ContextMapGetter.INSTANCE);
            }
            ObjectStoreContextHolder storeContextHolder = (ObjectStoreContextHolder) contextHolder;
            ObjectStore objectStore = tracingConfig.getObjectStore();
            ContextObjectStoreGetter textMapGetter = new ContextObjectStoreGetter(storeContextHolder.getContextId());
            return getTraceContext(objectStore, textMapGetter);
        }
        return Context.current();
    }



    private Transaction createTransaction(SpanWrapper trace, Context traceContext) {
        LOGGER.info("Creating  Transaction for - {}", trace);
        SpanBuilder spanBuilder = createSpanBuilder(trace).setParent(traceContext);
        trace.getTags().forEach(spanBuilder::setAttribute);
        LOGGER.info("Starting span  Transaction for - {}", trace);
        Span span = spanBuilder.startSpan();
        String traceId = span.getSpanContext().getTraceId();
        LOGGER.info("Create  span  traceId for - {}", traceId);
        return new Transaction(trace.getTransactionId(),span,traceId,trace.getStartTime());
    }

    private SpanBuilder createSpanBuilder(SpanWrapper trace) {
        String name = trace.getSpan().getName();
        String spanName = name==null? trace.getComponentLocation().getLocation(): name;
        LOGGER.info("Creating  Span builder for - {}", spanName);
        return tracer.spanBuilder(spanName)
                .setSpanKind(trace.getSpanKind())
                .setStartTimestamp(trace.getStartTime());
    }

    @Override
    public void closeTransaction(SpanWrapper spanWrapper) {
        LOGGER.trace("Closing transaction - {}", spanWrapper);
        Transaction transaction = getTransaction(spanWrapper.getTransactionId());
        if (Objects.nonNull(transaction)) {
            Transaction remove = transactionMap.remove(transaction.getId());
            Span span = remove.getSpan();
            span.end();
            remove.setEndTime(spanWrapper.getEndTime());
        }
    }

    private Transaction getTransaction(String transactionId) {
        return transactionMap.get(transactionId);
    }

    @Override
    public void initialise(OpenTelemetryConfiguration configuration) {
        SdkTracerProvider tracerProvider = openTelemetryProvider.getTracerProvider();
        tracer = tracerProvider.get(configuration.getConfigName(), "1.0.0");
    }

    public <T> Context getTraceContext(T carrier, TextMapGetter<T> textMapGetter) {
        TextMapPropagator textMapPropagator = openTelemetryProvider.getContextPropagators().getTextMapPropagator();
        return textMapPropagator.extract(Context.current(), carrier, textMapGetter);
    }

    public <T> void injectTraceContext(Context context, T carrier, TextMapSetter<T> textMapSetter) {
        TextMapPropagator textMapPropagator = openTelemetryProvider.getContextPropagators().getTextMapPropagator();
        textMapPropagator.inject(context, carrier, textMapSetter);
    }
}
