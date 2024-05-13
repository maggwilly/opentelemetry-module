package org.mule.extension.opentelemetry.internal;

import org.mule.extension.opentelemetry.internal.notification.MulePipelineMessageNotificationListener;
import org.mule.extension.opentelemetry.internal.service.OpenTelemetryConnectionHolder;
import org.mule.runtime.api.connection.ConnectionException;
import org.mule.runtime.api.connection.ConnectionProvider;
import org.mule.runtime.api.lifecycle.Initialisable;
import org.mule.runtime.api.lifecycle.Startable;
import org.mule.runtime.api.message.Message;
import org.mule.runtime.api.notification.NotificationListenerRegistry;
import org.mule.runtime.core.api.event.CoreEvent;
import org.mule.runtime.core.api.exception.NullExceptionHandler;
import org.mule.runtime.core.api.extension.ExtensionManager;
import org.mule.runtime.extension.api.annotation.Configuration;
import org.mule.runtime.extension.api.annotation.Operations;
import org.mule.runtime.extension.api.annotation.connectivity.ConnectionProviders;
import org.mule.runtime.extension.api.annotation.param.RefName;
import org.mule.runtime.extension.api.runtime.config.ConfigurationInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

import static org.mule.runtime.core.api.event.EventContextFactory.create;
import static org.mule.runtime.dsl.api.component.config.DefaultComponentLocation.fromSingleComponent;


@Operations(OpenTelemetryOperations.class)
@ConnectionProviders({OpenTelemetryConnectionProvider.class})
@Configuration
public class OpenTelemetryConfiguration implements Startable {
    private final Logger LOGGER = LoggerFactory.getLogger(OpenTelemetryConfiguration.class);
    @Inject
    private NotificationListenerRegistry notificationListenerRegistry;
    @Inject
    private ExtensionManager extensionManager;

    @Inject
    private OpenTelemetryConnectionHolder connectionHolder;
    @RefName
    private String configName;

    public String getConfigName() {
        return configName;
    }

    @Override
    public void start() {
        LOGGER.info("Initialising config {}", configName);
        CoreEvent event = CoreEvent.builder(create(configName, "dummy", fromSingleComponent(configName), NullExceptionHandler.getInstance())).message(Message.of("none")).build();
        ConfigurationInstance configuration = extensionManager.getConfiguration(getConfigName(), event);
        configuration.getConnectionProvider().ifPresent(connectionProvider1 -> {
            LOGGER.info("Initialising connectionProvider {}", connectionProvider1);
            this.doConnect(connectionProvider1);
            MulePipelineMessageNotificationListener notificationListener = new MulePipelineMessageNotificationListener(connectionHolder);
            notificationListenerRegistry.registerListener(notificationListener);
        });
    }

    private void doConnect(ConnectionProvider connectionProvider) {
        LOGGER.info("Connecting config {}", configName);
        try {
            connectionProvider.connect();
        } catch (ConnectionException e) {
            LOGGER.error("Connecting config {}", e.getMessage());
        }
    }
}
