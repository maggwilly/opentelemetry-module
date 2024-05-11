package org.mule.extension.opentelemetry.internal.context;

import io.opentelemetry.context.Context;
import io.opentelemetry.context.propagation.ContextPropagators;
import io.opentelemetry.context.propagation.TextMapGetter;
import io.opentelemetry.context.propagation.TextMapPropagator;
import io.opentelemetry.context.propagation.TextMapSetter;
import org.mule.extension.opentelemetry.trace.ContextObjectStoreGetter;
import org.mule.extension.opentelemetry.trace.ContextObjectStoreSetter;
import org.mule.runtime.api.store.ObjectStore;
import org.mule.runtime.api.store.ObjectStoreManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.Serializable;
import java.util.Objects;

public class DefaultContextManager implements ContextManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultContextManager.class);
    private final ObjectStoreManager objectStoreManager;

    private final ContextPropagators contextPropagators;
    @Inject
    public DefaultContextManager(ObjectStoreManager objectStoreManager, ContextPropagators contextPropagators) {
        this.objectStoreManager = objectStoreManager;
        this.contextPropagators = contextPropagators;
    }

    public void store(Context context, String transactionId) {
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

    public Context retrieve(String transactionId) {
        LOGGER.trace("Retrieving  Context - {}", transactionId);
        ObjectStore<Serializable> defaultPartition = objectStoreManager.getDefaultPartition();
        ContextObjectStoreGetter textMapGetter = new ContextObjectStoreGetter(transactionId);
        return getTraceContext(defaultPartition, textMapGetter);
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
