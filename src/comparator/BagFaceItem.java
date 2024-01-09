package comparator;

import baseObject.Item;

import java.util.Comparator;

/**
 * @Author: Feng Jixuan
 * @Date: 2023-01-2023-01-17
 * @Description: BPP_Model
 * @version=1.0
 */
public class BagFaceItem implements Comparator<Item> {
    public int compare(Item o1, Item o2) {
        int f1,f2;
        f1=(o1.getLengthOf(0)+o1.getLengthOf(2))*(o1.getLengthOf(1)+o1.getLengthOf(2));
        f2=(o2.getLengthOf(0)+o2.getLengthOf(2))*(o2.getLengthOf(1)+o2.getLengthOf(2));
        if (f1>f2) return -1;
        if (f1<f2) return 1;
        return 0;
    }
}
