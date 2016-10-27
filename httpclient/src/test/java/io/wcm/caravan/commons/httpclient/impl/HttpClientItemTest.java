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

import static io.wcm.caravan.commons.httpclient.impl.HttpClientConfigImpl.CONNECT_TIMEOUT_PROPERTY;
import static io.wcm.caravan.commons.httpclient.impl.HttpClientConfigImpl.HOST_PATTERNS_PROPERTY;
import static io.wcm.caravan.commons.httpclient.impl.HttpClientConfigImpl.HTTP_PASSWORD_PROPERTY;
import static io.wcm.caravan.commons.httpclient.impl.HttpClientConfigImpl.HTTP_USER_PROPERTY;
import static io.wcm.caravan.commons.httpclient.impl.HttpClientConfigImpl.KEYSTORE_PASSWORD_PROPERTY;
import static io.wcm.caravan.commons.httpclient.impl.HttpClientConfigImpl.KEYSTORE_PATH_PROPERTY;
import static io.wcm.caravan.commons.httpclient.impl.HttpClientConfigImpl.MAX_CONNECTIONS_PER_HOST_PROPERTY;
import static io.wcm.caravan.commons.httpclient.impl.HttpClientConfigImpl.MAX_TOTAL_CONNECTIONS_PROPERTY;
import static io.wcm.caravan.commons.httpclient.impl.HttpClientConfigImpl.PROXY_HOST_PROPERTY;
import static io.wcm.caravan.commons.httpclient.impl.HttpClientConfigImpl.PROXY_PASSWORD_PROPERTY;
import static io.wcm.caravan.commons.httpclient.impl.HttpClientConfigImpl.PROXY_PORT_PROPERTY;
import static io.wcm.caravan.commons.httpclient.impl.HttpClientConfigImpl.PROXY_USER_PROPERTY;
import static io.wcm.caravan.commons.httpclient.impl.HttpClientConfigImpl.RESOURCE_PATH_PROPERTY;
import static io.wcm.caravan.commons.httpclient.impl.HttpClientConfigImpl.SOCKET_TIMEOUT_PROPERTY;
import static io.wcm.caravan.commons.httpclient.impl.HttpClientConfigImpl.TRUSTSTORE_PASSWORD_PROPERTY;
import static io.wcm.caravan.commons.httpclient.impl.HttpClientConfigImpl.TRUSTSTORE_PATH_PROPERTY;
import static io.wcm.caravan.commons.httpclient.impl.HttpClientConfigImpl.WS_ADDRESSINGTO_URIS_PROPERTY;
import static org.junit.Assert.*;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.Registry;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.sling.testing.mock.osgi.junit.OsgiContext;
import org.junit.*;

import com.google.common.collect.ImmutableMap;

import io.wcm.caravan.commons.httpclient.impl.helpers.CertificateLoaderTest;

public class HttpClientItemTest {

  @Rule
  public OsgiContext context = new OsgiContext();

  @Test
  public void testMatchesHostnames() {
    HttpClientConfigImpl config = context.registerInjectActivateService(new HttpClientConfigImpl(),
        ImmutableMap.<String, Object>builder()
        .put(HOST_PATTERNS_PROPERTY, new String[] {
            "h1", "h2"
        })
        .build());

    HttpClientItem item = new HttpClientItem(config);
    assertTrue(item.matches("h1", null, null, false));
    assertTrue(item.matches("h2", null, null, false));
    assertFalse(item.matches("h3", null, null, false));
    item.close();
  }

  @Test
  public void testMatchesHostnamesRegExp() {
    HttpClientConfigImpl config = context.registerInjectActivateService(new HttpClientConfigImpl(),
        ImmutableMap.<String, Object>builder()
        .put(HOST_PATTERNS_PROPERTY, new String[] {
            "h(\\d*)"
        })
        .build());

    HttpClientItem item = new HttpClientItem(config);
    assertTrue(item.matches("h1", null, null, false));
    assertTrue(item.matches("h2", null, null, false));
    assertTrue(item.matches("h3", null, null, false));
    assertFalse(item.matches("hx", null, null, false));
    assertFalse(item.matches("xyz", null, null, false));
    item.close();
  }

  @Test
  public void testMatchesHostnamesEmptySet() {
    HttpClientConfigImpl config = context.registerInjectActivateService(new HttpClientConfigImpl());

    HttpClientItem item = new HttpClientItem(config);
    assertTrue(item.matches("h1", null, null, false));
    assertTrue(item.matches("h2", null, null, false));
    assertTrue(item.matches("h3", null, null, false));
    item.close();
  }

  @Test
  public void testMatchesHostnamesWithInvalidPattern() {
    HttpClientConfigImpl config = context.registerInjectActivateService(new HttpClientConfigImpl(),
        ImmutableMap.<String, Object>builder()
        .put(HOST_PATTERNS_PROPERTY, new String[] {
            "h(\\d*)",
            "(aaa"
        })
        .build());

    HttpClientItem item = new HttpClientItem(config);
    assertFalse(item.matches("h1", null, null, false));
    assertFalse(item.matches("h2", null, null, false));
    assertFalse(item.matches("h3", null, null, false));
    assertFalse(item.matches("hx", null, null, false));
    assertFalse(item.matches("xyz", null, null, false));
    item.close();
  }

  @Test
  public void testMatchesWSAddressingToUris() {
    HttpClientConfigImpl config = context.registerInjectActivateService(new HttpClientConfigImpl(),
        ImmutableMap.<String, Object>builder()
        .put(WS_ADDRESSINGTO_URIS_PROPERTY, new String[] {
            "http://uri1",
            "http://uri2"
        })
        .build());

    HttpClientItem item = new HttpClientItem(config);
    assertTrue(item.matches("h1", "http://uri1", null, true));
    assertTrue(item.matches("h2", "http://uri2", null, true));
    assertFalse(item.matches("h3", "http://uri3", null, true));
    assertFalse(item.matches("h1", null, null, true));
    item.close();
  }

  @Test
  public void testMatchesWSAddressingToUrisEmptySet() {
    HttpClientConfigImpl config = context.registerInjectActivateService(new HttpClientConfigImpl());

    HttpClientItem item = new HttpClientItem(config);
    assertTrue(item.matches("h1", "http://uri1", null, true));
    assertTrue(item.matches("h2", "http://uri2", null, true));
    assertTrue(item.matches("h3", "http://uri3", null, true));
    assertTrue(item.matches("h1", null, null, true));
    item.close();
  }

  @Test
  public void testMatchesHostnamesAndWSAddressintToUri() {
    HttpClientConfigImpl config = context.registerInjectActivateService(new HttpClientConfigImpl(),
        ImmutableMap.<String, Object>builder()
        .put(HOST_PATTERNS_PROPERTY, new String[] {
            "h1",
            "h2"
        })
        .put(WS_ADDRESSINGTO_URIS_PROPERTY, new String[] {
            "http://uri1",
            "http://uri2"
        })
        .build());

    HttpClientItem item = new HttpClientItem(config);
    assertTrue(item.matches("h1", "http://uri1", null, true));
    assertTrue(item.matches("h1", "http://uri2", null, true));
    assertFalse(item.matches("h1","http://uri3", null, true));
    assertFalse(item.matches("h1", null, null, true));
    assertTrue(item.matches("h2", "http://uri1", null, true));
    assertTrue(item.matches("h2", "http://uri2", null, true));
    assertFalse(item.matches("h2", "http://uri3", null, true));
    assertFalse(item.matches("h3", "http://uri1", null, true));
    assertFalse(item.matches("h3", "http://uri2", null, true));
    assertFalse(item.matches("h3", "http://uri3", null, true));
    item.close();
  }

  @Test
  public void testMatchesResourcePath() {
    HttpClientConfigImpl config = context.registerInjectActivateService(new HttpClientConfigImpl(),
        ImmutableMap.<String, Object>builder()
            .put(RESOURCE_PATH_PROPERTY, new String[] {
                "/path1",
                "/path2"
            })
            .build());

    HttpClientItem item = new HttpClientItem(config);
    assertTrue(item.matches("h1", null, "/path1", false));
    assertTrue(item.matches("h2", null, "/path2", false));
    assertFalse(item.matches("h3", null, "/path3", false));
    assertFalse(item.matches("h1", null, null, false));
    item.close();
  }

  @Test
  public void testMatchesResourcePathEmptyConfigurationForPath() {
    HttpClientConfigImpl config = context.registerInjectActivateService(new HttpClientConfigImpl());

    HttpClientItem item = new HttpClientItem(config);
    assertTrue(item.matches("h1", null, "/path1", false));
    assertTrue(item.matches("h2", null, "/path2", false));
    assertTrue(item.matches("h3", null, "/path3", false));
    assertTrue(item.matches("h1", null, null, false));
    item.close();
  }

  @Test
  public void testMatchesHostnamesAndResourcePath() {
    HttpClientConfigImpl config = context.registerInjectActivateService(new HttpClientConfigImpl(),
        ImmutableMap.<String, Object>builder()
            .put(HOST_PATTERNS_PROPERTY, new String[] {
                "h1",
                "h2"
            })
            .put(RESOURCE_PATH_PROPERTY, new String[] {
                "/path1",
                "/path2"
            })
            .build());

    HttpClientItem item = new HttpClientItem(config);
    assertTrue(item.matches("h1", null, "/path1", false));
    assertTrue(item.matches("h1", null, "/path2", false));
    assertFalse(item.matches("h1", null, "/path3", false));
    assertFalse(item.matches("h1", null, null, false));
    assertTrue(item.matches("h2", null, "/path1", false));
    assertTrue(item.matches("h2", null, "/path2", false));
    assertFalse(item.matches("h2", null, "/path3", false));
    assertFalse(item.matches("h3", null, "/path1", false));
    assertFalse(item.matches("h3", null, "/path2", false));
    assertFalse(item.matches("h3", null, "/path3", false));
    item.close();
  }

  @Test
  public void testClientConnectionManager() {
    HttpClientConfigImpl config = context.registerInjectActivateService(new HttpClientConfigImpl(),
        ImmutableMap.<String, Object>builder()
        .put(MAX_CONNECTIONS_PER_HOST_PROPERTY, 9)
        .put(MAX_TOTAL_CONNECTIONS_PROPERTY, 99)
        .build());

    HttpClientItem item = new HttpClientItem(config);
    HttpClient client = item.getHttpClient();

    PoolingHttpClientConnectionManager connManager = HttpClientTestUtils.getConnectionManager(client);
    assertEquals(9, connManager.getDefaultMaxPerRoute());
    assertEquals(99, connManager.getMaxTotal());
    item.close();
  }

  @Test
  public void testTimeoutSettings() {
    HttpClientConfigImpl config = context.registerInjectActivateService(new HttpClientConfigImpl(),
        ImmutableMap.<String, Object>builder()
        .put(CONNECT_TIMEOUT_PROPERTY, 9)
        .put(SOCKET_TIMEOUT_PROPERTY, 99)
        .build());

    HttpClientItem item = new HttpClientItem(config);
    HttpClient client = item.getHttpClient();
    RequestConfig requestConfig = HttpClientTestUtils.getDefaultRequestConfig(client);
    assertEquals(9, requestConfig.getConnectTimeout());
    assertEquals(99, requestConfig.getSocketTimeout());
    item.close();
  }

  @Test
  public void testHttpAuthentication() {
    HttpClientConfigImpl config = context.registerInjectActivateService(new HttpClientConfigImpl(),
        ImmutableMap.<String, Object>builder()
        .put(HTTP_USER_PROPERTY, HTTP_USER_PROPERTY)
        .put(HTTP_PASSWORD_PROPERTY, "httpPasswd")
        .build());

    HttpClientItem item = new HttpClientItem(config);
    HttpClient client = item.getHttpClient();

    Credentials credentials = HttpClientTestUtils.getCredentialsProvider(client).getCredentials(AuthScope.ANY);
    assertNotNull(credentials);
    assertEquals(HTTP_USER_PROPERTY, credentials.getUserPrincipal().getName());
    assertEquals("httpPasswd", credentials.getPassword());
    item.close();
  }

  @Test
  public void testProxySettingsNoProxy() {
    HttpClientConfigImpl config = context.registerInjectActivateService(new HttpClientConfigImpl());

    HttpClientItem item = new HttpClientItem(config);
    HttpClient client = item.getHttpClient();

    HttpHost host = HttpClientTestUtils.getProxyHost(client);
    assertNull(host);

    Credentials credentials = HttpClientTestUtils.getCredentialsProvider(client).getCredentials(new AuthScope(config.getProxyHost(), config.getProxyPort()));
    assertNull(credentials);
    item.close();
  }

  @Test
  public void testProxySettingsProxy() {
    HttpClientConfigImpl config = context.registerInjectActivateService(new HttpClientConfigImpl(),
        ImmutableMap.<String, Object>builder()
        .put(PROXY_HOST_PROPERTY, "hostname")
        .put(PROXY_PORT_PROPERTY, 123)
        .build());

    HttpClientItem item = new HttpClientItem(config);
    HttpClient client = item.getHttpClient();

    HttpHost host = HttpClientTestUtils.getProxyHost(client);
    assertNotNull(host);
    assertEquals("hostname", host.getHostName());
    assertEquals(123, host.getPort());

    Credentials credentials = HttpClientTestUtils.getCredentialsProvider(client).getCredentials(new AuthScope(config.getProxyHost(), config.getProxyPort()));
    assertNull(credentials);
    item.close();
  }

  @Test
  public void testProxySettingsProxyWithAuthentication() {
    HttpClientConfigImpl config = context.registerInjectActivateService(new HttpClientConfigImpl(),
        ImmutableMap.<String, Object>builder()
        .put(PROXY_HOST_PROPERTY, "hostname")
        .put(PROXY_PORT_PROPERTY, 123)
        .put(PROXY_USER_PROPERTY, "proxyuser")
        .put(PROXY_PASSWORD_PROPERTY, "proxypassword")
        .build());

    HttpClientItem item = new HttpClientItem(config);
    HttpClient client = item.getHttpClient();

    HttpHost host = HttpClientTestUtils.getProxyHost(client);
    assertNotNull(host);
    assertEquals("hostname", host.getHostName());
    assertEquals(123, host.getPort());

    Credentials credentials = HttpClientTestUtils.getCredentialsProvider(client).getCredentials(new AuthScope(config.getProxyHost(), config.getProxyPort()));
    assertNotNull(credentials);
    assertEquals("proxyuser", credentials.getUserPrincipal().getName());
    assertEquals("proxypassword", credentials.getPassword());
    item.close();
  }

  @Test
  public void testWithClientCertificate() {
    HttpClientConfigImpl config = context.registerInjectActivateService(new HttpClientConfigImpl(),
        ImmutableMap.<String, Object>builder()
        .put(KEYSTORE_PATH_PROPERTY, CertificateLoaderTest.KEYSTORE_PATH)
        .put(KEYSTORE_PASSWORD_PROPERTY, CertificateLoaderTest.KEYSTORE_PASSWORD)
        .put(TRUSTSTORE_PATH_PROPERTY, CertificateLoaderTest.TRUSTSTORE_PATH)
        .put(TRUSTSTORE_PASSWORD_PROPERTY, CertificateLoaderTest.TRUSTSTORE_PASSWORD)
        .build());

    HttpClientItem item = new HttpClientItem(config);
    HttpClient client = item.getHttpClient();

    Registry<ConnectionSocketFactory> schemeRegistry = HttpClientTestUtils.getSchemeRegistry(client);
    ConnectionSocketFactory schemeSocketFactory = schemeRegistry.lookup("https");

    assertNotEquals(schemeSocketFactory, SSLConnectionSocketFactory.getSocketFactory());
    item.close();
  }

}
