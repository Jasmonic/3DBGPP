package comparator;

import singleBag.ExtremePoint;

import java.util.Comparator;

/**
 * @Author: Feng Jixuan
 * @Date: 2022-12-2022-12-14
 * @Description: BPP_Model
 * @version=1.0
 */
public class ExtremePointXYZComparator implements Comparator<ExtremePoint> {

    public int compare(ExtremePoint o1, ExtremePoint o2) {
        boolean nullFirst=false;
        if (o1 == null) {
            return (o2 == null) ? 0 : (nullFirst ? -1 : 1);
        } else if (o1 == null) {
            return nullFirst ? 1: -1;
        }
        double zCoord = o1.getZ() - o2.getZ();
        double yCoord = o1.getY() - o2.getY();
        double xCoord = o1.getX() - o2.getX();

        if(zCoord == 0) {
            if(yCoord == 0){
                if(xCoord == 0) {
                    return 0;
                }
                return (int) Math.signum(xCoord);
            }
            return (int) Math.signum(yCoord);
        }
        return (int) Math.signum(zCoord);
    }
//按照zyx的递增顺序
}
