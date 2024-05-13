package org.mule.extension.opentelemetry.trace;

import org.mule.runtime.api.util.MultiMap;
import org.mule.runtime.extension.api.annotation.param.Content;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;
import org.mule.runtime.extension.api.annotation.param.display.Summary;

import java.util.Objects;

public class Metric {
    @Parameter
    @Optional
    @DisplayName("Metric name")
    @Summary("The name to identify the metric")
    private String name;

    @Content(primary = true)
    @Parameter
    @Optional
    private MultiMap<String, String> attributes= MultiMap.emptyMultiMap();


    public String getName() {
        return name;
    }

    public Metric setName(String name) {
        this.name = name;
        return this;
    }

    public MultiMap<String, String> getAttributes() {
        return attributes;
    }

    public Metric setAttributes(MultiMap<String, String> attributes) {
        this.attributes = attributes;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Metric flowSpan = (Metric) o;
        return Objects.equals(name, flowSpan.name) && Objects.equals(attributes, flowSpan.attributes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, attributes);
    }
}
