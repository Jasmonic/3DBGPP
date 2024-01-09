package baseObject;

import java.util.Objects;

/**
 * @Author: Feng Jixuan
 * @Date: 2023-03-2023-03-23
 * @Description: BPP_Model
 * @version=1.0
 */
public class TwoTuple<A, B> {

    /**
     * 两个元素的元组，用于在一个方法里返回两种类型的值
     */
    public  A first;
    public  B second;

    public TwoTuple(A a, B b) {
        this.first = a;
        this.second = b;
    }

    public TwoTuple() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TwoTuple<?, ?> twoTuple = (TwoTuple<?, ?>) o;
        return first.equals(twoTuple.first) && second.equals(twoTuple.second);
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, second);
    }

    public A getFirst() {
        return first;
    }

    public B getSecond() {
        return second;
    }
    public TwoTuple reset(A a,B b){
        this.first = a;
        this.second = b;
        return this;
    }


}
