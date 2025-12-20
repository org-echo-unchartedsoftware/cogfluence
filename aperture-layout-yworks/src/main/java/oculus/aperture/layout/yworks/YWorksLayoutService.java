/**
 * Copyright (c) 2013 Oculus Info Inc. http://www.oculusinfo.com/
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
package oculus.aperture.layout.yworks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import oculus.aperture.layout.impl.BridgedLayoutGraph;
import oculus.aperture.spi.common.Extents;
import oculus.aperture.spi.common.Link;
import oculus.aperture.spi.common.Node;
import oculus.aperture.spi.layout.options.GraphLayoutOptions;
import oculus.aperture.spi.layout.options.HorizontalTreeLayoutOptions;
import oculus.aperture.spi.layout.options.LayoutOptions;
import oculus.aperture.spi.layout.options.VerticalTreeLayoutOptions;
import org.jgrapht.Graph;
import org.jgrapht.alg.drawing.CircularLayoutAlgorithm2D;
import org.jgrapht.alg.drawing.FRLayoutAlgorithm2D;
import org.jgrapht.alg.drawing.LayoutAlgorithm2D;
import org.jgrapht.alg.drawing.model.Box2D;
import org.jgrapht.alg.drawing.model.LayoutModel2D;
import org.jgrapht.alg.drawing.model.MapLayoutModel2D;
import org.jgrapht.alg.drawing.model.Point2D;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedGraph;

/**
 * Wrapper for applying layout algorithms using JGraphT (replacement for YWorks toolkit).
 *
 * <p>This implementation provides equivalent functionality to the original yFiles-based
 * implementation using the open-source JGraphT library.
 *
 * @author dcheng (original), updated to use JGraphT
 */
public class YWorksLayoutService extends BridgedLayoutGraph {

  private Graph<String, DefaultEdge> graph = new SimpleDirectedGraph<>(DefaultEdge.class);
  private Map<String, Node> nodeMap = new HashMap<>();
  private List<String> nodeOrder = new ArrayList<>();

  @Override
  protected void onAddLink(Link link) {
    String sourceId = link.getSourceId();
    String targetId = link.getTargetId();

    // Ensure both vertices exist
    if (!graph.containsVertex(sourceId)) {
      graph.addVertex(sourceId);
    }
    if (!graph.containsVertex(targetId)) {
      graph.addVertex(targetId);
    }

    // Add edge if it doesn't exist
    if (!graph.containsEdge(sourceId, targetId)) {
      graph.addEdge(sourceId, targetId);
    }
  }

  @Override
  protected void onAddNode(Node node) {
    String nodeId = node.getId();
    graph.addVertex(nodeId);
    nodeMap.put(nodeId, node);
    nodeOrder.add(nodeId);
  }

  @Override
  protected void doLayout(LayoutOptions options) {
    if (graph.vertexSet().isEmpty()) {
      return;
    }

    String layoutType = options.getLayoutType();
    GraphLayoutOptions gopts = (GraphLayoutOptions) options;
    Extents extents = options.getPageExtents();

    // Default dimensions
    double width = extents != null ? extents.getWidth() : 1000;
    double height = extents != null ? extents.getHeight() : 1000;

    Box2D drawableArea = Box2D.of(0, 0, width, height);
    LayoutModel2D<String> layoutModel = new MapLayoutModel2D<>(drawableArea);

    LayoutAlgorithm2D<String, DefaultEdge> layoutAlgorithm;

    // Choose layout algorithm based on type
    if (layoutType.equals(GraphLayoutOptions.CIRCLE)
        || layoutType.equals(GraphLayoutOptions.RADIAL)) {
      // Circular layout
      CircularLayoutAlgorithm2D<String, DefaultEdge> circular = new CircularLayoutAlgorithm2D<>();
      layoutAlgorithm = circular;

    } else if (layoutType.equals(HorizontalTreeLayoutOptions.HORIZONTAL_TREE)) {
      // Horizontal tree layout - use hierarchical positioning
      HorizontalTreeLayoutOptions topts = (HorizontalTreeLayoutOptions) options;
      applyHorizontalTreeLayout(layoutModel, topts, width, height);
      copyPositionsToNodes(layoutModel);
      return;

    } else if (layoutType.equals(VerticalTreeLayoutOptions.VERTICAL_TREE)) {
      // Vertical tree layout - use hierarchical positioning
      VerticalTreeLayoutOptions topts = (VerticalTreeLayoutOptions) options;
      applyVerticalTreeLayout(layoutModel, topts, width, height);
      copyPositionsToNodes(layoutModel);
      return;

    } else {
      // Default: Organic/Force-directed layout (Fruchterman-Reingold)
      FRLayoutAlgorithm2D<String, DefaultEdge> fr = new FRLayoutAlgorithm2D<>();
      layoutAlgorithm = fr;
    }

    // Apply the layout algorithm
    layoutAlgorithm.layout(graph, layoutModel);

    // Copy positions back to nodes
    copyPositionsToNodes(layoutModel);
  }

  private void copyPositionsToNodes(LayoutModel2D<String> layoutModel) {
    for (Node node : getNodes()) {
      String id = node.getId();
      Point2D point = layoutModel.get(id);
      if (point != null) {
        node.setX((int) point.getX());
        node.setY((int) point.getY());
      }
    }
  }

  private void applyHorizontalTreeLayout(
      LayoutModel2D<String> layoutModel,
      HorizontalTreeLayoutOptions opts,
      double width,
      double height) {

    double levelDistance = opts.getTreeLevelDistance();
    double nodeDistance = opts.getNodeDistance();
    boolean rightToLeft = opts.isRightToLeft();

    // Find root nodes (nodes with no incoming edges)
    List<String> roots = new ArrayList<>();
    for (String vertex : graph.vertexSet()) {
      if (graph.inDegreeOf(vertex) == 0) {
        roots.add(vertex);
      }
    }
    if (roots.isEmpty() && !graph.vertexSet().isEmpty()) {
      roots.add(graph.vertexSet().iterator().next());
    }

    // Calculate levels using BFS
    Map<String, Integer> levels = new HashMap<>();
    Map<Integer, List<String>> levelNodes = new HashMap<>();
    calculateLevels(roots, levels, levelNodes);

    // Position nodes
    int maxLevel = levelNodes.keySet().stream().mapToInt(Integer::intValue).max().orElse(0);

    for (Map.Entry<Integer, List<String>> entry : levelNodes.entrySet()) {
      int level = entry.getKey();
      List<String> nodesAtLevel = entry.getValue();

      double x = rightToLeft ? width - (level * levelDistance) : (level * levelDistance);

      for (int i = 0; i < nodesAtLevel.size(); i++) {
        String nodeId = nodesAtLevel.get(i);
        double y = (i + 1) * nodeDistance;
        layoutModel.put(nodeId, Point2D.of(x, y));
      }
    }
  }

  private void applyVerticalTreeLayout(
      LayoutModel2D<String> layoutModel,
      VerticalTreeLayoutOptions opts,
      double width,
      double height) {

    double levelDistance = opts.getTreeLevelDistance();
    double nodeDistance = opts.getNodeDistance();
    boolean bottomToTop = opts.isBottomToTop();

    // Find root nodes (nodes with no incoming edges)
    List<String> roots = new ArrayList<>();
    for (String vertex : graph.vertexSet()) {
      if (graph.inDegreeOf(vertex) == 0) {
        roots.add(vertex);
      }
    }
    if (roots.isEmpty() && !graph.vertexSet().isEmpty()) {
      roots.add(graph.vertexSet().iterator().next());
    }

    // Calculate levels using BFS
    Map<String, Integer> levels = new HashMap<>();
    Map<Integer, List<String>> levelNodes = new HashMap<>();
    calculateLevels(roots, levels, levelNodes);

    // Position nodes
    int maxLevel = levelNodes.keySet().stream().mapToInt(Integer::intValue).max().orElse(0);

    for (Map.Entry<Integer, List<String>> entry : levelNodes.entrySet()) {
      int level = entry.getKey();
      List<String> nodesAtLevel = entry.getValue();

      double y = bottomToTop ? height - (level * levelDistance) : (level * levelDistance);

      for (int i = 0; i < nodesAtLevel.size(); i++) {
        String nodeId = nodesAtLevel.get(i);
        double x = (i + 1) * nodeDistance;
        layoutModel.put(nodeId, Point2D.of(x, y));
      }
    }
  }

  private void calculateLevels(
      List<String> roots, Map<String, Integer> levels, Map<Integer, List<String>> levelNodes) {

    java.util.Queue<String> queue = new java.util.LinkedList<>();
    for (String root : roots) {
      queue.add(root);
      levels.put(root, 0);
    }

    while (!queue.isEmpty()) {
      String current = queue.poll();
      int currentLevel = levels.get(current);

      levelNodes.computeIfAbsent(currentLevel, k -> new ArrayList<>()).add(current);

      for (DefaultEdge edge : graph.outgoingEdgesOf(current)) {
        String target = graph.getEdgeTarget(edge);
        if (!levels.containsKey(target)) {
          levels.put(target, currentLevel + 1);
          queue.add(target);
        }
      }
    }

    // Handle disconnected nodes
    for (String vertex : graph.vertexSet()) {
      if (!levels.containsKey(vertex)) {
        levels.put(vertex, 0);
        levelNodes.computeIfAbsent(0, k -> new ArrayList<>()).add(vertex);
      }
    }
  }
}
