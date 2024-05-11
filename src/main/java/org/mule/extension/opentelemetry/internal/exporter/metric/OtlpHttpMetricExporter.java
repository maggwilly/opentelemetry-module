package org.mule.extension.opentelemetry.internal.exporter.metric;

import io.opentelemetry.sdk.metrics.SdkMeterProvider;
import io.opentelemetry.sdk.metrics.SdkMeterProviderBuilder;
import io.opentelemetry.sdk.metrics.export.PeriodicMetricReader;
import org.mule.runtime.api.meta.ExpressionSupport;
import org.mule.runtime.api.tls.TlsContextFactory;
import org.mule.runtime.api.util.MultiMap;
import org.mule.runtime.extension.api.annotation.Expression;
import org.mule.runtime.extension.api.annotation.param.Content;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;

import java.util.concurrent.TimeUnit;

public class OtlpHttpMetricExporter implements MetricExporter {
    @Parameter
    private String endPoint;

    @Parameter
    @Content
    private MultiMap<String,String> headers= MultiMap.emptyMultiMap();
    @Parameter
    @Optional
    @Expression(ExpressionSupport.NOT_SUPPORTED)
    @DisplayName("TLS Configuration")
    private TlsContextFactory tlsContext;
    @Parameter
    @Optional(defaultValue ="5")
    private int interval;

    @Override
    public SdkMeterProviderBuilder createMeterProviderBuilder() {
        io.opentelemetry.exporter.otlp.http.metrics.OtlpHttpMetricExporter exporter = io.opentelemetry.exporter.otlp.http.metrics.OtlpHttpMetricExporter.builder()
                .setEndpoint(endPoint).setHeaders(() -> headers).build();
        return SdkMeterProvider.builder().registerMetricReader(PeriodicMetricReader.builder(exporter)
                .setInterval(interval, TimeUnit.SECONDS).build());
    }

    public String getEndPoint() {
        return endPoint;
    }

    public OtlpHttpMetricExporter setEndPoint(String endPoint) {
        this.endPoint = endPoint;
        return this;
    }

    public MultiMap<String, String> getHeaders() {
        return headers;
    }

    public OtlpHttpMetricExporter setHeaders(MultiMap<String, String> headers) {
        this.headers = headers;
        return this;
    }

    public int getInterval() {
        return interval;
    }

    public OtlpHttpMetricExporter setInterval(int interval) {
        this.interval = interval;
        return this;
    }
}

