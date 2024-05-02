package org.mule.extension.opentelemetry.module.trace;

import io.opentelemetry.context.propagation.TextMapGetter;
import io.opentelemetry.context.propagation.TextMapSetter;

import java.util.Map;

public interface Propagator{
    static enum PropagatorType{
        HTTP_REST
    }
    PropagatorType getType();


    TextMapGetter<Map<String, String>> getter();

    TextMapSetter<Map<String, String>> setter();
}
