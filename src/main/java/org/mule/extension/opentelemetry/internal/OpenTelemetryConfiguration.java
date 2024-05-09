package org.mule.extension.opentelemetry.internal;

import org.mule.extension.opentelemetry.internal.notification.MulePipelineMessageNotificationListener;
import org.mule.extension.opentelemetry.internal.service.ConnectionHolder;
import org.mule.extension.opentelemetry.internal.service.OpenTelemetryConnectionHolder;
import org.mule.runtime.api.connection.ConnectionProvider;
import org.mule.runtime.api.lifecycle.Initialisable;
import org.mule.runtime.api.lifecycle.InitialisationException;
import org.mule.runtime.api.notification.NotificationListenerRegistry;
import org.mule.runtime.extension.api.annotation.Operations;
import org.mule.runtime.extension.api.annotation.connectivity.ConnectionProviders;
import org.mule.runtime.extension.api.annotation.param.Connection;
import org.mule.runtime.extension.api.annotation.param.RefName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;


@Operations(OpenTelemetryOperations.class)
@ConnectionProviders({OpenTelemetryConnectionProvider.class})
public class OpenTelemetryConfiguration implements Initialisable {
    private final Logger LOGGER = LoggerFactory.getLogger(OpenTelemetryConfiguration.class);
    @Inject
    private NotificationListenerRegistry notificationListenerRegistry;
    @Inject
    private OpenTelemetryConnectionHolder connectionHolder;
    @RefName
    private String configName;

    public String getConfigName() {
        return configName;
    }

    @Override
    public void initialise() throws InitialisationException {
            MulePipelineMessageNotificationListener notificationListener = new MulePipelineMessageNotificationListener( connectionHolder);
            notificationListenerRegistry.registerListener(notificationListener);
    }
}
