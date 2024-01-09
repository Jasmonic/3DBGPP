package scorer;

import baseObject.Position;
import singleBag.ExtremePointSet;

/**
 * @Author: Feng Jixuan
 * @Date: 2023-01-2023-01-19
 * @Description: BPP_Model
 * @version=1.0
 */
public interface PositionScorer {
//     double calculateScore(Node node, Position position);
     double calculateScore(ExtremePointSet extremePointSet, Position position);
}
