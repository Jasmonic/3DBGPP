package singleBag;

import baseObject.*;
import comparator.BagFaceItem;
import comparator.NodeScoreComparator;
import comparator.VolumeHeightItem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

/**
 * @Author: Feng Jixuan
 * @Date: 2022-12-2022-12-12
 * @Description: BPP_Model
 * @version=1.0
 */
public class BeamSearch {
    public  boolean successToPack = false;
    public Node lastNode;
    public ArrayList<Node> nodesOfLastLevel;
    public BeamSearch() {
        nodesOfLastLevel = null;
    }

    public  void pack(Instance instance, Bag bag, ArrayList<Integer> remainingItemsId) {
        //对Item排序
//        System.out.println("排序前" + remainingItemsId);
        ArrayList<Item> remainingItems = new ArrayList<>(remainingItemsId.size());
        for (int Id : remainingItemsId) {
            remainingItems.add(instance.getAllItems().get(Id));
        }
        switch (Configuration.sortItem){
            case 0:remainingItems.sort(new VolumeHeightItem()); break;
            case 1:remainingItems.sort(new BagFaceItem()); break;
            default: Collections.shuffle(remainingItems); break;
        }
        for (int i = 0; i < remainingItems.size(); i++) {
            remainingItemsId.set(i,remainingItems.get(i).getId())  ;
        }
//        remainingItemsId.clear();
//        for (Item item : remainingItems) {
//            remainingItemsId.add(item.getId());
//        }
        System.out.println("排序后" + remainingItemsId);
        ArrayList<Node> nodeList = new ArrayList<>();
        Node node0 = new Node(bag, remainingItemsId, instance);
        nodeList.add(node0);
        int count = 0;
        boolean isTerminated = false;
        Node successNode = null;
        lastNode=node0;
        int w = Configuration.beamWidth;
        int itemWidth=Configuration.itemWidth;

        while (!isTerminated) {
//            System.out.println("——————————————————————————————————————————第" + count + "层——————————————————————————————————————");
            ArrayList<Node> newNodeList = new ArrayList<>(Configuration.beamWidth);
            for (Node node : nodeList) {
//                printNode(node);
                int c=0;
//                ArrayList<Node> expandedNodeList = node.expand(w, instance.getScorer(), node.getRemainingItemsId());
                ArrayList<Node> expandedNodeList = node.expand2(w, itemWidth,instance.getScorer(), node.getRemainingItemsId());
                if (expandedNodeList != null) {
                    newNodeList.addAll(expandedNodeList);
                }
            }
//            System.out.println(nodeList.size());
            if (newNodeList.size() > 0) {
                nodesOfLastLevel = newNodeList;
                nodeList.clear();
                newNodeList.sort(new NodeScoreComparator());
                for (int i = 0; i < Math.min(newNodeList.size(), w); i++) {
                    nodeList.add(newNodeList.get(i));
                }
                lastNode= newNodeList.get(0);
                if (remainingItems.size() == newNodeList.get(0).getPackedPosition().size()) {
                    this.successToPack = true;
                    successNode = newNodeList.get(0);
                    isTerminated = true;
                    break;
                }
            } else {
                this.successToPack = false;
                isTerminated = true;
                break;
            }
            count++;
        }
        if (this.successToPack) {
            System.out.println("BeamSearch 装完了");
//            for (Position position : successNode.getPackedPosition()) {
//                System.out.println(position);
//            }
        } else {
            System.out.println("装不完，装了" + count);
        }
    }

    public static void printNode(Node node) {
        System.out.println("==========START=============");
        for (Position position : node.getPackedPosition()) {
            System.out.println(position);
        }
        for (ExtremePoint extremePoint : node.getExtremePointSet().getExtremePoints()) {
            System.out.println(extremePoint);
        }
        System.out.println("装载物品"+node.getPackedItemsId());
        System.out.println("剩余物品" + node.getRemainingItemsId());
        System.out.println("==========END==============");
    }

    public static void main(String[] args) throws IOException {
        System.out.println("执行main程序");
        Instance instance = new Instance();
        instance.initialze(args[0]);
//        Bag bag=instance.getAllBags().get(38);//Bags().get(1);
//        bag=instance.getBags().get(1);
         Bag bag = instance.getBags().get(1);
        System.out.println(bag);
//        Integer[]items2={0, 1, 3, 4, 5, 6, 7, 8, 9, 10, 12, 13, 14, 15, 16, 18};
//        ArrayList<Integer> remainingItemsId = new ArrayList<>(Arrays.asList(items2));
//        [0, 1, 3, 4, 5, 6, 7, 8, 9, 10, 12, 13, 14, 15, 16, 18]
        ArrayList<Integer> remainingItemsId = new ArrayList<>();
        for (int i = 0; i < instance.getAllItems().size(); i++) {
            remainingItemsId.add(i);
        }

//        assert false;
        BeamSearch beamSearch =new BeamSearch();
        beamSearch.pack(instance, bag, remainingItemsId);
    }

    public void init(){
        successToPack = false;
        lastNode=null;
        nodesOfLastLevel = null;
    }

    public ArrayList<Node> getNodesOfLastLevel() {
        return nodesOfLastLevel;
    }
}

