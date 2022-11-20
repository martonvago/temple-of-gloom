package student;

import game.Node;

import java.util.*;
import java.util.stream.IntStream;

public class Path implements Comparable<Path> {
    private List<Node> nodes = new ArrayList<>();

    public Path() {}

    public Path(List<Node> nodes) {
        this.nodes = new ArrayList<>(nodes);
    }

    public Path(Node node) {
        this(List.of(node));
    }

    public List<Node> getNodes() {
        return nodes;
    }

    public void addNode(Node node) {
        getNodes().add(node);
    }

    public Integer getGold() {
        return new HashSet<>(getNodes()).stream().mapToInt(node -> node.getTile().getGold()).sum();
    }

    public Integer getWeight() {
        int sum = 0;
        for (int i = 1; i < getSize(); i++) {
            Node prev = getNode(i - 1);
            sum += getNode(i).getEdge(prev).length();
        }
        return sum;
    }

    public Path cloneWithNode(Node node) {
        Path pathCopy = new Path(getNodes());
        pathCopy.addNode(node);
        return pathCopy;
    }

    public void joinPath(Path path) {
        getNodes().addAll(path.getNodes());
    }

    public void replaceAtIndex(List<Node> nodes, int index) {
        getNodes().remove(index);
        getNodes().addAll(index, nodes);
    }

    public int getSize() {
        return getNodes().size();
    }

    public Node getNode(int index) {
        return getNodes().get(index);
    }

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

    public boolean isLoop() {
        if (getSize() < 2) {
            return false;
        }
        Node start = getNode(0);
        Node end = getNode(getSize() - 1);
        return start.equals(end);
    }

    @Override
    public int compareTo(Path otherPath) {
        if (!Objects.equals(getWeight(), otherPath.getWeight())) {
            return getWeight().compareTo(otherPath.getWeight());
        }
        return getGold().compareTo(otherPath.getGold()) * -1;
    }

    public void removeGoldlessLoops() {
        int smallestLoopSize = 3;
        int largestLoopSize = getSize();
        for (int size = smallestLoopSize; size <= largestLoopSize; size++) {
            removeGoldlessLoopsOfSize(size);
        }
    }

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
