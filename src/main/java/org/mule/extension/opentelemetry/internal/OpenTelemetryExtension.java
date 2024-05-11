package org.mule.extension.opentelemetry.internal;

import org.mule.extension.opentelemetry.internal.exporter.metric.LoggingMetricExporter;
import org.mule.extension.opentelemetry.internal.exporter.metric.MetricExporter;
import org.mule.extension.opentelemetry.internal.exporter.metric.OtlpGrpcMetricExporter;
import org.mule.extension.opentelemetry.internal.exporter.metric.PrometheusMetricExporter;
import org.mule.extension.opentelemetry.internal.exporter.span.LoggingTraceExporter;
import org.mule.extension.opentelemetry.internal.exporter.span.OtlpGrpcTraceExporter;
import org.mule.extension.opentelemetry.internal.exporter.span.TraceExporter;
import org.mule.runtime.api.exception.MuleException;
import org.mule.runtime.api.lifecycle.Startable;
import org.mule.runtime.api.store.ObjectStore;
import org.mule.runtime.extension.api.annotation.Configurations;
import org.mule.runtime.extension.api.annotation.Extension;
import org.mule.runtime.extension.api.annotation.Import;
import org.mule.runtime.extension.api.annotation.SubTypeMapping;
import org.mule.runtime.extension.api.annotation.dsl.xml.Xml;
import org.mule.runtime.extension.api.annotation.param.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Xml(prefix = "opl")
@Extension(name = "Opentelemetry")
@Import(type = ObjectStore.class)
@Configurations(OpenTelemetryConfiguration.class)
@SubTypeMapping(baseType = MetricExporter.class,
subTypes = {LoggingMetricExporter.class, OtlpGrpcMetricExporter.class, PrometheusMetricExporter.class })
@SubTypeMapping(baseType = TraceExporter.class,
        subTypes = {OtlpGrpcTraceExporter.class, LoggingTraceExporter.class })
public class OpenTelemetryExtension implements Startable {
    private final Logger LOGGER = LoggerFactory.getLogger(OpenTelemetryExtension.class);

    @Config
    private OpenTelemetryConfiguration configuration;


    @Override
    public void start() throws MuleException {
        LOGGER.info("Configuration- {}",configuration);
    }
}
