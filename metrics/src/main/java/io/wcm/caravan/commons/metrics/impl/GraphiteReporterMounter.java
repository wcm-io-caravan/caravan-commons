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

import java.io.IOException;
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
  private GraphiteRegistration graphiteRegistration = new GraphiteRegistration();

  @Reference
  private MetricRegistry metricRegistry;
  @Reference
  private GraphiteIntegrationConfig graphiteIntegrationConfig;

  @Activate
  protected void activate() {
    // stop the connections at first
    graphiteRegistration.interrupt();
    graphiteRegistration.start();
  }

  @Deactivate
  protected void deactivate() {
    graphiteRegistration.interrupt();
    metricRegistry.getNames().stream().forEach(metricRegistry::remove);
  }


  /**
   * register the graphite reporter only if the first connection could be established, to prevent lots of connection exception in logs.
   */
  private class GraphiteRegistration extends Thread {

    private Graphite graphiteServer;
    private static final int RETRY_DURATION_IN_MINUTES = 2;

    @Override
    public void run() {
      closeConnection();

      if (graphiteIntegrationConfig.isEnabled()) {
        LOG.info("Graphite publisher is enabled.");

        String hostName = graphiteIntegrationConfig.getGraphiteHostName();
        int port = graphiteIntegrationConfig.getGraphiteSocketPort();

        graphiteServer = new Graphite(new InetSocketAddress(hostName, port));


        if (establishConnection()) {
          int pushInterval = graphiteIntegrationConfig.getPushInterval();
          GraphiteReporter reporter = GraphiteReporter.forRegistry(metricRegistry).build(graphiteServer);
          reporter.start(pushInterval, TimeUnit.SECONDS);
          LOG.info("Sending metrics data every {} seconds to {}:{}", pushInterval, hostName, port);
        }
      }
      else {
        LOG.info("Graphite publisher is disabled.");
      }
    }

    /**
     * establish a connection to the graphite. if is failed, wait {@value #RETRY_DURATION_IN_MINUTES} minutes and retry
     * until the connection is established successfully or the thread is stopped.
     *
     */
    private boolean establishConnection() {
      while (!Thread.currentThread().isInterrupted()) {
        try {
          graphiteServer.connect();
          LOG.info("Connection to graphite server is established.");
          return true;
        }
        catch (IOException e) {
          LOG.error("Unable to connect to {}:{}! wait {} minutes and reconnect. ERROR: {}",
            graphiteIntegrationConfig.getGraphiteHostName(),
            graphiteIntegrationConfig.getGraphiteSocketPort(),
            RETRY_DURATION_IN_MINUTES,
            e.getMessage());
          try {
            Thread.sleep(TimeUnit.MINUTES.toMillis(RETRY_DURATION_IN_MINUTES));
          }
          catch (InterruptedException e1) {
            return false;
          }
        }
      }
      return false;
    }


    void closeConnection() {
      if (graphiteServer != null && graphiteServer.isConnected()) {
        try {
          graphiteServer.close();
        }
        catch (IOException e) {
          throw new RuntimeException(e);
        }
      }
    }
  }
}
