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
import com.oculusinfo.ml.feature.bagofwords.BagOfWordsFeature;
import com.oculusinfo.ml.feature.numeric.NumericVectorFeature;
import com.oculusinfo.ml.feature.spatial.GeoSpatialFeature;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.PairFunction;
import scala.Tuple2;

/**
 * Helper class for parsing input files into Instance RDDs using Spark. Also provides methods for
 * parsing serialized Instance strings.
 */
public class SparkInstanceParserHelper implements Serializable {
  private static final long serialVersionUID = 1L;

  private Map<String, String> fields;

  /** Default constructor for static utility methods. */
  public SparkInstanceParserHelper() {
    this.fields = new HashMap<>();
  }

  /**
   * Constructor that parses a serialized Instance string. Expected format: Instance[id=xxx,
   * features={name=value, ...}]
   *
   * @param serializedInstance The serialized instance string to parse
   */
  public SparkInstanceParserHelper(String serializedInstance) {
    this.fields = new HashMap<>();
    parseInstanceString(serializedInstance);
  }

  /** Parse a serialized Instance string into field values. */
  private void parseInstanceString(String str) {
    // Try to extract key=value pairs from the string
    // Pattern matches: fieldName=value or fieldName={...}
    Pattern pattern = Pattern.compile("(\\w+)=([^,}]+|\\{[^}]*\\})");
    Matcher matcher = pattern.matcher(str);

    while (matcher.find()) {
      String key = matcher.group(1);
      String value = matcher.group(2).trim();
      // Remove surrounding braces if present
      if (value.startsWith("{") && value.endsWith("}")) {
        value = value.substring(1, value.length() - 1);
      }
      fields.put(key, value);
    }
  }

  /** Get a field value as a String. */
  public String fieldToString(String fieldName) {
    return fields.get(fieldName);
  }

  /** Get a field value as a BagOfWordsFeature. */
  public BagOfWordsFeature fieldToBagOfWordsFeature(String fieldName) {
    String value = fields.get(fieldName);
    if (value == null || value.isEmpty()) {
      return null;
    }
    BagOfWordsFeature feature = new BagOfWordsFeature(fieldName);
    // Parse comma-separated words
    for (String word : value.split(",")) {
      word = word.trim();
      if (!word.isEmpty()) {
        feature.incrementValue(word);
      }
    }
    return feature;
  }

  /** Get a field value as a GeoSpatialFeature. */
  public GeoSpatialFeature fieldToGeoSpatialFeature(String fieldName) {
    String value = fields.get(fieldName);
    if (value == null || value.isEmpty()) {
      return null;
    }
    try {
      // Expected format: "lat;lon" or "lat,lon"
      String[] parts = value.split("[;,]");
      if (parts.length >= 2) {
        double lat = Double.parseDouble(parts[0].trim());
        double lon = Double.parseDouble(parts[1].trim());
        GeoSpatialFeature feature = new GeoSpatialFeature(fieldName);
        feature.setValue(lat, lon);
        return feature;
      }
    } catch (NumberFormatException e) {
      // Invalid format, return null
    }
    return null;
  }

  /** Get a field value as a NumericVectorFeature. */
  public NumericVectorFeature fieldToNumericVectorFeature(String fieldName) {
    String value = fields.get(fieldName);
    if (value == null || value.isEmpty()) {
      return null;
    }
    try {
      // Parse as comma-separated numbers or single number
      String[] parts = value.split(",");
      double[] values = new double[parts.length];
      for (int i = 0; i < parts.length; i++) {
        values[i] = Double.parseDouble(parts[i].trim());
      }
      NumericVectorFeature feature = new NumericVectorFeature(fieldName);
      feature.setValue(values);
      return feature;
    } catch (NumberFormatException e) {
      // Invalid format, return null
    }
    return null;
  }

  /**
   * Parse a text file into a JavaPairRDD of instances.
   *
   * @param sc The Spark context
   * @param inputPath Path to the input file (can be HDFS or local)
   * @param parser The parser to use for converting lines to instances
   * @return A JavaPairRDD containing (instanceId, Instance) tuples
   */
  public static JavaPairRDD<String, Instance> parseFile(
      JavaSparkContext sc, String inputPath, final SparkInstanceParser parser) {

    JavaRDD<String> lines = sc.textFile(inputPath);

    return lines
        .mapToPair(
            new PairFunction<String, String, Instance>() {
              private static final long serialVersionUID = 1L;

              @Override
              public Tuple2<String, Instance> call(String line) throws Exception {
                return parser.parse(line);
              }
            })
        .filter(tuple -> tuple != null && tuple._2 != null);
  }

  /**
   * Save instances to a text file.
   *
   * @param rdd The RDD to save
   * @param outputPath Path to the output directory
   */
  public static void saveToFile(JavaPairRDD<String, Instance> rdd, String outputPath) {
    rdd.map(tuple -> tuple._1 + "\t" + tuple._2.toString()).saveAsTextFile(outputPath);
  }
}
