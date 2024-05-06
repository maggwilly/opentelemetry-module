package org.mule.extension.opentelemetry.trace;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanId;
import io.opentelemetry.api.trace.TraceId;
import io.opentelemetry.context.Context;
import org.mule.extension.opentelemetry.util.EncodingUtil;

public class TransactionContext {
    private final Transaction transaction;
    private Context context = Context.current();
    private String spanId = SpanId.getInvalid();
    private String traceId = TraceId.getInvalid();
    private String spanIdLong = "0";
    private String traceIdLongLowPart = "0";

    public TransactionContext(Transaction transaction) {
        this.transaction = transaction;
    }

    public static TransactionContext of(Transaction transaction) {
        Span span = transaction.getSpan();
        TransactionContext transactionContext = new TransactionContext(transaction)
                .setContext(span.storeInContext(Context.current()))
                .setSpanId(span.getSpanContext().getSpanId())
                .setTraceId(span.getSpanContext().getTraceId());
        if (SpanId.isValid(transactionContext.getSpanId())) {
            transactionContext.setSpanIdLong(EncodingUtil.longFromBase16Hex(transactionContext.getSpanId()));
        }
        if (TraceId.isValid(transactionContext.getTraceId())) {
            transactionContext.setTraceIdLongLowPart(EncodingUtil.traceIdLong(transactionContext.getTraceId())[1]);
        }
        return transactionContext;
    }

    public static TransactionContext current(Transaction transaction) {
        return new TransactionContext(transaction);
    }
    public Context getContext() {
        return context;
    }

    public TransactionContext setContext(Context context) {
        this.context = context;
        return this;
    }

    public String getSpanId() {
        return spanId;
    }

    public TransactionContext setSpanId(String spanId) {
        this.spanId = spanId;
        return this;
    }

    public String getTraceId() {
        return traceId;
    }

    public TransactionContext setTraceId(String traceId) {
        this.traceId = traceId;
        return this;
    }

    public String getSpanIdLong() {
        return spanIdLong;
    }

    public TransactionContext setSpanIdLong(String spanIdLong) {
        this.spanIdLong = spanIdLong;
        return this;
    }

    public Transaction getTransaction() {
        return transaction;
    }


    public String getTraceIdLongLowPart() {
        return traceIdLongLowPart;
    }

    public TransactionContext setTraceIdLongLowPart(String traceIdLongLowPart) {
        this.traceIdLongLowPart = traceIdLongLowPart;
        return this;
    }

    @Override
    public String toString() {
        return "TransactionContext{" +
                "context=" + context +
                ", spanId='" + spanId + '\'' +
                ", traceId='" + traceId + '\'' +
                ", spanIdLong='" + spanIdLong + '\'' +
                ", traceIdLongLowPart='" + traceIdLongLowPart + '\'' +
                '}';
    }
}
