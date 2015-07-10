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

import io.wcm.caravan.commons.haldocs.model.LinkRelation;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.Map;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;
import org.osgi.framework.Bundle;
import org.osgi.service.component.ComponentContext;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.io.AbstractTemplateLoader;
import com.github.jknack.handlebars.io.TemplateSource;
import com.github.jknack.handlebars.io.URLTemplateSource;
import com.google.common.collect.ImmutableMap;

/**
 * Renders HTML views for HAL documentation.
 */
@Component(immediate = true)
@Service(TemplateRenderer.class)
public class TemplateRenderer {

  private static final String CLASSPATH_TEMPLATES = "HALDOCS-TEMPLATE-INF/templates";

  private Handlebars handlebars;
  private Template serviceTemplate;
  private Template linkRelationTemplate;

  @Activate
  void activate(ComponentContext componentContext) throws IOException {
    Bundle bundle = componentContext.getBundleContext().getBundle();
    this.handlebars = new Handlebars(new BundleTemplateLoader(bundle));
    this.serviceTemplate = handlebars.compile("service.html.hbs");
    this.linkRelationTemplate = handlebars.compile("linkRelation.html.hbs");
  }

  /**
   * Generate HTML file for service.
   * @param service Service
   * @return Rendered markup
   * @throws IOException
   */
  public String renderServiceHtml(io.wcm.caravan.commons.haldocs.model.Service service) throws IOException {
    Map<String, Object> model = ImmutableMap.<String, Object>builder()
        .put("service", service)
        .build();
    return render(service, model, serviceTemplate);
  }

  /**
   * Generate HTML file for link relation.
   * @param service Service
   * @param linkRelation Link relation
   * @return Rendered markup
   * @throws IOException
   */
  public String renderLinkRelationHtml(io.wcm.caravan.commons.haldocs.model.Service service, LinkRelation linkRelation)
      throws IOException {
    Map<String, Object> model = ImmutableMap.<String, Object>builder()
        .put("service", service)
        .put("linkRelation", linkRelation)
        .build();
    return render(service, model, linkRelationTemplate);
  }

  /**
   * Generate templated file with handlebars
   * @param model Model
   * @param template Template
   * @return Rendered markup
   */
  private String render(io.wcm.caravan.commons.haldocs.model.Service service, Map<String, Object> model,
      Template template) throws IOException {
    Map<String, Object> mergedModel = ImmutableMap.<String, Object>builder()
        .putAll(model)
        .put("docsContext", ImmutableMap.<String, Object>builder()
            .put("baseUrl", HalDocsBundleTracker.DOCS_URI_PREFIX + service.getServiceId() + "/")
            .put("resourcesPath", HalDocsBundleTracker.DOCS_RESOURCES_URI_PREFIX)
            .build())
            .build();
    return template.apply(mergedModel);
  }

  /**
   * Loads Handlebars templates from bundle.
   */
  private static class BundleTemplateLoader extends AbstractTemplateLoader {

    private final Bundle bundle;

    public BundleTemplateLoader(Bundle bundle) {
      this.bundle = bundle;
    }

    @Override
    public TemplateSource sourceAt(String location) throws IOException {
      URL resource = bundle.getResource(CLASSPATH_TEMPLATES + "/" + location);
      if (resource == null) {
        throw new FileNotFoundException(location);
      }
      return new URLTemplateSource(location, resource);
    }

  }

}
