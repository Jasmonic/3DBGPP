package comparator;

import baseObject.Item;

import java.util.Comparator;

/**
 * @Author: Feng Jixuan
 * @Date: 2022-12-2022-12-14
 * @Description: BPP_Model
 * @version=1.0
 */
public class AreaHeightItemComparator implements Comparator<Item> {

    public int compare(Item o1, Item o2) {
        double area1 = o1.getP() * (o1.getQ());
        double area2 = o2.getP() * (o2.getQ());
        if (area1 == area2 ) {
            return (int) Math.signum(o2.getR() - o1.getR());//非递增顺序
        }
        return (int) Math.signum(area2 - area1);//非递增顺序
    }
}
