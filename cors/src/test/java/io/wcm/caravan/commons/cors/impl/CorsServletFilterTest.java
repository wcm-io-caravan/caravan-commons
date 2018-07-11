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

import static io.wcm.caravan.commons.cors.impl.CorsServletFilter.PROPERTY_ALLOW_ALL_HOSTS;
import static io.wcm.caravan.commons.cors.impl.CorsServletFilter.PROPERTY_ENABLED;
import static io.wcm.caravan.commons.cors.impl.CorsServletFilter.PROPERTY_HOST_BLACKLIST;
import static io.wcm.caravan.commons.cors.impl.CorsServletFilter.PROPERTY_HOST_WHITELIST;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.sling.testing.mock.osgi.junit.OsgiContext;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.google.common.collect.ImmutableMap;

@RunWith(MockitoJUnitRunner.Silent.class)
public class CorsServletFilterTest {

  @Rule
  public OsgiContext context = new OsgiContext();

  @Mock
  private HttpServletRequest request;
  @Mock
  private HttpServletResponse response;
  @Mock
  private FilterChain filterChain;

  @Test
  public void testDisabled() throws IOException, ServletException {
    Filter underTest = setupFilter(ImmutableMap.of(PROPERTY_ENABLED, false));

    when(request.getHeader(HttpHeader.ORIGIN)).thenReturn("myhost");

    underTest.doFilter(request, response, filterChain);
    verify(filterChain).doFilter(request, response);
    verifyZeroInteractions(response);
  }

  @Test
  public void testAllowAllHosts_WithoutOrigin() throws IOException, ServletException {
    Filter underTest = setupFilter(ImmutableMap.of(PROPERTY_ALLOW_ALL_HOSTS, true));

    when(request.getHeader(HttpHeader.ORIGIN)).thenReturn(null);

    underTest.doFilter(request, response, filterChain);
    verify(filterChain).doFilter(request, response);
    verifyZeroInteractions(response);
  }

  @Test
  public void testAllowAllHosts_WithOrigin() throws IOException, ServletException {
    Filter underTest = setupFilter(ImmutableMap.of(PROPERTY_ALLOW_ALL_HOSTS, true));

    when(request.getHeader(HttpHeader.ORIGIN)).thenReturn("myhost");

    underTest.doFilter(request, response, filterChain);
    verify(filterChain).doFilter(request, response);

    verify(response).setHeader(HttpHeader.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
  }

  @Test
  public void testNotAllowAllHosts_WithoutOrigin() throws IOException, ServletException {
    Filter underTest = setupFilter(ImmutableMap.of(PROPERTY_ALLOW_ALL_HOSTS, false));

    when(request.getHeader(HttpHeader.ORIGIN)).thenReturn(null);

    underTest.doFilter(request, response, filterChain);
    verify(filterChain).doFilter(request, response);
    verifyZeroInteractions(response);
  }

  @Test
  public void testNotAllowAllHosts_WithOrigin() throws IOException, ServletException {
    Filter underTest = setupFilter(ImmutableMap.of(PROPERTY_ALLOW_ALL_HOSTS, false));

    when(request.getHeader(HttpHeader.ORIGIN)).thenReturn("myhost");

    underTest.doFilter(request, response, filterChain);
    verify(filterChain).doFilter(request, response);

    verify(response).setHeader(HttpHeader.ACCESS_CONTROL_ALLOW_ORIGIN, "myhost");
    verify(response).addHeader(HttpHeader.VARY, HttpHeader.ORIGIN);
  }

  @Test
  public void testWhitelist_Allowed() throws IOException, ServletException {
    Filter underTest = setupFilter(ImmutableMap.of(PROPERTY_ALLOW_ALL_HOSTS, false,
        PROPERTY_HOST_WHITELIST, new String[] {
        "host1", "host2"
    }));

    when(request.getHeader(HttpHeader.ORIGIN)).thenReturn("host1");

    underTest.doFilter(request, response, filterChain);
    verify(filterChain).doFilter(request, response);

    verify(response).setHeader(HttpHeader.ACCESS_CONTROL_ALLOW_ORIGIN, "host1");
    verify(response).addHeader(HttpHeader.VARY, HttpHeader.ORIGIN);
  }

  @Test
  public void testWhitelist_Disallowed() throws IOException, ServletException {
    Filter underTest = setupFilter(ImmutableMap.of(PROPERTY_ALLOW_ALL_HOSTS, false,
        PROPERTY_HOST_WHITELIST, new String[] {
        "host1", "host2"
    }));

    when(request.getHeader(HttpHeader.ORIGIN)).thenReturn("myhost");

    underTest.doFilter(request, response, filterChain);
    verify(filterChain).doFilter(request, response);
    verifyZeroInteractions(response);
  }

  @Test
  public void testBlacklist_Disallowed() throws IOException, ServletException {
    Filter underTest = setupFilter(ImmutableMap.of(PROPERTY_ALLOW_ALL_HOSTS, false,
        PROPERTY_HOST_BLACKLIST, new String[] {
        "host1", "host2"
    }));

    when(request.getHeader(HttpHeader.ORIGIN)).thenReturn("host1");

    underTest.doFilter(request, response, filterChain);
    verify(filterChain).doFilter(request, response);
    verifyZeroInteractions(response);
  }

  @Test
  public void testBlacklist_Allowed() throws IOException, ServletException {
    Filter underTest = setupFilter(ImmutableMap.of(PROPERTY_ALLOW_ALL_HOSTS, false,
        PROPERTY_HOST_BLACKLIST, new String[] {
        "host1", "host2"
    }));

    when(request.getHeader(HttpHeader.ORIGIN)).thenReturn("myhost");

    underTest.doFilter(request, response, filterChain);
    verify(filterChain).doFilter(request, response);

    verify(response).setHeader(HttpHeader.ACCESS_CONTROL_ALLOW_ORIGIN, "myhost");
    verify(response).addHeader(HttpHeader.VARY, HttpHeader.ORIGIN);
  }

  private CorsServletFilter setupFilter(Map<String, Object> props) {
    return context.registerInjectActivateService(new CorsServletFilter(), props);
  }

}
