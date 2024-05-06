package org.mule.extension.opentelemetry.trace;

import io.opentelemetry.context.propagation.TextMapSetter;
import org.mule.runtime.api.store.ObjectStore;
import org.mule.runtime.api.store.ObjectStoreException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.io.Serializable;

public  class ContextObjectStoreSetter implements TextMapSetter<ObjectStore<Serializable>> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ContextObjectStoreSetter.class);
    private final String contextId;

    public ContextObjectStoreSetter(String contextId) {
        this.contextId = contextId;
    }


    @Override
    public void set(@Nullable ObjectStore<Serializable> objectStore, String key, String value) {
        LOGGER.trace("Setting value - {}- {}", key, value);
        String format = String.format("%s:%s", contextId, key);
        try {
            objectStore.store(format, value);
        } catch (ObjectStoreException e) {
            LOGGER.warn("{}",e.getMessage());
        }
    }
}