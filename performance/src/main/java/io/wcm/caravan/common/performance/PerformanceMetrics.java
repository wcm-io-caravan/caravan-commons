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

import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

import rx.functions.Action0;

/**
 * Performance metrics aid to collect time spent for execution of operation, including any sub operations, which results
 * could be expected before the actual operation takes place. Each operation could be traced by key
 * and level. Key should stay the same for all dependent measurements, when the level should vary depends on the step of
 * measured sub operation.
 */
public final class PerformanceMetrics {

  /**
   *
   */
  private static final AtomicInteger KEY_GEN = new AtomicInteger(0);

  private Integer key;
  private Integer level;
  private String action;
  private String descriptor;
  private String correlationId;
  private Long startTime;
  private Long endTime;
  private Long totalTakenTimeByStep;
  private Long takenTimeByStepStart;
  private Long takenTimeByStepEnd;
  private PerformanceMetrics previous;

  private PerformanceMetrics(Integer key, Integer level, String action, String descriptor, String correlationId) {
    this.key = key;
    this.level = level;
    this.action = action;
    this.descriptor = descriptor;
    this.correlationId = correlationId;
  }

  /**
   * Creates new instance of performance metrics. Generates new metrics key and assigns zero level.
   * @param action a short name of measured operation, typically a first prefix of descriptor
   * @param descriptor a full description of measured operation
   * @param correlationId a reference to the request, which caused the operation
   * @return PerformanceMetrics a new instance of performance metrics
   */
  public static PerformanceMetrics createNew(String action, String descriptor, String correlationId) {
    return new PerformanceMetrics(KEY_GEN.getAndIncrement(), 0, action, descriptor, correlationId);
  }

  /**
   * Creates new instance of performance metrics. Stores the key and correlation id of the parent metrics instance.
   * Assigns next level.
   * @param nextAction a short name of measured operation, typically a first prefix of descriptor
   * @param nextDescriptor a full description of measured operation
   * @return PerformanceMetrics a new instance of performance metrics with stored key and correlationId from the parent
   *         metrics and assigned next level
   */
  public PerformanceMetrics createNext(String nextAction, String nextDescriptor) {
    PerformanceMetrics next = new PerformanceMetrics(key, level + 1, nextAction, nextDescriptor, correlationId);
    next.previous = this;
    return next;
  }

  /**
   * When called, start action sets time stamp to identify start time of operation.
   * @return Action0
   */
  public Action0 getStartAction() {
    return new Action0() {

      @Override
      public void call() {
        if (startTime == null) {
          startTime = new Date().getTime();
        }
      }
    };
  }

  /**
   * When called, end action sets time stamp to identify end time of operation and logs the metrics.
   * @return Action0
   */
  public Action0 getEndAction() {
    return new Action0() {

      @Override
      public void call() {
        if (endTime == null) {
          endTime = new Date().getTime();
          PerformanceLogger.log(PerformanceMetrics.this);
        }
      }
    };
  }

  /**
   * Set time stamp of operation start.
   */
  public void setStartTimestamp() {
    startTime = new Date().getTime();
  }

  /**
   * Set time stamp of operation end.
   */
  public void setEndTimestamp() {
    endTime = new Date().getTime();
  }

  /**
   * @return true if start and end time of measured operation are set
   */
  public boolean isCharged() {
    return endTime != null && startTime != null;
  }

  /**
   * @return true if there is a sub operation, which start and end time are set
   */
  public boolean isPreviousCharged() {
    return previous != null && previous.isCharged();
  }


  public Integer getKey() {
    return this.key;
  }


  public Integer getLevel() {
    return this.level;
  }


  public String getAction() {
    return this.action;
  }

  public String getDescriptor() {
    return this.descriptor;
  }

  public String getCorrelationId() {
    return this.correlationId;
  }

  public Long getStartTime() {
    return this.startTime;
  }

  public Long getEndTime() {
    return this.endTime;
  }

  /**
   * @return time in milliseconds taken by actual operation, excludes time taken by any measured sub operation
   */
  public Long getTakenTimeByStep() {
    if (this.isCharged() && this.totalTakenTimeByStep == null) {
      this.totalTakenTimeByStep = isPreviousCharged() ? getTakenTimeByStepStart() + getTakenTimeByStepEnd() : this.endTime - this.startTime;
    }
    return this.totalTakenTimeByStep;
  }

  /**
   * @return time in milliseconds spent by operation before sub operation was called
   */
  public Long getTakenTimeByStepStart() {
    if (this.isCharged() && this.isPreviousCharged() && this.takenTimeByStepStart == null) {
      this.takenTimeByStepStart = this.previous.startTime - this.startTime;
    }
    return this.takenTimeByStepStart;
  }

  /**
   * @return time in milliseconds spent by operation after sub operation was released
   */
  public Long getTakenTimeByStepEnd() {
    if (this.isCharged() && this.isPreviousCharged() && this.takenTimeByStepEnd == null) {
      this.takenTimeByStepEnd = this.endTime - this.previous.endTime;
    }
    return this.takenTimeByStepEnd;
  }

  @Override
  public String toString() {
    return "PerformanceMetrics [key=" + this.key + ", level=" + this.level + ", action=" + this.action + ", descriptor=" + this.descriptor + ", correlationId="
        + this.correlationId + ", startTime=" + this.startTime + ", endTime=" + this.endTime + ", totalTakenTimeByStep=" + this.totalTakenTimeByStep
        + ", takenTimeByStepStart=" + this.takenTimeByStepStart + ", takenTimeByStepEnd=" + this.takenTimeByStepEnd + ", previous=" + this.previous + "]";
  }


}
