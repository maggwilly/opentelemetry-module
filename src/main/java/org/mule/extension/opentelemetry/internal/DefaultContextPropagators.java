package org.mule.extension.opentelemetry.internal;

import io.opentelemetry.api.baggage.propagation.W3CBaggagePropagator;
import io.opentelemetry.api.trace.propagation.W3CTraceContextPropagator;
import io.opentelemetry.context.propagation.ContextPropagators;
import io.opentelemetry.context.propagation.TextMapPropagator;

public class DefaultContextPropagators implements ContextPropagators {
    @Override
    public TextMapPropagator getTextMapPropagator() {
        return TextMapPropagator.composite(W3CTraceContextPropagator.getInstance(), W3CBaggagePropagator.getInstance());
    }
}
