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

import org.apache.commons.lang3.StringUtils;

import com.couchbase.client.java.AsyncBucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.CouchbaseCluster;
import com.couchbase.client.java.env.CouchbaseEnvironment;
import com.couchbase.client.java.env.DefaultCouchbaseEnvironment;

/**
 * Manages couchbase environment.
 */
final class CouchbaseUtil {

  private static final CouchbaseEnvironment COUCHBASE_ENVIRONMENT = DefaultCouchbaseEnvironment.builder()
      .build();

  private CouchbaseUtil() {
    // static methods only
  }

  /**
   * Create new couchbase cluster.
   * @return Couchbase cluster
   */
  public static Cluster createCluster(String[] hostNames) {
    return CouchbaseCluster.create(COUCHBASE_ENVIRONMENT, hostNames);
  }

  /**
   * Open couchbase bucket.
   * @param cluster Couchbase cluster
   * @param bucketName Bucker name
   * @param bucketPassword Bucker password (optional)
   * @return Couchbase bucket
   */
  public static AsyncBucket openBucket(Cluster cluster, String bucketName, String bucketPassword) {
    if (StringUtils.isNotEmpty(bucketPassword)) {
      return cluster.openBucket(bucketName, bucketPassword).async();
    }
    else {
      return cluster.openBucket(bucketName).async();
    }
  }

}
