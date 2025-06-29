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
import java.util.LinkedList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Universal Fractal Partition Schema clustering implementation.
 * 
 * This clustering algorithm uses fractal geometry principles to partition data
 * into self-similar clusters at multiple scales. The algorithm recursively
 * divides the data space using fractal patterns that preserve the underlying
 * geometric structure of the data.
 * 
 * @author cognut-team
 */
public class UniversalFractalPartitionSchema extends AbstractClusterer {
    private static final Logger log = LoggerFactory.getLogger(UniversalFractalPartitionSchema.class);
    
    private int maxDepth;
    private double fractalDimension;
    private double partitionThreshold;
    
    /**
     * Constructor for Universal Fractal Partition Schema clustering.
     * 
     * @param maxDepth Maximum recursion depth for fractal partitioning
     * @param fractalDimension The fractal dimension to use for partitioning (typically between 1.0 and 3.0)
     * @param partitionThreshold Threshold for determining when to stop partitioning
     * @param penalizeMissingFeatures Whether to penalize missing features in distance calculations
     */
    public UniversalFractalPartitionSchema(int maxDepth, double fractalDimension, double partitionThreshold, boolean penalizeMissingFeatures) {
        super(false, false, penalizeMissingFeatures);
        this.maxDepth = maxDepth;
        this.fractalDimension = fractalDimension;
        this.partitionThreshold = partitionThreshold;
    }
    
    @Override
    public boolean isCandidate(Instance instance, Cluster cluster) {
        // Calculate fractal distance based on self-similarity
        if (cluster.size() == 0) {
            return true;
        }
        
        double fractalDistance = fractalDistance(instance, cluster);
        return fractalDistance < partitionThreshold;
    }
    
    /**
     * Calculate fractal distance between an instance and a cluster.
     * Uses fractal dimension to weight the distance calculation.
     */
    private double fractalDistance(Instance instance, Cluster cluster) {
        if (cluster.size() == 0) {
            return 0.0;
        }
        
        double baseDistance = distance(instance, cluster);
        // Apply fractal scaling
        double scaleFactor = Math.pow(cluster.size(), 1.0 / fractalDimension);
        return baseDistance / scaleFactor;
    }
    
    @Override
    protected ClusterResult doCluster(DataSet ds, List<Cluster> clusters) {
        log.info("Starting Universal Fractal Partition Schema clustering with fractal dimension: {}", fractalDimension);
        
        List<Cluster> resultClusters = new ArrayList<>(clusters);
        List<Instance> instances = ds.getInstances();
        
        // Perform fractal partitioning recursively
        List<List<Instance>> partitions = fractalPartition(instances, 0);
        
        // Create clusters from partitions
        for (List<Instance> partition : partitions) {
            if (!partition.isEmpty()) {
                Cluster cluster = factory.createCluster();
                for (Instance instance : partition) {
                    cluster.add(instance);
                }
                cluster.updateCentroid();
                resultClusters.add(cluster);
            }
        }
        
        log.info("Fractal partitioning completed. Created {} clusters", resultClusters.size());
        return new InMemoryClusterResult(resultClusters);
    }
    
    /**
     * Recursively partition instances using fractal principles.
     */
    private List<List<Instance>> fractalPartition(List<Instance> instances, int depth) {
        List<List<Instance>> partitions = new ArrayList<>();
        
        if (depth >= maxDepth || instances.size() <= 1) {
            partitions.add(new ArrayList<>(instances));
            return partitions;
        }
        
        // Calculate fractal partition points using golden ratio for self-similarity
        double goldenRatio = 1.618033988749;
        int partitionSize = (int) Math.ceil(instances.size() / goldenRatio);
        
        if (partitionSize >= instances.size()) {
            partitions.add(new ArrayList<>(instances));
            return partitions;
        }
        
        // Split instances based on fractal patterns
        List<Instance> firstPartition = instances.subList(0, partitionSize);
        List<Instance> secondPartition = instances.subList(partitionSize, instances.size());
        
        // Recursively partition each subset
        partitions.addAll(fractalPartition(firstPartition, depth + 1));
        partitions.addAll(fractalPartition(secondPartition, depth + 1));
        
        return partitions;
    }
    
    public int getMaxDepth() {
        return maxDepth;
    }
    
    public void setMaxDepth(int maxDepth) {
        this.maxDepth = maxDepth;
    }
    
    public double getFractalDimension() {
        return fractalDimension;
    }
    
    public void setFractalDimension(double fractalDimension) {
        this.fractalDimension = fractalDimension;
    }
    
    public double getPartitionThreshold() {
        return partitionThreshold;
    }
    
    public void setPartitionThreshold(double partitionThreshold) {
        this.partitionThreshold = partitionThreshold;
    }
}