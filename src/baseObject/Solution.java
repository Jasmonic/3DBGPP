package baseObject;

import java.util.ArrayList;

/**
 * @Author: Feng Jixuan
 * @Date: 2023-03-2023-03-02
 * @Description: BPP_Model
 * @version=1.0
 */
public class Solution {
    public ArrayList<Bag> usedBags;
    public ArrayList<ArrayList<Item>> packedItemsList;
    public ArrayList<ArrayList<Position>> packedPositionsList;
    double cost;
    public Solution(){
        usedBags=new ArrayList<>();
        packedItemsList=new ArrayList<>();
        packedPositionsList=new ArrayList<>();
        cost=0;
    }
    public Solution(ArrayList<Bag> useBags, ArrayList<ArrayList<Item>> packedItems, ArrayList<ArrayList<Position>> packedPositions) {
        this.usedBags = useBags;
        this.packedItemsList = packedItems;
        this.packedPositionsList = packedPositions;
    }
    public void addApattern(Bag bag,ArrayList<Item> packedItems, ArrayList<Position> packedPosition){
        usedBags.add(bag);
        packedItemsList.add(packedItems);
        packedPositionsList.add(packedPosition);
    }
    public void removePattern(int index){
        usedBags.remove(index);
        packedItemsList.remove(index);
        packedPositionsList.remove(index);
    }
    public void calCost(){
        double c=0;
        for (Bag bag: usedBags){
            c+=bag.getCost();
        }
        setCost(c);
    }

    public ArrayList<Bag> getUsedBags() {
        return usedBags;
    }

    public void setUsedBags(ArrayList<Bag> usedBags) {
        this.usedBags = usedBags;
    }

    public ArrayList<ArrayList<Item>> getPackedItemsList() {
        return packedItemsList;
    }

    public void setPackedItemsList(ArrayList<ArrayList<Item>> packedItemsList) {
        this.packedItemsList = packedItemsList;
    }

    public ArrayList<ArrayList<Position>> getPackedPositionsList() {
        return packedPositionsList;
    }

    public void setPackedPositionsList(ArrayList<ArrayList<Position>> packedPositionsList) {
        this.packedPositionsList = packedPositionsList;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }


}
