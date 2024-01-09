package scorer;

import baseObject.Position;
import singleBag.ExtremePointSet;

/**
 * @Author: Feng Jixuan
 * @Date: 2022-12-2022-12-18
 * @Description: BPP_Model
 * @version=1.0
 */
public class Envelope implements PositionScorer {
    public double calculateScore(ExtremePointSet extremePointSet, Position position){
        int x=position.getX()+position.getLx();
        int y=position.getY()+position.getLy();
        int z=position.getZ()+position.getLz();
        x=Math.max(x,extremePointSet.getMaxX());
        y=Math.max(y,extremePointSet.getMaxY());
        z=Math.max(z,extremePointSet.getMaxZ());
//        return (x+z)*(y+z);
        return x*y*z;
//        return x*y*z*1.0/(position.getLx()*position.getLy()*position.getLz());
//        return x*y*z*1.0/(position.getLx()*position.getLy()*position.getLz())*(x+z)*(y+z);
    }
}
