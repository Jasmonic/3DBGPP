package benders;

import baseObject.Configuration;
import baseObject.Instance;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * @Author: Feng Jixuan
 * @Date: 2022-10-2022-10-26
 * @Description: BPP_Model
 * @version=1.0
 */
public class Pattern {
    final int[] pattern;

    public Pattern(Instance instance, ArrayList<Integer> itemsIds, int bagId) {
        pattern = new int[instance.getItemTypeCount() + 1];
        for (int i = 0; i < itemsIds.size(); i++) {
            pattern[instance.getAllItems().get(itemsIds.get(i)).getType()] += 1;
        }
        pattern[instance.getItemTypeCount()] = instance.getAllBags().get(bagId).getType();
    }

    public static ArrayList<Integer> getOldMisFromHashMap(Instance instance, ArrayList<Integer> itemsIds, int bagId) {
        HashMap<Pattern, ArrayList<Integer>> map = instance.getInfeasiblePatternToMIS();
        Pattern pattern = new Pattern(instance, itemsIds, bagId);
        ArrayList<Integer> MISOnItemType = map.get(pattern);
        ArrayList<Integer> MISOnItemIds = new ArrayList<>(MISOnItemType.size());
        boolean[] usedForCut = new boolean[itemsIds.size()]; //该id物品是否被用进cut里了


        for (int type : MISOnItemType) {
            for (int i = 0; i < itemsIds.size(); i++) {
                if (usedForCut[i] == false && instance.getAllItems().get(itemsIds.get(i)).getType() == type) {
                    usedForCut[i] = true;
                    MISOnItemIds.add(itemsIds.get(i));
                    break;
                }
            }
        }
        return MISOnItemIds;

    }

    public static ArrayList<Integer> itemsIdToTypeList(Instance instance, ArrayList<Integer> itemsId) {
        ArrayList<Integer> ItemsType = new ArrayList<>(Configuration.listInitialCapacity);
        for (int id : itemsId) {
            ItemsType.add(instance.getAllItems().get(id).getType());
        }
        return ItemsType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pattern pattern1 = (Pattern) o;
        return Arrays.equals(pattern, pattern1.pattern);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(pattern);
    }

    @Override
    public String toString() {
        return "pattern=" + Arrays.toString(pattern);
    }
}
