package org.mule.extension.opentelemetry.module.internal;

import static org.mule.runtime.extension.api.annotation.param.MediaType.ANY;

import java.util.Map;

import javax.inject.Inject;

import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.context.Context;
import org.mule.extension.opentelemetry.module.internal.singleton.MetricCollector;
import org.mule.extension.opentelemetry.module.trace.DefaultContextMapGetter;
import org.mule.extension.opentelemetry.module.trace.Trace;
import org.mule.extension.opentelemetry.module.internal.provider.TransactionManager;
import org.mule.runtime.api.meta.ExpressionSupport;
import org.mule.runtime.api.meta.model.operation.ExecutionType;
import org.mule.runtime.extension.api.annotation.Expression;
import org.mule.runtime.extension.api.annotation.execution.Execution;
import org.mule.runtime.extension.api.annotation.param.Content;
import org.mule.runtime.extension.api.annotation.param.MediaType;
import org.mule.runtime.extension.api.runtime.parameter.CorrelationInfo;


public class OpenTelemetryOperations {
	@Inject
	private MetricCollector metricCollector;

	@Inject
	private TransactionManager transactionManager;
	@Execution(ExecutionType.CPU_LITE)
	@MediaType(value = ANY, strict = false)
	public void addToMetric(@Content Map<String, Object> values, long duration) {
		metricCollector.observe(values, duration);
	}

	@Execution(ExecutionType.CPU_LITE)
	@MediaType(value = ANY, strict = false)
	public void startTransaction(@Expression(value = ExpressionSupport.NOT_SUPPORTED) TraceContextPropagator context, CorrelationInfo correlationInfo) {
		Context traceContext = transactionManager.getTraceContext(context.getAttributes(), DefaultContextMapGetter.INSTANCE);
		Trace trace = new Trace("").setTransactionId(correlationInfo.getEventId()).setContext(traceContext).setTags(context.getAttributes()).setSpanKind(SpanKind.SERVER);
		transactionManager.openTransaction(trace);
	}

}
