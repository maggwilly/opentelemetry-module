package org.mule.extension.opentelemetry.internal.exporter.metric;

import io.opentelemetry.sdk.metrics.SdkMeterProvider;
import io.opentelemetry.sdk.metrics.SdkMeterProviderBuilder;
import io.opentelemetry.sdk.metrics.export.PeriodicMetricReader;
import org.mule.extension.opentelemetry.internal.ExporterInitialisationException;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

public class DynatraceMetricExporter implements MetricExporter {
    private static final Logger LOGGER = LoggerFactory.getLogger(DynatraceMetricExporter.class);
    @Parameter
    private String url;
    @Parameter
    private String apiToken;

    @Parameter
    @Optional(defaultValue = "10")
    private int interval;

    @Override
    public SdkMeterProviderBuilder createMeterProviderBuilder() throws ExporterInitialisationException {
        org.mule.extension.opentelemetry.internal.exporter.metric.dynatrace.DynatraceMetricExporter dynatraceMetricExporter = org.mule.extension.opentelemetry.internal.exporter.metric.dynatrace.DynatraceMetricExporter.builder().setEnrichWithOneAgentMetaData(true).setUrl(toUrl()).setApiToken(apiToken).build();
        return SdkMeterProvider.builder().registerMetricReader(PeriodicMetricReader.builder(dynatraceMetricExporter)
                .setInterval(interval, TimeUnit.SECONDS).build());
    }

    private URL toUrl(){
        try {
            return new URL(url);
        } catch (MalformedURLException e) {
            LOGGER.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
