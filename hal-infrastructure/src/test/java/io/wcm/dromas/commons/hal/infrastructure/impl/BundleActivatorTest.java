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
package io.wcm.dromas.commons.hal.infrastructure.impl;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

import java.util.Dictionary;

import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.osgi.framework.BundleContext;

@RunWith(MockitoJUnitRunner.class)
public class BundleActivatorTest {

  @Mock
  private BundleContext bundleContext;

  private BundleActivator underTest;

  @Before
  public void setUp() {
    underTest = new BundleActivator();
  }

  @SuppressWarnings("unchecked")
  @Test
  public void testStart() throws Exception {
    underTest.start(bundleContext);
    verify(bundleContext).registerService(eq(MessageBodyWriter.class.getName()), anyObject(), any(Dictionary.class));
    verify(bundleContext).registerService(eq(MessageBodyReader.class.getName()), anyObject(), any(Dictionary.class));
  }

  @Test
  public void testStop() throws Exception {
    underTest.stop(bundleContext);
  }

}
