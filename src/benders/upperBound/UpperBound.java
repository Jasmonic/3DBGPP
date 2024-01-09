package benders.upperBound;

import baseObject.*;
import comparator.VolumeHeightItem;
import singleBag.ExtremePoint;
import singleBag.ExtremePointSet;

import java.util.ArrayList;

/**
 * @Author: Feng Jixuan
 * @Date: 2023-01-2023-01-15
 * @Description: BPP_Model
 * @version=1.0
 */
public class UpperBound {
    Instance instance;
    int[] upperCounts; //每种袋子的最高使用个数
    boolean[] feasible;

    public UpperBound(Instance instance) {
        this.instance = instance;
        upperCounts = new int[instance.getBagTypeCount()];
        feasible = new boolean[instance.getBagTypeCount()];
    }

    public void calculateUpperCounts() {
        for (int i = 0; i < instance.getBagTypeCount(); i++) {
            upperCounts[i] = calculateSingleUpperCount(instance.getBags().get(i),i);
        }
    }

    public int calculateSingleUpperCount(Bag bag,int k) {
        int bound = 1;
        ArrayList<Item> items = new ArrayList<>();
        feasible[k]=true;
        for (Item item: instance.getAllItems()) {
            Item item1=item.copyOfOrientation(1);
            if (item1.getP()+item1.getR()<=bag.getX()&&item1.getQ()+item1.getR()<=bag.getY()){
                items.add(item);
            }else{
                feasible[k]=false;
            }
        }
        items.sort(new VolumeHeightItem());
        //第一个bag
        ExtremePointSet epSet = new ExtremePointSet(bag);
        ArrayList<Position> packedPositions = new ArrayList<>();
        ArrayList<ArrayList<Position>> packedPositionsList = new ArrayList<>();
        epSet.getExtremePoints().add(new ExtremePoint(0, 0, 0));
        boolean canPack;

        while (!items.isEmpty()) {
            canPack = false;
            Item packItem = null;
            for (Item toPackItem : items) {
                for (int i = 1; i <= 6; i++) {
                    Item tryItem = toPackItem.copyOfOrientation(i);
                    for (ExtremePoint extremePoint : epSet.getExtremePoints()) {
                        Position packPosition = new Position(toPackItem.getType(), extremePoint.getX(), extremePoint.getY(), extremePoint.getZ(),
                                tryItem.getP(), tryItem.getQ(), tryItem.getR());
                        if (!check.isPositionCollideWithPacked(packPosition, packedPositions)
                                && !check.isPositionOutOfBag(epSet, packPosition)) {
                            canPack = true;
                            epSet.updateEPSetAfterPack(packedPositions, packPosition);
                            packedPositions.add(packPosition);
                            break;
                        }
                    }
                    if (canPack) break;
                }
                if (canPack) {
                    packItem = toPackItem;
                    break;
                }
            }
            if (canPack)
                items.remove(packItem);
            else {
                //新开一个袋子
                bound++;
                packedPositionsList.add(packedPositions);
                epSet = new ExtremePointSet(bag);
                packedPositions = new ArrayList<>();
                epSet.getExtremePoints().add(new ExtremePoint(0, 0, 0));
            }
        }
        packedPositionsList.add(packedPositions);
//        System.out.println("=========================");
//        System.out.println(packedPositionsList.size());
//        for(ArrayList<Position> ps:packedPositionsList){
//            for (Position p:ps){
//                System.out.println(p);
//            }
//            System.out.println("------");
//        }
        return bound;
    }

    public int[] getUpperCounts() {
        return upperCounts;
    }
    public double calUbCost(){
        double ub=1000;
        for (int i = 0; i < upperCounts.length; i++) {
            if (feasible[i]){
                ub=Math.min(ub,upperCounts[i]*instance.getBags().get(i).getCost());
            }
        }
        return ub;
    }
}
