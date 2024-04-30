package org.mule.extension.opentelemetry.module.internal.http;

import org.mule.runtime.http.api.client.HttpClient;
import org.mule.runtime.http.api.client.auth.HttpAuthentication;
import org.mule.runtime.http.api.domain.message.request.HttpRequest;
import org.mule.runtime.http.api.domain.message.response.HttpResponse;

import java.util.concurrent.CompletableFuture;


public class ShareableHttpClient {

  private final HttpClient delegate;
  private Integer usageCount = 0;

  public ShareableHttpClient(HttpClient client) {
    delegate = client;
  }

  public synchronized void start() {
    if (++usageCount == 1) {
      try {
        delegate.start();
      } catch (Exception e) {
        usageCount--;
        throw e;
      }
    }
  }

  public synchronized void stop() {
    if (--usageCount == 0) {
      delegate.stop();
    }
  }

  public CompletableFuture<HttpResponse> sendAsync(HttpRequest request, int responseTimeout, boolean followRedirects,
                                                   HttpAuthentication authentication,
                                                   HttpSendBodyMode sendBodyMode) {
    return HttpClientReflection.sendAsync(delegate, request, responseTimeout, followRedirects, authentication, sendBodyMode);
  }
}
