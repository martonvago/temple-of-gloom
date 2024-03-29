package student.explore;

import game.NodeStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.*;

import org.mockito.internal.util.reflection.FieldReader;
import student.explore.ExploreGraph;
import student.explore.ExploreNode;

public class ExploreGraphTest {

    @Test
    void test_log_node_visit_no_neighbours() throws NoSuchFieldException {

        long nodeId = 56;
        int nodeDistance = 24;

        // Log a node visit with no neighbours
        ExploreGraph graph = new ExploreGraph();
        Collection<NodeStatus> noNeighbours = new ArrayList<>();
        graph.logNodeVisit(nodeId, nodeDistance, noNeighbours);

        Field nodeMapField = ExploreGraph.class.getDeclaredField("nodeMap");
        var nodeMap = (Map<Long, ExploreNode>) new FieldReader(graph, nodeMapField).read();

        // Assert that the internal node map has 1 entry
        var expected = new HashSet<>(List.of(nodeId));
        Assertions.assertEquals(expected, nodeMap.keySet());

        // Assert entry is marked and visited and has the correct distance value
        var node = nodeMap.get(nodeId);
        Assertions.assertEquals(nodeDistance, node.getDistanceToTarget());

    }


    @Test
    void test_log_node_visit_with_new_neighbours() throws NoSuchFieldException {

        // Log a node visit with 2 neighbours
        long nodeId = 56, nodeAId = 57, nodeBId = 58;
        int nodeDistance = 24, nodeADistance = 23, nodeBDistance = 23;

        var nodeA = new NodeStatus(nodeAId, nodeADistance);
        var nodeB = new NodeStatus(nodeBId, nodeBDistance);
        var neighbours = new ArrayList<>(Arrays.asList(nodeA, nodeB));

        ExploreGraph graph = new ExploreGraph();
        graph.logNodeVisit(nodeId, nodeDistance, neighbours);

        Field nodeMapField = ExploreGraph.class.getDeclaredField("nodeMap");
        var nodeMap = (Map<Long, ExploreNode>) new FieldReader(graph, nodeMapField).read();

        // Assert that the internal node map has 3 entries
        var expected = new HashSet<>(List.of(nodeId, nodeAId, nodeBId));
        Assertions.assertEquals(expected, nodeMap.keySet());

        var parentNode = nodeMap.get(nodeId);
        var NodeA = nodeMap.get(nodeAId);
        var NodeB = nodeMap.get(nodeBId);

        // Assert that the neighbours have a neighbour entry with the visited node (bidirectional connection)
        Assertions.assertEquals(parentNode, NodeA.getNeighbours().toArray()[0]);
        Assertions.assertEquals(parentNode, NodeB.getNeighbours().toArray()[0]);

        // Assert that neighbours are not visited
        Assertions.assertFalse(NodeA.getVisited());
        Assertions.assertFalse(NodeB.getVisited());

        // Assert All three entries have correct distances
        Assertions.assertEquals(nodeADistance, NodeA.getDistanceToTarget());
        Assertions.assertEquals(nodeBDistance, NodeB.getDistanceToTarget());


    }

    @Test
    void test_log_node_visit_node_already_exist() throws NoSuchFieldException {

        // Assert that A now has the visited flag

        long nodeId = 56, nodeAId = 57;
        int nodeDistance = 24, nodeADistance = 23;

        var nodeA = new NodeStatus(nodeAId, nodeADistance);
        var neighbours = new ArrayList<>(List.of(nodeA));
        ExploreGraph graph = new ExploreGraph();

        // Log node visit with neighbour A
        graph.logNodeVisit(nodeId, nodeDistance, neighbours);


        Field nodeMapField = ExploreGraph.class.getDeclaredField("nodeMap");
        var nodeMap = (Map<Long, ExploreNode>) new FieldReader(graph, nodeMapField).read();

        // Visit A next
        var OriginalNodeHash = nodeMap.get(nodeAId).hashCode();
        graph.logNodeVisit(nodeAId, nodeADistance, new ArrayList<>());

        // Assert that Node A hasn't been re-created
        Assertions.assertEquals(nodeMap.get(nodeAId).hashCode(), OriginalNodeHash);

        // Assert that A now has the visited flag
        Assertions.assertTrue(nodeMap.get(nodeAId).getVisited());

    }

    @Test
    void test_get_unexplored_neighbours() throws NoSuchFieldException {

        // Log a visit with neighbours A and B
        // Node A is 20 units from the target and B is 22
        // Get the unexplored neighbours
        // Assert that the list is in the order of [A, B]

        // Log a node visit with 2 neighbours
        long nodeId = 56, nodeAId = 57, nodeBId = 58;
        int nodeDistance = 24, nodeADistance = 20, nodeBDistance = 22;

        NodeStatus nodeA = new NodeStatus(nodeAId, nodeADistance), nodeB = new NodeStatus(nodeBId, nodeBDistance);
        var neighbours = new ArrayList<>(Arrays.asList(nodeA, nodeB));

        ExploreGraph graph = new ExploreGraph();
        Field nodeMapField = ExploreGraph.class.getDeclaredField("nodeMap");
        var nodeMap = (Map<Long, ExploreNode>) new FieldReader(graph, nodeMapField).read();
        graph.logNodeVisit(nodeId, nodeDistance, neighbours);

        var expected = new ArrayList<>(Arrays.asList(nodeMap.get(nodeAId), nodeMap.get(nodeBId)));
        var unexploredNeighbours = graph.getUnexploredNeighbours(nodeId);

        Assertions.assertEquals(unexploredNeighbours, expected);

    }

    @Test
    void test_list_unvisited_nodes_sorted() {

        // Create a graph with the following topography
        // E    F
        // |    |
        // A -- B       The Distances are as follows E=21, F=22, I=26 j=27
        // |    |
        // I    J

        // The visited nodes are A,B

        long nodeAId = 56, nodeBId = 57, nodeEId = 58, nodeFId = 59, nodeIId = 60, nodeJId = 61;
        int ADistance = 25, BDistance = 25, EDistance = 21, FDistance = 22, IDistance = 26, JDistance = 27;

        NodeStatus nodeA = new NodeStatus(nodeAId, ADistance), nodeB = new NodeStatus(nodeBId, BDistance),
                   nodeE = new NodeStatus(nodeEId, EDistance), nodeF = new NodeStatus(nodeFId, FDistance),
                   nodeI = new NodeStatus(nodeIId, IDistance), nodeJ = new NodeStatus(nodeJId, JDistance);

        var ANeighbours = new ArrayList<>(Arrays.asList(nodeE, nodeI, nodeA));
        var BNeighbours = new ArrayList<>(Arrays.asList(nodeF, nodeJ, nodeB));

        var graph = new ExploreGraph();

        graph.logNodeVisit(nodeAId, ADistance, ANeighbours);
        graph.logNodeVisit(nodeBId, BDistance, BNeighbours);


        // List the unvisited nodes, NOTE: the map is used because its easier to match the ID
        var nodes = graph.listUnVisitedNodesSorted().stream().map(ExploreNode::getId).toList();

        // Assert they are in the order of distance
        var expectedNodes = new ArrayList<>(Arrays.asList(nodeEId, nodeFId, nodeIId, nodeJId));
        Assertions.assertEquals(expectedNodes, nodes);


    }


    @Test
    void test_get_closest_unexplored() {

        // Create a graph with the following topography
        // E    F
        // |    |
        // A -- B
        // |    |
        // I    J
        // E is the closest unexplored node
        // Assert that E is returned

        long nodeAId = 56, nodeBId = 57, nodeEId = 58, nodeFId = 59, nodeIId = 60, nodeJId = 61;
        int ADistance = 25, BDistance = 25, EDistance = 21, FDistance = 22, IDistance = 26, JDistance = 27;

        NodeStatus nodeA = new NodeStatus(nodeAId, ADistance), nodeB = new NodeStatus(nodeBId, BDistance),
                nodeE = new NodeStatus(nodeEId, EDistance), nodeF = new NodeStatus(nodeFId, FDistance),
                nodeI = new NodeStatus(nodeIId, IDistance), nodeJ = new NodeStatus(nodeJId, JDistance);

        var ANeighbours = new ArrayList<>(Arrays.asList(nodeE, nodeI, nodeA));
        var BNeighbours = new ArrayList<>(Arrays.asList(nodeF, nodeJ, nodeB));

        var graph = new ExploreGraph();

        graph.logNodeVisit(nodeAId, ADistance, ANeighbours);
        graph.logNodeVisit(nodeBId, BDistance, BNeighbours);

        var node = graph.getClosestUnexploredNodeToGoal();

        Assertions.assertEquals(nodeEId, node.getId() );

    }

    @Test
    void test_get_path_to_best_node_similar_distance() throws NoSuchFieldException {

        // Create a graph with the following topography
        //  A         E
        //  |         |
        //  B -- C -- D -- F

        // E and A are unexplored, B, C , D, F are
        // the explorer is located on node F
        // A is closer to the target but F isn't that much further
        // Assert that the best node selected is E as its closer.

        var graph = createTestGraphSimilar();
        // node IDs are the following A = 56, B = 57, C = 58, D = 59, E = 60, F = 62
        var path = graph.getPathToBestNode(62);

        var expected = new ArrayList<>(List.of(62L, 59L, 60L));
        var node_id_path = path.getNodes().stream().map(ExploreNode::getId).toList();
        Assertions.assertEquals(expected, node_id_path);

    }

    @Test
    void test_get_path_to_best_node() throws NoSuchFieldException {

        // Create a graph with the following topography
        //  A         E
        //  |         |
        //  B -- C -- D -- F

        // E and A are unexplored, B, C , D, F are
        // the explorer is located on node F
        // A is the closest node by far
        // Assert that the best node selected is F as its closer.

        var graph = createTestGraph();
        // node IDs are the following A = 56, B = 57, C = 58, D = 59, E = 60, F = 62
        var path = graph.getPathToBestNode(62);


        var expected = new ArrayList<>(List.of(62L, 59L, 58L, 57L, 56L));
        var node_id_path = path.getNodes().stream().map(ExploreNode::getId).toList();
        Assertions.assertEquals(expected, node_id_path);

    }

    static ExploreGraph createTestGraph() throws NoSuchFieldException {

        long nodeAId = 56, nodeBId = 57, nodeCId = 58, nodeDId = 59, nodeEId = 60, nodeFId = 62;

        // Create the exploreNodes
        ExploreNode
                NodeA = new ExploreNode(nodeAId, 20, false), NodeB = new ExploreNode(nodeBId, 21, true),
                NodeC = new ExploreNode(nodeCId, 20, true), NodeD = new ExploreNode(nodeDId, 21, true),
                NodeE = new ExploreNode(nodeEId, 22, false), NodeF = new ExploreNode(nodeFId, 22, true);


        AddNeighbours(NodeA, List.of(NodeB));
        AddNeighbours(NodeB, List.of(NodeC));
        AddNeighbours(NodeC, List.of(NodeD));
        AddNeighbours(NodeD, List.of(NodeE, NodeF));
        AddNeighbours(NodeE, List.of(NodeD));


        ExploreGraph graph = new ExploreGraph();
        Field nodeMapField = ExploreGraph.class.getDeclaredField("nodeMap");
        var nodeMap = (Map<Long, ExploreNode>) new FieldReader(graph, nodeMapField).read();

        nodeMap.put(nodeAId, NodeA);
        nodeMap.put(nodeBId, NodeB);
        nodeMap.put(nodeCId, NodeC);
        nodeMap.put(nodeDId, NodeD);
        nodeMap.put(nodeEId, NodeE);
        nodeMap.put(nodeFId, NodeF);

        return graph;
    }


    static ExploreGraph createTestGraphSimilar() throws NoSuchFieldException {

        long nodeAId = 56, nodeBId = 57, nodeCId = 58, nodeDId = 59, nodeEId = 60, nodeFId = 62;

        // Create the exploreNodes
        ExploreNode
                NodeA = new ExploreNode(nodeAId, 20, false), NodeB = new ExploreNode(nodeBId, 21, true),
                NodeC = new ExploreNode(nodeCId, 20, true), NodeD = new ExploreNode(nodeDId, 21, true),
                NodeE = new ExploreNode(nodeEId, 21, false), NodeF = new ExploreNode(nodeFId, 22, true);


        AddNeighbours(NodeA, List.of(NodeB));
        AddNeighbours(NodeB, List.of(NodeC));
        AddNeighbours(NodeC, List.of(NodeD));
        AddNeighbours(NodeD, List.of(NodeE, NodeF));
        AddNeighbours(NodeE, List.of(NodeD));


        ExploreGraph graph = new ExploreGraph();
        Field nodeMapField = ExploreGraph.class.getDeclaredField("nodeMap");
        var nodeMap = (Map<Long, ExploreNode>) new FieldReader(graph, nodeMapField).read();

        nodeMap.put(nodeAId, NodeA);
        nodeMap.put(nodeBId, NodeB);
        nodeMap.put(nodeCId, NodeC);
        nodeMap.put(nodeDId, NodeD);
        nodeMap.put(nodeEId, NodeE);
        nodeMap.put(nodeFId, NodeF);

        return graph;
    }

    static void AddNeighbours(ExploreNode parent, List<ExploreNode> neighbours){

        for (var neighbour: neighbours){
            parent.addNeighbour(neighbour);
            neighbour.addNeighbour(parent);
        }
    }

}
