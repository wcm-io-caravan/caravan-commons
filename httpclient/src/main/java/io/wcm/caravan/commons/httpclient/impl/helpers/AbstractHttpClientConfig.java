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
package io.wcm.caravan.commons.httpclient.impl.helpers;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.jetbrains.annotations.NotNull;

import io.wcm.caravan.commons.httpclient.HttpClientConfig;

/**
 * Common functionality for {@link HttpClientConfig} implementations.
 */
public abstract class AbstractHttpClientConfig implements HttpClientConfig {

  /**
   * List of properties of this class that contain sensitive information which should not be logged.
   */
  static final Set<String> SENSITIVE_PROPERTY_NAMES = new HashSet<>(Arrays.asList(
    "httpPassword",
    "proxyPassword",
    "keyStorePassword",
    "trustStorePassword"
  ));

  @Override
  public @NotNull String toString() {
    ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE);
    Map<String, Object> properties = BeanUtil.getMaskedBeanProperties(this, SENSITIVE_PROPERTY_NAMES);
    for (Map.Entry<String, Object> entry : properties.entrySet()) {
      if (entry.getValue() != null) {
        builder.append(entry.getKey(), entry.getValue());
      }
    }
    return builder.toString();
  }

}
