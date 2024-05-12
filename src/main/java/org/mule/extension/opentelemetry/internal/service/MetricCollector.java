package org.mule.extension.opentelemetry.internal.service;

import io.opentelemetry.context.Context;

import java.util.Map;

public interface MetricCollector{
    void count(Map<String, Object> values, String name);
    void count(String name, Map<String, Object> values, Context context);
}
