package benders;


import ilog.concert.*;
import ilog.cplex.IloCplex;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * @Author: Feng Jixuan
 * @Date: 2022-10-2022-10-20
 * @Description: algorithm
 * @version=1.0
 */
public class Test {
    static int n = 127;
    static double[][] distance = new double[n][n];
    static double cost;
    static double[][] ans = new double[n][n];
    static double[] tour = new double[n];
    static int run = 0;

    public static void main(String[] args) throws IloException, IOException {
        String path = "D:\\1学习\\SRTP\\tsp\\tsp" + n + ".txt";
        BufferedReader br = new BufferedReader(new FileReader(path));
        String line = null;
        int count = 0;
        while ((line = br.readLine()) != null) {
            String[] str = line.split("\\s+");
            for (int i = 0; i < n; i++) {
                distance[count][i] = (int)Double.parseDouble(str[i]);   //29的数据强制转化int一下
            }
            count++;
        }
        long startTime = System.nanoTime();
        IloCplex cplex = new IloCplex();
        IloIntVar[][] x = new IloIntVar[n][n];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i != j)
                    x[i][j] = cplex.intVar(0, 1, "x" + i + "," + j);
                else
                    x[i][j] = cplex.intVar(0, 0, "x" + i + "," + j);
            }
        }
        //出入度为1
        for (int i = 0; i < n; i++) {
            IloLinearIntExpr constraint = cplex.linearIntExpr();
            for (int j = 0; j < n; j++) {
                constraint.addTerm(x[j][i], 1);
            }
            cplex.addEq(cplex.sum(x[i]), 1, "outDegree  " + i);
            cplex.addEq(constraint, 1, "inDegree  " + i);
        }
        IloLinearNumExpr obj = cplex.linearNumExpr();
        for (int i = 0; i < n; i++) {
            obj.add(cplex.scalProd(x[i], distance[i]));
        }
        cplex.addMinimize(obj);
        cplex.exportModel("test1.lp");
        System.out.println("——————————————————");
//        MyCallback callback = new MyCallback(x);
//        System.exit(0);
//        cplex.use(callback, IloCplex.Callback.Context.Id.Candidate|IloCplex.Callback.Context.Id.ThreadUp|IloCplex.Callback.Context.Id.ThreadDown);
//        cplex.setParam(IloCplex.Param.Threads, 1);
//        cplex.setParam(	IloCplex.Param.Preprocessing.Presolve,false);
//        cplex.use(new MyCallback(x,cplex));
        cplex.solve();
        System.out.println("状态="+cplex.getStatus());
        System.out.println(cplex.getObjValue());
        cplex.endModel();
        System.out.println(cplex);
        System.out.println(cplex==null);
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i != j)
                    x[i][j] = cplex.intVar(0, 1, "x" + i + "," + j);
                else
                    x[i][j] = cplex.intVar(0, 0, "x" + i + "," + j);
            }
        }
        //出入度为1

        IloLinearNumExpr obj2 = cplex.linearNumExpr();
        for (int i = 0; i < n; i++) {
            obj2.add(cplex.scalProd(x[i], distance[i]));
        }
        cplex.addMinimize(obj2);
        cplex.exportModel("test2.lp");
        long endTime = System.nanoTime();
        System.out.println("Time cost=" + (endTime - startTime) / 1000000000.0 + "s");
    }

    private static class MyCallback implements IloCplex.Callback.Function {
        private final IloIntVar[][] x;


        private MyCallback(IloIntVar[][] x) {
            this.x = x;
        }

        public void invoke(IloCplex.Callback.Context context) throws IloException {
            if (context.inThreadUp()) {
                return;
            }
            if (context.inThreadDown()) {
                return;
            }
            if (context.inCandidate()) {
                if (!context.isCandidatePoint())
                    throw new IloException("Unbounded solution");
                int n = x.length;
                IloCplex cplex = context.getCplex();
                ArrayList<Integer> subTour;
                ArrayList<ArrayList<Integer>> subTourPool = new ArrayList<ArrayList<Integer>>();
                double[][] arcs;
                arcs = new double[n][n];
                for (int i = 0; i < n; i++) {
                    arcs[i] = context.getCandidatePoint(x[i]);
                }
//                for (int i = 0; i < n; i++) {
//                    double s = 0;
//                    for (int j = 0; j < n; j++) {
//                        s += arcs[i][j];
//                    }
//                    if (Math.abs(s - 1) > 0.001) System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! hang");
//                }
//                for (int i = 0; i < n; i++) {
//                    double s = 0;
//                    for (int j = 0; j < n; j++) {
//                        s += arcs[j][i];
//                    }
//                    if (Math.abs(s - 1) > 0.001) System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! lie");
//                }
                int threadNo = context.getIntInfo(IloCplex.Callback.Context.Info.ThreadId);
//                int nodeId=context.getIntInfo(IloCplex.Callback.Context.Info.NodeUID);
                System.out.println("-----------------------checkSubTour-----------------------  ");
//                System.out.println("Thread :" + threadNo+"    NodeUid :"+nodeId);
                boolean check=true;
                int[] visited = new int[n];
                for (int i = 0; i < n; i++) {
                    if (visited[i] == 1) {
                        continue;
                    }
                    subTour = new ArrayList<Integer>();
                    int cur = i;
                    do {
                        subTour.add(cur);
                        visited[cur] = 1;
                        for (int j = 0; j < n; j++) {
                            if (arcs[cur][j] > 0.9) {
                                cur = j;
                                break;
                            }
                        }
                    } while (cur != i);
                    subTourPool.add(subTour);
//                    System.out.println(subTour);
                }
                if (subTourPool.size() == 1) {
                    System.out.println("只有一个大环，结束求解");
                    check = true;
                } else {
                    System.out.println("还有" + subTourPool.size() + "个子环");
                    check = false;
                }

                if (!check) {
                    while (!subTourPool.isEmpty()) {
//                        System.out.println("11111111111111111111111111111");
                        subTour = subTourPool.get(0);
                        subTourPool.remove(0);
                        IloLinearNumExpr constraint2 = cplex.linearNumExpr();
//                        System.out.println(subTour.toString());
                        for (int i : subTour) {
                            for (int j : subTour) {
                                constraint2.addTerm(1, x[i][j]);
                            }
                        }
                        int rhs=subTour.size() - 1;
//                        System.out.println("Adding lazy  constraint " + constraint2 + "<=" + (rhs));
                        context.rejectCandidate(cplex.le(constraint2, rhs));
                    }
                }
            }
        }


    }
}
