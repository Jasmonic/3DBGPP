package scorer;

import baseObject.Position;
import singleBag.ExtremePointSet;

/**
 * @Author: Feng Jixuan
 * @Date: 2023-03-2023-03-01
 * @Description: BPP_Model
 * @version=1.0
 */
public class AbsoluteItem implements PositionScorer {
    public double calculateScore(ExtremePointSet extremePointSet, Position position){
        int x=position.getX()+position.getLx();
        int y=position.getY()+position.getLy();
        int z=position.getZ()+position.getLz();
        x=Math.max(x,extremePointSet.getMaxX());
        y=Math.max(y,extremePointSet.getMaxY());
        z=Math.max(z,extremePointSet.getMaxZ());
//        越大越好
//        return -Math.min(node.getBag().getX()-x-z,node.getBag().getY()-y-z);
        return -(extremePointSet.getBag().getX()-x-z)-(extremePointSet.getBag().getY()-y-z);
    }
}
