package student;

import game.ExplorationState;
import game.Node;
import game.NodeStatus;
import game.Pair;

import java.util.*;

public class ExploreAlgorithm {

    private final ExplorationState state;
    private final ExploreGraph g = new ExploreGraph();

    public ExploreAlgorithm(ExplorationState state) {
        this.state = state;
    }

    public void explore() {
        // graph:
        // - nodes, their distance (NodeStatus) + Is it explored? (true if explored(n) > 0)
        g.SetDistance(state.getCurrentLocation(), state.getDistanceToTarget());

        while (state.getDistanceToTarget() != 0) {
            var current = state.getCurrentLocation();
            var dist = state.getDistanceToTarget();
            g.Visit(current, dist);

            for (var n : state.getNeighbours()) {
                g.Seen(current, n.nodeID(), n.distanceToTarget());
            }

            if (g.KeepExploring(current)) {
                // Distance benefit whatever calculation
                state.moveTo(g.GetClosestNeighbour(current));

                continue;
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



        }
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

    }

}
