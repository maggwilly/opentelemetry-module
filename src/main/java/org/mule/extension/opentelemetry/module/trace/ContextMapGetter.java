package org.mule.extension.opentelemetry.module.trace;

import io.opentelemetry.context.propagation.TextMapGetter;

import javax.annotation.Nullable;
import java.util.Map;

public enum ContextMapGetter implements TextMapGetter<Map<String, String>> {
    INSTANCE;

    @Override
    public Iterable<String> keys(Map<String, String> map) {
        return map.keySet();
    }

    @Nullable
    @Override
    public String get(@Nullable Map<String, String> map, String s) {
        return map == null ? null : map.get(s);
    }
}