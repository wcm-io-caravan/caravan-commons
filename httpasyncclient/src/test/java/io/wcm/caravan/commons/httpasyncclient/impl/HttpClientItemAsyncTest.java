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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.Registry;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager;
import org.apache.http.nio.client.HttpAsyncClient;
import org.apache.http.nio.conn.SchemeIOSessionStrategy;
import org.apache.sling.testing.mock.osgi.junit.OsgiContext;
import org.junit.Rule;
import org.junit.Test;

import com.google.common.collect.ImmutableMap;

import io.wcm.caravan.commons.httpclient.impl.HttpClientConfigImpl;

public class HttpClientItemAsyncTest {

  @Rule
  public OsgiContext context = new OsgiContext();

  @Test
  public void testClientConnectionManager() {
    HttpClientConfigImpl config = context.registerInjectActivateService(new HttpClientConfigImpl(),
        ImmutableMap.<String, Object>builder()
            .put("maxConnectionsPerHost", 9)
            .put("maxTotalConnections", 99)
            .build());

    HttpAsyncClientItem item = new HttpAsyncClientItem(config);
    HttpAsyncClient client = item.getHttpAsyncClient();

    PoolingNHttpClientConnectionManager connManager = HttpClientTestUtils.getConnectionManager(client);
    assertEquals(9, connManager.getDefaultMaxPerRoute());
    assertEquals(99, connManager.getMaxTotal());
    item.close();
  }

  @Test
  public void testTimeoutSettings() {
    HttpClientConfigImpl config = context.registerInjectActivateService(new HttpClientConfigImpl(),
        ImmutableMap.<String, Object>builder()
            .put("connectionRequestTimeout", 5)
            .put("connectTimeout", 9)
            .put("socketTimeout", 99)
            .build());

    HttpAsyncClientItem item = new HttpAsyncClientItem(config);
    HttpAsyncClient client = item.getHttpAsyncClient();
    RequestConfig requestConfig = HttpClientTestUtils.getDefaultRequestConfig(client);
    assertEquals(5, requestConfig.getConnectionRequestTimeout());
    assertEquals(9, requestConfig.getConnectTimeout());
    assertEquals(99, requestConfig.getSocketTimeout());
    item.close();
  }

  @Test
  public void testHttpAuthentication() {
    HttpClientConfigImpl config = context.registerInjectActivateService(new HttpClientConfigImpl(),
        ImmutableMap.<String, Object>builder()
            .put("httpUser", "httpUsr")
            .put("httpPassword", "httpPasswd")
            .build());

    HttpAsyncClientItem item = new HttpAsyncClientItem(config);
    HttpAsyncClient client = item.getHttpAsyncClient();

    Credentials credentials = HttpClientTestUtils.getCredentialsProvider(client).getCredentials(AuthScope.ANY);
    assertNotNull(credentials);
    assertEquals("httpUsr", credentials.getUserPrincipal().getName());
    assertEquals("httpPasswd", credentials.getPassword());
    item.close();
  }

  @Test
  public void testProxySettingsNoProxy() {
    HttpClientConfigImpl config = context.registerInjectActivateService(new HttpClientConfigImpl());

    HttpAsyncClientItem item = new HttpAsyncClientItem(config);
    HttpAsyncClient client = item.getHttpAsyncClient();

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
            .put("proxyHost", "hostname")
            .put("proxyPort", 123)
            .build());

    HttpAsyncClientItem item = new HttpAsyncClientItem(config);
    HttpAsyncClient client = item.getHttpAsyncClient();

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
            .put("proxyHost", "hostname")
            .put("proxyPort", 123)
            .put("proxyUser", "proxyuser")
            .put("proxyPassword", "proxypassword")
            .build());

    HttpAsyncClientItem item = new HttpAsyncClientItem(config);
    HttpAsyncClient client = item.getHttpAsyncClient();

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
            .put("keyStorePath", CertificateLoaderTestProps.KEYSTORE_PATH)
            .put("keyStorePassword", CertificateLoaderTestProps.KEYSTORE_PASSWORD)
            .put("trustStorePath", CertificateLoaderTestProps.TRUSTSTORE_PATH)
            .put("trustStorePassword", CertificateLoaderTestProps.TRUSTSTORE_PASSWORD)
            .build());

    HttpAsyncClientItem item = new HttpAsyncClientItem(config);
    HttpAsyncClient client = item.getHttpAsyncClient();

    Registry<SchemeIOSessionStrategy> schemeRegistry = HttpClientTestUtils.getSchemeRegistry(client);
    SchemeIOSessionStrategy schemeSocketFactory = schemeRegistry.lookup("https");

    assertNotEquals(schemeSocketFactory, SSLConnectionSocketFactory.getSocketFactory());
    item.close();
  }

  @Test
  public void testWithCookieSpec() {
    HttpClientConfigImpl config = context.registerInjectActivateService(new HttpClientConfigImpl(),
        ImmutableMap.<String, Object>builder()
            .put("cookieSpec", CookieSpecs.IGNORE_COOKIES)
            .build());

    HttpAsyncClientItem item = new HttpAsyncClientItem(config);
    HttpAsyncClient client = item.getHttpAsyncClient();
    RequestConfig requestConfig = HttpClientTestUtils.getDefaultRequestConfig(client);
    assertEquals(CookieSpecs.IGNORE_COOKIES, requestConfig.getCookieSpec());
  }

}
