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

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.config.CookieSpecs;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.osgi.service.metatype.annotations.Option;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.wcm.caravan.commons.httpclient.HttpClientConfig;
import io.wcm.caravan.commons.httpclient.impl.helpers.AbstractHttpClientConfig;
import io.wcm.caravan.commons.httpclient.impl.helpers.CertificateLoader;

/**
 * Default implementation of {@link HttpClientConfig}.
 */
@Component(service = HttpClientConfig.class, immediate = true, configurationPolicy = ConfigurationPolicy.REQUIRE,
    property = "webconsole.configurationFactory.nameHint={hostPatterns} {wsAddressingToUris} {pathPatterns}")
@Designate(ocd = HttpClientConfigImpl.Config.class, factory = true)
public class HttpClientConfigImpl extends AbstractHttpClientConfig {

  @ObjectClassDefinition(name = "wcm.io Caravan HTTP Client Configuration",
      description = "Allows to configure special HTTP client settings for target hosts.")
  static @interface Config {

    /**
     * Host pattern
     */
    @AttributeDefinition(name = "Host pattern", description = "Regular expressions for matching the host name(s)")
    String[] hostPatterns();

    /**
     * WS Uri
     */
    @AttributeDefinition(name = "WS Uri", description = "List of WS Addressing To URIs for SOAP calls")
    String[] wsAddressingToUris();

    /**
     * Path pattern
     */
    @AttributeDefinition(name = "Path pattern", description = "Regular expressions for matching the path part of the target URLs")
    String[] pathPatterns();

    /**
     * Connection request timeout
     */
    @AttributeDefinition(name = "Connection request timeout", description = "Max. timeout to wait for getting a connection from the connection manager (ms)")
    int connectionRequestTimeout() default HttpClientConfig.CONNECTION_REQUEST_TIMEOUT_DEFAULT;

    /**
     * Connect timeout
     */
    @AttributeDefinition(name = "Connect timeout", description = "Max. timeout to wait for HTTP connection (ms)")
    int connectTimeout() default HttpClientConfig.CONNECT_TIMEOUT_DEFAULT;

    /**
     * Socket timeout
     */
    @AttributeDefinition(name = "Socket timeout", description = "Max. timeout to wait for a HTTP response (ms)")
    int socketTimeout() default HttpClientConfig.SOCKET_TIMEOUT_DEFAULT;

    /**
     * Max per host
     */
    @AttributeDefinition(name = "Max per host", description = "Max connections per host")
    int maxConnectionsPerHost() default HttpClientConfig.MAX_CONNECTIONS_PER_HOST_DEFAULT;

    /**
     * Max total
     */
    @AttributeDefinition(name = "Max total", description = "Max total connections")
    int maxTotalConnections() default HttpClientConfig.MAX_TOTAL_CONNECTIONS_DEFAULT;

    /**
     * Cookie Specs
     */
    @AttributeDefinition(name = "Cookie Spec", description = "Standard cookie specification for HttpClient. "
        + "See https://www.javadoc.io/static/org.apache.httpcomponents/httpclient/4.3.4/org/apache/http/client/config/CookieSpecs.html",
        options = {
            @Option(value = CookieSpecs.BROWSER_COMPATIBILITY, label = "BROWSER_COMPATIBILITY"),
            @Option(value = CookieSpecs.NETSCAPE, label = "NETSCAPE"),
            @Option(value = CookieSpecs.STANDARD, label = "STANDARD"),
            @Option(value = CookieSpecs.BEST_MATCH, label = "BEST_MATCH"),
            @Option(value = CookieSpecs.IGNORE_COOKIES, label = "IGNORE_COOKIES")
        })
    String cookieSpec() default HttpClientConfig.COOKIE_SPEC_DEFAULT;

    /**
     * Http user
     */
    @AttributeDefinition(name = "Http user", description = "User name for basic HTTP authentication")
    String httpUser();

    /**
     * Http password
     */
    @AttributeDefinition(name = "Http password", description = "Password for basic HTTP authentication")
    String httpPassword();

    /**
     * Proxy host
     */
    @AttributeDefinition(name = "Proxy host", description = "Proxy hostname")
    String proxyHost();

    /**
     * Proxy port
     */
    @AttributeDefinition(name = "Proxy port", description = "Proxy port")
    int proxyPort();

    /**
     * Proxy user
     */
    @AttributeDefinition(name = "Proxy user", description = "Proxy user name")
    String proxyUser();

    /**
     * Proxy password
     */
    @AttributeDefinition(name = "Proxy password", description = "Proxy password")
    String proxyPassword();

    /**
     * SSL context type
     */
    @AttributeDefinition(name = "SSL context type", description = "SSL context type")
    String sslContextType() default CertificateLoader.SSL_CONTEXT_TYPE_DEFAULT;

    /**
     * KeyManager type
     */
    @AttributeDefinition(name = "KeyManager type", description = "KeyManager type")
    String keyManagerType() default CertificateLoader.KEY_MANAGER_TYPE_DEFAULT;

    /**
     * KeyStore type
     */
    @AttributeDefinition(name = "KeyStore type", description = "KeyStore type")
    String keyStoreType() default CertificateLoader.KEY_STORE_TYPE_DEFAULT;

    /**
     * KeyStore provider
     */
    @AttributeDefinition(name = "KeyStore provider", description = "KeyStore provider. If not set the first matching security provider is used.")
    String keyStoreProvider();

    /**
     * KeyStore path
     */
    @AttributeDefinition(name = "KeyStore path", description = "KeyStore path")
    String keyStorePath();

    /**
     * KeyStore password
     */
    @AttributeDefinition(name = "KeyStore password", description = "KeyStore password")
    String keyStorePassword();

    /**
     * TrustManager type
     */
    @AttributeDefinition(name = "TrustManager type", description = "TrustManager type")
    String trustManagerType() default CertificateLoader.TRUST_MANAGER_TYPE_DEFAULT;

    /**
     * TrustStore type
     */
    @AttributeDefinition(name = "TrustStore type", description = "TrustStore type")
    String trustStoreType() default CertificateLoader.TRUST_STORE_TYPE_DEFAULT;

    /**
     * TrustStore provider
     */
    @AttributeDefinition(name = "TrustStore provider", description = "TrustStore provider. If not set the first matching security provider is used.")
    String trustStoreProvider();

    /**
     * TrustStore path
     */
    @AttributeDefinition(name = "TrustStore path", description = "TrustStore path")
    String trustStorePath();

    /**
     * TrustStore password
     */
    @AttributeDefinition(name = "TrustStore password", description = "TrustStore password")
    String trustStorePassword();

    /**
     * Enabled
     */
    @AttributeDefinition(name = "Enabled", description = "Enable this HTTP client configuration")
    boolean enabled() default true;

    /**
     * Service Ranking.
     */
    @AttributeDefinition(name = "Service Ranking",
        description = "Allows to define an order in which the HTTP client configurations are evaluated. Lower value = higher ranking.")
    @SuppressWarnings("java:S100") // property name is service.ranking
    int service_ranking();

  }


  private boolean enabled;

  private int connectionRequestTimeout;
  private int connectTimeout;
  private int socketTimeout;
  private int maxConnectionsPerHost;
  private int maxTotalConnections;
  private String cookieSpec;
  private String httpUser;
  private String httpPassword;
  private String proxyHost;
  private int proxyPort;
  private String proxyUser;
  private String proxyPassword;
  private Set<Pattern> hostPatterns;
  private Set<String> wsAddressingToUris;
  private Set<Pattern> pathPatterns;

  private String sslContextType;
  private String keyManagerType;
  private String keyStoreType;
  private String keyStoreProvider;
  private String keyStorePath;
  private String keyStorePassword;
  private String trustManagerType;
  private String trustStoreType;
  private String trustStoreProvider;
  private String trustStorePath;
  private String trustStorePassword;

  private static final Logger log = LoggerFactory.getLogger(HttpClientConfigImpl.class);

  @Activate
  private void activate(Config config) {
    enabled = config.enabled();

    connectionRequestTimeout = config.connectionRequestTimeout();
    connectTimeout = config.connectTimeout();
    socketTimeout = config.socketTimeout();
    maxConnectionsPerHost = config.maxConnectionsPerHost();
    maxTotalConnections = config.maxTotalConnections();
    cookieSpec = config.cookieSpec();
    httpUser = config.httpUser();
    httpPassword = config.httpPassword();
    proxyHost = config.proxyHost();
    proxyPort = config.proxyPort();
    proxyUser = config.proxyUser();
    proxyPassword = config.proxyPassword();

    hostPatterns = new HashSet<>();
    String[] hostPatternsArray = config.hostPatterns();
    for (String hostPatternString : hostPatternsArray) {
      if (StringUtils.isNotBlank(hostPatternString)) {
        try {
          hostPatterns.add(Pattern.compile(hostPatternString));
        }
        catch (PatternSyntaxException ex) {
          log.warn("Invalid host name pattern '" + hostPatternString + "': " + ex.getMessage(), ex);
          this.enabled = false;
        }
      }
    }

    wsAddressingToUris = new HashSet<>();
    String[] wsAddressingToUrisArray = config.wsAddressingToUris();
    for (String wsAddressingToUriString : wsAddressingToUrisArray) {
      if (StringUtils.isNotBlank(wsAddressingToUriString)) {
        wsAddressingToUris.add(wsAddressingToUriString);
      }
    }

    pathPatterns = new HashSet<>();
    String[] pathPatternsArray = config.pathPatterns();
    for (String pathPatternString : pathPatternsArray) {
      if (StringUtils.isNotBlank(pathPatternString)) {
        try {
          pathPatterns.add(Pattern.compile(pathPatternString));
        }
        catch (PatternSyntaxException ex) {
          log.warn("Invalid path pattern '" + pathPatternString + "': " + ex.getMessage(), ex);
          this.enabled = false;
        }
      }
    }

    sslContextType = config.sslContextType();
    keyManagerType = config.keyManagerType();
    keyStoreType = config.keyStoreType();
    keyStoreProvider = config.keyStoreProvider();
    keyStorePath = config.keyStorePath();
    keyStorePassword = config.keyStorePassword();
    trustManagerType = config.trustManagerType();
    trustStoreType = config.trustStoreType();
    trustStoreProvider = config.trustStoreProvider();
    trustStorePath = config.trustStorePath();
    trustStorePassword = config.trustStorePassword();
  }

  @Override
  public boolean isEnabled() {
    return this.enabled;
  }

  @Override
  public int getConnectionRequestTimeout() {
    return connectionRequestTimeout;
  }

  @Override
  public int getConnectTimeout() {
    return connectTimeout;
  }

  @Override
  public int getSocketTimeout() {
    return socketTimeout;
  }

  @Override
  public int getMaxConnectionsPerHost() {
    return maxConnectionsPerHost;
  }

  @Override
  public int getMaxTotalConnections() {
    return maxTotalConnections;
  }

  @Override
  public @NotNull String getCookieSpec() {
    return cookieSpec;
  }

  @Override
  public @Nullable String getHttpUser() {
    return httpUser;
  }

  @Override
  public @Nullable String getHttpPassword() {
    return httpPassword;
  }

  @Override
  public @Nullable String getProxyHost() {
    return proxyHost;
  }

  @Override
  public int getProxyPort() {
    return proxyPort;
  }

  @Override
  public @Nullable String getProxyUser() {
    return proxyUser;
  }

  @Override
  public @Nullable String getProxyPassword() {
    return proxyPassword;
  }

  @Override
  public boolean matchesHost(@Nullable String host) {
    if (hostPatterns.isEmpty()) {
      return true;
    }
    if (StringUtils.isEmpty(host)) {
      return false;
    }
    for (Pattern hostPattern : hostPatterns) {
      if (hostPattern.matcher(host).matches()) {
        return true;
      }
    }
    return false;
  }

  @Override
  public boolean matchesWsAddressingToUri(@Nullable String addressingToUri) {
    if (wsAddressingToUris.isEmpty()) {
      return true;
    }
    if (StringUtils.isEmpty(addressingToUri)) {
      return false;
    }
    return wsAddressingToUris.contains(addressingToUri);
  }

  @Override
  public boolean matchesPath(@Nullable String path) {
    if (pathPatterns.isEmpty()) {
      return true;
    }
    if (StringUtils.isEmpty(path)) {
      return false;
    }
    for (Pattern pathPattern : pathPatterns) {
      if (pathPattern.matcher(path).matches()) {
        return true;
      }
    }
    return false;
  }

  @Override
  public @NotNull String getSslContextType() {
    return sslContextType;
  }

  @Override
  public @NotNull String getKeyManagerType() {
    return keyManagerType;
  }

  @Override
  public @NotNull String getKeyStoreType() {
    return keyStoreType;
  }

  @Override
  public @Nullable String getKeyStoreProvider() {
    return keyStoreProvider;
  }

  @Override
  public @Nullable String getKeyStorePath() {
    return keyStorePath;
  }

  @Override
  public @Nullable String getKeyStorePassword() {
    return keyStorePassword;
  }

  @Override
  public @NotNull String getTrustManagerType() {
    return trustManagerType;
  }

  @Override
  public @NotNull String getTrustStoreType() {
    return trustStoreType;
  }

  @Override
  public @Nullable String getTrustStoreProvider() {
    return trustStoreProvider;
  }

  @Override
  public @Nullable String getTrustStorePath() {
    return trustStorePath;
  }

  @Override
  public @Nullable String getTrustStorePassword() {
    return trustStorePassword;
  }

}
