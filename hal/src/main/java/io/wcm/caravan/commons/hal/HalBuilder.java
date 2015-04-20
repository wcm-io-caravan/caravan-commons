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
package io.wcm.caravan.commons.hal;

import io.wcm.caravan.commons.hal.domain.HalResource;
import io.wcm.caravan.commons.hal.domain.Link;
import io.wcm.caravan.commons.hal.mapper.ResourceMapper;

import java.util.List;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Iterables;

/**
 * Short named helper for HAL resources.
 */
public class HalBuilder {

  private final HalResource instance;

  /**
   * @see HalResourceFactory#createResource(String)
   * @param href Link HREF
   */
  public HalBuilder(String href) {
    instance = HalResourceFactory.createResource(href);
  }

  /**
   * @see HalResourceFactory#createResource(String)
   * @param href Link HREF
   * @param title Link title
   */
  public HalBuilder(String href, String title) {
    this(href);
    instance.getLinks("self").get(0).setTitle(title);
  }

  /**
   * @see HalResourceFactory#createResource(String)
   * @param href Link HREF
   * @param title Link title
   * @param name Link name
   */
  public HalBuilder(String href, String title, String name) {
    this(href, title);
    instance.getLinks("self").get(0).setName(name);
  }

  /**
   * @see HalResourceFactory#createResource(ObjectNode, String)
   * @param state Resource state
   * @param href Link HREF
   */
  public HalBuilder(Object state, String href) {
    instance = HalResourceFactory.createResource(state, href);
  }

  /**
   * @see HalResourceFactory#createResource(ObjectNode, String)
   * @param state Resource state
   * @param href Link HREF
   */
  public HalBuilder(ObjectNode state, String href) {
    instance = HalResourceFactory.createResource(state, href);
  }

  /**
   * @see HalResourceFactory#createResource(Object, ResourceMapper)
   * @param input Resource pre-mapped state
   * @param mapper Resource state mapper
   */
  public HalBuilder(Object input, ResourceMapper<?> mapper) {
    instance = HalResourceFactory.createResource(input, mapper);
  }

  /**
   * @see HalResourceFactory#createEmbeddedResource(Object, ResourceMapper)
   * @see HalResource#addEmbedded(String, HalResource...)
   * @param name Embedded resource name
   * @param input Embedded resource pre-mapped state
   * @param mapper Embedded resource state mapper
   * @return Helper
   */
  public HalBuilder embed(String name, Object input, ResourceMapper<?> mapper) {
    HalResource embeddedResource = HalResourceFactory.createEmbeddedResource(input, mapper);
    instance.addEmbedded(name, embeddedResource);
    return this;
  }

  /**
   * @see HalResourceFactory#createEmbeddedResources(Iterable, ResourceMapper)
   * @see HalResource#addEmbedded(String, HalResource...)
   * @param name Embedded resources name
   * @param inputs Embedded resources pre-mapped state
   * @param mapper Embedded resources state mapper
   * @return Helper
   */
  public HalBuilder embedAll(String name, Iterable<?> inputs, ResourceMapper<?> mapper) {
    List<HalResource> embeddedResource = HalResourceFactory.createEmbeddedResources(inputs, mapper);
    instance.addEmbedded(name, Iterables.toArray(embeddedResource, HalResource.class));
    return this;
  }

  /**
   * @see HalResource#setLink(String, Link)
   * @param relation Link relation
   * @param href Link HREF
   * @return Helper
   */
  public HalBuilder link(String relation, String href) {
    instance.setLink(relation, HalResourceFactory.createLink(href));
    return this;
  }

  /**
   * @see HalResource#setLink(String, Link)
   * @param relation Link relation
   * @param href Link HREF
   * @param title Link title
   * @return Helper
   */
  public HalBuilder link(String relation, String href, String title) {
    instance.setLink(relation, HalResourceFactory.createLink(href).setTitle(title));
    return this;
  }

  /**
   * @see HalResource#setLink(String, Link)
   * @param relation Link relation
   * @param href Link HREF
   * @param title Link title
   * @param name Link name
   * @return Helper
   */
  public HalBuilder link(String relation, String href, String title, String name) {
    instance.setLink(relation, HalResourceFactory.createLink(href).setName(name).setTitle(title));
    return this;
  }

  /**
   * @see HalResource#addLinks(String, Link...)
   * @param href Link HREF
   * @param name Link name
   * @return Helper
   */
  public HalBuilder curi(String href, String name) {
    return link("curies", href, null, name);
  }

  /**
   * @return The HAL resource
   */
  public HalResource build() {
    return instance;
  }

}
