package student.dijkstra;

import java.util.List;

public class TestPath extends DijkstraPath<TestNode> {
    public TestPath() {
        super();
    }

    public TestPath(List<TestNode> nodes) {
        super(nodes);
    }

    public TestPath(TestNode node) {
        super(node);
    }

    @Override
    public DijkstraPath<TestNode> cloneWithNode(TestNode node) {
        TestPath pathCopy = new TestPath(getNodes());
        pathCopy.addNode(node);
        return pathCopy;
    }

    @Override
    public int compareTo(DijkstraPath<TestNode> otherPath) {
        return Integer.compare(this.getWeight(), otherPath.getWeight());
    }

    @Override
    public Integer getWeight() {
        return this.getSize();
    }

    @Override
    public Integer getGold() {
        return 0;
    }
}
