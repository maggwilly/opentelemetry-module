package org.mule.extension.opentelemetry.internal.service;

import org.mule.extension.opentelemetry.internal.OpenTelemetryConnection;

public class OpenTelemetryConnectionHolder implements ConnectionHolder<OpenTelemetryConnection>{
    private OpenTelemetryConnection connection;

    @Override
    public OpenTelemetryConnection getConnection() {
        return connection;
    }

    @Override
    public OpenTelemetryConnection init(OpenTelemetryConnection connection) {
       return this.connection = connection.start();
    }
}
