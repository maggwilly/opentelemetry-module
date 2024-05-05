package org.mule.extension.opentelemetry.module.internal.provider.span;

import io.opentelemetry.sdk.metrics.SdkMeterProvider;
import io.opentelemetry.sdk.trace.SdkTracerProviderBuilder;
import org.mule.extension.opentelemetry.module.internal.ExporterInitialisationException;

public interface SpanExporter {
	SdkTracerProviderBuilder createSdkTracerProviderBuilder(SdkMeterProvider meterProvider) throws ExporterInitialisationException;
}
