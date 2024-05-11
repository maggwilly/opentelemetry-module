package org.mule.extension.opentelemetry.internal.context;

import io.opentelemetry.context.Context;
import io.opentelemetry.context.propagation.TextMapGetter;
import io.opentelemetry.context.propagation.TextMapSetter;
import org.mule.runtime.api.store.ObjectStore;

import java.io.Serializable;

public interface ContextManager {
    void store(Context extractContext, String transactionId);

    Context retrieve(String transactionId);

    void store(ObjectStore<Serializable> objectStore, Context context, String contextId);

    <T> void  injectTraceContext(Context context, T carrier, TextMapSetter<T> textMapSetter);

    <T>  Context getTraceContext(T carrier, TextMapGetter<T> textMapGetter);
}
