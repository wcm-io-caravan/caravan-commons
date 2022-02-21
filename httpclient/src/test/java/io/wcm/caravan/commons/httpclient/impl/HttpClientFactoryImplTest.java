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
package io.wcm.caravan.commons.httpclient.impl;

import static org.junit.Assert.assertEquals;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.client.HttpClient;
import org.apache.sling.testing.mock.osgi.junit.OsgiContext;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.osgi.framework.Constants;

import com.google.common.collect.ImmutableMap;

import io.wcm.caravan.commons.httpclient.HttpClientConfig;
import io.wcm.caravan.commons.httpclient.HttpClientFactory;

@RunWith(MockitoJUnitRunner.class)
public class HttpClientFactoryImplTest {

  @Rule
  public OsgiContext context = new OsgiContext();

  @Test
  public void testClientSelection() {

    context.registerInjectActivateService(new HttpClientConfigImpl(),
        ImmutableMap.<String, Object>builder()
            .put("connectTimeout", 55)
            .put("hostPatterns", new String[] {
                "host1"
            })
            .put(Constants.SERVICE_RANKING, 10)
            .build());

    context.registerInjectActivateService(new HttpClientConfigImpl(),
        ImmutableMap.<String, Object>builder()
            .put("connectTimeout", 66)
            .put("hostPatterns", new String[] {
                "host2"
            })
            .put(Constants.SERVICE_RANKING, 20)
            .build());

    HttpClientFactory underTest = context.registerInjectActivateService(new HttpClientFactoryImpl());

    HttpClient client1 = underTest.get("http://host1/xyz");
    assertEquals("client1.timeout", 55, HttpClientTestUtils.getConnectTimeout(client1));

    HttpClient client2 = underTest.get("http://host2/xyz");
    assertEquals("client2.timeout", 66, HttpClientTestUtils.getConnectTimeout(client2));

    HttpClient client3 = underTest.get("http://host3/xyz");
    assertEquals("client3.timeout", HttpClientConfig.CONNECT_TIMEOUT_DEFAULT, HttpClientTestUtils.getConnectTimeout(client3));

  }

  @Test
  public void testClientSelectionWithAllMatchesConfigIncluded() {

    context.registerInjectActivateService(new HttpClientConfigImpl(),
        ImmutableMap.<String, Object>builder()
            .put("connectTimeout", 55)
            .put("hostPatterns", new String[] {
                "host1"
            })
            .put(Constants.SERVICE_RANKING, 10)
            .build());

    context.registerInjectActivateService(new HttpClientConfigImpl(),
        ImmutableMap.<String, Object>builder()
            .put("connectTimeout", 66)
            .put(Constants.SERVICE_RANKING, 20)
            .build());

    HttpClientFactory underTest = context.registerInjectActivateService(new HttpClientFactoryImpl());

    HttpClient client1 = underTest.get("http://host1/xyz");
    assertEquals("client1.timeout", 55, HttpClientTestUtils.getConnectTimeout(client1));

    HttpClient client2 = underTest.get("http://host2/xyz");
    assertEquals("client2.timeout", 66, HttpClientTestUtils.getConnectTimeout(client2));

    HttpClient client3 = underTest.get("http://host3/xyz");
    assertEquals("client3.timeout", 66, HttpClientTestUtils.getConnectTimeout(client3));
  }

  @Test
  public void testGetConfigForWebservice() {

    context.registerInjectActivateService(new HttpClientConfigImpl(),
        ImmutableMap.<String, Object>builder()
            .put("connectTimeout", 55)
            .put("hostPatterns", new String[] {
                "host1"
            })
            .put("wsAddressingToUris", new String[] {
                "http://uri1"
            })
            .put(Constants.SERVICE_RANKING, 10)
            .build());

    context.registerInjectActivateService(new HttpClientConfigImpl(),
        ImmutableMap.<String, Object>builder()
            .put("connectTimeout", 66)
            .put("hostPatterns", new String[] {
                "host2"
            })
            .put(Constants.SERVICE_RANKING, 20)
            .build());

    HttpClientFactory underTest = context.registerInjectActivateService(new HttpClientFactoryImpl());

    HttpClient client1a = underTest.getWs("http://host1/xyz", "http://uri1");
    assertEquals("client1a.timeout", 55, HttpClientTestUtils.getConnectTimeout(client1a));

    HttpClient client1b = underTest.getWs("http://host1/xyz", "http://uri2");
    assertEquals("client1b.timeout", 15000, HttpClientTestUtils.getConnectTimeout(client1b));

    HttpClient client1c = underTest.getWs("http://host1/xyz", null);
    assertEquals("client1c.timeout", 15000, HttpClientTestUtils.getConnectTimeout(client1c));

    HttpClient client2a = underTest.getWs("http://host2/xyz", "http://uri1");
    assertEquals("client2a.timeout", 66, HttpClientTestUtils.getConnectTimeout(client2a));

    HttpClient client2b = underTest.getWs("http://host2/xyz", "http://uri2");
    assertEquals("client2b.timeout", 66, HttpClientTestUtils.getConnectTimeout(client2b));

    HttpClient client2c = underTest.getWs("http://host2/xyz", null);
    assertEquals("client2c.timeout", 66, HttpClientTestUtils.getConnectTimeout(client2c));
  }

  @Test
  public void testGetConfigForConfiguredPath() throws URISyntaxException {

    context.registerInjectActivateService(new HttpClientConfigImpl(),
        ImmutableMap.<String, Object>builder()
            .put("connectTimeout", 55)
            .put("hostPatterns", new String[] {
                "host1"
            })
            .put("pathPatterns", new String[] {
                "^/path1$"
            })
            .put(Constants.SERVICE_RANKING, 10)
            .build());

    context.registerInjectActivateService(new HttpClientConfigImpl(),
        ImmutableMap.<String, Object>builder()
            .put("connectTimeout", 66)
            .put("hostPatterns", new String[] {
                "host2"
            })
            .put(Constants.SERVICE_RANKING, 20)
            .build());

    context.registerInjectActivateService(new HttpClientConfigImpl(),
        ImmutableMap.<String, Object>builder()
            .put("connectTimeout", 77)
            .put("hostPatterns", new String[] {
                "host3"
            })
            .put("wsAddressingToUris", new String[] {
                "http://uri3"
            })
            .put("pathPatterns", new String[] {
                "^.*path1.*$"
            })
            .put(Constants.SERVICE_RANKING, 30)
            .build());

    HttpClientFactory underTest = context.registerInjectActivateService(new HttpClientFactoryImpl());

    HttpClient client1a = underTest.get("http://host1/path1");
    assertEquals("client1a.timeout", 55, HttpClientTestUtils.getConnectTimeout(client1a));

    HttpClient client1b = underTest.get("http://host1/path2");
    assertEquals("client1b.timeout", 15000, HttpClientTestUtils.getConnectTimeout(client1b));

    HttpClient client1c = underTest.get(new URI("http://host1/path1"));
    assertEquals("client1c.timeout", 55, HttpClientTestUtils.getConnectTimeout(client1c));

    HttpClient client2a = underTest.get("http://host2/path1");
    assertEquals("client2a.timeout", 66, HttpClientTestUtils.getConnectTimeout(client2a));

    HttpClient client2b = underTest.get("http://host2/path2");
    assertEquals("client2b.timeout", 66, HttpClientTestUtils.getConnectTimeout(client2b));

    HttpClient client2c = underTest.get(new URI("http://host2/xyz"));
    assertEquals("client2c.timeout", 66, HttpClientTestUtils.getConnectTimeout(client2c));

    HttpClient client3a = underTest.getWs("http://host3/path1", "http://uri3");
    assertEquals("client3a.timeout", 77, HttpClientTestUtils.getConnectTimeout(client3a));

    HttpClient client3b = underTest.getWs("http://host3/path2", "http://uri3");
    assertEquals("client3b.timeout", 15000, HttpClientTestUtils.getConnectTimeout(client3b));
  }

}
