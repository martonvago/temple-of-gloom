package student;

import game.NodeStatus;

import java.util.*;
import java.util.function.Predicate;


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

        System.out.println("visiting node " + current + " distance " + distance);

        if (!nodeMap.containsKey(current)) {
            // First time visiting a node
            var current_node = new ExploreNode(current, distance, true);
            nodeMap.put(current, current_node);
        } else {
            // visiting a node that is already present
            var current_node = nodeMap.get(current);
            current_node.Visit();
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

                var neighbour_node = new ExploreNode(neighbour.nodeID(), neighbour.distanceToTarget(), false);
                // Add parent node
                neighbour_node.AddNeighbour(parent);
                nodeMap.put(neighbour.nodeID(), neighbour_node);
            }
            // since the underlying data structure is a set we don't need to worry.
            parent.AddNeighbour(nodeMap.get(neighbour.nodeID()));
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
                .filter(Predicate.not(ExploreNode::Visited))
                .sorted()
                .toList();
    }

    /**
     * @return a list of unvisited nodes in order of the distance to target
     */
    private List<ExploreNode> listUnVisitedNodesSorted() {
        return nodeMap.entrySet()
                .stream()
                .filter(Predicate.not(entry -> entry.getValue().Visited()))
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
     * Breadth First search algorithm implementation for finding the shortest path between two nodes
     *
     * @param start  ID of the start node
     * @param target ID of the target node
     * @return a list of Explorer nodes that represent a path
     */
    private List<ExploreNode> shortestPathTo(long start, long target) {

        Queue<ExploreNode> queue = new LinkedList<>();
        Map<ExploreNode, Boolean> AlreadyVisited = new HashMap<>();
        Map<ExploreNode, ExploreNode> PreviousNodes = new HashMap<>();

        var startNode = nodeMap.get(start);
        var endNode = nodeMap.get(target);

        queue.add(startNode);
        AlreadyVisited.put(startNode, true);
        PreviousNodes.put(startNode, null);   // null is a sentinel value

        while (!queue.isEmpty()) {
            //pop a node from queue for search operation
            var current_node = queue.poll();
            //Loop through neighbors node to find the 'end'
            for (var node : current_node.getNeighbours()) {
                if (!AlreadyVisited.containsKey(node)) {
                    //Visit and add the node to the queue
                    AlreadyVisited.put(node, true);
                    queue.add(node);
                    // update its preceding nodes
                    PreviousNodes.put(node, current_node);
                    // If reached the end node then stop BFS
                    if (node == endNode) {
                        queue.clear();
                        break;
                    }
                }
            }
        }
        return convertToPath(PreviousNodes, endNode);
    }

    /**
     * Utility function takes the result of shortestPathTo and converts it to a list
     *
     * @param previousNodeMap a map representing a linked list like structure of nodes
     * @param endNode         a sentinel node indicating that the path is complete
     * @return a list of Explorer nodes that represent a path
     */
    private List<ExploreNode> convertToPath(Map<ExploreNode, ExploreNode> previousNodeMap, ExploreNode endNode) {

        List<ExploreNode> route = new ArrayList<>();
        var node = endNode;
        while (node != null) {
            var previous = previousNodeMap.get(node);

            if (previous == null) {
                break;
            }

            route.add(previous);
            node = previous;
        }
        Collections.reverse(route);
        // Remove first element as that the current node we are on
        route.remove(0);
        return route;
    }

    /**
     * returns a path to the "best" node. Best is determined by a weighted combination of what is closest to the
     * target and what is closest to the player
     *
     * @param current Current location of the player
     * @return List of Nodes that represent a path to the best location
     */
    public List<ExploreNode> getPathToBestNode(long current) {

        // Potential idea to be more clever
        // First select best node within a radius of the explorer
        // If all nodes are exhausted than switch to current function.

        int distanceThreshold = 1;
        // Get sorted list of unseen nodes
        var unVisitedNodes = listUnVisitedNodesSorted();

        if (unVisitedNodes.isEmpty()) {
            System.err.println("maze is un-solvable");
            return new ArrayList<>();
        }

        // Select all nodes that have are withing range of n + threshold, where n is the lowest distance
        var closestNodeDistance = unVisitedNodes.get(0).getDistanceToTarget() + distanceThreshold;
        var thresholdList = unVisitedNodes.stream()
                .filter(n -> n.getDistanceToTarget() < closestNodeDistance)
                .toList();

        // return early if there is only one entry
        if (thresholdList.size() == 1) {
            return shortestPathTo(current, thresholdList.get(0).getNodeID());
        }

        // select the one is the shortest distance to where the explorer is
        var bestCandidate = thresholdList.stream()
                .min((n1, n2) -> Integer.compare(
                        shortestPathTo(current, n1.getNodeID()).size(),
                        shortestPathTo(current, n2.getNodeID()).size()))
                .get();

        return shortestPathTo(current, bestCandidate.getNodeID());
    }
}
