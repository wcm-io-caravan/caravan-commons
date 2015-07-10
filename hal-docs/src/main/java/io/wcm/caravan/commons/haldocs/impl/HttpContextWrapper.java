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

import java.io.IOException;
import java.net.URL;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.CharEncoding;
import org.osgi.framework.Bundle;
import org.osgi.service.http.HttpContext;

import com.google.common.collect.ImmutableMap;

/**
 * Wraps a defaul HTTP context, but redirects all resource requests to the bundle given in contructor.
 */
class HttpContextWrapper implements HttpContext {

  static final String MIMETYPE_JSON = "application/json";
  static final String MIMETYPE_HTML = "text/html;charset=" + CharEncoding.UTF_8;

  /**
   * List of common mime types we should support always.
   */
  private static final Map<String, String> MIME_TYPES = ImmutableMap.<String, String>builder()
      .put("json", MIMETYPE_JSON)
      .put("html", MIMETYPE_HTML)
      .build();

  private final HttpContext delegate;
  private final Bundle bundle;

  public HttpContextWrapper(HttpContext httpContext, Bundle bundle) {
    this.delegate = httpContext;
    this.bundle = bundle;
  }

  @Override
  public boolean handleSecurity(HttpServletRequest request, HttpServletResponse response) throws IOException {
    return delegate.handleSecurity(request, response);
  }

  @Override
  public URL getResource(String name) {
    return bundle.getResource(name);
  }

  @Override
  public String getMimeType(String name) {
    String mimeType = delegate.getMimeType(name);
    if (mimeType == null) {
      String extension = FilenameUtils.getExtension(name);
      if (extension != null) {
        mimeType = MIME_TYPES.get(extension);
      }
    }
    return mimeType;
  }

}
