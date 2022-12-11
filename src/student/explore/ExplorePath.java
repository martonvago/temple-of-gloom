package student.explore;

import student.dijkstra.DijkstraPath;

import java.util.List;

/**
 * An ExplorePath is a DijkstraPath composed of ExploreNodes.
 */
public class ExplorePath extends DijkstraPath<ExploreNode> {
    public ExplorePath() {
        super();
    }

    public ExplorePath(List<ExploreNode> nodes) {
        super(nodes);
    }

    public ExplorePath(ExploreNode node) {
        super(node);
    }

    @Override
    public DijkstraPath<ExploreNode> cloneWithNode(ExploreNode node) {
        ExplorePath pathCopy = new ExplorePath(getNodes());
        pathCopy.addNode(node);
        return pathCopy;
    }

    @Override
    public int compareTo(DijkstraPath<ExploreNode> otherPath) {
        return getSize().compareTo(otherPath.getSize());
    }

    @Override
    public Integer getWeight() {
        return getSize();
    }

    @Override
    public Integer getGold() {
        return 0;
    }
}
