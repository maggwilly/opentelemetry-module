package org.mule.extension.opentelemetry.module.trace;

import io.opentelemetry.context.propagation.TextMapGetter;

import java.util.Map;

public  enum DefaultContextMapGetter implements TextMapGetter<Map<String, String>> {
    INSTANCE;

    @Override
    public Iterable<String> keys(Map<String, String> map) {
        return map.keySet();
    }

    @Override
    public String get(Map<String, String> map, String s) {
        return map == null ? null : map.get(s);
    }
}