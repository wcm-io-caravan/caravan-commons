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
package io.wcm.dromas.commons.hal.impl;

import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;

import org.osgi.framework.BundleContext;

import com.theoryinpractise.halbuilder.jaxrs.JaxRsHalBuilderReaderSupport;
import com.theoryinpractise.halbuilder.jaxrs.JaxRsHalBuilderSupport;

/**
 * Register JAX-RS Provider for HAL builder.
 */
public class BundleActivator implements org.osgi.framework.BundleActivator {

  @Override
  public void start(BundleContext context) throws Exception {
    // register JAX-RS support for HAL builder
    context.registerService(MessageBodyWriter.class.getName(), new JaxRsHalBuilderSupport(), null);
    context.registerService(MessageBodyReader.class.getName(), new JaxRsHalBuilderReaderSupport(), null);
  }

  @Override
  public void stop(BundleContext context) throws Exception {
    // nothing to do
  }

}
