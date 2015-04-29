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
package io.wcm.caravan.commons.performance;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import io.wcm.caravan.common.performance.PerformanceMetrics;

import org.junit.Before;
import org.junit.Test;

import rx.functions.Action0;


public class PerformanceMetricsTest {

  private PerformanceMetrics metrics;

  @Before
  public void setup() {
    metrics = PerformanceMetrics.createNew("Action", "Descriptor", "CorrelationId");
  }

  @Test
  public void testStartTimestamp() {
    assertNotLaunched(metrics);
    metrics.setStartTimestamp();
    assertStartOnly(metrics);
  }

  @Test
  public void testStartTimestampAction() {
    Action0 action = metrics.getStartAction();
    assertNotLaunched(metrics);
    action.call();
    assertStartOnly(metrics);
  }

  @Test
  public void testEndTimestamp() {
    assertNotLaunched(metrics);
    metrics.setEndTimestamp();
    assertEndOnly(metrics);
  }

  @Test
  public void testEndTimestampAction() {
    Action0 action = metrics.getEndAction();
    assertNotLaunched(metrics);
    action.call();
    assertEndOnly(metrics);
  }

  @Test
  public void testTakenTime() {
    assertNotLaunched(metrics);
    metrics.setStartTimestamp();
    assertStartOnly(metrics);
    metrics.setEndTimestamp();
    assertStartAndEnd(metrics);
  }

  @Test
  public void testTakenTimeByNext() {
    PerformanceMetrics next = metrics.createNext("nextAction", "nextDescription");
    assertNotLaunched(metrics);
    assertNotLaunched(next);

    next.setStartTimestamp();
    metrics.setStartTimestamp();

    assertStartOnly(metrics);
    assertStartOnly(next);

    metrics.setEndTimestamp();
    next.setEndTimestamp();

    assertStartAndEnd(metrics);
    assertStartAndEndWithPrevious(next);
  }

  private void assertNotLaunched(PerformanceMetrics metricsToTest) {
    assertNull(metricsToTest.getStartTime());
    assertNull(metricsToTest.getEndTime());
    assertEmpty(metricsToTest);
  }

  private void assertStartOnly(PerformanceMetrics metricsToTest) {
    assertTrue(metricsToTest.getStartTime() > 0);
    assertNull(metricsToTest.getEndTime());
    assertEmpty(metricsToTest);
  }

  private void assertEndOnly(PerformanceMetrics metricsToTest) {
    assertTrue(metricsToTest.getEndTime() > 0);
    assertNull(metricsToTest.getStartTime());
    assertEmpty(metricsToTest);
  }

  private void assertStartAndEnd(PerformanceMetrics metricsToTest) {
    assertCharged(metricsToTest);
    assertPreviousEmpty(metricsToTest);
  }

  private void assertStartAndEndWithPrevious(PerformanceMetrics metricsToTest) {
    assertCharged(metricsToTest);
    assertTrue(metricsToTest.isPreviousCharged());
    assertTrue(metricsToTest.getTakenTimeByStepEnd() >= 0);
    assertTrue(metricsToTest.getTakenTimeByStepStart() >= 0);
  }

  private void assertEmpty(PerformanceMetrics metricsToTest) {
    assertPreviousEmpty(metricsToTest);
    assertFalse(metricsToTest.isCharged());
    assertNull(metricsToTest.getTakenTimeByStep());
  }

  private void assertPreviousEmpty(PerformanceMetrics metricsToTest) {
    assertFalse(metricsToTest.isPreviousCharged());
    assertNull(metricsToTest.getTakenTimeByStepEnd());
    assertNull(metricsToTest.getTakenTimeByStepStart());
  }

  private void assertCharged(PerformanceMetrics metricsToTest) {
    assertTrue(metricsToTest.isCharged());
    assertTrue(metricsToTest.getStartTime() > 0);
    assertTrue(metricsToTest.getEndTime() > 0);
    assertTrue(metricsToTest.getTakenTimeByStep() >= 0);
  }



}
