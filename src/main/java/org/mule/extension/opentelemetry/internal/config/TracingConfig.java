package org.mule.extension.opentelemetry.internal.config;

import org.mule.extension.opentelemetry.internal.exporter.trace.TraceExporter;
import org.mule.runtime.api.meta.ExpressionSupport;
import org.mule.runtime.api.store.ObjectStore;
import org.mule.runtime.extension.api.annotation.Expression;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.reference.ObjectStoreReference;

public class TracingConfig {
    @Parameter
    @Expression(ExpressionSupport.NOT_SUPPORTED)
    private TraceExporter traceExporter;
    @Optional
    @Parameter
    @ObjectStoreReference
    @Expression(ExpressionSupport.NOT_SUPPORTED)
    private ObjectStore contextPropagator;

    public TraceExporter getTraceExporter() {
        return traceExporter;
    }

    public TracingConfig setTraceExporter(TraceExporter traceExporter) {
        this.traceExporter = traceExporter;
        return this;
    }

    public ObjectStore getContextPropagator() {
        return contextPropagator;
    }

    public TracingConfig setContextPropagator(ObjectStore contextPropagator) {
        this.contextPropagator = contextPropagator;
        return this;
    }
}
