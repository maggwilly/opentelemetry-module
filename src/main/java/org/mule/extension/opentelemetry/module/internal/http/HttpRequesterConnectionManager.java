
package org.mule.extension.opentelemetry.module.internal.http;

import static java.lang.String.format;
import static java.util.Optional.ofNullable;
import static org.mule.runtime.api.util.Preconditions.checkArgument;

import org.mule.runtime.api.lifecycle.Disposable;
import org.mule.runtime.http.api.HttpService;
import org.mule.runtime.http.api.client.HttpClientConfiguration;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import javax.inject.Inject;


public class HttpRequesterConnectionManager implements Disposable {

  @Inject
  private HttpService httpService;

  private Map<String, ShareableHttpClient> clients = new HashMap<>();

  public HttpRequesterConnectionManager() {}

  public HttpRequesterConnectionManager(HttpService httpService) {
    this.httpService = httpService;
  }


  @Deprecated
  public Optional<ShareableHttpClient> lookup(String configName) {
    return ofNullable(clients.get(configName));
  }


  @Deprecated
  public synchronized ShareableHttpClient create(String configName, HttpClientConfiguration clientConfiguration) {
    checkArgument(!clients.containsKey(configName), format("There's an HttpClient available for %s already.", configName));
    ShareableHttpClient client = new ShareableHttpClient(httpService.getClientFactory().create(clientConfiguration));
    clients.put(configName, client);
    return client;
  }


  public synchronized ShareableHttpClient lookupOrCreate(String configName,
                                                         Supplier<? extends HttpClientConfiguration> configSupplier) {
    return clients.computeIfAbsent(configName,
                                   name -> new ShareableHttpClient(httpService.getClientFactory().create(configSupplier.get())));
  }

  @Override
  public void dispose() {
    clients.clear();
  }


  public void disposeClient(String configName) {
    clients.remove(configName);
  }


}
