package org.mule.extension.opentelemetry.internal.exporter.metric;

import java.util.concurrent.TimeUnit;

import org.mule.runtime.extension.api.annotation.dsl.xml.TypeDsl;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;

import io.opentelemetry.sdk.metrics.SdkMeterProvider;
import io.opentelemetry.sdk.metrics.SdkMeterProviderBuilder;
import io.opentelemetry.sdk.metrics.export.PeriodicMetricReader;

@TypeDsl()
public class LoggingMetricExporter implements MetricExporter {
	 @Parameter
	 @Optional(defaultValue ="10")
    private int interval;
   
	@Override
	public SdkMeterProviderBuilder createMeterProviderBuilder() {
		io.opentelemetry.exporter.logging.LoggingMetricExporter create = io.opentelemetry.exporter.logging.LoggingMetricExporter.create();
		return SdkMeterProvider.builder()
				.registerMetricReader(PeriodicMetricReader.builder(create).setInterval(interval, TimeUnit.SECONDS).build());
	}

	@Override
	public String toString() {
		return "LoggingMetricExporter [interval=" + interval + "]";
	}

}
