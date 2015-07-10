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

import org.apache.commons.lang3.StringUtils;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Reference;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;
import org.osgi.util.tracker.BundleTracker;
import org.osgi.util.tracker.BundleTrackerCustomizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(immediate = true)
public class JsonSchemaBundleTracker implements BundleTrackerCustomizer<String> {

  static final String HEADER_DOMAIN_PATH = "Caravan-HalDocs-DomainPath";
  static final String SCHEMA_URI_PREFIX = "/docs/json-schema";
  static final String SCHEMA_CLASSPATH_PREFIX = "JSON-SCHEMA-INF";

  private static final Logger log = LoggerFactory.getLogger(JsonSchemaBundleTracker.class);

  private BundleContext bundleContext;
  private BundleTracker bundleTracker;

  @Reference
  private HttpService httpService;

  @Activate
  void activate(ComponentContext componentContext) {
    bundleContext = componentContext.getBundleContext();
    this.bundleTracker = new BundleTracker<String>(bundleContext, Bundle.ACTIVE, this);
    this.bundleTracker.open();
  }

  @Deactivate
  void deactivate(ComponentContext componentContext) {
    this.bundleTracker.close();
  }

  @Override
  public String addingBundle(Bundle bundle, BundleEvent event) {
    String domainPath = getHalDocsDomainPath(bundle);
    if (StringUtils.isNotBlank(domainPath)) {
      String schemaPath = getDocsPath(domainPath);

      if (log.isInfoEnabled()) {
        log.info("Mount JSON schema files for {} to {}", bundle.getSymbolicName(), schemaPath);
      }

      try {
        httpService.registerResources(schemaPath, SCHEMA_CLASSPATH_PREFIX,
            new HttpContextWrapper(httpService.createDefaultHttpContext(), bundle));
      }
      catch (NamespaceException ex) {
        throw new RuntimeException("Unable to mount JSON schema files to " + schemaPath, ex);
      }
      return schemaPath;
    }
    return null;
  }

  @Override
  public void modifiedBundle(Bundle bundle, BundleEvent event, String schemaPath) {
    // nothing to do
  }

  @Override
  public void removedBundle(Bundle bundle, BundleEvent event, String schemaPath) {
    if (schemaPath == null) {
      return;
    }
    if (log.isInfoEnabled()) {
      log.info("Unmount JSON schema files for {} from {}", bundle.getSymbolicName(), schemaPath);
    }
    httpService.unregister(schemaPath);
  }

  private String getHalDocsDomainPath(Bundle bundle) {
    return bundle.getHeaders().get(HEADER_DOMAIN_PATH);
  }

  private String getDocsPath(String domainPath) {
    return SCHEMA_URI_PREFIX + domainPath;
  }

}