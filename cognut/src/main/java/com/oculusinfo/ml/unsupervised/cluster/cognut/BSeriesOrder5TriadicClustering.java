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
 * B-Series Order-5 Triadic Representation clustering implementation.
 * 
 * This advanced clustering algorithm extends the B-Series foundation to order 5
 * and implements triadic (three-way) relationships between data points.
 * The algorithm organizes data into triadic structures where each cluster
 * represents a triangle of related instances with order-5 B-Series properties.
 * 
 * @author cognut-team
 */
public class BSeriesOrder5TriadicClustering extends AbstractClusterer {
    private static final Logger log = LoggerFactory.getLogger(BSeriesOrder5TriadicClustering.class);
    
    private static final int SERIES_ORDER = 5;
    private static final int TRIADIC_SIZE = 3;
    private static final double ORDER_5_COEFFICIENT = 1.0 / 120.0; // 1/5!
    
    private double triadicThreshold;
    private double order5Weight;
    private boolean enforceTriadicStructure;
    
    /**
     * Constructor for B-Series Order-5 Triadic clustering.
     * 
     * @param triadicThreshold Threshold for triadic relationship formation
     * @param order5Weight Weight factor for order-5 B-Series calculations
     * @param enforceTriadicStructure Whether to strictly enforce triadic (3-member) clusters
     * @param penalizeMissingFeatures Whether to penalize missing features in distance calculations
     */
    public BSeriesOrder5TriadicClustering(double triadicThreshold, double order5Weight, 
                                        boolean enforceTriadicStructure, boolean penalizeMissingFeatures) {
        super(false, false, penalizeMissingFeatures);
        this.triadicThreshold = triadicThreshold;
        this.order5Weight = order5Weight;
        this.enforceTriadicStructure = enforceTriadicStructure;
    }
    
    @Override
    public boolean isCandidate(Instance instance, Cluster cluster) {
        if (enforceTriadicStructure && cluster.size() >= TRIADIC_SIZE) {
            return false; // Triadic clusters can only have 3 members
        }
        
        if (cluster.size() == 0) {
            return true;
        }
        
        // Calculate triadic compatibility using order-5 B-Series
        double triadicValue = calculateTriadicCompatibility(instance, cluster);
        return triadicValue <= triadicThreshold;
    }
    
    /**
     * Calculate triadic compatibility using order-5 B-Series expansion.
     */
    private double calculateTriadicCompatibility(Instance instance, Cluster cluster) {
        if (cluster.size() == 0) {
            return 0.0;
        }
        
        double triadicSum = 0.0;
        List<Instance> members = new ArrayList<>(cluster.getMembers());
        
        // Calculate triadic relationships
        for (Instance member : members) {
            double order5Value = calculateOrder5Term(instance, member, cluster);
            triadicSum += order5Value * order5Weight;
        }
        
        // Apply triadic normalization
        return triadicSum / Math.max(1, members.size());
    }
    
    /**
     * Calculate order-5 B-Series term for triadic relationships.
     */
    private double calculateOrder5Term(Instance instance1, Instance instance2, Cluster cluster) {
        if (cluster.size() < 5) {
            // Use simplified calculation for smaller clusters
            return calculateSimplifiedOrder5(instance1, instance2, cluster);
        }
        
        double sum = 0.0;
        List<Instance> members = new ArrayList<>(cluster.getMembers());
        int count = 0;
        
        // Calculate 5-way interactions for order-5 B-Series
        for (int i = 0; i < members.size(); i++) {
            for (int j = i + 1; j < members.size(); j++) {
                for (int k = j + 1; k < members.size(); k++) {
                    for (int l = k + 1; l < members.size(); l++) {
                        double dist1 = distance(instance1, members.get(i));
                        double dist2 = distance(instance1, members.get(j));
                        double dist3 = distance(instance1, members.get(k));
                        double dist4 = distance(instance1, members.get(l));
                        double dist5 = distance(instance2, members.get(i));
                        
                        sum += ORDER_5_COEFFICIENT * dist1 * dist2 * dist3 * dist4 * dist5;
                        count++;
                    }
                }
            }
        }
        
        return count > 0 ? sum / count : 0.0;
    }
    
    /**
     * Calculate simplified order-5 term for smaller clusters.
     */
    private double calculateSimplifiedOrder5(Instance instance1, Instance instance2, Cluster cluster) {
        double directDistance = distance(instance1, instance2);
        
        if (cluster.size() == 0) {
            return directDistance;
        }
        
        double clusterInfluence = 0.0;
        for (Instance member : cluster.getMembers()) {
            if (!member.equals(instance1) && !member.equals(instance2)) {
                double dist1 = distance(instance1, member);
                double dist2 = distance(instance2, member);
                clusterInfluence += Math.pow(dist1 * dist2, 2.5); // Approximation of order-5
            }
        }
        
        return directDistance + ORDER_5_COEFFICIENT * clusterInfluence;
    }
    
    /**
     * Calculate triadic stability for a cluster.
     */
    private double calculateTriadicStability(Cluster cluster) {
        if (cluster.size() != TRIADIC_SIZE) {
            return 0.0; // Not a valid triad
        }
        
        List<Instance> members = new ArrayList<>(cluster.getMembers());
        
        // Calculate triangle stability using distances between all three points
        double dist12 = distance(members.get(0), members.get(1));
        double dist13 = distance(members.get(0), members.get(2));
        double dist23 = distance(members.get(1), members.get(2));
        
        // Triadic stability is higher when the triangle is more equilateral
        double meanDistance = (dist12 + dist13 + dist23) / 3.0;
        double variance = Math.pow(dist12 - meanDistance, 2) + 
                         Math.pow(dist13 - meanDistance, 2) + 
                         Math.pow(dist23 - meanDistance, 2);
        
        return 1.0 / (1.0 + variance); // Higher stability for lower variance
    }
    
    @Override
    protected ClusterResult doCluster(DataSet ds, List<Cluster> clusters) {
        log.info("Starting B-Series Order-5 Triadic clustering");
        
        List<Cluster> resultClusters = new ArrayList<>(clusters);
        List<Instance> instances = ds.getInstances();
        
        // Create triadic clusters
        List<Cluster> triadicClusters = new ArrayList<>();
        
        for (Instance instance : instances) {
            boolean assigned = false;
            
            // Try to assign to existing triadic clusters
            for (Cluster cluster : triadicClusters) {
                if (isCandidate(instance, cluster)) {
                    cluster.add(instance);
                    assigned = true;
                    break;
                }
            }
            
            // Create new triadic cluster if needed
            if (!assigned) {
                Cluster newTriad = factory.createCluster();
                newTriad.add(instance);
                triadicClusters.add(newTriad);
            }
        }
        
        // Optimize triadic structures
        for (Cluster cluster : triadicClusters) {
            if (cluster.size() > 0) {
                optimizeTriadicStructure(cluster);
                cluster.updateCentroid();
                resultClusters.add(cluster);
            }
        }
        
        // Merge incomplete triads if not enforcing strict structure
        if (!enforceTriadicStructure) {
            resultClusters = mergeIncompleteTriads(resultClusters);
        }
        
        log.info("B-Series Order-5 Triadic clustering completed. Created {} triadic clusters", 
                resultClusters.size());
        return new InMemoryClusterResult(resultClusters);
    }
    
    /**
     * Optimize triadic structure for better stability.
     */
    private void optimizeTriadicStructure(Cluster cluster) {
        if (cluster.size() != TRIADIC_SIZE) {
            return; // Can only optimize complete triads
        }
        
        List<Instance> members = new ArrayList<>(cluster.getMembers());
        
        // Find optimal ordering for maximum triadic stability
        double bestStability = calculateTriadicStability(cluster);
        List<Instance> bestOrdering = new ArrayList<>(members);
        
        // Try all permutations (only 6 for 3 elements)
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                for (int k = 0; k < 3; k++) {
                    if (i != j && j != k && i != k) {
                        Cluster testCluster = factory.createCluster();
                        testCluster.add(members.get(i));
                        testCluster.add(members.get(j));
                        testCluster.add(members.get(k));
                        
                        double stability = calculateTriadicStability(testCluster);
                        if (stability > bestStability) {
                            bestStability = stability;
                            bestOrdering.clear();
                            bestOrdering.add(members.get(i));
                            bestOrdering.add(members.get(j));
                            bestOrdering.add(members.get(k));
                        }
                    }
                }
            }
        }
        
        // Apply best ordering
        cluster.reset();
        for (Instance member : bestOrdering) {
            cluster.add(member);
        }
    }
    
    /**
     * Merge incomplete triads to form more stable clusters.
     */
    private List<Cluster> mergeIncompleteTriads(List<Cluster> clusters) {
        List<Cluster> completeClusters = new ArrayList<>();
        List<Cluster> incompleteClusters = new ArrayList<>();
        
        // Separate complete and incomplete triads
        for (Cluster cluster : clusters) {
            if (cluster.size() == TRIADIC_SIZE) {
                completeClusters.add(cluster);
            } else {
                incompleteClusters.add(cluster);
            }
        }
        
        // Merge incomplete clusters
        while (incompleteClusters.size() > 1) {
            Cluster cluster1 = incompleteClusters.remove(0);
            Cluster cluster2 = incompleteClusters.remove(0);
            
            // Merge if combined size doesn't exceed triadic limit too much
            if (cluster1.size() + cluster2.size() <= TRIADIC_SIZE + 1) {
                for (Instance member : cluster2.getMembers()) {
                    cluster1.add(member);
                }
                
                if (cluster1.size() == TRIADIC_SIZE) {
                    completeClusters.add(cluster1);
                } else {
                    incompleteClusters.add(cluster1);
                }
            } else {
                incompleteClusters.add(cluster1);
                incompleteClusters.add(cluster2);
                break; // Avoid infinite loop
            }
        }
        
        // Add remaining incomplete clusters
        completeClusters.addAll(incompleteClusters);
        
        return completeClusters;
    }
    
    public double getTriadicThreshold() {
        return triadicThreshold;
    }
    
    public void setTriadicThreshold(double triadicThreshold) {
        this.triadicThreshold = triadicThreshold;
    }
    
    public double getOrder5Weight() {
        return order5Weight;
    }
    
    public void setOrder5Weight(double order5Weight) {
        this.order5Weight = order5Weight;
    }
    
    public boolean isEnforceTriadicStructure() {
        return enforceTriadicStructure;
    }
    
    public void setEnforceTriadicStructure(boolean enforceTriadicStructure) {
        this.enforceTriadicStructure = enforceTriadicStructure;
    }
}