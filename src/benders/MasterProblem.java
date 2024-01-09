package benders;

import baseObject.Bag;
import baseObject.Instance;
import baseObject.Item;
import ilog.concert.IloException;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumVar;
import ilog.concert.IloRange;
import ilog.cplex.IloCplex;
import singleBag.Node;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * @Author: Feng Jixuan
 * @Date: 2022-10-2022-10-23
 * @Description: BPP_Model
 * @version=1.0
 */
public final class MasterProblem {
    private final int bagCount;
    private final int itemCount;
    final IloCplex master;
    private final IloNumVar[][] s;
    private final IloNumVar[] n;
    double[][] ss;
    double[] sn;
    final Instance instance;
    double ubCost;

    public MasterProblem(final Instance instance) throws IloException {
        this.instance = instance;
        bagCount = instance.getBagCount();
        itemCount = instance.getItemCount();
        master = new IloCplex();
        n = new IloNumVar[bagCount];
        s = new IloNumVar[itemCount][bagCount];
        ArrayList<Bag> allBags = instance.getAllBags();
        ArrayList<Item> allItems = instance.getAllItems();

        for (int i = 0; i < bagCount; i++) {
            n[i] = master.intVar(0, 1, "n_" + i);
        }
        for (int i = 0; i < itemCount; i++) {
            for (int j = 0; j < bagCount; j++) {
                s[i][j] = master.intVar(0, 1, "s_" + i + "," + j);
            }
        }

        for (int i = 0; i < itemCount; i++) {
            IloLinearNumExpr con8 = master.linearNumExpr();
            for (int j = 0; j < bagCount; j++) {
                con8.addTerm(1, s[i][j]);
            }
            master.addEq(con8, 1, "bag_and_item" + i);
        }

        for (int j = 0; j < bagCount; j++) {
            IloLinearNumExpr cutD = master.linearNumExpr();
            for (int i = 0; i < itemCount; i++) {
                cutD.addTerm(allItems.get(i).getVolume(), s[i][j]);
            }
            master.addLe(cutD, master.prod(allBags.get(j).getMaxVolume(), n[j]), "Volume_" + j);

        }

//        int cur = 0;
//        for (int i = 0; i < instance.getItems().size(); i++) {
//            double totalVolume = instance.getItems().get(i).getVolume() * instance.getItems().get(i).getNum();
//            for (int j = 0; j < allBags.size(); j++) {
//                if (totalVolume > allBags.get(j).getMaxVolume()) {
//                    IloLinearNumExpr cutC = master.linearNumExpr();
//                    for (int k = cur; k < cur + instance.getItems().get(i).getNum(); k++) {
//                        cutC.addTerm(1, s[k][j]);
//                    }
////                        System.out.println(i + " " + j + " " + "CCCCCCCCCCCCCCCCC");
////                        System.out.println(Math.floor(allBags.get(j).maxVolume/boxes.get(i).getVolume()));
//
////                        cplex.addLe(cutC,Math.floor(allBags.get(j).maxVolume/boxes.get(i).getVolume()),"CutC_"+i+"_"+j);
//                    if (Math.floor(allBags.get(j).getMaxVolume() / instance.getItems().get(i).getVolume()) == 0) {
//                        for (int k = cur; k < cur + instance.getItems().get(i).getNum(); k++) {
//                            s[k][j].setUB(0);
//                        }
//                    } else {
//                        System.out.println(Math.floor(allBags.get(j).getMaxVolume() / instance.getItems().get(i).getVolume()));
//                        master.addLe(cutC, master.prod(Math.floor(allBags.get(j).getMaxVolume() / instance.getItems().get(i).getVolume()), n[j]), "CutC_" + i + "_" + j);
//                    }
//
//                }
//            }
//            System.out.print(cur + ",");
//            cur += instance.getItems().get(i).getNum();
//            System.out.println(cur);
//        }
        //物品按照顺序cut
        for (int i = 0; i < itemCount - 1; i++) {
            if (allItems.get(i).getType() == allItems.get(i + 1).getType()) {
                for (int j = 0; j < bagCount; j++) {
                    IloLinearNumExpr con13 = master.linearNumExpr();
                    for (int r = 0; r <= j; r++) {
                        con13.addTerm(1, s[i][r]);
                    }
                    master.addGe(con13, s[i + 1][j], "itemCut" + i + "," + j);
                }
            }
        }

        //袋子对称cut
        for (int j = 0; j < bagCount - 1; j++) {
            if (allBags.get(j).getType() == allBags.get(j + 1).getType()) {
                master.addGe(n[j], n[j + 1], "bagCut_" + j + "," + (j + 1));
            }
        }
        IloLinearNumExpr obj = master.linearNumExpr();
        for (int j = 0; j < bagCount; j++) {
            obj.addTerm(allBags.get(j).getCost(), n[j]);
        }
        master.addMinimize(obj);
//        System.out.println(master);

    }

    public void end() {
        master.end();
    }

    public IloLinearNumExpr expression(final double constant)
            throws IloException {
        return master.linearNumExpr(constant);
    }

    public void attach(final BendersCallback callback) throws IloException {
        master.use(callback, IloCplex.Callback.Context.Id.Candidate
                | IloCplex.Callback.Context.Id.ThreadUp
                | IloCplex.Callback.Context.Id.ThreadDown);
    }

    public void solve() throws IloException {
        if (master.solve()) {

        }
    }

    public void setThreadNum(int num) throws IloException {
        master.setParam(IloCplex.Param.Threads, num);
    }

    public IloRange generateNoGoodCut(ArrayList<Integer> items, int bag) throws IloException {
        IloLinearNumExpr expr = master.linearNumExpr();
        for (int i : items) {
            expr.addTerm(1, s[i][bag]);
        }
        expr.addTerm(-items.size() + 1, n[bag]);
//        System.out.println("addNoGoodCut:  " + expr + " <= 0");
        return master.le(expr, 0);
    }

    public IloRange generateNoGoodCutForHeuristic(Node node, int bag) throws IloException {
        IloLinearNumExpr expr = master.linearNumExpr();
        for (int i : node.getPackedItemsId()) {
            expr.addTerm(1, s[i][bag]);
        }
        expr.addTerm(1, s[node.getRemainingItemsId().get(0)][bag]);
        // \sum_( packed) s[i]+ s[remain] <= size(packed)

        expr.addTerm(-node.getPackedItemsId().size(), n[bag]);
        System.out.println("addNoGoodCut:  " + expr + " <= 0");
        return master.le(expr, 0);
    }

    public void addUpperBound(double ub) throws IloException {
        IloLinearNumExpr obj = master.linearNumExpr();
        for (int j = 0; j < bagCount; j++) {
            obj.addTerm(instance.getAllBags().get(j).getCost(), n[j]);
        }
        master.addLe(obj, ub);
        master.exportModel("master.lp");
    }

    public void addLowerBound(double lb) throws IloException {
        IloLinearNumExpr obj = master.linearNumExpr();
        for (int j = 0; j < bagCount; j++) {
            obj.addTerm(instance.getAllBags().get(j).getCost(), n[j]);
        }
        master.addGe(obj, lb);
    }

    public double[][] getS(final IloCplex.Callback.Context context) throws IloException {
        double[][] sSol = new double[getItemCount()][getBagCount()];
        for (int i = 0; i < getItemCount(); i++) {
            sSol[i] = context.getCandidatePoint(s[i]);
        }
        return sSol;
    }

    public double[] getN(final IloCplex.Callback.Context context) throws IloException {

        return context.getCandidatePoint(n);
    }

    public double getObj(final IloCplex.Callback.Context context) throws IloException {

        return context.getCandidateObjective();
    }

    public int getBagCount() {
        return bagCount;
    }

    public int getItemCount() {
        return itemCount;
    }

    public int getThreadNum() throws IloException {
        return master.getNumCores();
    }

    public IloCplex.Status getStatus() throws IloException {
        return master.getStatus();
    }

    public double getObjective() throws IloException {
        return master.getObjValue();
    }

    public void printResult() throws IloException {
        ss = new double[itemCount][bagCount];
        for (int i = 0; i < getItemCount(); i++) {
            ss[i] = master.getValues(s[i]);
            System.out.println(Arrays.toString(ss[i]));
        }
    }

    public double getUbCost() {
        return ubCost;
    }

    public void setUbCost(double ubCost) {
        this.ubCost = ubCost;
    }

    public double[][] getSs() throws IloException {
        ss = new double[itemCount][bagCount];
        for (int i = 0; i < getItemCount(); i++) {
            ss[i] = master.getValues(s[i]);
        }
        return ss;
    }

    public double[] getSn() throws IloException {
        sn = master.getValues(n);
        return sn;
    }
}
