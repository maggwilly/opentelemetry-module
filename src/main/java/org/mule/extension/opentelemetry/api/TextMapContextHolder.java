package org.mule.extension.opentelemetry.api;

import org.mule.runtime.extension.api.annotation.dsl.xml.ParameterDsl;
import org.mule.runtime.extension.api.annotation.dsl.xml.TypeDsl;
import org.mule.runtime.extension.api.annotation.param.Content;
import org.mule.runtime.extension.api.annotation.param.Parameter;

import java.util.Map;
import java.util.Objects;

@TypeDsl(allowTopLevelDefinition = true)
public class TextMapContextHolder implements SpanContextHolder {
    @Content
    @Parameter
    @ParameterDsl(allowInlineDefinition = false)
    private Map<String, String> value;

    public Map<String, String> getValue() {
        return value;
    }

    public TextMapContextHolder setValue(Map<String, String> value) {
        this.value = value;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TextMapContextHolder that = (TextMapContextHolder) o;
        return value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "TextMapContextHolder {" +
                "value=" + value +
                '}';
    }
}
