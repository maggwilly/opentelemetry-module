package org.mule.extension.opentelemetry.internal;

import org.mule.extension.opentelemetry.api.ObjectStoreContextHolder;
import org.mule.extension.opentelemetry.api.SpanContextHolder;
import org.mule.extension.opentelemetry.api.TextMapContextHolder;
import org.mule.extension.opentelemetry.internal.exporter.metric.LoggingMetricExporter;
import org.mule.extension.opentelemetry.internal.exporter.metric.MetricExporter;
import org.mule.extension.opentelemetry.internal.exporter.metric.OtlpGrpcMetricExporter;
import org.mule.extension.opentelemetry.internal.exporter.metric.PrometheusMetricExporter;
import org.mule.extension.opentelemetry.internal.exporter.span.LoggingTraceExporter;
import org.mule.extension.opentelemetry.internal.exporter.span.OtlpGrpcTraceExporter;
import org.mule.extension.opentelemetry.internal.exporter.span.TraceExporter;
import org.mule.runtime.api.store.ObjectStore;
import org.mule.runtime.extension.api.annotation.Configurations;
import org.mule.runtime.extension.api.annotation.Extension;
import org.mule.runtime.extension.api.annotation.Import;
import org.mule.runtime.extension.api.annotation.SubTypeMapping;
import org.mule.runtime.extension.api.annotation.dsl.xml.Xml;


/**
 * This is the main class of an extension, is the entry point from which configurations, connection providers, operations
 * and sources are going to be declared.
 */
@Xml(prefix = "opl")
@Extension(name = "Opentelemetry")
@Import(type = ObjectStore.class)
@Configurations(OpenTelemetryConfiguration.class)
@SubTypeMapping(baseType = MetricExporter.class,
subTypes = {LoggingMetricExporter.class, OtlpGrpcMetricExporter.class, PrometheusMetricExporter.class })
@SubTypeMapping(baseType = TraceExporter.class,
        subTypes = {OtlpGrpcTraceExporter.class, LoggingTraceExporter.class })
@SubTypeMapping(baseType = SpanContextHolder.class, subTypes = {TextMapContextHolder.class, ObjectStoreContextHolder.class})
public class OpenTelemetryExtension {

}
