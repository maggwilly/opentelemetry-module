package org.mule.extension.opentelemetry.module.internal;

import io.opentelemetry.context.propagation.TextMapPropagator;

import java.util.Map;

public interface TraceContextPropagator {

    Map<String, String> getAttributes();
    TextMapPropagator getTxtMapPropagator();
}
