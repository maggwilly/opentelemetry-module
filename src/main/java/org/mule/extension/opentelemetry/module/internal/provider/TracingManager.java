package org.mule.extension.opentelemetry.module.internal.provider;

import io.opentelemetry.context.Context;
import io.opentelemetry.context.propagation.TextMapGetter;
import io.opentelemetry.context.propagation.TextMapSetter;
import org.mule.extension.opentelemetry.module.internal.OplInitialisable;
import org.mule.extension.opentelemetry.module.internal.config.TracingConfig;
import org.mule.extension.opentelemetry.module.trace.SpanWrapper;
import org.mule.extension.opentelemetry.module.trace.Transaction;

import java.util.Optional;

public interface TracingManager extends OplInitialisable {
  void openTransaction(SpanWrapper spanWrapper,  TracingConfig tracingConfig);
  Optional<Transaction> closeTransaction(String transactionId);

  Optional<Transaction> closeTransaction(String transactionId, Exception exception);
  <T> Context getTraceContext(T carrier, TextMapGetter<T> textMapGetter);

  <T> void injectTraceContext(Context context, T carrier, TextMapSetter<T> textMapSetter);
}
