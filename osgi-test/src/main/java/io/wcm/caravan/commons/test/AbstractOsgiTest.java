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

import io.wcm.caravan.commons.test.io.JsonFixture;
import io.wcm.caravan.commons.test.io.MockingCaravanHttpClient;
import io.wcm.caravan.commons.test.pipeline.InMemoryCacheAdapter;
import io.wcm.caravan.io.http.CaravanHttpClient;
import io.wcm.caravan.io.http.response.CaravanHttpResponse;
import io.wcm.caravan.io.http.response.CaravanHttpResponseBuilder;
import io.wcm.caravan.pipeline.JsonPipelineFactory;
import io.wcm.caravan.pipeline.cache.spi.CacheAdapter;
import io.wcm.caravan.pipeline.impl.JsonPipelineFactoryImpl;

import org.apache.sling.testing.mock.osgi.junit.OsgiContext;
import org.junit.Before;
import org.junit.Rule;

import com.codahale.metrics.MetricRegistry;
import com.google.common.base.Charsets;

/**
 * The abstract starting class for OSGI mocking tests. Registers Cache adapter and resilient HTTP client.
 */
public abstract class AbstractOsgiTest {

  /**
   * The OSGI context.
   */
  // CHECKSTYLE:OFF
  @Rule
  public OsgiContext context = new OsgiContext();
  // CHECKSTYLE:ON

  protected InMemoryCacheAdapter cacheAdapter;
  protected MockingCaravanHttpClient resilientHttp;
  protected MetricRegistry metricRegistry;

  /**
   * Sets up mocking environment.
   */
  @Before
  public void setUp() {
    TestConfiguration.init();

    cacheAdapter = new InMemoryCacheAdapter();
    context.registerService(CacheAdapter.class, cacheAdapter);

    resilientHttp = new MockingCaravanHttpClient();
    context.registerService(CaravanHttpClient.class, resilientHttp);

    metricRegistry = context.registerService(MetricRegistry.class, new MetricRegistry());
  }

  protected JsonFixture loadJson(final String classPath) {
    return new JsonFixture(getClass().getResourceAsStream(classPath));
  }

  protected void mockAnyRequest(final Object payload) {
    CaravanHttpResponse response = new CaravanHttpResponseBuilder()
        .status(200)
        .reason("OK")
        .body(payload.toString(), Charsets.UTF_8)
        .build();
    resilientHttp.mockAnyRequest(response);
  }

  protected JsonPipelineFactory registerJsonPipelineFactory() {
    return context.registerInjectActivateService(new JsonPipelineFactoryImpl());
  }

}
