package student;

import com.sun.jdi.event.BreakpointEvent;
import game.ExplorationState;

public class ExploreAlgorithm {

    private static final Integer TURNAROUND_THRESHOLD = 6;

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
        var closest = g.getClosestUnexploredNodeToGoal();
        var path = g.ShortestPathTo(state.getCurrentLocation(), closest.getNodeID());
        System.out.println("path back " + path);
        for (var node : path){
            state.moveTo(node.getNodeID());
            g.logNodeVisit(state.getCurrentLocation(), state.getDistanceToTarget(), state.getNeighbours() );

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


// Fallback / giving up
// Pathfind to least distance unexplored node

// End of session TODO:
// Where we are at: We messed around with too many things we shouldn't have
// We need to finish the ExploreGraph and ExploreAlgorithm
// We were currently working on this function, and implementing ExploreGraph as we went along
//
// 1. Working on KeepExploring -- Need to move it over here, maybe cache GetNeighbours()? / calculate closest
// - Needs logic to determine if we should go back to a known close node or not
// Logic for getclosetneighbour

// After it's working again:

// Extract out PathAlgorithm
// Investigate usage of Node class (allowed or not?)



// Look at neighbours
// -> Still unexplored
// -> Closer -> Move to it
// -> Further away -> ?
// -> No unexplored tiles adjacent
// -> Find lowest distance unexplored node in graph -> pathfind to it

// Update current node if dead end // obsolete?


// Perform move




//            explored.put(current, weight);
//
//            var edges = new ArrayList<Pair<Long, Integer>>();
//            for (var node : state.getNeighbours()) {
//                if (g.ContainsID(node.nodeID())) {
//                    continue;
//                }
//
//                edges.add(new Pair<>(node.nodeID(), 1));
//            }
//            g.AddNode(new NodeRecord(current, 0, edges));



//        explored.computeIfPresent(state.getCurrentLocation(), (id, n) -> n + weight);
//            explored.putIfAbsent(state.getCurrentLocation(), weight);
////            System.out.format("At %d - own weight %d - distance %d%n", state.getCurrentLocation(), explored.get(state.getCurrentLocation()), state.getDistanceToTarget());
//            var neighbours = state.getNeighbours();
//            var nicest = neighbours.stream()
//                    .min(Comparator.comparingInt(n -> n.distanceToTarget() + explored.getOrDefault(n.nodeID(), 0)));
////            System.out.println("Found nicest: " + nicest);
//            state.moveTo(nicest.orElse(neighbours.stream().max(Comparator.comparingLong(NodeStatus::nodeID)).get()).nodeID());
//        }
//    }


