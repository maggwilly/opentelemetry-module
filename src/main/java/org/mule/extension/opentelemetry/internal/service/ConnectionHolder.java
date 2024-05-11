package org.mule.extension.opentelemetry.internal.service;

import org.mule.extension.opentelemetry.internal.OpenTelemetryConnection;

public interface ConnectionHolder<T> {
    T getConnection();
    T init(T connection);

    void stop(OpenTelemetryConnection connection);
}
