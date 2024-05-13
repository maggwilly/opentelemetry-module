package org.mule.extension.opentelemetry.internal.service;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.common.AttributesBuilder;
import io.opentelemetry.api.metrics.LongCounter;
import io.opentelemetry.api.metrics.Meter;
import io.opentelemetry.context.Context;
import org.mule.extension.opentelemetry.util.OplUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultMetricCollector implements MetricCollector{
    private final Logger LOGGER = LoggerFactory.getLogger(DefaultMetricCollector.class);
    private final Map<String, LongCounter> counterMap = new ConcurrentHashMap<>();
    ;
    private final Meter meter;
    private final String configName;

    public DefaultMetricCollector(String configName, OpenTelemetry openTelemetry ) {
        this.configName = configName;
        meter = openTelemetry.getMeter(configName);
    }

    public void count(String name, Map<String, Object> values, Context context) {
        LOGGER.trace("Adding metric {}", values);
        LongCounter longCounter = counterMap.computeIfAbsent(name, s -> createCounter(name));
        longCounter.add(1, toAttributes(values), context);
    }

    private LongCounter createCounter(String name) {
        return meter.counterBuilder(name)
                .setDescription(name)
                .build();
    }

    public void count(Map<String, Object> values, String name) {
        String counterName = OplUtils.createCounterName(this.configName, name);
        count(counterName, values, Context.current());
    }

    private  Attributes toAttributes(Map<String, Object> values) {
        AttributesBuilder attributes = Attributes.builder();
        values.forEach((key, u) -> attributes.put(key, String.valueOf(u)));
        return attributes.build();
    }

}
