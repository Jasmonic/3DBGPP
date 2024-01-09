package benders;


import baseObject.Configuration;
import baseObject.Instance;
import benders.upperBound.UpperBound;
import ilog.concert.IloException;
import ilog.cplex.IloCplex;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;


/**
 * @Author: Feng Jixuan
 * @Date: 2022-10-2022-10-21
 * @Description: BPP_Model
 * @version=1.0
 */
public class Main {

    public static void main(String[] args) throws IOException, IloException {
        long start;
        long end;
        Instance instance = new Instance();
        instance.initialze(args[0]);


        UpperBound upperBound = new UpperBound(instance);
        start = System.currentTimeMillis();
        upperBound.calculateUpperCounts();
        end = System.currentTimeMillis();

//        Date date = new Date();
//        SimpleDateFormat formatter = new SimpleDateFormat("_yyyy_MM_dd_HH_mm_ss");
//        PrintStream ps = System.out;
//        ps = new PrintStream(new BufferedOutputStream(new FileOutputStream("./output/part2/BS/log/"+args[0]+formatter.format(date)+".txt")), true);
//        System.setOut(ps);

        System.out.println("=====================calculate the Upperbound=====================");
        System.out.println("Total time =" + (end - start) * 1.0 / 1000 + "s");
        int[] ub = upperBound.getUpperCounts();
        System.out.println("Bag upper bound:  " + Arrays.toString(ub));

        boolean feasible = false;
        for (int i : ub) {
            if (i > 0) {
                feasible = true;
                break;
            }
        }
        if (!feasible) {
            System.out.println("This instance is infeasible");
            assert false;
        }

        for (int i = 0; i < ub.length; i++) {
            instance.getBags().get(i).setNum(ub[i]);
        }
        instance.bagTransformToOneDimension();
        instance.setCount();
        System.out.println(instance.countToString());


        // 开始Benders


        MasterProblem masterProblem = new MasterProblem(instance);
        instance.setStart(System.currentTimeMillis());
        int threadNum = Configuration.masterProblemThreadNum;
        masterProblem.setThreadNum(threadNum);
        BendersCallback callback = new BendersCallback(threadNum, masterProblem, instance);
        masterProblem.attach(callback);
        masterProblem.master.setParam(IloCplex.Param.TimeLimit,Configuration.masterProblemTimeLimit);
        masterProblem.solve();
        end = System.currentTimeMillis();

        System.out.println("master problem status = "+masterProblem.getStatus());

        if (masterProblem.getStatus() == IloCplex.Status.Optimal||masterProblem.getStatus() ==IloCplex.Status.Feasible) {
            System.out.println(masterProblem.getObjective());
            masterProblem.printResult();
        }

        System.out.println("Total time =" + (end - start) * 1.0 / 1000 + "s");
        System.out.println("——————————————————————————————————————————————————");


        System.exit(0);
        Result result = new Result(instance, masterProblem);
        result.parseResults();
        if (result.checkIsResultFeasible()){
            result.printResults();
            result.resultsToCsv(args[0]);
            result.resultSolvwToCsv(args[0],end-start, masterProblem.master);
        }
        System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out),false));
        System.out.println("算例: "+ args[0]+"  time =" + (end - start) * 1.0 / 1000 + "s, "+"cost = "+masterProblem.getObjective() );

    }
}
