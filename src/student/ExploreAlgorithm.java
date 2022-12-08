package student;

import game.ExplorationState;

public class ExploreAlgorithm {

    private static final Integer TURNAROUND_THRESHOLD = 4;

    private final ExplorationState state;
    private final ExploreGraph g = new ExploreGraph();

    public ExploreAlgorithm(ExplorationState state) {
        this.state = state;
    }

    public void explore() {
        while (state.getDistanceToTarget() != 0) {
            g.logNodeVisit(state.getCurrentLocation(), state.getDistanceToTarget(), state.getNeighbours());
            if (keepExploring()) {
                var nextMove = g.getUnexploredNeighbours(state.getCurrentLocation()).get(0).getNodeID();
                state.moveTo(nextMove);
            } else {
                moveToLastKnownGoodNode();
            }
        }
    }

    private void moveToLastKnownGoodNode() {

        System.out.println("moving back");
        // Make sure to move to the unseen tile and log it
        var path = g.getPathToBestNode(state.getCurrentLocation());
        System.out.println("path back " + path);
        for (var node : path) {
            state.moveTo(node.getNodeID());
            g.logNodeVisit(state.getCurrentLocation(), state.getDistanceToTarget(), state.getNeighbours());

        }
    }

    private boolean keepExploring() {
        // Return true if we should keep trying to find a path through the adjacent nodes to the current node

        var neighbours = g.getUnexploredNeighbours(state.getCurrentLocation());
        if (neighbours.isEmpty()) {
            return false;
        }

        var target = neighbours.get(0);
        var closest = g.getClosestUnexploredNodeToGoal();
        return target.getDistanceToTarget() <= closest.getDistanceToTarget() + TURNAROUND_THRESHOLD;
    }
}


