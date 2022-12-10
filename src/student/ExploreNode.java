package student;

import java.util.HashSet;
import java.util.Set;

public class ExploreNode implements Comparable<ExploreNode> {

    private final Set<ExploreNode> neighbours = new HashSet<>();
    private final int distanceToTarget;
    private boolean visited;
    private final long nodeID;


    @Override
    public int compareTo(ExploreNode n2) {
        // if distances are the same pick the one with the highest node id
        return Integer.compare(this.getDistanceToTarget(), n2.getDistanceToTarget());
    }

    public ExploreNode(long id, int distance, boolean visit) {

        nodeID = id;
        distanceToTarget = distance;
        visited = visit;

    }

    public void Visit() {
        visited = true;
    }

    public boolean Visited() {
        return visited;
    }

    public void AddNeighbour(ExploreNode neighbour) {
        this.neighbours.add(neighbour);
    }

    public Set<ExploreNode> getNeighbours() {
        return neighbours;
    }

    public int getDistanceToTarget() {
        return distanceToTarget;
    }

    public long getNodeID() {
        return nodeID;
    }

    @Override
    public String toString() {
        return "node " + nodeID + " distance " + distanceToTarget;
    }
}
