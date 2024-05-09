package org.mule.extension.opentelemetry.internal;

import org.mule.extension.opentelemetry.api.SpanContextHolder;
import org.mule.extension.opentelemetry.trace.FlowSpan;
import org.mule.extension.opentelemetry.trace.SpanWrapper;
import org.mule.extension.opentelemetry.util.OplUtils;
import org.mule.runtime.api.component.location.ComponentLocation;
import org.mule.runtime.api.meta.model.operation.ExecutionType;
import org.mule.runtime.api.util.MultiMap;
import org.mule.runtime.extension.api.annotation.execution.Execution;
import org.mule.runtime.extension.api.annotation.param.*;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;
import org.mule.runtime.extension.api.runtime.parameter.CorrelationInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.Map;

import static org.mule.runtime.extension.api.annotation.param.MediaType.ANY;

public class OpenTelemetryOperations {
    private final Logger LOGGER = LoggerFactory.getLogger(OpenTelemetryOperations.class);

    private final OpenTelemetryConnection openTelemetryConnection;
    @Inject
    public OpenTelemetryOperations(@Connection OpenTelemetryConnection openTelemetryConnection) {
        this.openTelemetryConnection = openTelemetryConnection;
    }

    @Execution(ExecutionType.CPU_LITE)
    @MediaType(value = ANY, strict = false)
    public void addToMetric(@Content Map<String, Object> values, long duration) {
        openTelemetryConnection.getMetricCollector().observe(values, duration);
    }

    @Execution(ExecutionType.CPU_LITE)
    @MediaType(value = ANY, strict = false)
    public void createInContext(@Optional SpanContextHolder parent,
                                @Content @Optional @DisplayName("Attributes") MultiMap<String, String> tags,
                                CorrelationInfo correlationInfo, ComponentLocation componentLocation) {
        FlowSpan span = new FlowSpan().setName(componentLocation.getRootContainerName()).setAttributes(tags).setContextId(correlationInfo.getEventId());
        SpanWrapper trace = OplUtils.createSpan(span, correlationInfo.getEventId(), componentLocation);
        openTelemetryConnection.getTraceCollector().startTransaction(parent, trace);
    }

    @Execution(ExecutionType.CPU_LITE)
    @MediaType(value = ANY, strict = false)
    public void createSpan(@ParameterGroup(name = "Span") FlowSpan span,CorrelationInfo correlationInfo, ComponentLocation componentLocation) {
        SpanWrapper trace = OplUtils.createSpan(span, correlationInfo.getEventId(), componentLocation);
        openTelemetryConnection.getTraceCollector().startTransaction(trace);
    }



}
