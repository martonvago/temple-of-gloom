package student;

import game.Node;

import java.util.*;
import java.util.stream.IntStream;

/**
 * A Path contains a list of adjacent nodes, representing a valid sequence of steps through the cavern.
 * Loops are allowed.
 */
public class Path implements Comparable<Path> {
    /**
     * Represents the nodes constituting this Path
     */
    private List<Node> nodes = new ArrayList<>();

    /**
     * Constructor: an instance with default attributes
     */
    public Path() {}

    /**
     * Constructor: an instance with the given list of nodes
     */
    public Path(List<Node> nodes) {
        this.nodes = new ArrayList<>(nodes);
    }

    /**
     * Constructor: an instance with a list containing only the given node
     */
    public Path(Node node) {
        this(List.of(node));
    }

    /**
     * Return the list of nodes constituting this Path.
     *
     * @return the list of nodes
     */
    public List<Node> getNodes() {
        return nodes;
    }

    /**
     * Add the given node to this Path.
     *
     * @param node the node
     */
    public void addNode(Node node) {
        getNodes().add(node);
    }

    /**
     * Return the total amount of gold on this Path that can be collected when walked.
     *
     * @return the amount of gold
     */
    public Integer getGold() {
        return new HashSet<>(getNodes()).stream().mapToInt(node -> node.getTile().getGold()).sum();
    }

    /**
     * Return the sum of the weights of all the edges connecting the nodes of this Path.
     *
     * @return the total weight of the Path
     */
    public Integer getWeight() {
        int sum = 0;
        for (int i = 1; i < getSize(); i++) {
            Node prev = getNode(i - 1);
            sum += getNode(i).getEdge(prev).length();
        }
        return sum;
    }

    /**
     * Return a copy of this Path with the given node inserted as the last node on the copy.
     *
     * @param node the node to add
     * @return the copy including the node
     */
    public Path cloneWithNode(Node node) {
        Path pathCopy = new Path(getNodes());
        pathCopy.addNode(node);
        return pathCopy;
    }

    /**
     * Add the nodes of the given Path to the list of nodes of this Path.
     *
     * @param path the path to join
     */
    public void joinPath(Path path) {
        getNodes().addAll(path.getNodes());
    }

    /**
     * Replace the node at the given index of this Path with the given nodes.
     *
     * @param nodes the nodes to add
     * @param index the index of the node to replace
     */
    public void replaceAtIndex(List<Node> nodes, int index) {
        getNodes().remove(index);
        getNodes().addAll(index, nodes);
    }

    /**
     * Return the number of nodes in this Path.
     *
     * @return the number of nodes
     */
    public int getSize() {
        return getNodes().size();
    }

    /**
     * Return the node at the given index of this Path.
     *
     * @param index the index
     * @return the node
     */
    public Node getNode(int index) {
        return getNodes().get(index);
    }

    /**
     * Return a Path containing a sublist of the nodes of this Path.
     *
     * @param start an index into this Path specifying the start of the sublist
     * @param size the number of nodes in the sublist
     * @return the Path containing the sublist
     */
    public Path getSubpath(int start, int size) {
        int end = start + size - 1;
        if (getSize() <= end) {
            throw new IndexOutOfBoundsException();
        }

        Path subpath = new Path();
        for (int i = start; i <= end; i++) {
            subpath.addNode(getNode(i));
        }
        return subpath;
    }

    /**
     * Return whether this Path is a loop, i.e. whether it starts and ends on the same node.
     *
     * @return if this Path is a loop
     */
    public boolean isLoop() {
        if (getSize() < 2) {
            return false;
        }
        Node start = getNode(0);
        Node end = getNode(getSize() - 1);
        return start.equals(end);
    }

    /**
     * Compare two Paths.
     * The Paths are compared based on their total weight.
     * Ties are broken by the amount of gold on each Path, with the richer Path counting as the smaller one.
     *
     * @param otherPath the Path to compare to
     * @return 0 if the Paths are equal,
     *         negative if this Path is smaller than the other Path,
     *         positive if this Path is greater than the other Path
     */
    @Override
    public int compareTo(Path otherPath) {
        if (!Objects.equals(getWeight(), otherPath.getWeight())) {
            return getWeight().compareTo(otherPath.getWeight());
        }
        return getGold().compareTo(otherPath.getGold()) * -1;
    }

    /**
     * Find loops of all sizes in this Path which contain no gold and remove them.
     */
    public void removeGoldlessLoops() {
        int smallestLoopSize = 3;
        int largestLoopSize = getSize();
        for (int size = smallestLoopSize; size <= largestLoopSize; size++) {
            removeGoldlessLoopsOfSize(size);
        }
    }

    /**
     * Find loops of the given size in this Path which contain no gold and remove them.
     *
     * @param size the size of the loop
     */
    private void removeGoldlessLoopsOfSize(int size) {
        List<Integer> indicesToRemove = findGoldlessLoopsOfSize(size);
        List<Node> trimmed = new ArrayList<>();
        for (int i = 0; i < getSize(); i++) {
            if (!indicesToRemove.contains(i)) {
                trimmed.add(getNode(i));
            }
        }

        nodes = trimmed;
    }

    /**
     * Find loops of the given size in this Path which contain no gold.
     * Return the indices corresponding to the nodes of the loops on this Path, excluding the last node in each loop.
     *
     * @param size the size of the loop
     * @return the list of indices
     */
    private List<Integer> findGoldlessLoopsOfSize(int size) {
        List<Integer> indices = new ArrayList<>();
        int offset = size - 1;
        for (int i = 0; i < getSize() - offset; i++) {
            Path subpath = getSubpath(i, size);
            if (subpath.isLoop() && subpath.getGold() == 0) {
                IntStream.range(i, i + size - 1).forEach(indices::add);
            }
        }
        return indices;
    }

    /**
     * Alter this Path to increase the amount of collectable gold on it.
     * Add as many unvisited nodes to this Path as possible while preserving the adjacency of the nodes
     * and not exceeding the maximum time (total edge weight) available to walk the Path.
     *
     * This method aims to maximise the amount of gold on the path by replacing each node with a loop starting
     * and ending at the node. It distributes the remaining time (edge weight) equally among the loops.
     * If a loop cannot use up all of its allotted time, it pays it forward to the next loop.
     * Loops without gold are not included.
     *
     * @param maxWeight the maximum possible weight of the complete path
     */
    public void enhancePath(int maxWeight) {
        Set<Node> visitedNodes = new HashSet<>(getNodes());
        Path enhancedPath = new Path();

        Stack<Integer> budgets = getBudgetsForNodes(maxWeight);
        int leftoverBudget = 0;
        for (int i = 0; i < getSize() - 1; i++) {
            Node nodeOnPath = getNode(i);

            Integer budget = budgets.pop() + leftoverBudget;
            Path enhancedPathForNode = enhanceNode(nodeOnPath, visitedNodes, budget);
            if (enhancedPathForNode.getGold() - nodeOnPath.getTile().getGold() > 0) {
                leftoverBudget = budget - enhancedPathForNode.getWeight();
                enhancedPath.joinPath(enhancedPathForNode);
            } else {
                enhancedPath.addNode(nodeOnPath);
                leftoverBudget = budget;
            }
        }
        enhancedPath.addNode(getNode(getSize() - 1));
        nodes = enhancedPath.getNodes();
    }

    /**
     * Return a Path starting and ending at the given node containing as many unvisited nodes as possible.
     * The total weight of the Path must not exceed the amount specified in the budget.
     *
     * @param node the first node of the Path
     * @param visitedNodes a set of nodes already visited
     * @param budget the maximum weight of the Path
     * @return the newly created Path
     */
    private Path enhanceNode(Node node, Set<Node> visitedNodes, Integer budget) {
        Path nodeLoop = new Path(node);
        int i = 0;
        while (i < nodeLoop.getSize()) {
            Node current = nodeLoop.getNode(i);

            List<Node> neighbours = current.getNeighbours().stream()
                    .filter(neighbour -> !visitedNodes.contains(neighbour))
                    .toList();

            for (Node neighbour : neighbours) {
                Path pathToNeighbourAndBack = new Path(List.of(current, neighbour, current));
                if (budget - pathToNeighbourAndBack.getWeight() >= 0) {
                    nodeLoop.replaceAtIndex(pathToNeighbourAndBack.getNodes(), i);
                    visitedNodes.add(neighbour);
                    budget -= pathToNeighbourAndBack.getWeight();
                }
            }
            i++;
        }
        return nodeLoop;
    }

    /**
     * Allocate the remaining time (edge weight) available to walk the path equally among the nodes of this path.
     * The remaining time is the total available edge weight minus the weight of this Path.
     *
     * @param maxWeight the maximum possible weight of the complete path
     * @return a stack of the allocated weights
     */
    private Stack<Integer> getBudgetsForNodes(int maxWeight) {
        Stack<Integer> weights = new Stack<>();
        int nodeNumberExcludingExit = getSize() - 1;

        int spareWeight = maxWeight - getWeight();
        int allocated = spareWeight / nodeNumberExcludingExit;
        int toAllocate = spareWeight % nodeNumberExcludingExit;
        IntStream.range(0, nodeNumberExcludingExit)
                .forEach( i -> weights.push(i < toAllocate ? allocated + 1 : allocated));
        return weights;
    }
}
