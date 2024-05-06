package org.mule.extension.opentelemetry.trace;

import org.mule.runtime.api.meta.ExpressionSupport;
import org.mule.runtime.api.store.ObjectStore;
import org.mule.runtime.api.util.MultiMap;
import org.mule.runtime.extension.api.annotation.Expression;
import org.mule.runtime.extension.api.annotation.param.Content;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;
import org.mule.runtime.extension.api.annotation.param.display.Summary;
import org.mule.runtime.extension.api.annotation.param.reference.ObjectStoreReference;

import java.util.Objects;

public class FlowSpan  {
    @Parameter
    @DisplayName("Context ID")
    @Summary("The key of the context in cache")
    private String contextId;
    @Parameter
    @Optional
    @DisplayName("Span Name")
    @Summary("The name to identify the span")
    private String name;

    @Content(primary = true)
    @Parameter
    @Optional
    private MultiMap<String, String> attributes= MultiMap.emptyMultiMap();
    @Optional
    @Parameter
    @ObjectStoreReference
    @Expression(ExpressionSupport.NOT_SUPPORTED)
    @Summary("The cache as context propagator.")
    private ObjectStore propagator;

    public String getContextId() {
        return contextId;
    }

    public FlowSpan setContextId(String contextId) {
        this.contextId = contextId;
        return this;
    }

    public String getName() {
        return name;
    }

    public FlowSpan setName(String name) {
        this.name = name;
        return this;
    }
    public ObjectStore getPropagator() {
        return propagator;
    }

    public FlowSpan setPropagator(ObjectStore propagator) {
        this.propagator = propagator;
        return this;
    }
    public MultiMap<String, String> getAttributes() {
        return attributes;
    }

    public FlowSpan setAttributes(MultiMap<String, String> attributes) {
        this.attributes = attributes;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FlowSpan flowSpan = (FlowSpan) o;
        return contextId.equals(flowSpan.contextId) && name.equals(flowSpan.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(contextId, name);
    }

    @Override
    public String toString() {
        return "Span{" +
                "contextId='" + contextId + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
