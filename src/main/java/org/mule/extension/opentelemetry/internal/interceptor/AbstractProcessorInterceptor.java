package org.mule.extension.opentelemetry.internal.interceptor;

import org.mule.extension.opentelemetry.internal.OpenTelemetryConnection;
import org.mule.extension.opentelemetry.internal.context.ContextManager;
import org.mule.extension.opentelemetry.internal.service.ConnectionHolder;
import org.mule.extension.opentelemetry.internal.service.TraceCollector;
import org.mule.extension.opentelemetry.trace.SpanEvent;
import org.mule.runtime.api.component.location.ComponentLocation;
import org.mule.runtime.api.interception.InterceptionEvent;
import org.mule.runtime.api.interception.ProcessorInterceptor;
import org.mule.runtime.api.interception.ProcessorParameterValue;
import org.mule.runtime.api.util.MultiMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;


public abstract class AbstractProcessorInterceptor implements ProcessorInterceptor {
    private static final Logger LOGGER = LoggerFactory.getLogger(ContextVarsPropagateProcessorInterceptor.class);

    protected final ConnectionHolder<OpenTelemetryConnection> connectionHolder;
    protected final ContextManager contextManager;

    public AbstractProcessorInterceptor(ConnectionHolder<OpenTelemetryConnection> connectionHolder, ContextManager contextManager) {
        this.connectionHolder = connectionHolder;
        this.contextManager = contextManager;
    }

    @Override
    public void before(ComponentLocation location, Map<String, ProcessorParameterValue> parameters, InterceptionEvent event) {
        String eventId = event.getContext().getId();
        TraceCollector traceCollector = connectionHolder.getConnection().getTraceCollector();
        MultiMap<String, String> attributes = toAttributes(parameters);

        ProcessorParameterValue docName = parameters.get("doc:name");

        SpanEvent spanEvent = new SpanEvent().setAttributes(attributes).setName(docName.providedValue()).setLocation(location).setEventId(eventId);
        traceCollector.addEvent(spanEvent);
    }

    protected MultiMap<String, String> toAttributes(Map<String, ProcessorParameterValue> parameters) {
        return    parameters.entrySet().stream().filter(entry -> {
            Object resolveValue = entry.getValue().resolveValue();
            if(Objects.nonNull(resolveValue)){
                return (resolveValue instanceof String);
            }
            return true;
        }).collect(Collectors.toMap(Map.Entry::getKey, this::getValue, (s, s2) -> s2, MultiMap::new));
    }

    private String getValue(Map.Entry<String, ProcessorParameterValue> data) {
        ProcessorParameterValue value = data.getValue();
        if(Objects.nonNull(value)) {
            Object resolveValue = value.resolveValue();
            return Objects.nonNull(resolveValue)? resolveValue.toString(): "null" ;
        }
        return "null";
    }
    @Override
    public void after(ComponentLocation location, InterceptionEvent event, Optional<Throwable> thrown) {

    }
}
