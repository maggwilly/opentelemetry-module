package org.mule.extension.opentelemetry.module.internal.provider.span;

import io.opentelemetry.sdk.metrics.SdkMeterProvider;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.SdkTracerProviderBuilder;
import io.opentelemetry.sdk.trace.export.SimpleSpanProcessor;
import org.mule.extension.opentelemetry.module.internal.ExporterInitialisationException;
import org.mule.runtime.extension.api.annotation.dsl.xml.TypeDsl;

@TypeDsl(allowTopLevelDefinition = false, allowInlineDefinition = true)
public class LoggingTraceExporter implements TraceExporter {
    @Override
    public SdkTracerProviderBuilder createSdkTracerProviderBuilder(SdkMeterProvider meterProvider) throws ExporterInitialisationException {
        return SdkTracerProvider.builder()
                .addSpanProcessor(SimpleSpanProcessor.create(io.opentelemetry.exporter.logging.LoggingSpanExporter.create()));
    }
}
