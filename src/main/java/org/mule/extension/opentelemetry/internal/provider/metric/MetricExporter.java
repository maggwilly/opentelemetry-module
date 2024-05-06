package org.mule.extension.opentelemetry.internal.provider.metric;

import org.mule.extension.opentelemetry.internal.ExporterInitialisationException;

import io.opentelemetry.sdk.metrics.SdkMeterProviderBuilder;

public interface MetricExporter {
	SdkMeterProviderBuilder createMeterProviderBuilder() throws ExporterInitialisationException;
}
