package student;

import game.Node;

import java.util.ArrayList;
import java.util.List;

public class PathOptions {
    private final Integer maxWeight;
    private List<Node> shortest = null;
    private List<Node> richest = null;
    private List<List<Node>> paths = new ArrayList<>();

    public PathOptions(int maxWeight) {
        this.maxWeight = maxWeight;
    }

    public PathOptions(PathOptions pathOptions, int maxWeight) {
        this.maxWeight = maxWeight;
        if (pathOptions != null) {
            this.paths = pathOptions.getPaths();
            this.shortest = pathOptions.getShortest();
            this.richest = pathOptions.getRichest();
        }
    }

    public List<Node> getShortest() {
        return shortest;
    }

    public List<Node> getRichest() {
        return richest;
    }

    public List<List<Node>> getPaths() {
        return paths;
    }

    public void addPath(List<Node> path) {
        if (getPathWeight(path) > maxWeight) {
            return;
        }

        if (shortest == null || getPathWeight(path) < getPathWeight(shortest)) {
            shortest = path;
        }
        if (richest == null || getAllGoldForNodes(path) > getPathWeight(richest)) {
            richest = path;
        }

        paths.add(path);
    }

    public Integer getShortestWeight() {
        return getPathWeight(shortest);
    }

    private int getAllGoldForNodes(List<Node> path) {
        return path.stream().mapToInt(node -> node.getTile().getGold()).sum();
    }

    private Integer getPathWeight(List<Node> path) {
        if (path == null) {
            return null;
        }
        int sum = 0;
        for (int i = 1; i < path.size(); i++) {
            Node prev = path.get(i - 1);
            sum += path.get(i).getEdge(prev).length();
        }
        return sum;
    }
}
