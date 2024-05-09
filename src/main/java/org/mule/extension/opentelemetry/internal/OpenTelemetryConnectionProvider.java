package org.mule.extension.opentelemetry.internal;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.baggage.propagation.W3CBaggagePropagator;
import io.opentelemetry.api.trace.propagation.W3CTraceContextPropagator;
import io.opentelemetry.context.propagation.ContextPropagators;
import io.opentelemetry.context.propagation.TextMapPropagator;
import io.opentelemetry.exporter.logging.SystemOutLogRecordExporter;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.logs.SdkLoggerProvider;
import io.opentelemetry.sdk.logs.export.BatchLogRecordProcessor;
import io.opentelemetry.sdk.metrics.*;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.semconv.ResourceAttributes;
import org.mule.extension.opentelemetry.internal.config.MetricConfiguration;
import org.mule.extension.opentelemetry.internal.config.TracingConfiguration;
import org.mule.extension.opentelemetry.internal.exporter.metric.MetricExporter;
import org.mule.extension.opentelemetry.internal.exporter.span.TraceExporter;
import org.mule.extension.opentelemetry.internal.service.*;
import org.mule.runtime.api.connection.CachedConnectionProvider;
import org.mule.runtime.api.connection.ConnectionValidationResult;
import org.mule.runtime.api.meta.ExpressionSupport;
import org.mule.runtime.api.store.ObjectStoreManager;
import org.mule.runtime.extension.api.annotation.Expression;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.RefName;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;
import org.mule.runtime.extension.api.annotation.param.display.Placement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

public class OpenTelemetryConnectionProvider implements CachedConnectionProvider<OpenTelemetryConnection> {
    private final Logger LOGGER = LoggerFactory.getLogger(OpenTelemetryConnectionProvider.class);
    @RefName
    private String configName;

    @Inject
    private ObjectStoreManager objectStoreManager;

    @Inject
    private OpenTelemetryConnectionHolder connectionHolder;
    @Parameter
    @Placement(tab = "Metric", order = 1)
    @Expression(ExpressionSupport.NOT_SUPPORTED)
    private MetricConfiguration metricConfiguration;

    @Parameter
    @Placement(tab = "Tracing", order = 2)
    @Expression(ExpressionSupport.NOT_SUPPORTED)
    private TracingConfiguration tracingConfiguration;

    @DisplayName("Service Name")
    @Parameter
    private String serviceName;

    @DisplayName("Service Version")
    @Parameter
    private String serviceVersion;

    @Override
    public OpenTelemetryConnection connect() {
        LOGGER.info("Creating Resources for {} -{}", serviceName, configName);
        Resource resource = createResource();
        SdkMeterProvider meterProvider = createMeterProvider(resource);
        ContextPropagators contextPropagators = createContextPropagators();
        SdkTracerProvider tracerProvider = createTracerProvider(resource, meterProvider);
        SdkLoggerProvider loggerProvider = createLoggerProvider(resource);
        ContextPropagator contextPropagator = new DefaultContextPropagator(objectStoreManager, contextPropagators);
        OpenTelemetry openTelemetry = createOpenTelemetry(meterProvider, tracerProvider, loggerProvider, contextPropagators);
        MetricCollector metricCollector = new DefaultMetricCollector(configName, meterProvider);
        TraceCollector traceCollector = new DefaultTraceCollector(configName, tracerProvider, contextPropagator);
        return connectionHolder.init(new OpenTelemetryConnection(openTelemetry, metricCollector, traceCollector, contextPropagator));
    }

    @Override
    public void disconnect(OpenTelemetryConnection connection) {

    }

    private Resource createResource() {
        return Resource.getDefault().toBuilder().put(ResourceAttributes.SERVICE_NAME, serviceName).put(ResourceAttributes.SERVICE_VERSION, serviceVersion).build();
    }

    private ContextPropagators createContextPropagators() {
        TextMapPropagator textPropagator = TextMapPropagator.composite(W3CTraceContextPropagator.getInstance(), W3CBaggagePropagator.getInstance());
        return ContextPropagators.create(textPropagator);
    }

    private OpenTelemetrySdk createOpenTelemetry(SdkMeterProvider meterProvider, SdkTracerProvider tracerProvider, SdkLoggerProvider loggerProvider, ContextPropagators propagators) {
        return OpenTelemetrySdk.builder().setTracerProvider(tracerProvider).setMeterProvider(meterProvider).setLoggerProvider(loggerProvider).setPropagators(propagators).buildAndRegisterGlobal();
    }

    private SdkMeterProvider createMeterProvider(Resource resource) {
        InstrumentSelector selector = InstrumentSelector.builder().setType(InstrumentType.HISTOGRAM).setName(configName).build();
        View view = View.builder().setAggregation(Aggregation.base2ExponentialBucketHistogram()).build();
        MetricExporter metricExporter = metricConfiguration.getMetricExporter();
        return metricExporter.createMeterProviderBuilder().registerView(selector, view).setResource(resource).build();
    }

    private SdkTracerProvider createTracerProvider(Resource resource, SdkMeterProvider meterProvider) {
        TraceExporter exporter = tracingConfiguration.getTraceExporter();
        return exporter.createSdkTracerProviderBuilder(meterProvider).setResource(resource).build();
    }

    private SdkLoggerProvider createLoggerProvider(Resource resource) {
        return SdkLoggerProvider.builder()
                .addLogRecordProcessor(BatchLogRecordProcessor.builder(SystemOutLogRecordExporter.create()).build())
                .setResource(resource).build();
    }

    @Override
    public ConnectionValidationResult validate(OpenTelemetryConnection connection) {
        return ConnectionValidationResult.success();
    }
}
