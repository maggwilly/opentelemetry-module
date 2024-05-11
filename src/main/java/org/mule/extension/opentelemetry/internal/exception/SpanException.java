package org.mule.extension.opentelemetry.internal.exception;

import org.mule.runtime.api.exception.MuleException;
import org.mule.runtime.api.i18n.I18nMessage;

public class SpanException extends MuleException {
    public SpanException(I18nMessage message) {
        super(message);
    }
}
