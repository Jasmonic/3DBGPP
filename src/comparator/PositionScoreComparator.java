package comparator;

import baseObject.Position;

import java.util.Comparator;

/**
 * @Author: Feng Jixuan
 * @Date: 2022-12-2022-12-18
 * @Description: BPP_Model
 * @version=1.0
 */
public class PositionScoreComparator implements Comparator<Position> {

    @Override
    public int compare(Position o1, Position o2) {
        if (o1.score<o2.score) return -1;
        if (o1.score>o2.score) return 1;
        return 0;
    }
}
