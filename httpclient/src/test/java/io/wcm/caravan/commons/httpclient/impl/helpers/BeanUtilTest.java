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
package io.wcm.caravan.commons.httpclient.impl.helpers;

import static org.junit.Assert.assertEquals;

import java.util.Collections;
import java.util.SortedMap;

import org.junit.Test;

public class BeanUtilTest {

  @Test
  public void testGetMaskedBeanProperties() {

    SampleBean sampleBean = new SampleBean("value1", "value2");
    SortedMap<String, Object> properties = BeanUtil.getMaskedBeanProperties(sampleBean, Collections.singleton("attribute2"));

    assertEquals(2, properties.size());
    assertEquals("value1", properties.get("attribute1"));
    assertEquals("***", properties.get("attribute2"));
  }

  public static final class SampleBean {

    private final String attribute1;
    private final String attribute2;

    public SampleBean(String attribute1, String attribute2) {
      this.attribute1 = attribute1;
      this.attribute2 = attribute2;
    }

    public String getAttribute1() {
      return attribute1;
    }

    public String getAttribute2() {
      return attribute2;
    }

  }

}
