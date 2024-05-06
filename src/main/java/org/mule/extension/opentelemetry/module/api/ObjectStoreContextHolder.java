package org.mule.extension.opentelemetry.module.api;

import org.mule.runtime.api.meta.ExpressionSupport;
import org.mule.runtime.api.store.ObjectStore;
import org.mule.runtime.extension.api.annotation.Expression;
import org.mule.runtime.extension.api.annotation.dsl.xml.TypeDsl;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;
import org.mule.runtime.extension.api.annotation.param.display.Summary;
import org.mule.runtime.extension.api.annotation.param.reference.ObjectStoreReference;

import java.util.Objects;

@TypeDsl(allowTopLevelDefinition = true)
public class ObjectStoreContextHolder implements SpanContextHolder {
    @Parameter
    @DisplayName("Context ID")
    @Summary("The ID identifying the span context in the object store")
    private String contextId;

    @Parameter
    @ObjectStoreReference
    @Expression(ExpressionSupport.NOT_SUPPORTED)
    private ObjectStore objectStore;

    public String getContextId() {
        return contextId;
    }

    public ObjectStoreContextHolder setContextId(String contextId) {
        this.contextId = contextId;
        return this;
    }

    public ObjectStore getObjectStore() {
        return objectStore;
    }

    public ObjectStoreContextHolder setObjectStore(ObjectStore objectStore) {
        this.objectStore = objectStore;
        return this;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ObjectStoreContextHolder that = (ObjectStoreContextHolder) o;
        return contextId.equals(that.contextId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(contextId);
    }

    @Override
    public String toString() {
        return "ObjectStoreContextHolder{" +
                "contextId='" + contextId + '\'' +
                '}';
    }
}
