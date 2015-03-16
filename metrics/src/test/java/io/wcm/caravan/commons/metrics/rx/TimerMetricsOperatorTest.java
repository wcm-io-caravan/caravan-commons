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
import static org.junit.Assert.assertTrue;

import java.util.concurrent.TimeUnit;

import org.junit.Test;

import rx.Observable;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;

public class TimerMetricsOperatorTest {

  @Test
  public void test_time() {
    MetricRegistry registry = new MetricRegistry();
    Timer timer = registry.timer(MetricRegistry.name("test", "timer"));
    TimerMetricsOperator<String> operator = new TimerMetricsOperator<String>(timer);
    String output = Observable.just("test")
        .delay(100, TimeUnit.MILLISECONDS)
        .lift(operator)
        .toBlocking().single();
    assertEquals("test", output);
    assertEquals(1, timer.getCount());
    assertTrue(timer.getSnapshot().getValues()[0] / 1000000 >= 100);
  }
}
