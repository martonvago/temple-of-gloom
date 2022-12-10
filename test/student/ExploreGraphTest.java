package student;

import org.junit.jupiter.api.Test;

public class ExploreGraphTest {

    @Test
    private void test_log_node_visit_no_neighbours() {

        // Log a node visit with no neighbours
        // Assert that the internal node map has 1 entry
        // Asset entry is marked and visited and has the correct distance value

    }


    @Test
    private void test_log_node_visit_with_new_neighbours() {

        // Log a node visit with 2 neighbours
        // Assert that the internal node map has 3 entries
        // Assert that the neighbours have a neighbour entry with the visited node (bidirectional connection)
        // Assert All three entries have correct distances

    }

    @Test
    private void test_log_node_visit_neighbours_already_exist() {

        // Create a graph with nodes A,B,C. A is neighbours with B and C
        // Log a new Node visit to D, which is also neighbours with B and C
        // Assert that only 4 node entries in the graph
        // Assert that Nodes B and C have connections to Both neighbours A and D

    }


    @Test
    private void test_log_node_visit_node_already_in_graph() {

        // Create a graph with node A
        // Log a new Node visit to A
        // Assert that there is only 1 entry in the graph
        // Assert that Node A has the "visited" field set to true

    }

    @Test
    private void test_get_unexplored_neighbours() {

        // Log a visit to A with neighbours B and C
        // Node C is 20 unites from the target and B is 22
        // Get the unexplored neighbours of A
        // Assert that the list is in the order of [C, B]

    }

    @Test
    private void test_list_unvisited_nodes_sorted() {

        // Create a graph with the following topography
        // E    F    G   H
        // |    |    |   |
        // A -> B -> C - D      (-> Direction of the target)
        // |    |    |   |
        // I    J    K   L

        // The visited nodes are A,B,C,D
        // List the unvisited nodes
        // Assert that the returned order is E,I,F,J,G,K,H,L

    }


    @Test
    private void test_get_closest_unexplored() {

        // Create a graph with the following topography
        // E    F    G   H
        // |    |    |   |
        // A -> B -> C - D
        // |    |    |   |
        // I    J    K   L
        // F is the closest unexplored node
        // Assert that F is returned
    }

    @Test
    private void test_shorted_path_to() {

        // Create a graph with the following topography
        // C -> D -> E -> F
        // |              |
        // B              G
        // |              |
        // A -> I -> J -> H
        // Get the shortest path from A to H
        // Assert the path is I , J , H

    }


    @Test
    private void test_convert_to_path() {


    }

    @Test
    private void test_get_path_to_best_node() {


    }

}
