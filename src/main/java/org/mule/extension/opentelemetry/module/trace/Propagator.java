package org.mule.extension.opentelemetry.module.trace;

import io.opentelemetry.context.propagation.TextMapGetter;
import io.opentelemetry.context.propagation.TextMapSetter;

import java.util.Map;

public interface Propagator{

    TextMapGetter<Map<String, String>> getter(String contextId);

    TextMapSetter<Map<String, String>> setter(String contextId);
}
