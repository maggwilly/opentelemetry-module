package org.mule.extension.opentelemetry.module.api;

import org.mule.runtime.extension.api.annotation.dsl.xml.TypeDsl;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;
import org.mule.runtime.extension.api.annotation.param.display.Summary;

@TypeDsl(allowTopLevelDefinition = true)
public class ObjectStoreContextHolder implements SpanContextHolder {
    @Parameter
    @DisplayName("Context ID")
    @Summary("The ID identifying the span context in the object store")
    private String contextId;

    public String getContextId() {
        return contextId;
    }

    public ObjectStoreContextHolder setContextId(String contextId) {
        this.contextId = contextId;
        return this;
    }

}
