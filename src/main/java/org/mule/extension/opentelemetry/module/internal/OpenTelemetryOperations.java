package org.mule.extension.opentelemetry.module.internal;

import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.context.Context;
import org.mule.extension.opentelemetry.module.internal.config.TracingConfig;
import org.mule.extension.opentelemetry.module.internal.provider.TracingManager;
import org.mule.extension.opentelemetry.module.internal.singleton.MetricCollector;
import org.mule.extension.opentelemetry.module.trace.Propagator;
import org.mule.extension.opentelemetry.module.trace.Trace;
import org.mule.runtime.api.component.location.ComponentLocation;
import org.mule.runtime.api.meta.ExpressionSupport;
import org.mule.runtime.api.meta.model.operation.ExecutionType;
import org.mule.runtime.extension.api.annotation.Expression;
import org.mule.runtime.extension.api.annotation.execution.Execution;
import org.mule.runtime.extension.api.annotation.param.Config;
import org.mule.runtime.extension.api.annotation.param.Content;
import org.mule.runtime.extension.api.annotation.param.MediaType;
import org.mule.runtime.extension.api.runtime.parameter.CorrelationInfo;

import javax.inject.Inject;
import java.util.Map;

import static org.mule.runtime.extension.api.annotation.param.MediaType.ANY;


public class OpenTelemetryOperations {
	@Inject
	private MetricCollector metricCollector;

	@Inject
	private TracingManager tracingManager;
	@Execution(ExecutionType.CPU_LITE)
	@MediaType(value = ANY, strict = false)
	public void addToMetric(@Content Map<String, Object> values, long duration) {
		metricCollector.observe(values, duration);
	}


/*
	@Execution(ExecutionType.CPU_LITE)
	@MediaType(value = ANY, strict = false)
	public void startTrace(@Expression(value = ExpressionSupport.NOT_SUPPORTED)  @Config OpenTelemetryConfiguration configuration, String name, CorrelationInfo correlationInfo, ComponentLocation componentLocation) {
		TracingConfig tracingConfig = configuration.getTracingConfig();
		TraceContextPropagator contextPropagator = tracingConfig.getContextPropagator();
		Propagator propagator = contextPropagator.getPropagator();
		Context traceContext = tracingManager.getTraceContext(null, propagator.getMapGetter());
		Trace trace = new Trace(name).setComponentLocation(componentLocation).setTransactionId(correlationInfo.getEventId()).setContext(traceContext).setTags(null).setSpanKind(SpanKind.SERVER);
		tracingManager.openTransaction(trace);
	}

*/

}
