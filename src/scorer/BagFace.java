package scorer;

import baseObject.Position;
import singleBag.ExtremePointSet;

/**
 * @Author: Feng Jixuan
 * @Date: 2023-01-2023-01-18
 * @Description: BPP_Model
 * @version=1.0
 */
public class BagFace implements PositionScorer {
    public double calculateScore(ExtremePointSet extremePointSet, Position position){
        int x=position.getX()+position.getLx();
        int y=position.getY()+position.getLy();
        int z=position.getZ()+position.getLz();
        x=Math.max(x,extremePointSet.getMaxX());
        y=Math.max(y,extremePointSet.getMaxY());
        z=Math.max(z,extremePointSet.getMaxZ());
        return (x+z)*(y+z);
    }
}
