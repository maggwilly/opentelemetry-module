package org.mule.extension.opentelemetry.internal.exporter.trace;

import io.opentelemetry.exporter.otlp.http.trace.OtlpHttpSpanExporter;
import io.opentelemetry.exporter.otlp.http.trace.OtlpHttpSpanExporterBuilder;
import io.opentelemetry.exporter.otlp.trace.OtlpGrpcSpanExporterBuilder;
import io.opentelemetry.sdk.metrics.SdkMeterProvider;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.SdkTracerProviderBuilder;
import io.opentelemetry.sdk.trace.export.SimpleSpanProcessor;
import org.mule.extension.opentelemetry.internal.ExporterInitialisationException;
import org.mule.runtime.api.lifecycle.Initialisable;
import org.mule.runtime.api.lifecycle.InitialisationException;
import org.mule.runtime.api.meta.ExpressionSupport;
import org.mule.runtime.api.tls.TlsContextFactory;
import org.mule.runtime.api.util.MultiMap;
import org.mule.runtime.extension.api.annotation.Expression;
import org.mule.runtime.extension.api.annotation.param.Content;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;

import static org.mule.runtime.core.api.lifecycle.LifecycleUtils.initialiseIfNeeded;

public class OtlpHttpTraceExporter implements TraceExporter, Initialisable {
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
    @Override
    public SdkTracerProviderBuilder createSdkTracerProviderBuilder(SdkMeterProvider meterProvider) throws ExporterInitialisationException {
        OtlpHttpSpanExporterBuilder httpSpanExporterBuilder = OtlpHttpSpanExporter.builder()
                .setMeterProvider(meterProvider)
                .setEndpoint(endPoint).setHeaders(() -> headers);
        return SdkTracerProvider.builder().addSpanProcessor(SimpleSpanProcessor.create(httpSpanExporterBuilder.build()));
    }


    public String getEndPoint() {
        return endPoint;
    }

    public OtlpHttpTraceExporter setEndPoint(String endPoint) {
        this.endPoint = endPoint;
        return this;
    }

    public MultiMap<String, String> getHeaders() {
        return headers;
    }

    public OtlpHttpTraceExporter setHeaders(MultiMap<String, String> headers) {
        this.headers = headers;
        return this;
    }

    public TlsContextFactory getTlsContext() {
        return tlsContext;
    }

    public OtlpHttpTraceExporter setTlsContext(TlsContextFactory tlsContext) {
        this.tlsContext = tlsContext;
        return this;
    }

    @Override
    public void initialise() throws InitialisationException {
        if (tlsContext != null) {
            initialiseIfNeeded(tlsContext);
        }
    }

    @Override
    public String toString() {
        return "OtlpGrpcTraceExporter{" +
                "endPoint='" + endPoint + '\'' +
                ", headers=" + headers +
                '}';
    }
}
