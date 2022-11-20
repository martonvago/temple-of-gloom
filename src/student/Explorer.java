package student;

import game.*;

import java.util.*;
import java.util.stream.IntStream;

public class Explorer {
    /**
     * Explore the cavern, trying to find the orb in as few steps as possible.
     * Once you find the orb, you must return from the function in order to pick
     * it up. If you continue to move after finding the orb rather
     * than returning, it will not count.
     * If you return from this function while not standing on top of the orb,
     * it will count as a failure.
     * <p>
     * There is no limit to how many steps you can take, but you will receive
     * a score bonus multiplier for finding the orb in fewer steps.
     * <p>
     * At every step, you only know your current tile's ID and the ID of all
     * open neighbor tiles, as well as the distance to the orb at each of these tiles
     * (ignoring walls and obstacles).
     * <p>
     * To get information about the current state, use functions
     * getCurrentLocation(),
     * getNeighbours(), and
     * getDistanceToTarget()
     * in ExplorationState.
     * You know you are standing on the orb when getDistanceToTarget() is 0.
     * <p>
     * Use function moveTo(long id) in ExplorationState to move to a neighboring
     * tile by its ID. Doing this will change state to reflect your new position.
     * <p>
     * A suggested first implementation that will always find the orb, but likely won't
     * receive a large bonus multiplier, is a depth-first search.
     *
     * @param state the information available at the current state
     */
    public void explore(ExplorationState state) {
        Stack<Long> moves = new Stack<>();
        List<Long> deadEnds = new ArrayList<>();

        while (state.getDistanceToTarget() != 0) {
            moves.push(state.getCurrentLocation());
            Optional<NodeStatus> unexploredNeighbourClosestToOrb = state.getNeighbours()
                    .stream()
                    .filter(nodeStatus -> !moves.contains(nodeStatus.nodeID()) && !deadEnds.contains(nodeStatus.nodeID()))
                    .min(Comparator.comparingInt(NodeStatus::distanceToTarget));
            if (unexploredNeighbourClosestToOrb.isEmpty()) {
                deadEnds.add(moves.pop());
                state.moveTo(moves.pop());
                continue;
            }
            state.moveTo(unexploredNeighbourClosestToOrb.get().nodeID());
        }
    }

    /**
     * Escape from the cavern before the ceiling collapses, trying to collect as much
     * gold as possible along the way. Your solution must ALWAYS escape before time runs
     * out, and this should be prioritized above collecting gold.
     * <p>
     * You now have access to the entire underlying graph, which can be accessed through EscapeState.
     * getCurrentNode() and getExit() will return you Node objects of interest, and getVertices()
     * will return a collection of all nodes on the graph.
     * <p>
     * Note that time is measured entirely in the number of steps taken, and for each step
     * the time remaining is decremented by the weight of the edge taken. You can use
     * getTimeRemaining() to get the time still remaining, pickUpGold() to pick up any gold
     * on your current tile (this will fail if no such gold exists), and moveTo() to move
     * to a destination node adjacent to your current node.
     * <p>
     * You must return from this function while standing at the exit. Failing to do so before time
     * runs out or returning from the wrong location will be considered a failed run.
     * <p>
     * You will always have enough time to escape using the shortest path from the starting
     * position to the exit, although this will not collect much gold.
     *
     * @param state the information available at the current state
     */
    public void escape(EscapeState state) {
        int maxWeight = state.getTimeRemaining();
        Path path = findShortestPath(state);

        int unusedBudget = maxWeight - path.getWeight();
        int oldSize = path.getSize();
        while (unusedBudget > 0) {
            path.enhancePath(maxWeight);
            path.removeGoldlessLoops();
            if (oldSize >= path.getSize()) {
                break;
            }
            unusedBudget = maxWeight - path.getWeight();
            oldSize = path.getSize();
        }

        pickUpGoldIfAny(state);
        path.getNodes().stream().skip(1).forEach(node -> {
            state.moveTo(node);
            pickUpGoldIfAny(state);
        });
    }

    private Path findShortestPath(EscapeState state) {
        Node start = state.getCurrentNode();
        Node exit = state.getExit();
        Set<Node> visitedNodes = new HashSet<>();
        Set<Node> candidateNodes = new HashSet<>();
        Map<Long, Path> pathMap = new HashMap<>();

        pathMap.put(start.getId(), new Path(start));
        while (!visitedNodes.contains(exit)) {
            Node closestCandidate = candidateNodes.stream()
                    .min(Comparator.comparing((candidate-> pathMap.get(candidate.getId()))))
                    .orElse(start);
            Path pathToClosest = pathMap.get(closestCandidate.getId());
            closestCandidate.getNeighbours().stream()
                    .filter(neighbour -> !visitedNodes.contains(neighbour))
                    .forEach(neighbour -> {
                        Path oldPathToNeighbour = pathMap.get(neighbour.getId());
                        Path newPathToNeighbour = pathToClosest.cloneWithNode(neighbour);
                        if (oldPathToNeighbour == null || newPathToNeighbour.compareTo(oldPathToNeighbour) < 0) {
                            pathMap.put(neighbour.getId(), newPathToNeighbour);
                        }
                        candidateNodes.add(neighbour);
                    });
            candidateNodes.remove(closestCandidate);
            visitedNodes.add(closestCandidate);
        }

        return pathMap.get(state.getExit().getId());
    }

    private void pickUpGoldIfAny(EscapeState state) {
        if (state.getCurrentNode().getTile().getGold() > 0) {
            state.pickUpGold();
        }
    }
}
