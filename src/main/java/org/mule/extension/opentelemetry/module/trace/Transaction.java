package org.mule.extension.opentelemetry.module.trace;

import io.opentelemetry.api.trace.Span;


import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class Transaction {
    private final String id;

    private final Map<String, String> tags = new HashMap<>();
    private final Span span;
    private final String traceId;
    private final Instant startTime;
    private Instant endTime;

    public Transaction(String id, Span span, String traceId, Instant startTime) {
        this.id = id;
        this.span = span;
        this.traceId = traceId;
        this.startTime = startTime;
    }

    public String getId() {
        return id;
    }

    public Map<String, String> getTags() {
        return tags;
    }

    public Span getSpan() {
        return span;
    }

    public String getTraceId() {
        return traceId;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public Instant getEndTime() {
        return endTime;
    }

    public Transaction setEndTime(Instant endTime) {
        this.endTime = endTime;
        return this;
    }
}
