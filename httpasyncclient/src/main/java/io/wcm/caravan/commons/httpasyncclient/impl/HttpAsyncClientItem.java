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
package io.wcm.caravan.commons.httpasyncclient.impl;

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
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.ProxyAuthenticationStrategy;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager;
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.apache.http.nio.conn.NoopIOSessionStrategy;
import org.apache.http.nio.conn.SchemeIOSessionStrategy;
import org.apache.http.nio.conn.ssl.SSLIOSessionStrategy;
import org.apache.http.nio.reactor.ConnectingIOReactor;
import org.apache.http.nio.reactor.IOReactorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.wcm.caravan.commons.httpclient.HttpClientConfig;
import io.wcm.caravan.commons.httpclient.impl.helpers.CertificateLoader;

/**
 * Item for {@link HttpAsyncClientFactoryImpl} for each {@link HttpClientConfig} configured.
 */
class HttpAsyncClientItem {

  private final HttpClientConfig config;
  private final PoolingNHttpClientConnectionManager asyncConnectionManager;
  private final CloseableHttpAsyncClient httpAsyncClient;

  private static final Logger log = LoggerFactory.getLogger(HttpAsyncClientItem.class);

  /**
   * @param config Http client configuration
   */
  HttpAsyncClientItem(HttpClientConfig config) {
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
    asyncConnectionManager = buildAsyncConnectionManager(config, sslContext);
    httpAsyncClient = buildHttpAsyncClient(config, asyncConnectionManager, credentialsProvider);

    // start async client
    httpAsyncClient.start();
  }

  private static PoolingNHttpClientConnectionManager buildAsyncConnectionManager(HttpClientConfig config,
      SSLContext sslContext) {
    // scheme configuration
    SchemeIOSessionStrategy sslSocketFactory = new SSLIOSessionStrategy(sslContext);
    Registry<SchemeIOSessionStrategy> asyncSchemeRegistry = RegistryBuilder.<SchemeIOSessionStrategy>create()
        .register("http", NoopIOSessionStrategy.INSTANCE)
        .register("https", sslSocketFactory)
        .build();

    // pooling settings
    ConnectingIOReactor ioreactor;
    try {
      ioreactor = new DefaultConnectingIOReactor(IOReactorConfig.DEFAULT);
    }
    catch (IOReactorException ex) {
      throw new RuntimeException("Unable to initialize IO reactor.", ex);
    }
    PoolingNHttpClientConnectionManager conmgr = new PoolingNHttpClientConnectionManager(ioreactor, asyncSchemeRegistry);
    conmgr.setMaxTotal(config.getMaxTotalConnections());
    conmgr.setDefaultMaxPerRoute(config.getMaxConnectionsPerHost());
    return conmgr;
  }

  private static CloseableHttpAsyncClient buildHttpAsyncClient(HttpClientConfig config,
      PoolingNHttpClientConnectionManager connectionManager, CredentialsProvider credentialsProvider) {

    // prepare HTTPClient builder
    HttpAsyncClientBuilder httpClientAsyncBuilder = HttpAsyncClientBuilder.create()
        .setConnectionManager(connectionManager);

    // timeout settings
    httpClientAsyncBuilder.setDefaultRequestConfig(RequestConfig.custom()
        .setConnectTimeout(config.getConnectTimeout())
        .setSocketTimeout(config.getSocketTimeout()).build());

    httpClientAsyncBuilder.setDefaultCredentialsProvider(credentialsProvider);

    // optional proxy support
    if (StringUtils.isNotEmpty(config.getProxyHost())) {
      httpClientAsyncBuilder.setProxy(new HttpHost(config.getProxyHost(), config.getProxyPort()));

      // optional proxy authentication
      if (StringUtils.isNotEmpty(config.getProxyUser())) {
        httpClientAsyncBuilder.setProxyAuthenticationStrategy(new ProxyAuthenticationStrategy());
      }
    }

    return httpClientAsyncBuilder.build();
  }

  /**
   * @return Http client instance (asynchronous)
   */
  public CloseableHttpAsyncClient getHttpAsyncClient() {
    return httpAsyncClient;
  }

  /**
   * @param hostName Host name
   * @param wsAddressingToURI WS addressing "to" URI
   * @return true if host name is associated with this http client config
   */
  public boolean matches(String hostName, String wsAddressingToURI) {
    return config.isEnabled()
        && config.matchesHost(hostName)
        && config.matchesWsAddressingToUri(wsAddressingToURI);
  }

  /**
   * Close underlying http clients.
   */
  public void close() {
    try {
      httpAsyncClient.close();
    }
    catch (IOException ex) {
      log.warn("Error closing async HTTP client.", ex);
    }
  }

  @Override
  public String toString() {
    return "HttpClientItem[" + config.toString() + "]";
  }

}
