package org.mule.extension.opentelemetry.internal.http;

import org.mule.extension.http.api.request.authentication.HttpRequestAuthentication;
import org.mule.runtime.api.exception.MuleException;
import org.mule.runtime.api.lifecycle.Startable;
import org.mule.runtime.api.lifecycle.Stoppable;
import org.mule.runtime.core.api.lifecycle.LifecycleUtils;
import org.mule.runtime.http.api.client.auth.HttpAuthentication;
import org.mule.runtime.http.api.domain.message.request.HttpRequest;
import org.mule.runtime.http.api.domain.message.response.HttpResponse;

import java.util.concurrent.CompletableFuture;

public class HttpConnection implements Startable, Stoppable {

    private final HttpRequesterConnectionManager.ShareableHttpClient httpClient;
    private final HttpRequestAuthentication authentication;

    private final String url;

    public HttpConnection(HttpRequesterConnectionManager.ShareableHttpClient httpClient,  HttpRequestAuthentication authentication, String url) {
        this.httpClient = httpClient;
        this.authentication = authentication;
        this.url = url;
    }
    public HttpRequestAuthentication getDefaultAuthentication() {
        return this.authentication;
    }
    @Override
    public void start() throws MuleException {
        this.httpClient.start();
        try {
            LifecycleUtils.startIfNeeded(this.authentication);
        } catch (Exception var2) {
            this.httpClient.stop();
            throw var2;
        }
    }

    @Override
    public void stop() throws MuleException {
        LifecycleUtils.stopIfNeeded(this.authentication);
        this.httpClient.stop();
    }

    public String getUrl() {
        return url;
    }

    public CompletableFuture<HttpResponse> send(HttpRequest request, int responseTimeout, boolean followRedirects, HttpAuthentication authentication) {
        return this.httpClient.sendAsync(request, responseTimeout, followRedirects, authentication);
    }
}
