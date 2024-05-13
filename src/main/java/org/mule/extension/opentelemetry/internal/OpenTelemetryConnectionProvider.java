package org.mule.extension.opentelemetry.internal;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.context.propagation.ContextPropagators;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.metrics.InstrumentSelector;
import io.opentelemetry.sdk.metrics.InstrumentType;
import io.opentelemetry.sdk.metrics.SdkMeterProvider;
import io.opentelemetry.sdk.metrics.View;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.semconv.ResourceAttributes;
import org.mule.extension.opentelemetry.internal.config.MetricConfig;
import org.mule.extension.opentelemetry.internal.config.TracingConfig;
import org.mule.extension.opentelemetry.internal.context.ContextManager;
import org.mule.extension.opentelemetry.internal.exporter.metric.MetricExporter;
import org.mule.extension.opentelemetry.internal.exporter.trace.TraceExporter;
import org.mule.extension.opentelemetry.internal.resource.MuleResource;
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
    private ContextManager contextManager;
    @Inject
    private  ContextPropagators contextPropagators;
    @Inject
    private OpenTelemetryConnectionHolder connectionHolder;
    @Parameter
    @Placement(tab = "Metric", order = 1)
    @Expression(ExpressionSupport.NOT_SUPPORTED)
    private MetricConfig metricConfig;

    @Parameter
    @Placement(tab = "Tracing", order = 2)
    @Expression(ExpressionSupport.NOT_SUPPORTED)
    private TracingConfig tracingConfig;

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
        SdkTracerProvider tracerProvider = createTracerProvider(resource, meterProvider);
        OpenTelemetry openTelemetry = createOpenTelemetry(meterProvider, tracerProvider, contextPropagators);

        MetricCollector metricCollector = new DefaultMetricCollector(configName, openTelemetry);
        TraceCollector traceCollector = new DefaultTraceCollector(configName, openTelemetry, contextManager);
        return connectionHolder.init(new OpenTelemetryConnection(openTelemetry, metricCollector, traceCollector, metricConfig, tracingConfig));
    }

    @Override
    public void disconnect(OpenTelemetryConnection connection) {
        connectionHolder.stop(connection);
    }

    private Resource createResource() {
        return Resource.getDefault().merge(MuleResource.buildResource()).toBuilder()
                .put(ResourceAttributes.SERVICE_NAME, serviceName)
                .put(ResourceAttributes.SERVICE_VERSION, serviceVersion)
                .build();
    }

    private OpenTelemetrySdk createOpenTelemetry(SdkMeterProvider meterProvider, SdkTracerProvider tracerProvider,  ContextPropagators propagators) {
        return OpenTelemetrySdk.builder().setTracerProvider(tracerProvider).setMeterProvider(meterProvider).setPropagators(propagators).buildAndRegisterGlobal();
    }

    private SdkMeterProvider createMeterProvider(Resource resource) {
        InstrumentSelector selector = InstrumentSelector.builder().setType(InstrumentType.COUNTER).setName(configName).build();
        MetricExporter metricExporter = metricConfig.getMetricExporter();
        return metricExporter.createMeterProviderBuilder().registerView(selector, View.builder().build()).setResource(resource).build();
    }

    private SdkTracerProvider createTracerProvider(Resource resource, SdkMeterProvider meterProvider) {
        TraceExporter exporter = tracingConfig.getTraceExporter();
        return exporter.createSdkTracerProviderBuilder(meterProvider).setResource(resource).build();
    }


    @Override
    public ConnectionValidationResult validate(OpenTelemetryConnection connection) {
        return ConnectionValidationResult.success();
    }
}
