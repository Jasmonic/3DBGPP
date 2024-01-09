package comparator;

import baseObject.Item;

import java.util.Comparator;

/**
 * @Author: Feng Jixuan
 * @Date: 2022-12-2022-12-18
 * @Description: BPP_Model
 * @version=1.0
 */
public class VolumeHeightItem implements Comparator<Item> {

    @Override
    public int compare(Item o1, Item o2) {
        if (o1.getVolume()>o2.getVolume()) return -1;
        if (o1.getVolume()<o2.getVolume()) return 1;
//        if (o1.getVolume()==o2.getVolume()){
//
//        }
        return 0;
    }
}
