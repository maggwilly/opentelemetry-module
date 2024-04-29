package org.mule.extension.opentelemetry.module.trace;

import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.context.Context;


import java.time.Instant;
import java.util.Map;

public class Trace {
    private String transactionId;
    private Map<String, String> tags;
    private final String name;
    private Context context;
    private SpanKind spanKind;
    private String errorMessage;
    private StatusCode statusCode;

    private Instant startTime = Instant.now();
    private Instant endTime;

    public Trace(String name) {
        this.name = name;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public Trace setTransactionId(String transactionId) {
        this.transactionId = transactionId;
        return this;
    }

    public Map<String, String> getTags() {
        return tags;
    }

    public Trace setTags(Map<String, String> tags) {
        this.tags = tags;
        return this;
    }

    public String getName() {
        return name;
    }

    public Context getContext() {
        return context;
    }

    public Trace setContext(Context context) {
        this.context = context;
        return this;
    }

    public SpanKind getSpanKind() {
        return spanKind;
    }

    public Trace setSpanKind(SpanKind spanKind) {
        this.spanKind = spanKind;
        return this;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public Trace setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
        return this;
    }

    public StatusCode getStatusCode() {
        return statusCode;
    }

    public Trace setStatusCode(StatusCode statusCode) {
        this.statusCode = statusCode;
        return this;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public Trace setStartTime(Instant startTime) {
        this.startTime = startTime;
        return this;
    }

    public Instant getEndTime() {
        return endTime;
    }

    public Trace setEndTime(Instant endTime) {
        this.endTime = endTime;
        return this;
    }

    @Override
    public String toString() {
        return "Trace{" +
                "name='" + name + '\'' +
                ", context=" + context +
                ", spanKind=" + spanKind +
                ", errorMessage='" + errorMessage + '\'' +
                ", statusCode=" + statusCode +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                '}';
    }
}
