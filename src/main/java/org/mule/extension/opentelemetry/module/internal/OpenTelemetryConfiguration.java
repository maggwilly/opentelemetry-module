package org.mule.extension.opentelemetry.module.internal;

import org.mule.extension.opentelemetry.module.internal.config.MetricConfig;
import org.mule.extension.opentelemetry.module.internal.config.TracingConfig;
import org.mule.extension.opentelemetry.module.internal.notification.MulePipelineMessageNotificationListener;
import org.mule.extension.opentelemetry.module.internal.singleton.MetricCollector;
import org.mule.extension.opentelemetry.module.internal.singleton.OpenTelemetryProvider;
import org.mule.runtime.api.lifecycle.InitialisationException;
import org.mule.runtime.api.lifecycle.Startable;
import org.mule.runtime.api.meta.ExpressionSupport;
import org.mule.runtime.api.notification.NotificationListenerRegistry;
import org.mule.runtime.extension.api.annotation.Expression;
import org.mule.runtime.extension.api.annotation.Operations;
import org.mule.runtime.extension.api.annotation.connectivity.ConnectionProviders;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.RefName;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;
import org.mule.runtime.extension.api.annotation.param.display.Placement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;


@Operations(OpenTelemetryOperations.class)
@ConnectionProviders({OpenTelemetryConnectionProvider.class})
public class OpenTelemetryConfiguration implements Startable {
    private final Logger LOGGER = LoggerFactory.getLogger("monitoring.opentelemetry.logger");

    @Inject
    NotificationListenerRegistry notificationListenerRegistry;
    @Inject
    private OpenTelemetryProvider openTelemetryProvider;

    @Inject
    private MetricCollector metricCollector;

    @RefName
    private String configName;

    @DisplayName("Service Name")
    @Parameter
    private String serviceName;

    @DisplayName("Service Version")
    @Parameter
    private String serviceVersion;

    @Parameter
    @Placement(tab = "Metric")
    @Expression(ExpressionSupport.NOT_SUPPORTED)
    private MetricConfig metricConfig;

    @Parameter
    @Placement(tab = "Tracing")
    @Expression(ExpressionSupport.NOT_SUPPORTED)
    private TracingConfig tracingConfig;

    public String getConfigName() {
        return configName;
    }


    public void setConfigName(String configName) {
        this.configName = configName;
    }


    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }


    public void setServiceVersion(String serviceVersion) {
        this.serviceVersion = serviceVersion;
    }


    @Override
    public void start() throws InitialisationException {
        try {
            notificationListenerRegistry.registerListener(new MulePipelineMessageNotificationListener());
            openTelemetryProvider.initialise(this);
            metricCollector.initialise(this);
        } catch (Exception e) {
            LOGGER.error("Failed to initialize the opentelemetry module: {} {}", e.getMessage(), this);
        }
    }

    public String getServiceName() {
        return this.serviceName;
    }

    public String getServiceVersion() {
        return this.serviceVersion;
    }

    public MetricConfig getMetricConfig() {
        return metricConfig;
    }

    public OpenTelemetryConfiguration setMetricConfig(MetricConfig metricConfig) {
        this.metricConfig = metricConfig;
        return this;
    }

    public TracingConfig getTracingConfig() {
        return tracingConfig;
    }

    public OpenTelemetryConfiguration setTracingConfig(TracingConfig tracingConfig) {
        this.tracingConfig = tracingConfig;
        return this;
    }

    @Override
    public String toString() {
        return "OpenTelemetryConfiguration{" +
                "notificationListenerRegistry=" + notificationListenerRegistry +
                ", openTelemetryProvider=" + openTelemetryProvider +
                ", metricCollector=" + metricCollector +
                ", configName='" + configName + '\'' +
                ", serviceName='" + serviceName + '\'' +
                ", serviceVersion='" + serviceVersion + '\'' +
                ", metricConfig=" + metricConfig +
                '}';
    }
}
