//package student;
//
//import game.GameState;
//import game.Node;
//import game.Pair;
//
//import java.util.*;
//
//public class Graph {
//    private final Map<Long, NodeRecord> idToNode = new HashMap<>();
//
//
//    private Path pathToClosest;
//    private Set<Node> visitedNodes = new HashSet<>();
//    private Set<Node> candidateNodes = new HashSet<>();
//    private Map<Long, Path> pathMap = new HashMap<>();
//    /**
//     * For Escape()
//     * @param game the information available at the current state
//     */
//    public Graph(GameState game) {
//        // add the nodes
//        for (Node n : game.getVertices()) {
//           var nr = NodeRecord.FromNode(n);
//           idToNode.put(nr.id(), nr);
//        }
//    }
//
//    /**
//     * For Explore()
//     */
//    public Graph() {}
//
//    /**
//     * Find the shortest path from the starting node to the exit node using Dijkstra's algorithm.
//     *
//     * @return the shortest path
//     */
//    public Path FindShortestPathDijkstra(Node start, Node exit) {
//
//        // NodeRecord:
//        // id() -- Long
//        // gold() -- int
//        // edges() -- ArrayList<Pair<Long, Int>>
//
//        // Existing:
//        // Node
//        // id() -- Long
//        // edges Set<Edge>
//        //
//
//        // map[]
//        // start -> outwards
//        // map[node] = path (this path is the shortest path)
//
//        pathMap.put(start.getId(), new Path(start));
//        while (!visitedNodes.contains(exit)) {
//            // TODO: should let the algorithm complete, first exit may not be the shortest path
//            Node closestCandidate = GetClosestCandidate(start);
//            pathToClosest = pathMap.get(closestCandidate.getId());
//
//            // Loop through the all the neighbours of the closest candidate
//            closestCandidate.getNeighbours().stream()
//                    // Filter out already visited nodes
//                    .filter(neighbour -> !visitedNodes.contains(neighbour))
//                    .forEach(this::UpdateShortestPath);
//            // Removed current candidate node
//            candidateNodes.remove(closestCandidate);
//            // Track the next candidate
//            visitedNodes.add(closestCandidate);
//        }
//
//        return pathMap.get(exit.getId());
//    }
//
//    private void UpdateShortestPath(Node neighbour) {
//        Path oldPathToNeighbour = pathMap.get(neighbour.getId());
//        Path newPathToNeighbour = pathToClosest.cloneWithNode(neighbour);
//
//        // Select Path with the largest score (weight, and gold count) and put it on the path map
//        if (oldPathToNeighbour == null || newPathToNeighbour.compareTo(oldPathToNeighbour) < 0) {
//            pathMap.put(neighbour.getId(), newPathToNeighbour);
//        }
//
//        // Add the next candidate nodes
//        candidateNodes.add(neighbour);
//    }
//
//    private Node GetClosestCandidate(Node start) {
//        return candidateNodes.stream()
//                .min(Comparator.comparing((candidate -> pathMap.get(candidate.getId()))))
//                .orElse(start);
//    }
//
//    public boolean ContainsID(Long id) {
//        return idToNode.containsKey(id);
//    }
//
//    public void AddNode(NodeRecord n) {
//        idToNode.putIfAbsent(n.id(), n);
//    }
//}