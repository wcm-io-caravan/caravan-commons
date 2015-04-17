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
package io.wcm.caravan.commons.test.io;

import io.wcm.caravan.io.http.CaravanHttpClient;
import io.wcm.caravan.io.http.request.CaravanHttpRequest;
import io.wcm.caravan.io.http.response.CaravanHttpResponse;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import rx.Observable;

import com.google.common.collect.Sets;

/**
 * Depending on the host of the given request, decides which delegate to use.
 */
public class HostSplittingCaravanHttpClient implements CaravanHttpClient {

  private static final Logger LOG = LoggerFactory.getLogger(HostSplittingCaravanHttpClient.class);

  private final CaravanHttpClient delegateForHosts;
  private final CaravanHttpClient delegateOthers;
  private final Set<String> hosts;

  /**
   * @param delegateForHost
   * @param hosts
   */
  public HostSplittingCaravanHttpClient(final CaravanHttpClient delegateForHost, final CaravanHttpClient delegateOthers, final String... hosts) {
    this.delegateForHosts = delegateForHost;
    this.delegateOthers = delegateOthers;
    this.hosts = Sets.newHashSet(hosts);
  }

  @Override
  public Observable<CaravanHttpResponse> execute(final CaravanHttpRequest request) {
    return (isAllowed(request) ? delegateForHosts : delegateOthers).execute(request);
  }

  @Override
  public Observable<CaravanHttpResponse> execute(final CaravanHttpRequest request, final Observable<CaravanHttpResponse> fallback) {
    return (isAllowed(request) ? delegateForHosts : delegateOthers).execute(request, fallback);
  }

  @Override
  public boolean hasValidConfiguration(String serviceName) {
    return true;
  }

  private boolean isAllowed(final CaravanHttpRequest request) {
    try {
      String requestHost = new URL(request.url()).getHost();
      return hosts.contains(requestHost);
    }
    catch (MalformedURLException ex) {
      LOG.error("Can't extract host from request URL: " + request.url(), ex);
    }
    return false;
  }

}
