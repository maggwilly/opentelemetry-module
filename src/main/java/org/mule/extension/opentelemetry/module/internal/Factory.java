package org.mule.extension.opentelemetry.module.internal;

public interface Factory<T,R> {
    R create(T input);
}
