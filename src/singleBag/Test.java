package singleBag;

import baseObject.Bag;
import baseObject.Item;
import baseObject.Position;
import baseObject.check;
import comparator.AreaHeightItemComparator;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * @Author: Feng Jixuan
 * @Date: 2022-12-2022-12-15
 * @Description: BPP_Model
 * @version=1.0
 */
public class Test {
    public static void main(String[] args) {
        Node node1 = new Node();

        Bag bag1 = new Bag(1, 370, 330, 0.4, 1);
        ExtremePointSet test = new ExtremePointSet(bag1);
        node1.setPackedPosition(new ArrayList<>());
        node1.setBag(bag1);
        node1.setExtremePointSet(test);

        Position box1 = new Position(0, 0, 0, 0, 1, 2, 3);
        System.out.println(node1.extremePointSet.getExtremePoints());
        node1.extremePointSet.updateEPSetAfterPack(node1.getPackedPosition(), box1);
        System.out.println("box1");
        System.out.println(node1.extremePointSet.getExtremePoints());

        Position box2 = new Position(0, 1, 0, 0, 1, 2, 3);
        node1.extremePointSet.updateEPSetAfterPack(node1.getPackedPosition(), box2);
        System.out.println(node1.extremePointSet.getExtremePoints());

        Position box3 = new Position(0, 2, 0, 0, 3, 3, 3);
        node1.extremePointSet.updateEPSetAfterPack(node1.getPackedPosition(), box3);
        System.out.println(node1.extremePointSet.getExtremePoints());

        Item item1 = new Item(0, 1, 2, 3, 1);
        for (int i = 1; i < 6; i++) {
            System.out.println(item1.copyOfOrientation(i));
        }
        System.out.println("——————————————————————————————————————");
        node1 = new Node();
        bag1.setX(440);
        bag1.setY(410);
        test = new ExtremePointSet(bag1);
        node1.setPackedPosition(new ArrayList<>());
        node1.setBag(bag1);
        node1.setExtremePointSet(test);
        //TODO 测试订单8 EP-FirstFeed
        Item[] items1 = {
                new Item(0, 200, 200, 50, 1),
                new Item(0, 200, 200, 50, 1),
                new Item(1, 200, 110, 40, 1),
                new Item(2, 10, 10, 10, 1),
        };
        Item[] items2={
                new Item(0, 70, 70, 240, 1),
                new Item(1, 50, 50, 210, 1),
                new Item(1, 50, 50, 210, 1),
                new Item(2, 280, 220, 70, 1),
                new Item(3, 230, 50, 80, 1),
                new Item(4, 200, 70, 170, 1),
        };
        Arrays.sort(items2, new AreaHeightItemComparator());
        ArrayList<Item> itemList = new ArrayList<>(Arrays.asList(items2));
        for (Item item : itemList) {
            System.out.println(item.toString());
        }
        System.out.println("初始极端点:" + node1.getExtremePointSet().getExtremePoints());
        System.out.println(node1.getExtremePointSet().bag);
        boolean canPack;
        while (!itemList.isEmpty()) {
//            assert  false;
            canPack = false;
            Item packItem = null;
            for (Item toPackItem : itemList) {
                //尝试所有物品
                for (int i = 1; i <=6; i++) {
                    //尝试六个转向
                    Item tryItem = toPackItem.copyOfOrientation(i);
                    for (ExtremePoint extremePoint : node1.getExtremePointSet().getExtremePoints()) {
                        //尝试所有极端点
                        Position packPosition = new Position(toPackItem.getType(), extremePoint.getX(), extremePoint.getY(), extremePoint.getZ(),
                                tryItem.getP(), tryItem.getQ(), tryItem.getR());
                        if (check.canFit(node1, packPosition)) {
                            canPack = true;

                            node1.extremePointSet.updateEPSetAfterPack(node1.getPackedPosition(), packPosition);
                            node1.getPackedPosition().add(packPosition);
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
                itemList.remove(packItem);
            else {
                System.out.println("装不完，还剩" + itemList);
                break;
            }
        }
        System.out.println("已经装进去的position"+node1.packedPosition);
        System.out.println(node1.getExtremePointSet().getMaxX()+"+"+node1.getExtremePointSet().getMaxZ()+"<="+node1.getBag().getX());
        System.out.println(node1.getExtremePointSet().getMaxY()+"+"+node1.getExtremePointSet().getMaxZ()+"<="+node1.getBag().getY());
        System.out.println("=========================测试订单10=======================");
        node1 = new Node();
        bag1.setX(440);
        bag1.setY(410);
        test = new ExtremePointSet(bag1);
        node1.setPackedPosition(new ArrayList<>());
        node1.setBag(bag1);
        node1.setExtremePointSet(test);
        Position p0=new Position(0,0,0,70,70,240,70);
        Position p1=new Position(1,70,0,70,50,210,50);
        Position p2=new Position(1,90,210,80,210,50,50);
        Position p3=new Position(2,0,0,0,280,220,70);
        Position p4=new Position(3,70,220,0,230,50,80);
        Position p5=new Position(4,130,0,70,170,200,70);

        node1.extremePointSet.updateEPSetAfterPack(node1.getPackedPosition(), p3);
        node1.getPackedPosition().add(p3);
        for (ExtremePoint extremePoint:node1.extremePointSet.getExtremePoints()){
            System.out.println(extremePoint);
        }
        System.out.println("-------------");

        node1.extremePointSet.updateEPSetAfterPack(node1.getPackedPosition(), p0);
        node1.getPackedPosition().add(p0);
        for (ExtremePoint extremePoint:node1.extremePointSet.getExtremePoints()){
            System.out.println(extremePoint);
        }
        System.out.println("-------------");
        System.out.println(node1.getExtremePointSet().getMaxX()+"?? "+node1.getExtremePointSet().getMaxY()+" "+node1.getExtremePointSet().getMaxZ());
        node1.extremePointSet.updateEPSetAfterPack(node1.getPackedPosition(), p1);
        node1.getPackedPosition().add(p1);
        for (ExtremePoint extremePoint:node1.extremePointSet.getExtremePoints()){
            System.out.println(extremePoint);
        }
        System.out.println("-------------");
        System.out.println(node1.getExtremePointSet().getMaxX()+"?? "+node1.getExtremePointSet().getMaxY()+" "+node1.getExtremePointSet().getMaxZ());

        System.out.println(check.canFit(node1,p4));

//        EP_FFD.pack(node1.instance, node1.bag, node1.remainingItemsId);
        System.out.println("=====================");

    }
}
