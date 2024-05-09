package org.mule.extension.opentelemetry.internal.config;

import org.mule.extension.opentelemetry.internal.exporter.span.TraceExporter;
import org.mule.runtime.api.meta.ExpressionSupport;
import org.mule.runtime.extension.api.annotation.Expression;
import org.mule.runtime.extension.api.annotation.param.Parameter;

public class TracingConfiguration {
    @Parameter
    @Expression(ExpressionSupport.NOT_SUPPORTED)
    private TraceExporter traceExporter;

    public TraceExporter getTraceExporter() {
        return traceExporter;
    }

    public TracingConfiguration setTraceExporter(TraceExporter traceExporter) {
        this.traceExporter = traceExporter;
        return this;
    }
}
