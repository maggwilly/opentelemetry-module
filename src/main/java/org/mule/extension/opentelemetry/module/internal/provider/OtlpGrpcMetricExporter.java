package org.mule.extension.opentelemetry.module.internal.provider;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.mule.runtime.extension.api.annotation.dsl.xml.TypeDsl;
import org.mule.runtime.extension.api.annotation.param.Content;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;

import io.opentelemetry.sdk.metrics.SdkMeterProvider;
import io.opentelemetry.sdk.metrics.SdkMeterProviderBuilder;
import io.opentelemetry.sdk.metrics.export.PeriodicMetricReader;
@TypeDsl(allowTopLevelDefinition = false, allowInlineDefinition = true)
public class OtlpGrpcMetricExporter implements MetricExporter {
	 @Parameter
     private String endPoint;
    
     @Parameter
     @Content
     private Map<String,String> headers;
     
	 @Parameter
	 @Optional(defaultValue ="5")
     private int interval;
    
	@Override
	public SdkMeterProviderBuilder createMeterProviderBuilder() {
		io.opentelemetry.exporter.otlp.metrics.OtlpGrpcMetricExporter build = io.opentelemetry.exporter.otlp.metrics.OtlpGrpcMetricExporter.builder().setEndpoint(endPoint).setHeaders(() -> headers).build();
		return SdkMeterProvider.builder()
	            .registerMetricReader(PeriodicMetricReader.builder(build).setInterval(interval, TimeUnit.SECONDS).build());
	}

	@Override
	public String toString() {
		return "OtlpGrpcMetricExporter [endPoint=" + endPoint + ", headers=" + headers + ", interval=" + interval + "]";
	}

}
