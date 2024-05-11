package org.mule.extension.opentelemetry.internal.context;

import org.mule.extension.opentelemetry.internal.exception.ContextPropagatorException;
import org.mule.extension.opentelemetry.internal.exception.ParentContextException;
import org.mule.extension.opentelemetry.trace.FlowSpan;
import org.mule.extension.opentelemetry.trace.SpanWrapper;
import org.mule.extension.opentelemetry.util.OplUtils;
import org.mule.runtime.api.component.location.ComponentLocation;
import org.mule.runtime.api.event.Event;
import org.mule.runtime.api.exception.MuleException;
import org.mule.runtime.api.util.MultiMap;

public abstract class AbstractSpanCreator {
    protected abstract void  extractParentContext(Event event) throws ParentContextException, ContextPropagatorException;

    protected abstract  MultiMap<String, String> getAttributes(Event event);

    public SpanWrapper createSpan(Event event, ComponentLocation componentLocation) throws MuleException {
        this.extractParentContext(event);
        MultiMap<String, String> attributes = getAttributes(event);
        FlowSpan span = new FlowSpan().setName(componentLocation.getRootContainerName()).setAttributes(attributes);
        return OplUtils.createSpan(span, event.getContext().getId(), componentLocation);
    }
}
