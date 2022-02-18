/*
 * #%L
 * wcm.io
 * %%
 * Copyright (C) 2014 wcm.io
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package io.wcm.caravan.commons.httpclient.impl;

import java.io.IOException;
import java.security.GeneralSecurityException;

import javax.net.ssl.SSLContext;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.ProxyAuthenticationStrategy;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.wcm.caravan.commons.httpclient.HttpClientConfig;
import io.wcm.caravan.commons.httpclient.impl.helpers.CertificateLoader;

/**
 * Item for {@link HttpClientFactoryImpl} for each {@link HttpClientConfig} configured.
 */
class HttpClientItem {

  private final HttpClientConfig config;
  private final PoolingHttpClientConnectionManager connectionManager;
  private final CloseableHttpClient httpClient;

  private static final Logger log = LoggerFactory.getLogger(HttpClientItem.class);

  /**
   * @param config Http client configuration
   */
  HttpClientItem(HttpClientConfig config) {
    this.config = config;

    // optional SSL client certificate support
    SSLContext sslContext;
    if (CertificateLoader.isSslKeyManagerEnabled(config) || CertificateLoader.isSslTrustStoreEnbaled(config)) {
      try {
        sslContext = CertificateLoader.buildSSLContext(config);
      }
      catch (IOException | GeneralSecurityException ex) {
        throw new IllegalArgumentException("Invalid SSL client certificate configuration.", ex);
      }
    }
    else {
      sslContext = CertificateLoader.createDefaultSSlContext();
    }

    CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
    // optional proxy authentication
    if (StringUtils.isNotEmpty(config.getProxyUser())) {
      credentialsProvider.setCredentials(new AuthScope(config.getProxyHost(), config.getProxyPort()),
          new UsernamePasswordCredentials(config.getProxyUser(), config.getProxyPassword()));
    }
    // optional http basic authentication support
    if (StringUtils.isNotEmpty(config.getHttpUser())) {
      credentialsProvider.setCredentials(AuthScope.ANY,
          new UsernamePasswordCredentials(config.getHttpUser(), config.getHttpPassword()));
    }

    // build http clients
    connectionManager = buildConnectionManager(config, sslContext);
    httpClient = buildHttpClient(config, connectionManager, credentialsProvider);
  }

  private static PoolingHttpClientConnectionManager buildConnectionManager(HttpClientConfig config,
      SSLContext sslContext) {
    // scheme configuration
    ConnectionSocketFactory sslSocketFactory = new SSLConnectionSocketFactory(sslContext);
    Registry<ConnectionSocketFactory> schemeRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
        .register("http", PlainConnectionSocketFactory.getSocketFactory())
        .register("https", sslSocketFactory)
        .build();

    // pooling settings
    PoolingHttpClientConnectionManager conmgr = new PoolingHttpClientConnectionManager(schemeRegistry);
    conmgr.setMaxTotal(config.getMaxTotalConnections());
    conmgr.setDefaultMaxPerRoute(config.getMaxConnectionsPerHost());
    return conmgr;
  }

  private static CloseableHttpClient buildHttpClient(HttpClientConfig config,
      PoolingHttpClientConnectionManager connectionManager, CredentialsProvider credentialsProvider) {

    // prepare HTTPClient builder
    HttpClientBuilder httpClientBuilder = HttpClientBuilder.create()
        .setConnectionManager(connectionManager);

    httpClientBuilder.setDefaultRequestConfig(RequestConfig.custom()
        // timeout settings
        .setConnectionRequestTimeout(config.getConnectionRequestTimeout())
        .setConnectTimeout(config.getConnectTimeout())
        .setSocketTimeout(config.getSocketTimeout())
        .setCookieSpec(config.getCookieSpec())
        .build());

    httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);

    // optional proxy support
    if (StringUtils.isNotEmpty(config.getProxyHost())) {
      httpClientBuilder.setProxy(new HttpHost(config.getProxyHost(), config.getProxyPort()));

      // optional proxy authentication
      if (StringUtils.isNotEmpty(config.getProxyUser())) {
        httpClientBuilder.setProxyAuthenticationStrategy(new ProxyAuthenticationStrategy());
      }
    }

    return httpClientBuilder.build();
  }

  /**
   * @return Http client instance (synchronous)
   */
  public CloseableHttpClient getHttpClient() {
    return httpClient;
  }

  /**
   * @param hostName Host name
   * @param wsAddressingToURI WS addressing "to" URI
   * @param path Path part of URI
   * @param isWsCall indicates if the call is a soap webservice call
   * @return true if host name is associated with this http client config
   */
  public boolean matches(String hostName, String wsAddressingToURI, String path, boolean isWsCall) {
    if (isWsCall) {
      return config.isEnabled()
          && config.matchesHost(hostName)
          && config.matchesPath(path)
          && config.matchesWsAddressingToUri(wsAddressingToURI);
    }
    else {
      return config.isEnabled()
          && config.matchesHost(hostName)
          && config.matchesPath(path);
    }
  }

  /**
   * Close underlying http clients.
   */
  public void close() {
    try {
      httpClient.close();
    }
    catch (IOException ex) {
      log.warn("Error closing HTTP client.", ex);
    }
  }

  @Override
  public String toString() {
    return "HttpClientItem[" + config.toString() + "]";
  }

}
