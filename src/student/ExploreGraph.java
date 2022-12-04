package student;

import game.NodeStatus;
import game.Pair;

import java.util.*;


public class ExploreGraph {

    private final Map<Long, TreeSet<Long>> neighbourMap = new HashMap<>();
    // Tracks the nodes we've visited to deprioritize traversing them
    private final Set<Long> visited = new HashSet<>();
    private final Map<Long, Integer> distanceMap = new HashMap<>();
    private Pair<Long, Integer> closestUnexploredNode;
    private Stack<Long> movesTaken = new Stack<>();

    public void logNodeVisit(long current, int distance, Collection<NodeStatus> neighbours) {
        if (!movesTaken.empty()) {
            if (movesTaken.peek() == current) {
                System.out.println("WARING: double log");
                return;
            }
        }
        visited.add(current);
        distanceMap.putIfAbsent(current, distance);
        movesTaken.push(current);

        for (var n : neighbours) {
            seen(current, n.nodeID(), n.distanceToTarget());
        }
        System.out.println("vistited " + current);
    }

    private void seen(long currentID, long nodeID, int distanceToTarget) {
        neighbourMap.compute(currentID, (aLong, longs) -> {
            if (longs == null) {
                longs = new TreeSet<>();
            }
            longs.add(nodeID);
            return longs;
        });
        distanceMap.putIfAbsent(nodeID, distanceToTarget);
    }

    public List<Pair<Long, Integer>> getNeighbours(long current) {
        // Augment the list of neighbours with their distances,
        // then return them in sorted order, the lowest distance to highest, if there is a tie, the lowest tile id
        return neighbourMap.get(current).stream()
                .map((l) ->
                        new Pair<>(l, distanceMap.get(l))
                )
                .sorted(Comparator.comparingInt(
                                (Pair<Long, Integer> p) -> p.second())
                        .thenComparingLong(Pair::first)
                ).toList();
    }

    public List<Pair<Long, Integer>> getUnexploredNeighbours(long current) {
        return getNeighbours(current).stream().filter((k) -> !visited.contains(k.first())).toList();
    }

    public boolean hasNotVisited(long id) {
        return !visited.contains(id);
    }

    public int getDistance(long current) {
        return distanceMap.get(current);
    }

    /**
     * Finds a NodeID that has the lowest distance that's unvisted
     *
     * @return Long
     */
    public Pair<Long, Integer> getClosestUnexploredNodeToGoal() {
        // TODO: We'll want to store this somehow. A tree of sorts?
        var e = distanceMap.entrySet().stream()
                .sorted(Comparator.comparingInt(Map.Entry::getValue))
                .filter((k) -> !visited.contains(k.getKey()))
                .findFirst()
                .get();
        return new Pair<Long, Integer>(e.getKey(), e.getValue());
    }

    public List<Long> pathToClosestUnexploredFrom() {
        var target = getClosestUnexploredNodeToGoal();
        System.out.println("target: " + target.first());

        List<Long> Path = new ArrayList<>();
        var path_complete = false;

        while (!path_complete) {
            Long node = movesTaken.pop();
            var neighbours = neighbourMap.get(node);

//            System.out.println("popping node: " + node);
//            System.out.println("neighbours of node: " + neighbours);

            if (neighbours.contains(target.first())) {
                path_complete = true;
            }
            Path.add(node);
        }
        return Path;
    }
}
