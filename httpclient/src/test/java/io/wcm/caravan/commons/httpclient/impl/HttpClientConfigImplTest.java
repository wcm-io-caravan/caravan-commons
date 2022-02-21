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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.apache.sling.testing.mock.osgi.junit.OsgiContext;
import org.junit.Rule;
import org.junit.Test;

import com.google.common.collect.ImmutableMap;

import io.wcm.caravan.commons.httpclient.HttpClientConfig;
import io.wcm.caravan.commons.httpclient.impl.helpers.CertificateLoader;

public class HttpClientConfigImplTest {

  @Rule
  public OsgiContext context = new OsgiContext();

  @Test
  public void testDefaultValues() {
    HttpClientConfigImpl config = context.registerInjectActivateService(new HttpClientConfigImpl());

    assertEquals("connectionRequestTimeout", HttpClientConfig.CONNECTION_REQUEST_TIMEOUT_DEFAULT, config.getConnectionRequestTimeout());
    assertEquals("connectTimeout", HttpClientConfig.CONNECT_TIMEOUT_DEFAULT, config.getConnectTimeout());
    assertEquals("socketTimeout", HttpClientConfig.SOCKET_TIMEOUT_DEFAULT, config.getSocketTimeout());
    assertEquals("maxConnectionsPerHost", HttpClientConfig.MAX_CONNECTIONS_PER_HOST_DEFAULT, config.getMaxConnectionsPerHost());
    assertEquals("maxTotalConnections", HttpClientConfig.MAX_TOTAL_CONNECTIONS_DEFAULT, config.getMaxTotalConnections());
    assertNull("httpUser", config.getHttpUser());
    assertNull("httpPassword", config.getHttpPassword());
    assertNull("proxyHost", config.getProxyHost());
    assertEquals("proxyPort", 0, config.getProxyPort());
    assertNull("proxyUser", config.getProxyUser());
    assertNull("proxyPassword", config.getProxyPassword());
    assertTrue("matchesHost", config.matchesHost("h1"));
    assertTrue("matchesWsAddressingToUri", config.matchesWsAddressingToUri("http://uri1"));

    assertEquals("sslContextType", CertificateLoader.SSL_CONTEXT_TYPE_DEFAULT, config.getSslContextType());
    assertEquals("keyManagerType", CertificateLoader.KEY_MANAGER_TYPE_DEFAULT, config.getKeyManagerType());
    assertEquals("keyStoreType", CertificateLoader.KEY_STORE_TYPE_DEFAULT, config.getKeyStoreType());
    assertNull("keyStoreProvider", config.getKeyStoreProvider());
    assertNull("keyStorePath", config.getKeyStorePath());
    assertNull("keyStorePassword", config.getKeyStorePassword());
    assertEquals("trustManagerType", CertificateLoader.TRUST_MANAGER_TYPE_DEFAULT, config.getTrustManagerType());
    assertEquals("trustStoreType", CertificateLoader.TRUST_STORE_TYPE_DEFAULT, config.getTrustStoreType());
    assertNull("trustStoreProvider", config.getTrustStoreProvider());
    assertNull("trustStorePath", config.getTrustStorePath());
    assertNull("trustStorePassword", config.getTrustStorePassword());

    assertNotNull(config.toString());
  }

  @Test
  public void testReadFromConfig() {
    HttpClientConfigImpl config = context.registerInjectActivateService(new HttpClientConfigImpl(),
        ImmutableMap.<String, Object>builder()
            .put("connectionRequestTimeout", 5)
            .put("connectTimeout", 1)
            .put("socketTimeout", 2)
            .put("maxConnectionsPerHost", 3)
            .put("maxTotalConnections", 4)
            .put("httpUser", "httpUsr")
            .put("httpPassword", "httpPwd")
            .put("proxyHost", "abc")
            .put("proxyPort", 5)
            .put("proxyUser", "def")
            .put("proxyPassword", "ghi")
            .put("hostPatterns", new String[] {
                "h1",
                "h2",
                "h3"
            })
            .put("wsAddressingToUris", new String[] {
                "http://uri1",
                "http://uri2"
            })
            .put("sslContextType", "ssltype")
            .put("keyManagerType", "keymantype")
            .put("keyStoreType", "keystoretype")
            .put("keyStoreProvider", "keystoreprvdr")
            .put("keyStorePath", "keypath")
            .put("keyStorePassword", "keypasswd")
            .put("trustManagerType", "trustmantype")
            .put("trustStoreType", "truststoretype")
            .put("trustStoreProvider", "truststoreprvdr")
            .put("trustStorePath", "trustpath")
            .put("trustStorePassword", "trustpasswd")
            .build());

    assertEquals("connectionRequestTimeout", 5, config.getConnectionRequestTimeout());
    assertEquals("connectTimeout", 1, config.getConnectTimeout());
    assertEquals("socketTimeout", 2, config.getSocketTimeout());
    assertEquals("maxConnectionsPerHost", 3, config.getMaxConnectionsPerHost());
    assertEquals("maxTotalConnections", 4, config.getMaxTotalConnections());
    assertEquals("httpUser", "httpUsr", config.getHttpUser());
    assertEquals("httpPassword", "httpPwd", config.getHttpPassword());
    assertEquals("proxyHost", "abc", config.getProxyHost());
    assertEquals("proxyPort", 5, config.getProxyPort());
    assertEquals("proxyUser", "def", config.getProxyUser());
    assertEquals("proxyPassword", "ghi", config.getProxyPassword());
    assertTrue("hostNames.0", config.matchesHost("h1"));
    assertTrue("hostNames.1", config.matchesHost("h2"));
    assertTrue("hostNames.2", config.matchesHost("h3"));
    assertFalse("hostNames.false", config.matchesHost("h4"));
    assertTrue("wsAddressingToUris.0", config.matchesWsAddressingToUri("http://uri1"));
    assertTrue("wsAddressingToUris.1", config.matchesWsAddressingToUri("http://uri2"));
    assertFalse("wsAddressingToUris.false", config.matchesWsAddressingToUri("http://uri3"));

    assertEquals("sslContextType", "ssltype", config.getSslContextType());
    assertEquals("keyManagerType", "keymantype", config.getKeyManagerType());
    assertEquals("keyStoreType", "keystoretype", config.getKeyStoreType());
    assertEquals("keyStoreProvider", "keystoreprvdr", config.getKeyStoreProvider());
    assertEquals("keyStorePath", "keypath", config.getKeyStorePath());
    assertEquals("keyStorePassword", "keypasswd", config.getKeyStorePassword());
    assertEquals("trustManagerType", "trustmantype", config.getTrustManagerType());
    assertEquals("trustStoreType", "truststoretype", config.getTrustStoreType());
    assertEquals("trustStoreProvider", "truststoreprvdr", config.getTrustStoreProvider());
    assertEquals("trustStorePath", "trustpath", config.getTrustStorePath());
    assertEquals("trustStorePassword", "trustpasswd", config.getTrustStorePassword());

    assertNotNull(config.toString());
  }

}
