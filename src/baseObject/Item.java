package baseObject;

import java.util.Arrays;

/**
 * @Author: Feng Jixuan
 * @Date: 2022-08-2022-08-23
 * @Description: BPP_Model
 * @version=1.0
 */
public class Item {
    int type;
    int p, q, r;
    int num;
    int volume;
    int id;
    Item[] itemCopies;
    int[] lengthInOrder;

    public Item(int type, int p, int q, int r, int num) {
        this.type = type;
        this.p = p;
        this.q = q;
        this.r = r;
        this.num = num;
        this.volume = p * q * r;
    }

    public Item(int type, int p, int q, int r, int num, int volume, int id) {
        this.type = type;
        this.p = p;
        this.q = q;
        this.r = r;
        this.num = num;
        this.volume = volume;
        this.id = id;
    }

    public Item copyOfOrientation(int t) {
        if (t < 1 || t > 6) {
            throw new IllegalArgumentException("param of copyOfOrientation must be in range [1..6],now is " + t);
        }
        return itemCopies[t - 1];
    }

    public int getLengthOf(int i) {
        if (i < 0 || i > 2) {
            throw new IllegalArgumentException("param of getLengthOf must be in range [0..2],now is " + i);
        }
        return lengthInOrder[i];
    }

    public void setItemCopies() {
        itemCopies = new Item[6];
        itemCopies[5] = new Item(type, lengthInOrder[0], lengthInOrder[1], lengthInOrder[2], 1, volume, id);
        itemCopies[4] = new Item(type, lengthInOrder[0], lengthInOrder[2], lengthInOrder[1], 1, volume, id);
        itemCopies[3] = new Item(type, lengthInOrder[1], lengthInOrder[0], lengthInOrder[2], 1, volume, id);
        itemCopies[2] = new Item(type, lengthInOrder[1], lengthInOrder[2], lengthInOrder[0], 1, volume, id);
        itemCopies[1] = new Item(type, lengthInOrder[2], lengthInOrder[0], lengthInOrder[1], 1, volume, id);
        itemCopies[0] = new Item(type, lengthInOrder[2], lengthInOrder[1], lengthInOrder[0], 1, volume, id);

    }

    public void setLengthInOrder() {
        lengthInOrder = new int[3];
        lengthInOrder[0] = getP();
        lengthInOrder[1] = getQ();
        lengthInOrder[2] = getR();
        Arrays.sort(lengthInOrder);
    }

    @Override
    public String toString() {
        return "Item{" +
                "type=" + type +
                ", id=" + id +
                ", p=" + p +
                ", q=" + q +
                ", r=" + r +
                ", num=" + num +
                ", volume=" + volume +
                '}';
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getP() {
        return p;
    }

    public void setP(int p) {
        this.p = p;
    }

    public int getQ() {
        return q;
    }

    public void setQ(int q) {
        this.q = q;
    }

    public int getR() {
        return r;
    }

    public void setR(int r) {
        this.r = r;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
