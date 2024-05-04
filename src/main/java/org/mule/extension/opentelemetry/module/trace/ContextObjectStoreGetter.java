package org.mule.extension.opentelemetry.module.trace;

import io.opentelemetry.context.propagation.TextMapGetter;
import org.mule.runtime.api.store.ObjectStore;
import org.mule.runtime.api.store.ObjectStoreException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;

public  class ContextObjectStoreGetter implements TextMapGetter<ObjectStore<? extends Serializable>> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ContextObjectStoreGetter.class);
    private final String contextId;

    public ContextObjectStoreGetter(String contextId) {
        this.contextId = contextId;
    }

    @Override
    public Iterable<String> keys(ObjectStore<? extends Serializable> objectStore) {
        return Collections.unmodifiableList(Arrays.asList("traceparent", "tracestate"));
    }

    @Nullable
    @Override
    public String get(@Nullable ObjectStore<? extends Serializable> objectStore, String key) {
        LOGGER.info("Getting value Context - {}", key);
        String format = String.format("%s:%s", contextId, key);
        Object retrieve = retrieve(objectStore, format);
        return Objects.nonNull(retrieve) ? retrieve.toString() : null;
    }

    private Object retrieve(@Nonnull ObjectStore<? extends Serializable> objectStore, String key){
        try {
           return objectStore.retrieve(key) ;
        } catch (ObjectStoreException e) {
           return null;
        }
    }

}