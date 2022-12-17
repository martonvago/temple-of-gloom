package student.dijkstra;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class DijkstraAlgorithmTest extends DijkstraTestHelper {

    @Test
    void Test_FindsShortestUnweightedPath() {
        var nodes = makeTestNodes(5);
        // 0 - 1 - 2 - 3
        // 0 - 4 - 3
        nodes.get(0).setNeighbours(nodes, 1, 4);
        nodes.get(1).setNeighbours(nodes, 0, 2);
        nodes.get(2).setNeighbours(nodes, 1, 3);
        nodes.get(3).setNeighbours(nodes, 2, 4);
        nodes.get(4).setNeighbours(nodes, 0, 3);

        var exitId = nodes.get(3).getId();

        var alg = new DijkstraAlgorithm<>(nodes.get(0), exitId);

        var path = alg.findShortestPath().get(exitId).getNodes();

        Assertions.assertEquals(path, List.of(nodes.get(0), nodes.get(4), nodes.get(3)));
    }

}
