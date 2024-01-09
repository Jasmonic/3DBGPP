package singleBag;

/**
 * @Author: Feng Jixuan
 * @Date: 2022-12-2022-12-12
 * @Description: BPP_Model
 * @version=1.0
 */
public class ExtremePoint {
    private int x,y,z;

    public ExtremePoint(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    public ExtremePoint copy(){
        return new ExtremePoint(this.getX(), this.getY(), this.getZ());
    }
    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getZ() {
        return z;
    }

    public void setZ(int z) {
        this.z = z;
    }

    @Override
    public String toString() {
        return "ExtremePoint{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }
}
