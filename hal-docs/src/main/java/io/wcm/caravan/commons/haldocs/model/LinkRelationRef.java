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
package io.wcm.caravan.commons.haldocs.model;

import org.apache.commons.lang3.StringUtils;

/**
 * Link relation reference for nested link relations.
 */
public class LinkRelationRef implements Comparable<LinkRelationRef> {

  private String rel;
  private String descriptionMarkup;
  private String filename;

  public String getRel() {
    return this.rel;
  }

  public void setRel(String rel) {
    this.rel = rel;
  }

  public String getDescriptionMarkup() {
    return this.descriptionMarkup;
  }

  public void setDescriptionMarkup(String descriptionMarkup) {
    this.descriptionMarkup = descriptionMarkup;
  }

  public String getFilename() {
    return this.filename;
  }

  public void setFilename(String filename) {
    this.filename = filename;
  }

  @Override
  public int hashCode() {
    return rel.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof LinkRelationRef)) {
      return false;
    }
    return StringUtils.equals(rel, ((LinkRelationRef)obj).rel);
  }

  @Override
  public int compareTo(LinkRelationRef o) {
    return StringUtils.defaultString(rel).compareTo(o.getRel());
  }

}
