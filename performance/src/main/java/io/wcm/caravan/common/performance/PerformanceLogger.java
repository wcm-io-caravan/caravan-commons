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
package io.wcm.caravan.common.performance;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;

/**
 * Performance logger aids to create and log lines about results of performance measurements.
 */
public final class PerformanceLogger {

  private static final Logger log = LoggerFactory.getLogger(PerformanceLogger.class);

  private PerformanceLogger() {
    // default empty constructor
  }


  /**
   * Log line of measured performance of single operation specifying by performance metrics parameter.
   * @param marker log marker
   * @param metrics PerformanceMetrics a result of measurement
   */
  public static void log(Marker marker, PerformanceMetrics metrics) {
    log.debug(marker, getEndMetrics(metrics));
  }

  /**
   * Log line of measured performance of single operation specifying by performance metrics parameter.
   * @param metrics PerformanceMetrics a result of measurement
   */
  public static void log(PerformanceMetrics metrics) {
    log.debug(getEndMetrics(metrics));
  }


  private static String getTakenTimeByStepStartMessage(PerformanceMetrics metrics) {
    if (metrics.getTakenTimeByStepStart() != null) {
      return metrics.getTakenTimeByStepStart() >= 0
          ? " - taken time by step start : " + metrics.getTakenTimeByStepStart() + " ms" : " - incorrect step start time";
    }

    return "";

  }


  private static String getTakenTimeByStepEndMessage(PerformanceMetrics metrics) {
    if (metrics.getTakenTimeByStepEnd() != null) {
      return metrics.getTakenTimeByStepEnd() >= 0
          ? " - " + "taken time by step end : " + metrics.getTakenTimeByStepEnd() + " ms" : " - incorrect step end time";
    }

    return "";

  }

  private static String getEndMetrics(PerformanceMetrics metrics) {
    return "Key : " + metrics.getKey()
        + " - " + "level : " + metrics.getLevel()
        + " - " + "action : " + metrics.getAction()
        + " - " + "taken time by step : " + metrics.getTakenTimeByStep() + " ms"
        + getTakenTimeByStepStartMessage(metrics)
        + getTakenTimeByStepEndMessage(metrics)
        + " - " + "start time : " + metrics.getStartTime() + " ms"
        + " - " + "end time : " + metrics.getEndTime() + " ms"
        + " - " + "description : " + metrics.getDescriptor()
        + " - " + "correlation id : " + metrics.getCorrelationId();
  }
}
