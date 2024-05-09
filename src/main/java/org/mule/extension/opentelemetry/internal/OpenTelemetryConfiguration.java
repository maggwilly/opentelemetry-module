package org.mule.extension.opentelemetry.internal;

import org.mule.extension.opentelemetry.internal.notification.MulePipelineMessageNotificationListener;
import org.mule.runtime.api.lifecycle.InitialisationException;
import org.mule.runtime.api.lifecycle.Startable;
import org.mule.runtime.api.notification.NotificationListenerRegistry;
import org.mule.runtime.extension.api.annotation.Operations;
import org.mule.runtime.extension.api.annotation.connectivity.ConnectionProviders;
import org.mule.runtime.extension.api.annotation.param.Connection;
import org.mule.runtime.extension.api.annotation.param.RefName;

import javax.inject.Inject;


@Operations(OpenTelemetryOperations.class)
@ConnectionProviders({OpenTelemetryConnectionProvider.class})
public class OpenTelemetryConfiguration implements Startable {
    @Inject
    private NotificationListenerRegistry notificationListenerRegistry;
    @Connection
    private OpenTelemetryConnection openTelemetryConnection;
    @RefName
    private String configName;

    public String getConfigName() {
        return configName;
    }

    @Override
    public void start() throws InitialisationException {
            MulePipelineMessageNotificationListener notificationListener = new MulePipelineMessageNotificationListener(openTelemetryConnection.getTraceCollector());
            notificationListenerRegistry.registerListener(notificationListener);
            openTelemetryConnection.start();
    }

}
