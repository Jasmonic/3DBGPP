package baseObject;


import benders.Pattern;
import scorer.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @Author: Feng Jixuan
 * @Date: 2022-10-2022-10-21
 * @Description: BPP_Model
 * @version=1.0
 */
public class Instance {
    private int bagTypeCount, itemTypeCount, bagCount, itemCount;
    private final ArrayList<Bag> bags, allBags;
    private final ArrayList<Item> items, allItems;
    private PositionScorer scorer;
    HashMap<Pattern, Boolean> isPatternFeasible;
    HashMap<Pattern, ArrayList<Position>> patternToPositions;
    HashMap<Pattern, ArrayList<Integer>> infeasiblePatternToMIS;


    long start = System.currentTimeMillis();
    ArrayList<Long> timeList;
    ArrayList<Double> objList;
    ArrayList<Boolean> statusList;
    HashMap<TwoTuple<Bag, Bag>, Bag> mergeBagMap;


    public Instance() {
        bags = new ArrayList<>();
        items = new ArrayList<>();
        allBags = new ArrayList<>();
        allItems = new ArrayList<>();
        isPatternFeasible = new HashMap<>();
        patternToPositions = new HashMap<>();
        infeasiblePatternToMIS = new HashMap<>();
        timeList = new ArrayList<>();
        objList = new ArrayList<>();
        statusList = new ArrayList<>();
        timeList.add(0L);
        objList.add((double) 0);
        statusList.add(false);

    }

    public void setCount() {
        bagTypeCount = bags.size();
        itemTypeCount = items.size();
        bagCount = allBags.size();
        itemCount = allItems.size();
    }


    public void initialze(String suanli) throws IOException {
        ArrayList<Bag> bags = this.getBags();
        ArrayList<Item> items = this.getItems();
        BufferedReader br = new BufferedReader(new FileReader("./data/allData/" + suanli + ".csv"));
        String line;
        String[] splitLine;
        Item tempItem;
        int count=1;
        line = br.readLine();
        System.out.println(line);
        int bagType= Integer.parseInt(line);
        for (int i = 0; i < bagType; i++) {
            splitLine=br.readLine().split(",");
            Bag tempBag = new Bag(Integer.parseInt(splitLine[0]) - 1, Integer.parseInt(splitLine[1]),
                    Integer.parseInt(splitLine[2]), Double.parseDouble(splitLine[3]), Integer.parseInt(splitLine[4]));
            bags.add(tempBag);
        }
        line = br.readLine();
        int itemType= Integer.parseInt(line);
        for (int i = 0; i < itemType; i++) {
            splitLine = br.readLine().split(",");
            tempItem = new Item(Integer.parseInt(splitLine[0]) - 1, Integer.parseInt(splitLine[1]), Integer.parseInt(splitLine[2]),
                    Integer.parseInt(splitLine[3]), Integer.parseInt(splitLine[4]));
            items.add(tempItem);
        }


//        int tol_pro = Integer.parseInt(br.readLine().trim());
//        if (tol_pro < pro_num || pro_num < 1) {
//            System.out.println("算例编号最小值为1，最大值为" + tol_pro);
//        } else {
//            while ((line = br.readLine()) != null) {
//                splitLine = line.split("\\s+");
//                if (splitLine.length == 3 && splitLine[1].equals(String.valueOf(pro_num))) {
//                    int bag_in = Integer.parseInt(br.readLine().trim());
//                    for (int i = 0; i < bag_in; i++) {
//                        splitLine = br.readLine().split("\\s+");
//                        Bag tempBag = new Bag(Integer.parseInt(splitLine[1]) - 1, Integer.parseInt(splitLine[2]),
//                                Integer.parseInt(splitLine[3]), Double.parseDouble(splitLine[4]), Integer.parseInt(splitLine[5]));
//                        bags.add(tempBag);
//                    }
//                    int item_in = Integer.parseInt(br.readLine().trim());
//                    for (int i = 0; i < item_in; i++) {
//                        splitLine = br.readLine().split("\\s+");
//                        tempItem = new Item(Integer.parseInt(splitLine[1]) - 1, Integer.parseInt(splitLine[2]), Integer.parseInt(splitLine[3]),
//                                Integer.parseInt(splitLine[4]), Integer.parseInt(splitLine[5]));
//                        items.add(tempItem);
//                    }
//                    break;
//                }
//            }
//        }
        switch (Configuration.positionScorer) {
            case 0:
                this.setScorer(new Envelope());
                break;
            case 1:
                this.setScorer(new BagFace());
                break;
            case 2:
                this.setScorer(new EnvelopeUtilization());
                break;
            case 3:
                this.setScorer(new EnvelopeUtilizationBagFace());
                break;
            case 4:
                this.setScorer(new Envelope());
                break;
            case 5:
                this.setScorer(new AbsoluteItem());
                break;
        }
        this.itemTransformToOneDimension();
        this.setCount();
    }

    public void itemTransformToOneDimension() {
        for (Item item : items) {
            for (int i = 0; i < item.getNum(); i++) {
                Item tempitem = new Item(item.getType(), item.getP(), item.getQ(), item.getR(), 1);
                allItems.add(tempitem);
            }
        }
        for (int i = 0; i < getAllItems().size(); i++) {
            getAllItems().get(i).setId(i);
        }
        for (Item item : allItems) {
            item.setLengthInOrder();
            item.setItemCopies();
        }
        for (Bag bag:bags){
            bag.setNum(allItems.size());
        }
    }

    public void bagTransformToOneDimension() {
        for (Bag bag : bags) {
            for (int i = 0; i < bag.getNum(); i++) {
                Bag tempBag = new Bag(bag.getType(), bag.getX(), bag.getY(), bag.getMaxVolume(), bag.getCost(), 1);
                allBags.add(tempBag);
            }
        }
    }

    public void preprocessMergeBagMap() {
        mergeBagMap = new HashMap<>();
        for (Bag b1 : bags) {
            for (Bag b2 : bags) {
                for (int i = bags.size() - 1; i >= 0; i--) {
                    if ((b1.getCost() + b2.getCost() > bags.get(i).getCost()) && (bags.get(i).getCost() > b1.getCost()) && (bags.get(i).getCost() > b2.getCost())) {
                        mergeBagMap.put(new TwoTuple<>(b1, b2), bags.get(i));
                        break;
                    }
                }
            }
        }
    }

    public ArrayList<Bag> getBags() {
        return bags;
    }


    public int getBagTypeCount() {
        return bagTypeCount;
    }


    public int getBagCount() {
        return bagCount;
    }


    public ArrayList<Bag> getAllBags() {
        return allBags;
    }

    public int getItemTypeCount() {
        return itemTypeCount;
    }

    public int getItemCount() {
        return itemCount;
    }

    public ArrayList<Item> getItems() {
        return items;
    }

    public ArrayList<Item> getAllItems() {
        return allItems;
    }

    public PositionScorer getScorer() {
        return scorer;
    }

    public void setScorer(PositionScorer scorer) {
        this.scorer = scorer;
    }

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public ArrayList<Long> getTimeList() {
        return timeList;
    }

    public String countToString() {
        return "Instance{" +
                "bagTypeCount=" + bagTypeCount +
                ", itemTypeCount=" + itemTypeCount +
                ", bagCount=" + bagCount +
                ", itemCount=" + itemCount +
                '}';
    }

    public HashMap<Pattern, Boolean> getIsPatternFeasible() {
        return isPatternFeasible;
    }

    public HashMap<Pattern, ArrayList<Position>> getPatternToPositions() {
        return patternToPositions;
    }

    public HashMap<TwoTuple<Bag, Bag>, Bag> getMergeBagMap() {
        return mergeBagMap;
    }

    public HashMap<Pattern, ArrayList<Integer>> getInfeasiblePatternToMIS() {
        return infeasiblePatternToMIS;
    }

    public void getFinalResult(double[][] s) {
        System.out.println("=========pattern2position=======");
        for (int j = 0; j < s[0].length; j++) {
            double n_j = 0;
            for (int i = 0; i < s.length; i++) {
                n_j += s[i][j];
            }
            if (n_j > 0.5) {
                ArrayList<Integer> items = new ArrayList<>();
                for (int i = 0; i < s.length; i++) {
                    if (s[i][j] > 0.5) {
                        items.add(i);
                    }
                }
                Pattern pattern = new Pattern(this, items, j);
                ArrayList<Position> ps = patternToPositions.get(pattern);
                for (Position p1 : ps) {
                    for (Position p2 : ps)
                        if (p1 != p2) {
                            assert (!check.isPositionCollide(p1, p2)) : p1.toString() + "||" + p2.toString();
                        }
                }
                for (Position p : ps) {
                    assert (!check.isPositionOutOfBag(p, getAllBags().get(j))) : p.toString() + "||" + getAllBags().get(j).toString();
                }
                System.out.println("use bag type :" + getAllBags().get(j).getType() + " ;  cost :" + getAllBags().get(j).getCost());
                System.out.println("Feasible Checked");
                for (Position p : ps) {
                    System.out.println(p);
                }

            }
        }
    }

    public void recordObj(long time, double obj, boolean status) {
        timeList.add(time);
        objList.add(obj);
        statusList.add(status);
    }
}
