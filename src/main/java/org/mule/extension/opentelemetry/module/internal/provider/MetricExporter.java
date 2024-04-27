package org.mule.extension.opentelemetry.module.internal.provider;

import org.mule.extension.opentelemetry.module.internal.ExporterInitialisationException;

import io.opentelemetry.sdk.metrics.SdkMeterProviderBuilder;

public interface MetricExporter {
	SdkMeterProviderBuilder createMeterProviderBuilder() throws ExporterInitialisationException;
}
