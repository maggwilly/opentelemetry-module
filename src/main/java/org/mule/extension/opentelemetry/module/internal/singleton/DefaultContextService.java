package org.mule.extension.opentelemetry.module.internal.singleton;

import io.opentelemetry.context.Context;
import io.opentelemetry.context.propagation.TextMapGetter;
import io.opentelemetry.context.propagation.TextMapPropagator;
import io.opentelemetry.context.propagation.TextMapSetter;
import org.mule.extension.opentelemetry.module.api.ObjectStoreContextHolder;
import org.mule.extension.opentelemetry.module.api.SpanContextHolder;
import org.mule.extension.opentelemetry.module.api.TextMapContextHolder;
import org.mule.extension.opentelemetry.module.internal.config.TracingConfig;
import org.mule.extension.opentelemetry.module.trace.*;
import org.mule.runtime.api.store.ObjectStore;
import org.mule.runtime.api.store.ObjectStoreManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.Serializable;
import java.util.Map;
import java.util.Objects;

public class DefaultContextService implements ContextService {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultContextService.class);
    @Inject
    private ObjectStoreManager objectStoreManager;

    @Inject
    private OpenTelemetryProvider openTelemetryProvider;

    public void storeLocal(Context context, String transactionId) {
        LOGGER.info("Storing  Context - for {}", transactionId);
        ContextObjectStoreSetter contextObjectStoreSetter = new ContextObjectStoreSetter(transactionId);
        ObjectStore<Serializable> defaultPartition = objectStoreManager.getDefaultPartition();
        this.injectTraceContext(context, defaultPartition, contextObjectStoreSetter);
    }

    public Context retrieveLocal(String transactionId) {
        LOGGER.info("Retrieving  Context - {}", transactionId);
        ObjectStore<Serializable> defaultPartition = objectStoreManager.getDefaultPartition();
        ContextObjectStoreGetter textMapGetter = new ContextObjectStoreGetter(transactionId);
        return getTraceContext(defaultPartition, textMapGetter);
    }

    public Context extractContext(SpanContextHolder contextHolder) {
        LOGGER.info("Getting  context from  {}", contextHolder);
        if (Objects.nonNull(contextHolder)) {
            if (contextHolder instanceof TextMapContextHolder) {
                Map<String, String> stringMap = ((TextMapContextHolder) contextHolder).getValue();
                return getTraceContext(stringMap, ContextMapGetter.INSTANCE);
            }
            ObjectStoreContextHolder storeContextHolder = (ObjectStoreContextHolder) contextHolder;
            ObjectStore objectStore = storeContextHolder.getObjectStore();
            ContextObjectStoreGetter textMapGetter = new ContextObjectStoreGetter(storeContextHolder.getContextId());
            return getTraceContext(objectStore, textMapGetter);
        }
        return Context.current();
    }

    private void storeContext(SpanWrapper trace, TracingConfig tracingConfig, TransactionContext transactionContext) {
        LOGGER.info("Storing  transaction Context - {}", transactionContext);
        ObjectStore objectStore = tracingConfig.getObjectStore();
        String contextId = trace.getSpan().getContextId();
        ContextObjectStoreSetter contextObjectStoreSetter = new ContextObjectStoreSetter(contextId);
        this.injectTraceContext(transactionContext.getContext(), objectStore, contextObjectStoreSetter);
    }

    public <T> Context getTraceContext(T carrier, TextMapGetter<T> textMapGetter) {
        TextMapPropagator textMapPropagator = openTelemetryProvider.getContextPropagators().getTextMapPropagator();
        return textMapPropagator.extract(Context.current(), carrier, textMapGetter);
    }

    public <T> void injectTraceContext(Context context, T carrier, TextMapSetter<T> textMapSetter) {
        TextMapPropagator textMapPropagator = openTelemetryProvider.getContextPropagators().getTextMapPropagator();
        textMapPropagator.inject(context, carrier, textMapSetter);
    }

}
