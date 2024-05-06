package org.mule.extension.opentelemetry.module.trace;

import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.api.trace.StatusCode;
import org.mule.runtime.api.component.location.ComponentLocation;


import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class SpanWrapper {
    private String eventId;
    private SpanKind spanKind;
    private String errorMessage;
    private StatusCode statusCode;
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

    public SpanKind getSpanKind() {
        return spanKind;
    }

    public SpanWrapper setSpanKind(SpanKind spanKind) {
        this.spanKind = spanKind;
        return this;
    }


    public String getErrorMessage() {
        return errorMessage;
    }

    public SpanWrapper setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
        return this;
    }

    public StatusCode getStatusCode() {
        return statusCode;
    }

    public SpanWrapper setStatusCode(StatusCode statusCode) {
        this.statusCode = statusCode;
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
                ", errorMessage='" + errorMessage + '\'' +
                ", statusCode=" + statusCode +
                ", componentLocation=" + componentLocation +
                ", span=" + span +
                '}';
    }
}
