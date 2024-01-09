package model;

/**
 * @Author: Feng Jixuan
 * @Date: 2022-08-2022-08-21
 * @Description: BPP_Model
 * @version=1.0
 */


import ilog.concert.IloException;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumExpr;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class ChenModelv2 {
    public static ArrayList<Container> containers = new ArrayList<>();
    public static ArrayList<Box> boxes = new ArrayList<>();
    public static double ctMaxDim = 0;

    public static void Initialize(String url, String suanli) throws IOException {
        int pro_num = Integer.parseInt(suanli);
        BufferedReader br = new BufferedReader(new java.io.FileReader(
                "D:\\java\\BPP_Model\\data\\br\\" + url + ".txt"));
        String line;
        String[] splitLine;
        Box tempBox;
        int tol_pro = Integer.parseInt(br.readLine().trim());
        if (tol_pro < pro_num || pro_num < 1) {
            System.out.println("算例编号最小值为1，最大值为" + tol_pro);
        } else {
            while ((line = br.readLine()) != null) {
                splitLine = line.split("\\s+");
                if (splitLine.length == 3 && splitLine[1].equals(String.valueOf(pro_num))) {
                    splitLine = br.readLine().split("\\s+");
                    Container a = new Container();
                    a.l = Integer.parseInt(splitLine[1]);
                    a.h = Integer.parseInt(splitLine[2]);
                    a.w = Integer.parseInt(splitLine[3]);
                    ctMaxDim = Math.max(a.l, ctMaxDim);
                    ctMaxDim = Math.max(a.w, ctMaxDim);
                    ctMaxDim = Math.max(a.h, ctMaxDim);
                    containers.add(a);
                    int box_in = Integer.parseInt(br.readLine().trim());
                    for (int i = 0; i < box_in; i++) {
                        splitLine = br.readLine().split("\\s+");
                        tempBox = new Box(Integer.parseInt(splitLine[1]) - 1, Integer.parseInt(splitLine[2]),
                                Integer.parseInt(splitLine[4]), Integer.parseInt(splitLine[6]), Integer.parseInt(splitLine[8]));
                        System.out.println(Integer.parseInt(splitLine[1]) - 1 + ":" + splitLine[2] + " " + splitLine[4]
                                + " " + splitLine[6] + " " + splitLine[8]);
                        boxes.add(tempBox);
                    }
                    break;
                }
            }
        }
    }

    public static void buildModel(String url, String suanli) throws IloException {
        IloCplex cplex = new IloCplex();
        ArrayList<Container> allCt = new ArrayList<>();
        ArrayList<Box> allBox = new ArrayList<>();
        for (Container ct : containers) {
            for (int i = 0; i < ct.num; i++) {
                Container tempCt = new Container(ct.type, ct.l, ct.w, ct.h, ct.num);
                allCt.add(tempCt);
            }
        }
        for (Box box : boxes) {
            for (int i = 0; i < box.num; i++) {
                Box tempBox = new Box(box.type, box.p, box.q, box.r, 1);
                allBox.add(tempBox);
            }
        }
        int ctCount = allCt.size(), boxCount = allBox.size();
        IloNumVar[] n = new IloNumVar[ctCount];
        IloNumVar[] x = new IloNumVar[boxCount];
        IloNumVar[] y = new IloNumVar[boxCount];
        IloNumVar[] z = new IloNumVar[boxCount];
        IloNumVar[][] l = new IloNumVar[boxCount][3];
        IloNumVar[][] w = new IloNumVar[boxCount][3];
        IloNumVar[][] h = new IloNumVar[boxCount][3];
        IloNumVar[][] s = new IloNumVar[boxCount][ctCount];
        IloNumVar[][] a = new IloNumVar[boxCount][boxCount];
        IloNumVar[][] b = new IloNumVar[boxCount][boxCount];
        IloNumVar[][] c = new IloNumVar[boxCount][boxCount];
        IloNumVar[][] d = new IloNumVar[boxCount][boxCount];
        IloNumVar[][] e = new IloNumVar[boxCount][boxCount];
        IloNumVar[][] f = new IloNumVar[boxCount][boxCount];
        double M1 = 0, M2 = 0, M3 = 0, M4 = 0, M5 = 0, M6 = 0, M9 = boxCount + 1, M10 = 0, M11 = 0, M12 = 0;
        for (Container ct : containers) {
            M1 = Math.max(M1, ct.l);
            M2 = M10 = M1;
            M3 = Math.max(M3, ct.w);
            M4 = M11 = M3;
            M5 = Math.max(M5, ct.h);
            M6 = M12 = M5;
        }
//        M1 = M2 = M3 = M4 = M5 = M6 = M9 = M10 = M11 = M12 = 10000000;
//        cplex.setParam(	IloCplex.Param.Preprocessing.Presolve,false);
//        cplex.setParam(	IloCplex.Param.MIP.Cuts.Cliques,1);
//        cplex.setParam(IloCplex.Param.Threads, 1);
        for (int i = 0; i < ctCount; i++) {
            n[i] = cplex.intVar(0, 1, "n_" + i);
        }
        for (int i = 0; i < boxCount; i++) {
            x[i] = cplex.numVar(0, ctMaxDim, "x_" + i);
            y[i] = cplex.numVar(0, ctMaxDim, "y_" + i);
            z[i] = cplex.numVar(0, ctMaxDim, "z_" + i);
        }
        for (int i = 0; i < boxCount; i++) {
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
        for (int i = 0; i < boxCount; i++) {
            for (int j = 0; j < ctCount; j++) {
                s[i][j] = cplex.intVar(0, 1, "s_" + i + "," + j);
            }
        }
        for (int i = 0; i < boxCount; i++) {
            for (int j = i + 1; j < boxCount; j++) {
                a[i][j] = cplex.intVar(0, 1, "a_" + i + "," + j);
                b[i][j] = cplex.intVar(0, 1, "b_" + i + "," + j);
                c[i][j] = cplex.intVar(0, 1, "c_" + i + "," + j);
                d[i][j] = cplex.intVar(0, 1, "d_" + i + "," + j);
                e[i][j] = cplex.intVar(0, 1, "r_" + i + "," + j);
                f[i][j] = cplex.intVar(0, 1, "f_" + i + "," + j);
            }
        }
        //约束
        for (int i = 0; i < boxCount; i++) {
            for (int k = i + 1; k < boxCount; k++) {
                IloNumExpr con1 = cplex.numExpr();
                con1 = cplex.sum(con1, x[i], cplex.prod(allBox.get(i).p, l[i][0]), cplex.prod(allBox.get(i).q, w[i][0]), cplex.prod(allBox.get(i).r, h[i][0]));
                cplex.addLe(con1, cplex.sum(x[k], cplex.prod(cplex.diff(1, a[i][k]), M1)), "xi_" + i + "," + k);

                IloNumExpr con2 = cplex.numExpr();
                con2 = cplex.sum(con2, x[k], cplex.prod(allBox.get(k).p, l[k][0]), cplex.prod(allBox.get(k).q, w[k][0]), cplex.prod(allBox.get(k).r, h[k][0]));
                cplex.addLe(con2, cplex.sum(x[i], cplex.prod(cplex.diff(1, b[i][k]), M2)), "xk_" + i + "," + k);

                IloNumExpr con3 = cplex.numExpr();
                con3 = cplex.sum(con3, y[i], cplex.prod(allBox.get(i).p, l[i][1]), cplex.prod(allBox.get(i).q, w[i][1]), cplex.prod(allBox.get(i).r, h[i][1]));
                cplex.addLe(con3, cplex.sum(y[k], cplex.prod(cplex.diff(1, c[i][k]), M3)), "yi_" + i + "," + k);

                IloNumExpr con4 = cplex.numExpr();
                con4 = cplex.sum(con4, y[k], cplex.prod(allBox.get(k).p, l[k][1]), cplex.prod(allBox.get(k).q, w[k][1]), cplex.prod(allBox.get(k).r, h[k][1]));
                cplex.addLe(con4, cplex.sum(y[i], cplex.prod(cplex.diff(1, d[i][k]), M4)), "yk_" + i + "," + k);

                IloNumExpr con5 = cplex.numExpr();
                con5 = cplex.sum(con5, z[i], cplex.prod(allBox.get(i).p, l[i][2]), cplex.prod(allBox.get(i).q, w[i][2]), cplex.prod(allBox.get(i).r, h[i][2]));
                cplex.addLe(con5, cplex.sum(z[k], cplex.prod(cplex.diff(1, e[i][k]), M5)), "zi_" + i + "," + k);

                IloNumExpr con6 = cplex.numExpr();
                con6 = cplex.sum(con6, z[k], cplex.prod(allBox.get(k).p, l[k][2]), cplex.prod(allBox.get(k).q, w[k][2]), cplex.prod(allBox.get(k).r, h[k][2]));
                cplex.addLe(con6, cplex.sum(z[i], cplex.prod(cplex.diff(1, f[i][k]), M6)), "zk_" + i + "," + k);

                if(allBox.get(i).type==allBox.get(k).type){
//                    b[i][k].setUB(0);
//                    d[i][k].setUB(0);
//                    f[i][k].setUB(0);
                    cplex.addLe(cplex.sum(a[i][k],b[i][k]),1);
                    cplex.addLe(cplex.sum(c[i][k],d[i][k]),1);
                    cplex.addLe(cplex.sum(e[i][k],f[i][k]),1);
                    System.out.println("对称!!!!!!!!!!!!!!!"+i+" "+k);
                }
            }
        }
        for (int i = 0; i < boxCount; i++) {
            for (int k = i + 1; k < boxCount; k++) {
                for (int j = 0; j < ctCount; j++) {
                    IloNumExpr con7 = cplex.numExpr();
                    con7 = cplex.sum(con7, a[i][k], b[i][k], c[i][k], d[i][k], e[i][k], f[i][k]);
                    con7 = cplex.sum(con7, 1);
                    cplex.addGe(con7, cplex.sum(s[i][j], s[k][j]), "position_" + "," + i + "," + j + "," + k);
                }
            }
        }
        for (int i = 0; i < boxCount; i++) {
            IloLinearNumExpr con8 = cplex.linearNumExpr();
            for (int j = 0; j < ctCount; j++) {
                con8.addTerm(1, s[i][j]);
            }
            cplex.addEq(con8, 1, "ct_and_box" + i);
        }
        for (int j = 0; j < ctCount; j++) {
            IloLinearNumExpr con9 = cplex.linearNumExpr();
            for (int i = 0; i < boxCount; i++) {
                con9.addTerm(1, s[i][j]);
            }
            cplex.addLe(con9, cplex.prod(M9, n[j]), "ctUsed_" + j);
        }
        for (int i = 0; i < boxCount; i++) {
            for (int j = 0; j < ctCount; j++) {
                IloNumExpr con10 = cplex.numExpr();
                con10 = cplex.sum(x[i], cplex.prod(allBox.get(i).p, l[i][0]), cplex.prod(allBox.get(i).q, cplex.sum(cplex.diff(l[i][2], w[i][1]), h[i][2])),
                        cplex.prod(allBox.get(i).r, cplex.diff(cplex.sum(1, w[i][1]), cplex.sum(l[i][0], l[i][2], h[i][2]))));
                cplex.addLe(con10, cplex.sum(allCt.get(j).l, cplex.prod(cplex.diff(1, s[i][j]), M10)), "length_" + i + "," + j);

                IloNumExpr con11 = cplex.numExpr();
                con11 = cplex.sum(y[i], cplex.prod(allBox.get(i).p, cplex.diff(cplex.diff(1, l[i][0]), l[i][2])), cplex.prod(allBox.get(i).q, w[i][1]),
                        cplex.prod(allBox.get(i).r, cplex.diff(cplex.sum(l[i][0], l[i][2]), w[i][1])));
                cplex.addLe(con11, cplex.sum(allCt.get(j).w, cplex.prod(cplex.diff(1, s[i][j]), M11)), "width_" + i + "," + j);

                IloNumExpr con12 = cplex.numExpr();
                con12 = cplex.sum(z[i], cplex.prod(allBox.get(i).p, l[i][2]), cplex.prod(allBox.get(i).q, cplex.diff(cplex.diff(1, l[i][2]), h[i][2])), cplex.prod(allBox.get(i).r, h[i][2]));
                cplex.addLe(con12, cplex.sum(allCt.get(j).h, cplex.prod(cplex.diff(1, s[i][j]), M12)), "height_" + i + "," + j);
            }
        }
        for (int i = 0; i < boxCount; i++) {
            cplex.addEq(cplex.sum(l[i][0], l[i][1], l[i][2]), 1);
            cplex.addEq(cplex.sum(w[i][0], w[i][1], w[i][2]), 1);
            cplex.addEq(cplex.sum(h[i][0], h[i][1], h[i][2]), 1);
            cplex.addEq(cplex.sum(l[i][0], w[i][0], h[i][0]), 1);
            cplex.addEq(cplex.sum(l[i][1], w[i][1], h[i][1]), 1);
            cplex.addEq(cplex.sum(l[i][2], w[i][2], h[i][2]), 1);
        }
        for (int j = 0; j < ctCount - 1; j++) {
            if (allCt.get(j).type == allCt.get(j + 1).type) {
                cplex.addGe(n[j], n[j + 1]);
            }
        }
        IloLinearNumExpr obj = cplex.linearNumExpr();
        for (int j = 0; j < ctCount; j++) {
            obj.addTerm(1, n[j]);
        }
        cplex.addMinimize(obj);
//        cplex.exportModel("lp/ChenBinM" + url + "_" + suanli + ".lp");
//        System.exit(0);
        long start = System.currentTimeMillis();
        boolean success = cplex.solve();
        long end = System.currentTimeMillis();

        if (success) {
            System.out.println(cplex.getObjValue());
            System.out.println("n:" + Arrays.toString(cplex.getValues(n)));
            double[] xx, yy, zz, lx, ly, lz, ss;
            xx = cplex.getValues(x);
            yy = cplex.getValues(y);
            zz = cplex.getValues(z);
            lx = new double[boxCount];
            ly = new double[boxCount];
            lz = new double[boxCount];
            ss = new double[boxCount];

            for (int i = 0; i < boxCount; i++) {
                for (int j = 0; j < ctCount; j++) {
                    if (cplex.getValue(s[i][j]) > 0.9) {
                        ss[i] = j;
                    }
                }
//                System.out.println(Arrays.toString(cplex.getValues(s[i])));
            }
            for (int i = 0; i < boxCount; i++) {
                double[] ll = new double[3];
                double[] ww = new double[3];
                double[] hh = new double[3];
                ll[0] = cplex.getValue(l[i][0]);
                ll[2] = cplex.getValue(l[i][2]);
                ll[1] = 1 - ll[0] - ll[2];
                ww[1] = cplex.getValue(w[i][1]);
                hh[2] = cplex.getValue(h[i][2]);
                ww[0] = ll[2] - ww[1] + hh[2];
                ww[2] = 1 - ww[0] - ww[1];
                hh[1] = ll[0] + ll[2] - ww[1];
                hh[0] = 1 - hh[1] - hh[2];
                System.out.print("l:" + Arrays.toString(ll));
                System.out.print("w:" + Arrays.toString(ww));
                System.out.print("h:" + Arrays.toString(hh));
                System.out.println();
                if (ll[0] > 0.9) {
                    lx[i] = allBox.get(i).p;
                } else {
                    if (ww[0] > 0.9) {
                        lx[i] = allBox.get(i).q;
                    } else {
                        lx[i] = allBox.get(i).r;
                    }
                }
                if (ll[1] > 0.9) {
                    ly[i] = allBox.get(i).p;
                } else {
                    if (ww[1] > 0.9) {
                        ly[i] = allBox.get(i).q;
                    } else {
                        ly[i] = allBox.get(i).r;
                    }
                }
                if (ll[2] > 0.9) {
                    lz[i] = allBox.get(i).p;
                } else {
                    if (ww[2] > 0.9) {
                        lz[i] = allBox.get(i).q;
                    } else {
                        lz[i] = allBox.get(i).r;
                    }
                }

            }
            System.out.println("s:" + Arrays.toString(ss));
            System.out.println("x:" + Arrays.toString(xx));
            System.out.println("y:" + Arrays.toString(yy));
            System.out.println("z:" + Arrays.toString(zz));
            System.out.println("lx:" + Arrays.toString(lx));
            System.out.println("ly:" + Arrays.toString(ly));
            System.out.println("lz:" + Arrays.toString(lz));

        } else {
            System.out.println(cplex.getStatus());
        }
        System.out.println("cplex solve time:" + (end - start) + "ms");
//        for (int i = 0; i < boxes.size(); i++) {
//            System.out.println(boxes.get(i).p+" "+boxes.get(i).q+" "+boxes.get(i).r+" "+boxes.get(i).num);
//        }
//        System.out.println(boxCount);
//        System.out.println(ctCount);
    }

    public static void main(String[] args) throws IOException, IloException {
        Initialize(args[0], args[1]);
        buildModel(args[0], args[1]);
    }
}


