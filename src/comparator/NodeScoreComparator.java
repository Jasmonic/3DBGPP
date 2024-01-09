package comparator;

import singleBag.Node;

import java.util.Comparator;

/**
 * @Author: Feng Jixuan
 * @Date: 2022-12-2022-12-31
 * @Description: BPP_Model
 * @version=1.0
 */
public class NodeScoreComparator implements Comparator<Node> {
    @Override
    public int compare(Node o1, Node o2) {
        if (o1.getScore()<o2.getScore()) return -1;
        if (o1.getScore()>o2.getScore()) return 1;
        return 0;
    }
}
