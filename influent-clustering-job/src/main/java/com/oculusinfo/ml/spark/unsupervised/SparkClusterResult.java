/*
 * Copyright 2013-2016 Uncharted Software Inc.
 *
 *  Property of Uncharted(TM), formerly Oculus Info Inc.
 *  https://uncharted.software/
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.oculusinfo.ml.spark.unsupervised;

import com.oculusinfo.ml.Instance;
import java.io.Serializable;
import org.apache.spark.api.java.JavaPairRDD;

/**
 * Result of a Spark clustering operation. Contains the clustered instances with their cluster
 * assignments.
 */
public class SparkClusterResult implements Serializable {
  private static final long serialVersionUID = 1L;

  private final JavaPairRDD<String, Instance> clusteredRDD;
  private final int numClusters;

  public SparkClusterResult(JavaPairRDD<String, Instance> rdd, int numClusters) {
    this.clusteredRDD = rdd;
    this.numClusters = numClusters;
  }

  /**
   * Get the RDD containing instances with their cluster assignments. The key is the cluster ID, the
   * value is the Instance.
   */
  public JavaPairRDD<String, Instance> getRDD() {
    return clusteredRDD;
  }

  /** Get the number of clusters found. */
  public int getNumClusters() {
    return numClusters;
  }

  /** Get the count of instances in the result. */
  public long count() {
    return clusteredRDD != null ? clusteredRDD.count() : 0;
  }
}
