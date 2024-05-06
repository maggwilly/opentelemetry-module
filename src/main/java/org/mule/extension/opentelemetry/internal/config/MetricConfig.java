package org.mule.extension.opentelemetry.internal.config;

import org.mule.extension.opentelemetry.internal.provider.metric.MetricExporter;
import org.mule.runtime.api.meta.ExpressionSupport;
import org.mule.runtime.extension.api.annotation.Expression;
import org.mule.runtime.extension.api.annotation.param.Parameter;

public class MetricConfig {
    @Parameter
    @Expression(ExpressionSupport.NOT_SUPPORTED)
    private MetricExporter metricExporter;

    public MetricExporter getMetricExporter() {
        return metricExporter;
    }


    public void setMetricExporter(MetricExporter metricExporter) {
        this.metricExporter = metricExporter;
    }
}
