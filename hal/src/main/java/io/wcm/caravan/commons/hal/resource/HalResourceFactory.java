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
package io.wcm.caravan.commons.hal.resource;

import java.util.regex.Pattern;

import org.osgi.annotation.versioning.ProviderType;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Factory for HAL {@link HalResource}s.
 */
@ProviderType
public final class HalResourceFactory {

  /**
   * JSON object mapper
   */
  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
      .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

  /**
   * Pattern that will hit an RFC 6570 URI template.
   */
  private static final Pattern URI_TEMPLATE_PATTERN = Pattern.compile("\\{.+\\}");

  private HalResourceFactory() {
    // nothing to do
  }

  /**
   * Converts any object into a JSON {@link ObjectNode}.
   * @param input Any object
   * @return JSON object node
   */
  public static ObjectNode convert(Object input) {
    return OBJECT_MAPPER.convertValue(input, ObjectNode.class);
  }

  /**
   * Creates a HAL link with the given HREF.
   * @param href Link HREF
   * @return Link
   */
  public static Link createLink(String href) {
    Link link = new Link(OBJECT_MAPPER.createObjectNode()).setHref(href);

    if (href != null && URI_TEMPLATE_PATTERN.matcher(href).find()) {
      link.setTemplated(true);
    }

    return link;
  }

  /**
   * Creates a HAL resource with empty state but a self link. Mostly needed for index resources.
   * @param href The self HREF for the resource
   * @return New HAL resource
   */
  public static HalResource createResource(String href) {
    return createResource(OBJECT_MAPPER.createObjectNode(), href);
  }

  /**
   * Creates a HAL resource with state and a self link.
   * @param model The state of the resource
   * @param href The self link for the resource
   * @return New HAL resource
   */
  public static HalResource createResource(Object model, String href) {
    return createResource(convert(model), href);
  }

  /**
   * Creates a HAL resource with state and a self link.
   * @param model The state of the resource
   * @param href The self link for the resource
   * @return New HAL resource
   */
  public static HalResource createResource(ObjectNode model, String href) {
    HalResource resource = new HalResource(model);

    if (href != null) {
      resource.setLink(createLink(href));
    }

    return resource;
  }

  /**
   * Converts the JSON model to an object of the given type.
   * @param halResource HAL resource with model to convert
   * @param type Type of the requested object
   * @param <T> Output type
   * @return State as object
   */
  public static <T> T getStateAsObject(HalResource halResource, Class<T> type) {
    return HalResourceFactory.OBJECT_MAPPER.convertValue(halResource.getModel(), type);
  }

}
