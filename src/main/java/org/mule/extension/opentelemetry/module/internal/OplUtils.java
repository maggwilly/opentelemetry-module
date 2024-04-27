package org.mule.extension.opentelemetry.module.internal;

public class OplUtils {

	public static String createHistogramName(String serviceName) {
		return String.format("%s-%s",serviceName, "histogram");	
	}
	
	public static String createCounterName(String serviceName) {
		return String.format("%s-%s",serviceName, "counter");	
	}
}
