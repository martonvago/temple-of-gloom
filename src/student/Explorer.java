package student;

import game.*;

import java.util.*;

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
        Path path = new PathAlgorithm(state).findShortestPathDijkstra();

        int unusedBudget = maxWeight - path.getWeight();
        int oldSize = path.getSize();

        // Enhance and trim the path until the budget is spent or the path stops growing
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

    /**
     * Pick up gold from the current node if the node contains gold.
     *
     * @param state the information available at the current state
     */
    private void pickUpGoldIfAny(EscapeState state) {
        if (state.getCurrentNode().getTile().getGold() > 0) {
            state.pickUpGold();
        }
    }

}

class ExploreAlgorithm{

    private final ExplorationState state;
    private int weight;
    private HashMap<Long, Integer> explored;

    public ExploreAlgorithm(ExplorationState state){
        this.state = state;
        this.explored = new HashMap<>();
        this.weight = 5;
    }

    public void explore(){

        // Never return to the entrance tile
        explored.put(state.getCurrentLocation(), Integer.MAX_VALUE);

        while (state.getDistanceToTarget() != 0) {

            explored.compute(state.getCurrentLocation(), (id, n) -> n == null ? effort : n + weight);

            var neighbours = state.getNeighbours();
            var nicest = neighbours.stream()
                    .min(Comparator.comparingInt(n -> n.distanceToTarget() + explored.compute(n.nodeID(), (id, m) -> m == null ? 0 : m)));
            state.moveTo(nicest.orElse(neighbours.stream().max(Comparator.comparingLong(n -> n.nodeID())).get()).nodeID());
        }
    }

}


class PathAlgorithm {
    private final Node start;
    private final Node exit;
    private final Set<Node> visitedNodes;
    private final Set<Node> candidateNodes;
    private final Map<Long, Path> pathMap;
    private Path pathToClosest;

    /**
     * @param state the information available at the current state
     */
    public PathAlgorithm(EscapeState state){
        this(state.getCurrentNode(), state.getExit());
    }

    public PathAlgorithm(Node start, Node exit){
        this.start = start;
        this.exit = exit;
        visitedNodes = new HashSet<>();
        candidateNodes = new HashSet<>();
        pathMap = new HashMap<>();
    }

    /**
     * Find the shortest path from the starting node to the exit node using Dijkstra's algorithm.
     * @return the shortest path
     */
    public Path findShortestPathDijkstra() {

        pathMap.put(start.getId(), new Path(start));
        while (!visitedNodes.contains(exit)) {

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

    private void UpdateShortestPath(Node neighbour){

        Path oldPathToNeighbour = pathMap.get(neighbour.getId());
        Path newPathToNeighbour = pathToClosest.cloneWithNode(neighbour);

        // Select Path with the largest score (weight, and gold count) and put it on the path map
        if (oldPathToNeighbour == null || newPathToNeighbour.compareTo(oldPathToNeighbour) < 0) {
            pathMap.put(neighbour.getId(), newPathToNeighbour);
        }

        // Add the next candidate nodes
        candidateNodes.add(neighbour);

    }

    private Node GetClosestCandidate(){

        return candidateNodes.stream()
                .min(Comparator.comparing((candidate-> pathMap.get(candidate.getId()))))
                .orElse(start);

    }

}