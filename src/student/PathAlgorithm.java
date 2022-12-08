package student;

import game.EscapeState;
import game.Node;

import java.util.*;

public class PathAlgorithm {
    private final Node start;
    private final Node exit;
    private final Set<Node> visitedNodes;
    private final Set<Node> candidateNodes;
    private final Map<Long, Path> pathMap;
    private Path pathToClosest;

    /**
     * @param state the information available at the current state
     */
    public PathAlgorithm(EscapeState state) {
        this(state.getCurrentNode(), state.getExit());
    }

    public PathAlgorithm(Node start, Node exit) {
        this.start = start;
        this.exit = exit;
        visitedNodes = new HashSet<>();
        candidateNodes = new HashSet<>();
        pathMap = new HashMap<>();
    }

    /**
     * Find the shortest path from the starting node to the exit node using Dijkstra's algorithm.
     *
     * @return the shortest path
     */
    public Path findShortestPathDijkstra() {

        pathMap.put(start.getId(), new Path(start));
        while (!visitedNodes.contains(exit)) { // TODO: Should not exist when it has the last node -- could be other path to last node that is shorter

            Node closestCandidate = GetClosestCandidate();
            pathToClosest = pathMap.get(closestCandidate.getId());

            // Loop through the all the neighbours of the closest candidate
            closestCandidate.getNeighbours().stream()
                    // Filter out already visited nodes
                    .filter(neighbour -> !visitedNodes.contains(neighbour))
                    .forEach(this::UpdateShortestPath);
            // Removed current candidate node
            candidateNodes.remove(closestCandidate);
            // Track the next candidate
            visitedNodes.add(closestCandidate);
        }

        return pathMap.get(exit.getId());
    }

    private void UpdateShortestPath(Node neighbour) {

        Path oldPathToNeighbour = pathMap.get(neighbour.getId());
        Path newPathToNeighbour = pathToClosest.cloneWithNode(neighbour);

        // Select Path with the largest score (weight, and gold count) and put it on the path map
        if (oldPathToNeighbour == null || newPathToNeighbour.compareTo(oldPathToNeighbour) < 0) {
            pathMap.put(neighbour.getId(), newPathToNeighbour);
        }

        // Add the next candidate nodes
        candidateNodes.add(neighbour);

    }

    private Node GetClosestCandidate() {

        return candidateNodes.stream()
                .min(Comparator.comparing((candidate -> pathMap.get(candidate.getId()))))
                .orElse(start);

    }

}
