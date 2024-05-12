package org.mule.extension.opentelemetry.internal;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.instrumentation.runtimemetrics.java8.*;
import org.mule.extension.opentelemetry.internal.config.MetricConfig;
import org.mule.extension.opentelemetry.internal.config.TracingConfig;
import org.mule.extension.opentelemetry.internal.service.MetricCollector;
import org.mule.extension.opentelemetry.internal.service.TraceCollector;
import org.mule.runtime.api.exception.MuleException;
import org.mule.runtime.core.api.lifecycle.LifecycleUtils;

public class OpenTelemetryConnection{
    private final MetricCollector metricCollector;
    private final TraceCollector traceCollector;
    private final  OpenTelemetry openTelemetry;

    private  final MetricConfig metricConfig;
    private final TracingConfig tracingConfig;
    public OpenTelemetryConnection(OpenTelemetry openTelemetry, MetricCollector metricCollector, TraceCollector traceCollector, MetricConfig metricConfig, TracingConfig tracingConfig) {
        this.metricCollector = metricCollector;
        this.traceCollector = traceCollector;
        this.openTelemetry = openTelemetry;
        this.metricConfig = metricConfig;
        this.tracingConfig = tracingConfig;
    }

    public OpenTelemetryConnection start() {
        MemoryPools.registerObservers(openTelemetry);
        BufferPools.registerObservers(openTelemetry);
        Classes.registerObservers(openTelemetry);
        Cpu.registerObservers(openTelemetry);
        Threads.registerObservers(openTelemetry);
        GarbageCollector.registerObservers(openTelemetry);
        return this;
    }

    public MetricConfig getMetricConfig() {
        return metricConfig;
    }

    public TracingConfig getTracingConfig() {
        return tracingConfig;
    }

    public MetricCollector getMetricCollector() {
        return metricCollector;
    }

    public TraceCollector getTraceCollector() {
        return traceCollector;
    }

    @Override
    public String toString() {
        return "OpenTelemetryConnection {" +
                ", metricConfiguration=" + metricConfig +
                ", tracingConfiguration=" + tracingConfig +
                '}';
    }

    public void stop() throws MuleException {
        LifecycleUtils.stopIfNeeded(metricCollector);
        LifecycleUtils.stopIfNeeded(traceCollector);
        LifecycleUtils.stopIfNeeded(metricConfig);
    }
}
