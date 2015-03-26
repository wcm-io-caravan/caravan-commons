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
package io.wcm.caravan.commons.hal.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Bean representation of a HAL link.
 */
public class Link implements HalObject {

  private final ObjectNode model;

  /**
   * @param model
   */
  public Link(ObjectNode model) {
    this.model = model;
  }

  @Override
  public ObjectNode getModel() {
    return model;
  }

  /**
   * @return the type
   */
  public String getType() {
    return model.path("type").asText();
  }

  /**
   * @param type the type to set
   * @return Link
   */
  public Link setType(String type) {
    model.put("type", type);
    return this;
  }

  /**
   * @return the deprecation
   */
  public String getDeprecation() {
    return model.path("deprecation").asText();
  }

  /**
   * @param deprecation the deprecation to set
   * @return Link
   */
  public Link setDeprecation(String deprecation) {
    model.put("deprecation", deprecation);
    return this;
  }

  /**
   * @return the name
   */
  public String getName() {
    return model.path("name").asText();
  }

  /**
   * @param name the name to set
   * @return Link
   */
  public Link setName(String name) {
    model.put("name", name);
    return this;
  }

  /**
   * @return the profile
   */
  public String getProfile() {
    return model.path("profile").asText();
  }

  /**
   * @param profile the profile to set
   * @return Link
   */
  public Link setProfile(String profile) {
    model.put("profile", profile);
    return this;
  }

  /**
   * @return the title
   */
  public String getTitle() {
    return model.path("title").asText();
  }

  /**
   * @param title the title to set
   * @return Link
   */
  public Link setTitle(String title) {
    model.put("title", title);
    return this;
  }

  /**
   * @return the hreflang
   */
  public String getHreflang() {
    return model.path("hreflang").asText();
  }

  /**
   * @param hreflang the hreflang to set
   * @return Link
   */
  public Link setHreflang(String hreflang) {
    model.put("hreflang", hreflang);
    return this;
  }

  /**
   * @return the href
   */
  public String getHref() {
    return model.path("href").asText();
  }

  /**
   * @param href the href to set
   * @return Link
   */
  public Link setHref(String href) {
    model.put("href", href);
    return this;
  }

  /**
   * @return is templated
   */
  public boolean isTemplated() {
    return model.path("templated").asBoolean();
  }

  /**
   * @param templated the templated to set
   * @return Link
   */
  public Link setTemplated(boolean templated) {
    model.put("templated", templated);
    return this;
  }

  @Override
  public int hashCode() {
    return HashCodeBuilder.reflectionHashCode(this, false);
  }

  @Override
  public boolean equals(Object obj) {
    return EqualsBuilder.reflectionEquals(this, obj);
  }

}
