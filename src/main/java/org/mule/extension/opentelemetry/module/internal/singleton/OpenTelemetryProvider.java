package org.mule.extension.opentelemetry.module.internal.singleton;

import org.mule.extension.opentelemetry.module.internal.OpenTelemetryConfiguration;
import org.mule.extension.opentelemetry.module.internal.OplInitialisable;
import org.mule.extension.opentelemetry.module.internal.TraceContextPropagator;
import org.mule.extension.opentelemetry.module.utils.OplUtils;
import org.mule.extension.opentelemetry.module.internal.provider.MetricExporter;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.baggage.propagation.W3CBaggagePropagator;
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

public class OpenTelemetryProvider implements OplInitialisable {

    private SdkMeterProvider meterProvider;
    private SdkLoggerProvider loggerProvider;
    private SdkTracerProvider tracerProvider;
    private ContextPropagators contextPropagators;


    public void initialise(OpenTelemetryConfiguration configuration) {
        final Resource resource = createResource(configuration);

        meterProvider = createMeterProvider(resource, configuration);

        loggerProvider = createLoggerProvider(resource);

        tracerProvider = createTracerProvider(resource);

        contextPropagators = createContextPropagators(configuration);

        OpenTelemetry openTelemetry = createOpenTelemetry();
        this.observeJvm(openTelemetry);
    }

    private ContextPropagators createContextPropagators(OpenTelemetryConfiguration configuration) {
        TraceContextPropagator contextPropagator = configuration.getTracingConfig().getContextPropagator();
        TextMapPropagator textPropagator = TextMapPropagator.composite(contextPropagator.getTxtMapPropagator(), W3CBaggagePropagator.getInstance());
        return ContextPropagators.create(textPropagator);
    }

    private Resource createResource(OpenTelemetryConfiguration configuration) {
        return Resource.getDefault().toBuilder()
                .put(ResourceAttributes.SERVICE_NAME, configuration.getServiceName())
                .put(ResourceAttributes.SERVICE_VERSION, configuration.getServiceVersion()).build();
    }

    private SdkMeterProvider createMeterProvider(Resource resource, OpenTelemetryConfiguration configuration) {
        String histogramName = OplUtils.createHistogramName(configuration.getServiceName());
        InstrumentSelector selector = InstrumentSelector.builder().setType(InstrumentType.HISTOGRAM).setName(histogramName).build();
        View view = View.builder().setAggregation(Aggregation.base2ExponentialBucketHistogram()).build();
        MetricExporter metricExporter = configuration.getMetricConfig().getMetricExporter();
        return metricExporter.createMeterProviderBuilder().registerView(selector, view).setResource(resource).build();
    }

    private SdkTracerProvider createTracerProvider(Resource resource) {
        return SdkTracerProvider.builder()
                .addSpanProcessor(SimpleSpanProcessor.create(LoggingSpanExporter.create()))
                .setResource(resource)
                .build();
    }

    private SdkLoggerProvider createLoggerProvider(Resource resource) {
        return SdkLoggerProvider.builder()
                .addLogRecordProcessor(BatchLogRecordProcessor.builder(SystemOutLogRecordExporter.create()).build())
                .setResource(resource).build();
    }

    private OpenTelemetrySdk createOpenTelemetry() {
        return OpenTelemetrySdk.builder()
                .setTracerProvider(tracerProvider)
                .setMeterProvider(meterProvider)
                .setLoggerProvider(loggerProvider)
                .setPropagators(contextPropagators)
                .buildAndRegisterGlobal();
    }

    public ContextPropagators getContextPropagators() {
        return contextPropagators;
    }

    private void observeJvm(OpenTelemetry openTelemetry) {
        MemoryPools.registerObservers(openTelemetry);
        BufferPools.registerObservers(openTelemetry);
        Classes.registerObservers(openTelemetry);
        Cpu.registerObservers(openTelemetry);
        Threads.registerObservers(openTelemetry);
        GarbageCollector.registerObservers(openTelemetry);
    }

    public SdkMeterProvider getMeterProvider() {
        return meterProvider;
    }

    public SdkLoggerProvider getLoggerProvider() {
        return loggerProvider;
    }

    public SdkTracerProvider getTracerProvider() {
        return tracerProvider;
    }
}
