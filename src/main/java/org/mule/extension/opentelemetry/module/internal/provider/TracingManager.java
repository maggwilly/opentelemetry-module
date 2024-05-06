package org.mule.extension.opentelemetry.module.internal.provider;

import org.mule.extension.opentelemetry.module.api.SpanContextHolder;
import org.mule.extension.opentelemetry.module.internal.OplInitialisable;
import org.mule.extension.opentelemetry.module.trace.SpanWrapper;
import org.mule.extension.opentelemetry.module.trace.Transaction;
import org.mule.runtime.api.component.location.ComponentLocation;

import java.util.Optional;

public interface TracingManager extends OplInitialisable {
  void startTransaction(SpanWrapper spanWrapper);
  Optional<Transaction> endTransaction(String transactionId, ComponentLocation componentLocation);

  Optional<Transaction> endTransaction(String transactionId, ComponentLocation componentLocation, Exception exception);

    void startTransaction(SpanContextHolder source, SpanWrapper spanWrapper);

  void createTransaction(String eventId, ComponentLocation componentLocation);
}
