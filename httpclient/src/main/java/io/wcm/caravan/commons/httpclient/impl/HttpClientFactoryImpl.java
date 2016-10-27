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
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.ReferencePolicy;
import org.apache.felix.scr.annotations.Service;
import org.apache.http.client.HttpClient;
import org.apache.sling.commons.osgi.ServiceUtil;
import org.osgi.framework.BundleContext;

import io.wcm.caravan.commons.httpclient.HttpClientConfig;
import io.wcm.caravan.commons.httpclient.HttpClientFactory;
import io.wcm.caravan.commons.httpclient.impl.helpers.DefaultHttpClientConfig;

/**
 * Default implementation of {@link HttpClientFactory}.
 */
@Component(immediate = true)
@Service(HttpClientFactory.class)
public class HttpClientFactoryImpl implements HttpClientFactory {

  @Reference(name = "httpClientConfig", referenceInterface = HttpClientConfig.class,
      cardinality = ReferenceCardinality.OPTIONAL_MULTIPLE, policy = ReferencePolicy.DYNAMIC)
  private final ConcurrentMap<Comparable<Object>, HttpClientItem> factoryItems = new ConcurrentSkipListMap<>();

  private HttpClientItem defaultFactoryItem;

  @Activate
  private void activate(BundleContext context) {
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

  protected void unbindHttpClientConfig(HttpClientConfig httpClientConfig, Map<String, Object> config) {
    HttpClientItem removed = factoryItems.remove(ServiceUtil.getComparableForServiceRanking(config));
    if (removed != null) {
      removed.close();
    }
  }

  @Override
  public HttpClient get(String targetUrl) {
    return getFactoryItem(toUri(targetUrl), null, null, false).getHttpClient();
  }

  @Override
  public HttpClient get(final String targetUrl, final String resourcePath) {
    return getFactoryItem(toUri(targetUrl), null, resourcePath, false).getHttpClient();
  }

  @Override
  public HttpClient get(URI targetUrl) {
    return getFactoryItem(targetUrl, null, targetUrl.getPath(), false).getHttpClient();
  }

  @Override
  public HttpClient getWs(String targetUrl, String wsAddressingToUri) {
    return getFactoryItem(toUri(targetUrl), wsAddressingToUri, null, true).getHttpClient();
  }

  @Override
  public HttpClient getWs(URI targetUrl, URI wsAddressingToUri) {
    return getFactoryItem(targetUrl, wsAddressingToUri.toString(), null, true).getHttpClient();
  }

  private HttpClientItem getFactoryItem(URI targetUrl, String wsAddressingToUri, String resourcePath, boolean isWsCall) {
    for (HttpClientItem item : factoryItems.values()) {
      if (item.matches(targetUrl.getHost(), wsAddressingToUri, resourcePath, isWsCall)) {
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
