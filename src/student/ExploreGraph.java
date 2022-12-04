package student;

import game.NodeStatus;
import game.Pair;

import java.util.*;
import java.util.stream.Stream;

public class ExploreGraph {

    private final Map<Long, TreeSet<Long>> neighbourMap = new HashMap<>();
    // Tracks the nodes we've visited to deprioritize traversing them
    private final Set<Long> visited = new HashSet<>();
    private final Map<Long, Integer> distanceMap = new HashMap<>();
    private Pair<Long, Integer> closestUnexploredNode;

    public void logNodeVisit(long current, int distance, Collection<NodeStatus> neighbours) {
        visited.add(current);
        distanceMap.putIfAbsent(current, distance);

        for (var n : neighbours) {
            seen(current, n.nodeID(), n.distanceToTarget());
        }
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

    public List<Long> pathToClosestUnexploredFrom(long current) {
        var end = getClosestUnexploredNodeToGoal().first();
        Set<Long> been = new HashSet<>();
        List<List<Long>> paths = new ArrayList<>();
        var added = true;

        while (added) {
            added = false;
            var tmp = new ArrayList<>();

            for (var path : paths) {
                var neighbours = getNeighbours(path.get(path.size()-1));
                for (var n : neighbours) {
                    long nId = n.first();
                    if (been.contains(nId)) continue;
                    been.add(nId);
                    added = true;

                    List<Long> newPath = Stream.concat(path.stream(), Stream.of(nId)).toList();
                    if (nId == end) {
                        return newPath;
                    }
                    tmp.add(newPath);
                }
            }
        }
        return null;
        // we have node X
        // if a neighbour is end -> return path
        // for each neighbour
        // repeat

//        while
//
//
//
//
//        var current = start;
//        var neighbours = getNeighbours(current);

    }

}
// 1  procedure BFS(G, root) is
//         2      let Q be a queue
//         3      label root as explored
//         4      Q.enqueue(root)
//         5      while Q is not empty do
//         6          v := Q.dequeue()
//         7          if v is the goal then
//         8              return v
//         9          for all edges from v to w in G.adjacentEdges(v) do
//         10              if w is not labeled as explored then
//         11                  label w as explored
//         12                  w.parent := v
//         13                  Q.enqueue(w)