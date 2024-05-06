package org.mule.extension.opentelemetry.module.trace;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanBuilder;
import io.opentelemetry.api.trace.StatusCode;

import java.io.Serializable;

public class Transaction implements Serializable {
    private final String id;
    private  Span span;
    private StatusCode statusCode = StatusCode.UNSET;
    private final SpanBuilder spanBuilder;

    public Transaction(String id,  SpanBuilder spanBuilder) {
        this.id = id;
        this.spanBuilder = spanBuilder;
    }

    public String getId() {
        return id;
    }

    public Span getSpan() {
        return span;
    }

    public StatusCode getStatusCode() {
        return statusCode;
    }

    public Transaction setStatusCode(StatusCode statusCode) {
        this.statusCode = statusCode;
        return this;
    }

    public Transaction setSpan(Span span) {
        this.span = span;
        return this;
    }

    public SpanBuilder getSpanBuilder() {
        return spanBuilder;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id='" + id + '\'' +
                ", span=" + span +
                '}';
    }
}
