package org.mule.extension.opentelemetry.internal.service;

import org.mule.extension.opentelemetry.trace.SpanWrapper;
import org.mule.extension.opentelemetry.trace.Transaction;
import org.mule.runtime.api.component.location.ComponentLocation;

import java.util.Optional;

public interface TraceCollector {
  void startTransaction(SpanWrapper spanWrapper);
  Optional<Transaction> endTransaction(String transactionId, ComponentLocation componentLocation);

  Optional<Transaction> endTransaction(String transactionId, ComponentLocation componentLocation, Exception exception);

}
