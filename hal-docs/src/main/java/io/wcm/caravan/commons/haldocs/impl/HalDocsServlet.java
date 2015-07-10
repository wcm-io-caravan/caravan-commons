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
package io.wcm.caravan.commons.haldocs.impl;

import static io.wcm.caravan.commons.haldocs.impl.HalDocsBundleTracker.DOCS_CLASSPATH_PREFIX;
import static io.wcm.caravan.commons.haldocs.impl.HalDocsBundleTracker.SERVICE_DOC_FILE;
import io.wcm.caravan.commons.haldocs.model.LinkRelation;
import io.wcm.caravan.commons.stream.Streams;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.CharEncoding;
import org.apache.commons.lang3.StringUtils;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.osgi.framework.Bundle;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Serves HAL documentation pages generated from JSON service documentation models with handlebars.
 */
@Component(factory = HalDocsServlet.FACTORY)
@Service(Servlet.class)
public class HalDocsServlet extends HttpServlet {
  private static final long serialVersionUID = 1L;

  static final String FACTORY = "caravan.haldocs.servlet.factory";
  static final String PROPERTY_BUNDLE = "caravan.haldocs.relatedBundle";

  private io.wcm.caravan.commons.haldocs.model.Service serviceModel;

  @Reference
  private TemplateRenderer templateRenderer;

  private static final Logger log = LoggerFactory.getLogger(HalDocsServlet.class);

  @Activate
  void activate(ComponentContext componentContext) {
    // bundle which contains the JAX-RS services
    Bundle bundle = (Bundle)componentContext.getProperties().get(PROPERTY_BUNDLE);
    String resourcePath = DOCS_CLASSPATH_PREFIX + "/" + SERVICE_DOC_FILE;
    try (InputStream is = bundle.getResource(resourcePath).openStream()) {
      serviceModel = ServiceJson.read(is);
    }
    catch (Throwable ex) {
      log.error("Unable to parse JSON file " + resourcePath + " from bundle " + bundle.getSymbolicName());
    }
  }

  @Override
  public void service(ServletRequest servletRequest, ServletResponse servletResponse) throws ServletException, IOException {
    HttpServletRequest request = (HttpServletRequest)servletRequest;
    HttpServletResponse response = (HttpServletResponse)servletResponse;

    if (serviceModel != null) {
      try {
        String uri = StringUtils.defaultString(request.getPathInfo());
        if (StringUtils.equals(uri, "/")) {
          sendMarkup(templateRenderer.renderServiceHtml(serviceModel), response);
          return;
        }
        else {
          String rel = StringUtils.substringAfter(uri, "/");
          LinkRelation linkRelation = Streams.of(serviceModel.getLinkRelations())
              .filter(item -> StringUtils.equals(item.getRel(), rel))
              .findFirst().orElse(null);
          if (linkRelation != null) {
            sendMarkup(templateRenderer.renderLinkRelationHtml(serviceModel, linkRelation), response);
            return;
          }
        }
      }
      catch (Throwable ex) {
        log.error("Error rendering HAL docs for " + serviceModel.getServiceId() + ": " + request.getPathInfo(), ex);
        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ex.getMessage());
      }
    }

    response.sendError(HttpServletResponse.SC_NOT_FOUND);
  }

  private void sendMarkup(String markup, HttpServletResponse response) throws IOException {
    byte[] data = markup.getBytes(CharEncoding.UTF_8);
    response.setContentType(HttpContextWrapper.MIMETYPE_HTML);
    response.setContentLength(data.length);
    response.getOutputStream().write(data);
  }

  @Override
  public void init() throws ServletException {
    // nothing to do
  }

  @Override
  public void init(ServletConfig config) throws ServletException {
    // nothing to do
  }

  @Override
  public void destroy() {
    // nothing to do
  }

}
