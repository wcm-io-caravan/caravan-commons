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
package io.wcm.caravan.commons.test;

import io.wcm.caravan.commons.jsonpath.impl.JsonPathDefaultConfig;
import net.javacrumbs.jsonunit.core.Configuration;
import net.javacrumbs.jsonunit.core.Option;

/**
 * Configuration for the test module. Contains helping classes and initializes JSON path to use Jackson.
 */
public final class TestConfiguration {

  /**
   * JSON unit configuration with non-strict settings.
   */
  public static final Configuration JSONUNIT_CONF = Configuration.empty().withOptions(Option.IGNORING_EXTRA_FIELDS, Option.TREATING_NULL_AS_ABSENT);

  private TestConfiguration() {
    // nothing to do
  }

  /**
   * Initializes JSON path to use Jackson mapper.
   */
  public static void init() {
    com.jayway.jsonpath.Configuration.setDefaults(JsonPathDefaultConfig.INSTANCE);
  }

}
