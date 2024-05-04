package org.mule.extension.opentelemetry.module.trace;

import org.mule.runtime.api.util.MultiMap;
import org.mule.runtime.extension.api.annotation.param.Content;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;
import org.mule.runtime.extension.api.annotation.param.display.Summary;

import java.util.Objects;

public class FlowSpan  {
    @Parameter
    @DisplayName("Context ID")
    @Summary("The ID to identify the span context across downstream systems")
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
