package student;

import game.NodeStatus;

import java.util.*;
import java.util.function.Predicate;


public class ExploreGraph {
    private final Map<Long, ExploreNode> nodeMap = new HashMap<>();

    public void logNodeVisit(long current, int distance, Collection<NodeStatus> neighbours) {

        System.out.println("visiting node " + current + " distance " + distance);

        if (!nodeMap.containsKey(current)){
            // First time visiting a node
            var current_node = new ExploreNode( current, distance, true);
            nodeMap.put(current, current_node);
        } else {
            // visiting a node that is already present
            var current_node = nodeMap.get(current);
            current_node.Visit();
        }

        var parent = nodeMap.get(current);

        for (var neighbour : neighbours){
            // Only create new nodes if they have not been seen before
            if (!nodeMap.containsKey(neighbour.nodeID())){

                var neighbour_node =new ExploreNode( neighbour.nodeID(), neighbour.distanceToTarget(), false);
                // Add parent node
                neighbour_node.AddNeighbour(parent);
                nodeMap.put(neighbour.nodeID(), neighbour_node);
            }
            // since the underlying data structure is a set we don't need to worry.
            parent.AddNeighbour(nodeMap.get(neighbour.nodeID()));
        }

    }

    public List<ExploreNode> getUnexploredNeighbours(long current) {

        return nodeMap.get(current).getNeighbours().stream()
                .filter(Predicate.not(ExploreNode::Visited))
                .sorted()
                .toList();
    }

    private List<ExploreNode> shortestPathTo(long start, long target){

        Queue<ExploreNode> queue = new LinkedList<>();
        queue.add(nodeMap.get(start));
        var startNode =  nodeMap.get(start);
        var endNode = nodeMap.get(target);

        Map<ExploreNode, Boolean> bfsVisited = new HashMap<>();
        Map<ExploreNode, ExploreNode> bfsPrevious = new HashMap<>();

        // null is a sentinel value
        bfsVisited.put(startNode, true);
        bfsPrevious.put(startNode, null);

        while(!queue.isEmpty()){
            //pop a node from queue for search operation
            var current_node = queue.poll();
            //Loop through neighbors node to find the 'end'
            for(var node: current_node.getNeighbours()){
                if(!bfsVisited.containsKey(node)){
                    //Visit and add the node to the queue
                    bfsVisited.put(node, true);
                    queue.add(node);
                    //update its precedings nodes
                    bfsPrevious.put(node, current_node);
                    //If reached the end node then stop BFS
                    if(node==endNode){
                        queue.clear();
                        break;
                    }
                }
            }
        }
        return convertToPath(bfsPrevious, endNode);
    }

    /**
     * @param bfsPrevious a map representing a linked list like structure of nodes
     * @param endNode a sentinel node indicating that the path is complete
     * @return a list of Explorer nodes that represent a path
     */
    private List<ExploreNode> convertToPath(Map<ExploreNode, ExploreNode> bfsPrevious, ExploreNode endNode){

        List<ExploreNode> route = new ArrayList<>();
        var node = endNode;
        while(node != null){
            var previous = bfsPrevious.get(node);

            if (previous == null){
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
     * @param current Current location of the player
     * @return List of Nodes that represent a path to the best location
     */
    public List<ExploreNode> getPathToBestNode(long current){

        int distanceThreshold = 1;

        // Potential idea to be more clever
        // First select best node within a radius of the explorer
        // If all nodes are exhausted than switch to current function.

        // Get sorted list of unseen nodes
        var unseenNodes =  nodeMap.entrySet()
                .stream()
                .filter(Predicate.not(entry -> entry.getValue().Visited()))
                .map(Map.Entry::getValue)
                .sorted().toList();

        if (unseenNodes.isEmpty()){
            System.err.println("maze is un-solvable");
            return new ArrayList<>();
        }

        // Select all nodes that have are withing range of n + threshold, where n is the lowest distance
        var closestNodeDistance = unseenNodes.get(0).getDistanceToTarget() + distanceThreshold;
        var thresholdList = unseenNodes.stream()
                .filter(n -> n.getDistanceToTarget() < closestNodeDistance)
                .toList();

        // return early if there is only one entry
        if (thresholdList.size() == 1) {
            return shortestPathTo(current, thresholdList.get(0).getNodeID());
        }

        // select the one is the shortest distance to where the explorer is
        var bestCandidate =  thresholdList.stream()
                .min((n1, n2) -> Integer.compare(
                        shortestPathTo(current, n1.getNodeID()).size(),
                        shortestPathTo(current, n2.getNodeID()).size()))
                .get();


        return shortestPathTo(current, bestCandidate.getNodeID());
    }


    /**
     * Finds a NodeID that has the lowest distance that's unvisted
     *
     * @return Long
     */
    public ExploreNode getClosestUnexploredNodeToGoal() {
        return nodeMap.entrySet()
                .stream()
                .filter(Predicate.not(entry -> entry.getValue().Visited()))
                .map(Map.Entry::getValue)
                .sorted().toList().get(0);
    }

}

//-s -8565390209477936194