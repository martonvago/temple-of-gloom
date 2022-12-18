package student.explore;

import game.NodeStatus;
import student.dijkstra.DijkstraAlgorithm;
import student.dijkstra.DijkstraPath;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;


public class ExploreGraph {
    private final Map<Long, ExploreNode> nodeMap = new HashMap<>();

    /**
     * Adds the current node and neighbor nodes to the graph map
     *
     * @param current    ID of the current node
     * @param distance   distance of the Current node to the
     * @param neighbours a list of Node status objects describing neighbours
     */
    public void logNodeVisit(long current, int distance, Collection<NodeStatus> neighbours) {

        if (!nodeMap.containsKey(current)) {
            // First time visiting a node
            var currentNode = new ExploreNode(current, distance, true);
            nodeMap.put(current, currentNode);
        } else {
            // visiting a node that is already present
            var currentNode = nodeMap.get(current);
            currentNode.visit();
        }

        logNeighbours(current, neighbours);
    }

    /**
     * Adds any neighbours that have not been visited to the graph map
     *
     * @param current    ID of the current Node
     * @param neighbours List of Neighbours that need to be added to the current node
     */
    private void logNeighbours(long current, Collection<NodeStatus> neighbours) {
        var parent = nodeMap.get(current);

        for (var neighbour : neighbours) {
            // Only create new nodes if they have not been seen before
            if (!nodeMap.containsKey(neighbour.nodeID())) {

                var neighbourNode = new ExploreNode(neighbour.nodeID(), neighbour.distanceToTarget(), false);
                // Add parent node
                neighbourNode.addNeighbour(parent);
                nodeMap.put(neighbour.nodeID(), neighbourNode);
            }
            // since the underlying data structure is a set we don't need to worry.
            parent.addNeighbour(nodeMap.get(neighbour.nodeID()));
        }
    }

    /**
     * Returns a sorted list of unexplored nodes connected to current
     *
     * @param current ID of the current Node
     * @return List of sorted Explore nodes
     */
    public List<ExploreNode> getUnexploredNeighbours(long current) {

        return nodeMap.get(current).getNeighbours().stream()
                .filter(Predicate.not(ExploreNode::getVisited))
                .sorted()
                .toList();
    }

    /**
     * @return a list of unvisited nodes in order of the distance to target
     */
    public List<ExploreNode> listUnVisitedNodesSorted() {
        return nodeMap.entrySet()
                .stream()
                .filter(Predicate.not(entry -> entry.getValue().getVisited()))
                .map(Map.Entry::getValue)
                .sorted().toList();
    }

    /**
     * Finds a NodeID that has the lowest distance that's unvisted
     *
     * @return Long
     */
    public ExploreNode getClosestUnexploredNodeToGoal() {
        return listUnVisitedNodesSorted().get(0);
    }

    /**
     * returns a path to the "best" node. Best is determined by a weighted combination of what is closest to the
     * target and what is closest to the player
     *
     * @param current Current location of the player
     * @return List of Nodes that represent a path to the best location
     */
    public DijkstraPath<ExploreNode> getPathToBestNode(long current) {

        // Potential idea to be more clever
        // First select best node within a radius of the explorer
        // If all nodes are exhausted than switch to current function.

        int distanceThreshold = 1;
        // Get sorted list of unseen nodes
        var unVisitedNodes = listUnVisitedNodesSorted();

        if (unVisitedNodes.isEmpty()) {
            System.err.println("maze is un-solvable");
            return new ExplorePath();
        }

        // Select all nodes that have are withing range of n + threshold, where n is the lowest distance
        var closestNodeDistance = unVisitedNodes.get(0).getDistanceToTarget() + distanceThreshold;
        var nodeIdsWithinRange = unVisitedNodes.stream()
                .filter(n -> n.getDistanceToTarget() <= closestNodeDistance)
                .mapToLong(ExploreNode::getId)
                .boxed().collect(Collectors.toSet());

        // Calculate the shortest path to each node within range
        var shortestPaths = new DijkstraAlgorithm<>(nodeMap.get(current), nodeIdsWithinRange).findShortestPath();
        // Choose the node with the shortest path
        return nodeIdsWithinRange.stream()
                .map(shortestPaths::get)
                .min(Comparator.comparing(path -> path))
                .orElseGet(ExplorePath::new);
    }
}
