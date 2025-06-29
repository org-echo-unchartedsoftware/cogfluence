/**
 * Copyright (c) 2024 Oculus Info Inc. http://www.oculusinfo.com/
 *
 * <p>Released under the MIT License.
 */
package com.oculusinfo.ml.unsupervised.cluster.cognut;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Basic tests for Cognut clustering strategies.
 */
public class CognutClusteringTest {
    
    @Test
    public void testUniversalFractalPartitionSchemaConstruction() {
        UniversalFractalPartitionSchema clusterer = new UniversalFractalPartitionSchema(5, 2.0, 0.3, false);
        assertNotNull(clusterer);
        assertEquals(5, clusterer.getMaxDepth());
        assertEquals(2.0, clusterer.getFractalDimension(), 0.001);
        assertEquals(0.3, clusterer.getPartitionThreshold(), 0.001);
    }
    
    @Test
    public void testUndecadicHeptavertexClusteringConstruction() {
        UndecadicHeptavertexClustering clusterer = new UndecadicHeptavertexClustering(0.5, true, false);
        assertNotNull(clusterer);
        assertEquals(0.5, clusterer.getVertexThreshold(), 0.001);
        assertTrue(clusterer.isUseTopologicalSort());
    }
    
    @Test
    public void testHexadicVicenaryTreeClusteringConstruction() {
        HexadicVicenaryTreeClustering clusterer = new HexadicVicenaryTreeClustering(4, 0.4, true, false);
        assertNotNull(clusterer);
        assertEquals(4, clusterer.getMaxTreeDepth());
        assertEquals(0.4, clusterer.getBranchingThreshold(), 0.001);
        assertTrue(clusterer.isEnableVicenaryOptimization());
    }
    
    @Test
    public void testBSeriesFoundationClusteringConstruction() {
        BSeriesFoundationClustering clusterer = new BSeriesFoundationClustering(3, 0.2, true, false);
        assertNotNull(clusterer);
        assertEquals(3, clusterer.getTargetOrder());
        assertEquals(0.2, clusterer.getSeriesThreshold(), 0.001);
        assertTrue(clusterer.isUseCompleteExpansion());
    }
    
    @Test
    public void testBSeriesOrder5TriadicClusteringConstruction() {
        BSeriesOrder5TriadicClustering clusterer = new BSeriesOrder5TriadicClustering(0.3, 1.5, true, false);
        assertNotNull(clusterer);
        assertEquals(0.3, clusterer.getTriadicThreshold(), 0.001);
        assertEquals(1.5, clusterer.getOrder5Weight(), 0.001);
        assertTrue(clusterer.isEnforceTriadicStructure());
    }
    
    @Test
    public void testBSeriesFoundationOrderBounds() {
        // Test order bounds enforcement
        BSeriesFoundationClustering clusterer1 = new BSeriesFoundationClustering(0, 0.2, true, false);
        assertEquals(1, clusterer1.getTargetOrder()); // Should be clamped to 1
        
        BSeriesFoundationClustering clusterer2 = new BSeriesFoundationClustering(10, 0.2, true, false);
        assertEquals(4, clusterer2.getTargetOrder()); // Should be clamped to 4
    }
}