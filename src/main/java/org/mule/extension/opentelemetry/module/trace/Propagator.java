package org.mule.extension.opentelemetry.module.trace;

public interface Propagator {
    static enum PropagatorType{
        HTTP_REST
    }
    PropagatorType getType();
}
