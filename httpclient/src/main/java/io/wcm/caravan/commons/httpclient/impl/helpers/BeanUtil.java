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

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

/**
 * Helper methods for managing java beans.
 */
final class BeanUtil {

  private BeanUtil() {
    // static methods only
  }

  /**
   * Get map with key/value pairs for properties of a java bean (using {@link Introspector#getBeanInfo(Class)}).
   * An array of property names can be passed that should be masked with "***" because they contain sensitive
   * information.
   * @param beanObject Bean object
   * @param maskProperties List of property names
   * @return Map with masked key/value pairs
   */
  public static @NotNull SortedMap<String, Object> getMaskedBeanProperties(@NotNull Object beanObject, @NotNull Set<String> maskProperties) {
    try {
      SortedMap<String, Object> configProperties = new TreeMap<>();

      BeanInfo beanInfo = Introspector.getBeanInfo(beanObject.getClass());
      PropertyDescriptor[] props = beanInfo.getPropertyDescriptors();

      for (PropertyDescriptor prop : props) {
        String property = prop.getName();
        if (StringUtils.equals(property, "class")) {
          continue;
        }
        Object value = prop.getReadMethod().invoke(beanObject);
        if (value != null) {
          if (maskProperties.contains(property)) {
            value = "***";
          }
          configProperties.put(property, value);
        }
      }

      return configProperties;
    }
    catch (IntrospectionException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
      throw new IllegalArgumentException("Unable to get properties from: " + beanObject, ex);
    }
  }

}
