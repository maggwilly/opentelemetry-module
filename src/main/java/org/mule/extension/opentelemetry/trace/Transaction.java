package org.mule.extension.opentelemetry.trace;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanBuilder;
import io.opentelemetry.api.trace.StatusCode;

import java.io.Serializable;

public class Transaction implements Serializable {
    private final String id;
    private  Span span;
    private StatusCode statusCode = StatusCode.UNSET;
    public Transaction(String id, Span span) {
        this.id = id;
        this.span = span;
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

    @Override
    public String toString() {
        return "Transaction{" +
                "id='" + id + '\'' +
                ", span=" + span +
                '}';
    }
}
