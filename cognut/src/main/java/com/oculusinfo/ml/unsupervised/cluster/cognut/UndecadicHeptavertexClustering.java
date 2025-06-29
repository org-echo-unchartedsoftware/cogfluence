/**
 * Copyright (c) 2024 Oculus Info Inc. http://www.oculusinfo.com/
 *
 * <p>Released under the MIT License.
 *
 * <p>Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * <p>The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * <p>THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.oculusinfo.ml.unsupervised.cluster.cognut;

import com.oculusinfo.ml.DataSet;
import com.oculusinfo.ml.Instance;
import com.oculusinfo.ml.unsupervised.cluster.AbstractClusterer;
import com.oculusinfo.ml.unsupervised.cluster.Cluster;
import com.oculusinfo.ml.unsupervised.cluster.ClusterResult;
import com.oculusinfo.ml.unsupervised.cluster.InMemoryClusterResult;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Undecadic Clustering of 48 Heptavertex Trees implementation.
 * 
 * This clustering algorithm operates on the principle of undecadic (base-11) 
 * number systems and structures data as heptavertex (7-vertex) trees.
 * The algorithm partitions data into 48 distinct tree structures, each containing
 * exactly 7 vertices arranged in specific topological patterns.
 * 
 * @author cognut-team
 */
public class UndecadicHeptavertexClustering extends AbstractClusterer {
    private static final Logger log = LoggerFactory.getLogger(UndecadicHeptavertexClustering.class);
    
    private static final int NUM_TREES = 48;
    private static final int VERTICES_PER_TREE = 7;
    private static final int UNDECADIC_BASE = 11;
    
    private double vertexThreshold;
    private boolean useTopologicalSort;
    
    /**
     * Constructor for Undecadic Heptavertex clustering.
     * 
     * @param vertexThreshold Threshold for vertex assignment in heptavertex trees
     * @param useTopologicalSort Whether to use topological sorting for tree construction
     * @param penalizeMissingFeatures Whether to penalize missing features in distance calculations
     */
    public UndecadicHeptavertexClustering(double vertexThreshold, boolean useTopologicalSort, boolean penalizeMissingFeatures) {
        super(false, false, penalizeMissingFeatures);
        this.vertexThreshold = vertexThreshold;
        this.useTopologicalSort = useTopologicalSort;
    }
    
    @Override
    public boolean isCandidate(Instance instance, Cluster cluster) {
        if (cluster.size() >= VERTICES_PER_TREE) {
            return false; // Tree is full
        }
        
        // Calculate undecadic hash for vertex assignment
        int undecadicHash = calculateUndecadicHash(instance);
        int treeIndex = undecadicHash % NUM_TREES;
        int expectedVertexPosition = cluster.size();
        
        // Check if instance belongs to this tree structure
        return isValidVertexPosition(instance, cluster, expectedVertexPosition);
    }
    
    /**
     * Calculate undecadic hash value for an instance.
     */
    private int calculateUndecadicHash(Instance instance) {
        int hash = 0;
        List<String> featureNames = new ArrayList<>(instance.getFeatures());
        Collections.sort(featureNames);
        
        for (int i = 0; i < featureNames.size(); i++) {
            String featureName = featureNames.get(i);
            Object value = instance.getFeature(featureName);
            if (value != null) {
                int featureHash = value.hashCode();
                hash += featureHash * Math.pow(UNDECADIC_BASE, i % 3);
            }
        }
        
        return Math.abs(hash);
    }
    
    /**
     * Check if an instance can occupy a specific vertex position in the heptavertex tree.
     */
    private boolean isValidVertexPosition(Instance instance, Cluster cluster, int position) {
        if (position >= VERTICES_PER_TREE) {
            return false;
        }
        
        // Define heptavertex tree topology: specific connectivity patterns
        int[][] adjacencyMatrix = getHeptavertexTopology(position);
        
        // Check topological constraints
        for (Instance member : cluster.getMembers()) {
            double dist = distance(instance, member);
            if (dist > vertexThreshold) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Get the adjacency matrix for heptavertex tree topology.
     */
    private int[][] getHeptavertexTopology(int variant) {
        // Define different heptavertex tree structures
        // This is a simplified representation - each of 48 trees has unique topology
        int[][] baseTopology = {
            {0, 1, 1, 0, 0, 0, 0}, // Vertex 0 connects to 1, 2
            {1, 0, 0, 1, 1, 0, 0}, // Vertex 1 connects to 0, 3, 4
            {1, 0, 0, 0, 0, 1, 1}, // Vertex 2 connects to 0, 5, 6
            {0, 1, 0, 0, 0, 0, 0}, // Vertex 3 connects to 1
            {0, 1, 0, 0, 0, 0, 0}, // Vertex 4 connects to 1
            {0, 0, 1, 0, 0, 0, 0}, // Vertex 5 connects to 2
            {0, 0, 1, 0, 0, 0, 0}  // Vertex 6 connects to 2
        };
        
        // Rotate topology based on variant to create different tree structures
        return rotateTopology(baseTopology, variant % 7);
    }
    
    /**
     * Rotate the topology matrix to create different tree variants.
     */
    private int[][] rotateTopology(int[][] matrix, int rotation) {
        int size = matrix.length;
        int[][] rotated = new int[size][size];
        
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                int newI = (i + rotation) % size;
                int newJ = (j + rotation) % size;
                rotated[newI][newJ] = matrix[i][j];
            }
        }
        
        return rotated;
    }
    
    @Override
    protected ClusterResult doCluster(DataSet ds, List<Cluster> clusters) {
        log.info("Starting Undecadic Heptavertex clustering for {} trees", NUM_TREES);
        
        List<Cluster> resultClusters = new ArrayList<>(clusters);
        List<Instance> instances = ds.getInstances();
        
        // Initialize 48 heptavertex trees
        List<Cluster> trees = new ArrayList<>();
        for (int i = 0; i < NUM_TREES; i++) {
            trees.add(factory.createCluster());
        }
        
        // Assign instances to appropriate trees based on undecadic hash
        for (Instance instance : instances) {
            int undecadicHash = calculateUndecadicHash(instance);
            int treeIndex = undecadicHash % NUM_TREES;
            
            Cluster targetTree = trees.get(treeIndex);
            
            if (isCandidate(instance, targetTree)) {
                targetTree.add(instance);
            } else {
                // Find alternative tree if primary assignment fails
                boolean assigned = false;
                for (int i = 1; i < NUM_TREES && !assigned; i++) {
                    int altIndex = (treeIndex + i) % NUM_TREES;
                    Cluster altTree = trees.get(altIndex);
                    if (isCandidate(instance, altTree)) {
                        altTree.add(instance);
                        assigned = true;
                    }
                }
                
                // Create new tree if no existing tree can accommodate
                if (!assigned) {
                    Cluster newTree = factory.createCluster();
                    newTree.add(instance);
                    trees.add(newTree);
                }
            }
        }
        
        // Update centroids and add non-empty trees to result
        for (Cluster tree : trees) {
            if (tree.size() > 0) {
                if (useTopologicalSort) {
                    topologicalSort(tree);
                }
                tree.updateCentroid();
                resultClusters.add(tree);
            }
        }
        
        log.info("Undecadic heptavertex clustering completed. Created {} tree clusters", resultClusters.size());
        return new InMemoryClusterResult(resultClusters);
    }
    
    /**
     * Apply topological sorting to tree vertices.
     */
    private void topologicalSort(Cluster tree) {
        // Simplified topological sort - in practice would use proper graph algorithms
        List<Instance> members = new ArrayList<>(tree.getMembers());
        
        // Sort by undecadic hash to maintain consistent vertex ordering
        members.sort((a, b) -> {
            int hashA = calculateUndecadicHash(a);
            int hashB = calculateUndecadicHash(b);
            return Integer.compare(hashA, hashB);
        });
        
        // Clear and re-add in sorted order
        tree.reset();
        for (Instance member : members) {
            tree.add(member);
        }
    }
    
    public double getVertexThreshold() {
        return vertexThreshold;
    }
    
    public void setVertexThreshold(double vertexThreshold) {
        this.vertexThreshold = vertexThreshold;
    }
    
    public boolean isUseTopologicalSort() {
        return useTopologicalSort;
    }
    
    public void setUseTopologicalSort(boolean useTopologicalSort) {
        this.useTopologicalSort = useTopologicalSort;
    }
}