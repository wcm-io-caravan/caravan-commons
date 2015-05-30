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

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;

/**
 * Filter that returns the content of the /halbrowser/browser.html
 */
@Component(immediate = true)
@Service(Filter.class)
@Properties({
  @Property(name = "pattern", value = HalBrowserHttpServiceMounter.HALBROWSER_URI_PREFIX + "(/)?")
})
public class HalBrowserRootForwardFilter implements Filter {

  private static final String INDEX_PAGE_PATH = "/halbrowser-webapp/browser.html";

  private byte[] indexPageContent;

  @Override
  public void init(FilterConfig value) throws ServletException {
    try (InputStream is = getClass().getResourceAsStream(INDEX_PAGE_PATH)) {
      String markup = IOUtils.toString(is, "UTF-8");

      // inject base element to ensure correct loading of referenced resources
      markup = markup.replaceFirst("<head>",
          "<head><base href=\"" + HalBrowserHttpServiceMounter.HALBROWSER_URI_PREFIX + "/browser.html\">");

      indexPageContent = markup.getBytes("UTF-8");
    }
    catch (IOException ex) {
      throw new ServletException("Unable to get content from: " + INDEX_PAGE_PATH, ex);
    }
  }

  @Override
  public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
    HttpServletResponse response = (HttpServletResponse)servletResponse;

    response.setStatus(HttpServletResponse.SC_OK);
    response.setContentType("text/html");
    response.setContentLength(indexPageContent.length);
    response.getOutputStream().write(indexPageContent);
  }

  @Override
  public void destroy() {
    // nothing to do
  }

}
