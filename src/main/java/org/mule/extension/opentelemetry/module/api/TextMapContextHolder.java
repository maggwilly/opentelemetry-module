package org.mule.extension.opentelemetry.module.api;

import org.mule.runtime.api.util.MultiMap;
import org.mule.runtime.extension.api.annotation.dsl.xml.TypeDsl;
import org.mule.runtime.extension.api.annotation.param.Content;
import org.mule.runtime.extension.api.annotation.param.Parameter;
@TypeDsl(allowTopLevelDefinition = true)
public class TextMapContextHolder implements SpanContextHolder {
    @Content
    @Parameter
    private MultiMap<String, String> carrier;

    public MultiMap<String, String> getCarrier() {
        return carrier;
    }

    public TextMapContextHolder setCarrier(MultiMap<String, String> carrier) {
        this.carrier = carrier;
        return this;
    }

}
