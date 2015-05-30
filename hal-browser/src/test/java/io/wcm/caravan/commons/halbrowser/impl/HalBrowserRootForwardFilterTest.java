/*
 * #%L
 * wcm.io
 * %%
 * Copyright (C) 2015 wcm.io
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

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.sling.testing.mock.osgi.junit.OsgiContext;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class HalBrowserRootForwardFilterTest {

  @Rule
  public OsgiContext context = new OsgiContext();

  @Mock
  private FilterConfig filterConfig;
  @Mock
  private HttpServletRequest request;
  @Mock
  private HttpServletResponse response;
  @Mock
  private FilterChain filterChain;

  private ByteArrayOutputStream output = new ByteArrayOutputStream();

  private HalBrowserRootForwardFilter underTest;

  @Before
  public void setUp() throws Exception {
    when(response.getOutputStream()).thenReturn(new ServletOutputStream() {
      @Override
      public void write(int b) throws IOException {
        output.write(b);
      }
    });

    this.underTest = context.registerInjectActivateService(new HalBrowserRootForwardFilter());
    underTest.init(filterConfig);
  }

  @Test
  public void testDoFilter() throws Exception {
    underTest.doFilter(request, response, filterChain);

    byte[] data = output.toByteArray();

    assertTrue(data.length > 0);

    verify(response).setStatus(HttpServletResponse.SC_OK);
    verify(response).setContentType("text/html");
    verify(response).setContentLength(data.length);

    verifyNoMoreInteractions(filterChain);
  }

}
