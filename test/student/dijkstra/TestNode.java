package student.dijkstra;

import student.EqualsById;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

class TestNode extends EqualsById implements DijkstraNode<TestNode> {
    static private long nextId = 0;
    static private long makeId() {
        return nextId++;
    }

    private final long id = makeId();
    private final HashSet<TestNode> neighbours = new HashSet<>();

    @Override
    public long getId() {
        return this.id;
    }

    @Override
    public Set<TestNode> getNeighbours() {
        return this.neighbours;
    }

    void setNeighbours(List<TestNode> nodes, int... ids) {
        for (int i : ids) neighbours.add(nodes.get(i));
    }
    @Override
    public DijkstraPath<TestNode> wrapToPath() {
        return new TestPath(this);
    }
}
