package org.mule.extension.opentelemetry.module.internal.singleton;

import com.google.common.base.Strings;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanBuilder;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.propagation.TextMapGetter;
import io.opentelemetry.context.propagation.TextMapPropagator;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import org.mule.extension.opentelemetry.module.api.SpanContextHolder;
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

public class TraceManager implements TracingManager, OplInitialisable {
    private static final Logger LOGGER = LoggerFactory.getLogger(TraceManager.class);
    private final Map<String, Transaction> transactionMap = new ConcurrentHashMap<>();
    @Inject
    private OpenTelemetryProvider openTelemetryProvider;
    private Tracer tracer;
    @Override
    public void openTransaction(SpanWrapper trace, SpanContextHolder patent, TracingConfig tracingConfig) {
        LOGGER.trace("Opening transaction - {}", trace);


        ObjectStore objectStore = tracingConfig.getObjectStore();
        String contextId = trace.getSpan().getContextId();
        ContextObjectStoreGetter textMapGetter = new ContextObjectStoreGetter(contextId);
        Context traceContext = this.getTraceContext(objectStore, textMapGetter);
        final Transaction transaction = createTransaction(trace, traceContext);
        transactionMap.put(transaction.getId(), transaction);



    }

    private Transaction createTransaction(SpanWrapper trace, Context traceContext) {
        SpanBuilder spanBuilder = createSpanBuilder(trace).setParent(traceContext);
        trace.getTags().forEach(spanBuilder::setAttribute);
        Span span = spanBuilder.startSpan();
        String traceId = span.getSpanContext().getTraceId();
        return new Transaction(trace.getTransactionId(),span,traceId,trace.getStartTime());
    }

    private SpanBuilder createSpanBuilder(SpanWrapper trace) {
        String name = trace.getSpan().getName();
        String spanName = Strings.isNullOrEmpty(name)? trace.getComponentLocation().getLocation(): name;
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
}
