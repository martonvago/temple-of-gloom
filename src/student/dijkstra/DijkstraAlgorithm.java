package student.dijkstra;

import java.util.*;

/**
 * A class implementing Dijkstra's shortest path algorithm.
 *
 * @param <NodeType> the type of the nodes constituting the graph the algorithm operates on
 */
public class DijkstraAlgorithm<NodeType extends DijkstraNode<NodeType>> {

    /**
     * The node from which shortest paths to other nodes are calculated
     */
    private final NodeType start;

    /**
     * The ids of the nodes to which we would like to find the shortest path from the starting node
     */
    private final Set<Long> targetNodeIds;

    /**
     * The nodes to which the shortest path has already been found
     */
    private final Set<NodeType> completeNodes;

    /**
     * The nodes the algorithm has seen but to which it has not found the shortest path yet
     */
    private final Set<NodeType> candidateNodes;

    /**
     * A map containing a path to some nodes from the start node. Only target nodes are guaranteed to be included
     * and to have the shortest path. To save time, we process the maze only as far as necessary, so other nodes may
     * be missing or their path may not be the shortest path.
     */
    private final Map<Long, DijkstraPath<NodeType>> pathMap;

    /**
     * Construct an algorithm with a start node and multiple target nodes
     * @param startNode the start node
     * @param targetIds the ids of the target nodes
     */
    public DijkstraAlgorithm(NodeType startNode, Set<Long> targetIds) {
        start = startNode;
        targetNodeIds = targetIds;
        completeNodes = new HashSet<>();
        candidateNodes = new HashSet<>();
        pathMap = new HashMap<>();
    }

    /**
     * Construct an algorithm with a start node and a single target node
     * @param startNode the start node
     * @param targetId the id of the target node
     */
    public DijkstraAlgorithm(NodeType startNode, Long targetId) {
        this(startNode, Set.of(targetId));
    }

    /**
     * Find the shortest paths from the starting node to the nodes specified in targetNodeIds.
     *
     * @return a map containing (at least) the shortest path to each target node
     */
    public Map<Long, DijkstraPath<NodeType>> findShortestPath() {
        pathMap.put(start.getId(), start.wrapToPath());

        // End as soon as we have all the shortest paths to the targets
        int targetCompleteCount = 0;
        while (targetCompleteCount < targetNodeIds.size()) {
            NodeType closestCandidate = getClosestCandidate();
            DijkstraPath<NodeType> pathToClosest = pathMap.get(closestCandidate.getId());

            // Build paths to all neighbours of the closest candidate, if not complete yet
            closestCandidate.getNeighbours().stream()
                    .filter(neighbour -> !completeNodes.contains(neighbour))
                    .forEach(neighbour -> addOrUpdatePath(neighbour, pathToClosest));

            // We have now found the shortest path to the closest candidate
            candidateNodes.remove(closestCandidate);
            completeNodes.add(closestCandidate);

            // Check if we have found the shortest path for a target node
            if (targetNodeIds.contains(closestCandidate.getId())) {
                targetCompleteCount++;
            }
        }

        return pathMap;
    }

    /**
     * Compare a node's current shortest path with an alternative path to the node.
     * If the alternative path is better make that the new best path for the node.
     * @param node the node
     * @param pathToNeighbour path to a neighbour of the node, not including the node itself
     */
    private void addOrUpdatePath(NodeType node, DijkstraPath<NodeType> pathToNeighbour) {
        DijkstraPath<NodeType> oldPathToNode = pathMap.get(node.getId());
        DijkstraPath<NodeType> newPathToNode = pathToNeighbour.cloneWithNode(node);

        // Save new path if it's better than the old one
        if (oldPathToNode == null || newPathToNode.compareTo(oldPathToNode) < 0) {
            pathMap.put(node.getId(), newPathToNode);
        }

        // Mark the node as having been seen
        candidateNodes.add(node);
    }

    /**
     * Find the node among the nodes the algorithm has seen which is the least expensive to get to from the start node.
     * If no such node exists return the start node.
     *
     * @return the node closest to the start node
     */
    private NodeType getClosestCandidate() {
        return candidateNodes.stream()
                .min(Comparator.comparing((candidate -> pathMap.get(candidate.getId()))))
                .orElse(start);
    }
}
