package student;

import java.util.ArrayList;
import java.util.List;

public class PathOptions {
    private final Integer maxWeight;
    private Path shortest = null;
    private Path richest = null;
    private List<Path> paths = new ArrayList<>();

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

    public Path getShortest() {
        return shortest;
    }

    public Path getRichest() {
        return richest;
    }

    public List<Path> getPaths() {
        return paths;
    }

    public void addPath(Path path) {
        if (path.getWeight() > maxWeight) {
            return;
        }

        if (shortest == null || path.compareTo(shortest) < 0) {
            shortest = path;
        }
        if (richest == null || path.getGold() > richest.getGold()) {
            richest = path;
        }

        paths.add(path);
    }
}
