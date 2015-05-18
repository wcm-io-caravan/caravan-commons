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
import io.wcm.caravan.commons.hal.resource.HalResource;
import io.wcm.caravan.commons.hal.resource.Link;

import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;


public class HalBuilderTest {

  private static final String HAL_URI = "/resource";
  private static final String HAL_TITLE = "title";
  private static final String HAL_NAME = "name";

  @Test
  public void halBuilderString_shouldSetHref() throws Exception {
    assertEquals(HAL_URI, new HalBuilder(HAL_URI).build().getLink().getHref());
  }

  @Test
  public void halBuilderStringString_shouldSetHrefAndTitle() throws Exception {
    Link selfLink = new HalBuilder(HAL_URI, HAL_TITLE).build().getLink();
    assertEquals(HAL_URI, selfLink.getHref());
    assertEquals(HAL_TITLE, selfLink.getTitle());
  }

  @Test
  public void halBuilderStringStringString_shouldSetHrefAndTitleAndName() throws Exception {
    Link selfLink = new HalBuilder(HAL_URI, HAL_TITLE, HAL_NAME).build().getLink();
    assertEquals(HAL_URI, selfLink.getHref());
    assertEquals(HAL_TITLE, selfLink.getTitle());
    assertEquals(HAL_NAME, selfLink.getName());
  }

  @Test
  public void halBuilderObjectString_shouldConvertStateAndSetHref() throws Exception {
    HalResource hal = new HalBuilder(new State().setAtt1("value").setAtt2(2), HAL_URI).build();
    assertEquals(HAL_URI, hal.getLink().getHref());
    assertEquals("value", hal.getModel().get("att1").asText());
    assertEquals(2, hal.getModel().get("att2").asInt());
  }

  @Test
  public void halBuilderObjectNodeString_shouldSetStateAndHref() throws Exception {
    ObjectMapper mapper = new ObjectMapper();
    ObjectNode state = mapper.createObjectNode().put("att1", "value").put("att2", 2);
    HalResource hal = new HalBuilder(state, HAL_URI).build();
    assertEquals(HAL_URI, hal.getLink().getHref());
    assertEquals("value", hal.getModel().get("att1").asText());
    assertEquals(2, hal.getModel().get("att2").asInt());
  }

  @Test
  public void linkStringString_shouldSetLinkWithHref() throws Exception {
    HalResource hal = new HalBuilder(HAL_URI)
        .link("next", "/next")
        .build();
    assertEquals("/next", hal.getLink("next").getHref());
  }

  @Test
  public void linkStringStringString_shouldSetLinkWithHrefAndTitle() throws Exception {
    Link link = new HalBuilder(HAL_URI)
        .link("next", "/next", "nextTitle")
        .build()
        .getLink("next");
    assertEquals("/next", link.getHref());
    assertEquals("nextTitle", link.getTitle());
  }

  @Test
  public void linkStringStringStringString_shouldSetLinkWithHrefAndTitleAndName() throws Exception {
    Link link = new HalBuilder(HAL_URI)
        .link("next", "/next", "nextTitle", "nextName")
        .build()
        .getLink("next");
    assertEquals("/next", link.getHref());
    assertEquals("nextTitle", link.getTitle());
    assertEquals("nextName", link.getName());
  }

  @Test
  public void curi_shouldSetCuriWithHrefAndName() throws Exception {
    Link link = new HalBuilder(HAL_URI).curi("/doc", "doc").build().getLink("curies");
    assertEquals("/doc", link.getHref());
    assertEquals("doc", link.getName());
  }

  private static final class State {

    private String att1;
    private int att2;

    /**
     * @return Returns the att1.
     */
    public String getAtt1() {
      return this.att1;
    }

    /**
     * @param att1 The att1 to set.
     */
    public State setAtt1(String att1) {
      this.att1 = att1;
      return this;
    }

    /**
     * @return Returns the att2.
     */
    public int getAtt2() {
      return this.att2;
    }

    /**
     * @param att2 The att2 to set.
     */
    public State setAtt2(int att2) {
      this.att2 = att2;
      return this;
    }

  }

}
