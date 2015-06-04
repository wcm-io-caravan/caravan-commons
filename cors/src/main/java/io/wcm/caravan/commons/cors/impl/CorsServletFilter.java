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

import io.wcm.caravan.commons.stream.Collectors;
import io.wcm.caravan.commons.stream.Streams;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.commons.osgi.PropertiesUtil;
import org.osgi.framework.Constants;

/**
 * ServletFilter that sets proper CORS header in response.
 */
@Component(immediate = true, metatype = true,
label = "wcm.io Caravan CORS Filter",
description = "Servlet filter that sends CORS response header to allow cross-origin access.")
@Service(Filter.class)
@Properties({
  @Property(name = Constants.SERVICE_RANKING, intValue = 10000),
  @Property(name = "pattern", value = "/.*")
})
public class CorsServletFilter implements Filter {

  @Property(boolValue = CorsServletFilter.DEFAULT_ENABLED,
      label = "Enabled",
      description = "Enable the CORS Filter.")
  static final String PROPERTY_ENABLED = "enabled";
  static final boolean DEFAULT_ENABLED = true;

  @Property(boolValue = CorsServletFilter.DEFAULT_ALLOW_ALL_HOSTS,
      label = "Allow all hosts",
      description = "Always send '*' in the 'Access-Control-Allow-Origin' header. "
          + "If not enabled only hosts in the white list and not in the blacklist are accepted. "
          + "If both lists are empty, every origin is accepted and always included in the 'Access-Control-Allow-Origin' header.")
  static final String PROPERTY_ALLOW_ALL_HOSTS = "allowAllHosts";
  static final boolean DEFAULT_ALLOW_ALL_HOSTS = true;

  @Property(label = "Host Whitelist",
      description = "List of hosts (origins) allowed to access the resources protected by this filter.",
      cardinality = Integer.MAX_VALUE)
  static final String PROPERTY_HOST_WHITELIST = "hostWhitelist";

  @Property(label = "Host Blacklist",
      description = "List of hosts (origins) not allowed to access the resources protected by this filter.",
      cardinality = Integer.MAX_VALUE)
  static final String PROPERTY_HOST_BLACKLIST = "hostBlacklist";

  private boolean enabled;
  private boolean allowAllHosts;
  private Set<String> hostWhitelist;
  private Set<String> hostBlacklist;


  @Activate
  void activate(Map<String, Object> config) {
    enabled = PropertiesUtil.toBoolean(config.get(PROPERTY_ENABLED), DEFAULT_ENABLED);
    allowAllHosts = PropertiesUtil.toBoolean(config.get(PROPERTY_ALLOW_ALL_HOSTS), DEFAULT_ALLOW_ALL_HOSTS);

    String[] whitelist = PropertiesUtil.toStringArray(config.get(PROPERTY_HOST_WHITELIST), new String[0]);
    hostWhitelist = Streams.of(whitelist)
        .filter(StringUtils::isNotBlank)
        .collect(Collectors.toSet());

    String[] blacklist = PropertiesUtil.toStringArray(config.get(PROPERTY_HOST_BLACKLIST), new String[0]);
    hostBlacklist = Streams.of(blacklist)
        .filter(StringUtils::isNotBlank)
        .collect(Collectors.toSet());
  }

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
    // nothing to do
  }

  @Override
  public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
    if (!enabled) {
      chain.doFilter(servletRequest, servletResponse);
      return;
    }

    HttpServletRequest request = (HttpServletRequest)servletRequest;
    HttpServletResponse response = (HttpServletResponse)servletResponse;

    String origin = request.getHeader(HttpHeader.ORIGIN);
    if (StringUtils.isNotEmpty(origin)) {
      if (allowAllHosts) {
        response.setHeader(HttpHeader.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
      }
      else if ((hostWhitelist.isEmpty() || hostWhitelist.contains(origin))
          && (hostBlacklist.isEmpty() || !hostBlacklist.contains(origin))) {
        response.setHeader(HttpHeader.ACCESS_CONTROL_ALLOW_ORIGIN, origin);
        response.addHeader(HttpHeader.VARY, HttpHeader.ORIGIN);
      }
    }

    chain.doFilter(request, response);
  }

  @Override
  public void destroy() {
    // nothing to do
  }

}
