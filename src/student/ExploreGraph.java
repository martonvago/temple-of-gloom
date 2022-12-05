package student;

import game.NodeStatus;

import java.util.*;


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

//                System.out.println("Creating neighbour " + neighbour.nodeID());
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

//        System.out.println("all neighbours " + neighbours);

        for(var neighbour : neighbours){
            if (!neighbour.getVisited()){
                UnexploredNeighbours.add(neighbour);
            }
//            else {
//                System.out.println("binning " + neighbour);
//            }
        }

        Collections.sort(UnexploredNeighbours);
        return UnexploredNeighbours;
    }

    List<exploreNode> ShortestPathTo(long start, long target){

        Queue<exploreNode> queue = new LinkedList<>();
        queue.add(nodeMap.get(start));
        nodeMap.get(start).visitedBFS();
        var end = nodeMap.get(target);

        while(!queue.isEmpty()){
            //pop a node from queue for search operation
            var current_node = queue.poll();
            //Loop through neighbors node to find the 'end'
            for(var node: current_node.getNeighbours()){
                if(!node.is_visitedBFS()){
                    //Visit and add the node to the queue
                    node.visitedBFS();
                    queue.add(node);
                    //update its precedings nodes
                    node.previousBFS(current_node);
                    //If reached the end node then stop BFS
                    if(node==end){
                        queue.clear();
                        break;
                    }
                }
            }
        }

        List<exploreNode> route = new ArrayList<>();
        var node = end;
        while(node != null){
            route.add(node);
            node = node.bfs_previous();
        }
        Collections.reverse(route);
        // Remove first element as that the current node we are on
        route.remove(0);
        reset_bfs();
        return route;
    }

    private void reset_bfs(){
        for (var n : nodeMap.entrySet()){
            n.getValue().resetBfs();
        }
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

    private boolean bfs_visited = false;
    private exploreNode bfs_previous = null;


    @Override
    public int compareTo(exploreNode n2){

        // if distances are the same pick the one with the highest node id
        if (this.getDistanceToTarget() == n2.getDistanceToTarget()){

            if (this.getNodeID() > n2.getNodeID()){
                return 1;
            } else {
                return -1;
            }
        } else {
            if (this.getDistanceToTarget() > n2.getDistanceToTarget()){
                return 1;
            } else {
                return -1;
            }

        }

    }

    public exploreNode( long id, int distance, boolean seen){

        nodeID = id;
        distanceToTarget = distance;
        visited = seen;

    }

    public void visitedBFS(){
        bfs_visited = true;
    }

    public boolean is_visitedBFS(){
        return bfs_visited;
    }

    public void previousBFS(exploreNode node) {
        bfs_previous = node;
    }

    public void resetBfs(){
        bfs_visited = false;
        bfs_previous = null;
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

    public exploreNode bfs_previous() {
        return bfs_previous;
    }
}
