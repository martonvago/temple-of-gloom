package student.dijkstra;

import java.util.Set;

/**
 * This interface specifies what methods a node must implement to allow a graph composed of these nodes
 * to be processed by DijkstraAlgorithm.
 *
 * @param <NodeType> a type implementing DijkstraNode
 */
public interface DijkstraNode<NodeType extends DijkstraNode<NodeType>> {
    /**
     * Return the id of the node
     *
     * @return the node id
     */
    long getId();

    /**
     * Return the neighbours of the node, preserving their type
     *
     * @return the neighbours
     */
    Set<NodeType> getNeighbours();

    /**
     * Return a DijkstraPath of the correct type containing the node
     *
     * @return the path
     */
    DijkstraPath<NodeType> wrapToPath();
}
