package org.mule.extension.opentelemetry.module.internal.singleton;

import java.util.Map;
import javax.inject.Inject;

import org.mule.extension.opentelemetry.module.internal.OpenTelemetryConfiguration;
import org.mule.extension.opentelemetry.module.internal.OplInitialisable;
import org.mule.extension.opentelemetry.module.internal.OplUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.common.AttributesBuilder;
import io.opentelemetry.api.metrics.LongHistogram;
import io.opentelemetry.api.metrics.Meter;

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
		OpenTelemetry openTelemetry = openTelemetryProvider.getOpenTelemetry();
		return openTelemetry.meterBuilder("instrumentation-" + configuration.getServiceName())
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
