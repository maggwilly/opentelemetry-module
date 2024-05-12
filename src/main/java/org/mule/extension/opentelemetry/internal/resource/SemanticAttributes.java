package org.mule.extension.opentelemetry.internal.resource;

import io.opentelemetry.api.common.AttributeKey;

/**
 * Defines the attribute keys to be used when capturing mule related span
 * attributes.
 */
public final class SemanticAttributes {
  private SemanticAttributes() {
  }

  /**
   * Absolute path to mule installation.
   */
  public static final AttributeKey<String> MULE_HOME = AttributeKey.stringKey("mule.home");

  /**
   * Mule Correlation Id for the current event.
   */
  public static final AttributeKey<String> MULE_CORRELATION_ID = AttributeKey.stringKey("mule.correlationId");

  /**
   * Mule Server Id that is processing current request.
   */
  public static final AttributeKey<String> MULE_SERVER_ID = AttributeKey.stringKey("mule.serverId");
  public static final AttributeKey<String> MULE_CSORGANIZATION_ID = AttributeKey.stringKey("mule.csOrganization.id");

  /**
   * Most of the Mule users are familiar with organization id instead of
   * CSORGANIZATION ID.
   */
  public static final AttributeKey<String> MULE_ORGANIZATION_ID = AttributeKey.stringKey("mule.organization.id");

  /**
   * Mule Environment ID. See <a src=
   * "https://help.mulesoft.com/s/article/CloudHub-Reserved-Properties">CloudHub-Reserved-Properties</a>.
   */
  public static final AttributeKey<String> MULE_ENVIRONMENT_ID = AttributeKey.stringKey("mule.environment.id");

  /**
   * Mule Environment Type - eg. sandbox or production. See <a src=
   * "https://help.mulesoft.com/s/article/CloudHub-Reserved-Properties">CloudHub-Reserved-Properties</a>.
   */
  public static final AttributeKey<String> MULE_ENVIRONMENT_TYPE = AttributeKey.stringKey("mule.environment.type");

  /**
   * AWS Region in which Application is deployed in. See <a src=
   * "https://help.mulesoft.com/s/article/CloudHub-Reserved-Properties">CloudHub-Reserved-Properties</a>.
   */
  public static final AttributeKey<String> MULE_ENVIRONMENT_AWS_REGION = AttributeKey
      .stringKey("mule.environment.awsRegion");

  /**
   * Mule CloudHub Worker id that is processing current request. See <a src=
   * "https://help.mulesoft.com/s/article/CloudHub-Reserved-Properties">CloudHub-Reserved-Properties</a>.
   */
  public static final AttributeKey<String> MULE_WORKER_ID = AttributeKey.stringKey("mule.worker.id");

}
