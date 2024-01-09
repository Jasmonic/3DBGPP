package benders;


import baseObject.Bag;
import baseObject.Instance;
import baseObject.Position;
import baseObject.check;
import ilog.concert.IloException;
import ilog.cplex.IloCplex;


import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

/**
 * @Author: Feng Jixuan
 * @Date: 2023-02-2023-02-03
 * @Description: BPP_Model
 * @version=1.0
 */
public class Result {
    //    HashMap<Pattern, ArrayList<Position>> resultsPatternToPositions;
    ArrayList<ArrayList<Position>> resultsPositions;
    ArrayList<Bag> resultsBags;
    Instance instance;
    MasterProblem master;

    public Result(Instance instance, MasterProblem master) {
        this.instance = instance;
        this.master = master;
        resultsPositions = new ArrayList<>();
        resultsBags = new ArrayList<>();
//        this.patternToPositions = instance.getPatternToPositions();
    }

    public void parseResults() throws IloException {
        int itemCount = instance.getItemCount(), bagCount = instance.getBagCount();
        double[][] s = master.getSs();
//        double [] n= master.getSn();
        double[] n = new double[instance.getBagCount()];
        for (int j = 0; j < s[0].length; j++) {
            n[j] = 0;
            for (int i = 0; i < s.length; i++) {
                n[j] += s[i][j];
            }
        }
        HashMap<Pattern, ArrayList<Position>> patternToPositions = instance.getPatternToPositions();
        double cost = 0;
        for (int j = 0; j < s[0].length; j++) {
            if (n[j] > 0.5) {
                ArrayList<Integer> itemsIds = new ArrayList<>(s.length);
                for (int i = 0; i < s.length; i++) {
                    if (s[i][j] > 0.5) {
                        itemsIds.add(i);
                    }
                }
                Pattern pattern = new Pattern(instance, itemsIds, j);
                if (!patternToPositions.containsKey(pattern)) {
                    throw new IllegalArgumentException("result中找不到想要的pattern");
                } else {
                    resultsPositions.add(patternToPositions.get(pattern));
                    resultsBags.add(instance.getAllBags().get(j));
                    cost = cost + instance.getAllBags().get(j).getCost();
                }
            }
        }
    }

    public void printResults() {
        for (int i = 0; i < resultsPositions.size(); i++) {
            System.out.println(resultsBags.get(i).getCost());
            for (Position position : resultsPositions.get(i)) {
                System.out.println(position);
            }
        }
    }
    public void resultsToCsv(String suanli){
        for (int i = 0; i < resultsPositions.size(); i++) {
            System.out.println(resultsBags.get(i).getCost());
            for (Position position : resultsPositions.get(i)) {
                System.out.println(position);
            }
        }
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("_yyyy_MM_dd_HH_mm_ss");
        String csv_path = "./output/part2/BS" + "/positions/" + suanli + "_bw500"+formatter.format(date)+".csv";
        File writeFile = new File(csv_path);
        try {
            //第二步：通过BufferedReader类创建一个使用默认大小输出缓冲区的缓冲字符输出流
            BufferedWriter writeText = new BufferedWriter(new FileWriter(writeFile));
            double cost = 0;
            for (int i = 0; i < resultsBags.size(); i++) {
                cost += resultsBags.get(i).getCost();
            }
            writeText.write("number of bags," + resultsPositions.size() + ",total cost," + cost);
            writeText.newLine();
            for (int i = 0; i < resultsPositions.size(); i++) {
                writeText.write("bagType," + resultsBags.get(i).getType() + ",bagCost," + resultsBags.get(i).getCost()
                        + ",X," + resultsBags.get(i).getX() + ",Y," + resultsBags.get(i).getY());
                writeText.newLine();
                for (int j = 0; j < resultsPositions.get(i).size(); j++) {
                    Position position = resultsPositions.get(i).get(j);
                    writeText.write(position.getType() + "," + position.x + "," + position.y + "," + position.z + "," + position.lx + "," + position.ly + "," + position.lz);
                    writeText.newLine();
                }
            }
            //使用缓冲区的刷新方法将数据刷到目的地中
            writeText.flush();
            //关闭缓冲区，缓冲区没有调用系统底层资源，真正调用底层资源的是FileWriter对象，缓冲区仅仅是一个提高效率的作用,因此，此处的close()方法关闭的是被缓存的流对象
            writeText.close();
        } catch (FileNotFoundException e) {
            System.out.println("没有找到指定文件");
        } catch (IOException e) {
            System.out.println("文件读写出错");
        }
    }

    public  void resultSolvwToCsv(String suanli, long time, IloCplex cplex) throws IloException {
        String csv_path = "./output/part2/BS" + "/statistic_bw500.csv";
        File writeFile = new File(csv_path);
        try {
            //第二步：通过BufferedReader类创建一个使用默认大小输出缓冲区的缓冲字符输出流
            BufferedWriter writeText = new BufferedWriter(new FileWriter(writeFile,true));
            writeText.write(suanli+","+time * 1.0 / 1000 + "s,"+cplex.getStatus());

            if (cplex.getStatus()!= IloCplex.Status.Unknown){
                writeText.write(","+cplex.getObjValue()+","+cplex.getMIPRelativeGap());
            }
            writeText.newLine();
            writeText.flush();
            writeText.close();
        } catch (FileNotFoundException e) {
            System.out.println("没有找到指定文件");
        } catch (IOException e) {
            System.out.println("文件读写出错");
        }
    }

    public boolean checkIsResultFeasible() throws IloException {
        int[] checkItemsCount= new int[instance.getItemTypeCount()];
        double cost = 0;
        for (int i = 0; i < resultsPositions.size(); i++) {
            Bag tempBag = resultsBags.get(i);
            cost = cost + tempBag.getCost();
            for (Position position : resultsPositions.get(i)) {
                if (check.isPositionOutOfBag(position, tempBag)) {
                    System.out.println("物品超出边界");
                    return false;
                }
            }
            for (Position p1 : resultsPositions.get(i)) {
                for (Position p2 : resultsPositions.get(i)) {
                    if (p1 != p2) {
                        if (check.isPositionCollide(p1, p2)) {
                            System.out.println("物品有重合");
                            return false;
                        }
                    }
                }
            }
            for (Position position: resultsPositions.get(i)){
                checkItemsCount[position.getType()]++;
            }
        }
        for (int i = 0; i < instance.getItemTypeCount(); i++) {
            if (checkItemsCount[i]!= instance.getItems().get(i).getNum()) {
                System.out.println("物品个数对不上");
                return false;
            }
        }
        if (Math.abs(cost-master.getObjective())>=10E-6) {
            System.out.println("总成本出错");
            return false;
        }

        return true;
    }
}
