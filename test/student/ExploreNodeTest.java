package student;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import student.explore.ExploreNode;

import java.util.HashSet;
import java.util.List;

public class ExploreNodeTest {

    long nodeAId = 55;
    int distanceA = 20;

    long nodeBId = 56;
    int distanceB = 21;


    @Test
    public void test_new_explore_node(){
        new ExploreNode(nodeAId, distanceA, true);
    }

    @Test
    public void test_explore_node_get_id(){
        var node = new ExploreNode(nodeAId, distanceA, true);
        Assertions.assertEquals(nodeAId, node.getId());
    }

    @Test
    public void test_explore_node_get_visited(){
        var node = new ExploreNode(nodeAId, distanceA, true);
        Assertions.assertTrue(node.getVisited());
    }

    @Test
    public void test_explore_node_visit_unvisited_node(){
        // test that visiting a node updated the visited variable
        var node = new ExploreNode(nodeAId, distanceA, false);
        Assertions.assertFalse(node.getVisited());
        node.visit();
        Assertions.assertTrue(node.getVisited());
    }

    @Test
    public void test_explore_node_get_distance(){
        var node = new ExploreNode(nodeAId, distanceA, true);
        Assertions.assertEquals(distanceA, node.getDistanceToTarget());
    }

    @Test
    public void test_explore_node_add_neighbour(){
        var nodeA = new ExploreNode(nodeAId, distanceA, true);
        var nodeB = new ExploreNode(nodeBId, distanceB, false);

        // assert that node A neighbours' set is empty before adding
        Assertions.assertEquals(new HashSet<ExploreNode>(), nodeA.getNeighbours());

        nodeA.addNeighbour(nodeB);
        Assertions.assertEquals(new HashSet<>(List.of(nodeB)), nodeA.getNeighbours());

        // Adding a neighbour should not be cyclical
        Assertions.assertEquals(new HashSet<ExploreNode>(), nodeB.getNeighbours());
    }

    @Test
    public void test_explore_node_compare() {
        // Assert that NodeB is selected as the smaller value when its closer to the target

        var nodeA = new ExploreNode(nodeAId, distanceA, true);
        var nodeB = new ExploreNode(nodeBId, distanceB, false);
        Assertions.assertEquals(-1, nodeA.compareTo(nodeB));
    }


    @Test
    public void test_explore_node_compare_same_distance() {
        // Assert that NodeB is selected as the smaller value when the distances are identical
        // As it has the largest ID

        var nodeA = new ExploreNode(nodeAId, distanceA, true);
        var nodeB = new ExploreNode(nodeBId, distanceA, false);
        Assertions.assertEquals(-1, nodeA.compareTo(nodeB));
    }


}
