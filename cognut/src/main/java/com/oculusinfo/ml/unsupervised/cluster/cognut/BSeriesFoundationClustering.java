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
 * Complete B-Series Foundation clustering implementation for Orders 1-4.
 * 
 * This clustering algorithm is based on B-Series mathematical foundations,
 * implementing order-specific clustering strategies from order 1 through order 4.
 * Each order represents a different level of mathematical complexity and
 * clustering granularity in the B-Series framework.
 * 
 * @author cognut-team
 */
public class BSeriesFoundationClustering extends AbstractClusterer {
    private static final Logger log = LoggerFactory.getLogger(BSeriesFoundationClustering.class);
    
    private static final int MAX_ORDER = 4;
    private static final double[] ORDER_COEFFICIENTS = {1.0, 0.5, 0.16667, 0.04167}; // 1/n! for orders 1-4
    
    private int targetOrder;
    private double seriesThreshold;
    private boolean useCompleteExpansion;
    
    /**
     * Constructor for B-Series Foundation clustering.
     * 
     * @param targetOrder Target B-Series order (1-4)
     * @param seriesThreshold Threshold for B-Series convergence
     * @param useCompleteExpansion Whether to use complete B-Series expansion
     * @param penalizeMissingFeatures Whether to penalize missing features in distance calculations
     */
    public BSeriesFoundationClustering(int targetOrder, double seriesThreshold, 
                                     boolean useCompleteExpansion, boolean penalizeMissingFeatures) {
        super(false, false, penalizeMissingFeatures);
        this.targetOrder = Math.min(Math.max(targetOrder, 1), MAX_ORDER);
        this.seriesThreshold = seriesThreshold;
        this.useCompleteExpansion = useCompleteExpansion;
    }
    
    @Override
    public boolean isCandidate(Instance instance, Cluster cluster) {
        if (cluster.size() == 0) {
            return true;
        }
        
        // Calculate B-Series compatibility for the target order
        double bSeriesValue = calculateBSeriesValue(instance, cluster);
        return bSeriesValue <= seriesThreshold;
    }
    
    /**
     * Calculate B-Series value for instance-cluster compatibility.
     */
    private double calculateBSeriesValue(Instance instance, Cluster cluster) {
        double seriesSum = 0.0;
        
        // Calculate B-Series expansion up to target order
        for (int order = 1; order <= targetOrder; order++) {
            double orderTerm = calculateOrderTerm(instance, cluster, order);
            seriesSum += ORDER_COEFFICIENTS[order - 1] * orderTerm;
        }
        
        return Math.abs(seriesSum);
    }
    
    /**
     * Calculate B-Series term for a specific order.
     */
    private double calculateOrderTerm(Instance instance, Cluster cluster, int order) {
        switch (order) {
            case 1:
                return calculateOrder1Term(instance, cluster);
            case 2:
                return calculateOrder2Term(instance, cluster);
            case 3:
                return calculateOrder3Term(instance, cluster);
            case 4:
                return calculateOrder4Term(instance, cluster);
            default:
                return 0.0;
        }
    }
    
    /**
     * Calculate Order 1 B-Series term (linear approximation).
     */
    private double calculateOrder1Term(Instance instance, Cluster cluster) {
        if (cluster.size() == 0) {
            return 0.0;
        }
        
        double sum = 0.0;
        for (Instance member : cluster.getMembers()) {
            sum += distance(instance, member);
        }
        
        return sum / cluster.size();
    }
    
    /**
     * Calculate Order 2 B-Series term (quadratic approximation).
     */
    private double calculateOrder2Term(Instance instance, Cluster cluster) {
        if (cluster.size() < 2) {
            return 0.0;
        }
        
        double sum = 0.0;
        List<Instance> members = new ArrayList<>(cluster.getMembers());
        
        for (int i = 0; i < members.size(); i++) {
            for (int j = i + 1; j < members.size(); j++) {
                double dist1 = distance(instance, members.get(i));
                double dist2 = distance(instance, members.get(j));
                sum += dist1 * dist2;
            }
        }
        
        int pairs = members.size() * (members.size() - 1) / 2;
        return pairs > 0 ? sum / pairs : 0.0;
    }
    
    /**
     * Calculate Order 3 B-Series term (cubic approximation).
     */
    private double calculateOrder3Term(Instance instance, Cluster cluster) {
        if (cluster.size() < 3) {
            return 0.0;
        }
        
        double sum = 0.0;
        List<Instance> members = new ArrayList<>(cluster.getMembers());
        
        for (int i = 0; i < members.size(); i++) {
            for (int j = i + 1; j < members.size(); j++) {
                for (int k = j + 1; k < members.size(); k++) {
                    double dist1 = distance(instance, members.get(i));
                    double dist2 = distance(instance, members.get(j));
                    double dist3 = distance(instance, members.get(k));
                    sum += dist1 * dist2 * dist3;
                }
            }
        }
        
        int triplets = members.size() * (members.size() - 1) * (members.size() - 2) / 6;
        return triplets > 0 ? sum / triplets : 0.0;
    }
    
    /**
     * Calculate Order 4 B-Series term (quartic approximation).
     */
    private double calculateOrder4Term(Instance instance, Cluster cluster) {
        if (cluster.size() < 4) {
            return 0.0;
        }
        
        double sum = 0.0;
        List<Instance> members = new ArrayList<>(cluster.getMembers());
        
        for (int i = 0; i < members.size(); i++) {
            for (int j = i + 1; j < members.size(); j++) {
                for (int k = j + 1; k < members.size(); k++) {
                    for (int l = k + 1; l < members.size(); l++) {
                        double dist1 = distance(instance, members.get(i));
                        double dist2 = distance(instance, members.get(j));
                        double dist3 = distance(instance, members.get(k));
                        double dist4 = distance(instance, members.get(l));
                        sum += dist1 * dist2 * dist3 * dist4;
                    }
                }
            }
        }
        
        int quadruplets = members.size() * (members.size() - 1) * (members.size() - 2) * (members.size() - 3) / 24;
        return quadruplets > 0 ? sum / quadruplets : 0.0;
    }
    
    @Override
    protected ClusterResult doCluster(DataSet ds, List<Cluster> clusters) {
        log.info("Starting B-Series Foundation clustering for order: {}", targetOrder);
        
        List<Cluster> resultClusters = new ArrayList<>(clusters);
        List<Instance> instances = ds.getInstances();
        
        // Create order-specific clustering structure
        Map<Integer, Cluster> orderClusters = new HashMap<>();
        
        for (Instance instance : instances) {
            boolean assigned = false;
            
            // Try to assign to existing clusters based on B-Series compatibility
            for (Cluster cluster : orderClusters.values()) {
                if (isCandidate(instance, cluster)) {
                    cluster.add(instance);
                    assigned = true;
                    break;
                }
            }
            
            // Create new cluster if no compatible cluster found
            if (!assigned) {
                Cluster newCluster = factory.createCluster();
                newCluster.add(instance);
                orderClusters.put(orderClusters.size(), newCluster);
            }
        }
        
        // Apply B-Series optimization to clusters
        for (Cluster cluster : orderClusters.values()) {
            if (cluster.size() > 0) {
                if (useCompleteExpansion) {
                    optimizeBSeriesExpansion(cluster);
                }
                cluster.updateCentroid();
                resultClusters.add(cluster);
            }
        }
        
        log.info("B-Series Foundation clustering completed. Created {} clusters for order {}", 
                resultClusters.size(), targetOrder);
        return new InMemoryClusterResult(resultClusters);
    }
    
    /**
     * Optimize clusters using complete B-Series expansion.
     */
    private void optimizeBSeriesExpansion(Cluster cluster) {
        List<Instance> members = new ArrayList<>(cluster.getMembers());
        
        if (members.size() <= targetOrder) {
            return; // No optimization needed for small clusters
        }
        
        // Sort members by B-Series value for optimal arrangement
        members.sort((a, b) -> {
            double valueA = calculateBSeriesValueForInstance(a, cluster);
            double valueB = calculateBSeriesValueForInstance(b, cluster);
            return Double.compare(valueA, valueB);
        });
        
        // Rebuild cluster with optimized ordering
        cluster.reset();
        for (Instance member : members) {
            cluster.add(member);
        }
    }
    
    /**
     * Calculate B-Series value for a single instance within a cluster.
     */
    private double calculateBSeriesValueForInstance(Instance instance, Cluster cluster) {
        // Create temporary cluster without this instance
        Cluster tempCluster = factory.createCluster();
        for (Instance member : cluster.getMembers()) {
            if (!member.equals(instance)) {
                tempCluster.add(member);
            }
        }
        
        return calculateBSeriesValue(instance, tempCluster);
    }
    
    public int getTargetOrder() {
        return targetOrder;
    }
    
    public void setTargetOrder(int targetOrder) {
        this.targetOrder = Math.min(Math.max(targetOrder, 1), MAX_ORDER);
    }
    
    public double getSeriesThreshold() {
        return seriesThreshold;
    }
    
    public void setSeriesThreshold(double seriesThreshold) {
        this.seriesThreshold = seriesThreshold;
    }
    
    public boolean isUseCompleteExpansion() {
        return useCompleteExpansion;
    }
    
    public void setUseCompleteExpansion(boolean useCompleteExpansion) {
        this.useCompleteExpansion = useCompleteExpansion;
    }
}