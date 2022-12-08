package student;

import game.Node;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PathTest {
    // Constructor tests
    
    @Test
    void Test_NewPathIsEmpty() {
        var p = new Path();
        Assertions.assertEquals(nodeList(0), p.getNodes());
    }

    @Test
    void Test_NewPathFromPathIsEqual() {
        var nodes = nodeList(2);
        var p = new Path(nodes);

        Assertions.assertEquals(nodes, p.getNodes());
    }


    @Test
    void Test_NewPathFromNodeContainsNode() {
        var nodes = nodeList(1);
        var p = new Path(nodes.get(0));

        Assertions.assertEquals(nodes, p.getNodes());
    }

    // addNode tests

    @Test
    void Test_addNode() {
        var nodes = nodeList(5);
        var newNode = getMockNode();

        var p = new Path(nodes);

        Assertions.assertEquals(nodes, p.getNodes());

        p.addNode(newNode);
        nodes.add(newNode);

        Assertions.assertEquals(nodes, p.getNodes());
    }

    // cloneWithNode

    @Test
    void Test_cloneWithNode() {
        var nodes = nodeList(5);
        var newNode = getMockNode();

        var p = new Path(nodes);

        Assertions.assertEquals(nodes, p.getNodes());

        var p2 = p.cloneWithNode(newNode);
        nodes.add(newNode);

        Assertions.assertEquals(nodes, p2.getNodes());
    }

    // compareTo

    @Test
    void Test_compareTo_different() {
        var plow = getMockPath();
        var phigh = getMockPath();

        // Call compareTo instead of mocking it
        Mockito.when(plow.compareTo(Mockito.any())).thenAnswer(Mockito.CALLS_REAL_METHODS);
        Mockito.when(phigh.compareTo(Mockito.any())).thenAnswer(Mockito.CALLS_REAL_METHODS);

        // Return pre-baked results for weight
        Mockito.when(plow.getWeight()).thenReturn(5);
        Mockito.when(phigh.getWeight()).thenReturn(6);

        Assertions.assertEquals(-1, plow.compareTo(phigh));
        Assertions.assertEquals(1, phigh.compareTo(plow));
    }


    @Test
    void Test_compareTo_equal() {
        var plow = getMockPath();
        var phigh = getMockPath();

        // Call compareTo instead of mocking it
        Mockito.when(plow.compareTo(Mockito.any())).thenAnswer(Mockito.CALLS_REAL_METHODS);
        Mockito.when(phigh.compareTo(Mockito.any())).thenAnswer(Mockito.CALLS_REAL_METHODS);

        // Return identical for weight
        Mockito.when(plow.getWeight()).thenReturn(5);
        Mockito.when(phigh.getWeight()).thenReturn(5);

        // Return pre-baked results for gold
        Mockito.when(plow.getGold()).thenReturn(100);
        Mockito.when(phigh.getGold()).thenReturn(200);

        Assertions.assertEquals(1, plow.compareTo(phigh));
        Assertions.assertEquals(-1, phigh.compareTo(plow));
    }

    private static Path getMockPath() {
        return Mockito.mock(Path.class);
    }

    /*
     * What to test in Path:
     * Construction, that it's empty after being consturcted
     * addNode - that the node being added is added, and that other nodes are not present
     * addNode -
     */

    private static ArrayList<Node> nodeList(int num) {
        ArrayList<Node> list = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            list.add(getMockNode());
        }
        return list;
    }

    private static ArrayList<Node> nodeList(Node[] nodes) {
        return new ArrayList<>(Arrays.asList(nodes));
    }

    private static ArrayList<Node> nodeList(ArrayList<Node> nodes) {
       return new ArrayList<>(nodes);
    }

    private static Node getMockNode() {
        return Mockito.mock(Node.class);
    }
}
