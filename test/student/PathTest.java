package student;

import game.Node;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Arrays;

public class PathTest {
    // Constructor tests
    
    @Test
    void Test_NewPathIsEmpty() {
        Path p = new Path();
        Assertions.assertEquals(p.getNodes(), new ArrayList<Node>());
    }

    @Test
    void Test_NewPathFromPathIsEqual() {
        ArrayList<Node> nodes = nodeList();
        nodes.add(Mockito.mock(Node.class));
        nodes.add(Mockito.mock(Node.class));

        Path p = new Path(nodes);
        Assertions.assertEquals(p.getNodes(), nodes);
    }

    @Test
    void Test_NewPathFromNodeContainsNode() {
        ArrayList<Node> nodes = nodeList();
        nodes.add(Mockito.mock(Node.class));

        Path p = new Path(nodes.get(0));
        Assertions.assertEquals(nodes, p.getNodes());
    }

    // addNode tests
    @Test
    void Test_addNode_preservesOrder() {
        var nodes = nodeList();
    }

    /*
     * What to test in Path:
     * Construction, that it's empty after being consturcted
     * addNode - that the node being added is added, and that other nodes are not present
     * addNode -
     */

    private static ArrayList<Node> nodeList() {
       return new ArrayList<>();
    }

    private static ArrayList<Node> nodeList(Node[] nodes) {
        return new ArrayList<>(Arrays.asList(nodes));
    }

    private static ArrayList<Node> nodeList(ArrayList<Node> nodes) {
       return new ArrayList<>(nodes);
    }
}
