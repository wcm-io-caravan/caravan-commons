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

import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.lang3.StringUtils;

/**
 * Describes a HAL link relation.
 */
public class LinkRelation implements Comparable<LinkRelation> {

  private String rel;
  private String descriptionMarkup;
  private String jsonSchemaRef;
  private SortedSet<LinkRelationRef> nestedRels = new TreeSet<>();

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

  public String getJsonSchemaRef() {
    return this.jsonSchemaRef;
  }

  public void setJsonSchemaRef(String jsonSchemaRef) {
    this.jsonSchemaRef = jsonSchemaRef;
  }

  /**
   * @return Get link relations of embedded resources.
   */
  public Set<LinkRelationRef> getNestedLinkRelations() {
    return nestedRels;
  }

  /**
   * @param nestedRel Link relation name.
   * @param nestedDescription Optional description for describing the link relation in context of the parent link relation.
   */
  public void addNestedLinkRelation(String nestedRel, String nestedDescription) {
    LinkRelationRef ref = new LinkRelationRef();
    ref.setRel(nestedRel);
    ref.setDescriptionMarkup(nestedDescription);
    this.nestedRels.add(ref);
  }

  @Override
  public int hashCode() {
    return rel.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof LinkRelation)) {
      return false;
    }
    return StringUtils.equals(rel, ((LinkRelation)obj).rel);
  }

  @Override
  public int compareTo(LinkRelation o) {
    return StringUtils.defaultString(rel).compareTo(o.getRel());
  }

}
