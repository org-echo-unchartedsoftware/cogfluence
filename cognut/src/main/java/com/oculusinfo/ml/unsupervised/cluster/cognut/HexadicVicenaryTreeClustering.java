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
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Hexadic Clustering of Vicenary Trees implementation.
 * 
 * This clustering algorithm uses hexadic (base-6) number systems to organize
 * data into vicenary (base-20) tree structures. The algorithm creates hierarchical
 * clusters where each node can have up to 6 children, and the overall structure
 * follows vicenary principles with 20 primary branches.
 * 
 * @author cognut-team
 */
public class HexadicVicenaryTreeClustering extends AbstractClusterer {
    private static final Logger log = LoggerFactory.getLogger(HexadicVicenaryTreeClustering.class);
    
    private static final int HEXADIC_BASE = 6;
    private static final int VICENARY_BASE = 20;
    private static final int MAX_CHILDREN_PER_NODE = 6;
    
    private int maxTreeDepth;
    private double branchingThreshold;
    private boolean enableVicenaryOptimization;
    
    /**
     * Constructor for Hexadic Vicenary Tree clustering.
     * 
     * @param maxTreeDepth Maximum depth of the vicenary trees
     * @param branchingThreshold Threshold for creating new branches in hexadic structure
     * @param enableVicenaryOptimization Whether to enable vicenary-specific optimizations
     * @param penalizeMissingFeatures Whether to penalize missing features in distance calculations
     */
    public HexadicVicenaryTreeClustering(int maxTreeDepth, double branchingThreshold, 
                                        boolean enableVicenaryOptimization, boolean penalizeMissingFeatures) {
        super(false, false, penalizeMissingFeatures);
        this.maxTreeDepth = maxTreeDepth;
        this.branchingThreshold = branchingThreshold;
        this.enableVicenaryOptimization = enableVicenaryOptimization;
    }
    
    @Override
    public boolean isCandidate(Instance instance, Cluster cluster) {
        // Check if cluster can accept more instances based on hexadic branching rules
        int hexadicPosition = calculateHexadicPosition(instance);
        int currentSize = cluster.size();
        
        // Apply hexadic constraints: each level can have at most 6 branches
        int maxCapacity = calculateMaxCapacity(getClusterDepth(cluster));
        
        if (currentSize >= maxCapacity) {
            return false;
        }
        
        // Check vicenary tree compatibility
        return isVicenaryCompatible(instance, cluster);
    }
    
    /**
     * Calculate hexadic position for an instance.
     */
    private int calculateHexadicPosition(Instance instance) {
        int position = 0;
        int multiplier = 1;
        
        List<String> features = new ArrayList<>(instance.getFeatures());
        for (int i = 0; i < Math.min(features.size(), 3); i++) {
            String featureName = features.get(i);
            Object value = instance.getFeature(featureName);
            if (value != null) {
                int featureValue = Math.abs(value.hashCode()) % HEXADIC_BASE;
                position += featureValue * multiplier;
                multiplier *= HEXADIC_BASE;
            }
        }
        
        return position;
    }
    
    /**
     * Calculate maximum capacity for a cluster at given depth using hexadic rules.
     */
    private int calculateMaxCapacity(int depth) {
        if (depth == 0) {
            return VICENARY_BASE; // Root level can have 20 primary branches
        }
        
        // Each subsequent level follows hexadic branching (max 6 children per node)
        return (int) Math.pow(HEXADIC_BASE, Math.min(depth, 3));
    }
    
    /**
     * Get the depth of a cluster in the tree structure.
     */
    private int getClusterDepth(Cluster cluster) {
        // Simplified depth calculation based on cluster size and structure
        if (cluster.size() == 0) return 0;
        if (cluster.size() <= VICENARY_BASE) return 1;
        
        return (int) Math.ceil(Math.log(cluster.size()) / Math.log(HEXADIC_BASE)) + 1;
    }
    
    /**
     * Check if an instance is compatible with vicenary tree structure.
     */
    private boolean isVicenaryCompatible(Instance instance, Cluster cluster) {
        if (cluster.size() == 0) {
            return true;
        }
        
        // Calculate vicenary hash for structural compatibility
        int vicenaryHash = calculateVicenaryHash(instance);
        
        // Check compatibility with existing cluster members
        for (Instance member : cluster.getMembers()) {
            int memberHash = calculateVicenaryHash(member);
            double distance = distance(instance, member);
            
            // Apply vicenary compatibility rules
            int hashDiff = Math.abs(vicenaryHash - memberHash);
            double compatibilityScore = 1.0 - (hashDiff % VICENARY_BASE) / (double) VICENARY_BASE;
            
            if (distance > branchingThreshold * compatibilityScore) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Calculate vicenary hash for structural positioning.
     */
    private int calculateVicenaryHash(Instance instance) {
        int hash = 0;
        List<String> features = new ArrayList<>(instance.getFeatures());
        
        for (int i = 0; i < features.size(); i++) {
            String featureName = features.get(i);
            Object value = instance.getFeature(featureName);
            if (value != null) {
                hash += value.hashCode() * (i % VICENARY_BASE + 1);
            }
        }
        
        return Math.abs(hash);
    }
    
    @Override
    protected ClusterResult doCluster(DataSet ds, List<Cluster> clusters) {
        log.info("Starting Hexadic Vicenary Tree clustering with max depth: {}", maxTreeDepth);
        
        List<Cluster> resultClusters = new ArrayList<>(clusters);
        List<Instance> instances = ds.getInstances();
        
        // Create vicenary tree structure with 20 primary branches
        Map<Integer, Cluster> vicenaryBranches = new HashMap<>();
        
        for (Instance instance : instances) {
            int vicenaryHash = calculateVicenaryHash(instance);
            int primaryBranch = vicenaryHash % VICENARY_BASE;
            
            // Get or create primary branch
            Cluster branch = vicenaryBranches.computeIfAbsent(primaryBranch, 
                k -> factory.createCluster());
            
            if (isCandidate(instance, branch)) {
                branch.add(instance);
            } else {
                // Create hexadic sub-branches if primary branch is full
                Cluster subBranch = createHexadicSubBranch(branch, instance);
                if (subBranch != null) {
                    vicenaryBranches.put(vicenaryBranches.size(), subBranch);
                }
            }
        }
        
        // Process branches with hexadic optimization
        for (Cluster branch : vicenaryBranches.values()) {
            if (branch.size() > 0) {
                if (enableVicenaryOptimization) {
                    optimizeVicenaryStructure(branch);
                }
                branch.updateCentroid();
                resultClusters.add(branch);
            }
        }
        
        log.info("Hexadic vicenary clustering completed. Created {} tree clusters", resultClusters.size());
        return new InMemoryClusterResult(resultClusters);
    }
    
    /**
     * Create hexadic sub-branch when primary branch reaches capacity.
     */
    private Cluster createHexadicSubBranch(Cluster parentBranch, Instance instance) {
        int currentDepth = getClusterDepth(parentBranch);
        
        if (currentDepth >= maxTreeDepth) {
            return null; // Maximum depth reached
        }
        
        Cluster subBranch = factory.createCluster();
        
        // Apply hexadic branching logic
        int hexadicPosition = calculateHexadicPosition(instance);
        int branchIndex = hexadicPosition % MAX_CHILDREN_PER_NODE;
        
        // Create structured sub-branch based on hexadic principles
        subBranch.add(instance);
        
        return subBranch;
    }
    
    /**
     * Optimize the vicenary structure for better clustering performance.
     */
    private void optimizeVicenaryStructure(Cluster cluster) {
        List<Instance> members = new ArrayList<>(cluster.getMembers());
        
        if (members.size() <= VICENARY_BASE) {
            return; // No optimization needed for small clusters
        }
        
        // Sort members by vicenary hash for optimal tree structure
        members.sort((a, b) -> {
            int hashA = calculateVicenaryHash(a);
            int hashB = calculateVicenaryHash(b);
            return Integer.compare(hashA % VICENARY_BASE, hashB % VICENARY_BASE);
        });
        
        // Rebuild cluster with optimized ordering
        cluster.reset();
        for (Instance member : members) {
            cluster.add(member);
        }
    }
    
    public int getMaxTreeDepth() {
        return maxTreeDepth;
    }
    
    public void setMaxTreeDepth(int maxTreeDepth) {
        this.maxTreeDepth = maxTreeDepth;
    }
    
    public double getBranchingThreshold() {
        return branchingThreshold;
    }
    
    public void setBranchingThreshold(double branchingThreshold) {
        this.branchingThreshold = branchingThreshold;
    }
    
    public boolean isEnableVicenaryOptimization() {
        return enableVicenaryOptimization;
    }
    
    public void setEnableVicenaryOptimization(boolean enableVicenaryOptimization) {
        this.enableVicenaryOptimization = enableVicenaryOptimization;
    }
}