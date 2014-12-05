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
package io.wcm.caravan.commons.couchbase.impl;

import io.wcm.caravan.commons.couchbase.CouchbaseClientProvider;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.commons.osgi.PropertiesUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.couchbase.client.java.AsyncBucket;
import com.couchbase.client.java.Cluster;

/**
 * Default implementation of {@link CouchbaseClientProvider}.
 */
@Component(immediate = true, metatype = true,
label = "wcm.io Caravan Couchbase Client Provider",
description = "Provides access to a preconfigured couchbase client.")
@Service(CouchbaseClientProvider.class)
public class CouchbaseClientProviderImpl implements CouchbaseClientProvider {

  @Property(label = "Enabled", description = "Enable or disable couchbase caching.",
      boolValue = CouchbaseClientProviderImpl.ENABLED_PROPERTY_DEFAULT)
  private static final String ENABLED_PROPERTY = "enabled";
  private static final boolean ENABLED_PROPERTY_DEFAULT = true;

  @Property(label = "Couchbase Hosts", description = "Couchbase cluster host list.",
      cardinality = Integer.MAX_VALUE)
  private static final String COUCHBASE_HOSTS_PROPERTY = "couchbaseHosts";

  @Property(label = "Cache Bucket Name", description = "Couchbase bucket name for caching data")
  private static final String CACHE_BUCKET_NAME_PROPERTY = "cacheBucketName";

  @Property(label = "Cache Bucket Password", description = "Couchbase bucket password for caching data")
  private static final String CACHE_BUCKET_PASSWORD_PROPERTY = "cacheBucketPassword";

  private static final Logger log = LoggerFactory.getLogger(CouchbaseClientProviderImpl.class);

  private boolean enabled;
  private Cluster cluster;
  private AsyncBucket cacheBucket;

  @Activate
  private void activate(Map<String, Object> config) {
    enabled = PropertiesUtil.toBoolean(config.get(ENABLED_PROPERTY), ENABLED_PROPERTY_DEFAULT);
    if (!enabled) {
      log.info("Couchbase caching is disabled by configuration.");
      return;
    }

    String[] couchbaseHosts = PropertiesUtil.toStringArray(config.get(COUCHBASE_HOSTS_PROPERTY));
    if (couchbaseHosts == null || couchbaseHosts.length == 0) {
      log.warn("No couchbase host configured, caching is disabled.");
      return;
    }

    String cacheBucketName = PropertiesUtil.toString(config.get(CACHE_BUCKET_NAME_PROPERTY), null);
    String cacheBucketPassword = PropertiesUtil.toString(config.get(CACHE_BUCKET_PASSWORD_PROPERTY), null);
    if (StringUtils.isEmpty(cacheBucketName)) {
      log.warn("No couchbase bucket name configured, caching is disabled.");
      return;
    }

    try {
      cluster = CouchbaseUtil.createCluster(couchbaseHosts);
      cacheBucket = CouchbaseUtil.openBucket(cluster, cacheBucketName, cacheBucketPassword);
    }
    catch (Throwable ex) {
      log.warn("Unable to connect to couchbase cluster or open couchbase bucket, caching is disabled.", ex);
    }
  }

  @Deactivate
  private void deactivate() {
    if (cacheBucket != null) {
      cacheBucket.close();
      cacheBucket = null;
    }
    if (cluster != null) {
      cluster.disconnect();
      cluster = null;
    }
  }

  @Override
  public boolean isEnabled() {
    return enabled && cacheBucket != null;
  }

  @Override
  public AsyncBucket getCacheBucket() {
    return cacheBucket;
  }

}
