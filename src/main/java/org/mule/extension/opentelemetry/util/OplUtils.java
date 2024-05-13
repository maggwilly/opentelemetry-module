package org.mule.extension.opentelemetry.util;

import io.opentelemetry.api.trace.SpanKind;
import org.mule.extension.opentelemetry.trace.FlowSpan;
import org.mule.extension.opentelemetry.trace.SpanWrapper;
import org.mule.runtime.api.component.location.ComponentLocation;

import java.util.Objects;

public class OplUtils {

	public static String createCounterName(String name, String ... prefix) {
		String pref = String.join(".", prefix);
		return String.format("%s.%s",pref, createName(name));
	}

	private static Object createName(String name) {
		CharSequence replace = replace(name, "\\\\", '_');
		CharSequence replace1 = replace(replace, ":", '.');
		CharSequence charSequence = Objects.requireNonNull(replace1);
		return charSequence.subSequence(0, Math.min(charSequence.length()-1,62));
	}

	public static CharSequence replace(CharSequence input, String regex, char replacement) {
		return Objects.requireNonNull(input).toString().replaceAll(regex, Character.toString(replacement));
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
