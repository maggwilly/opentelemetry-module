package org.mule.extension.opentelemetry.module.internal;

import javax.inject.Inject;

import org.mule.extension.opentelemetry.module.internal.provider.MetricExporter;
import org.mule.extension.opentelemetry.module.internal.singleton.MetricCollector;
import org.mule.extension.opentelemetry.module.internal.singleton.OpenTelemetryProvider;
import org.mule.runtime.api.lifecycle.InitialisationException;
import org.mule.runtime.api.lifecycle.Startable;
import org.mule.runtime.extension.api.annotation.Operations;
import org.mule.runtime.extension.api.annotation.connectivity.ConnectionProviders;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.RefName;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;
import org.mule.runtime.extension.api.annotation.param.display.Placement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



@Operations(OpenTelemetryOperations.class)
@ConnectionProviders(OpenTelemetryConnectionProvider.class)
public class OpenTelemetryConfiguration implements Startable{
	private final Logger LOGGER = LoggerFactory.getLogger("monitoring.opentelemetry.logger");

	@Inject
	private OpenTelemetryProvider openTelemetryProvider;
	
	@Inject
	private MetricCollector metricCollector;
	
     @RefName
     private String configName;
  	  
	  @DisplayName("Service Name")
	  @Parameter
	  private String serviceName;
	
	  @DisplayName("Service Version")
	  @Parameter
	  private String serviceVersion;
	
	  @Parameter
	  @Placement(tab = "Metric")
	  private MetricExporter metricExporter;

	public String getConfigName() {
		return configName;
	}


	public void setConfigName(String configName) {
		this.configName = configName;
	}



	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}


	public void setServiceVersion(String serviceVersion) {
		this.serviceVersion = serviceVersion;
	}


	public MetricExporter getMetricExporter() {
		return metricExporter;
	}


	public void setMetricExporter(MetricExporter metricExporter) {
		this.metricExporter = metricExporter;
	}


	@Override
	public void start() throws InitialisationException {
		try {
		openTelemetryProvider.initialise(this);
		metricCollector.initialise(this);
		}catch (Exception e) {
			LOGGER.error("Failed to initialize the opentelemetry module: {} {}",e.getMessage(),this);
		}
	}

	public String getServiceName() {
		return this.serviceName;
	}

	public String getServiceVersion() {
		return this.serviceVersion;
	}


	@Override
	public String toString() {
		return "OpentelemetrymoduleConfiguration [configName=" + configName + ", serviceName=" + serviceName
				+ ", serviceVersion=" + serviceVersion + ", metricExporter=" + metricExporter + "]";
	}

	
}
