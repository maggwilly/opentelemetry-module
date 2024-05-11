package org.mule.extension.opentelemetry.internal.exception;

import org.mule.runtime.api.exception.MuleException;
import org.mule.runtime.api.i18n.I18nMessage;

public class ParentContextException extends MuleException {
    public ParentContextException(I18nMessage cause) {
        super(cause);
    }
}
