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

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import org.apache.sling.testing.mock.osgi.junit.OsgiContext;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.codahale.metrics.MetricRegistry;

@RunWith(MockitoJUnitRunner.class)
public class GraphiteReporterMounterTest {

  @Mock
  private MetricRegistry metricRegistry;

  @Mock
  private GraphiteIntegrationConfig graphiteIntegrationConfig;

  private GraphiteReporterMounter mounter;

  @Rule
  public OsgiContext osgiContext = new OsgiContext();

  @Before
  public void setup() {
    when(graphiteIntegrationConfig.isEnabled()).thenReturn(true);
    when(graphiteIntegrationConfig.getGraphiteHostName()).thenReturn("localhost");
    when(graphiteIntegrationConfig.getGraphiteSocketPort()).thenReturn(80);
    when(graphiteIntegrationConfig.getPushInterval()).thenReturn(10);

    osgiContext.registerService(MetricRegistry.class, metricRegistry);
    osgiContext.registerService(GraphiteIntegrationConfig.class, graphiteIntegrationConfig);
  }

  @Test
  public void graphiteReporterShouldBeRegistered() {
    mounter = osgiContext.registerInjectActivateService(new GraphiteReporterMounter());
    assertThat(mounter.reporter, notNullValue());
  }

  @Test
  public void graphiteReporterShouldNotBeRegistered() {
    when(graphiteIntegrationConfig.isEnabled()).thenReturn(false);
    mounter = osgiContext.registerInjectActivateService(new GraphiteReporterMounter());
    assertThat(mounter.reporter, nullValue());
  }

  @Test
  public void graphiteReporterShouldBeStoppedAfterDeactivate() {
    mounter = osgiContext.registerInjectActivateService(new GraphiteReporterMounter());
    mounter.deactivate();
    assertThat(mounter.reporter, nullValue());
  }

}
