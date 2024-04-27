package org.mule.extension.opentelemetry.module.internal.singleton;

import org.mule.extension.opentelemetry.module.internal.OpenTelemetryConfiguration;
import org.mule.extension.opentelemetry.module.internal.OplInitialisable;
import org.mule.extension.opentelemetry.module.internal.OplUtils;
import org.mule.extension.opentelemetry.module.internal.provider.MetricExporter;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.baggage.propagation.W3CBaggagePropagator;
import io.opentelemetry.api.trace.propagation.W3CTraceContextPropagator;
import io.opentelemetry.context.propagation.ContextPropagators;
import io.opentelemetry.context.propagation.TextMapPropagator;
import io.opentelemetry.exporter.logging.LoggingSpanExporter;
import io.opentelemetry.exporter.logging.SystemOutLogRecordExporter;
import io.opentelemetry.instrumentation.runtimemetrics.java8.BufferPools;
import io.opentelemetry.instrumentation.runtimemetrics.java8.Classes;
import io.opentelemetry.instrumentation.runtimemetrics.java8.Cpu;
import io.opentelemetry.instrumentation.runtimemetrics.java8.GarbageCollector;
import io.opentelemetry.instrumentation.runtimemetrics.java8.MemoryPools;
import io.opentelemetry.instrumentation.runtimemetrics.java8.Threads;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.logs.SdkLoggerProvider;
import io.opentelemetry.sdk.logs.export.BatchLogRecordProcessor;
import io.opentelemetry.sdk.metrics.Aggregation;
import io.opentelemetry.sdk.metrics.InstrumentSelector;
import io.opentelemetry.sdk.metrics.InstrumentType;
import io.opentelemetry.sdk.metrics.SdkMeterProvider;
import io.opentelemetry.sdk.metrics.View;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.export.SimpleSpanProcessor;
import io.opentelemetry.semconv.ResourceAttributes;

public class OpenTelemetryProvider implements OplInitialisable{
	private OpenTelemetry openTelemetry;

	public OpenTelemetry getOpenTelemetry() {
		return openTelemetry;
	}

	public void initialise(OpenTelemetryConfiguration configuration) {
		
		Resource resource = Resource.getDefault().toBuilder()
				.put(ResourceAttributes.SERVICE_NAME, configuration.getServiceName())
				.put(ResourceAttributes.SERVICE_VERSION, configuration.getServiceVersion()).build();


		String histogramName = OplUtils.createHistogramName(configuration.getServiceName());
		InstrumentSelector selector = InstrumentSelector.builder().setType(InstrumentType.HISTOGRAM).setName(histogramName).build();
		View view = View.builder().setAggregation(Aggregation.base2ExponentialBucketHistogram()).build();
		

		MetricExporter metricExporter = configuration.getMetricExporter();
	
		SdkMeterProvider meterProvider = metricExporter.createMeterProviderBuilder().registerView(selector, view).setResource(resource).build();
		
		SdkLoggerProvider sdkLoggerProvider = SdkLoggerProvider.builder()
				.addLogRecordProcessor(BatchLogRecordProcessor.builder(SystemOutLogRecordExporter.create()).build())
				.setResource(resource).build();
	
		SdkTracerProvider sdkTracerProvider = SdkTracerProvider.builder()
				.addSpanProcessor(SimpleSpanProcessor.create(LoggingSpanExporter.create()))
				.setResource(resource)
				.build();

		this.openTelemetry = OpenTelemetrySdk.builder()
				.setTracerProvider(sdkTracerProvider)
				.setMeterProvider(meterProvider)
				.setLoggerProvider(sdkLoggerProvider)
				.setPropagators(ContextPropagators.create(TextMapPropagator
						.composite(W3CTraceContextPropagator.getInstance(), W3CBaggagePropagator.getInstance())))
				.buildAndRegisterGlobal();
		this.observeJvm();
	}


	private void observeJvm() {
		MemoryPools.registerObservers(openTelemetry);
		BufferPools.registerObservers(openTelemetry);
		Classes.registerObservers(openTelemetry);
		Cpu.registerObservers(openTelemetry);
		Threads.registerObservers(openTelemetry);
		GarbageCollector.registerObservers(openTelemetry);
	}

	
}
