package org.mule.extension.opentelemetry.module.internal.config;

import org.mule.runtime.api.meta.ExpressionSupport;
import org.mule.runtime.api.store.ObjectStore;
import org.mule.runtime.extension.api.annotation.Expression;
import org.mule.runtime.extension.api.annotation.dsl.xml.ParameterDsl;
import org.mule.runtime.extension.api.annotation.param.Parameter;

public class TracingConfig {
    @Parameter
    @Expression(ExpressionSupport.NOT_SUPPORTED)
    @ParameterDsl(allowInlineDefinition = false)
    private ObjectStore objectStore;

    public ObjectStore getObjectStore() {
        return objectStore;
    }

    public TracingConfig setObjectStore(ObjectStore objectStore) {
        this.objectStore = objectStore;
        return this;
    }

}
