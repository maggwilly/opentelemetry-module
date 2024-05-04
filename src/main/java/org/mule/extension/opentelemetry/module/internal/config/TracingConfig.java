package org.mule.extension.opentelemetry.module.internal.config;

import org.mule.runtime.api.meta.ExpressionSupport;
import org.mule.runtime.api.store.ObjectStore;
import org.mule.runtime.extension.api.annotation.Expression;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.reference.ObjectStoreReference;

public class TracingConfig {
    @Parameter
    @ObjectStoreReference
    @Expression(ExpressionSupport.NOT_SUPPORTED)
    private ObjectStore objectStore;

    public ObjectStore getObjectStore() {
        return objectStore;
    }

    public TracingConfig setObjectStore(ObjectStore objectStore) {
        this.objectStore = objectStore;
        return this;
    }
}
