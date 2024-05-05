package org.mule.extension.opentelemetry.module.utils;

import io.opentelemetry.api.common.AttributeKey;

public class OplConstants {
    public static String TRACE_CONTEXT_MAP_KEY = "OTEL_TRACE_CONTEXT";

    public static final AttributeKey<String> ERROR_MESSAGE = AttributeKey.stringKey("error.message");
}
