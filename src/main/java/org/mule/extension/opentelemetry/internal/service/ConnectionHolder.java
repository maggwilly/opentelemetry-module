package org.mule.extension.opentelemetry.internal.service;

public interface ConnectionHolder<T> {
    T getConnection();
    T init(T connection);
}
