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
package io.wcm.caravan.commons.metrics.rx;

import static org.junit.Assert.assertEquals;

import java.util.NoSuchElementException;

import org.junit.Before;
import org.junit.Test;

import rx.Observable;

import com.codahale.metrics.Counter;
import com.codahale.metrics.MetricRegistry;


public class HitsAndMissesCountingMetricsOperatorTest {

  private Counter hitsCounter;
  private Counter missesCounter;
  private HitsAndMissesCountingMetricsOperator<String> operator;

  @Before
  public void setUp() {
    MetricRegistry registry = new MetricRegistry();
    hitsCounter = registry.counter(MetricRegistry.name("test", "hits"));
    missesCounter = registry.counter(MetricRegistry.name("test", "misses"));
    operator = new HitsAndMissesCountingMetricsOperator<String>(hitsCounter, missesCounter);
  }

  @Test
  public void test_hit() {
    String output = Observable.just("test")
        .lift(operator)
        .toBlocking().single();
    assertEquals("test", output);
    assertEquals(1, hitsCounter.getCount());
    assertEquals(0, missesCounter.getCount());
  }

  @Test
  public void test_miss() {
    try {
      Observable.<String>empty()
      .lift(operator)
      .toBlocking().single();
    }
    catch (NoSuchElementException ex) {
      // nothing to do
    }
    assertEquals(0, hitsCounter.getCount());
    assertEquals(1, missesCounter.getCount());
  }

}
