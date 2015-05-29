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
package io.wcm.caravan.commons.cors.impl;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
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
public class CorsServletFilterTest {

  @Rule
  public OsgiContext context = new OsgiContext();

  @Mock
  private HttpServletRequest request;
  @Mock
  private HttpServletResponse response;
  @Mock
  private FilterChain filterChain;

  private CorsServletFilter underTest;

  @Before
  public void setUp() {
    underTest = context.registerInjectActivateService(new CorsServletFilter());
  }

  @Test
  public void testWithoutOrigin() throws IOException, ServletException {
    when(request.getHeader(HttpHeader.ORIGIN)).thenReturn(null);

    underTest.doFilter(request, response, filterChain);
    verify(filterChain).doFilter(request, response);
    verifyZeroInteractions(response);
  }

  @Test
  public void testWithOrigin() throws IOException, ServletException {
    when(request.getHeader(HttpHeader.ORIGIN)).thenReturn("myhost");

    underTest.doFilter(request, response, filterChain);
    verify(filterChain).doFilter(request, response);

    verify(response).setHeader(HttpHeader.ACCESS_CONTROL_ALLOW_ORIGIN, "myhost");
    verify(response).addHeader(HttpHeader.VARY, HttpHeader.ORIGIN);
  }

}
