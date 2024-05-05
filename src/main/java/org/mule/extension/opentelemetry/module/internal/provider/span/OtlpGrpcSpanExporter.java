package org.mule.extension.opentelemetry.module.internal.provider.span;

import io.opentelemetry.exporter.otlp.trace.OtlpGrpcSpanExporterBuilder;
import io.opentelemetry.sdk.metrics.SdkMeterProvider;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.SdkTracerProviderBuilder;
import io.opentelemetry.sdk.trace.export.SimpleSpanProcessor;
import org.mule.extension.opentelemetry.module.internal.ExporterInitialisationException;
import org.mule.runtime.extension.api.annotation.param.Content;
import org.mule.runtime.extension.api.annotation.param.Parameter;

import java.util.Map;

public class OtlpGrpcSpanExporter implements SpanExporter{
    @Parameter
    private String endPoint;

    @Parameter
    @Content
    private Map<String,String> headers;

    @Override
    public SdkTracerProviderBuilder createSdkTracerProviderBuilder(SdkMeterProvider meterProvider) throws ExporterInitialisationException {
        OtlpGrpcSpanExporterBuilder otlpGrpcSpanExporterBuilder = io.opentelemetry.exporter.otlp.trace.OtlpGrpcSpanExporter.builder()
                .setMeterProvider(meterProvider)
                .setEndpoint(endPoint).setHeaders(() -> headers);
        return SdkTracerProvider.builder()
                .addSpanProcessor(SimpleSpanProcessor.create(otlpGrpcSpanExporterBuilder.build()));
    }
}
