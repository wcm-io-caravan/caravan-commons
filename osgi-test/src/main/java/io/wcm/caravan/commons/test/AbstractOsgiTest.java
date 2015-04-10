/* Copyright (c) pro!vision GmbH. All rights reserved. */
package io.wcm.caravan.commons.test;

import io.wcm.caravan.commons.test.io.JsonFixture;
import io.wcm.caravan.commons.test.io.MockingCaravanHttpClient;
import io.wcm.caravan.commons.test.pipeline.InMemoryCacheAdapter;
import io.wcm.caravan.io.http.CaravanHttpClient;
import io.wcm.caravan.io.http.response.CaravanHttpResponse;
import io.wcm.caravan.pipeline.JsonPipelineFactory;
import io.wcm.caravan.pipeline.cache.spi.CacheAdapter;
import io.wcm.caravan.pipeline.impl.JsonPipelineFactoryImpl;

import java.nio.charset.Charset;

import org.apache.sling.testing.mock.osgi.junit.OsgiContext;
import org.junit.Before;
import org.junit.Rule;

import com.codahale.metrics.MetricRegistry;
import com.google.common.collect.ImmutableMultimap;

/**
 * The abstract starting class for OSGI mocking tests. Registers Cache adapter and resilient HTTP client.
 */
public abstract class AbstractOsgiTest {

  /**
   * The OSGI context.
   */
  @Rule
  protected OsgiContext context = new OsgiContext();

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
    CaravanHttpResponse response = CaravanHttpResponse.create(200, "Ok", ImmutableMultimap.of(), payload.toString(), Charset.forName("UTF-8"));
    resilientHttp.mockAnyRequest(response);
  }

  protected JsonPipelineFactory registerJsonPipelineFactory() {
    return context.registerInjectActivateService(new JsonPipelineFactoryImpl());
  }

}
