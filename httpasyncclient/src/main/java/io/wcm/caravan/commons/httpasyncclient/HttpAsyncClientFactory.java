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
package io.wcm.caravan.commons.httpasyncclient;

import java.net.URI;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.nio.client.HttpAsyncClient;
import org.osgi.annotation.versioning.ProviderType;

/**
 * Interface for getting a pre-configured {@link HttpAsyncClient} instance.
 */
@ProviderType
public interface HttpAsyncClientFactory {

  /**
   * Returns a configured asynchronous Http Client for the given target URL. If a special configuration
   * (e.g. timeout setting, proxy server, authentication) is configured it is applied in the factory.
   * @param targetUrl Target URL to call (this url is not called, but required to check for configuration)
   * @return Http Client
   */
  HttpAsyncClient get(String targetUrl);

  /**
   * Returns a configured asynchronous Http Client for the given target URL. If a special configuration
   * (e.g. timeout setting, proxy server, authentication) is configured it is applied in the factory.
   * @param targetUrl Target URL to call (this url is not called, but required to check for configuration)
   * @return Http Client
   */
  CloseableHttpAsyncClient getCloseable(String targetUrl);

  /**
   * Returns a configured asynchronous Http Client for the given target URL. If a special configuration
   * (e.g. timeout setting, proxy server, authentication) is configured it is applied in the factory.
   * @param targetUrl Target URL to call (this url is not called, but required to check for configuration)
   * @return Http Client
   */
  HttpAsyncClient get(URI targetUrl);

  /**
   * Returns a configured asynchronous Http Client for the given target URL. If a special configuration
   * (e.g. timeout setting, proxy server, authentication) is configured it is applied in the factory.
   * @param targetUrl Target URL to call (this url is not called, but required to check for configuration)
   * @return Http Client
   */
  CloseableHttpAsyncClient getCloseable(URI targetUrl);

  /**
   * Returns a configured asynchronous Http Client for the given target URL. The Http Client is dedicated
   * for SOAP access and supports filtering specific configuration via the "WS Addressing To" URI.
   * If a special configuration (e.g. timeout setting, proxy server, authentication) is configured it is
   * applied in the factory.
   * @param targetUrl Target URL to call (this url is not called, but required to check for configuration)
   * @param wsAddressingToUri WS Addressing "To" header
   * @return Http Client
   */
  HttpAsyncClient getWs(String targetUrl, String wsAddressingToUri);

  /**
   * Returns a configured asynchronous Http Client for the given target URL. The Http Client is dedicated
   * for SOAP access and supports filtering specific configuration via the "WS Addressing To" URI.
   * If a special configuration (e.g. timeout setting, proxy server, authentication) is configured it is
   * applied in the factory.
   * @param targetUrl Target URL to call (this url is not called, but required to check for configuration)
   * @param wsAddressingToUri WS Addressing "To" header
   * @return Http Client
   */
  CloseableHttpAsyncClient getCloseableWs(String targetUrl, String wsAddressingToUri);

  /**
   * Returns a configured asynchronous Http Client for the given target URL. The Http Client is dedicated
   * for SOAP access and supports filtering specific configuration via the "WS Addressing To" URI.
   * If a special configuration (e.g. timeout setting, proxy server, authentication) is configured it is
   * applied in the factory.
   * @param targetUrl Target URL to call (this url is not called, but required to check for configuration)
   * @param wsAddressingToUri WS Addressing "To" header
   * @return Http Client
   */
  HttpAsyncClient getWs(URI targetUrl, URI wsAddressingToUri);

  /**
   * Returns a configured asynchronous Http Client for the given target URL. The Http Client is dedicated
   * for SOAP access and supports filtering specific configuration via the "WS Addressing To" URI.
   * If a special configuration (e.g. timeout setting, proxy server, authentication) is configured it is
   * applied in the factory.
   * @param targetUrl Target URL to call (this url is not called, but required to check for configuration)
   * @param wsAddressingToUri WS Addressing "To" header
   * @return Http Client
   */
  CloseableHttpAsyncClient getCloseableWs(URI targetUrl, URI wsAddressingToUri);

  /**
   * Returns the default Request Configuration for the given target URL. If a special configuration
   * (e.g. timeout setting, proxy server, authentication) is configured it is applied in the factory.
   * @param targetUrl Target URL to call (this url is not called, but required to check for configuration)
   * @return Default Request Config
   */
  RequestConfig getDefaultRequestConfig(String targetUrl);

  /**
   * Returns the default Request Configuration for the given target URL. If a special configuration
   * (e.g. timeout setting, proxy server, authentication) is configured it is applied in the factory.
   * @param targetUrl Target URL to call (this url is not called, but required to check for configuration)
   * @return Default Request Config
   */
  RequestConfig getDefaultRequestConfig(URI targetUrl);

}
