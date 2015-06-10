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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import io.wcm.caravan.commons.hal.resource.HalResource;
import io.wcm.caravan.commons.hal.resource.HalResourceFactory;
import io.wcm.caravan.commons.hal.resource.Link;
import io.wcm.caravan.commons.stream.Collectors;
import io.wcm.caravan.commons.stream.Streams;

import java.util.Collection;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Before;
import org.junit.Test;

import com.google.common.base.Predicate;
import com.google.common.collect.ListMultimap;

public class HalUtilTest {

  private HalResource payload;

  @Before
  public void setUp() {

    payload = HalResourceFactory.createResource("/resource")
        .addLinks("curies", HalResourceFactory.createLink("/doc#{rel}").setName("topic"))
        .addLinks("section", HalResourceFactory.createLink("/link-1"), HalResourceFactory.createLink("/link-2"))
        .addEmbedded("item", HalResourceFactory.createResource("/embedded-1")
            .addLinks("item", HalResourceFactory.createLink("/embedded-1-link-1"), HalResourceFactory.createLink("/embedded-1-link-2")));

  }

  @Test
  public void getAllLinks_shouldExtractAllMainResourceLinks() {
    ListMultimap<String, Link> links = HalUtil.getAllLinks(payload);
    assertEquals("/resource", links.get("self").get(0).getHref());
    assertEquals("/link-1", links.get("section").get(0).getHref());
  }

  private Set<String> getUris(Collection<Link> links) {
    return Streams.of(links).map(Link::getHref).collect(Collectors.toSet());
  }

  @Test
  public void getAllLinks_shouldExtractAllEmbeddedResourceLinks() {
    ListMultimap<String, Link> links = HalUtil.getAllLinks(payload);
    assertEquals("/embedded-1", links.get("item").get(0).getHref());
    assertEquals("/embedded-1-link-1", links.get("item").get(1).getHref());
  }

  @Test
  public void getAllLinks_shouldExtractNoCuriLinks() {
    ListMultimap<String, Link> links = HalUtil.getAllLinks(payload);
    assertFalse(links.containsKey("curies"));
  }

  @Test
  public void getAllLinksWithPredicat_shouldOnlyExtractLinksFittingThePredicate() {

    ListMultimap<String, Link> links = HalUtil.getAllLinks(payload, new Predicate<Pair<String, Link>>() {

      @Override
      public boolean apply(Pair<String, Link> input) {
        return input.getKey().equals("item");
      }
    });
    assertTrue(links.containsKey("item"));
    assertFalse(links.containsKey("section"));

  }

  @Test
  public void getAllLinksForRelation_shouldOnlyExtractMainResourceLinksForGivenRelation() {

    Set<String> uris = getUris(HalUtil.getAllLinksForRelation(payload, "item"));
    assertFalse(uris.contains("/resource"));
    assertFalse(uris.contains("/link-1"));
    assertFalse(uris.contains("/link-2"));
    assertTrue(uris.contains("/embedded-1-link-1"));
    assertTrue(uris.contains("/embedded-1-link-2"));

  }

  @Test
  public void getAllLinksForRelation_shouldOnlyExtractEmbeddedResourceLinksForGivenRelation() {

    Set<String> uris = getUris(HalUtil.getAllLinksForRelation(payload, "item"));
    assertFalse(uris.contains("/embedded-1"));
    assertTrue(uris.contains("/embedded-1-link-1"));
    assertTrue(uris.contains("/embedded-1-link-2"));

  }

}
