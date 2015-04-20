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
package io.wcm.caravan.commons.hal.mapper;

import org.osgi.annotation.versioning.ConsumerType;

import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Transforms the input data for a HAL resource into a full or embedded resource. Further generates the HREF for the
 * resource.
 * @param <R> Resource input type
 */
@ConsumerType
public interface ResourceMapper<R> {

  /**
   * @param resource The input resource
   * @return The HREF for the resource
   */
  String getHref(final R resource);

  /**
   * @param resource The input resource
   * @return The embedded resource representation of the resource
   */
  ObjectNode getEmbeddedResource(final R resource);

  /**
   * @param resource The input resource
   * @return The full resource representation of the resource
   */
  ObjectNode getResource(final R resource);

}
