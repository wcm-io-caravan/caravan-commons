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

import io.wcm.caravan.commons.hal.resource.HalResource;
import io.wcm.caravan.commons.hal.resource.HalResourceFactory;
import io.wcm.caravan.commons.hal.resource.Link;

import org.osgi.annotation.versioning.ProviderType;

import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Short named helper for HAL resources.
 */
@ProviderType
public final class HalBuilder {

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
    instance.addLinks("curies", HalResourceFactory.createLink(href).setName(name));
    return this;
  }

  /**
   * @return The HAL resource
   */
  public HalResource build() {
    return instance;
  }

}
