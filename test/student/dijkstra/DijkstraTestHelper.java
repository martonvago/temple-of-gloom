package student.dijkstra;

import java.util.ArrayList;
import java.util.List;

public class DijkstraTestHelper {
    List<TestNode> makeTestNodes(int num) {
            var list = new ArrayList<TestNode>(num);

            for (int i = 0; i < num; i++) {
                list.add(new TestNode());
            }

            return list;
    }
}

