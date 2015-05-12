package io.wcm.caravan.commons.hal.resource;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;


public class HalResourceFactoryTest {

  @Test
  public void testGetStateAsObject() throws Exception {
    ObjectNode model = new ObjectMapper().readValue(getClass().getResourceAsStream("/jackson_hal_resource_model.json"), ObjectNode.class);
    HalResource hal = new HalResource(model);
    TestObject state = HalResourceFactory.getStateAsObject(hal, TestObject.class);
    assertEquals("value1", state.property1);
    assertEquals("value2", state.property2);
  }

  private static class TestObject {

    public String property1;
    public String property2;

  }


}
