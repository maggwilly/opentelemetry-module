package org.mule.extension.opentelemetry.internal;

import org.mule.extension.opentelemetry.internal.exporter.metric.*;
import org.mule.extension.opentelemetry.internal.exporter.trace.LoggingTraceExporter;
import org.mule.extension.opentelemetry.internal.exporter.trace.OtlpGrpcTraceExporter;
import org.mule.extension.opentelemetry.internal.exporter.trace.OtlpHttpTraceExporter;
import org.mule.extension.opentelemetry.internal.exporter.trace.TraceExporter;
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
subTypes = {LoggingMetricExporter.class, OtlpHttpMetricExporter.class, OtlpGrpcMetricExporter.class, PrometheusMetricExporter.class })
@SubTypeMapping(baseType = TraceExporter.class, subTypes = {LoggingTraceExporter.class, OtlpHttpTraceExporter.class ,OtlpGrpcTraceExporter.class})
public class OpenTelemetryExtension implements Startable {
    private final Logger LOGGER = LoggerFactory.getLogger(OpenTelemetryExtension.class);

    @Config
    private OpenTelemetryConfiguration configuration;


    @Override
    public void start() throws MuleException {
        LOGGER.info("Configuration- {}",configuration);
    }
}
