package bagpackingModel;

import baseObject.*;
import ilog.concert.IloException;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumExpr;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: Feng Jixuan
 * @Date: 2022-08-2022-08-31
 * @Description: BPP_Model
 * @version=1.0
 */

public class ChenBagWithCost {
    public static ArrayList<Bag> bags = new ArrayList<>();
    public static ArrayList<Item> items = new ArrayList<>();
    public static String name;


    public static void buildModel( String suanli, String method, Instance instance) throws IloException, IOException {
        IloCplex cplex = new IloCplex();
        ArrayList<Bag> allBags = instance.getAllBags();
        ArrayList<Item> allItems = instance.getAllItems();
        bags = instance.getBags();
        items = instance.getItems();
        int bagCount = allBags.size(), itemCount = allItems.size();
        IloNumVar[] n = new IloNumVar[bagCount];
        IloNumVar[] x = new IloNumVar[itemCount];
        IloNumVar[] y = new IloNumVar[itemCount];
        IloNumVar[] z = new IloNumVar[itemCount];
        IloNumVar[][] l = new IloNumVar[itemCount][3];
        IloNumVar[][] w = new IloNumVar[itemCount][3];
        IloNumVar[][] h = new IloNumVar[itemCount][3];
        IloNumVar[][] s = new IloNumVar[itemCount][bagCount];
        IloNumVar[][] a = new IloNumVar[itemCount][itemCount];
        IloNumVar[][] b = new IloNumVar[itemCount][itemCount];
        IloNumVar[][] c = new IloNumVar[itemCount][itemCount];
        IloNumVar[][] d = new IloNumVar[itemCount][itemCount];
        IloNumVar[][] e = new IloNumVar[itemCount][itemCount];
        IloNumVar[][] f = new IloNumVar[itemCount][itemCount];
//        IloNumVar[][] q = new IloNumVar[itemCount][bagCount];
        IloNumVar[] L = new IloNumVar[bagCount];
        IloNumVar[] W = new IloNumVar[bagCount];
        IloNumVar[] H = new IloNumVar[bagCount];
        double M1 = 0, M2 = 0, M3 = 0, M4 = 0, M5 = 0, M6 = 0, M9 = itemCount + 1, M10 = 0, M11 = 0, M12 = 0;
        double maxDim = 0;
//        M1 = M2 = M3 = M4 = M5 = M6 = M9 = M10 = M11 = M12 = 10000;
        for (Bag bag : bags) {
            M1 = M2 = M10 = Math.max(M10, bag.getX());
            M3 = M4 = M11 = Math.max(M11, bag.getY());
            M5 = M6 = M12 = Math.max(Math.max(M12, M10), M11);
        }
        System.out.println(M1 + " " + M2 + " " + M3 + " " + M4 + " " + M5 + " " + M6 + " " + M9 + " " + M10 + " " + M11 + " " + M12);

        for (int i = 0; i < bagCount; i++) {
            n[i] = cplex.intVar(0, 1, "n_" + i);
            double dim = Math.max(allBags.get(i).getX(), allBags.get(i).getY());
            maxDim = Math.max(maxDim, dim);
            L[i] = cplex.numVar(0, dim, "L_" + i);
            W[i] = cplex.numVar(0, dim, "W_" + i);
            H[i] = cplex.numVar(0, dim, "H_" + i);
        }
        for (int i = 0; i < itemCount; i++) {
            x[i] = cplex.numVar(0, maxDim, "x_" + i);
            y[i] = cplex.numVar(0, maxDim, "y_" + i);
            z[i] = cplex.numVar(0, maxDim, "z_" + i);
        }
        for (int i = 0; i < itemCount; i++) {
            l[i][0] = cplex.intVar(0, 1, "lx_" + i);
            l[i][1] = cplex.intVar(0, 1, "ly_" + i);
            l[i][2] = cplex.intVar(0, 1, "lz_" + i);
            w[i][0] = cplex.intVar(0, 1, "wx_" + i);
            w[i][1] = cplex.intVar(0, 1, "wy_" + i);
            w[i][2] = cplex.intVar(0, 1, "wz_" + i);
            h[i][0] = cplex.intVar(0, 1, "hx_" + i);
            h[i][1] = cplex.intVar(0, 1, "hy_" + i);
            h[i][2] = cplex.intVar(0, 1, "hz_" + i);
        }
        for (int i = 0; i < itemCount; i++) {
            for (int j = 0; j < bagCount; j++) {
                s[i][j] = cplex.intVar(0, 1, "s_" + i + "," + j);
            }
        }
        for (int i = 0; i < itemCount; i++) {
            for (int j = i + 1; j < itemCount; j++) {
                a[i][j] = cplex.intVar(0, 1, "a_" + i + "," + j);
                b[i][j] = cplex.intVar(0, 1, "b_" + i + "," + j);
                c[i][j] = cplex.intVar(0, 1, "c_" + i + "," + j);
                d[i][j] = cplex.intVar(0, 1, "d_" + i + "," + j);
                e[i][j] = cplex.intVar(0, 1, "r_" + i + "," + j);
                f[i][j] = cplex.intVar(0, 1, "f_" + i + "," + j);
            }
        }
        //约束
        for (int i = 0; i < itemCount; i++) {
            for (int k = i + 1; k < itemCount; k++) {
                IloNumExpr con1 = cplex.numExpr();
                con1 = cplex.sum(con1, x[i], cplex.prod(allItems.get(i).getP(), l[i][0]), cplex.prod(allItems.get(i).getQ(), w[i][0]), cplex.prod(allItems.get(i).getR(), h[i][0]));
                cplex.addLe(con1, cplex.sum(x[k], cplex.prod(cplex.diff(1, a[i][k]), M1)), "xi_" + i + "," + k);

                IloNumExpr con2 = cplex.numExpr();
                con2 = cplex.sum(con2, x[k], cplex.prod(allItems.get(k).getP(), l[k][0]), cplex.prod(allItems.get(k).getQ(), w[k][0]), cplex.prod(allItems.get(k).getR(), h[k][0]));
                cplex.addLe(con2, cplex.sum(x[i], cplex.prod(cplex.diff(1, b[i][k]), M2)), "xk_" + i + "," + k);

                IloNumExpr con3 = cplex.numExpr();
                con3 = cplex.sum(con3, y[i], cplex.prod(allItems.get(i).getP(), l[i][1]), cplex.prod(allItems.get(i).getQ(), w[i][1]), cplex.prod(allItems.get(i).getR(), h[i][1]));
                cplex.addLe(con3, cplex.sum(y[k], cplex.prod(cplex.diff(1, c[i][k]), M3)), "yi_" + i + "," + k);

                IloNumExpr con4 = cplex.numExpr();
                con4 = cplex.sum(con4, y[k], cplex.prod(allItems.get(k).getP(), l[k][1]), cplex.prod(allItems.get(k).getQ(), w[k][1]), cplex.prod(allItems.get(k).getR(), h[k][1]));
                cplex.addLe(con4, cplex.sum(y[i], cplex.prod(cplex.diff(1, d[i][k]), M4)), "yk_" + i + "," + k);

                IloNumExpr con5 = cplex.numExpr();
                con5 = cplex.sum(con5, z[i], cplex.prod(allItems.get(i).getP(), l[i][2]), cplex.prod(allItems.get(i).getQ(), w[i][2]), cplex.prod(allItems.get(i).getR(), h[i][2]));
                cplex.addLe(con5, cplex.sum(z[k], cplex.prod(cplex.diff(1, e[i][k]), M5)), "zi_" + i + "," + k);

                IloNumExpr con6 = cplex.numExpr();
                con6 = cplex.sum(con6, z[k], cplex.prod(allItems.get(k).getP(), l[k][2]), cplex.prod(allItems.get(k).getQ(), w[k][2]), cplex.prod(allItems.get(k).getR(), h[k][2]));
                cplex.addLe(con6, cplex.sum(z[i], cplex.prod(cplex.diff(1, f[i][k]), M6)), "zk_" + i + "," + k);
            }
        }
        for (int i = 0; i < itemCount; i++) {
            for (int k = i + 1; k < itemCount; k++) {
                for (int j = 0; j < bagCount; j++) {
                    IloNumExpr con7 = cplex.numExpr();
                    con7 = cplex.sum(con7, a[i][k], b[i][k], c[i][k], d[i][k], e[i][k], f[i][k]);
                    con7 = cplex.sum(con7, 1);
                    cplex.addGe(con7, cplex.sum(s[i][j], s[k][j]), "position_" + "," + i + "," + j + "," + k);
                }
            }
        }
        for (int i = 0; i < itemCount; i++) {
            IloLinearNumExpr con8 = cplex.linearNumExpr();
            for (int j = 0; j < bagCount; j++) {
                con8.addTerm(1, s[i][j]);
            }
            cplex.addEq(con8, 1, "bag_and_item" + i);
        }
        for (int j = 0; j < bagCount; j++) {
            IloLinearNumExpr con9 = cplex.linearNumExpr();
            for (int i = 0; i < itemCount; i++) {
                con9.addTerm(1, s[i][j]);
            }
            cplex.addLe(con9, cplex.prod(M9, n[j]), "bagUsed_" + j);
        }
        for (int i = 0; i < itemCount; i++) {
            for (int j = 0; j < bagCount; j++) {
                IloNumExpr con10 = cplex.numExpr();
                con10 = cplex.sum(x[i], cplex.prod(allItems.get(i).getP()*0.75, l[i][0]), cplex.prod(allItems.get(i).getQ()*0.75, w[i][0]), cplex.prod(allItems.get(i).getR()*0.75, h[i][0]));
                cplex.addLe(con10, cplex.sum(L[j], cplex.prod(cplex.diff(1, s[i][j]), M10)), "length_" + i + "," + j);

                IloNumExpr con11 = cplex.numExpr();
                con11 = cplex.sum(y[i], cplex.prod(allItems.get(i).getP(), l[i][1]), cplex.prod(allItems.get(i).getQ(), w[i][1]), cplex.prod(allItems.get(i).getR(), h[i][1]));
                cplex.addLe(con11, cplex.sum(W[j], cplex.prod(cplex.diff(1, s[i][j]), M11)), "width_" + i + "," + j);
                IloNumExpr con12 = cplex.numExpr();
                con12 = cplex.sum(z[i], cplex.prod(allItems.get(i).getP(), l[i][2]), cplex.prod(allItems.get(i).getQ(), w[i][2]), cplex.prod(allItems.get(i).getR(), h[i][2]));
                cplex.addLe(con12, cplex.sum(H[j], cplex.prod(cplex.diff(1, s[i][j]), M12)), "height_" + i + "," + j);
            }
        }
        for (int i = 0; i < itemCount; i++) {
            cplex.addEq(cplex.sum(l[i][0], l[i][1], l[i][2]), 1);
            cplex.addEq(cplex.sum(w[i][0], w[i][1], w[i][2]), 1);
            cplex.addEq(cplex.sum(h[i][0], h[i][1], h[i][2]), 1);
            cplex.addEq(cplex.sum(l[i][0], w[i][0], h[i][0]), 1);
            cplex.addEq(cplex.sum(l[i][1], w[i][1], h[i][1]), 1);
            cplex.addEq(cplex.sum(l[i][2], w[i][2], h[i][2]), 1);
        }
        //袋子变形约束
        for (int i = 0; i < bagCount; i++) {
            cplex.addLe(cplex.sum(L[i], H[i]), allBags.get(i).getX(), "transformX_" + i);
            cplex.addLe(cplex.sum(W[i], H[i]), allBags.get(i).getY(), "transformY_" + i);
        }
        //袋子对称cut
        for (int j = 0; j < bagCount - 1; j++) {
            if (allBags.get(j).getType() == allBags.get(j + 1).getType()) {
                cplex.addGe(n[j], n[j + 1], "bagCut" + j + "," + (j + 1));
            }
        }
        IloLinearNumExpr obj = cplex.linearNumExpr();
        for (int j = 0; j < bagCount; j++) {
            obj.addTerm(allBags.get(j).getCost(), n[j]);
        }

        cplex.addMinimize(obj);
        //Advanced cut
        if (ModelParam.itemRelativePositionCut) {
            for (int i = 0; i < itemCount; i++) {
                for (int k = i + 1; k < itemCount; k++) {
                    if (allItems.get(i).getType() == allItems.get(k).getType()) {
                        b[i][k].setUB(0);
                        d[i][k].setUB(0);
                        f[i][k].setUB(0);
//                        System.out.println("对称!!!!!!!!!!!!!!!" + i + " " + k);
                    }
                }
            }
        }
        //袋子和同类物品cut
        if (ModelParam.itemOrderCut) {
            for (int i = 0; i < itemCount - 1; i++) {
                if (allItems.get(i).getType() == allItems.get(i + 1).getType()) {
                    for (int j = 0; j < bagCount; j++) {
                        IloLinearNumExpr con13 = cplex.linearNumExpr();
                        for (int r = 0; r <= j; r++) {
                            con13.addTerm(1, s[i][r]);
                        }
                        cplex.addGe(con13, s[i + 1][j], "itemOrderCut" + i + "," + j);
                    }
                }
            }
        }
        // c 不能用
        if (method.contains("c")) {
            int cur = 0;
            for (int i = 0; i < items.size(); i++) {
                double totalVolume = items.get(i).getVolume() * items.get(i).getNum();
                for (int j = 0; j < allBags.size(); j++) {
                    if (totalVolume > allBags.get(j).getMaxVolume()) {
                        IloLinearNumExpr cutC = cplex.linearNumExpr();
                        for (int k = cur; k < cur + items.get(i).getNum(); k++) {
                            cutC.addTerm(1, s[k][j]);
                        }
                        System.out.println(i + " " + j + " " + "CCCCCCCCCCCCCCCCC");
//                        System.out.println(Math.floor(allBags.get(j).getMaxVolume()/itemes.get(i).getVolume()));

//                        cplex.addLe(cutC,Math.floor(allBags.get(j).getMaxVolume()/itemes.get(i).getVolume()),"CutC_"+i+"_"+j);
                        if (Math.floor(allBags.get(j).getMaxVolume() / items.get(i).getVolume()) == 0) {
                            for (int k = cur; k < cur + items.get(i).getNum(); k++) {
                                s[k][j].setUB(0);
                            }
                        } else {
                            cplex.addLe(cutC, cplex.prod(Math.floor(allBags.get(j).getMaxVolume() / items.get(i).getVolume()), n[j]), "CutC_" + i + "_" + j);
                        }

                    }
                }
                System.out.print(cur + ",");
                cur += items.get(i).getNum();
                System.out.println(cur);
            }
        }
        //d也不能用
        if (method.contains("d")) {
            for (int j = 0; j < bagCount; j++) {
                IloLinearNumExpr cutD = cplex.linearNumExpr();
                for (int i = 0; i < itemCount; i++) {
                    cutD.addTerm(allItems.get(i).getVolume(), s[i][j]);
                }
                cplex.addLe(cutD, cplex.prod(allBags.get(j).getMaxVolume(), n[j]));
            }
        }
        cplex.addLe(H[0],W[0]);
        if (method.contains("e")) {
            String outputPath = "./output/part1/" + ModelParam.outPutFile + "/lp/"  + suanli;
            if (ModelParam.itemRelativePositionCut) outputPath = outputPath + "_" + "Pos";
            if (ModelParam.itemOrderCut) outputPath = outputPath + "_" + "Or";

//            cplex.exportModel(outputPath + ".lp");
        }
        //cplex param
//        cplex.setParam(IloCplex.Param.Threads, 1);
        cplex.setParam(IloCplex.Param.TimeLimit, 7200);
//        cplex.setOut(new FileOutputStream("./output/part1/"+ModelParam.outPutFile+"/cplexLog/"+suanli+"_7200s.txt"));

        long start = System.currentTimeMillis();
        boolean success = cplex.solve();
        long end = System.currentTimeMillis();


//        System.out.println("cplex solve time:" + (end - start) + "ms");
        System.out.println("cplex solve time:" + (end - start) * 1.0 / 1000 + "s");
        if (success) {
            System.out.println(cplex.getObjValue());
            System.out.println("n:" + Arrays.toString(cplex.getValues(n)));
            double[] xx, yy, zz, lx, ly, lz, LL, WW, HH, length, width, height;
            int[] ss = new int[itemCount];
            xx = cplex.getValues(x);
            yy = cplex.getValues(y);
            zz = cplex.getValues(z);
            lx = new double[itemCount];
            ly = new double[itemCount];
            lz = new double[itemCount];
            length = new double[itemCount];
            width = new double[itemCount];
            height = new double[itemCount];
            LL = cplex.getValues(L);
            WW = cplex.getValues(W);
            HH = cplex.getValues(H);
            int[] bagNo = new int[itemCount];
            for (int i = 0; i < itemCount; i++) {
                for (int j = 0; j < bagCount; j++) {
                    if (cplex.getValue(s[i][j]) > 0.9) {
                        ss[i] = allBags.get(j).getType();
                        bagNo[i] = j;
                    }
                }
            }
            for (int i = 0; i < itemCount; i++) {
                length[i] = LL[bagNo[i]];
                width[i] = WW[bagNo[i]];
                height[i] = HH[bagNo[i]];
            }
            for (int i = 0; i < itemCount; i++) {
                double[] ll = cplex.getValues(l[i]);
                double[] ww = cplex.getValues(w[i]);
                double[] hh = cplex.getValues(h[i]);
                System.out.print("l:" + Arrays.toString(ll));
                System.out.print("w:" + Arrays.toString(ww));
                System.out.print("h:" + Arrays.toString(hh));
                System.out.println();
                if (ll[0] > 0.9) {
                    lx[i] = allItems.get(i).getP();
                } else {
                    if (ww[0] > 0.9) {
                        lx[i] = allItems.get(i).getQ();
                    } else {
                        lx[i] = allItems.get(i).getR();
                    }
                }
                if (ll[1] > 0.9) {
                    ly[i] = allItems.get(i).getP();
                } else {
                    if (ww[1] > 0.9) {
                        ly[i] = allItems.get(i).getQ();
                    } else {
                        ly[i] = allItems.get(i).getR();
                    }
                }
                if (ll[2] > 0.9) {
                    lz[i] = allItems.get(i).getP();
                } else {
                    if (ww[2] > 0.9) {
                        lz[i] = allItems.get(i).getQ();
                    } else {
                        lz[i] = allItems.get(i).getR();
                    }
                }

            }
            System.out.println("No.:" + Arrays.toString(bagNo));
            System.out.println("type:" + Arrays.toString(ss));
            System.out.println("L:" + Arrays.toString(length));
            System.out.println("W:" + Arrays.toString(width));
            System.out.println("H:" + Arrays.toString(height));
            System.out.println("x:" + Arrays.toString(xx));
            System.out.println("y:" + Arrays.toString(yy));
            System.out.println("z:" + Arrays.toString(zz));
            System.out.println("lx:" + Arrays.toString(lx));
            System.out.println("ly:" + Arrays.toString(ly));
            System.out.println("lz:" + Arrays.toString(lz));
            ArrayList<Integer> ansBags = new ArrayList<>();
            ArrayList<Bag> ansBagsInfo = new ArrayList<>();
            ArrayList<ArrayList<Position>> allPs = new ArrayList<>();
            Map<Integer, Integer> map = new HashMap<>(); //bagNO 到 allPs的下标
            for (int i = 0; i < itemCount; i++) {
                boolean in = false;
                for (Integer type : ansBags) {
                    if (type == bagNo[i]) {
                        in = true;
                        break;
                    }
                }
                if (!in) {
                    ansBags.add(bagNo[i]);
                    ArrayList<Position> ps = new ArrayList<>();
                    allPs.add(ps);
                    Bag tempBag = new Bag(ss[i], length[i], width[i], height[i], bags.get(ss[i]).getCost());
                    tempBag.setX(bags.get(tempBag.getType()).getX());
                    tempBag.setY(bags.get(tempBag.getType()).getY());
                    ansBagsInfo.add(tempBag);
                    map.put(bagNo[i], allPs.size() - 1);
                }
            }

            System.out.println(map);
            for (int i = 0; i < itemCount; i++) {
                Position position = new Position(allItems.get(i).getType(), (int) Math.round(xx[i]), (int) Math.round(yy[i]), (int) Math.round(zz[i]),
                        (int) Math.round(lx[i]), (int) Math.round(ly[i]), (int) Math.round(lz[i]));
                position.setId(allItems.get(i).getId());
                position.setType(allItems.get(i).getType());
                allPs.get(map.get(bagNo[i])).add(position);
            }
            System.out.println(ansBagsInfo);
            for (ArrayList<Position> ps : allPs) {
                System.out.println(ps);
            }
            assert (ansBagsInfo.size() == allPs.size()) : "袋子数量！=allPs数量";

            for (ArrayList<Position> ps : allPs) {
                for (Position p1 : ps) {
                    for (Position p2 : ps) {
                        if (p1 != p2) {
                            assert (!check.isPositionCollide(p1, p2)) : p1.toString() + "||" + p2.toString();
                        }
                    }
                }
            }
            System.out.println("————————————————————————————————ssss");
            for (int i = 0; i < itemCount; i++) {
                for (int j = 0; j < bagCount; j++) {
                    if (cplex.getValue(s[i][j]) > 0.9) {
                        System.out.println("s_" + i + "," + j + "=1");
                    }
                }
            }
            resultPositionToCsv(suanli, ansBagsInfo, allPs);


//            PrintStream out = System.out;
//            PrintStream prs = new PrintStream("result_qiedan\\result2_" + url + "_" + suanli + "_" + method + ".txt");
//            System.setOut(prs);
//            System.out.println(ansBagsInfo.size());
//            for (int i = 0; i < ansBagsInfo.size(); i++) {
//                System.out.println("use " + i + " bag");
//                System.out.println(ansBagsInfo.get(i).getType() + " " + ansBagsInfo.get(i).getL() + " " + ansBagsInfo.get(i).getW() + " " + ansBagsInfo.get(i).getH() + " " + ansBagsInfo.get(i).getCost());
//                System.out.println(allPs.get(i).size());
//                for (Position position : allPs.get(i)) {
//                    System.out.println(position.getType() + " " + position.x + " " + position.y + " " + position.z + " " + position.lx + " " + position.ly + " " + position.lz);
//                }
//            }
//            prs.close();
//            System.setOut(out);

//            FileWriter fw = new FileWriter("result_qiedan\\fenkai.txt",true);
//            fw.write("order"+suanli+"\n");
//            fw.write();
//            fw.close();
        } else {
            System.out.println(cplex.getStatus());
        }
//        resultSolvwToCsv(suanli, end - start, cplex);

//        FileWriter fw = new FileWriter("bishe\\part1\\originalModel.txt", true);
//        fw.write(suanli + " " + cplex.getStatus());
//        if (success) {
//            fw.write(" " + cplex.getObjValue());
//        }
//        //写入换行
//        fw.write(" " + (end - start) * 1.0 / 1000 + "s");
//        fw.write("\n");//Windows平台下用\r\n，Linux/Unix平台下用\n
//        fw.close();

    }

    public static void resultPositionToCsv(String suanli, ArrayList<Bag> ansBagsInfo, ArrayList<ArrayList<Position>> allPs) {
        String csv_path = "./output/part1/" + ModelParam.outPutFile + "/positions/" + suanli + "_case_study.csv";
        File writeFile = new File(csv_path);
        try {
            //第二步：通过BufferedReader类创建一个使用默认大小输出缓冲区的缓冲字符输出流
            BufferedWriter writeText = new BufferedWriter(new FileWriter(writeFile));
            double cost = 0;
            for (int i = 0; i < ansBagsInfo.size(); i++) {
                cost += ansBagsInfo.get(i).getCost();
            }
            writeText.write("number of bags," + ansBagsInfo.size() + ",total cost," + cost);
            writeText.newLine();
            for (int i = 0; i < ansBagsInfo.size(); i++) {
                writeText.write("bagType," + ansBagsInfo.get(i).getType() + ",bagCost," + ansBagsInfo.get(i).getCost()
                        + ",X," + ansBagsInfo.get(i).getX() + ",Y," + ansBagsInfo.get(i).getY());
                writeText.newLine();
                for (int j = 0; j < allPs.get(i).size(); j++) {
                    Position position = allPs.get(i).get(j);
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

    public static void resultSolvwToCsv(String suanli, long time, IloCplex cplex) throws IloException {
        String csv_path = "./output/part1/" + ModelParam.outPutFile + "/statistic_7200.csv";
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

    public static void main(String[] args) throws IOException, IloException {
        Instance instance = new Instance();
        instance.initialze( args[0]);
        instance.bagTransformToOneDimension();
        instance.setCount();
//        System.out.println(instance.getBags());
//        System.out.println(instance.getAllBags());
//        System.out.println(instance.getBagCount());
//        System.out.println(instance.getBagTypeCount());
//        System.out.println(args[1]+" "+instance.getItemCount());
        System.out.println(instance.getItems());
        System.out.println(instance.getBags());
//        boolean duichen=false;
//        for (Item item: instance.getItems()) {
//            if (item.getNum()>1){
//                duichen=true;
//            }
//        }
//        if (duichen){
//            System.out.println("对称");
//        }else{
//            System.out.println("不对称");
//        }
//        System.exit(0);
        String method;
        if (args.length < 2) {
            method = "0";
        } else {
            method = args[1];
        }
//        FileWriter fw = new FileWriter("bishe\\part1\\originalModel.txt",true);
//        fw.write(args[1]+" "+instance.getItemCount()+" "+instance.getAllItems().size());
//        fw.write("\n");//Windows平台下用\r\n，Linux/Unix平台下用\n
//        fw.close();
        buildModel(args[0], method, instance);
    }
}
