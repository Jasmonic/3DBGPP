package baseObject;

/**
 * @Author: Feng Jixuan
 * @Date: 2022-08-2022-08-23
 * @Description: BPP_Model
 * @version=1.0
 */
public class Bag {
    int type;
    double X,Y;
    double l,w,h;
    double maxVolume;
    double cost;
    int num;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public double getX() {
        return X;
    }

    public void setX(double x) {
        X = x;
    }

    public double getY() {
        return Y;
    }

    public void setY(double y) {
        Y = y;
    }

    public double getL() {
        return l;
    }

    public void setL(double l) {
        this.l = l;
    }

    public double getW() {
        return w;
    }

    public void setW(double w) {
        this.w = w;
    }

    public double getH() {
        return h;
    }

    public void setH(double h) {
        this.h = h;
    }

    public double getMaxVolume() {
        return maxVolume;
    }

    public void setMaxVolume(double maxVolume) {
        this.maxVolume = maxVolume;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    @Override
    public String toString() {
        return "Bag{" +
                "type=" + type +
                ", X=" + X +
                ", Y=" + Y +
                ", l=" + l +
                ", w=" + w +
                ", h=" + h +
                ", maxVolume=" + maxVolume +
                ", cost=" + cost +
                ", num=" + num +
                '}';
    }



    public Bag(int type, double x, double y, int num) {
        this.type = type;
        X = x;
        Y = y;
        this.num = num;
        this.maxVolume=this.calMaxVolume(x,y);
    }
    public Bag(int type, double x, double y, double cost, int num ) {
        this.type = type;
        X = x;
        Y = y;
        this.cost=cost;
        this.num = num;
        this.maxVolume=this.calMaxVolume(x,y);
    }

    public Bag(int type, double x, double y, double maxVolume, double cost, int num) {
        this.type = type;
        X = x;
        Y = y;
        this.maxVolume = maxVolume;
        this.cost = cost;
        this.num = num;
    }

    public Bag(int type, double l, double w, double h, double cost) {
        this.type = type;
        this.l = l;
        this.w = w;
        this.h = h;
        this.cost = cost;
    }
    public double calMaxVolume(double x,double y){
        double zn;
        zn=(x+y-Math.sqrt((x-y)*(x-y)+x*y))/3;
        return (x-zn)*(y-zn)*zn;
    }
}
