package org.mule.extension.opentelemetry.internal.resource;

import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.common.AttributesBuilder;
import io.opentelemetry.sdk.resources.Resource;

import static org.mule.extension.opentelemetry.internal.resource.SemanticAttributes.*;

public class MuleResource {
  public static Resource buildResource() {
    AttributesBuilder builder = Attributes.builder();
    addAttribute("mule.home", builder, MULE_HOME);
    addAttribute("csorganization.id", builder, MULE_CSORGANIZATION_ID);
    addAttribute("csorganization.id", builder, MULE_ORGANIZATION_ID);
    addAttribute("server.id", builder, MULE_SERVER_ID);
    addAttribute("environment.id", builder, MULE_ENVIRONMENT_ID);
    addAttribute("environment.type", builder, MULE_ENVIRONMENT_TYPE);
    addAttribute("worker.id", builder, MULE_WORKER_ID);
    addAttribute("application.aws.region", builder, MULE_ENVIRONMENT_AWS_REGION);
    Attributes build = builder.build();
    return Resource.create(build);
  }

  private static void addAttribute(String sysProperty, AttributesBuilder builder,
      AttributeKey<String> attributeKey) {
    String value = System.getProperty(sysProperty);
    if (value != null) {
      builder.put(attributeKey, value);
    }
  }
}
