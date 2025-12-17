# Plan for Remediating the `influent-clustering-job` Module

**Author:** Manus AI
**Date:** December 17, 2025

## 1. Introduction

The `influent-clustering-job` module in the `cogfluence` repository is currently disabled due to several missing and outdated dependencies. This document outlines a high-level plan to remediate these issues, with the goal of making the module buildable and functional within the existing project structure.

## 2. Problem Analysis

The module's `pom.xml` declares three problematic dependencies:

| Group ID | Artifact ID | Version | Issue |
| :--- | :--- | :--- | :--- |
| `com.oculusinfo.ml` | `ml` | `0.0.14-SNAPSHOT` | Internal dependency, not available in any public repository. |
| `org.apache.hadoop` | `hadoop-core` | `2.6.0-mr1-cdh5.16.99` | Legacy Cloudera distribution, not available in public repositories. |
| `org.spark-project` | `spark-core_2.9.3` | `0.7.3` | Extremely outdated Spark version (from ~2013), incompatible with modern Java and Spark ecosystems. |

Analysis of the source code reveals that the `com.oculusinfo.ml` dependency is used for core machine learning abstractions (`Instance`, `Feature`, `DistanceFunction`, etc.) and for a Spark-based K-Means implementation. The other two dependencies support this legacy Spark implementation.

## 3. Remediation Plan

I propose a four-step plan to resolve these dependencies and modernize the module.

### Step 1: Integrate Local `ensemble-clustering` Module

My analysis shows that the `ensemble-clustering` module, which is present in this same repository, contains the source code for the non-Spark-related classes of the `com.oculusinfo.ml` library. The first step is to treat this as a local dependency.

**Actions:**
1.  Add `ensemble-clustering` as a `<dependency>` in the `influent-clustering-job/pom.xml`.
2.  This will immediately resolve all `com.oculusinfo.ml` imports except for the Spark-specific classes (`SparkDataSet`, `SparkInstanceParser`, `KMeansClusterer`, etc.).

### Step 2: Modernize Spark and Hadoop Dependencies

The legacy Spark 0.7.3 and Cloudera Hadoop dependencies must be replaced with modern, stable, and publicly available versions.

**Actions:**
1.  Remove the `org.spark-project:spark-core_2.9.3` and `org.apache.hadoop:hadoop-core` dependencies from the `pom.xml`.
2.  Add dependencies for a modern Spark version. I recommend using Spark 3.x to align with current industry standards. For example, Spark 3.4.2, which is compatible with Scala 2.12 and Java 17.

```xml
<dependency>
    <groupId>org.apache.spark</groupId>
    <artifactId>spark-core_2.12</artifactId>
    <version>3.4.2</version>
</dependency>
<dependency>
    <groupId>org.apache.spark</groupId>
    <artifactId>spark-mllib_2.12</artifactId>
    <version>3.4.2</version>
</dependency>
```

3.  The existing `hadoop-client` dependency should be sufficient for interacting with HDFS, and its version should be kept consistent with the Spark version's requirements.

### Step 3: Refactor Clustering Logic to Use Modern Spark MLlib

This is the most significant step. The clustering logic in `ClusterEntities.java` must be refactored to use the modern Spark MLlib API, which is based on `DataFrame`s and `SparkSession`.

**Actions:**
1.  **Replace `JavaSparkContext` with `SparkSession`:** The entry point for modern Spark applications is the `SparkSession`.
2.  **Migrate from RDDs to DataFrames/Datasets:** The core data abstraction will shift from `JavaPairRDD<String, Instance>` to `Dataset<Row>`.
3.  **Implement a New `KMeans` Clustering Flow:** The legacy `com.oculusinfo.ml.spark.unsupervised.KMeansClusterer` will be replaced by `org.apache.spark.ml.clustering.KMeans` from the standard Spark MLlib library [1]. This involves:
    *   Creating a `VectorAssembler` to transform input feature columns into a single feature vector column.
    *   Initializing and training the `KMeans` model.
    *   Transforming the dataset to get cluster assignments.
4.  **Adapt Custom Distance and Centroid Logic:** The custom distance functions (`EditDistance`, `HaversineDistance`) and centroid calculation logic from the `ensemble-clustering` module will need to be adapted to work within the new Spark MLlib pipeline, likely through the use of User Defined Functions (UDFs) if direct integration is not possible.

### Step 4: Build, Test, and Enable the Module

After the refactoring is complete, the final step is to ensure the module builds and integrates with the rest of the project.

**Actions:**
1.  Attempt to build the `influent-clustering-job` module in isolation.
2.  Once it builds successfully, enable the module in the root `pom.xml`.
3.  Perform a full project build (`mvn clean install -DskipTests`) to ensure no new integration issues have been introduced.

## 4. Conclusion

By following this four-step plan, the `influent-clustering-job` module can be modernized and re-enabled. The key challenges will be the refactoring of the Spark clustering logic and the potential need to adapt the custom feature logic to the modern Spark MLlib API. The result will be a more maintainable, secure, and performant module that relies on standard, publicly available libraries.

---

### References

[1] Apache Spark. "Clustering - RDD-based API - Spark 4.0.1 Documentation". [https://spark.apache.org/docs/latest/mllib-clustering.html](https://spark.apache.org/docs/latest/mllib-clustering.html)
