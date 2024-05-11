package org.mule.extension.opentelemetry.internal.exporter.metric;

import io.opentelemetry.exporter.prometheus.PrometheusHttpServer;
import io.opentelemetry.sdk.metrics.SdkMeterProvider;
import io.opentelemetry.sdk.metrics.SdkMeterProviderBuilder;
import org.mule.extension.opentelemetry.internal.ExporterInitialisationException;
import org.mule.extension.opentelemetry.internal.OpenTelemetryExtension;
import org.mule.runtime.extension.api.annotation.dsl.xml.TypeDsl;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@TypeDsl()
public class PrometheusMetricExporter implements MetricExporter {
	private final Logger LOGGER = LoggerFactory.getLogger(PrometheusMetricExporter.class);

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
	PrometheusHttpServer httpServer = PrometheusHttpServer.builder().setPort(serverPort).build();
	return SdkMeterProvider.builder().registerMetricReader(httpServer);
	}catch (Exception e) {
		LOGGER.error("Failed to create Prometheus server {}", serverPort, e);
		throw new ExporterInitialisationException(e);
	} 
}

@Override
public String toString() {
	return "PrometheusExporter [serverPort=" + serverPort + "]";
}
  
}
