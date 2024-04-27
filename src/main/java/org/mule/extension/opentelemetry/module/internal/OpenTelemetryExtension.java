package org.mule.extension.opentelemetry.module.internal;

import org.mule.runtime.extension.api.annotation.Extension;
import org.mule.runtime.extension.api.annotation.SubTypeMapping;
import org.mule.extension.opentelemetry.module.internal.provider.LoggingMetricExporter;
import org.mule.extension.opentelemetry.module.internal.provider.MetricExporter;
import org.mule.extension.opentelemetry.module.internal.provider.OtlpGrpcMetricExporter;
import org.mule.extension.opentelemetry.module.internal.provider.PrometheusExporter;
import org.mule.runtime.extension.api.annotation.Configurations;
import org.mule.runtime.extension.api.annotation.dsl.xml.Xml;


/**
 * This is the main class of an extension, is the entry point from which configurations, connection providers, operations
 * and sources are going to be declared.
 */
@Xml(prefix = "openTelemetry")
@Extension(name = "Opentelemetry")
@Configurations(OpenTelemetryConfiguration.class)
@SubTypeMapping(baseType = MetricExporter.class,
subTypes = {LoggingMetricExporter.class, OtlpGrpcMetricExporter.class, PrometheusExporter.class })
public class OpenTelemetryExtension {

}
