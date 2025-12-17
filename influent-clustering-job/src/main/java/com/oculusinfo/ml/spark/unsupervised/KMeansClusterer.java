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
import com.oculusinfo.ml.centroid.Centroid;
import com.oculusinfo.ml.distance.DistanceFunction;
import com.oculusinfo.ml.feature.Feature;
import com.oculusinfo.ml.spark.SparkDataSet;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.mllib.clustering.KMeans;
import org.apache.spark.mllib.clustering.KMeansModel;
import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.mllib.linalg.Vectors;
import scala.Tuple2;

/**
 * K-Means clustering implementation using Apache Spark MLlib. This class provides a bridge between
 * the ensemble-clustering API and Spark MLlib.
 */
public class KMeansClusterer implements Serializable {
  private static final long serialVersionUID = 1L;

  private final int k;
  private final int maxIterations;
  private final double convergenceTol;

  private final Map<String, FeatureTypeDefinition> featureTypes = new HashMap<>();

  /**
   * Create a new K-Means clusterer.
   *
   * @param k Number of clusters
   * @param maxIterations Maximum number of iterations
   * @param convergenceTol Convergence tolerance (stopping threshold)
   */
  public KMeansClusterer(int k, int maxIterations, double convergenceTol) {
    this.k = k;
    this.maxIterations = maxIterations;
    this.convergenceTol = convergenceTol;
  }

  /**
   * Register a feature type with its centroid class and distance function.
   *
   * @param featureName Name of the feature
   * @param featureType Type identifier for the feature
   * @param centroidClass Class to use for computing centroids
   * @param distanceFunction Distance function for this feature type
   */
  public void registerFeatureType(
      String featureName,
      String featureType,
      Class<? extends Centroid> centroidClass,
      DistanceFunction distanceFunction) {
    featureTypes.put(
        featureName, new FeatureTypeDefinition(featureType, centroidClass, distanceFunction));
  }

  /**
   * Perform K-Means clustering on the dataset.
   *
   * @param dataset The SparkDataSet to cluster
   * @return SparkClusterResult containing the clustered instances
   */
  public SparkClusterResult doCluster(SparkDataSet dataset) {
    JavaPairRDD<String, Instance> inputRDD = dataset.getRDD();

    // Convert instances to feature vectors for MLlib
    JavaRDD<Tuple2<String, Vector>> vectorRDD =
        inputRDD.map(
            tuple -> {
              Instance inst = tuple._2;
              double[] features = extractFeatureVector(inst);
              return new Tuple2<>(tuple._1, Vectors.dense(features));
            });

    // Cache the RDD for iterative algorithm
    vectorRDD.cache();

    // Extract just the vectors for training
    JavaRDD<Vector> vectors = vectorRDD.map(t -> t._2);

    // Train K-Means model using Spark MLlib
    KMeansModel model = KMeans.train(vectors.rdd(), k, maxIterations);

    // Predict cluster assignments
    final KMeansModel broadcastModel = model;

    JavaPairRDD<String, Instance> clusteredRDD =
        inputRDD.mapToPair(
            new PairFunction<Tuple2<String, Instance>, String, Instance>() {
              private static final long serialVersionUID = 1L;

              @Override
              public Tuple2<String, Instance> call(Tuple2<String, Instance> tuple) {
                Instance inst = tuple._2;
                double[] features = extractFeatureVector(inst);
                Vector vec = Vectors.dense(features);
                int cluster = broadcastModel.predict(vec);
                return new Tuple2<>("cluster-" + cluster, inst);
              }
            });

    return new SparkClusterResult(clusteredRDD, k);
  }

  /**
   * Extract a numeric feature vector from an Instance. This converts various feature types to a
   * double array for MLlib.
   */
  private static double[] extractFeatureVector(Instance inst) {
    List<Double> values = new ArrayList<>();

    for (String featureName : inst.getFeatures().keySet()) {
      Feature feature = inst.getFeature(featureName);
      if (feature != null) {
        // Handle different feature types
        if (feature instanceof com.oculusinfo.ml.feature.numeric.NumericVectorFeature) {
          com.oculusinfo.ml.feature.numeric.NumericVectorFeature nvf =
              (com.oculusinfo.ml.feature.numeric.NumericVectorFeature) feature;
          for (double v : nvf.getValue()) {
            values.add(v);
          }
        } else if (feature instanceof com.oculusinfo.ml.feature.spatial.GeoSpatialFeature) {
          com.oculusinfo.ml.feature.spatial.GeoSpatialFeature gsf =
              (com.oculusinfo.ml.feature.spatial.GeoSpatialFeature) feature;
          values.add(gsf.getLatitude());
          values.add(gsf.getLongitude());
        } else {
          // For other features, use hash code as a simple numeric representation
          values.add((double) feature.hashCode() % 1000);
        }
      }
    }

    // Ensure we have at least one feature
    if (values.isEmpty()) {
      values.add(0.0);
    }

    return values.stream().mapToDouble(Double::doubleValue).toArray();
  }

  /** Internal class to hold feature type definitions. */
  private static class FeatureTypeDefinition implements Serializable {
    private static final long serialVersionUID = 1L;

    final String featureType;
    final Class<? extends Centroid> centroidClass;
    final DistanceFunction distanceFunction;

    FeatureTypeDefinition(
        String featureType,
        Class<? extends Centroid> centroidClass,
        DistanceFunction distanceFunction) {
      this.featureType = featureType;
      this.centroidClass = centroidClass;
      this.distanceFunction = distanceFunction;
    }
  }
}
