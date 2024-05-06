package org.mule.extension.opentelemetry.util;

import io.opentelemetry.api.trace.SpanKind;
import org.mule.extension.opentelemetry.trace.FlowSpan;
import org.mule.extension.opentelemetry.trace.SpanWrapper;
import org.mule.runtime.api.component.location.ComponentLocation;
import org.mule.runtime.api.event.Event;
import org.mule.runtime.extension.api.runtime.parameter.CorrelationInfo;

public class OplUtils {

	public static String createHistogramName(String serviceName) {
		return String.format("%s-%s",serviceName, "histogram");	
	}
	public static String getEventTransactionId(Event event) {
		// For child contexts, the primary id is appended with "_{timeInMillis}".
		// We remove time part to get a unique id across the event processing.
		return event.getContext().getId().split("_")[0];
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
