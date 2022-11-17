package student;

import game.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Path implements Comparable<Path> {
    private final List<Node> path;

    public Path(List<Node> path) {
        this.path = path;
    }

    public Path(Node node) {
        this.path = new ArrayList<>(List.of(node));;
    }

    public List<Node> getPath() {
        return path;
    }

    public void addNode(Node node) {
        path.add(node);
    }

    public Integer getGold() {
        return path.stream().mapToInt(node -> node.getTile().getGold()).sum();
    }

    public Integer getWeight() {
        int sum = 0;
        for (int i = 1; i < path.size(); i++) {
            Node prev = path.get(i - 1);
            sum += path.get(i).getEdge(prev).length();
        }
        return sum;
    }

    public Path cloneWithNode(Node node) {
        Path pathCopy = new Path(new ArrayList<>(path));
        pathCopy.addNode(node);
        return pathCopy;
    }

    @Override
    public int compareTo(Path otherPath) {
        if (!Objects.equals(this.getWeight(), otherPath.getWeight())) {
            return this.getWeight().compareTo(otherPath.getWeight());
        }
        return this.getGold().compareTo(otherPath.getGold()) * -1;
    }
}
