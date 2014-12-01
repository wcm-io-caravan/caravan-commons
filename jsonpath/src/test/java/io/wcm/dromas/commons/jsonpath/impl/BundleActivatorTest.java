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
package io.wcm.dromas.commons.jsonpath.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.osgi.framework.BundleContext;

import com.fasterxml.jackson.databind.node.TextNode;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;

@RunWith(MockitoJUnitRunner.class)
public class BundleActivatorTest {

  @Mock
  private BundleContext bundleContext;

  private BundleActivator underTest;

  @Before
  public void setUp() throws Exception {
    underTest = new BundleActivator();
    underTest.start(bundleContext);
  }

  @After
  public void tearDown() throws Exception {
    underTest.stop(bundleContext);
  }

  @Test
  public void testJsonPath() {
    Object doc = Configuration.defaultConfiguration().jsonProvider().parse("{\"node1\":{\"node2\":{\"prop1\":\"value1\"}}}");
    assertTrue(doc instanceof com.fasterxml.jackson.databind.node.ObjectNode);
    TextNode prop1 = JsonPath.read(doc, "$.node1.node2.prop1");
    assertEquals("value1", prop1.asText());
  }

}
