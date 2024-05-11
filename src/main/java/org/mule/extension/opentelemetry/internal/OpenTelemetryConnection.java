package org.mule.extension.opentelemetry.internal;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.instrumentation.runtimemetrics.java8.*;
import org.mule.extension.opentelemetry.internal.config.MetricConfiguration;
import org.mule.extension.opentelemetry.internal.config.TracingConfiguration;
import org.mule.extension.opentelemetry.internal.service.MetricCollector;
import org.mule.extension.opentelemetry.internal.service.TraceCollector;
import org.mule.runtime.api.exception.MuleException;
import org.mule.runtime.core.api.lifecycle.LifecycleUtils;

public class OpenTelemetryConnection{
    private final MetricCollector metricCollector;
    private final TraceCollector traceCollector;
    private final  OpenTelemetry openTelemetry;

    private  final MetricConfiguration metricConfiguration;
    private final  TracingConfiguration tracingConfiguration;
    public OpenTelemetryConnection(OpenTelemetry openTelemetry, MetricCollector metricCollector, TraceCollector traceCollector,MetricConfiguration metricConfiguration, TracingConfiguration tracingConfiguration) {
        this.metricCollector = metricCollector;
        this.traceCollector = traceCollector;
        this.openTelemetry = openTelemetry;
        this.metricConfiguration = metricConfiguration;
        this.tracingConfiguration= tracingConfiguration;
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

    public MetricConfiguration getMetricConfiguration() {
        return metricConfiguration;
    }

    public TracingConfiguration getTracingConfiguration() {
        return tracingConfiguration;
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
                ", metricConfiguration=" + metricConfiguration +
                ", tracingConfiguration=" + tracingConfiguration +
                '}';
    }

    public void stop() throws MuleException {
        LifecycleUtils.stopIfNeeded(metricCollector);
        LifecycleUtils.stopIfNeeded(traceCollector);
        LifecycleUtils.stopIfNeeded(metricConfiguration);
    }
}
