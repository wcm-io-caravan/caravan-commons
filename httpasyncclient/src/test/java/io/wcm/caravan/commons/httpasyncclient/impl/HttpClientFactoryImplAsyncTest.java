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
package io.wcm.caravan.commons.httpasyncclient.impl;

import static io.wcm.caravan.commons.httpclient.impl.HttpClientConfigImpl.CONNECT_TIMEOUT_PROPERTY;
import static io.wcm.caravan.commons.httpclient.impl.HttpClientConfigImpl.HOST_PATTERNS_PROPERTY;
import static io.wcm.caravan.commons.httpclient.impl.HttpClientConfigImpl.PATH_PATTERNS_PROPERTY;
import static io.wcm.caravan.commons.httpclient.impl.HttpClientConfigImpl.WS_ADDRESSINGTO_URIS_PROPERTY;
import static org.junit.Assert.assertEquals;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.nio.client.HttpAsyncClient;
import org.apache.sling.testing.mock.osgi.junit.OsgiContext;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.osgi.framework.Constants;

import com.google.common.collect.ImmutableMap;

import io.wcm.caravan.commons.httpasyncclient.HttpAsyncClientFactory;
import io.wcm.caravan.commons.httpclient.HttpClientConfig;
import io.wcm.caravan.commons.httpclient.impl.HttpClientConfigImpl;

@RunWith(MockitoJUnitRunner.class)
public class HttpClientFactoryImplAsyncTest {

  @Rule
  public OsgiContext context = new OsgiContext();

  @Test
  public void testClientSelection() {

    context.registerInjectActivateService(new HttpClientConfigImpl(),
        ImmutableMap.<String, Object>builder()
        .put(CONNECT_TIMEOUT_PROPERTY, 55)
        .put(HOST_PATTERNS_PROPERTY, new String[] {
            "host1"
        })
        .put(Constants.SERVICE_RANKING, 10)
        .build());

    context.registerInjectActivateService(new HttpClientConfigImpl(),
        ImmutableMap.<String, Object>builder()
        .put(CONNECT_TIMEOUT_PROPERTY, 66)
        .put(HOST_PATTERNS_PROPERTY, new String[] {
            "host2"
        })
        .put(Constants.SERVICE_RANKING, 20)
        .build());

    HttpAsyncClientFactory underTest = context.registerInjectActivateService(new HttpAsyncClientFactoryImpl());

    HttpAsyncClient client1 = underTest.get("http://host1/xyz");
    assertEquals("client1.timeout", 55, HttpClientTestUtils.getConnectTimeout(client1));

    HttpAsyncClient client2 = underTest.get("http://host2/xyz");
    assertEquals("client2.timeout", 66, HttpClientTestUtils.getConnectTimeout(client2));

    HttpAsyncClient client3 = underTest.get("http://host3/xyz");
    assertEquals("client3.timeout", HttpClientConfig.CONNECT_TIMEOUT_DEFAULT, HttpClientTestUtils.getConnectTimeout(client3));

  }

  @Test
  public void testClientSelectionWithAllMatchesConfigIncluded() {

    context.registerInjectActivateService(new HttpClientConfigImpl(),
        ImmutableMap.<String, Object>builder()
        .put(CONNECT_TIMEOUT_PROPERTY, 55)
        .put(HOST_PATTERNS_PROPERTY, new String[] {
            "host1"
        })
        .put(Constants.SERVICE_RANKING, 10)
        .build());

    context.registerInjectActivateService(new HttpClientConfigImpl(),
        ImmutableMap.<String, Object>builder()
        .put(CONNECT_TIMEOUT_PROPERTY, 66)
        .put(Constants.SERVICE_RANKING, 20)
        .build());

    HttpAsyncClientFactory underTest = context.registerInjectActivateService(new HttpAsyncClientFactoryImpl());

    HttpAsyncClient client1 = underTest.get("http://host1/xyz");
    assertEquals("client1.timeout", 55, HttpClientTestUtils.getConnectTimeout(client1));

    HttpAsyncClient client2 = underTest.get("http://host2/xyz");
    assertEquals("client2.timeout", 66, HttpClientTestUtils.getConnectTimeout(client2));

    HttpAsyncClient client3 = underTest.get("http://host3/xyz");
    assertEquals("client3.timeout", 66, HttpClientTestUtils.getConnectTimeout(client3));

  }

  @Test
  public void testGetHttpClientConfigForWebservice() {

    context.registerInjectActivateService(new HttpClientConfigImpl(),
        ImmutableMap.<String, Object>builder()
        .put(CONNECT_TIMEOUT_PROPERTY, 55)
        .put(HOST_PATTERNS_PROPERTY, new String[] {
            "host1"
        })
        .put(WS_ADDRESSINGTO_URIS_PROPERTY, new String[] {
            "http://uri1"
        })
        .put(Constants.SERVICE_RANKING, 10)
        .build());

    context.registerInjectActivateService(new HttpClientConfigImpl(),
        ImmutableMap.<String, Object>builder()
        .put(CONNECT_TIMEOUT_PROPERTY, 66)
        .put(HOST_PATTERNS_PROPERTY, new String[] {
            "host2"
        })
        .put(Constants.SERVICE_RANKING, 20)
        .build());

    HttpAsyncClientFactory underTest = context.registerInjectActivateService(new HttpAsyncClientFactoryImpl());

    HttpAsyncClient client1a = underTest.getWs("http://host1/xyz", "http://uri1");
    assertEquals("client1a.timeout", 55, HttpClientTestUtils.getConnectTimeout(client1a));

    HttpAsyncClient client1b = underTest.getWs("http://host1/xyz", "http://uri2");
    assertEquals("client1b.timeout", 15000, HttpClientTestUtils.getConnectTimeout(client1b));

    HttpAsyncClient client1c = underTest.getWs("http://host1/xyz", null);
    assertEquals("client1c.timeout", 15000, HttpClientTestUtils.getConnectTimeout(client1c));

    HttpAsyncClient client2a = underTest.getWs("http://host2/xyz", "http://uri1");
    assertEquals("client2a.timeout", 66, HttpClientTestUtils.getConnectTimeout(client2a));

    HttpAsyncClient client2b = underTest.getWs("http://host2/xyz", "http://uri2");
    assertEquals("client2b.timeout", 66, HttpClientTestUtils.getConnectTimeout(client2b));

    HttpAsyncClient client2c = underTest.getWs("http://host2/xyz", null);
    assertEquals("client2c.timeout", 66, HttpClientTestUtils.getConnectTimeout(client2c));

  }

  @Test
  public void testGetConfigForConfiguredPath() throws URISyntaxException {

    context.registerInjectActivateService(new HttpClientConfigImpl(),
        ImmutableMap.<String, Object>builder()
        .put(CONNECT_TIMEOUT_PROPERTY, 55)
        .put(HOST_PATTERNS_PROPERTY, new String[] {
            "host1"
        })
        .put(PATH_PATTERNS_PROPERTY, new String[] {
            "/path1"
        })
        .put(Constants.SERVICE_RANKING, 10)
        .build());

    context.registerInjectActivateService(new HttpClientConfigImpl(),
        ImmutableMap.<String, Object>builder()
        .put(CONNECT_TIMEOUT_PROPERTY, 66)
        .put(HOST_PATTERNS_PROPERTY, new String[] {
            "host2"
        })
        .put(Constants.SERVICE_RANKING, 20)
        .build());

    context.registerInjectActivateService(new HttpClientConfigImpl(),
        ImmutableMap.<String, Object>builder()
        .put(CONNECT_TIMEOUT_PROPERTY, 77)
        .put(HOST_PATTERNS_PROPERTY, new String[] {
            "host3"
        })
        .put(WS_ADDRESSINGTO_URIS_PROPERTY, new String[] {
            "http://uri3"
        })
        .put(PATH_PATTERNS_PROPERTY, new String[] {
            "/path1"
        })
        .put(Constants.SERVICE_RANKING, 30)
        .build());

    HttpAsyncClientFactory underTest = context.registerInjectActivateService(new HttpAsyncClientFactoryImpl());

    HttpAsyncClient client1a = underTest.get("http://host1/path1");
    assertEquals("client1a.timeout", 55, HttpClientTestUtils.getConnectTimeout(client1a));

    HttpAsyncClient client1b = underTest.get("http://host1/path2");
    assertEquals("client1b.timeout", 15000, HttpClientTestUtils.getConnectTimeout(client1b));

    HttpAsyncClient client1c = underTest.get(new URI("http://host1/path1"));
    assertEquals("client1c.timeout", 55, HttpClientTestUtils.getConnectTimeout(client1c));

    HttpAsyncClient client2a = underTest.get("http://host2/path1");
    assertEquals("client2a.timeout", 66, HttpClientTestUtils.getConnectTimeout(client2a));

    HttpAsyncClient client2b = underTest.get("http://host2/path2");
    assertEquals("client2b.timeout", 66, HttpClientTestUtils.getConnectTimeout(client2b));

    HttpAsyncClient client2c = underTest.get(new URI("http://host2/xyz"));
    assertEquals("client2c.timeout", 66, HttpClientTestUtils.getConnectTimeout(client2c));

    HttpAsyncClient client3a = underTest.getWs("http://host3/path1", "http://uri3");
    assertEquals("client3a.timeout", 77, HttpClientTestUtils.getConnectTimeout(client3a));

    HttpAsyncClient client3b = underTest.getWs("http://host3/path2", "http://uri3");
    assertEquals("client3b.timeout", 15000, HttpClientTestUtils.getConnectTimeout(client3b));
  }

}
