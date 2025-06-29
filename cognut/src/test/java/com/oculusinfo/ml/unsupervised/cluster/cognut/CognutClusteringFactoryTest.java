/**
 * Copyright (c) 2024 Oculus Info Inc. http://www.oculusinfo.com/
 *
 * <p>Released under the MIT License.
 */
package com.oculusinfo.ml.unsupervised.cluster.cognut;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests for the Cognut clustering factory.
 */
public class CognutClusteringFactoryTest {
    
    @Test
    public void testCreateFractalClustererDefault() {
        UniversalFractalPartitionSchema clusterer = CognutClusteringFactory.createFractalClusterer();
        assertNotNull(clusterer);
        assertEquals(4, clusterer.getMaxDepth());
        assertEquals(2.0, clusterer.getFractalDimension(), 0.001);
        assertEquals(0.5, clusterer.getPartitionThreshold(), 0.001);
    }
    
    @Test
    public void testCreateFractalClustererCustom() {
        UniversalFractalPartitionSchema clusterer = CognutClusteringFactory.createFractalClusterer(6, 2.5, 0.3);
        assertNotNull(clusterer);
        assertEquals(6, clusterer.getMaxDepth());
        assertEquals(2.5, clusterer.getFractalDimension(), 0.001);
        assertEquals(0.3, clusterer.getPartitionThreshold(), 0.001);
    }
    
    @Test
    public void testCreateUndecadicClustererDefault() {
        UndecadicHeptavertexClustering clusterer = CognutClusteringFactory.createUndecadicClusterer();
        assertNotNull(clusterer);
        assertEquals(0.5, clusterer.getVertexThreshold(), 0.001);
        assertTrue(clusterer.isUseTopologicalSort());
    }
    
    @Test
    public void testCreateHexadicVicenaryClustererDefault() {
        HexadicVicenaryTreeClustering clusterer = CognutClusteringFactory.createHexadicVicenaryClusterer();
        assertNotNull(clusterer);
        assertEquals(4, clusterer.getMaxTreeDepth());
        assertEquals(0.4, clusterer.getBranchingThreshold(), 0.001);
        assertTrue(clusterer.isEnableVicenaryOptimization());
    }
    
    @Test
    public void testCreateBSeriesClustererDefault() {
        BSeriesFoundationClustering clusterer = CognutClusteringFactory.createBSeriesClusterer();
        assertNotNull(clusterer);
        assertEquals(2, clusterer.getTargetOrder());
        assertEquals(0.3, clusterer.getSeriesThreshold(), 0.001);
        assertTrue(clusterer.isUseCompleteExpansion());
    }
    
    @Test
    public void testCreateBSeriesClustererCustomOrder() {
        BSeriesFoundationClustering clusterer = CognutClusteringFactory.createBSeriesClusterer(4);
        assertNotNull(clusterer);
        assertEquals(4, clusterer.getTargetOrder());
    }
    
    @Test
    public void testCreateTriadicClustererDefault() {
        BSeriesOrder5TriadicClustering clusterer = CognutClusteringFactory.createTriadicClusterer();
        assertNotNull(clusterer);
        assertEquals(0.4, clusterer.getTriadicThreshold(), 0.001);
        assertEquals(1.0, clusterer.getOrder5Weight(), 0.001);
        assertFalse(clusterer.isEnforceTriadicStructure());
    }
    
    @Test
    public void testCreateStrictTriadicClusterer() {
        BSeriesOrder5TriadicClustering clusterer = CognutClusteringFactory.createStrictTriadicClusterer();
        assertNotNull(clusterer);
        assertEquals(0.3, clusterer.getTriadicThreshold(), 0.001);
        assertEquals(1.5, clusterer.getOrder5Weight(), 0.001);
        assertTrue(clusterer.isEnforceTriadicStructure());
    }
    
    @Test
    public void testGetAvailableStrategies() {
        String strategies = CognutClusteringFactory.getAvailableStrategies();
        assertNotNull(strategies);
        assertTrue(strategies.contains("Universal Fractal"));
        assertTrue(strategies.contains("Undecadic Heptavertex"));
        assertTrue(strategies.contains("Hexadic Vicenary"));
        assertTrue(strategies.contains("B-Series Foundation"));
        assertTrue(strategies.contains("Order-5 Triadic"));
    }
}