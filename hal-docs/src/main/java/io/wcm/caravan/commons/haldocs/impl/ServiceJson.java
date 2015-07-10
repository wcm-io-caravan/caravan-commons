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

import io.wcm.caravan.commons.haldocs.model.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Manages serialization and deserialization of service JSON files.
 */
public final class ServiceJson {

  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
  static {
    // ensure only field serialization is used for jackson
    OBJECT_MAPPER.setVisibilityChecker(OBJECT_MAPPER.getSerializationConfig().getDefaultVisibilityChecker()
        .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
        .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
        .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
        .withCreatorVisibility(JsonAutoDetect.Visibility.NONE));
  }

  private ServiceJson() {
    // static methods only
  }

  /**
   * Write model to stream.
   * @param model Model
   * @param os Stream
   * @throws IOException
   */
  public static void write(Service model, OutputStream os) throws IOException {
    OBJECT_MAPPER.writeValue(os, model);
  }

  /**
   * Read model from stream
   * @param is stream
   * @return Model
   * @throws IOException
   */
  public static Service read(InputStream is) throws IOException {
    return OBJECT_MAPPER.readValue(is, Service.class);
  }

}
