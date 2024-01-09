package benders;

import baseObject.Configuration;
import baseObject.Instance;
import ilog.concert.IloException;
import ilog.concert.IloRange;
import ilog.cplex.IloCplex;
import singleBag.BeamSearch;
import singleBag.Node;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * @Author: Feng Jixuan
 * @Date: 2022-10-2022-10-23
 * @Description: BPP_Model
 * @version=1.0
 */
public class BendersCallback implements IloCplex.Callback.Function {

    private final MasterProblem master;
    private final Instance instance;       // the problem being solved
    private final SubProblem[] subProblems;
    final HashMap<Pattern, Boolean> isPatternFeasible;
    public BeamSearch beamSearchSolver;

    public BendersCallback(int threadNum, MasterProblem master, Instance instance) {
        subProblems = new SubProblem[threadNum];
        this.master = master;
        this.instance = instance;
        isPatternFeasible = instance.getIsPatternFeasible();
        beamSearchSolver = new BeamSearch();
    }

    @Override
    public void invoke(IloCplex.Callback.Context context) throws IloException {
        int threadNo = context.getIntInfo(IloCplex.Callback.Context.Info.ThreadId);
        int nth = context.getIntInfo(IloCplex.Callback.Context.Info.Threads);
        switch ((int) context.getId()) {
            case (int) IloCplex.Callback.Context.Id.ThreadUp:
                subProblems[threadNo] = new SubProblem(instance);
                System.out.println("!!Thread UP");
                return;

            case (int) IloCplex.Callback.Context.Id.ThreadDown:
                subProblems[threadNo].end();
                System.out.println("!!Thread down");
                return;

            case (int) IloCplex.Callback.Context.Id.Candidate:
                SubProblem subProblem = subProblems[threadNo];
                double[][] s = master.getS(context);
                if (s[0].length != instance.getBagCount()) {
                    throw new IllegalArgumentException("s[0].length has problem");
                }
                ArrayList<IloRange> cutsList = new ArrayList<>();
                System.out.println("print n");
                double[] n = new double[instance.getBagCount()];
                for (int j = 0; j < s[0].length; j++) {
                    n[j] = 0;
                    for (int i = 0; i < s.length; i++) {
                        n[j] += s[i][j];
                    }
                }
                System.out.println(Arrays.toString(n));
                for (int j = 0; j < s[0].length; j++) {
                    if (n[j] > 0.5) {
                        ArrayList<Integer> itemsIds = new ArrayList<>(s.length);
                        for (int i = 0; i < s.length; i++) {
                            if (s[i][j] > 0.5) {
                                itemsIds.add(i);
                            }
                        }

                        System.out.println("====================");
                        Pattern pattern = new Pattern(instance, itemsIds, j);
                        System.out.print("将物品  " + itemsIds + "  放进袋子[" + j + "]  袋子种类为[" + instance.getAllBags().get(j).getType() + "]              ");
                        System.out.println(pattern);
                        boolean needToSolve = true;
                        boolean needToAddCut = false;
                        // if 如果检测到同样的pattern 直接reject
                        if (isPatternFeasible.containsKey(pattern)) {
                            needToSolve = false;
                            if (isPatternFeasible.get(pattern) == true) {
                                needToAddCut = false;
                            } else {
                                needToAddCut = true;
                            }
                        }
                        boolean useHeuristic = false;
                        Node lastNode = null;
                        long start = System.currentTimeMillis();
                        if (needToSolve == true) {
                            System.out.print(" Need to solve  ");
                            if (itemsIds.size() < Configuration.gamma) {
                                //小于gamma个用精确解
                                System.out.println("method : solve MIP");
                                try {
                                    subProblem.BuildChenModel(itemsIds, j);
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                }
                                System.out.print("子问题建模完毕-->");
                                subProblem.solveLP();
                                System.out.print("求解完毕-->");
                                System.out.println("子问题状态=" + subProblem.getLPStatus() + "           ");
                                if (subProblem.getLPStatus() == IloCplex.Status.Infeasible) {
                                    needToAddCut = true;
                                    isPatternFeasible.put(pattern, false);
                                }
                                if (subProblem.getLPStatus() == IloCplex.Status.Optimal) {
                                    needToAddCut = false;
                                    isPatternFeasible.put(pattern, true);
                                    instance.getPatternToPositions().put(pattern, subProblem.getChenModelResults(itemsIds, j));
                                }
                                if (subProblem.getLPStatus() == IloCplex.Status.Unknown) {
                                    throw new IllegalArgumentException("the subProblem status is unknown");
                                }
                                subProblem.clearLP();
                            } else {
                                //大于gamma个用启发式
                                System.out.println("method : Beam Search");
                                useHeuristic = true;
                                beamSearchSolver.init();
                                beamSearchSolver.pack(instance, instance.getAllBags().get(j), itemsIds);
                                lastNode = beamSearchSolver.getNodesOfLastLevel().get(0);
                                if (beamSearchSolver.successToPack == false) {
                                    needToAddCut = true;
                                    isPatternFeasible.put(pattern, false);
                                } else {
                                    needToAddCut = false;
                                    isPatternFeasible.put(pattern, true);
                                    instance.getPatternToPositions().put(pattern, lastNode.getPackedPosition());
                                }
                            }
                            long end = System.currentTimeMillis();
                            System.out.println("Time to solve subproblem:" + (end - start) * 1.0 / 1000 + "s");
                        } else {
                            System.out.println(" Do not need to solve");
                        }

                        if (needToAddCut) {
                            System.out.println(" Cut -> need to add cut");
                        } else {
                            System.out.println(" Cut -> do not need to add cut");
                        }


                        if (needToAddCut) {
                            ArrayList<Integer> newItemsId = itemsIds;
                            if (!needToSolve) {
                                newItemsId = Pattern.getOldMisFromHashMap(instance, itemsIds, j);
                                cutsList.add(master.generateNoGoodCut(newItemsId, j));
                            } else {
                                if (useHeuristic) {

                                    newItemsId = lastNode.getMisFromLastNode();
                                    System.out.println(newItemsId);
//                                    for (int i = 0; i < beamSearchSolver.getNodesOfLastLevel().size(); i++) {
//                                        System.out.println(beamSearchSolver.getNodesOfLastLevel().get(i).getMisFromLastNode());
//                                    }

                                } else {
                                    //TODO newItemsId = LP解完后的求MIS
                                }
                                cutsList.add(master.generateNoGoodCut(newItemsId, j));
                                instance.getInfeasiblePatternToMIS().put(pattern, Pattern.itemsIdToTypeList(instance, newItemsId));
//                                System.out.println("**" +Pattern.itemsIdToTypeList(instance, newItemsId));
                            }
                            System.out.println("items in cut --->" + newItemsId);
//                            System.out.println("addCuts——" + cutsList.get(cutsList.size() - 1) + "无法将物品 " + items + "  放进袋子 " + j + " 袋子种类为" + instance.getAllBags().get(j).getType());
                        }

                    }
                }


                IloRange[] cuts = new IloRange[cutsList.size()];
                cutsList.toArray(cuts);
                if (!cutsList.isEmpty()) {
                    context.rejectCandidate(cuts);
                }
//                System.out.println();
//                instance.recordObj(System.currentTimeMillis() - instance.getStart(), master.getObj(context), cutsList.isEmpty());

                return;


            default:
                System.err.println("Callback was called from an invalid context: "
                        + context.getId() + ".\n");
        }
    }
}
