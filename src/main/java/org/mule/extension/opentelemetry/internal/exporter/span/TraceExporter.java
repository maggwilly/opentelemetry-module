package org.mule.extension.opentelemetry.internal.exporter.span;

import io.opentelemetry.sdk.metrics.SdkMeterProvider;
import io.opentelemetry.sdk.trace.SdkTracerProviderBuilder;
import org.mule.extension.opentelemetry.internal.ExporterInitialisationException;

public interface TraceExporter {
	SdkTracerProviderBuilder createSdkTracerProviderBuilder(SdkMeterProvider meterProvider) throws ExporterInitialisationException;
}
