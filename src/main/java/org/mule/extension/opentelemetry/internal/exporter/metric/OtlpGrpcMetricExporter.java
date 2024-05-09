package org.mule.extension.opentelemetry.internal.exporter.metric;

import io.opentelemetry.sdk.metrics.SdkMeterProvider;
import io.opentelemetry.sdk.metrics.SdkMeterProviderBuilder;
import io.opentelemetry.sdk.metrics.export.PeriodicMetricReader;
import org.mule.runtime.api.meta.ExpressionSupport;
import org.mule.runtime.api.tls.TlsContextFactory;
import org.mule.runtime.api.util.MultiMap;
import org.mule.runtime.extension.api.annotation.Expression;
import org.mule.runtime.extension.api.annotation.dsl.xml.TypeDsl;
import org.mule.runtime.extension.api.annotation.param.Content;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;

import java.util.concurrent.TimeUnit;
@TypeDsl()
public class OtlpGrpcMetricExporter implements MetricExporter {
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
		io.opentelemetry.exporter.otlp.metrics.OtlpGrpcMetricExporter otlpGrpcMetricExporter = io.opentelemetry.exporter.otlp.metrics.OtlpGrpcMetricExporter.builder()
				.setEndpoint(endPoint).setHeaders(() -> headers).build();
		return SdkMeterProvider.builder().registerMetricReader(PeriodicMetricReader.builder(otlpGrpcMetricExporter)
						.setInterval(interval, TimeUnit.SECONDS).build());
	}

	@Override
	public String toString() {
		return "OtlpGrpcMetricExporter [endPoint=" + endPoint + ", headers=" + headers + ", interval=" + interval + "]";
	}

	public String getEndPoint() {
		return endPoint;
	}

	public OtlpGrpcMetricExporter setEndPoint(String endPoint) {
		this.endPoint = endPoint;
		return this;
	}

	public MultiMap<String, String> getHeaders() {
		return headers;
	}

	public OtlpGrpcMetricExporter setHeaders(MultiMap<String, String> headers) {
		this.headers = headers;
		return this;
	}

	public TlsContextFactory getTlsContext() {
		return tlsContext;
	}

	public OtlpGrpcMetricExporter setTlsContext(TlsContextFactory tlsContext) {
		this.tlsContext = tlsContext;
		return this;
	}

	public int getInterval() {
		return interval;
	}

	public OtlpGrpcMetricExporter setInterval(int interval) {
		this.interval = interval;
		return this;
	}
}
