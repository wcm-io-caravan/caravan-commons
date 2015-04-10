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
package io.wcm.caravan.commons.test;

import io.wcm.caravan.commons.test.io.SimpleCaravanHttpClient;
import io.wcm.caravan.commons.test.pipeline.InMemoryCacheAdapter;
import io.wcm.caravan.io.http.CaravanHttpClient;
import io.wcm.caravan.pipeline.JsonPipelineFactory;
import io.wcm.caravan.pipeline.cache.spi.CacheAdapter;

import org.junit.Before;

import com.codahale.metrics.MetricRegistry;

/**
 * WARNING: all HTTP requests are really executed. Don't use it on CI!
 */
public abstract class AbstractRealHttpOsgiTest extends AbstractOsgiTest {

  protected JsonPipelineFactory pipelineFactory;

  protected SimpleCaravanHttpClient httpClient;

  @Override
  @Before
  public void setUp() {
    TestConfiguration.init();
    cacheAdapter = new InMemoryCacheAdapter();
    context.registerService(CacheAdapter.class, cacheAdapter);
    metricRegistry = context.registerService(MetricRegistry.class, new MetricRegistry());

    httpClient = new SimpleCaravanHttpClient();
    context.registerService(CaravanHttpClient.class, httpClient);

    pipelineFactory = registerJsonPipelineFactory();
  }

}
