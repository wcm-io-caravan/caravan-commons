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

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.graphite.Graphite;
import com.codahale.metrics.graphite.GraphiteReporter;

/**
 * add graphite reporter to metrics registry. send metrics to graphite server
 */
@Component
public class GraphiteReporterMounter {

  private static final Logger LOG = LoggerFactory.getLogger(GraphiteReporterMounter.class);

  @Reference
  private MetricRegistry metricRegistry;
  @Reference
  private GraphiteIntegrationConfig graphiteIntegrationConfig;

  GraphiteReporter reporter;

  @Activate
  protected void activate() {
    if (graphiteIntegrationConfig.isEnabled()) {
      LOG.info("Graphite publisher is enabled.");

      String hostName = graphiteIntegrationConfig.getGraphiteHostName();
      int port = graphiteIntegrationConfig.getGraphiteSocketPort();
      int pushInterval = graphiteIntegrationConfig.getPushInterval();

      Graphite graphiteServer = new Graphite(new InetSocketAddress(hostName, port));
      reporter = GraphiteReporter.forRegistry(metricRegistry)
          .prefixedWith(graphiteIntegrationConfig.getPrefix())
          .build(graphiteServer);

      reporter.start(pushInterval, TimeUnit.SECONDS);

      LOG.info("Sending metrics data every {} seconds to {}:{}", pushInterval, hostName, port);
    }
    else {
      LOG.info("Graphite publisher is disabled.");
    }
  }


  @Deactivate
  protected void deactivate() {
    if (reporter != null) {
      reporter.stop();
      reporter = null;
    }
  }

}
