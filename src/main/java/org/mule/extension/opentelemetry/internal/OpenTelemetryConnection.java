package org.mule.extension.opentelemetry.internal;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.instrumentation.runtimemetrics.java8.*;
import org.mule.extension.opentelemetry.internal.service.ContextPropagator;
import org.mule.extension.opentelemetry.internal.service.MetricCollector;
import org.mule.extension.opentelemetry.internal.service.TraceCollector;

public class OpenTelemetryConnection{
    private final MetricCollector metricCollector;
    private final TraceCollector traceCollector;
    private final  OpenTelemetry openTelemetry;

    private final ContextPropagator contextPropagator;

    public OpenTelemetryConnection(OpenTelemetry openTelemetry, MetricCollector metricCollector, TraceCollector traceCollector, ContextPropagator contextPropagator) {
        this.metricCollector = metricCollector;
        this.traceCollector = traceCollector;
        this.openTelemetry = openTelemetry;
        this.contextPropagator = contextPropagator;
    }

    public void start() {
        MemoryPools.registerObservers(openTelemetry);
        BufferPools.registerObservers(openTelemetry);
        Classes.registerObservers(openTelemetry);
        Cpu.registerObservers(openTelemetry);
        Threads.registerObservers(openTelemetry);
        GarbageCollector.registerObservers(openTelemetry);
    }

    public MetricCollector getMetricCollector() {
        return metricCollector;
    }

    public TraceCollector getTraceCollector() {
        return traceCollector;
    }

    public ContextPropagator getContextPropagator() {
        return contextPropagator;
    }
}
