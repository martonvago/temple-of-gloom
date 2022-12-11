package student.escape;

import student.dijkstra.DijkstraPath;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * An EscapePath is a DijkstraPath composed of EscapeNodes.
 * It extends DijkstraPath by including functionality that allows a path to be enhanced, i.e. redrawn
 * between source and destination to traverse more tiles with gold on them.
 */
public class EscapePath extends DijkstraPath<EscapeNode> {
    public EscapePath() {
        super();
    }

    public EscapePath(List<EscapeNode> nodes) {
        super(nodes);
    }

    public EscapePath(EscapeNode node) {
        super(node);
    }

    /**
     * Compare two EscapePaths.
     * The EscapePaths are compared based on their total weight.
     * Ties are broken by the amount of gold on each EscapePath, with the richer EscapePath counting as the smaller one.
     *
     * @param otherPath the EscapePath to compare to
     * @return 0 if the Paths are equal,
     *         negative if this EscapePath is smaller than the other EscapePath,
     *         positive if this EscapePath is greater than the other EscapePath
     */
    @Override
    public int compareTo(DijkstraPath<EscapeNode> otherPath) {
        if (!Objects.equals(getWeight(), otherPath.getWeight())) {
            return getWeight().compareTo(otherPath.getWeight());
        }
        return getGold().compareTo(otherPath.getGold()) * -1;
    }

    /**
     * Return the total amount of gold on this EscapePath that can be collected when walked.
     *
     * @return the amount of gold
     */
    @Override
    public Integer getGold() {
        return new HashSet<>(getNodes()).stream().mapToInt(node -> node.getTile().getGold()).sum();
    }

    /**
     * Return the sum of the weights of all the edges connecting the nodes of this EscapePath.
     *
     * @return the total weight of the EscapePath
     */
    @Override
    public Integer getWeight() {
        int sum = 0;
        for (int i = 1; i < getSize(); i++) {
            long prevId = getNode(i - 1).getId();
            sum += getNode(i).getEdge(prevId).length();
        }
        return sum;
    }

    @Override
    public DijkstraPath<EscapeNode> cloneWithNode(EscapeNode node) {
        EscapePath pathCopy = new EscapePath(getNodes());
        pathCopy.addNode(node);
        return pathCopy;
    }

    /**
     * Enhance and trim the path until the weight budget is spent or the path stops growing.
     *
     * @param maxWeight the maximum budget
     */
    public void optimise(int maxWeight) {
        int unusedBudget = maxWeight - getWeight();
        int oldSize = getSize();

        while (unusedBudget > 0) {
            extend(maxWeight);
            removeGoldlessLoops();
            if (oldSize >= getSize()) {
                break;
            }
            unusedBudget = maxWeight - getWeight();
            oldSize = getSize();
        }
    }

    /**
     * Return the node at the given index of this EscapePath.
     *
     * @param index the index
     * @return the node
     */
    public EscapeNode getNode(int index) {
        return getNodes().get(index);
    }

    /**
     * Alter this EscapePath to increase the amount of collectable gold on it.
     * Add as many unvisited nodes to this EscapePath as possible while preserving the adjacency of the nodes
     * and not exceeding the maximum time (total edge weight) available to walk the EscapePath.
     * <p>
     * This method aims to maximise the amount of gold on the path by replacing each node with a loop starting
     * and ending at the node. It distributes the remaining time (edge weight) equally among the loops.
     * If a loop cannot use up all of its allotted time, it pays it forward to the next loop.
     * Loops without gold are not included.
     *
     * @param maxWeight the maximum possible weight of the complete path
     */
    private void extend(int maxWeight) {
        Set<Long> visitedNodes = getNodes().stream().mapToLong(EscapeNode::getId).boxed().collect(Collectors.toSet());

        if (getSize() == 1) {
            EscapePath enhancedPathForNode = getNode(0).extend(visitedNodes, maxWeight);
            setNodes(enhancedPathForNode.getNodes());
            return;
        }

        EscapePath enhancedPath = new EscapePath();

        Stack<Integer> budgets = getBudgetsForNodes(maxWeight);
        int leftoverBudget = 0;
        for (int i = 0; i < getSize() - 1; i++) {
            EscapeNode nodeOnPath = getNode(i);

            Integer budget = budgets.pop() + leftoverBudget;
            EscapePath enhancedPathForNode = nodeOnPath.extend(visitedNodes, budget);
            if (enhancedPathForNode.getGold() - nodeOnPath.getTile().getGold() > 0) {
                leftoverBudget = budget - enhancedPathForNode.getWeight();
                enhancedPath.joinPath(enhancedPathForNode);
            } else {
                enhancedPath.addNode(nodeOnPath);
                leftoverBudget = budget;
            }
        }
        enhancedPath.addNode(getNode(getSize() - 1));
        setNodes(enhancedPath.getNodes());
    }

    /**
     * Find loops of the given size in this EscapePath which contain no gold.
     * Return the indices corresponding to the nodes of the loops on this EscapePath, excluding the last node in each loop.
     *
     * @param size the size of the loop
     * @return the list of indices
     */
    private List<Integer> findGoldlessLoopsOfSize(int size) {
        List<Integer> indices = new ArrayList<>();
        int offset = size - 1;
        for (int i = 0; i < getSize() - offset; i++) {
            EscapePath subpath = getSubpath(i, size);
            if (subpath.isLoop() && subpath.getGold() == 0) {
                IntStream.range(i, i + size - 1).forEach(indices::add);
            }
        }
        return indices;
    }

    /**
     * Allocate the remaining time (edge weight) available to walk the path equally among the nodes of this path.
     * The remaining time is the total available edge weight minus the weight of this EscapePath.
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

    /**
     * Return a EscapePath containing a sublist of the nodes of this EscapePath.
     *
     * @param start an index into this EscapePath specifying the start of the sublist
     * @param size the number of nodes in the sublist
     * @return the EscapePath containing the sublist
     */
    private EscapePath getSubpath(int start, int size) {
        int end = start + size - 1;
        if (getSize() <= end) {
            throw new IndexOutOfBoundsException();
        }

        EscapePath subpath = new EscapePath();
        for (int i = start; i <= end; i++) {
            subpath.addNode(getNode(i));
        }
        return subpath;
    }

    /**
     * Return whether this EscapePath is a loop, i.e. whether it starts and ends on the same node.
     *
     * @return if this EscapePath is a loop
     */
    private boolean isLoop() {
        if (getSize() < 2) {
            return false;
        }
        EscapeNode start = getNode(0);
        EscapeNode end = getNode(getSize() - 1);
        return start.equals(end);
    }

    /**
     * Add the nodes of the given EscapePath to the list of nodes of this EscapePath.
     *
     * @param path the path to join
     */
    private void joinPath(EscapePath path) {
        getNodes().addAll(path.getNodes());
    }

    /**
     * Find loops of all sizes in this EscapePath which contain no gold and remove them.
     */
    private void removeGoldlessLoops() {
        int smallestLoopSize = 3;
        int largestLoopSize = getSize();
        for (int size = smallestLoopSize; size <= largestLoopSize; size++) {
            removeGoldlessLoopsOfSize(size);
        }
    }

    /**
     * Find loops of the given size in this EscapePath which contain no gold and remove them.
     *
     * @param size the size of the loop
     */
    private void removeGoldlessLoopsOfSize(int size) {
        List<Integer> indicesToRemove = findGoldlessLoopsOfSize(size);
        List<EscapeNode> trimmed = new ArrayList<>();
        for (int i = 0; i < getSize(); i++) {
            if (!indicesToRemove.contains(i)) {
                trimmed.add(getNode(i));
            }
        }

        setNodes(trimmed);
    }

    /**
     * Replace the node at the given index of this EscapePath with the given nodes.
     *
     * @param nodes the nodes to add
     * @param index the index of the node to replace
     */
    public void replaceAtIndex(List<EscapeNode> nodes, int index) {
        getNodes().remove(index);
        getNodes().addAll(index, nodes);
    }
}
