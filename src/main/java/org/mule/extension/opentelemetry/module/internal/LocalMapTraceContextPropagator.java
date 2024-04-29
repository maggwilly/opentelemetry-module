package org.mule.extension.opentelemetry.module.internal;

import io.opentelemetry.api.trace.propagation.W3CTraceContextPropagator;
import io.opentelemetry.context.propagation.TextMapPropagator;
import org.mule.runtime.extension.api.annotation.param.Content;
import org.mule.runtime.extension.api.annotation.param.Parameter;

import java.util.Map;

public class LocalMapTraceContextPropagator implements TraceContextPropagator {
    @Content
    @Parameter
    private Map<String, String> attributes;

    public Map<String, String> getAttributes() {
        return attributes;
    }

    @Override
    public TextMapPropagator getTxtMapPropagator() {
        return W3CTraceContextPropagator.getInstance();
    }

    @Override
    public PropagatorType getType() {
        return PropagatorType.W3_LOCAL;
    }

    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }

}
