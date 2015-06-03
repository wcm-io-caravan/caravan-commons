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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import io.wcm.caravan.commons.hal.resource.HalResource;
import io.wcm.caravan.commons.hal.resource.HalResourceFactory;
import io.wcm.caravan.commons.hal.resource.Link;
import io.wcm.caravan.commons.stream.Streams;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;


public class HalCuriAugmenterTest {

  private HalCuriAugmenter augmenter;
  private HalResource hal;

  @Before
  public void setUp() {
    augmenter = new HalCuriAugmenter()
    .register("ex", "https://example.com/doc/ex/{rel}")
    .register("in", "https://example.com/doc/in/{rel}")
    .register("cust", "https://example.com/doc/cust/{rel}");
    hal = HalResourceFactory.createResource("/resource")
        .setLink("ex:external-link", HalResourceFactory.createLink("/external-link"))
        .addLinks("in:children", HalResourceFactory.createLink("/child-1"), HalResourceFactory.createLink("/child-2"))
        .addLinks("no-curie", HalResourceFactory.createLink("/no-curi-1"));
  }

  @Test
  public void has_shouldReturnTrueForExistingCuries() {
    assertTrue(augmenter.has("ex"));
  }

  @Test
  public void has_shouldReturnFalsForMissingCuries() {
    assertFalse(augmenter.has("missing"));
  }

  @Test
  public void get_shouldReturnCuriLink() {
    assertEquals("https://example.com/doc/ex/{rel}", augmenter.get("ex").getHref());
  }

  @Test
  public void get_shouldReturnNullForMissingRelationName() {
    assertNull(augmenter.get("missing"));
  }

  @Test
  public void unregister_shouldRemoveExistingRelation() {
    augmenter.unregister("ex");
    assertFalse(augmenter.has("ex"));
  }

  @Test
  public void unregister_shouldDoNothingForMissingRelation() {
    augmenter.unregister("missing");
  }

  @Test
  public void augment_shouldAddAllCuriesForCurieNames() {
    augmenter.augment(hal);
    List<Link> curies = hal.getLinks("curies");
    assertEquals(2, curies.size());
    assertEquals("ex", curies.get(0).getName());
    assertEquals("https://example.com/doc/ex/{rel}", curies.get(0).getHref());
  }

  @Test
  public void augment_shouldNotAddCuriForMissingCurieName() {
    augmenter.augment(hal);
    Streams.of(hal.getLinks("curies"))
    .map(link -> link.getName())
    .filter(name -> StringUtils.equals(name, "cust"))
    .forEach(name -> fail("cust is no CURI for this HAL"));
  }

  @Test
  public void augment_shouldNotOverrideExistingCuri() {
    hal.addLinks("curies", HalResourceFactory.createLink("https://example.com/doc/other/{rel}").setName("ex"));
    augmenter.augment(hal);
    List<Link> curies = hal.getLinks("curies");
    assertEquals("https://example.com/doc/other/{rel}", curies.get(0).getHref());
  }

  @Test
  public void augment_shouldOnlyAddCuriLinkOnce() {
    hal.addLinks("ex:external-link2", HalResourceFactory.createLink("/external-link2"));
    augmenter.augment(hal);
    List<Link> curies = hal.getLinks("curies");
    assertEquals(2, curies.size());
  }

  @Test
  public void augment_shouldAddCuriForLinksInEmbeddedResource() {
    HalResource item = HalResourceFactory.createResource("/item")
        .addLinks("cust:item", HalResourceFactory.createLink("/item-link"));
    hal.addEmbedded("item", item);

    augmenter.augment(hal);

    List<Link> curies = hal.getLinks("curies");
    assertEquals(3, curies.size());
    assertEquals("cust", curies.get(2).getName());
  }

}
