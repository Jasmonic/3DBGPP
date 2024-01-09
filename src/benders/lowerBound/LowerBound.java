package benders.lowerBound;

import baseObject.Bag;
import baseObject.Instance;
import baseObject.Item;
import ilog.concert.IloException;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumExpr;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;

import java.util.ArrayList;

/**
 * @Author: Feng Jixuan
 * @Date: 2023-02-2023-02-07
 * @Description: BPP_Model
 * @version=1.0
 */
public class LowerBound {
    public Instance instance;

    public LowerBound(Instance instance) {
        this.instance = instance;
    }
    public double calLB() throws IloException {
        IloCplex cplex = new IloCplex();
        int bagCount=instance.getBagCount();
        int itemCount= instance.getItemCount();
        ArrayList<Bag> allBags= instance.getAllBags();
        ArrayList<Item> allItems= instance.getAllItems();
        System.out.println("===Bags====");
        System.out.println(instance.getBags());

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
        IloNumVar[] L = new IloNumVar[bagCount];
        IloNumVar[] W = new IloNumVar[bagCount];
        IloNumVar[] H = new IloNumVar[bagCount];
        double M1 = 0, M2 = 0, M3 = 0, M4 = 0, M5 = 0, M6 = 0, M9 = itemCount + 1, M10 = 0, M11 = 0, M12 = 0;
        double maxDim = 0;
//        M1 = M2 = M3 = M4 = M5 = M6 = M9 = M10 = M11 = M12 = 10000;
        for (Bag bag : instance.getBags()) {
            M1 = M2 = M10 = Math.max(M10, bag.getX());
            M3 = M4 = M11 = Math.max(M11, bag.getY());
            M5 = M6 = M12 = Math.max(Math.max(M12, M10), M11);
        }
        System.out.println(M1 + " " + M2 + " " + M3 + " " + M4 + " " + M5 + " " + M6 + " " + M9 + " " + M10 + " " + M11 + " " + M12);

//        cplex.setParam(	IloCplex.Param.Preprocessing.Presolve,false);
        for (int i = 0; i < bagCount; i++) {
            n[i] = cplex.numVar(0, 1, "n_" + i);
            double dim = Math.max(allBags.get(i).getX(), allBags.get(i).getY());
            maxDim = Math.max(maxDim, dim);
            L[i] = cplex.numVar(0, dim, "L_" + i);
            W[i] = cplex.numVar(0, dim, "W_" + i);
            H[i] = cplex.numVar(0, allBags.get(i).getY() /2, "H_" + i);
        }
        for (int i = 0; i < itemCount; i++) {
            x[i] = cplex.numVar(0, maxDim, "x_" + i);
            y[i] = cplex.numVar(0, maxDim, "y_" + i);
            z[i] = cplex.numVar(0, maxDim, "z_" + i);
        }
        for (int i = 0; i < itemCount; i++) {
            l[i][0] = cplex.numVar(0, 1, "lx_" + i);
            l[i][1] = cplex.numVar(0, 1, "ly_" + i);
            l[i][2] = cplex.numVar(0, 1, "lz_" + i);
            w[i][0] = cplex.numVar(0, 1, "wx_" + i);
            w[i][1] = cplex.numVar(0, 1, "wy_" + i);
            w[i][2] = cplex.numVar(0, 1, "wz_" + i);
            h[i][0] = cplex.numVar(0, 1, "hx_" + i);
            h[i][1] = cplex.numVar(0, 1, "hy_" + i);
            h[i][2] = cplex.numVar(0, 1, "hz_" + i);
        }
        for (int i = 0; i < itemCount; i++) {
            for (int j = 0; j < bagCount; j++) {
                s[i][j] = cplex.numVar(0, 1, "s_" + i + "," + j);
            }
        }
        for (int i = 0; i < itemCount; i++) {
            for (int j = i + 1; j < itemCount; j++) {
                a[i][j] = cplex.numVar(0, 1, "a_" + i + "," + j);
                b[i][j] = cplex.numVar(0, 1, "b_" + i + "," + j);
                c[i][j] = cplex.numVar(0, 1, "c_" + i + "," + j);
                d[i][j] = cplex.numVar(0, 1, "d_" + i + "," + j);
                e[i][j] = cplex.numVar(0, 1, "r_" + i + "," + j);
                f[i][j] = cplex.numVar(0, 1, "f_" + i + "," + j);
            }
        }
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
            IloLinearNumExpr cutD = cplex.linearNumExpr();
            for (int i = 0; i < itemCount; i++) {
                cutD.addTerm(allItems.get(i).getVolume(), s[i][j]);
            }
            cplex.addLe(cutD, cplex.prod(allBags.get(j).getMaxVolume(), n[j]));
        }
        for (int i = 0; i < itemCount; i++) {
            for (int j = 0; j < bagCount; j++) {
                IloNumExpr con10 = cplex.numExpr();
                con10 = cplex.sum(x[i], cplex.prod(allItems.get(i).getP(), l[i][0]), cplex.prod(allItems.get(i).getQ(), w[i][0]), cplex.prod(allItems.get(i).getR(), h[i][0]));
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
        for (int i = 0; i < itemCount; i++) {
            for (int k = i + 1; k < itemCount; k++) {
                if (allItems.get(i).getType() == allItems.get(k).getType()) {
                    b[i][k].setUB(0);
                    d[i][k].setUB(0);
                    f[i][k].setUB(0);
                    System.out.println("对称!!!!!!!!!!!!!!!" + i + " " + k);
                }
            }
        }
        for (int i = 0; i < itemCount - 1; i++) {
            if (allItems.get(i).getType() == allItems.get(i + 1).getType()) {
                for (int j = 0; j < bagCount; j++) {
                    IloLinearNumExpr con13 = cplex.linearNumExpr();
                    for (int r = 0; r <= j; r++) {
                        con13.addTerm(1, s[i][r]);
                    }
                    cplex.addGe(con13, s[i + 1][j], "itemCut" + i + "," + j);
                }
            }
        }
        long start = System.currentTimeMillis();
        boolean success = cplex.solve();
        long end = System.currentTimeMillis();

        return cplex.getObjValue();

    }
}
