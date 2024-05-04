package org.mule.extension.opentelemetry.module.internal;

import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.context.propagation.TextMapGetter;
import io.opentelemetry.context.propagation.TextMapSetter;
import org.mule.extension.opentelemetry.module.internal.provider.TracingManager;
import org.mule.extension.opentelemetry.module.internal.singleton.MapGetterFactory;
import org.mule.extension.opentelemetry.module.internal.singleton.MapSetterFactory;
import org.mule.extension.opentelemetry.module.internal.singleton.MetricCollector;
import org.mule.extension.opentelemetry.module.trace.FlowSpan;
import org.mule.extension.opentelemetry.module.api.SpanContextHolder;
import org.mule.extension.opentelemetry.module.trace.SpanWrapper;
import org.mule.runtime.api.component.location.ComponentLocation;
import org.mule.runtime.api.meta.model.operation.ExecutionType;
import org.mule.runtime.api.store.ObjectStoreManager;
import org.mule.runtime.extension.api.annotation.Expression;
import org.mule.runtime.extension.api.annotation.dsl.xml.ParameterDsl;
import org.mule.runtime.extension.api.annotation.execution.Execution;
import org.mule.runtime.extension.api.annotation.param.*;
import org.mule.runtime.extension.api.runtime.parameter.CorrelationInfo;

import javax.inject.Inject;
import java.util.Map;

import static org.mule.runtime.api.meta.ExpressionSupport.NOT_SUPPORTED;
import static org.mule.runtime.extension.api.annotation.param.MediaType.ANY;


public class OpenTelemetryOperations {
	@Inject
	private ObjectStoreManager objectStoreManager;
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
	public void createSpan(@ParameterGroup(name = "Span") FlowSpan span,
						   @Optional SpanContextHolder parent,
						   @Expression(value = NOT_SUPPORTED) @ParameterDsl(allowInlineDefinition = false)  @Config OpenTelemetryConfiguration configuration,
						   CorrelationInfo correlationInfo, ComponentLocation componentLocation) {
		SpanWrapper trace = getTrace(span,  correlationInfo, componentLocation);
		// tracingManager.openTransaction(trace,patent, configuration.getTracingConfig());
	}

	private SpanWrapper getTrace(FlowSpan span, CorrelationInfo correlationInfo, ComponentLocation componentLocation)  {
		return new SpanWrapper(span).setComponentLocation(componentLocation).setTransactionId(correlationInfo.getEventId()).setTags(span.getAttributes()).setSpanKind(SpanKind.SERVER);
	}



}
