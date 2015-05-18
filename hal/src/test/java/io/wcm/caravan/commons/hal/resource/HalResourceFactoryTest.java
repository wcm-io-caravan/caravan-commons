package io.wcm.caravan.commons.hal.resource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;


public class HalResourceFactoryTest {

  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  @Test
  public void getStateAsObject_shouldConvertJsonToAnyObject() throws Exception {
    ObjectNode model = OBJECT_MAPPER.readValue(getClass().getResourceAsStream("/jackson_hal_resource_model.json"), ObjectNode.class);
    HalResource hal = new HalResource(model);
    TestObject state = HalResourceFactory.getStateAsObject(hal, TestObject.class);
    assertEquals("value1", state.property1);
    assertEquals("value2", state.property2);
  }

  @Test
  public void convert_shouldConvertObjectToJson() throws Exception {
    TestObject state = new TestObject();
    state.property1 = "value1";
    state.property2 = "value2";
    ObjectNode json = HalResourceFactory.convert(state);
    assertEquals("value1", json.get("property1").asText());
  }

  @Test
  public void createLink_shouldSetHref() {
    Link link = HalResourceFactory.createLink("/");
    assertEquals("/", link.getHref());
    assertFalse(link.isTemplated());
  }

  @Test
  public void createLink_shouldSetTemplatedFlag() {
    Link link = HalResourceFactory.createLink("/path{?query}");
    assertTrue(link.isTemplated());
  }

  @Test
  public void createResourceString_shouldSetHrefForSelfLink() {
    HalResource hal = HalResourceFactory.createResource("/");
    assertEquals("/", hal.getLink().getHref());
  }

  @Test
  public void createResourceObjectString_shouldSetStateAndHrefForSelfLink() {
    TestObject model = new TestObject();
    model.property1 = "value1";
    HalResource hal = HalResourceFactory.createResource(model, "/");
    assertEquals("value1", hal.getModel().get("property1").asText());
    assertEquals("/", hal.getLink().getHref());
  }

  @Test
  public void createResourceObjectNodeString_shouldSetStateAndHrefForSelfLink() {
    ObjectNode model = OBJECT_MAPPER.createObjectNode().put("att", "value");
    HalResource hal = HalResourceFactory.createResource(model, "/");
    assertEquals("value", hal.getModel().get("att").asText());
    assertEquals("/", hal.getLink().getHref());
  }

  private static class TestObject {

    public String property1;
    public String property2;

  }

}
