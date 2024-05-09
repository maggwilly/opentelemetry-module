package org.mule.extension.opentelemetry.internal.service;

import io.opentelemetry.context.Context;
import io.opentelemetry.context.propagation.TextMapGetter;
import io.opentelemetry.context.propagation.TextMapSetter;
import org.mule.extension.opentelemetry.api.SpanContextHolder;
import org.mule.runtime.api.store.ObjectStore;

import java.io.Serializable;

public interface ContextPropagator {
    Context extractContext(SpanContextHolder source);

    void storeLocally(Context extractContext, String transactionId);

    Context retrieveLocally(String transactionId);

    void store(ObjectStore<Serializable> objectStore, Context context, String contextId);

    <T> void  injectTraceContext(Context context, T carrier, TextMapSetter<T> textMapSetter);

    <T>  Context getTraceContext(T carrier, TextMapGetter<T> textMapGetter);
}
