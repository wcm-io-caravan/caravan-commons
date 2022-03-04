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

import java.net.URI;

import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.osgi.annotation.versioning.ProviderType;

/**
 * Interface for getting a pre-configured {@link HttpClient} instance.
 */
@ProviderType
public interface HttpClientFactory {

  /**
   * Returns a configured synchronous Http Client for the given target URL. If a special configuration
   * (e.g. timeout setting, proxy server, authentication) is configured it is applied in the factory.
   * @param targetUrl Target URL to call (this url is not called, but required to check for configuration)
   * @return Http Client
   */
  HttpClient get(String targetUrl);

  /**
   * Returns a configured synchronous Http Client for the given target URL. If a special configuration
   * (e.g. timeout setting, proxy server, authentication) is configured it is applied in the factory.
   * @param targetUrl Target URL to call (this url is not called, but required to check for configuration)
   * @return Http Client
   */
  CloseableHttpClient getCloseable(String targetUrl);

  /**
   * Returns a configured synchronous Http Client for the given target URL. If a special configuration
   * (e.g. timeout setting, proxy server, authentication) is configured it is applied in the factory.
   * @param targetUrl Target URL to call (this url is not called, but required to check for configuration)
   * @return Http Client
   */
  HttpClient get(URI targetUrl);

  /**
   * Returns a configured synchronous Http Client for the given target URL. If a special configuration
   * (e.g. timeout setting, proxy server, authentication) is configured it is applied in the factory.
   * @param targetUrl Target URL to call (this url is not called, but required to check for configuration)
   * @return Http Client
   */
  CloseableHttpClient getCloseable(URI targetUrl);

  /**
   * Returns a configured synchronous Http Client for the given target URL. The Http Client is dedicated
   * for SOAP access and supports filtering specific configuration via the "WS Addressing To" URI.
   * If a special configuration (e.g. timeout setting, proxy server, authentication) is configured it is
   * applied in the factory.
   * @param targetUrl Target URL to call (this url is not called, but required to check for configuration)
   * @param wsAddressingToUri WS Addressing "To" header
   * @return Http Client
   */
  HttpClient getWs(String targetUrl, String wsAddressingToUri);

  /**
   * Returns a configured synchronous Http Client for the given target URL. The Http Client is dedicated
   * for SOAP access and supports filtering specific configuration via the "WS Addressing To" URI.
   * If a special configuration (e.g. timeout setting, proxy server, authentication) is configured it is
   * applied in the factory.
   * @param targetUrl Target URL to call (this url is not called, but required to check for configuration)
   * @param wsAddressingToUri WS Addressing "To" header
   * @return Http Client
   */
  CloseableHttpClient getCloseableWs(String targetUrl, String wsAddressingToUri);

  /**
   * Returns a configured synchronous Http Client for the given target URL. The Http Client is dedicated
   * for SOAP access and supports filtering specific configuration via the "WS Addressing To" URI.
   * If a special configuration (e.g. timeout setting, proxy server, authentication) is configured it is
   * applied in the factory.
   * @param targetUrl Target URL to call (this url is not called, but required to check for configuration)
   * @param wsAddressingToUri WS Addressing "To" header
   * @return Http Client
   */
  HttpClient getWs(URI targetUrl, URI wsAddressingToUri);

  /**
   * Returns a configured synchronous Http Client for the given target URL. The Http Client is dedicated
   * for SOAP access and supports filtering specific configuration via the "WS Addressing To" URI.
   * If a special configuration (e.g. timeout setting, proxy server, authentication) is configured it is
   * applied in the factory.
   * @param targetUrl Target URL to call (this url is not called, but required to check for configuration)
   * @param wsAddressingToUri WS Addressing "To" header
   * @return Http Client
   */
  CloseableHttpClient getCloseableWs(URI targetUrl, URI wsAddressingToUri);

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
