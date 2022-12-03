package student;

import java.util.*;

public class ExploreGraph {

    private Map<Long, TreeSet<Long>> neighbourMap = new HashMap<>();
    // Tracks the nodes we've visited to deprioritize traversing them
    private Set<Long> visited = new HashSet<>();
    private Map<Long, Integer> distanceMap = new HashMap<>();


    public void Visit(long current, int distance) {
        visited.add(current);
    }

    public void Seen(long currentID, long nodeID, int distanceToTarget) {
        neighbourMap.compute(currentID, (aLong, longs) -> {
            if (longs == null) {
                longs = new TreeSet<>();
            }
            longs.add(nodeID);
            return longs;
        });
        distanceMap.putIfAbsent(nodeID, distanceToTarget);
    }

    public Set<Long> GetNeighbours(long current) {
        return neighbourMap.get(current); // TODO: return distances as well
    }

    // TODO: Move to exploreAlgo
    public boolean KeepExploring(long current) {
        // Return true if we should keep trying to find a path through the adjacent nodes to the current node
        var currentDistance = distanceMap.get(current);

        // Current lowest unexplored distance

        var neighbours = GetNeighbours(current);
        for (var n : neighbours) {
            if (!visited.contains(n)) {

                return true;
            }
        }
        return false;
    }
    /**
     * Finds a NodeID that has the lowest distance that's unvisted
     * @return Long
     */
    public long GetLowestUnexploredDistance() {
        return distanceMap.entrySet().stream()
                .sorted(Comparator.comparingInt(Map.Entry::getValue))
                .filter((k) -> visited.contains(k.getKey()))
                .findFirst()
                .get().getKey();
    }

}
