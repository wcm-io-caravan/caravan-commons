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
import io.wcm.caravan.commons.stream.Collectors;
import io.wcm.caravan.commons.stream.Streams;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * Augments a HAL resource by CURI documentation links. Doesn't override existing CURI links in a HAL resource.
 */
public class HalCuriAugmenter {

  /**
   * HAL specific relation for CURI links.
   */
  public static final String LINK_RELATION_CURIES = "curies";

  /**
   * HAL specific separator for CURI names and relation.
   */
  private static final String LINK_RELATION_SEPARATOR = ":";

  private Map<String, Link> registry = Maps.newHashMap();

  /**
   * Registers a CURI link by the given name and HREF.
   * @param name CURI link name
   * @param href Link URI
   * @return This augmenter
   */
  public HalCuriAugmenter register(String name, String href) {
    Link link = HalResourceFactory.createLink(href).setName(name);
    return register(link);
  }

  /**
   * Registers a CURI link.
   * @param link CURI link
   * @return This augmenter
   */
  public HalCuriAugmenter register(Link link) {
    registry.put(link.getName(), link);
    return this;
  }

  /**
   * Unregisters a CURI link.
   * @param name CURI name
   * @return This augmenter
   */
  public HalCuriAugmenter unregister(String name) {
    registry.remove(name);
    return this;
  }

  /**
   * Returns the CURI link for a given CURI name.
   * @param name CURI name
   * @return CURI link or {@code null} if missing
   */
  public Link get(String name) {
    return registry.get(name);
  }

  /**
   * @param name CURI name
   * @return True if there is a CURI link registered for the given CURI name
   */
  public boolean has(String name) {
    return registry.containsKey(name);
  }

  /**
   * Augments a HAL resource by CURI links. Only adds CURIES being registered and referenced in the HAL resource. Will not override existing CURI links.
   * @param hal HAL resource to augment
   * @return This augmenter
   */
  public HalCuriAugmenter augment(HalResource hal) {

    Set<String> existingCurieNames = getExistingCurieNames(hal);
    Set<Link> curieLinks = getCurieLinks(hal, existingCurieNames);
    hal.addLinks(LINK_RELATION_CURIES, curieLinks);
    return this;

  }

  private Set<String> getExistingCurieNames(HalResource hal) {

    if (!hal.hasLink(LINK_RELATION_CURIES)) {
      return Collections.emptySet();
    }
    return Streams.of(hal.getLink(LINK_RELATION_CURIES))
        .map(link -> link.getName())
        .collect(Collectors.toSet());

  }

  private Set<Link> getCurieLinks(HalResource hal, Set<String> existingCurieNames) {

    Set<Link> curiLinks = Sets.newLinkedHashSet();

    Streams.of(hal.getLinks().keySet())
        // get CURI name for relation
        .map(relation -> getCurieName(relation))
        // filter CURIE being empty or exist in HAL resource
        .filter(curieName -> StringUtils.isNotEmpty(curieName) && !existingCurieNames.contains(curieName))
        // get link for CURI name
        .map(curieName -> registry.get(curieName))
        // filter non existing links
        .filter(link -> link != null)
        // add distinct links
        .forEach(link -> curiLinks.add(link));

    return curiLinks;

  }

  private String getCurieName(String relation) {
    String[] tokens = StringUtils.split(relation, LINK_RELATION_SEPARATOR, 2);
    return ArrayUtils.isEmpty(tokens) ? null : tokens[0];
  }

}
