package org.mule.extension.opentelemetry.module.internal.singleton;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanBuilder;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.propagation.TextMapGetter;
import io.opentelemetry.context.propagation.TextMapPropagator;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import org.mule.extension.opentelemetry.module.internal.OpenTelemetryConfiguration;
import org.mule.extension.opentelemetry.module.internal.OplInitialisable;
import org.mule.extension.opentelemetry.module.trace.Trace;
import org.mule.extension.opentelemetry.module.trace.Transaction;
import org.mule.extension.opentelemetry.module.internal.provider.TransactionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class TraceManager implements TransactionManager, OplInitialisable {
    private static final Logger LOGGER = LoggerFactory.getLogger("monitoring.opentelemetry.logger");
    private final Map<String, Transaction> transactionMap = new ConcurrentHashMap<>();
    @Inject
    private OpenTelemetryProvider openTelemetryProvider;
    private Tracer tracer;

    @Override
    public void openTransaction(Trace trace) {
        LOGGER.trace("Opening transaction - {}", trace);
        final Transaction transaction = createTransaction(trace);
        transactionMap.put(transaction.getId(), transaction);
    }

    private Transaction createTransaction(Trace trace) {
        SpanBuilder spanBuilder = createSpanBuilder(trace);
        trace.getTags().forEach(spanBuilder::setAttribute);
        Span span = spanBuilder.startSpan();
        String traceId = span.getSpanContext().getTraceId();
        return new Transaction(trace.getTransactionId(),span,traceId,trace.getStartTime());
    }

    private SpanBuilder createSpanBuilder(Trace trace) {
        return tracer.spanBuilder(trace.getName())
                .setSpanKind(trace.getSpanKind())
                .setParent(trace.getContext())
                .setStartTimestamp(trace.getStartTime());
    }

    @Override
    public void closeTransaction(Trace trace) {
        LOGGER.trace("Closing transaction - {}", trace);
        Transaction transaction = getTransaction(trace.getTransactionId());
        if (Objects.nonNull(transaction)) {
            Transaction remove = transactionMap.remove(transaction.getId());
            Span span = remove.getSpan();
            span.end();
            remove.setEndTime(trace.getEndTime());
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
