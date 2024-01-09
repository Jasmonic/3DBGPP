package baseObject;

import singleBag.ExtremePointSet;
import singleBag.Node;

import java.util.ArrayList;

/**
 * @Author: Feng Jixuan
 * @Date: 2022-09-2022-09-01
 * @Description: BPP_Model
 * @version=1.0
 */
public class check {
    public static boolean isPositionCollide(Position p1, Position p2) {
        boolean a = isDimOfPositionCollide(p1, p2, 0);
        boolean b = isDimOfPositionCollide(p1, p2, 1);
        boolean c = isDimOfPositionCollide(p1, p2, 2);
        //如果在三个维度的投影均有重合，说明空间碰撞。
        boolean collision = (a && b && c);
        return collision;
        //真，说明物品有重合
    }

    public static boolean isDimOfPositionCollide(Position p1, Position p2, int i) {
        double[] d1 = {p1.getLx(), p1.getLy(), p1.getLz()};
        double[] d2 = {p2.getLx(), p2.getLy(), p2.getLz()};
        //货品1的i维度投影，加上该维度长的一半
        double pj1 = p1.position(i) + d1[i] / 2;
        //货品2的i维度投影，加上该维度长的一半
        double pj2 = p2.position(i) + d2[i] / 2;
        //两者相减，代表对应维度上，两货品中点间的距离
        double dis = Math.abs(pj1 - pj2);
        //真，说明两货品在该维度的投影有重合。
        return dis < (d1[i] + d2[i]) / 2;
    }

    public static boolean isPositionCollideWithPacked(Position p, ArrayList<Position> packedPositions) {
        for (Position packedPosition : packedPositions) {
            if (isPositionCollide(p, packedPosition)) {
//                System.out.println(packedPosition);
                return true;
            }

        }
        //如果能放进去 没重合 返回false
        return false;
    }

    public static boolean canFit(Node node, Position p) {
        boolean can = true;
        if (isPositionCollideWithPacked(p, node.getPackedPosition())) {
//            System.out.println("1111");
            can = false;
        }
        if (isPositionOutOfBag(node.getExtremePointSet(), p)) {
//            System.out.println("2222");
            can = false;
        }
        return can;
    }

    public static boolean canFit() {
        boolean can = true;
        return can;
    }

    public static boolean isPositionOutOfBag(ExtremePointSet extremePointSet, Position p) {
        //在做heuristic的时候用
        int L = Math.max(p.getX() + p.getLx(), extremePointSet.getMaxX());
        int W = Math.max(p.getY() + p.getLy(), extremePointSet.getMaxY());
        int H = Math.max(p.getZ() + p.getLz(), extremePointSet.getMaxZ());
        if (L + H > extremePointSet.getBag().getX() || W + H > extremePointSet.getBag().getY()) {
//            System.out.println(L+" "+W+" "+H);
//            System.out.println(node.getBag().getX());
//            System.out.println(H);
            return true;
        }
        if (2 * H > extremePointSet.getBag().getY()) {
            return true;
        }
        //如果超过边界就返回真，否则返回false
        return false;
    }

    public static boolean isPositionOutOfBag(Position p, Bag bag) {
        //在最后check的时候用
        int L = p.getX() + p.getLx();
        int W = p.getY() + p.getLy();
        int H = p.getZ() + p.getLz();
        if (L + H > bag.getX() || W + H > bag.getY()) {
            return true;
        }
        return false;
    }

    public static boolean isInstanceFeasible(Instance instance) {
        for (Item item : instance.getItems()) {
            //最大的袋子放一下
            Bag bag = instance.getBags().get(instance.getBags().size() - 1);
            if (item.getLengthOf(0) + item.getLengthOf(2) > bag.getX()
                    || item.getLengthOf(1) + item.getLengthOf(2) > bag.getY()) {
                return false;
            }
        }
        return true;
    }
}
