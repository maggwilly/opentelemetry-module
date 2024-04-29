package org.mule.extension.opentelemetry.module.internal.config;

import org.mule.extension.opentelemetry.module.internal.provider.MetricExporter;
import org.mule.runtime.extension.api.annotation.param.Parameter;

public class MetricConfig {
    @Parameter
    private MetricExporter metricExporter;

    public MetricExporter getMetricExporter() {
        return metricExporter;
    }


    public void setMetricExporter(MetricExporter metricExporter) {
        this.metricExporter = metricExporter;
    }
}
