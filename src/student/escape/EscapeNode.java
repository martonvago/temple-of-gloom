package student.escape;

import game.Edge;
import game.Node;
import game.Tile;
import student.EqualsById;
import student.dijkstra.DijkstraNode;
import student.dijkstra.DijkstraPath;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * EscapeNode is a wrapper around game.Node, allowing us to implement the DijkstraNode interface,
 * and so use DijkstraAlgorithm in the escape phase.
 * A wrapper is necessary because game.Node cannot be modified (as per the project specification) or extended (its
 * constructors are package-private).
 */
public class EscapeNode extends EqualsById implements DijkstraNode<EscapeNode> {
    /**
     * The game.Node wrapped by this EscapeNode
     */
    private final Node delegate;

    /**
     * Construct an EscapeNode from a given game.Node.
     *
     * @param delegateNode the game.Node to wrap
     */
    public EscapeNode(Node delegateNode) {
        delegate = delegateNode;
    }

    @Override
    public long getId() {
        return delegate.getId();
    }

    @Override
    public Set<EscapeNode> getNeighbours() {
        return delegate.getNeighbours().stream().map(EscapeNode::new).collect(Collectors.toSet());
    }

    @Override
    public DijkstraPath<EscapeNode> wrapToPath() {
        return new EscapePath(this);
    }

    /**
     * Return the wrapped game.Node.
     *
     * @return the game.Node
     */
    public Node getDelegate() {
        return delegate;
    }

    /**
     * Return the Tile associated with this node.
     *
     * @return the Tile
     */
    public Tile getTile() {
        return delegate.getTile();
    }

    /**
     * Return the Edge from this node to another node in the maze.
     *
     * @param otherNodeId the id of the node on the other end of the Edge
     * @return the Edge
     */
    public Edge getEdge(Long otherNodeId) {
        Node otherDelegate = delegate.getNeighbours().stream()
                .filter(neighbour -> neighbour.getId() == otherNodeId)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("getEdge: Node must be a neighbour of this Node"));
        return delegate.getEdge(otherDelegate);
    }

    /**
     * Return a EscapePath starting and ending at the given node containing as many unvisited nodes as possible.
     * The total weight of the EscapePath must not exceed the amount specified in the budget.
     *
     * @param visitedNodes a set of nodes already visited
     * @param budget the maximum weight of the EscapePath
     * @return the newly created EscapePath
     */
    public EscapePath extend(Set<Long> visitedNodes, Integer budget) {
        EscapePath nodeLoop = new EscapePath(this);
        int i = 0;
        while (i < nodeLoop.getSize()) {
            EscapeNode current = nodeLoop.getNode(i);

            List<EscapeNode> neighbours = current.getNeighbours().stream()
                    .filter(neighbour -> !visitedNodes.contains(neighbour.getId()))
                    .toList();

            for (EscapeNode neighbour : neighbours) {
                EscapePath pathToNeighbourAndBack = new EscapePath(List.of(current, neighbour, current));
                if (budget - pathToNeighbourAndBack.getWeight() >= 0) {
                    nodeLoop.replaceAtIndex(pathToNeighbourAndBack.getNodes(), i);
                    visitedNodes.add(neighbour.getId());
                    budget -= pathToNeighbourAndBack.getWeight();
                }
            }
            i++;
        }
        return nodeLoop;
    }
}
