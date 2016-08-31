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


import java.util.Map;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.ConfigurationPolicy;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.commons.osgi.PropertiesUtil;

/**
 * Configuration for integrating the metrics to graphite server(Carbon) via TCP socket connection.
 */
@Component(immediate = true, metatype = true,
    label = "wcm.io Caravan Resilient Http Graphite Integration Configuration",
    description = "Configure to integrate the metrics to a graphite server(Carbon) via TCP socket connection",
    policy = ConfigurationPolicy.OPTIONAL)
@Service(GraphiteIntegrationConfig.class)
public class GraphiteIntegrationConfig {

  @Property(label = "Graphite host name",
      description = "Send metrics to the graphite server, if the graphite server host and port is set. Otherwise do nothing.")
  private static final String HOST_NAME = "hostName";

  @Property(label = "Carbon tcp port to receive the metrics",
      description = "Send metrics to the graphite server, if the graphite server host and port is set. Otherwise do nothing.",
      intValue = GraphiteIntegrationConfig.DEFAULT_PORT)
  private static final String PORT = "port";
  private static final int DEFAULT_PORT = 0;

  @Property(label = "Time interval in seconds to push metrics to graphite server",
      description = "How often(in seconds) should the metrics be pushed to the graphite server.",
      intValue = GraphiteIntegrationConfig.DEFAULT_PUSH_INTERVAL)
  private static final String PUSH_METRICS_INTERVAL = "pushInterval";
  private static final int DEFAULT_PUSH_INTERVAL = 5;

  @Property(label = "Enable the graphte integration.",
      description = "Enable the graphte integration. Metrics will be send to the graphite server",
      boolValue = GraphiteIntegrationConfig.DEFAULT_ENABLED)
  private static final String ENABLED = "enabled";
  private static final boolean DEFAULT_ENABLED = false;

  @Property(label = "Prefix", description = "Prefix of metrics to differentiate server or environment.")
  private static final String METRIC_PREFIX = "prefix";

  private String graphiteHostName;
  private int graphiteSocketPort;
  private int pushInterval;
  private boolean enabled;
  private String prefix;

  @Activate
  void activate(Map<String, Object> config) {
    enabled = PropertiesUtil.toBoolean(config.get(ENABLED), DEFAULT_ENABLED);
    graphiteHostName = PropertiesUtil.toString(config.get(HOST_NAME), null);
    prefix = PropertiesUtil.toString(config.get(METRIC_PREFIX), null);
    graphiteSocketPort = PropertiesUtil.toInteger(config.get(PORT), DEFAULT_PORT);
    pushInterval = PropertiesUtil.toInteger(config.get(PUSH_METRICS_INTERVAL), DEFAULT_PUSH_INTERVAL);
  }

  public boolean isEnabled() {
    return enabled;
  }

  public String getGraphiteHostName() {
    return graphiteHostName;
  }

  public int getGraphiteSocketPort() {
    return graphiteSocketPort;
  }

  public int getPushInterval() {
    return pushInterval;
  }

  public String getPrefix() {
    return prefix;
  }
}
