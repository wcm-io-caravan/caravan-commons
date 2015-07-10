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
package io.wcm.caravan.commons.haldocs.impl;

import static org.junit.Assert.assertEquals;
import io.wcm.caravan.commons.haldocs.model.LinkRelation;
import io.wcm.caravan.commons.haldocs.model.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.junit.Before;
import org.junit.Test;

public class ServiceJsonTest {

  private Service service;

  @Before
  public void setUp() {
    service = new Service();
    service.setServiceId("/my/test/service");
    service.setName("My Test Service");
    service.setDescriptionMarkup("<p>This is a lengthy description of the service. "
        + "This is a lengthy description of the service. This is a lengthy description of the service. "
        + "This is a lengthy description of the service. This is a lengthy description of the service. "
        + "This is a lengthy description of the service.</p>"
        + "<p>This is a lengthy description of the service.</p>");

    LinkRelation rel1 = new LinkRelation();
    rel1.setRel("ns:rel1");
    rel1.setDescriptionMarkup("<p>This is a lengthy description of the relation. "
        + "This is a lengthy description of the relation. This is a lengthy description of the relation. "
        + "This is a lengthy description of the relation. This is a lengthy description of the relation. "
        + "This is a lengthy description of the relation.</p>"
        + "<p>This is a lengthy description of the relation.</p>");
    rel1.setJsonSchemaRef("schema1.json");
    service.addLinkRelation(rel1);

    LinkRelation rel2 = new LinkRelation();
    rel2.setRel("ns:rel2");
    rel2.setDescriptionMarkup("<p>This is a description of the relation.</p>");
    rel2.setJsonSchemaRef("schema2.json");
    service.addLinkRelation(rel2);

    LinkRelation rel3 = new LinkRelation();
    rel3.setRel("ns:rel3");
    rel3.setDescriptionMarkup("<p>This is a description of the relation.</p>");
    rel3.setJsonSchemaRef("schema3.json");
    service.addLinkRelation(rel3);

    rel1.addNestedLinkRelation(rel2.getRel(), "Description for rel1->rel2");
    rel2.addNestedLinkRelation(rel3.getRel(), null);
    rel2.addNestedLinkRelation("unknown/invalid", null);
  }

  @Test
  public void testWriteRead() throws IOException {
    File targetFile = new File("target/documentation-test/serviceDoc.json");
    targetFile.getParentFile().mkdirs();

    try (OutputStream os = new FileOutputStream(targetFile)) {
      ServiceJson.write(service, os);
    }
    Service service2;
    try (InputStream is = new FileInputStream(targetFile)) {
      service2 = ServiceJson.read(is);
    }

    assertEquals(service.getServiceId(), service2.getServiceId());
    assertEquals(service.getName(), service2.getName());
    assertEquals(service.getDescriptionMarkup(), service2.getDescriptionMarkup());
    assertEquals(service.getLinkRelations(), service2.getLinkRelations());
  }

}
