/**
 * Copyright (c) 2024 Oculus Info Inc. http://www.oculusinfo.com/
 *
 * <p>Released under the MIT License.
 */
package com.oculusinfo.ml.unsupervised.cluster.cognut;

import com.oculusinfo.ml.DataSet;
import com.oculusinfo.ml.Instance;
import com.oculusinfo.ml.feature.Feature;
import com.oculusinfo.ml.feature.numeric.NumericVectorFeature;
import com.oculusinfo.ml.feature.string.StringFeature;
import com.oculusinfo.ml.unsupervised.cluster.Cluster;
import com.oculusinfo.ml.unsupervised.cluster.ClusterResult;
import java.util.ArrayList;
import java.util.List;

/**
 * Demonstration of the Cognut clustering strategies.
 * Shows how to use each of the five clustering algorithms with sample data.
 */
public class CognutClusteringDemo {
    
    public static void main(String[] args) {
        System.out.println("=== Cognut Clustering Strategies Demonstration ===\n");
        
        // Create sample dataset
        DataSet sampleData = createSampleDataSet();
        System.out.println("Created sample dataset with " + sampleData.size() + " instances\n");
        
        // Demonstrate each clustering strategy
        demonstrateUniversalFractalPartition(sampleData);
        demonstrateUndecadicHeptavertex(sampleData);
        demonstrateHexadicVicenary(sampleData);
        demonstrateBSeriesFoundation(sampleData);
        demonstrateBSeriesOrder5Triadic(sampleData);
        
        System.out.println("=== Demonstration Complete ===");
    }
    
    /**
     * Create a sample dataset for demonstration purposes.
     */
    private static DataSet createSampleDataSet() {
        DataSet ds = new DataSet();
        
        // Create sample instances with numeric and string features
        for (int i = 0; i < 20; i++) {
            Instance instance = new Instance(String.valueOf(i));
            
            // Add numeric features
            double[] vector = {Math.random() * 10, Math.random() * 10, Math.random() * 10};
            instance.addFeature(new NumericVectorFeature("coordinates", vector));
            
            // Add string features
            String category = "category_" + (i % 5);
            instance.addFeature(new StringFeature("category", category));
            
            ds.add(instance);
        }
        
        return ds;
    }
    
    /**
     * Demonstrate Universal Fractal Partition Schema clustering.
     */
    private static void demonstrateUniversalFractalPartition(DataSet data) {
        System.out.println("--- Universal Fractal Partition Schema ---");
        
        UniversalFractalPartitionSchema clusterer = new UniversalFractalPartitionSchema(
            3,      // maxDepth
            1.5,    // fractalDimension  
            0.4,    // partitionThreshold
            false   // penalizeMissingFeatures
        );
        
        clusterer.init();
        ClusterResult result = clusterer.doCluster(data);
        
        System.out.println("Fractal clustering created " + result.getClusters().size() + " clusters");
        printClusterSizes(result);
        System.out.println();
    }
    
    /**
     * Demonstrate Undecadic Heptavertex clustering.
     */
    private static void demonstrateUndecadicHeptavertex(DataSet data) {
        System.out.println("--- Undecadic Heptavertex Clustering ---");
        
        UndecadicHeptavertexClustering clusterer = new UndecadicHeptavertexClustering(
            0.6,    // vertexThreshold
            true,   // useTopologicalSort
            false   // penalizeMissingFeatures
        );
        
        clusterer.init();
        ClusterResult result = clusterer.doCluster(data);
        
        System.out.println("Undecadic clustering created " + result.getClusters().size() + " tree clusters");
        printClusterSizes(result);
        System.out.println();
    }
    
    /**
     * Demonstrate Hexadic Vicenary Tree clustering.
     */
    private static void demonstrateHexadicVicenary(DataSet data) {
        System.out.println("--- Hexadic Vicenary Tree Clustering ---");
        
        HexadicVicenaryTreeClustering clusterer = new HexadicVicenaryTreeClustering(
            3,      // maxTreeDepth
            0.5,    // branchingThreshold
            true,   // enableVicenaryOptimization
            false   // penalizeMissingFeatures
        );
        
        clusterer.init();
        ClusterResult result = clusterer.doCluster(data);
        
        System.out.println("Hexadic clustering created " + result.getClusters().size() + " tree clusters");
        printClusterSizes(result);
        System.out.println();
    }
    
    /**
     * Demonstrate B-Series Foundation clustering.
     */
    private static void demonstrateBSeriesFoundation(DataSet data) {
        System.out.println("--- B-Series Foundation Clustering (Order 3) ---");
        
        BSeriesFoundationClustering clusterer = new BSeriesFoundationClustering(
            3,      // targetOrder
            0.3,    // seriesThreshold
            true,   // useCompleteExpansion
            false   // penalizeMissingFeatures
        );
        
        clusterer.init();
        ClusterResult result = clusterer.doCluster(data);
        
        System.out.println("B-Series clustering created " + result.getClusters().size() + " clusters");
        printClusterSizes(result);
        System.out.println();
    }
    
    /**
     * Demonstrate B-Series Order-5 Triadic clustering.
     */
    private static void demonstrateBSeriesOrder5Triadic(DataSet data) {
        System.out.println("--- B-Series Order-5 Triadic Clustering ---");
        
        BSeriesOrder5TriadicClustering clusterer = new BSeriesOrder5TriadicClustering(
            0.4,    // triadicThreshold
            1.2,    // order5Weight
            false,  // enforceTriadicStructure (allow flexible sizes)
            false   // penalizeMissingFeatures
        );
        
        clusterer.init();
        ClusterResult result = clusterer.doCluster(data);
        
        System.out.println("Triadic clustering created " + result.getClusters().size() + " triadic clusters");
        printClusterSizes(result);
        System.out.println();
    }
    
    /**
     * Print the sizes of clusters in a result.
     */
    private static void printClusterSizes(ClusterResult result) {
        List<Cluster> clusters = result.getClusters();
        List<Integer> sizes = new ArrayList<>();
        
        for (Cluster cluster : clusters) {
            sizes.add(cluster.size());
        }
        
        System.out.println("Cluster sizes: " + sizes);
    }
}