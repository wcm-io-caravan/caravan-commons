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
package io.wcm.caravan.commons.halbrowser.impl;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

import org.apache.sling.testing.mock.osgi.MockOsgi;
import org.apache.sling.testing.mock.osgi.junit.OsgiContext;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.osgi.service.http.HttpContext;
import org.osgi.service.http.HttpService;

import com.google.common.collect.ImmutableMap;

@RunWith(MockitoJUnitRunner.class)
public class HalBrowserHttpServiceMounterTest {

  @Rule
  public OsgiContext context = new OsgiContext();

  @Mock
  private HttpService httpService;

  @Before
  public void setUp() {
    context.registerService(HttpService.class, httpService);
  }

  @Test
  public void testActivateDeactivate() throws Exception {
    HalBrowserHttpServiceMounter underTest = new HalBrowserHttpServiceMounter();

    context.registerInjectActivateService(underTest);
    verify(httpService).registerResources(eq(HalBrowserHttpServiceMounter.HALBROWSER_URI_PREFIX), anyString(), any(HttpContext.class));
    verify(httpService).registerResources(eq(HalBrowserHttpServiceMounter.HALBROWSER_URI_PREFIX_LEGACY), anyString(), any(HttpContext.class));

    MockOsgi.deactivate(underTest, context.bundleContext(), ImmutableMap.<String, Object>of());
    verify(httpService).unregister(eq(HalBrowserHttpServiceMounter.HALBROWSER_URI_PREFIX));
    verify(httpService).unregister(eq(HalBrowserHttpServiceMounter.HALBROWSER_URI_PREFIX_LEGACY));
  }

}
