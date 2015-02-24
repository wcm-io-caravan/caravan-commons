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

import javax.servlet.ServletException;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Reference;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.health.HealthCheckRegistry;
import com.codahale.metrics.servlets.HealthCheckServlet;
import com.codahale.metrics.servlets.MetricsServlet;
import com.codahale.metrics.servlets.PingServlet;
import com.codahale.metrics.servlets.ThreadDumpServlet;

/**
 * Registers metrics Servlets with referenced registries.
 */
@Component
public class MetricsServletMounter {

  private static final String METRICS_URI = "/system/metrics";
  private static final String HEALTH_URI = "/system/healthcheck";
  private static final String PING_URI = "/system/ping";
  private static final String THREADS_URI = "/system/threads";

  @Reference
  private MetricRegistry metricRegistry;
  @Reference
  private HealthCheckRegistry healthCheckRegistry;

  @Reference
  private HttpService httpService;

  @Activate
  protected void activate() throws NamespaceException, ServletException {
    httpService.registerServlet(METRICS_URI, new MetricsServlet(metricRegistry), null, null);
    httpService.registerServlet(HEALTH_URI, new HealthCheckServlet(healthCheckRegistry), null, null);
    httpService.registerServlet(PING_URI, new PingServlet(), null, null);
    httpService.registerServlet(THREADS_URI, new ThreadDumpServlet(), null, null);
  }

  @Deactivate
  protected void deactivate() {
    httpService.unregister(METRICS_URI);
    httpService.unregister(HEALTH_URI);
    httpService.unregister(PING_URI);
    httpService.unregister(THREADS_URI);
  }

}
