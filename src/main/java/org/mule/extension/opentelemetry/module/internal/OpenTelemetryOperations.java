package org.mule.extension.opentelemetry.module.internal;

import io.opentelemetry.api.trace.SpanKind;
import org.mule.extension.opentelemetry.module.api.SpanContextHolder;
import org.mule.extension.opentelemetry.module.internal.provider.TracingManager;
import org.mule.extension.opentelemetry.module.internal.singleton.MetricCollector;
import org.mule.extension.opentelemetry.module.trace.FlowSpan;
import org.mule.extension.opentelemetry.module.trace.SpanWrapper;
import org.mule.runtime.api.component.location.ComponentLocation;
import org.mule.runtime.api.meta.model.operation.ExecutionType;
import org.mule.runtime.api.store.ObjectStoreManager;
import org.mule.runtime.extension.api.annotation.Expression;
import org.mule.runtime.extension.api.annotation.dsl.xml.ParameterDsl;
import org.mule.runtime.extension.api.annotation.execution.Execution;
import org.mule.runtime.extension.api.annotation.param.*;
import org.mule.runtime.extension.api.runtime.parameter.CorrelationInfo;

import javax.inject.Inject;
import java.util.Map;

import static org.mule.runtime.api.meta.ExpressionSupport.NOT_SUPPORTED;
import static org.mule.runtime.extension.api.annotation.param.MediaType.ANY;

public class OpenTelemetryOperations {
    @Inject
    private ObjectStoreManager objectStoreManager;
    @Inject
    private MetricCollector metricCollector;

    @Inject
    private TracingManager tracingManager;
    ;

    @Execution(ExecutionType.CPU_LITE)
    @MediaType(value = ANY, strict = false)
    public void addToMetric(@Content Map<String, Object> values, long duration) {
        metricCollector.observe(values, duration);
    }


    @Execution(ExecutionType.CPU_LITE)
    @MediaType(value = ANY, strict = false)
    public void createSpan(@ParameterGroup(name = "Span") FlowSpan span,
                           @Optional SpanContextHolder parent,
                           @Expression(value = NOT_SUPPORTED) @ParameterDsl(allowInlineDefinition = false) @Config OpenTelemetryConfiguration configuration,
                           CorrelationInfo correlationInfo, ComponentLocation componentLocation) {
        SpanWrapper trace = getSpan(span, parent, correlationInfo, componentLocation);
        tracingManager.openTransaction(trace, configuration.getTracingConfig());
    }

    private SpanWrapper getSpan(FlowSpan span, SpanContextHolder parent, CorrelationInfo correlationInfo, ComponentLocation componentLocation) {
        return new SpanWrapper(span)
                .setComponentLocation(componentLocation)
                .setContextHolder(parent)
                .setTransactionId(correlationInfo.getEventId())
                .setTags(span.getAttributes())
                .setSpanKind(SpanKind.SERVER);
    }


}
