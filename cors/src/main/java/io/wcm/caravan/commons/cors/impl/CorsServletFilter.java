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

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.osgi.framework.Constants;

/**
 * ServletFilter that sets proper CORS header in response.
 */
@Component(immediate = true)
@Service(Filter.class)
@Properties({
  @Property(name = Constants.SERVICE_RANKING, intValue = 10000),
  @Property(name = "pattern", value = "/.*")
})
public class CorsServletFilter implements Filter {

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
    // nothing to do
  }

  @Override
  public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
    HttpServletRequest request = (HttpServletRequest)servletRequest;
    HttpServletResponse response = (HttpServletResponse)servletResponse;

    String origin = request.getHeader(HttpHeader.ORIGIN);

    // FIXME: this is only a hack without and origin validation - implement it
    if (StringUtils.isNotEmpty(origin)) {
      response.setHeader(HttpHeader.ACCESS_CONTROL_ALLOW_ORIGIN, origin);
      response.addHeader(HttpHeader.VARY, HttpHeader.ORIGIN);
    }

    chain.doFilter(request, response);
  }

  @Override
  public void destroy() {
    // nothing to do
  }

}
