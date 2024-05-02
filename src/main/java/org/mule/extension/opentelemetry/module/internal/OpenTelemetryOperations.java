package org.mule.extension.opentelemetry.module.internal;

import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.propagation.TextMapGetter;
import io.opentelemetry.context.propagation.TextMapSetter;
import org.mule.extension.opentelemetry.module.internal.config.TracingConfig;
import org.mule.extension.opentelemetry.module.internal.provider.TracingManager;
import org.mule.extension.opentelemetry.module.internal.singleton.MapGetterFactory;
import org.mule.extension.opentelemetry.module.internal.singleton.MapSetterFactory;
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

	private final Factory<TraceContextPropagator, TextMapGetter<Map<String, String>>> getterFactory = new MapGetterFactory();
	private final Factory<TraceContextPropagator, TextMapSetter<Map<String, String>>> setterFactory = new MapSetterFactory();
	@Execution(ExecutionType.CPU_LITE)
	@MediaType(value = ANY, strict = false)
	public void addToMetric(@Content Map<String, Object> values, long duration) {
		metricCollector.observe(values, duration);
	}



	@Execution(ExecutionType.CPU_LITE)
	@MediaType(value = ANY, strict = false)
	public void startTrace(@Expression(value = ExpressionSupport.NOT_SUPPORTED)  @Config OpenTelemetryConfiguration configuration, String name, CorrelationInfo correlationInfo, ComponentLocation componentLocation) {
		TracingConfig tracingConfig = configuration.getTracingConfig();
		TraceContextPropagator contextPropagator = tracingConfig.getContextPropagator();
		TextMapGetter<Map<String, String>> mapTextMapGetter = getterFactory.create(contextPropagator);
		Context traceContext = tracingManager.getTraceContext(contextPropagator.getAttributes(), mapTextMapGetter);
		Trace trace = new Trace(name).setComponentLocation(componentLocation).setTransactionId(correlationInfo.getEventId()).setContext(traceContext).setTags(contextPropagator.getAttributes()).setSpanKind(SpanKind.SERVER);
		tracingManager.openTransaction(trace);
	}


}
