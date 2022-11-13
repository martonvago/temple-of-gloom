package student;

import game.EscapeState;
import game.ExplorationState;
import game.Node;
import game.NodeStatus;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Explorer {
    private List<Node> bestEscapePath = new ArrayList<>();

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
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);
        Runnable findBestPath = () -> step(
                state.getCurrentNode(),
                new ArrayList<>(),
                state.getExit(),
                state.getTimeRemaining()
        );
        Future<?> futureFindBestPath = executor.submit(findBestPath);
        Runnable cancelSearch = () -> futureFindBestPath.cancel(true);
        executor.schedule(cancelSearch, 10, TimeUnit.SECONDS);
        executor.shutdown();

        try {
            futureFindBestPath.get();
        } catch (Exception ignored) {
            // Search time limit exceeded
        }

        pickUpGoldIfAny(state);
        bestEscapePath.stream()
                .skip(1)
                .forEach(node -> {
                    state.moveTo(node);
                    pickUpGoldIfAny(state);
                });
    }

    private void pickUpGoldIfAny(EscapeState state) {
        if (state.getCurrentNode().getTile().getGold() > 0) {
            state.pickUpGold();
        }
    }

    private void step(Node next, List<Node> moves, Node exit, int maxWeight) {
        if (Thread.currentThread().isInterrupted()) {
            return;
        }

        Node prev = moves.size() > 0 ? moves.get(moves.size() - 1) : null;
        moves.add(next);

        if (getTotalWeightForNodes(moves) > maxWeight) {
            return;
        }

        if (next.equals(exit)) {
            if (isNewPathBetter(moves, bestEscapePath)) {
                bestEscapePath = moves;
                return;
            }
        }

        next.getNeighbours().stream()
                .filter(neighbour -> !neighbour.equals(prev))
                .forEach(neighbour -> step(neighbour, new ArrayList<>(moves), exit, maxWeight));
    }

    private boolean isNewPathBetter(List<Node> newPath, List<Node> bestPath) {
        if (bestPath.isEmpty()) {
            return true;
        }

        if (getAllGoldForNodes(newPath) > getAllGoldForNodes(bestPath)) {
            return true;
        }
        if (getAllGoldForNodes(newPath) < getAllGoldForNodes(bestPath)) {
            return false;
        }
        return getTotalWeightForNodes(newPath) < getTotalWeightForNodes(bestPath);
    }

    private int getTotalWeightForNodes(List<Node> path) {
        int sum = 0;
        for (int i = 1; i < path.size(); i++) {
            Node prev = path.get(i - 1);
            sum += path.get(i).getEdge(prev).length();
        }
        return sum;
    }

    private int getAllGoldForNodes(List<Node> path) {
        return path.stream().mapToInt(node -> node.getTile().getGold()).sum();
    }
}
