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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListMap;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.sling.commons.osgi.ServiceUtil;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

import io.wcm.caravan.commons.httpclient.HttpClientConfig;
import io.wcm.caravan.commons.httpclient.HttpClientFactory;
import io.wcm.caravan.commons.httpclient.impl.helpers.DefaultHttpClientConfig;

/**
 * Default implementation of {@link HttpClientFactory}.
 */
@Component(service = HttpClientFactory.class, immediate = true, reference = {
    @Reference(name = "httpClientConfig", service = HttpClientConfig.class,
        cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC,
        bind = "bindHttpClientConfig", unbind = "unbindHttpClientConfig")
})
public class HttpClientFactoryImpl implements HttpClientFactory {

  private final ConcurrentMap<Comparable<Object>, HttpClientItem> factoryItems = new ConcurrentSkipListMap<>();

  private HttpClientItem defaultFactoryItem;

  @Activate
  private void activate() {
    defaultFactoryItem = new HttpClientItem(DefaultHttpClientConfig.INSTANCE);
  }

  @Deactivate
  private void deactivate() {
    for (HttpClientItem item : factoryItems.values()) {
      item.close();
    }
    factoryItems.clear();
    defaultFactoryItem.close();
    defaultFactoryItem = null;
  }

  protected void bindHttpClientConfig(HttpClientConfig httpClientConfig, Map<String, Object> config) {
    factoryItems.put(ServiceUtil.getComparableForServiceRanking(config), new HttpClientItem(httpClientConfig));
  }

  @SuppressWarnings("unused")
  protected void unbindHttpClientConfig(HttpClientConfig httpClientConfig, Map<String, Object> config) {
    HttpClientItem removed = factoryItems.remove(ServiceUtil.getComparableForServiceRanking(config));
    if (removed != null) {
      removed.close();
    }
  }

  @Override
  public CloseableHttpClient get(String targetUrl) {
    return getCloseable(targetUrl);
  }

  @Override
  public CloseableHttpClient getCloseable(String targetUrl) {
    final URI uri = toUri(targetUrl);
    final String path = uri != null ? uri.getPath() : null;
    return getFactoryItem(uri, null, path, false).getHttpClient();
  }

  @Override
  public HttpClient get(URI targetUrl) {
    return getCloseable(targetUrl);
  }

  @Override
  public CloseableHttpClient getCloseable(URI targetUrl) {
    return getFactoryItem(targetUrl, null, targetUrl.getPath(), false).getHttpClient();
  }

  @Override
  public HttpClient getWs(String targetUrl, String wsAddressingToUri) {
    return getCloseableWs(targetUrl, wsAddressingToUri);
  }

  @Override
  public CloseableHttpClient getCloseableWs(String targetUrl, String wsAddressingToUri) {
    final URI uri = toUri(targetUrl);
    final String path = uri != null ? uri.getPath() : null;
    return getFactoryItem(uri, wsAddressingToUri, path, true).getHttpClient();
  }

  @Override
  public HttpClient getWs(URI targetUrl, URI wsAddressingToUri) {
    return getCloseableWs(targetUrl, wsAddressingToUri);
  }

  @Override
  public CloseableHttpClient getCloseableWs(URI targetUrl, URI wsAddressingToUri) {
    return getFactoryItem(targetUrl, wsAddressingToUri.toString(), targetUrl.getPath(), true).getHttpClient();
  }

  private HttpClientItem getFactoryItem(URI targetUrl, String wsAddressingToUri, String path, boolean isWsCall) {
    for (HttpClientItem item : factoryItems.values()) {
      if (item.matches(targetUrl.getHost(), wsAddressingToUri, path, isWsCall)) {
        return item;
      }
    }
    return defaultFactoryItem;
  }

  private URI toUri(String uri) {
    if (StringUtils.isEmpty(uri)) {
      return null;
    }
    else {
      try {
        return new URI(uri);
      }
      catch (URISyntaxException ex) {
        throw new IllegalArgumentException("Invalid URI: " + ex.getMessage(), ex);
      }
    }
  }

}
