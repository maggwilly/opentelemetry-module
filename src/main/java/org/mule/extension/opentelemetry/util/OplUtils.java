package org.mule.extension.opentelemetry.util;

import io.opentelemetry.api.trace.SpanKind;
import org.mule.extension.opentelemetry.trace.FlowSpan;
import org.mule.extension.opentelemetry.trace.SpanWrapper;
import org.mule.runtime.api.component.location.ComponentLocation;

public class OplUtils {

	public static String createHistogramName(String serviceName) {
		return String.format("%s-%s",serviceName, "histogram_business");
	}

	public static String getParentTransactionId(String eventId) {
		int lastIndex = eventId.lastIndexOf('_');
		if (lastIndex != -1) {
			return eventId.substring(0, lastIndex);
		} else {
			return eventId;
		}
	}

	public static SpanWrapper createSpan(FlowSpan span, String eventId, ComponentLocation componentLocation) {
		return new SpanWrapper(span)
				.setComponentLocation(componentLocation)
				.setEventId(eventId)
				.setSpanKind(SpanKind.SERVER);
	}
}
