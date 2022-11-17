package student;

import game.Node;

import java.util.ArrayList;
import java.util.List;

public class Path {
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

    public int getGold() {
        return path.stream().mapToInt(node -> node.getTile().getGold()).sum();
    }

    public int getWeight() {
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
}
