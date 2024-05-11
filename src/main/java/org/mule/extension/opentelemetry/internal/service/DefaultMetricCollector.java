package org.mule.extension.opentelemetry.internal.service;

import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.common.AttributesBuilder;
import io.opentelemetry.api.metrics.LongHistogram;
import io.opentelemetry.api.metrics.Meter;
import io.opentelemetry.sdk.metrics.SdkMeterProvider;
import org.mule.extension.opentelemetry.util.OplUtils;
import org.mule.runtime.api.exception.MuleException;
import org.mule.runtime.api.lifecycle.Stoppable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class DefaultMetricCollector implements MetricCollector , Stoppable {
	private final Logger LOGGER = LoggerFactory.getLogger(DefaultMetricCollector.class);
	private final LongHistogram myHistogram;
	private final SdkMeterProvider meterProvider;

	public DefaultMetricCollector(String configName, SdkMeterProvider meterProvider) {
		this.meterProvider = meterProvider;
		Meter meter = createMeter(this.meterProvider, configName);
		String histogramName = OplUtils.createHistogramName(configName);
		this.myHistogram = createHistogram(meter, histogramName);
	}

	private Meter createMeter(SdkMeterProvider meterProvider, String name) {
		return meterProvider.meterBuilder(name)
				.setInstrumentationVersion("1.0.0").build();
	}

	@Override
	public void stop() throws MuleException {
		meterProvider.close();
	}

	private LongHistogram createHistogram(Meter meter, String histogramName) {
		return meter.histogramBuilder(histogramName).setDescription("Histogram of response time").setUnit("millis")
				.ofLongs().build();
	}

	public void observe(Map<String, Object> values, long duration) {
		LOGGER.trace("Adding metric {}",values);
		AttributesBuilder builder = Attributes.builder();
		values.forEach((key, u) -> builder.put(key, "" + u));
		myHistogram.record(duration, builder.build());
	}
}
