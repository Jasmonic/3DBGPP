package bagpackingModel;

import ilog.concert.IloException;

import java.io.IOException;

/**
 * @Author: Feng Jixuan
 * @Date: 2023-02-2023-02-04
 * @Description: BPP_Model
 * @version=1.0
 */
public class RunChen {
    public static void main(String[] args) throws IOException, IloException {
//        for (int i = 1; i <= 57; i++) {
//            ChenBagWithCost.main(new String[]{""+i, "e"});
//        }
//        for (int i = 101; i <= 150; i++) {
//            ChenBagWithCost.main(new String[]{""+i, "e"});
//        }
//        for (int i = 244; i <= 250; i++) {
//            ChenBagWithCost.main(new String[]{""+i, "e"});
//        }
        for (int i = 301; i <= 350; i++) {
            ChenBagWithCost.main(new String[]{""+i, "e"});
        }
    }
}
