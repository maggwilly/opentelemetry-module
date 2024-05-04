package org.mule.extension.opentelemetry.module.trace;

import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.api.trace.StatusCode;
import org.mule.runtime.api.component.location.ComponentLocation;


import java.time.Instant;
import java.util.Map;

public class SpanWrapper {
    private String transactionId;
    private Map<String, String> tags;
    private SpanKind spanKind;
    private String errorMessage;
    private StatusCode statusCode;
    private ComponentLocation componentLocation;
    private Instant startTime = Instant.now();
    private Instant endTime;
    private FlowSpan span;
    public SpanWrapper(FlowSpan span) {
        this.span = span;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public SpanWrapper setTransactionId(String transactionId) {
        this.transactionId = transactionId;
        return this;
    }

    public Map<String, String> getTags() {
        return tags;
    }

    public SpanWrapper setTags(Map<String, String> tags) {
        this.tags = tags;
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

    public Instant getStartTime() {
        return startTime;
    }

    public SpanWrapper setStartTime(Instant startTime) {
        this.startTime = startTime;
        return this;
    }

    public Instant getEndTime() {
        return endTime;
    }

    public SpanWrapper setEndTime(Instant endTime) {
        this.endTime = endTime;
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
                "transactionId='" + transactionId + '\'' +
                ", tags=" + tags +
                ", spanKind=" + spanKind +
                ", errorMessage='" + errorMessage + '\'' +
                ", statusCode=" + statusCode +
                ", componentLocation=" + componentLocation +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", span=" + span +
                '}';
    }
}
