package org.mule.extension.opentelemetry.internal.singleton;

import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.common.AttributesBuilder;
import io.opentelemetry.api.metrics.LongHistogram;
import io.opentelemetry.api.metrics.Meter;
import io.opentelemetry.sdk.metrics.SdkMeterProvider;
import org.mule.extension.opentelemetry.internal.OpenTelemetryConfiguration;
import org.mule.extension.opentelemetry.internal.OplInitialisable;
import org.mule.extension.opentelemetry.util.OplUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.Map;

public class MetricCollector implements OplInitialisable {
	private final Logger LOGGER = LoggerFactory.getLogger(MetricCollector.class);

	@Inject
	private OpenTelemetryProvider openTelemetryProvider;
	private LongHistogram myHistogram;


	@Override
	public void initialise(OpenTelemetryConfiguration configuration) {

		Meter meter = createMeter(configuration);

		String histogramName = OplUtils.createHistogramName(configuration.getServiceName());
		this.myHistogram = createHistogram(meter, histogramName);

	}


	private Meter createMeter(OpenTelemetryConfiguration configuration) {
		SdkMeterProvider openTelemetry = openTelemetryProvider.getMeterProvider();
		return openTelemetry.meterBuilder(configuration.getConfigName())
				.setInstrumentationVersion("1.0.0").build();
	}

	private LongHistogram createHistogram(Meter meter, String histogramName) {
		return meter.histogramBuilder(histogramName).setDescription("Histogram of response time").setUnit("millis")
				.ofLongs().build();
	}


	public void observe(Map<String, Object> values, long duration) {
		AttributesBuilder builder = Attributes.builder();
		values.forEach((key, u) -> builder.put(key, "" + u));
		myHistogram.record(duration, builder.build());
	}

}
