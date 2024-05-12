package org.mule.extension.opentelemetry.internal.service;

import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.common.AttributesBuilder;
import io.opentelemetry.api.metrics.LongCounter;
import io.opentelemetry.api.metrics.Meter;
import io.opentelemetry.context.Context;
import io.opentelemetry.sdk.metrics.SdkMeterProvider;
import org.mule.extension.opentelemetry.util.OplUtils;
import org.mule.runtime.api.exception.MuleException;
import org.mule.runtime.api.lifecycle.Stoppable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultMetricCollector implements MetricCollector, Stoppable {
    private final Logger LOGGER = LoggerFactory.getLogger(DefaultMetricCollector.class);
    private final Map<String, LongCounter> counterMap = new ConcurrentHashMap<>();

    private final SdkMeterProvider meterProvider;
    private final Meter meter;
    private final String configName;

    public DefaultMetricCollector(String configName, SdkMeterProvider meterProvider) {
        this.meterProvider = meterProvider;
        this.configName = configName;
        meter = createMeter(this.meterProvider, this.configName);
    }

    private Meter createMeter(SdkMeterProvider meterProvider, String name) {
        return meterProvider.meterBuilder(name)
                .setInstrumentationVersion("1.0.0").build();
    }

    @Override
    public void stop() throws MuleException {
        meterProvider.close();
    }

    public void count(String name, Map<String, Object> values, Context context) {
        LongCounter longCounter = counterMap.computeIfAbsent(name, s -> createCounter(name));
        longCounter.add(1, toAttributes(values), context);
    }

    private LongCounter createCounter(String name) {
        return meter.counterBuilder(name)
                .setDescription(name)
                .build();
    }

    public void count(Map<String, Object> values, String name) {
        LOGGER.trace("Adding metric {}", values);
        String counterName = OplUtils.createCounterName(this.configName, name);
        count(counterName, values, Context.current());
    }

    private static Attributes toAttributes(Map<String, Object> values) {
        AttributesBuilder attributes = Attributes.builder();
        values.forEach((key, u) -> attributes.put(key, "" + u));
        return attributes.build();
    }
}
