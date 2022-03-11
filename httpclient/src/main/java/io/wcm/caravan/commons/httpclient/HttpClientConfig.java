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
package io.wcm.caravan.commons.httpclient;

import org.apache.http.client.config.CookieSpecs;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.osgi.annotation.versioning.ConsumerType;

/**
 * HTTP Client configuration.
 */
@ConsumerType
public interface HttpClientConfig {

  /**
   * Default value for connection request timeout (in ms)
   */
  int CONNECTION_REQUEST_TIMEOUT_DEFAULT = 15000; // 15sec

  /**
   * Default value for connection timeout (in ms)
   */
  int CONNECT_TIMEOUT_DEFAULT = 15000; // 15sec

  /**
   * Default value for response timeout (in ms)
   */
  int SOCKET_TIMEOUT_DEFAULT = 60000; // 60sec

  /**
   * Default value for default maximum connections per host
   */
  int MAX_CONNECTIONS_PER_HOST_DEFAULT = 50;

  /**
   * Default value for maximum total connections
   */
  int MAX_TOTAL_CONNECTIONS_DEFAULT = 50;

  /**
   * Default value for cookie specs.
   */
  String COOKIE_SPEC_DEFAULT = CookieSpecs.STANDARD;

  /**
   * Configuration enabled.
   * @return true if configuration is enabled.
   */
  boolean isEnabled();

  /**
   * Connection request timeout in ms.
   * @return Connection request timeout in ms.
   */
  default int getConnectionRequestTimeout() {
    return CONNECTION_REQUEST_TIMEOUT_DEFAULT;
  }

  /**
   * Connection timeout in ms.
   * @return Connection timeout in ms.
   */
  int getConnectTimeout();

  /**
   * Response timeout in ms.
   * @return Response timeout in ms.
   */
  int getSocketTimeout();

  /**
   * Maximal HTTP connections per host.
   * @return Maximal HTTP connections per host.
   */
  int getMaxConnectionsPerHost();

  /**
   * Maximal total HTTP connections.
   * @return Maximal total HTTP connections.
   */
  int getMaxTotalConnections();

  /**
   * Standard cookie specification for HttpClient.
   * @return Cookie spec
   */
  @NotNull
  default String getCookieSpec() {
    return HttpClientConfig.COOKIE_SPEC_DEFAULT;
  }

  /**
   * Http basic authentication user (optional).
   * @return Http user or null.
   */
  @Nullable
  String getHttpUser();

  /**
   * Http basic authentication password (optional).
   * @return Http password or null.
   */
  @Nullable
  String getHttpPassword();

  /**
   * Proxy host (optional).
   * @return Proxy host or null.
   */
  @Nullable
  String getProxyHost();

  /**
   * Proxy port (optional).
   * @return Proxy port or 0.
   */
  int getProxyPort();

  /**
   * Proxy user (optional).
   * @return Proxy user or null.
   */
  @Nullable
  String getProxyUser();

  /**
   * Proxy password (optional).
   * @return Proxy password or null.
   */
  @Nullable
  String getProxyPassword();

  /**
   * Check if this configuration should be applied to a given host name.
   * @param host Host name
   * @return true if configuration matches.
   */
  boolean matchesHost(@Nullable String host);

  /**
   * Check if this configuration should be applied for a given WS addressing to URI.
   * @param addressingToUri Web service address
   * @return true if configuration matches.
   */
  boolean matchesWsAddressingToUri(@Nullable String addressingToUri);

  /**
   * Check if this configuration should be applied for a given path of the target URL
   * @param path Path part
   * @return true if configuration matches
   */
  default boolean matchesPath(@Nullable String path) {
    return true;
  }

  /**
   * @return SSL context type (default: TLS)
   */
  @NotNull
  String getSslContextType();

  /**
   * @return Key manager type (default: SunX509)
   */
  @NotNull
  String getKeyManagerType();

  /**
   * @return Key store type (default: PKCS12)
   */
  @NotNull
  String getKeyStoreType();

  /**
   * @return Key store provider (default: null = use first matching security provider)
   */
  @Nullable
  default String getKeyStoreProvider() {
    return null;
  }

  /**
   * @return Key store file path
   */
  @Nullable
  String getKeyStorePath();

  /**
   * @return Key store password
   */
  @Nullable
  String getKeyStorePassword();

  /**
   * @return Trust manager type (default: SunX509)
   */
  @NotNull
  String getTrustManagerType();

  /**
   * @return Trust store type (default: JKS)
   */
  @NotNull
  String getTrustStoreType();

  /**
   * @return Trust store provider (default: null = use first matching security provider)
   */
  @Nullable
  default String getTrustStoreProvider() {
    return null;
  }

  /**
   * @return Trust store file path
   */
  @Nullable
  String getTrustStorePath();

  /**
   * @return Trust store password
   */
  @Nullable
  String getTrustStorePassword();

}
