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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListMap;

import org.apache.commons.lang3.StringUtils;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.ReferencePolicy;
import org.apache.felix.scr.annotations.Service;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.nio.client.HttpAsyncClient;
import org.apache.sling.commons.osgi.ServiceUtil;
import org.osgi.framework.BundleContext;

import io.wcm.caravan.commons.httpasyncclient.HttpAsyncClientFactory;
import io.wcm.caravan.commons.httpclient.HttpClientConfig;
import io.wcm.caravan.commons.httpclient.impl.helpers.DefaultHttpClientConfig;

/**
 * Default implementation of {@link HttpAsyncClientFactory}.
 */
@Component(immediate = true)
@Service(HttpAsyncClientFactory.class)
public class HttpAsyncClientFactoryImpl implements HttpAsyncClientFactory {

  @Reference(name = "httpClientConfig", referenceInterface = HttpClientConfig.class,
      cardinality = ReferenceCardinality.OPTIONAL_MULTIPLE, policy = ReferencePolicy.DYNAMIC)
  private final ConcurrentMap<Comparable<Object>, HttpAsyncClientItem> factoryItems = new ConcurrentSkipListMap<>();

  private HttpAsyncClientItem defaultFactoryItem;

  @Activate
  private void activate(BundleContext context) {
    defaultFactoryItem = new HttpAsyncClientItem(DefaultHttpClientConfig.INSTANCE);
  }

  @Deactivate
  private void deactivate() {
    for (HttpAsyncClientItem item : factoryItems.values()) {
      item.close();
    }
    factoryItems.clear();
    defaultFactoryItem.close();
    defaultFactoryItem = null;
  }

  protected void bindHttpClientConfig(HttpClientConfig httpClientConfig, Map<String, Object> config) {
    factoryItems.put(ServiceUtil.getComparableForServiceRanking(config), new HttpAsyncClientItem(httpClientConfig));
  }

  protected void unbindHttpClientConfig(HttpClientConfig httpClientConfig, Map<String, Object> config) {
    HttpAsyncClientItem removed = factoryItems.remove(ServiceUtil.getComparableForServiceRanking(config));
    if (removed != null) {
      removed.close();
    }
  }

  @Override
  public HttpAsyncClient get(String targetUrl) {
    return getCloseable(targetUrl);
  }

  @Override
  public CloseableHttpAsyncClient getCloseable(String targetUrl) {
    final URI uri = toUri(targetUrl);
    final String path = uri != null ? uri.getPath() : null;
    return getFactoryItem(uri, null, path, false).getHttpAsyncClient();
  }

  @Override
  public HttpAsyncClient get(URI targetUrl) {
    return getCloseable(targetUrl);
  }

  @Override
  public CloseableHttpAsyncClient getCloseable(URI targetUrl) {
    return getFactoryItem(targetUrl, null, targetUrl.getPath(), false).getHttpAsyncClient();
  }

  @Override
  public HttpAsyncClient getWs(String targetUrl, String wsAddressingToUri) {
    return getCloseableWs(targetUrl, wsAddressingToUri);
  }

  @Override
  public CloseableHttpAsyncClient getCloseableWs(String targetUrl, String wsAddressingToUri) {
    final URI uri = toUri(targetUrl);
    final String path = uri != null ? uri.getPath() : null;
    return getFactoryItem(uri, wsAddressingToUri, path, true).getHttpAsyncClient();
  }

  @Override
  public HttpAsyncClient getWs(URI targetUrl, URI wsAddressingToUri) {
    return getCloseableWs(targetUrl, wsAddressingToUri);
  }

  @Override
  public CloseableHttpAsyncClient getCloseableWs(URI targetUrl, URI wsAddressingToUri) {
    return getFactoryItem(targetUrl, wsAddressingToUri.toString(), targetUrl.getPath(), true).getHttpAsyncClient();
  }

  private HttpAsyncClientItem getFactoryItem(URI targetUrl, String wsAddressingToUri, String path, boolean isWsCall) {
    for (HttpAsyncClientItem item : factoryItems.values()) {
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
