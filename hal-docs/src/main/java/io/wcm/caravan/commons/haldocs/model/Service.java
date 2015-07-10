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

import io.wcm.caravan.commons.stream.Streams;

import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.ImmutableList;

/**
 * Describes a HAL RESTful service.
 */
public class Service {

  private String serviceId;
  private String name;
  private String descriptionMarkup;
  private SortedSet<LinkRelation> linkRelations = new TreeSet<>();

  public String getServiceId() {
    return this.serviceId;
  }

  public void setServiceId(String serviceId) {
    this.serviceId = serviceId;
  }

  public String getName() {
    return this.name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescriptionMarkup() {
    return this.descriptionMarkup;
  }

  public void setDescriptionMarkup(String descriptionMarkup) {
    this.descriptionMarkup = descriptionMarkup;
  }

  public List<LinkRelation> getLinkRelations() {
    return ImmutableList.copyOf(this.linkRelations);
  }

  /**
   * @param linkRelation Link relation to add
   */
  public void addLinkRelation(LinkRelation linkRelation) {
    this.linkRelations.add(linkRelation);
  }

  /**
   * Resolves all nested link relations.
   */
  public void resolve() {
    Streams.of(linkRelations).forEach(this::resolve);
  }

  /**
   * Iterates over nested link relations. If they are valid filename is set and decription inherited (if empty).
   * Invalid ones are removed.
   * @param rel Link relations
   */
  private void resolve(LinkRelation rel) {
    Iterator<LinkRelationRef> refs = rel.getNestedLinkRelations().iterator();
    while (refs.hasNext()) {
      LinkRelationRef ref = refs.next();
      LinkRelation referencedRel = Streams.of(linkRelations)
          .filter(item -> StringUtils.equals(item.getRel(), ref.getRel()))
          .findFirst().orElse(null);
      if (referencedRel == null) {
        refs.remove();
      }
      else {
        if (StringUtils.isBlank(ref.getDescriptionMarkup())) {
          ref.setDescriptionMarkup(referencedRel.getDescriptionMarkup());
        }
      }
    }
  }

}
