package org.mule.extension.opentelemetry.module.internal.provider;

import io.opentelemetry.context.Context;
import io.opentelemetry.context.propagation.TextMapGetter;
import io.opentelemetry.context.propagation.TextMapSetter;
import org.mule.extension.opentelemetry.module.internal.OplInitialisable;
import org.mule.extension.opentelemetry.module.internal.config.TracingConfig;
import org.mule.extension.opentelemetry.module.trace.SpanWrapper;

public interface TracingManager extends OplInitialisable {
  void openTransaction(SpanWrapper spanWrapper,  TracingConfig tracingConfig);
  void closeTransaction(SpanWrapper spanWrapper);

  <T> Context getTraceContext(T carrier, TextMapGetter<T> textMapGetter);

  <T> void injectTraceContext(Context context, T carrier, TextMapSetter<T> textMapSetter);
}
