package org.mule.extension.opentelemetry.internal.service;

import java.util.Map;

public interface MetricCollector{
    void observe(Map<String, Object> values, long duration);
}
