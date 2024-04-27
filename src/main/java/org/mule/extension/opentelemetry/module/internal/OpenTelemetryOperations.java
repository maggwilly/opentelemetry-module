package org.mule.extension.opentelemetry.module.internal;

import static org.mule.runtime.extension.api.annotation.param.MediaType.ANY;

import java.util.Map;

import javax.inject.Inject;

import org.mule.extension.opentelemetry.module.internal.singleton.MetricCollector;
import org.mule.runtime.api.meta.model.operation.ExecutionType;
import org.mule.runtime.extension.api.annotation.execution.Execution;
import org.mule.runtime.extension.api.annotation.param.Content;
import org.mule.runtime.extension.api.annotation.param.MediaType;


public class OpenTelemetryOperations {
	@Inject
	private MetricCollector metricCollector;

	@Execution(ExecutionType.CPU_LITE)
	@MediaType(value = ANY, strict = false)
	public void addToMetric(@Content Map<String, Object> values, long duration) {
		metricCollector.observe(values, duration);
	}
}
