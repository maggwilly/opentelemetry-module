package org.mule.extension.opentelemetry.internal;

import io.opentelemetry.api.trace.SpanKind;
import org.mule.extension.opentelemetry.api.SpanContextHolder;
import org.mule.extension.opentelemetry.internal.provider.TracingManager;
import org.mule.extension.opentelemetry.internal.singleton.MetricCollector;
import org.mule.extension.opentelemetry.trace.FlowSpan;
import org.mule.extension.opentelemetry.trace.SpanWrapper;
import org.mule.runtime.api.component.location.ComponentLocation;
import org.mule.runtime.api.meta.model.operation.ExecutionType;
import org.mule.runtime.api.store.ObjectStoreManager;
import org.mule.runtime.api.util.MultiMap;
import org.mule.runtime.extension.api.annotation.execution.Execution;
import org.mule.runtime.extension.api.annotation.param.Content;
import org.mule.runtime.extension.api.annotation.param.MediaType;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.ParameterGroup;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;
import org.mule.runtime.extension.api.runtime.parameter.CorrelationInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.Map;

import static org.mule.runtime.extension.api.annotation.param.MediaType.ANY;

public class OpenTelemetryOperations {
    private final Logger LOGGER = LoggerFactory.getLogger(OpenTelemetryOperations.class);

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
    public void createInContext(@Optional SpanContextHolder parent,
                                @Content @Optional @DisplayName("Attributes") MultiMap<String, String> tags,
                                CorrelationInfo correlationInfo, ComponentLocation componentLocation) {
        FlowSpan span = new FlowSpan().setName(componentLocation.getRootContainerName()).setAttributes(tags).setContextId(correlationInfo.getEventId());
        SpanWrapper trace = getSpan(span, correlationInfo, componentLocation);
        tracingManager.startTransaction(parent, trace);
    }

    @Execution(ExecutionType.CPU_LITE)
    @MediaType(value = ANY, strict = false)
    public void createSpan(@ParameterGroup(name = "Span") FlowSpan span,CorrelationInfo correlationInfo, ComponentLocation componentLocation) {
        SpanWrapper trace = getSpan(span, correlationInfo, componentLocation);
        tracingManager.startTransaction(trace);
    }

    private SpanWrapper getSpan(FlowSpan span, CorrelationInfo correlationInfo, ComponentLocation componentLocation) {
        LOGGER.info("Event Id {}", correlationInfo.getEventId());
        return new SpanWrapper(span)
                .setComponentLocation(componentLocation)
                .setEventId(correlationInfo.getEventId())
                .setSpanKind(SpanKind.SERVER);
    }

}
