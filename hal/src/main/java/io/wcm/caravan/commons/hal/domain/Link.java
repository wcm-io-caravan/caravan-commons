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

import java.util.regex.Pattern;

/**
 * Bean representation of a link for a resource.
 */
public class Link {

  /**
   * Pattern that will hit an RFC 6570 URI template.
   */
  private static final Pattern URI_TEMPLATE_PATTERN = Pattern.compile("\\{.+\\}");

  private final String href;
  private final boolean templated;
  private String type;
  private String deprecation;
  private String name;
  private String profile;
  private String title;
  private String hreflang;

  /**
   * @param href The URI to the resource
   */
  public Link(final String href) {
    this.href = href;
    this.templated = href != null && URI_TEMPLATE_PATTERN.matcher(href).find();
  }

  /**
   * @return True if URI is templated
   */
  public boolean isTemplated() {
    return this.templated;
  }

  /**
   * @return the type
   */
  public String getType() {
    return this.type;
  }

  /**
   * @param type the type to set
   * @return Link
   */
  public Link setType(String type) {
    this.type = type;
    return this;
  }

  /**
   * @return the deprecation
   */
  public String getDeprecation() {
    return this.deprecation;
  }

  /**
   * @param deprecation the deprecation to set
   * @return Link
   */
  public Link setDeprecation(String deprecation) {
    this.deprecation = deprecation;
    return this;
  }

  /**
   * @return the name
   */
  public String getName() {
    return this.name;
  }

  /**
   * @param name the name to set
   * @return Link
   */
  public Link setName(String name) {
    this.name = name;
    return this;
  }

  /**
   * @return the profile
   */
  public String getProfile() {
    return this.profile;
  }

  /**
   * @param profile the profile to set
   * @return Link
   */
  public Link setProfile(String profile) {
    this.profile = profile;
    return this;
  }

  /**
   * @return the title
   */
  public String getTitle() {
    return this.title;
  }

  /**
   * @param title the title to set
   * @return Link
   */
  public Link setTitle(String title) {
    this.title = title;
    return this;
  }

  /**
   * @return the hreflang
   */
  public String getHreflang() {
    return this.hreflang;
  }

  /**
   * @param hreflang the hreflang to set
   * @return Link
   */
  public Link setHreflang(String hreflang) {
    this.hreflang = hreflang;
    return this;
  }

  /**
   * @return the href
   */
  public String getHref() {
    return this.href;
  }

}
