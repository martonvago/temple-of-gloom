package student.escape;

import game.EscapeState;
import student.dijkstra.DijkstraAlgorithm;

import java.util.List;

/**
 * EscapeAlgorithm calculates the shortest path from the current node to the exit and enhances it to traverse
 * more tiles with gold. Finally, it walks the optimised path and collects gold on the way.
 */
public class EscapeAlgorithm {
    /**
     * The information available at the current state
     */
    private final EscapeState state;

    /**
     * Construct an algorithm with the current state
     * @param escapeState the state
     */
    public EscapeAlgorithm(EscapeState escapeState) {
        state = escapeState;
    }

    /**
     * Find and walk a path to the exit node which can be walked in the remaining time and contains as much gold
     * as possible.
     */
    public void escape() {
        EscapePath path = findShortestEscapePath();
        path.optimise(state.getTimeRemaining());

        pickUpGoldIfAny();
        path.getNodes().stream().skip(1).forEach(node -> {
            state.moveTo(node.getDelegate());
            pickUpGoldIfAny();
        });
    }

    /**
     * Find the shortest path from the starting node to the exit node using Dijkstra's algorithm.
     *
     * @return the shortest path
     */
    private EscapePath findShortestEscapePath() {
        long exitId = state.getExit().getId();
        EscapeNode start = new EscapeNode(state.getCurrentNode());
        List<EscapeNode> escapeNodes = new DijkstraAlgorithm<>(start, exitId)
                .findShortestPath()
                .get(exitId)
                .getNodes();

        return new EscapePath(escapeNodes);
    }

    /**
     * Pick up gold from the current node if the node contains gold.
     */
    private void pickUpGoldIfAny() {
        if (state.getCurrentNode().getTile().getGold() > 0) {
            state.pickUpGold();
        }
    }
}
