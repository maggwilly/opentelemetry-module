package org.mule.extension.opentelemetry.util;

import io.opentelemetry.api.common.AttributeKey;

public class OplConstants {
    public static String TRACE_CONTEXT_MAP_KEY = "OPLTracingContext";
    public static final String ID_BRIDGE = "::";
    public static final AttributeKey<String> ERROR_MESSAGE = AttributeKey.stringKey("mule.error.message");
}
