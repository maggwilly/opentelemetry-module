package org.mule.extension.opentelemetry.util;

import io.opentelemetry.api.trace.SpanKind;
import org.mule.extension.opentelemetry.trace.FlowSpan;
import org.mule.extension.opentelemetry.trace.SpanWrapper;
import org.mule.runtime.api.component.location.ComponentLocation;

public class OplUtils {

	public static String createCounterName(String name, String ... prefix) {
		String pref = String.join(".", prefix);
		return String.format("%s.%s.%s",pref, name.toLowerCase(), "counter");
	}
	public static String createTransactionId(String eventId, ComponentLocation componentLocation) {
		String rootContainerName = componentLocation.getRootContainerName();
		return OplUtils.getParentTransactionId(eventId) + OplConstants.ID_BRIDGE + rootContainerName;
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
