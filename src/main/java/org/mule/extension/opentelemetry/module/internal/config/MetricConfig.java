package org.mule.extension.opentelemetry.module.internal.config;

import org.mule.extension.opentelemetry.module.internal.provider.metric.MetricExporter;
import org.mule.runtime.api.meta.ExpressionSupport;
import org.mule.runtime.extension.api.annotation.Expression;
import org.mule.runtime.extension.api.annotation.param.Parameter;

public class MetricConfig {
    @Parameter
    @Expression(ExpressionSupport.NOT_SUPPORTED)
    private MetricExporter exporter;

    public MetricExporter getExporter() {
        return exporter;
    }


    public void setExporter(MetricExporter exporter) {
        this.exporter = exporter;
    }
}
