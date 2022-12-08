package student;

import game.NodeStatus;

import java.util.*;
import java.util.stream.Collectors;


public class ExploreGraph {

    private final Map<Long, exploreNode> nodeMap = new HashMap<>();
    // Tracks the nodes we've visited to deprioritize traversing them
    private final Set<exploreNode> seen = new HashSet<>();


    public void logNodeVisit(long current, int distance, Collection<NodeStatus> neighbours) {

        System.out.println("visiting node " + current);

        if (!nodeMap.containsKey(current)){
            // First time visiting a node
            var current_node = new exploreNode( current, distance, true);
            nodeMap.put(current, current_node);
        } else {
            // visiting a node that is already present
            var current_node = nodeMap.get(current);
            // Remove from the seen set
            seen.remove(current_node);
            current_node.setVisited();
        }

        var parent = nodeMap.get(current);

        for (var neighbour : neighbours){
            // Only create new nodes if they have not been seen before
            if (!nodeMap.containsKey(neighbour.nodeID())){

                var neighbour_node =new exploreNode( neighbour.nodeID(), neighbour.distanceToTarget(), false);
                // Add parent node
                neighbour_node.AddNeighbour(parent);
                nodeMap.put(neighbour.nodeID(), neighbour_node);
                seen.add(neighbour_node);
            }
            // since the underlying data structure is a set we don't need to worry.
            parent.AddNeighbour(nodeMap.get(neighbour.nodeID()));
        }

    }

    public List<exploreNode> getUnexploredNeighbours(long current) {
        var neighbours = nodeMap.get(current).getNeighbours();
        List<exploreNode> UnexploredNeighbours = new ArrayList<>();

        for(var neighbour : neighbours){
            if (!neighbour.getVisited()){
                UnexploredNeighbours.add(neighbour);
            }
        }

        Collections.sort(UnexploredNeighbours);
        return UnexploredNeighbours;
    }

    private List<exploreNode> shortestPathTo(long start, long target){

        Queue<exploreNode> queue = new LinkedList<>();
        queue.add(nodeMap.get(start));
        var startNode =  nodeMap.get(start);
        var endNode = nodeMap.get(target);

        Map<exploreNode, Boolean> bfsVisited = new HashMap<>();
        Map<exploreNode, exploreNode> bfsPrevious = new HashMap<>();

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

        List<exploreNode> route = new ArrayList<>();
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

    public List<exploreNode> getPathToBestNode(long current){

        // Get sorted list of unseen nodes
        // Select all nodes that have are withing range of n + threshold, where n is the lowest distance
        // select the one is the shortest distance to where the explorer is

        var list = new ArrayList<>(seen.stream().toList());

        if (list.isEmpty()){
            System.err.println("maze is un-solvable");
            return new ArrayList<>();
        }

        Collections.sort(list);
        int distanceThreshold = 1;
        var closestNodeDistance = list.get(0).getDistanceToTarget() + distanceThreshold;

        var thresholdList = list.stream()
                .filter(n -> n.getDistanceToTarget() < closestNodeDistance)
                .collect(Collectors.toList());

        if (thresholdList.size() == 1) {
            return shortestPathTo(current, thresholdList.get(0).getNodeID());
        }

        exploreNode bestCandidate = null;
        int CandidateDistance = 0;
        for (var candidate : thresholdList){
            var path = shortestPathTo(current, candidate.getNodeID());

            if (bestCandidate == null){
                bestCandidate = candidate;
                CandidateDistance = path.size();

            } else {
                if(CandidateDistance > path.size()) {
                bestCandidate = candidate;
                CandidateDistance = path.size();
             }
            }
        }

        assert bestCandidate != null;
        return shortestPathTo(current, bestCandidate.getNodeID());
    }



    /**
     * Finds a NodeID that has the lowest distance that's unvisted
     *
     * @return Long
     */
    public exploreNode getClosestUnexploredNodeToGoal() {
        var list = new ArrayList<>(seen.stream().toList());
        Collections.sort(list);
        return list.get(0);
    }



}

class exploreNode implements Comparable<exploreNode>{

    private final Set<exploreNode> neighbours = new HashSet<>();
    private final int distanceToTarget;
    private boolean visited;
    private final long nodeID;


    @Override
    public int compareTo(exploreNode n2){
        // if distances are the same pick the one with the highest node id
        return Integer.compare(this.getDistanceToTarget(), n2.getDistanceToTarget());
    }

    public exploreNode( long id, int distance, boolean seen){

        nodeID = id;
        distanceToTarget = distance;
        visited = seen;

    }

    public void setVisited(){
        visited = true;
    }

    public boolean getVisited(){
        return visited;
    }

    public void AddNeighbour(exploreNode neighbour){
        this.neighbours.add(neighbour);
    }

    public Set<exploreNode> getNeighbours() {
        return neighbours;
    }

    public int getDistanceToTarget() {
        return distanceToTarget;
    }

    public long getNodeID(){
        return nodeID;
    }

    @Override
    public String toString() {
        return "exploreNode{" +
                "nodeID=" + nodeID +
                '}';
    }
}

//-s -8565390209477936194