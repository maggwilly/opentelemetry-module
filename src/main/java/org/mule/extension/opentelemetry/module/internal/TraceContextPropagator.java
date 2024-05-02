package org.mule.extension.opentelemetry.module.internal;

import io.opentelemetry.context.propagation.TextMapGetter;
import io.opentelemetry.context.propagation.TextMapPropagator;
import io.opentelemetry.context.propagation.TextMapSetter;

import java.util.Map;

public interface TraceContextPropagator {
    Map<String, String> getAttributes();
    TextMapPropagator getTxtMapPropagator();

    TextMapGetter<Map<String, String>> getter();

    TextMapSetter<Map<String, String>> Setter();
}
