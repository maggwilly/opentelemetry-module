package org.mule.extension.opentelemetry.internal;

import org.mule.extension.opentelemetry.internal.service.ConnectionHolder;
import org.mule.extension.opentelemetry.trace.FlowSpan;
import org.mule.extension.opentelemetry.trace.Metric;
import org.mule.extension.opentelemetry.trace.SpanWrapper;
import org.mule.extension.opentelemetry.util.OplUtils;
import org.mule.runtime.api.component.location.ComponentLocation;
import org.mule.runtime.api.meta.model.operation.ExecutionType;
import org.mule.runtime.api.util.MultiMap;
import org.mule.runtime.extension.api.annotation.execution.Execution;
import org.mule.runtime.extension.api.annotation.param.MediaType;
import org.mule.runtime.extension.api.annotation.param.ParameterGroup;
import org.mule.runtime.extension.api.runtime.parameter.CorrelationInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.mule.runtime.extension.api.annotation.param.MediaType.ANY;

public class OpenTelemetryOperations {
    private final Logger LOGGER = LoggerFactory.getLogger(OpenTelemetryOperations.class);
    @Inject
    private  ConnectionHolder<OpenTelemetryConnection> connectionHolder;


    @Execution(ExecutionType.CPU_LITE)
    @MediaType(value = ANY, strict = false)
    public void addToMetric(@ParameterGroup(name = "Metric") Metric metric) {
        connectionHolder.getConnection().getMetricCollector().count(getMultiMap(metric), metric.getName());
    }

    private  MultiMap<String, Object> getMultiMap(Metric metric) {
        return metric.getAttributes().entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, this::asObject, (s, s2) -> s2, MultiMap::new));
    }

    private Object asObject(Map.Entry<String, String> data) {
        return Objects.nonNull(data.getValue()) ? data.getValue() : "null";
    }

    @Execution(ExecutionType.CPU_LITE)
    @MediaType(value = ANY, strict = false)
    public void setAttributes(@ParameterGroup(name = "Span") FlowSpan span,  CorrelationInfo correlationInfo, ComponentLocation componentLocation) {
        SpanWrapper wrapper = OplUtils.createSpan(span, correlationInfo.getEventId(), componentLocation);
        connectionHolder.getConnection().getTraceCollector().startTransaction(wrapper);
    }


}
