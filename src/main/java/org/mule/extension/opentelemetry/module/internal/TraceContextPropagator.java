package org.mule.extension.opentelemetry.module.internal;

import io.opentelemetry.context.propagation.TextMapPropagator;

import java.util.Map;

public interface TraceContextPropagator {
    static enum PropagatorType{
        W3_LOCAL,
        JSON_DISTRIBUTED
    }
    Map<String, String> getAttributes();
    TextMapPropagator getTxtMapPropagator();
    PropagatorType getType();
}
