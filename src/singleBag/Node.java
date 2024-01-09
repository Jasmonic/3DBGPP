package singleBag;

import baseObject.*;
import comparator.PositionScoreComparator;
import scorer.PositionScorer;

import java.util.ArrayList;

/**
 * @Author: Feng Jixuan
 * @Date: 2022-12-2022-12-15
 * @Description: BPP_Model
 * @version=1.0
 */
public class Node {
    ArrayList<Position> packedPosition;
    ExtremePointSet extremePointSet;
    Bag bag;
    ArrayList<Integer> remainingItemsId;
    ArrayList<Integer> packedItemsId;
    ArrayList<Item> remainingItems;
    Instance instance;
    int depth;
    double score;


    public Node(ArrayList<Position> packedPosition, ExtremePointSet extremePointSet, Bag bag) {

        this.packedPosition = packedPosition;
        this.extremePointSet = extremePointSet;
        this.bag = bag;
    }

    public Node() {

    }

    public Node(Bag bag, ArrayList<Integer> remainingItemsId, Instance instance) {
        //TODO 用来构造初始node
        this.packedPosition = new ArrayList<>(Configuration.listInitialCapacity);
        this.extremePointSet = new ExtremePointSet(bag);
        this.bag = bag;
        this.remainingItemsId = remainingItemsId;
        this.packedItemsId = new ArrayList<>(Configuration.listInitialCapacity);
        this.instance = instance;
        this.remainingItems = transformItemTypeToItems(remainingItemsId);
        this.extremePointSet.getExtremePoints().add(new ExtremePoint(0, 0, 0));
    }

    public Node(Node node) {
        this.packedPosition = new ArrayList<>(Configuration.listInitialCapacity);
        for (Position position : node.getPackedPosition()) {
            this.packedPosition.add(position.copy());
        }
        this.extremePointSet = node.getExtremePointSet().copy();
        this.bag = node.getBag();
        this.remainingItemsId = new ArrayList<>(Configuration.listInitialCapacity);
        this.packedItemsId = new ArrayList<>(Configuration.listInitialCapacity);
        for (int itemId : node.getRemainingItemsId()) {
            this.remainingItemsId.add(itemId);
        }
        for (int itemID : node.getPackedItemsId()) {
            this.packedItemsId.add(itemID);
        }
        this.instance = node.instance;
//        this.remainingItems=this.transformItemTypeToItems(this.getRemainingItemsId()); //不需要id->item,update完后再更新
    }

    public ArrayList<Node> expand(int beamWidth, PositionScorer scorer, ArrayList<Integer> remainingItemsId) {
        //TODO
        int packId = -1;
        Item toPackItem = null;
        //选择第一个能放的item
        for (int Id : remainingItemsId) {
            boolean canPack = false;
            for (int i = 1; i <= 6; i++) {
                //尝试六个转向
                Item tryItem = instance.getAllItems().get(Id).copyOfOrientation(i);
                for (ExtremePoint extremePoint : this.getExtremePointSet().getExtremePoints()) {
                    //尝试所有极端点
                    Position packPosition = new Position(instance.getAllItems().get(Id).getType(), extremePoint.getX(), extremePoint.getY(), extremePoint.getZ(),
                            tryItem.getP(), tryItem.getQ(), tryItem.getR());
                    if (check.canFit(this, packPosition)) {
                        canPack = true;
                        break;
                    }
                }
                if (canPack) break;
            }
            if (canPack) {
                packId = Id;
                toPackItem = instance.getAllItems().get(packId);
                break;
            }
        }
        if (packId == -1) {
//            System.out.println("当前节点没有可放置的item-EP对，返回null");
            return null;
        } else {
            //6个朝向和所有EP 选择top max（beamwidth，6*EP）个作为子节点
            ArrayList<Node> expandedNodeList = new ArrayList<>();
            ArrayList<Position> candidatePositionList = new ArrayList<>();
            for (int i = 1; i <= 6; i++) {
                Item tryItem = toPackItem.copyOfOrientation(i);
                for (ExtremePoint extremePoint : this.getExtremePointSet().getExtremePoints()) {

                    Position packPosition = new Position(toPackItem.getType(), toPackItem.getId(), extremePoint.getX(), extremePoint.getY(), extremePoint.getZ(),
                            tryItem.getP(), tryItem.getQ(), tryItem.getR());
//                    System.out.println(packPosition.getId());
                    if (check.canFit(this, packPosition)) {
                        candidatePositionList.add(packPosition);
                        packPosition.setScore(scorer.calculateScore(this.getExtremePointSet(), packPosition));
                    }
                }
            }
            //评分
            candidatePositionList.sort(new PositionScoreComparator());
//            for (Position p:candidatePositionList){
//                System.out.println(p.score);
//            }
            for (int i = 0; i < Math.min(candidatePositionList.size(), beamWidth); i++) {
                Node newNode = new Node(this);
                newNode.update(packId, candidatePositionList.get(i));
//                System.out.println(candidatePositionList.get(i).getId());
                newNode.setScore(candidatePositionList.get(i).getScore());
                expandedNodeList.add(newNode);
            }
            return expandedNodeList;
        }
    }

    public ArrayList<Node> expand2(int beamWidth, int itemWidth, PositionScorer scorer, ArrayList<Integer> remainingItemsId) {
        //TODO
        int packId = -1;
        Item toPackItem = null;
        ArrayList<Integer> packIdList = new ArrayList<>();
        ArrayList<Item> toPackItemList = new ArrayList<>();
        //选择 itemWidth 个能放的items
        for (int Id : remainingItemsId) {
            boolean canPack = false;
            for (int i = 1; i <= 6; i++) {
                //尝试六个转向
                Item tryItem = instance.getAllItems().get(Id).copyOfOrientation(i);
                for (ExtremePoint extremePoint : this.getExtremePointSet().getExtremePoints()) {
                    //尝试所有极端点
                    Position packPosition = new Position(instance.getAllItems().get(Id).getType(), extremePoint.getX(), extremePoint.getY(), extremePoint.getZ(),
                            tryItem.getP(), tryItem.getQ(), tryItem.getR());
                    if (check.canFit(this, packPosition)) {
                        canPack = true;
                        break;
                    }
                }
                if (canPack) break;
            }
            if (canPack) {
                packIdList.add(Id);
                toPackItemList.add(instance.getAllItems().get(Id));
                if (packIdList.size() >= itemWidth) break;
            }
        }
        if (packIdList.size() == 0) {
//            System.out.println("当前节点没有可放置的item-EP对，返回null");
            return null;
        } else {
            //6个朝向和所有EP 选择top max（beamwidth，6*EP）个作为子节点
            ArrayList<Node> expandedNodeList = new ArrayList<>(Configuration.beamWidth);
            ArrayList<Position> candidatePositionList = new ArrayList<>(Configuration.candidateInitialCapacity);
            for (int i = 0; i < packIdList.size(); i++) {//所有候选物品
                toPackItem = toPackItemList.get(i);
                for (int j = 1; j <= 6; j++) {
                    Item tryItem = toPackItem.copyOfOrientation(j);
                    for (ExtremePoint extremePoint : this.getExtremePointSet().getExtremePoints()) {
                        Position packPosition = new Position(toPackItem.getType(), toPackItem.getId(),
                                extremePoint.getX(), extremePoint.getY(), extremePoint.getZ(),
                                tryItem.getP(), tryItem.getQ(), tryItem.getR());
                        if (check.canFit(this, packPosition)) {
                            candidatePositionList.add(packPosition);
                            packPosition.setScore(scorer.calculateScore(this.getExtremePointSet(), packPosition));
                        }
                    }
                }
            }
            //评分
            candidatePositionList.sort(new PositionScoreComparator());
//            for (Position p:candidatePositionList){
//                System.out.println(p.score);
//            }
            for (int i = 0; i < Math.min(candidatePositionList.size(), beamWidth); i++) {
                Node newNode = new Node(this);
                if (candidatePositionList.get(i).getId() == -1) {
                    System.out.println("!!!!!!!!!!!!!");
                }
                newNode.update(candidatePositionList.get(i).getId(), candidatePositionList.get(i));
                newNode.setScore(candidatePositionList.get(i).getScore());
                expandedNodeList.add(newNode);
            }
            return expandedNodeList;
        }
    }

    public void update(int ItemId, Position packPosition) {
        //放入物品之后的update ，更新remainingitems，更新extremepoint,再 packedPositoin
//        System.out.println("移除前"+remainingItemsId);
//        System.out.println(getPackedPosition());
        this.remainingItemsId.remove((Integer) ItemId);
        this.packedItemsId.add(ItemId);
//        System.out.println("移除后"+ItemId+remainingItemsId);
        this.remainingItems = transformItemTypeToItems(remainingItemsId);
        this.extremePointSet.updateEPSetAfterPack(this.getPackedPosition(), packPosition);
        this.packedPosition.add(packPosition);

//        System.out.println(getPackedPosition());
    }

    private ArrayList<Item> transformItemTypeToItems(ArrayList<Integer> remainingItemsId) {
        ArrayList<Item> remainingItems = new ArrayList<>(Configuration.listInitialCapacity);
        for (int itemId : remainingItemsId) {
            remainingItems.add(instance.getAllItems().get(itemId));
        }
        return remainingItems;
    }

    public ArrayList<Integer> getMisFromLastNode(){
        ArrayList<Integer> itemsId= new ArrayList<>(packedItemsId.size()+1);
        itemsId.addAll(this.packedItemsId);
        itemsId.add(this.remainingItemsId.get(0));
        return  itemsId;
    }

    public ArrayList<Position> getPackedPosition() {
        return packedPosition;
    }

    public void setPackedPosition(ArrayList<Position> packedPosition) {
        this.packedPosition = packedPosition;
    }

    public ExtremePointSet getExtremePointSet() {
        return extremePointSet;
    }

    public void setExtremePointSet(ExtremePointSet extremePointSet) {
        this.extremePointSet = extremePointSet;
    }

    public Bag getBag() {
        return bag;
    }

    public void setBag(Bag bag) {
        this.bag = bag;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public ArrayList<Integer> getRemainingItemsId() {
        return remainingItemsId;
    }

    public void setRemainingItemsId(ArrayList<Integer> remainingItemsId) {
        this.remainingItemsId = remainingItemsId;
    }

    public ArrayList<Item> getRemainingItems() {
        return remainingItems;
    }

    public void setRemainingItems(ArrayList<Item> remainingItems) {
        this.remainingItems = remainingItems;
    }

    public Instance getInstance() {
        return instance;
    }

    public void setInstance(Instance instance) {
        this.instance = instance;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public ArrayList<Integer> getPackedItemsId() {
        return packedItemsId;
    }

    public void setPackedItemsId(ArrayList<Integer> packedItemsId) {
        this.packedItemsId = packedItemsId;
    }
}
