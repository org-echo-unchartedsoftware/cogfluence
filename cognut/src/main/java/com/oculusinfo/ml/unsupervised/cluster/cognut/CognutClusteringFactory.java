/**
 * Copyright (c) 2024 Oculus Info Inc. http://www.oculusinfo.com/
 *
 * <p>Released under the MIT License.
 */
package com.oculusinfo.ml.unsupervised.cluster.cognut;

/**
 * Factory class for creating Cognut clustering strategy instances with default parameters.
 * Provides convenient methods to create clusterers with sensible defaults.
 */
public class CognutClusteringFactory {
    
    /**
     * Create a Universal Fractal Partition Schema clusterer with default parameters.
     */
    public static UniversalFractalPartitionSchema createFractalClusterer() {
        return new UniversalFractalPartitionSchema(
            4,      // maxDepth
            2.0,    // fractalDimension
            0.5,    // partitionThreshold
            false   // penalizeMissingFeatures
        );
    }
    
    /**
     * Create a Universal Fractal Partition Schema clusterer with custom parameters.
     */
    public static UniversalFractalPartitionSchema createFractalClusterer(
            int maxDepth, double fractalDimension, double partitionThreshold) {
        return new UniversalFractalPartitionSchema(maxDepth, fractalDimension, partitionThreshold, false);
    }
    
    /**
     * Create an Undecadic Heptavertex clusterer with default parameters.
     */
    public static UndecadicHeptavertexClustering createUndecadicClusterer() {
        return new UndecadicHeptavertexClustering(
            0.5,    // vertexThreshold
            true,   // useTopologicalSort
            false   // penalizeMissingFeatures
        );
    }
    
    /**
     * Create an Undecadic Heptavertex clusterer with custom parameters.
     */
    public static UndecadicHeptavertexClustering createUndecadicClusterer(
            double vertexThreshold, boolean useTopologicalSort) {
        return new UndecadicHeptavertexClustering(vertexThreshold, useTopologicalSort, false);
    }
    
    /**
     * Create a Hexadic Vicenary Tree clusterer with default parameters.
     */
    public static HexadicVicenaryTreeClustering createHexadicVicenaryClusterer() {
        return new HexadicVicenaryTreeClustering(
            4,      // maxTreeDepth
            0.4,    // branchingThreshold
            true,   // enableVicenaryOptimization
            false   // penalizeMissingFeatures
        );
    }
    
    /**
     * Create a Hexadic Vicenary Tree clusterer with custom parameters.
     */
    public static HexadicVicenaryTreeClustering createHexadicVicenaryClusterer(
            int maxTreeDepth, double branchingThreshold, boolean enableOptimization) {
        return new HexadicVicenaryTreeClustering(maxTreeDepth, branchingThreshold, enableOptimization, false);
    }
    
    /**
     * Create a B-Series Foundation clusterer with default parameters (Order 2).
     */
    public static BSeriesFoundationClustering createBSeriesClusterer() {
        return new BSeriesFoundationClustering(
            2,      // targetOrder
            0.3,    // seriesThreshold
            true,   // useCompleteExpansion
            false   // penalizeMissingFeatures
        );
    }
    
    /**
     * Create a B-Series Foundation clusterer with custom order.
     */
    public static BSeriesFoundationClustering createBSeriesClusterer(int order) {
        return new BSeriesFoundationClustering(order, 0.3, true, false);
    }
    
    /**
     * Create a B-Series Foundation clusterer with custom parameters.
     */
    public static BSeriesFoundationClustering createBSeriesClusterer(
            int targetOrder, double seriesThreshold, boolean useCompleteExpansion) {
        return new BSeriesFoundationClustering(targetOrder, seriesThreshold, useCompleteExpansion, false);
    }
    
    /**
     * Create a B-Series Order-5 Triadic clusterer with default parameters.
     */
    public static BSeriesOrder5TriadicClustering createTriadicClusterer() {
        return new BSeriesOrder5TriadicClustering(
            0.4,    // triadicThreshold
            1.0,    // order5Weight
            false,  // enforceTriadicStructure
            false   // penalizeMissingFeatures
        );
    }
    
    /**
     * Create a B-Series Order-5 Triadic clusterer for strict triadic clustering.
     */
    public static BSeriesOrder5TriadicClustering createStrictTriadicClusterer() {
        return new BSeriesOrder5TriadicClustering(
            0.3,    // triadicThreshold (stricter)
            1.5,    // order5Weight
            true,   // enforceTriadicStructure
            false   // penalizeMissingFeatures
        );
    }
    
    /**
     * Create a B-Series Order-5 Triadic clusterer with custom parameters.
     */
    public static BSeriesOrder5TriadicClustering createTriadicClusterer(
            double triadicThreshold, double order5Weight, boolean enforceTriadicStructure) {
        return new BSeriesOrder5TriadicClustering(triadicThreshold, order5Weight, enforceTriadicStructure, false);
    }
    
    /**
     * Get a description of all available clustering strategies.
     */
    public static String getAvailableStrategies() {
        StringBuilder sb = new StringBuilder();
        sb.append("Available Cognut Clustering Strategies:\n");
        sb.append("1. Universal Fractal Partition Schema - Fractal geometry-based clustering\n");
        sb.append("2. Undecadic Heptavertex Clustering - Base-11 tree structures with 7 vertices\n");
        sb.append("3. Hexadic Vicenary Tree Clustering - Base-6/Base-20 hierarchical trees\n");
        sb.append("4. B-Series Foundation Clustering - Mathematical B-Series orders 1-4\n");
        sb.append("5. B-Series Order-5 Triadic Clustering - Advanced triadic relationships\n");
        return sb.toString();
    }
}