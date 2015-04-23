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

import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import rx.Observable;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Maps;

/**
 * Mocking implementation of {@link CaravanHttpClient} for tests. Use
 * mockRequest methods to register a response. Returns a 404 NOT FOUND
 * response if there is no response registered for the request.
 */
public class MockingCaravanHttpClient implements CaravanHttpClient {

  private static final Logger LOGGER = LoggerFactory.getLogger(MockingCaravanHttpClient.class);

  private static final CaravanHttpResponse NOT_FOUND = CaravanHttpResponse.create(HttpStatus.SC_NOT_FOUND, "Not Found", ImmutableMultimap.of(), new byte[0]);
  private final Map<String, CaravanHttpResponse> store = Maps.newConcurrentMap();
  private CaravanHttpResponse matchesAll;

  @Override
  public Observable<CaravanHttpResponse> execute(final CaravanHttpRequest request) {
    return execute(request, Observable.just(NOT_FOUND));
  }

  @Override
  public Observable<CaravanHttpResponse> execute(final CaravanHttpRequest request, final Observable<CaravanHttpResponse> fallback) {
    String url = request.getUrl();
    // search for equal URL
    if (store.containsKey(url)) {
      return Observable.just(store.get(url));
    }
    // search for URL starting with
    for (Entry<String, CaravanHttpResponse> entry : store.entrySet()) {
      if (url.startsWith(entry.getKey())) {
        return Observable.just(entry.getValue());
      }
    }
    // search for service
    if (store.containsKey(request.getServiceName())) {
      return Observable.just(store.get(request.getServiceName()));
    }
    else if (matchesAll != null) {
      return Observable.just(matchesAll);
    }
    else {
      LOGGER.warn("No response register for url: " + url);
      return fallback;
    }
  }

  @Override
  public boolean hasValidConfiguration(String serviceName) {
    return true;
  }

  /**
   * Registers a response for the given service and URL.
   * @param url The URL
   * @param response The response to register
   */
  public void mockRequest(final String url, final CaravanHttpResponse response) {
    store.put(url, response);
  }

  /**
   * Registers the response for the given service name.
   * @param serviceName The service name
   * @param response The response to return
   */
  public void mockRequestByService(final String serviceName, final CaravanHttpResponse response) {
    store.put(serviceName, response);
  }

  /**
   * Returns the given response for any request.
   * @param response Response to return
   */
  public void mockAnyRequest(final CaravanHttpResponse response) {
    matchesAll = response;
  }

}
