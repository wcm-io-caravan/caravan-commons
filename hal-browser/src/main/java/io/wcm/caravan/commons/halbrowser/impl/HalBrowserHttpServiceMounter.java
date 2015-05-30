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

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Reference;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;

/**
 * Registers HAL Browser static webapp in HTTP service.
 */
@Component
public class HalBrowserHttpServiceMounter {

  static final String HALBROWSER_URI_PREFIX = "/hal";
  static final String HALBROWSER_URI_PREFIX_LEGACY = "/system/halbrowser";

  @Reference
  private HttpService httpService;

  @Activate
  protected void activate() throws NamespaceException {
    httpService.registerResources(HALBROWSER_URI_PREFIX, "/halbrowser-webapp", null);
    httpService.registerResources(HALBROWSER_URI_PREFIX_LEGACY, "/halbrowser-legacy-redirect", null);
  }

  @Deactivate
  protected void deactivate() {
    httpService.unregister(HALBROWSER_URI_PREFIX);
    httpService.unregister(HALBROWSER_URI_PREFIX_LEGACY);
  }

}
