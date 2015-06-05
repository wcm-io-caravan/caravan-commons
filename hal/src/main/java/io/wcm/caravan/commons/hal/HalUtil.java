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
import io.wcm.caravan.commons.hal.resource.Link;
import io.wcm.caravan.commons.stream.Collectors;
import io.wcm.caravan.commons.stream.Streams;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableListMultimap.Builder;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;

/**
 * Utility class for HAL resources.
 */
public final class HalUtil {

  private HalUtil() {
    // nothing to do
  }

  /**
   * Returns all links in a HAL resource and its embedded resources.
   * @param hal HAL resource
   * @return Map of Relations and Links
   */
  public static ListMultimap<String, Link> getAllLinks(HalResource hal) {

    return getAllLinks(hal, "self", Collections.emptySet());

  }

  private static ListMultimap<String, Link> getAllLinks(HalResource hal, String embeddedRelation, Set<String> except) {

    Builder<String, Link> builder = ImmutableListMultimap.builder();
    Streams.of(hal.getLinks().keySet())
        .filter(relation -> !except.contains(relation))
        .forEach(relation -> builder.putAll("self".equals(relation) ? embeddedRelation : relation, hal.getLinks(relation)));
    Streams.of(hal.getEmbedded().entries())
        .map(entry -> getAllLinks(entry.getValue(), entry.getKey(), except))
        .forEach(embeddedLinks -> builder.putAll(embeddedLinks));
    return builder.build();

  }

  /**
   * Returns all relations and links in a HAL resource and its embedded resources except CURI links.
   * @param hal HAL resource
   * @return Map of Relations and Links
   */
  public static ListMultimap<String, Link> getAllLinksExceptCuries(HalResource hal) {

    return getAllLinks(hal, "self", ImmutableSet.of("curies"));

  }

  /**
   * Returns all links in a HAL resource and its embedded resources fitting the supplied relation.
   * @param hal HAL resource
   * @param relation Link relation
   * @return List of all fitting links
   */
  public static List<Link> getAllLinksForRelation(HalResource hal, String relation) {

    List<Link> links = Lists.newArrayList(hal.getLinks(relation));
    List<Link> embeddedLinks = Streams.of(hal.getEmbedded().values())
        .flatMap(embedded -> Streams.of(getAllLinksForRelation(embedded, relation)))
        .collect(Collectors.toList());
    links.addAll(embeddedLinks);
    return links;

  }

}
