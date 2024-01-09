package comparator;

import baseObject.Bag;
import baseObject.Instance;
import baseObject.Item;

import java.util.Comparator;

/**
 * @Author: Feng Jixuan
 * @Date: 2023-03-2023-03-02
 * @Description: BPP_Model
 * @version=1.0
 */
public class GraspItemComparator implements Comparator<Item> {
    Instance instance;

    public GraspItemComparator(Instance instance) {
        this.instance = instance;
    }

    public int compare(Item o1, Item o2) {
        // 能放进的袋子种类数 升序
        int n1 = 0, n2 = 0;
        for (Bag bag : instance.getBags()) {
            if (o1.getLengthOf(0) + o1.getLengthOf(2) <= bag.getX() && o1.getLengthOf(1) + o1.getLengthOf(2) <= bag.getY())
                n1++;
            if (o2.getLengthOf(0) + o2.getLengthOf(2) <= bag.getX() && o2.getLengthOf(1) + o2.getLengthOf(2) <= bag.getY())
                n2++;
        }
        if (n1 < n2) {
            return -1;
        }
        if (n1 > n2) {
            return 1;
        }
        //能放进的袋子的最低成本 升序
        double c1 = 10, c2 = 10;
        for (Bag bag : instance.getBags()) {
            if (o1.getLengthOf(0) + o1.getLengthOf(2) <= bag.getX() && o1.getLengthOf(1) + o1.getLengthOf(2) <= bag.getY()) {
                c1 = Math.min(c1, bag.getCost());
            }
            if (o2.getLengthOf(0) + o2.getLengthOf(2) <= bag.getX() && o2.getLengthOf(1) + o2.getLengthOf(2) <= bag.getY()) {
                c2 = Math.min(c2, bag.getCost());
            }
        }
        if (c1 < c2) {
            return -1;
        }
        if (c1 > c2) {
            return 1;
        }
        //按照物体的体积
        if (o1.getVolume() > o2.getVolume()) return -1;
        if (o1.getVolume() < o2.getVolume()) return 1;
        //按照长宽高
        if (o1.getLengthOf(0) > o2.getLengthOf(0)) return -1;
        if (o1.getLengthOf(0) < o2.getLengthOf(0)) return 1;
        if (o1.getLengthOf(1) > o2.getLengthOf(1)) return -1;
        if (o1.getLengthOf(1) < o2.getLengthOf(1)) return 1;
        if (o1.getLengthOf(2) > o2.getLengthOf(2)) return -1;
        if (o1.getLengthOf(2) < o2.getLengthOf(2)) return 1;
        return 0;
    }
}
