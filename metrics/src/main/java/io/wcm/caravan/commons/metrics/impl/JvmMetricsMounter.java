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
package io.wcm.caravan.commons.metrics.impl;

import java.lang.management.ManagementFactory;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Reference;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.jvm.BufferPoolMetricSet;
import com.codahale.metrics.jvm.ClassLoadingGaugeSet;
import com.codahale.metrics.jvm.GarbageCollectorMetricSet;
import com.codahale.metrics.jvm.MemoryUsageGaugeSet;
import com.codahale.metrics.jvm.ThreadStatesGaugeSet;

/**
 * Adds JVM details to the Metrics registry.
 */
@Component
public class JvmMetricsMounter {

  private static final String PREFIX = "jvm";
  @Reference
  private MetricRegistry metricRegistry;

  @Activate
  protected void activate() {
    metricRegistry.register(MetricRegistry.name(PREFIX, "gc"), new GarbageCollectorMetricSet());
    metricRegistry.register(MetricRegistry.name(PREFIX, "buffers"), new BufferPoolMetricSet(ManagementFactory.getPlatformMBeanServer()));
    metricRegistry.register(MetricRegistry.name(PREFIX, "memory"), new MemoryUsageGaugeSet());
    metricRegistry.register(MetricRegistry.name(PREFIX, "threads"), new ThreadStatesGaugeSet());
    metricRegistry.register(MetricRegistry.name(PREFIX, "classLoaders"), new ClassLoadingGaugeSet());
  }

  @Deactivate
  protected void deactivate() {
    metricRegistry.remove(MetricRegistry.name(PREFIX, "gc"));
    metricRegistry.remove(MetricRegistry.name(PREFIX, "buffers"));
    metricRegistry.remove(MetricRegistry.name(PREFIX, "memory"));
    metricRegistry.remove(MetricRegistry.name(PREFIX, "threads"));
    metricRegistry.remove(MetricRegistry.name(PREFIX, "classLoaders"));
  }

}
