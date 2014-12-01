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
package io.wcm.dromas.commons.jsonpath.impl;

import java.util.EnumSet;
import java.util.Set;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.internal.spi.json.JacksonJsonNodeJsonProvider;
import com.jayway.jsonpath.internal.spi.mapper.JacksonMappingProvider;
import com.jayway.jsonpath.spi.json.JsonProvider;
import com.jayway.jsonpath.spi.mapper.MappingProvider;

/**
 * Default JsonPath configuration to use Jackson.
 */
public final class JsonPathDefaultConfig implements Configuration.Defaults {

  /**
   * Singleton.
   */
  public static final JsonPathDefaultConfig INSTANCE = new JsonPathDefaultConfig();

  private final JsonProvider jsonProvider = new JacksonJsonNodeJsonProvider();
  private final MappingProvider mappingProvider = new JacksonMappingProvider();
  private final Set<Option> options = EnumSet.noneOf(Option.class);

  private JsonPathDefaultConfig() {
    // singleton
  }

  @Override
  public JsonProvider jsonProvider() {
    return jsonProvider;
  }

  @Override
  public MappingProvider mappingProvider() {
    return mappingProvider;
  }

  @Override
  public Set<Option> options() {
    return options;
  }

}
