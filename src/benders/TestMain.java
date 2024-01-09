package benders;

import ilog.concert.IloException;

import java.io.IOException;

/**
 * @Author: Feng Jixuan
 * @Date: 2023-01-2023-01-18
 * @Description: BPP_Model
 * @version=1.0
 */
public class TestMain {
    public static void main(String[] args) throws IOException, IloException {
        for (int i = 1; i <= 57; i++) {
            Main.main(new String[]{""+i, "e"});
        }
        for (int i = 101; i <= 150; i++) {
            Main.main(new String[]{""+i, "e"});
        }
        for (int i = 201; i <= 250; i++) {
            Main.main(new String[]{""+i, "e"});
        }
        for (int i = 301; i <= 350; i++) {
            Main.main(new String[]{""+i, "e"});
        }
    }
}
