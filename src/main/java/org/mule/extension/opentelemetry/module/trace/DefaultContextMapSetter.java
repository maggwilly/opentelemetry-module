package org.mule.extension.opentelemetry.module.trace;

import io.opentelemetry.context.propagation.TextMapSetter;

import java.util.Map;
import java.util.Objects;

public enum DefaultContextMapSetter implements TextMapSetter<Map<String, String>> {
    INSTANCE;
    @Override
    public void set(Map<String, String> carrier, String key, String value) {
        if (Objects.nonNull(carrier)) {
            carrier.put(key, value);
        }
    }
}
