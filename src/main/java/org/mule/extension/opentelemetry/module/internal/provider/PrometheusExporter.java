package org.mule.extension.opentelemetry.module.internal.provider;

import org.mule.extension.opentelemetry.module.internal.ExporterInitialisationException;
import org.mule.runtime.extension.api.annotation.dsl.xml.TypeDsl;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;

import io.opentelemetry.exporter.prometheus.PrometheusHttpServer;
import io.opentelemetry.sdk.metrics.SdkMeterProvider;
import io.opentelemetry.sdk.metrics.SdkMeterProviderBuilder;

@TypeDsl(allowTopLevelDefinition = false, allowInlineDefinition = true)
public class PrometheusExporter implements MetricExporter {
	
   @Parameter
   @DisplayName("Server port")
   @Optional(defaultValue = "${glb.globalProperties.metric.prometheus.port}")
  private int serverPort;

public int getServerPort() {
	return serverPort;
}

public void setServerPort(int serverPort) {
	this.serverPort = serverPort;
}

@Override
public SdkMeterProviderBuilder createMeterProviderBuilder(){
	try {
	PrometheusHttpServer build = PrometheusHttpServer.builder().setPort(serverPort).build();
	return SdkMeterProvider.builder().registerMetricReader(build);
	}catch (Exception e) {
		throw new ExporterInitialisationException(e);
	} 
}

@Override
public String toString() {
	return "PrometheusExporter [serverPort=" + serverPort + "]";
}
  
}
