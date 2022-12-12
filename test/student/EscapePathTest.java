package student;

import game.Edge;
import game.Node;
import game.Tile;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import student.escape.EscapeNode;
import student.escape.EscapePath;

import java.util.ArrayList;
import java.util.List;

public class EscapePathTest {
    // Constructor tests
    
    @Test
    void Test_NewPathIsEmpty() {
        var p = new EscapePath();
        Assertions.assertEquals(nodeList(0), p.getNodes());
    }

    @Test
    void Test_NewPathFromPathIsEqual() {
        var nodes = nodeList(2);
        var p = new EscapePath(nodes);

        Assertions.assertEquals(nodes, p.getNodes());
    }


    @Test
    void Test_NewPathFromNodeContainsNode() {
        var nodes = nodeList(1);
        var p = new EscapePath(nodes.get(0));

        Assertions.assertEquals(nodes, p.getNodes());
    }

    // addNode tests

    @Test
    void Test_addNode() {
        var nodes = nodeList(5);
        var newNode = getMockEscapeNode();

        var p = new EscapePath(nodes);

        Assertions.assertEquals(nodes, p.getNodes());

        p.addNode(newNode);
        nodes.add(newNode);

        Assertions.assertEquals(nodes, p.getNodes());
    }

    // cloneWithNode

    @Test
    void Test_cloneWithNode() {
        var nodes = nodeList(5);
        var newNode = getMockEscapeNode();

        var p = new EscapePath(nodes);

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
    // enhancePath()

    // TODO: This is a bit more complicated since we need to feed a graph

    // getGold()

    @Test
    void Test_getGold_zero() {
        var nodes = nodeList(5);
        for (EscapeNode node : nodes) {
            Mockito.when(node.getTile()).thenReturn(
                    new Tile(0, 0, 0, Tile.Type.FLOOR)
            );
        }

        var p = new EscapePath(nodes);

        Assertions.assertEquals(0, p.getGold());
    }

    @Test
    void Test_getGold_nonZero() {
        var nodes = nodeList(5);
        var golds = new int[] {0, 10, 100, 0, 1};
        for (int i = 0; i < golds.length; i++) {
            Mockito.when(nodes.get(i).getTile()).thenReturn(
                    new Tile(0, 0, golds[i], Tile.Type.FLOOR)
            );
        }

        var p = new EscapePath(nodes);

        Assertions.assertEquals(111, p.getGold());
    }

    // getNode
    @Test
    void Test_getNode() {
        var nodes = nodeList(5);
        var p = new EscapePath(nodes);

        for (int i = 0; i < nodes.size(); i++) {
            Assertions.assertEquals(nodes.get(i), p.getNode(i));
        }
        Assertions.assertThrows(IndexOutOfBoundsException.class,
                () -> p.getNode(-1)
        );

        Assertions.assertThrows(IndexOutOfBoundsException.class,
                () -> p.getNode(nodes.size() + 1)
        );
    }

    // getNodes

    @Test
    void Test_getNodes() {
        var nodes = nodeList(4);
        var p = new EscapePath(nodes);

        Assertions.assertEquals(nodes, p.getNodes());
    }

    // getSize

    @Test
    void Test_getSize() {
        var nodes = nodeList(11);
        var p = new EscapePath(nodes);

        Assertions.assertEquals(nodes.size(), p.getSize());
    }

    // getSubpath

    @Test
    void Test_getSubpath() {
        var nodes = nodeList(10);
        var p = new EscapePath(nodes);

        Assertions.assertEquals(nodes.subList(0, 2), p.getSubpath(0, 2).getNodes());
        Assertions.assertEquals(nodes.subList(5, 9), p.getSubpath(5, 4).getNodes());

        Assertions.assertThrows(IndexOutOfBoundsException.class,
                () -> p.getSubpath(5, 6)
        );
    }

    // getWeight

    @Test
    void Test_getWeight_zero() {
        var nodes = nodeList(5);
        // Beware: This is probably mimicking the implementation too much.
        // If the implementation of getWeight is changed (i.e, to use i = 0; i < size - 1), then the test will fail.
        for (int i = 1; i < nodes.size(); i++) {
            var na = nodes.get(i);
            var nb = nodes.get(i-1);
            Mockito.when(na.getEdge(nb.getId())).thenReturn(
                    new Edge(getMockNode(), getMockNode(), 0)
            );
        }

        var p = new EscapePath(nodes);

        Assertions.assertEquals(0, p.getWeight());
    }

    @Test
    void Test_getWeight_nonZero() {
        var nodes = nodeList(5);

        List<Integer> lengths = List.of(5, 8, 7, 5);
        // Beware: This is probably mimicking the implementation too much.
        // If the implementation of getWeight is changed (i.e, to use i = 0; i < size - 1), then the test will fail.
        for (int i = 1; i < nodes.size(); i++) {
            var na = nodes.get(i);
            var nb = nodes.get(i-1);
            Mockito.when(na.getEdge(nb.getId())).thenReturn(
                    new Edge(getMockNode(), getMockNode(), lengths.get(i-1))
            );
        }

        var p = new EscapePath(nodes);

        Assertions.assertEquals(
                lengths.stream().reduce(Integer::sum).get(),
                p.getWeight()
        );
    }

    // isLoop

    // joinPath

    // removeGoldlessLoops

    // replaceAtIndex

    // Helper functions


    private static EscapePath getMockPath() {
        return Mockito.mock(EscapePath.class);
    }


    private static ArrayList<EscapeNode> nodeList(int num) {
        ArrayList<EscapeNode> list = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            list.add(getMockEscapeNode());
        }
        return list;
    }

    private static EscapeNode getMockEscapeNode() {
        return Mockito.mock(EscapeNode.class);
    }

    private static Node getMockNode() {
        return Mockito.mock(Node.class);
    }
}
