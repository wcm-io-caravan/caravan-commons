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
package io.wcm.caravan.commons.test.pipeline;

import io.wcm.caravan.pipeline.cache.CachePersistencyOptions;
import io.wcm.caravan.pipeline.cache.spi.CacheAdapter;

import java.util.Map;

import rx.Observable;

import com.google.common.collect.Maps;

/**
 * An in-memory implementation of the {@link CacheAdapter} for tests.
 */
public class InMemoryCacheAdapter implements CacheAdapter {

  private final Map<String, String> store = Maps.newConcurrentMap();

  @Override
  public Observable<String> get(String cacheKey, CachePersistencyOptions options) {
    return store.containsKey(cacheKey) ? Observable.just(store.get(cacheKey)) : Observable.empty();
  }

  @Override
  public void put(String cacheKey, String jsonString, CachePersistencyOptions options) {
    store.put(cacheKey, jsonString);
  }

}
