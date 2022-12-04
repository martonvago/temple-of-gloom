package student;

import game.ExplorationState;

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
        g.logNodeVisit(state.getCurrentLocation(), state.getDistanceToTarget());

        while (state.getDistanceToTarget() != 0) {
            var current = state.getCurrentLocation();
            g.logNodeVisit(state.getCurrentLocation(), state.getDistanceToTarget());

            for (var n : state.getNeighbours()) {
                g.Seen(current, n.nodeID(), n.distanceToTarget());
            }

            if (this.KeepExploring(current)) {
                // Distance benefit whatever calculation
                state.moveTo(getClosestNeighbour(current));
                continue;
            } else {
                moveToLastKnownGoodNode(current);
            }
        }
    }

    private void moveToLastKnownGoodNode(long current) {
        // TODO: use some kind of path algorithm to move to the known good nodes

    }

    private boolean KeepExploring(long current) {
        // Return true if we should keep trying to find a path through the adjacent nodes to the current node

        // TODO: We should also track the lowest distance we have visited and if it goes higher than a threshold
        //  Return False

        var currentDistance = g.getDistance(current);

        // Current lowest unexplored distance
        var neighbours = g.GetNeighbours(current);
        for (var n : neighbours) {
            if (!g.hasVisited(n)) {
                return true;
            }
        }
        return false;
    }

    private long getClosestNeighbour(long current) {
        var neighbours = g.GetNeighbours(current);
        Map<Long, Integer> distanceMap = new HashMap<>();

        // Create the local distance map
        for(var n: neighbours) {
            if (!g.hasVisited(n)) {
                distanceMap.put(n, g.getDistance(n));
            }
        }

        // sort the distance map
        ArrayList<Map.Entry<Long, Integer>> sortedlist = new ArrayList<>(distanceMap.entrySet());
        sortedlist.sort(Map.Entry.comparingByValue());

        // Select entry with the lowest distance to orb
        return sortedlist.get(sortedlist.size() -1).getKey();

        // TODO: Check for entries with same distance, if present select one with the highest ID

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
