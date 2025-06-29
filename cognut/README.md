# Cognut Clustering Strategies

The Cognut module provides advanced clustering algorithms implementing mathematical foundations and novel clustering approaches. This module extends the existing clustering framework with sophisticated strategies for complex data analysis.

## Clustering Strategies

### 1. Universal Fractal Partition Schema

**Class:** `UniversalFractalPartitionSchema`

A clustering algorithm that uses fractal geometry principles to partition data into self-similar clusters at multiple scales. The algorithm recursively divides the data space using fractal patterns that preserve the underlying geometric structure.

**Parameters:**
- `maxDepth`: Maximum recursion depth for fractal partitioning
- `fractalDimension`: The fractal dimension for partitioning (typically 1.0-3.0)
- `partitionThreshold`: Threshold for determining when to stop partitioning

**Usage:**
```java
UniversalFractalPartitionSchema clusterer = new UniversalFractalPartitionSchema(
    5,      // maxDepth
    2.0,    // fractalDimension
    0.3,    // partitionThreshold
    false   // penalizeMissingFeatures
);
```

### 2. Undecadic Clustering of 48 Heptavertex Trees

**Class:** `UndecadicHeptavertexClustering`

This algorithm operates on undecadic (base-11) number systems and structures data as heptavertex (7-vertex) trees. It partitions data into 48 distinct tree structures, each containing exactly 7 vertices in specific topological patterns.

**Parameters:**
- `vertexThreshold`: Threshold for vertex assignment in heptavertex trees
- `useTopologicalSort`: Whether to use topological sorting for tree construction

**Usage:**
```java
UndecadicHeptavertexClustering clusterer = new UndecadicHeptavertexClustering(
    0.5,    // vertexThreshold
    true,   // useTopologicalSort
    false   // penalizeMissingFeatures
);
```

### 3. Hexadic Clustering of Vicenary Trees

**Class:** `HexadicVicenaryTreeClustering`

Uses hexadic (base-6) number systems to organize data into vicenary (base-20) tree structures. Creates hierarchical clusters where each node can have up to 6 children, following vicenary principles with 20 primary branches.

**Parameters:**
- `maxTreeDepth`: Maximum depth of the vicenary trees
- `branchingThreshold`: Threshold for creating new branches in hexadic structure
- `enableVicenaryOptimization`: Whether to enable vicenary-specific optimizations

**Usage:**
```java
HexadicVicenaryTreeClustering clusterer = new HexadicVicenaryTreeClustering(
    4,      // maxTreeDepth
    0.4,    // branchingThreshold
    true,   // enableVicenaryOptimization
    false   // penalizeMissingFeatures
);
```

### 4. Complete B-Series Foundation (Orders 1-4)

**Class:** `BSeriesFoundationClustering`

Based on B-Series mathematical foundations, implementing order-specific clustering strategies from order 1 through order 4. Each order represents a different level of mathematical complexity and clustering granularity.

**Parameters:**
- `targetOrder`: Target B-Series order (1-4)
- `seriesThreshold`: Threshold for B-Series convergence
- `useCompleteExpansion`: Whether to use complete B-Series expansion

**Usage:**
```java
BSeriesFoundationClustering clusterer = new BSeriesFoundationClustering(
    3,      // targetOrder
    0.2,    // seriesThreshold
    true,   // useCompleteExpansion
    false   // penalizeMissingFeatures
);
```

### 5. B-Series Order-5 Triadic Representation

**Class:** `BSeriesOrder5TriadicClustering`

Advanced clustering extending B-Series foundation to order 5 with triadic (three-way) relationships. Organizes data into triadic structures where each cluster represents a triangle of related instances with order-5 B-Series properties.

**Parameters:**
- `triadicThreshold`: Threshold for triadic relationship formation
- `order5Weight`: Weight factor for order-5 B-Series calculations
- `enforceTriadicStructure`: Whether to strictly enforce triadic (3-member) clusters

**Usage:**
```java
BSeriesOrder5TriadicClustering clusterer = new BSeriesOrder5TriadicClustering(
    0.3,    // triadicThreshold
    1.5,    // order5Weight
    true,   // enforceTriadicStructure
    false   // penalizeMissingFeatures
);
```

## Installation

The cognut module is included as part of the cogfluent project. To build:

```bash
./gradlew cognut:build
```

## Quick Start

Use the `CognutClusteringFactory` for easy instantiation with default parameters:

```java
import com.oculusinfo.ml.unsupervised.cluster.cognut.CognutClusteringFactory;

// Create clusterers with sensible defaults
UniversalFractalPartitionSchema fractalClusterer = CognutClusteringFactory.createFractalClusterer();
UndecadicHeptavertexClustering undecadicClusterer = CognutClusteringFactory.createUndecadicClusterer();
BSeriesFoundationClustering bSeriesClusterer = CognutClusteringFactory.createBSeriesClusterer(3); // Order 3

// Initialize and use
fractalClusterer.init();
ClusterResult result = fractalClusterer.doCluster(yourDataSet);
```

## Demo

Run the demonstration to see all clustering strategies in action:

```java
com.oculusinfo.ml.unsupervised.cluster.cognut.CognutClusteringDemo
```

This will show how each clustering strategy performs on sample data.

## Dependencies

- `ensemble-clustering`: Base clustering framework
- `commons-math3`: Mathematical utilities
- `slf4j-api`: Logging framework

## License

Released under the MIT License. See LICENSE file for details.