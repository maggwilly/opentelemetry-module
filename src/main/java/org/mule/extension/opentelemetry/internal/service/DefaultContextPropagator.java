package org.mule.extension.opentelemetry.internal.service;

import io.opentelemetry.context.Context;
import io.opentelemetry.context.propagation.ContextPropagators;
import io.opentelemetry.context.propagation.TextMapGetter;
import io.opentelemetry.context.propagation.TextMapPropagator;
import io.opentelemetry.context.propagation.TextMapSetter;
import org.mule.extension.opentelemetry.api.ObjectStoreContextHolder;
import org.mule.extension.opentelemetry.api.SpanContextHolder;
import org.mule.extension.opentelemetry.api.TextMapContextHolder;
import org.mule.extension.opentelemetry.trace.ContextMapGetter;
import org.mule.extension.opentelemetry.trace.ContextObjectStoreGetter;
import org.mule.extension.opentelemetry.trace.ContextObjectStoreSetter;
import org.mule.runtime.api.store.ObjectStore;
import org.mule.runtime.api.store.ObjectStoreManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;

public class DefaultContextPropagator implements ContextPropagator {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultContextPropagator.class);
    private final ObjectStoreManager objectStoreManager;

    private final ContextPropagators contextPropagators;

    public DefaultContextPropagator(ObjectStoreManager objectStoreManager, ContextPropagators contextPropagators) {
        this.objectStoreManager = objectStoreManager;
        this.contextPropagators = contextPropagators;
    }

    public void storeLocally(Context context, String transactionId) {
        ObjectStore<Serializable> defaultPartition = objectStoreManager.getDefaultPartition();
        this.store(defaultPartition, context, transactionId);
    }

    @Override
    public void store(ObjectStore<Serializable> objectStore, Context context, String contextId) {
        if (Objects.nonNull(objectStore) && Objects.nonNull(contextId)) {
            LOGGER.trace("Storing  Context - for {}", contextId);
            ContextObjectStoreSetter contextObjectStoreSetter = new ContextObjectStoreSetter(contextId);
            this.injectTraceContext(context, objectStore, contextObjectStoreSetter);
        }
    }

    public Context retrieveLocally(String transactionId) {
        LOGGER.trace("Retrieving  Context - {}", transactionId);
        ObjectStore<Serializable> defaultPartition = objectStoreManager.getDefaultPartition();
        ContextObjectStoreGetter textMapGetter = new ContextObjectStoreGetter(transactionId);
        return getTraceContext(defaultPartition, textMapGetter);
    }

    public Context extractContext(SpanContextHolder contextHolder) {
        LOGGER.trace("Getting  context from  {}", contextHolder);
        if (Objects.nonNull(contextHolder)) {
            if (contextHolder instanceof TextMapContextHolder) {
                Map<String, String> stringMap = ((TextMapContextHolder) contextHolder).getValue();
                return getTraceContext(stringMap, ContextMapGetter.INSTANCE);
            }
            ObjectStoreContextHolder storeContextHolder = (ObjectStoreContextHolder) contextHolder;
            ObjectStore objectStore = storeContextHolder.getPropagator();
            ContextObjectStoreGetter textMapGetter = new ContextObjectStoreGetter(storeContextHolder.getContextId());
            return getTraceContext(objectStore, textMapGetter);
        }
        return Context.current();
    }

    public <T> Context getTraceContext(T carrier, TextMapGetter<T> textMapGetter) {
        TextMapPropagator textMapPropagator = contextPropagators.getTextMapPropagator();
        return textMapPropagator.extract(Context.current(), carrier, textMapGetter);
    }

    public <T> void injectTraceContext(Context context, T carrier, TextMapSetter<T> textMapSetter) {
        TextMapPropagator textMapPropagator = contextPropagators.getTextMapPropagator();
        textMapPropagator.inject(context, carrier, textMapSetter);
    }

}
