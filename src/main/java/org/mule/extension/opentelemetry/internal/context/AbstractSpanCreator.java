package org.mule.extension.opentelemetry.internal.context;

import org.mule.extension.opentelemetry.internal.exception.ContextPropagatorException;
import org.mule.extension.opentelemetry.internal.exception.ParentContextException;
import org.mule.extension.opentelemetry.internal.interceptor.AbstractTracingHandler;
import org.mule.extension.opentelemetry.trace.FlowSpan;
import org.mule.extension.opentelemetry.trace.SpanWrapper;
import org.mule.extension.opentelemetry.util.OplUtils;
import org.mule.runtime.api.component.location.ComponentLocation;
import org.mule.runtime.api.event.Event;
import org.mule.runtime.api.exception.MuleException;
import org.mule.runtime.api.util.MultiMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractSpanCreator {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractSpanCreator.class);
    protected abstract void  extractParentContext(Event event) throws ParentContextException, ContextPropagatorException;

    protected abstract  MultiMap<String, String> getAttributes(Event event);

    public SpanWrapper createSpan(Event event, ComponentLocation componentLocation)  {
        try {
            this.extractParentContext(event);
        } catch (ParentContextException | ContextPropagatorException e) {
            LOGGER.warn("Failed to extract parent span from source {}", e.getMessage());
        }
        MultiMap<String, String> attributes = getAttributes(event);
        FlowSpan span = new FlowSpan().setName(componentLocation.getRootContainerName()).setAttributes(attributes);
        return OplUtils.createSpan(span, event.getContext().getId(), componentLocation);
    }
}
