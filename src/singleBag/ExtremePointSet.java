package singleBag;

import baseObject.Bag;
import baseObject.Item;
import baseObject.Position;
import baseObject.check;
import comparator.ExtremePointXYZComparator;

import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;

/**
 * @Author: Feng Jixuan
 * @Date: 2022-12-2022-12-15
 * @Description: BPP_Model
 * @version=1.0
 */
public class ExtremePointSet {
    Set<ExtremePoint> extremePoints = null;
    int maxX;
    int maxY;
    int maxZ;
    Bag bag;

    public ExtremePointSet(Bag bag) {
        //构造初始node
        this.extremePoints = new TreeSet<ExtremePoint>(new ExtremePointXYZComparator());
        this.bag = bag;

//        extremePoints.add(new ExtremePoint(0,0,0));
    }

    public ExtremePointSet(Set<ExtremePoint> extremePoints, int maxX, int maxY, int maxZ, Bag bag) {
        this.extremePoints = extremePoints;
        this.maxX = maxX;
        this.maxY = maxY;
        this.maxZ = maxZ;
        this.bag = bag;
    }

    public void updateEPSetAfterPack(ArrayList<Position> packedPositions, Position packPosition) {
        int[] maxBound = {-1, -1, -1, -1, -1, -1};//Yx Yz Xy Xz Zx Zy
//        ExtremePointSet newExtremePointSet=node.extremePointSet.copy();
//        System.out.println(packPosition);
        ExtremePoint[] neweps = new ExtremePoint[6];
        //删除现有的ep
        this.extremePoints.remove(new ExtremePoint(packPosition.getX(), packPosition.getY(), packPosition.getZ()));
        //往已有的position投影
        for (Position position : packedPositions) {
//            System.out.println("yes");
            if (Position.canTakeProjection(packPosition, position, 0) && position.getX() + position.getLx() > maxBound[0]) {
                neweps[0] = new ExtremePoint(position.getX() + position.getLx(), packPosition.getY() + packPosition.getLy(), packPosition.getZ());
                maxBound[0] = position.getX() + position.getLx();
            }
            if (Position.canTakeProjection(packPosition, position, 1) && position.getZ() + position.getLz() > maxBound[1]) {
                neweps[1] = new ExtremePoint(packPosition.getX(), packPosition.getY() + packPosition.getLy(), position.getZ() + position.getLz());
                maxBound[1] = position.getZ() + position.getLz();
            }
            if (Position.canTakeProjection(packPosition, position, 2) && position.getY() + position.getLy() > maxBound[2]) {
                neweps[2] = new ExtremePoint(packPosition.getX() + packPosition.getLx(), position.getY() + position.getLy(), packPosition.getZ());
                maxBound[2] = position.getY() + position.getLy();
            }
            if (Position.canTakeProjection(packPosition, position, 3) && position.getZ() + position.getLz() > maxBound[3]) {
                neweps[3] = new ExtremePoint(packPosition.getX() + packPosition.getLx(), packPosition.getY(), position.getZ() + position.getLz());
                maxBound[3] = position.getZ() + position.getLz();
            }
            if (Position.canTakeProjection(packPosition, position, 4) && position.getX() + position.getLx() > maxBound[4]) {
                neweps[4] = new ExtremePoint(position.getX() + position.getLx(), packPosition.getY(), packPosition.getZ() + packPosition.getLz());
                maxBound[4] = position.getX() + position.getLx();
            }
            if (Position.canTakeProjection(packPosition, position, 5) && position.getY() + position.getLy() > maxBound[5]) {
                neweps[5] = new ExtremePoint(packPosition.getX(), position.getY() + position.getLy(), packPosition.getZ() + packPosition.getLz());
                maxBound[5] = position.getY() + position.getLy();
            }
        }
//        System.out.println("qqq"+Arrays.toString(neweps));
        //还要往xy yz xz面投影
        int M = (int) bag.getX();
        Position[] surface = {
                new Position(0, 0, 0, 0, M, M, 0),
                new Position(0, 0, 0, 0, M, 0, M),
                new Position(0, 0, 0, 0, 0, M, M),
        };
        for (Position position : surface) {
            if (Position.canTakeProjection(packPosition, position, 0) && position.getX() + position.getLx() > maxBound[0]) {
                neweps[0] = new ExtremePoint(position.getX() + position.getLx(), packPosition.getY() + packPosition.getLy(), packPosition.getZ());
                maxBound[0] = position.getX() + position.getLx();
            }
            if (Position.canTakeProjection(packPosition, position, 1) && position.getZ() + position.getLz() > maxBound[1]) {
                neweps[1] = new ExtremePoint(packPosition.getX(), packPosition.getY() + packPosition.getLy(), position.getZ() + position.getLz());
                maxBound[1] = position.getZ() + position.getLz();
            }
            if (Position.canTakeProjection(packPosition, position, 2) && position.getY() + position.getLy() > maxBound[2]) {
                neweps[2] = new ExtremePoint(packPosition.getX() + packPosition.getLx(), position.getY() + position.getLy(), packPosition.getZ());
                maxBound[2] = position.getY() + position.getLy();
            }
            if (Position.canTakeProjection(packPosition, position, 3) && position.getZ() + position.getLz() > maxBound[3]) {
                neweps[3] = new ExtremePoint(packPosition.getX() + packPosition.getLx(), packPosition.getY(), position.getZ() + position.getLz());
                maxBound[3] = position.getZ() + position.getLz();
            }
            if (Position.canTakeProjection(packPosition, position, 4) && position.getX() + position.getLx() > maxBound[4]) {
                neweps[4] = new ExtremePoint(position.getX() + position.getLx(), packPosition.getY(), packPosition.getZ() + packPosition.getLz());
                maxBound[4] = position.getX() + position.getLx();
            }
            if (Position.canTakeProjection(packPosition, position, 5) && position.getY() + position.getLy() > maxBound[5]) {
                neweps[5] = new ExtremePoint(packPosition.getX(), position.getY() + position.getLy(), packPosition.getZ() + packPosition.getLz());
                maxBound[5] = position.getY() + position.getLy();
            }
        }
        for (ExtremePoint newExtremePoint : neweps) {
            this.extremePoints.add(newExtremePoint);
            // 更新node的max
            this.setMaxX(Math.max(maxX, newExtremePoint.getX()));
            this.setMaxY(Math.max(maxY, newExtremePoint.getY()));
            this.setMaxZ(Math.max(maxZ, newExtremePoint.getZ()));
        }

    }

    public ExtremePointSet copy() {
        Set<ExtremePoint> newExtremePoints = new TreeSet<ExtremePoint>(new ExtremePointXYZComparator());
        for (ExtremePoint extremePoint : this.extremePoints) {
            newExtremePoints.add(extremePoint.copy());
        }
        ExtremePointSet newExtremePointSet = new ExtremePointSet(
                newExtremePoints, this.getMaxX(), this.getMaxY(), this.getMaxZ(), this.bag
        );
        return newExtremePointSet;
    }

    public boolean canPackItemOfOrientation(Item toPackItem,ArrayList<Position> packedPositions){
        boolean canPack=false;
        for (int j = 1; j <= 6; j++) {
            Item tryItem = toPackItem.copyOfOrientation(j);
            for (ExtremePoint extremePoint : this.getExtremePoints()) {
                Position packPosition = new Position(toPackItem.getType(), extremePoint.getX(), extremePoint.getY(), extremePoint.getZ(),
                        tryItem.getP(), tryItem.getQ(), tryItem.getR());
                if (!check.isPositionCollideWithPacked(packPosition, packedPositions)
                        && !check.isPositionOutOfBag(this, packPosition)) {
                    canPack = true;
                    break;
                }
            }
            if (canPack) return true;
        }
        return false;

    }
    public Set<ExtremePoint> getExtremePoints() {
        return extremePoints;
    }

    public void setExtremePoints(Set<ExtremePoint> extremePoints) {
        this.extremePoints = extremePoints;
    }

    public int getMaxX() {
        return maxX;
    }

    public void setMaxX(int maxX) {
        this.maxX = maxX;
    }

    public int getMaxY() {
        return maxY;
    }

    public void setMaxY(int maxY) {
        this.maxY = maxY;
    }

    public int getMaxZ() {
        return maxZ;
    }

    public void setMaxZ(int maxZ) {
        this.maxZ = maxZ;
    }

    public Bag getBag() {
        return bag;
    }
}
