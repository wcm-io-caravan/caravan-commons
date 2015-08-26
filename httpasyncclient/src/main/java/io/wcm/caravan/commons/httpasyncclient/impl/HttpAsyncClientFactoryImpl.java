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

import io.wcm.caravan.commons.httpasyncclient.HttpAsyncClientFactory;
import io.wcm.caravan.commons.httpclient.HttpClientConfig;
import io.wcm.caravan.commons.httpclient.impl.helpers.DefaultHttpClientConfig;

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
import org.apache.http.nio.client.HttpAsyncClient;
import org.apache.sling.commons.osgi.ServiceUtil;
import org.osgi.framework.BundleContext;

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
    return getFactoryItem(toUri(targetUrl), null).getHttpAsyncClient();
  }

  @Override
  public HttpAsyncClient get(URI targetUrl) {
    return getFactoryItem(targetUrl, null).getHttpAsyncClient();
  }

  @Override
  public HttpAsyncClient getWs(String targetUrl, String wsAddressingToUri) {
    return getFactoryItem(toUri(targetUrl), wsAddressingToUri).getHttpAsyncClient();
  }

  @Override
  public HttpAsyncClient getWs(URI targetUrl, URI wsAddressingToUri) {
    return getFactoryItem(targetUrl, wsAddressingToUri.toString()).getHttpAsyncClient();
  }

  private HttpAsyncClientItem getFactoryItem(URI targetUrl, String wsAddressingToUri) {
    for (HttpAsyncClientItem item : factoryItems.values()) {
      if (item.matches(targetUrl.getHost(), wsAddressingToUri)) {
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
