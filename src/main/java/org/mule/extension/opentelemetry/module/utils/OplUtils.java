package org.mule.extension.opentelemetry.module.utils;

import org.mule.runtime.api.event.Event;

public class OplUtils {

	public static String createHistogramName(String serviceName) {
		return String.format("%s-%s",serviceName, "histogram");	
	}
	public static String getEventTransactionId(Event event) {
		// For child contexts, the primary id is appended with "_{timeInMillis}".
		// We remove time part to get a unique id across the event processing.
		return event.getContext().getId().split("_")[0];
	}
}
