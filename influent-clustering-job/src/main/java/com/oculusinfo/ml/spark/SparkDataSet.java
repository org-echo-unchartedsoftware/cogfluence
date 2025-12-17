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
package com.oculusinfo.ml.spark;

import com.oculusinfo.ml.Instance;
import java.io.Serializable;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaSparkContext;

/**
 * Spark-compatible DataSet wrapper that holds instances in a JavaPairRDD. This class bridges the
 * ensemble-clustering library with Apache Spark.
 */
public class SparkDataSet implements Serializable {
  private static final long serialVersionUID = 1L;

  private final JavaSparkContext sparkContext;
  private JavaPairRDD<String, Instance> rdd;

  public SparkDataSet(JavaSparkContext sc) {
    this.sparkContext = sc;
  }

  /** Load instances from an existing RDD. */
  public void load(JavaPairRDD<String, Instance> instances) {
    this.rdd = instances;
  }

  /** Get the underlying RDD. */
  public JavaPairRDD<String, Instance> getRDD() {
    return rdd;
  }

  /** Get the Spark context. */
  public JavaSparkContext getSparkContext() {
    return sparkContext;
  }

  /** Get the count of instances. */
  public long count() {
    return rdd != null ? rdd.count() : 0;
  }

  /**
   * Load instances from a file using a parser.
   *
   * @param inputPath Path to the input file
   * @param parser Parser to convert lines to instances
   */
  public void load(String inputPath, SparkInstanceParser parser) {
    this.rdd = SparkInstanceParserHelper.parseFile(sparkContext, inputPath, parser);
  }
}
