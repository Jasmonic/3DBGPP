package benders;


import bagpackingModel.ModelParam;
import baseObject.*;
import ilog.concert.*;
import ilog.cp.*;
import ilog.cplex.IloCplex;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * @Author: Feng Jixuan
 * @Date: 2022-10-2022-10-23
 * @Description: BPP_Model
 * @version=1.0
 */
public class SubProblem {
    private final IloCplex sub;
    private final Instance instance;
    IloNumVar[] x, y, z;
    IloNumVar L, W, H;
    IloNumVar[][] a, b, c, d, e, f, l, w, h;

    public SubProblem(Instance instance) throws IloException {
        sub = new IloCplex();
        this.instance = instance;
    }

    public void BuildChenModel(ArrayList<Integer> itemsId, int bagId) throws IloException, FileNotFoundException {
        int itemsCount = itemsId.size();
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("_yyyy_MM_dd_HH_mm_ss");
//        sub.setOut(null);
        sub.setOut(new FileOutputStream("./output/part2/BS/lp/"+formatter.format(date)+".txt"));
        ArrayList<Bag> allBags = instance.getAllBags();
        ArrayList<Item> allItems = instance.getAllItems();
        if (Configuration.subProblemThreadNum > 0) {
            sub.setParam(IloCplex.Param.Threads, Configuration.subProblemThreadNum);
        }
        x = new IloNumVar[itemsCount];
        y = new IloNumVar[itemsCount];
        z = new IloNumVar[itemsCount];
        a = new IloNumVar[itemsCount][itemsCount];
        b = new IloNumVar[itemsCount][itemsCount];
        c = new IloNumVar[itemsCount][itemsCount];
        d = new IloNumVar[itemsCount][itemsCount];
        e = new IloNumVar[itemsCount][itemsCount];
        f = new IloNumVar[itemsCount][itemsCount];
        l = new IloNumVar[itemsCount][3];
        w = new IloNumVar[itemsCount][3];
        h = new IloNumVar[itemsCount][3];
        double Mx = allBags.get(bagId).getX(), My = allBags.get(bagId).getY(), Mz = Math.max(allBags.get(bagId).getX(), allBags.get(bagId).getY());
        for (int i = 0; i < itemsCount; i++) {
            x[i] = sub.numVar(0, Mx, "x_" + i);
            y[i] = sub.numVar(0, My, "y_" + i);
            z[i] = sub.numVar(0, Mz, "z_" + i);
        }
        L = sub.numVar(0, Mx, "L");
        W = sub.numVar(0, My, "W");
        H = sub.numVar(0, Mz, "H");
        for (int i = 0; i < itemsCount - 1; i++) {
            for (int j = i + 1; j < itemsCount; j++) {
                a[i][j] = sub.intVar(0, 1, "a_" + i + "," + j);
                b[i][j] = sub.intVar(0, 1, "b_" + i + "," + j);
                c[i][j] = sub.intVar(0, 1, "c_" + i + "," + j);
                d[i][j] = sub.intVar(0, 1, "d_" + i + "," + j);
                e[i][j] = sub.intVar(0, 1, "r_" + i + "," + j);
                f[i][j] = sub.intVar(0, 1, "f_" + i + "," + j);
            }
        }
        for (int i = 0; i < itemsCount; i++) {
            l[i][0] = sub.intVar(0, 1, "lx_" + i);
            l[i][1] = sub.intVar(0, 1, "ly_" + i);
            l[i][2] = sub.intVar(0, 1, "lz_" + i);
            w[i][0] = sub.intVar(0, 1, "wx_" + i);
            w[i][1] = sub.intVar(0, 1, "wy_" + i);
            w[i][2] = sub.intVar(0, 1, "wz_" + i);
            h[i][0] = sub.intVar(0, 1, "hx_" + i);
            h[i][1] = sub.intVar(0, 1, "hy_" + i);
            h[i][2] = sub.intVar(0, 1, "hz_" + i);
        }
        for (int i = 0; i < itemsCount - 1; i++) {
            for (int k = i + 1; k < itemsCount; k++) {
                IloNumExpr con1 = sub.numExpr();
                con1 = sub.sum(con1, x[i], sub.prod(allItems.get(itemsId.get(i)).getP(), l[i][0]), sub.prod(allItems.get(itemsId.get(i)).getQ(), w[i][0]), sub.prod(allItems.get(itemsId.get(i)).getR(), h[i][0]));
                sub.addLe(con1, sub.sum(x[k], sub.prod(sub.diff(1, a[i][k]), Mx)), "xi_" + i + "," + k);

                IloNumExpr con2 = sub.numExpr();
                con2 = sub.sum(con2, x[k], sub.prod(allItems.get(itemsId.get(k)).getP(), l[k][0]), sub.prod(allItems.get(itemsId.get(k)).getQ(), w[k][0]), sub.prod(allItems.get(itemsId.get(k)).getR(), h[k][0]));
                sub.addLe(con2, sub.sum(x[i], sub.prod(sub.diff(1, b[i][k]), Mx)), "xk_" + i + "," + k);

                IloNumExpr con3 = sub.numExpr();
                con3 = sub.sum(con3, y[i], sub.prod(allItems.get(itemsId.get(i)).getP(), l[i][1]), sub.prod(allItems.get(itemsId.get(i)).getQ(), w[i][1]), sub.prod(allItems.get(itemsId.get(i)).getR(), h[i][1]));
                sub.addLe(con3, sub.sum(y[k], sub.prod(sub.diff(1, c[i][k]), My)), "yi_" + i + "," + k);

                IloNumExpr con4 = sub.numExpr();
                con4 = sub.sum(con4, y[k], sub.prod(allItems.get(itemsId.get(k)).getP(), l[k][1]), sub.prod(allItems.get(itemsId.get(k)).getQ(), w[k][1]), sub.prod(allItems.get(itemsId.get(k)).getR(), h[k][1]));
                sub.addLe(con4, sub.sum(y[i], sub.prod(sub.diff(1, d[i][k]), My)), "yk_" + i + "," + k);

                IloNumExpr con5 = sub.numExpr();
                con5 = sub.sum(con5, z[i], sub.prod(allItems.get(itemsId.get(i)).getP(), l[i][2]), sub.prod(allItems.get(itemsId.get(i)).getQ(), w[i][2]), sub.prod(allItems.get(itemsId.get(i)).getR(), h[i][2]));
                sub.addLe(con5, sub.sum(z[k], sub.prod(sub.diff(1, e[i][k]), Mz)), "zi_" + i + "," + k);

                IloNumExpr con6 = sub.numExpr();
                con6 = sub.sum(con6, z[k], sub.prod(allItems.get(itemsId.get(k)).getP(), l[k][2]), sub.prod(allItems.get(itemsId.get(k)).getQ(), w[k][2]), sub.prod(allItems.get(itemsId.get(k)).getR(), h[k][2]));
                sub.addLe(con6, sub.sum(z[i], sub.prod(sub.diff(1, f[i][k]), Mz)), "zk_" + i + "," + k);
            }
        }

        for (int i = 0; i < itemsCount - 1; i++) {
            for (int k = i + 1; k < itemsCount; k++) {
                IloNumExpr con7 = sub.numExpr();
                con7 = sub.sum(con7, a[i][k], b[i][k], c[i][k], d[i][k], e[i][k], f[i][k]);
                sub.addGe(con7, 1, "position_" + i + "," + k);
            }

        }
        for (int i = 0; i < itemsCount; i++) {
            sub.addEq(sub.sum(l[i][0], l[i][1], l[i][2]), 1);
            sub.addEq(sub.sum(w[i][0], w[i][1], w[i][2]), 1);
            sub.addEq(sub.sum(h[i][0], h[i][1], h[i][2]), 1);
            sub.addEq(sub.sum(l[i][0], w[i][0], h[i][0]), 1);
            sub.addEq(sub.sum(l[i][1], w[i][1], h[i][1]), 1);
            sub.addEq(sub.sum(l[i][2], w[i][2], h[i][2]), 1);
        }
        for (int i = 0; i < itemsCount; i++) {
            IloNumExpr con10 = sub.numExpr();
            con10 = sub.sum(x[i], sub.prod(allItems.get(itemsId.get(i)).getP(), l[i][0]), sub.prod(allItems.get(itemsId.get(i)).getQ(), w[i][0]), sub.prod(allItems.get(itemsId.get(i)).getR(), h[i][0]));
            sub.addLe(con10, L, "length_" + i + "," + bagId);
            IloNumExpr con11 = sub.numExpr();
            con11 = sub.sum(y[i], sub.prod(allItems.get(itemsId.get(i)).getP(), l[i][1]), sub.prod(allItems.get(itemsId.get(i)).getQ(), w[i][1]), sub.prod(allItems.get(itemsId.get(i)).getR(), h[i][1]));
            sub.addLe(con11, W, "width_" + i + "," + bagId);
            IloNumExpr con12 = sub.numExpr();
            con12 = sub.sum(z[i], sub.prod(allItems.get(itemsId.get(i)).getP(), l[i][2]), sub.prod(allItems.get(itemsId.get(i)).getQ(), w[i][2]), sub.prod(allItems.get(itemsId.get(i)).getR(), h[i][2]));
            sub.addLe(con12, H, "height_" + i + "," + bagId);

        }
        //Branch and repair 里面的对称
        if (Configuration.useCutInBranchAndRepair) {
            IloNumExpr con11 = sub.numExpr();
            con11 = sub.sum(con11, x[0], sub.prod(0.5 * allItems.get(itemsId.get(0)).getP(), l[0][0]), sub.prod(0.5 * allItems.get(itemsId.get(0)).getQ(), w[0][0]), sub.prod(0.5 * allItems.get(itemsId.get(0)).getR(), h[0][0]));
            sub.addLe(con11, sub.prod(0.5, L));
            IloNumExpr con12 = sub.numExpr();
            con12 = sub.sum(con12, y[0], sub.prod(0.5 * allItems.get(itemsId.get(0)).getP(), l[0][1]), sub.prod(0.5 * allItems.get(itemsId.get(0)).getQ(), w[0][1]), sub.prod(0.5 * allItems.get(itemsId.get(0)).getR(), h[0][1]));
            sub.addLe(con12, sub.prod(0.5, W));
            IloNumExpr con13 = sub.numExpr();
            con13 = sub.sum(con13, z[0], sub.prod(0.5 * allItems.get(itemsId.get(0)).getP(), l[0][2]), sub.prod(0.5 * allItems.get(itemsId.get(0)).getQ(), w[0][2]), sub.prod(0.5 * allItems.get(itemsId.get(0)).getR(), h[0][2]));
            sub.addLe(con13, sub.prod(0.5, H));
        }
        //袋子变形约束
        sub.addLe(sub.sum(L, H), allBags.get(bagId).getX(), "transformX_" + bagId);
        sub.addLe(sub.sum(W, H), allBags.get(bagId).getY(), "transformY_" + bagId);
        // 同类物品相对位置cut
        for (int i = 0; i < itemsCount; i++) {
            for (int k = i + 1; k < itemsCount; k++) {
                if (allItems.get(itemsId.get(i)).getType() == allItems.get(itemsId.get(k)).getType()) {
                    b[i][k].setUB(0);
                    d[i][k].setUB(0);
                    f[i][k].setUB(0);

                }
            }
        }
        // bag cut H<Y/2;
        H.setUB(allBags.get(bagId).getY() / 2);
        //sub.addLe(H,allBags.get(bag).getY()/2,"transformCUt");
        IloNumVar obj = sub.boolVar();
    }


    public void solveLP() throws IloException {
        sub.solve();
    }


    public IloCplex.Status getLPStatus() throws IloException {
        return sub.getStatus();
    }

    public void end() throws IloException {
        sub.end();
    }

    public void clearLP() throws IloException {
        sub.endModel();
    }

    public void exportLP(String path) throws IloException {
        sub.exportModel(path);
    }

    public double getSubObj() throws IloException {
        return sub.getObjValue();
    }

    public ArrayList<Position> getChenModelResults(ArrayList<Integer> itemsId, int bagId) throws IloException {
        ArrayList<Position> positions = new ArrayList<>(itemsId.size());
        int itemsCount = itemsId.size();
//        ArrayList<Bag> allBags = instance.getAllBags();
        ArrayList<Item> allItems = instance.getAllItems();
        double[] xx, yy, zz, lx, ly, lz;
//        double LL, WW, HH;
        xx = sub.getValues(x);
        yy = sub.getValues(y);
        zz = sub.getValues(z);
        lx = new double[itemsCount];
        ly = new double[itemsCount];
        lz = new double[itemsCount];
//        LL = sub.getValue(L);
//        WW = sub.getValue(W);
//        HH = sub.getValue(H);
        for (int i = 0; i < itemsCount; i++) {
            double[] ll = sub.getValues(l[i]);
            double[] ww = sub.getValues(w[i]);
            double[] hh = sub.getValues(h[i]);
            if (ll[0] > 0.9) {
                lx[i] = allItems.get(itemsId.get(i)).getP();
            } else {
                if (ww[0] > 0.9) {
                    lx[i] = allItems.get(itemsId.get(i)).getQ();
                } else {
                    lx[i] = allItems.get(itemsId.get(i)).getR();
                }
            }
            if (ll[1] > 0.9) {
                ly[i] = allItems.get(itemsId.get(i)).getP();
            } else {
                if (ww[1] > 0.9) {
                    ly[i] = allItems.get(itemsId.get(i)).getQ();
                } else {
                    ly[i] = allItems.get(itemsId.get(i)).getR();
                }
            }
            if (ll[2] > 0.9) {
                lz[i] = allItems.get(itemsId.get(i)).getP();
            } else {
                if (ww[2] > 0.9) {
                    lz[i] = allItems.get(itemsId.get(i)).getQ();
                } else {
                    lz[i] = allItems.get(itemsId.get(i)).getR();
                }
            }
        }
        for (int i = 0; i < itemsCount; i++) {
            Position position = new Position(allItems.get(itemsId.get(i)).getType(), (int) Math.round(xx[i]), (int) Math.round(yy[i]), (int) Math.round(zz[i]),
                    (int) Math.round(lx[i]), (int) Math.round(ly[i]), (int) Math.round(lz[i]));
            positions.add(position);
        }
//        for (Position p : positions) {
//            System.out.println(p);
//        }
        return positions;
    }
}
