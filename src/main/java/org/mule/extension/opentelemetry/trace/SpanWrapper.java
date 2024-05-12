package org.mule.extension.opentelemetry.trace;

import io.opentelemetry.api.trace.SpanKind;
import org.mule.runtime.api.component.location.ComponentLocation;

public class SpanWrapper {
    private String eventId;
    private SpanKind spanKind;

    private ComponentLocation componentLocation;

    private FlowSpan span;

    public SpanWrapper(FlowSpan span) {
        this.span = span;
    }

    public String getEventId() {
        return eventId;
    }

    public SpanWrapper setEventId(String eventId) {
        this.eventId = eventId;
        return this;
    }



    public ComponentLocation getComponentLocation() {
        return componentLocation;
    }

    public SpanWrapper setComponentLocation(ComponentLocation componentLocation) {
        this.componentLocation = componentLocation;
        return this;
    }

    public SpanWrapper setSpanKind(SpanKind spanKind) {
        this.spanKind = spanKind;
        return this;
    }


    public FlowSpan getSpan() {
        return span;
    }

    public SpanWrapper setSpan(FlowSpan span) {
        this.span = span;
        return this;
    }

    @Override
    public String toString() {
        return "Span {" +
                "eventId='" + eventId + '\'' +
                ", spanKind=" + spanKind +
                ", componentLocation=" + componentLocation +
                ", span=" + span +
                '}';
    }
}
