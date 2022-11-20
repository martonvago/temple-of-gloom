package student;

import game.Node;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

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
        nodes.add(node);
    }

    public Integer getGold() {
        return new HashSet<>(nodes).stream().mapToInt(node -> node.getTile().getGold()).sum();
    }

    public Integer getWeight() {
        int sum = 0;
        for (int i = 1; i < nodes.size(); i++) {
            Node prev = nodes.get(i - 1);
            sum += nodes.get(i).getEdge(prev).length();
        }
        return sum;
    }

    public Path cloneWithNode(Node node) {
        Path pathCopy = new Path(nodes);
        pathCopy.addNode(node);
        return pathCopy;
    }

    public void joinPath(Path path) {
        this.nodes.addAll(path.getNodes());
    }

    public void replaceAtIndex(List<Node> nodes, int index) {
        this.nodes.remove(index);
        this.nodes.addAll(index, nodes);
    }

    public int getSize() {
        return nodes.size();
    }

    public Node getNode(int index) {
        return nodes.get(index);
    }

    public Path getSubpath(int start, int size) {
        int end = start + size - 1;
        if (nodes.size() <= end) {
            throw new IndexOutOfBoundsException();
        }

        Path subpath = new Path();
        for (int i = start; i <= end; i++) {
            subpath.addNode(nodes.get(i));
        }
        return subpath;
    }

    public boolean isLoop() {
        if (nodes.size() < 2) {
            return false;
        }
        Node start = nodes.get(0);
        Node end = nodes.get(nodes.size() - 1);
        return start.equals(end);
    }

    @Override
    public int compareTo(Path otherPath) {
        if (!Objects.equals(this.getWeight(), otherPath.getWeight())) {
            return this.getWeight().compareTo(otherPath.getWeight());
        }
        return this.getGold().compareTo(otherPath.getGold()) * -1;
    }
}
