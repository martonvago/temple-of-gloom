package student.explore;

import student.EqualsById;
import student.dijkstra.DijkstraPath;
import student.dijkstra.DijkstraNode;

import java.util.HashSet;
import java.util.Set;

public class ExploreNode extends EqualsById implements DijkstraNode<ExploreNode>, Comparable<ExploreNode> {
    private final long id;
    private final Set<ExploreNode> neighbours = new HashSet<>();
    private final int distanceToTarget;
    private boolean visited;

    public ExploreNode(long nodeId, int distance, boolean vis) {
        id = nodeId;
        distanceToTarget = distance;
        visited = vis;
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public Set<ExploreNode> getNeighbours() {
        return neighbours;
    }

    @Override
    public DijkstraPath<ExploreNode> wrapToPath() {
        return new ExplorePath(this);
    }

    /**
     * Compares two ExploreNodes by their distance to the target. Ties are broken by their id.
     *
     * @param otherNode the node to compare with
     * @return the result of the comparison
     */
    @Override
    public int compareTo(ExploreNode otherNode) {
        if (getDistanceToTarget() != otherNode.getDistanceToTarget()) {
            return Integer.compare(getDistanceToTarget(), otherNode.getDistanceToTarget());
        }
        return Long.compare(getId(), otherNode.getId());
    }

    @Override
    public String toString() {
        return "[ExploreNode " + id + "] " + distanceToTarget + " from target";
    }

    /**
     * Mark this ExploreNode as visited.
     */
    public void visit(){
        visited = true;
    }

    /**
     * Return whether this node has been visited.
     *
     * @return whether this node has been visited
     */
    public boolean getVisited(){
        return visited;
    }

    /**
     * Add an ExploreNode as a neighbour of this node.
     *
     * @param neighbour the neighbour
     */
    public void addNeighbour(ExploreNode neighbour){
        neighbours.add(neighbour);
    }

    /**
     * Return the distance of this node from the target.
     *
     * @return the distance
     */
    public int getDistanceToTarget() {
        return distanceToTarget;
    }
}
