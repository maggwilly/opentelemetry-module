package org.mule.extension.opentelemetry.internal.service;

import org.mule.extension.opentelemetry.internal.OpenTelemetryConfiguration;
import org.mule.extension.opentelemetry.internal.OpenTelemetryConnection;
import org.mule.runtime.api.exception.MuleException;
import org.mule.runtime.extension.api.annotation.param.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class OpenTelemetryConnectionHolder implements ConnectionHolder<OpenTelemetryConnection>{
    private static final Logger LOGGER = LoggerFactory.getLogger(OpenTelemetryConnectionHolder.class);
    private  OpenTelemetryConnection connection;
    @Config
    private OpenTelemetryConfiguration configuration;
    @Override
    public OpenTelemetryConnection getConnection() {
        return connection;
    }

    @Override
    public OpenTelemetryConnection init(OpenTelemetryConnection connection) {
        if (Objects.isNull(this.connection)) {
            LOGGER.info("Setting connection Init {}", connection);
            return this.connection = connection.start();
        }
        return this.connection ;
    }


    @Override
    public void stop(OpenTelemetryConnection connection) {
        try {
            connection.stop();
        } catch (MuleException e) {
            LOGGER.error("Failed to stop connection holder ", e);
        }
    }
}
