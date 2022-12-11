package student.dijkstra;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A DijkstraPath contains a list of adjacent nodes, representing a valid sequence of steps through the cavern.
 * It is used in DijkstraAlgorithm to find shortest paths in graphs composed of DijkstraNodes.
 * A DijkstraPath is composed of DijkstraNodes of a specific type.
 * Loops are allowed.
 *
 * @param <NodeType> the type of the DijkstraNode constituting this DijkstraPath
 */
public abstract class DijkstraPath<NodeType extends DijkstraNode<NodeType>> implements Comparable<DijkstraPath<NodeType>> {
    /**
     * Represents the nodes constituting this DijkstraPath
     */
    private List<NodeType> nodes = new ArrayList<>();

    /**
     * Constructor: an instance with default attributes
     */
    public DijkstraPath() {}

    /**
     * Constructor: an instance with the given list of nodes
     */
    public DijkstraPath(List<NodeType> nodes) {
        this.nodes = new ArrayList<>(nodes);
    }

    /**
     * Constructor: an instance with a list containing only the given node
     */
    public DijkstraPath(NodeType node) {
        this(List.of(node));
    }


    /**
     * Add the given node to this path.
     *
     * @param node the node
     */
    public void addNode(NodeType node) {
        getNodes().add(node);
    }

    /**
     * Return a copy of this path with the given node inserted as the last node on the copy.
     *
     * @param node the node to add
     * @return the copy including the node
     */
    public abstract DijkstraPath<NodeType> cloneWithNode(NodeType node);

    /**
     * Compare two DijkstraPaths.
     * Subclasses must decide how the "length" or "weight" of a path is understood.
     *
     * @param otherPath the path to compare to
     * @return 0 if the paths are equal,
     *         negative if this path is smaller than the other path,
     *         positive if this path is greater than the other path
     */
    @Override
    public abstract int compareTo(DijkstraPath<NodeType> otherPath);


    /**
     * Return the list of nodes constituting this path.
     *
     * @return the list of nodes
     */
    public List<NodeType> getNodes() {
        return nodes;
    }

    /**
     * Set the list of nodes constituting this path to the given list.
     *
     * @param nodes the new nodes
     */
    public void setNodes(List<NodeType> nodes) {
        this.nodes = nodes;
    }

    /**
     * Return the number of nodes in this path.
     *
     * @return the number of nodes
     */
    public Integer getSize() {
        return getNodes().size();
    }

    /**
     * Return the weight of the path. Subclasses decide what "weight" means for a concrete path.
     *
     * @return the weight of this path
     */
    public abstract Integer getWeight();

    /**
     * Return the gold value of the path. Subclasses decide what "value" means for a concrete path.
     *
     * @return the gold value of this path
     */
    public abstract Integer getGold();

    @Override
    public String toString() {
        return getNodes().stream().map(n -> Long.toString(n.getId())).collect(Collectors.joining(", "));
    }
}
