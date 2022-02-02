package com.formreleaf.common.utils.tuple;

import java.util.Objects;

/**
 * @author Bazlur Rahman Rokon
 * @date 5/21/15.
 */
public class Tuple2<T0, T1> implements Tuple<Tuple2<T0, T1>> {
    public final T0 _0;
    public final T1 _1;

    protected Tuple2(T0 _0, T1 _1) {
        this._0 = _0;
        this._1 = _1;
    }

    @Override
    public int size() {
        return 2;
    }

    public static <T0, T1> Tuple2<T0, T1> valueOf(T0 _0, T1 _1) {
        return new Tuple2<>(_0, _1);
    }

    @Override
    public Object apply(int value) {
        switch (value) {
            case 0:
                return _0;
            case 1:
                return _1;
            default:
                throw new IndexOutOfBoundsException();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tuple2<?, ?> tuple2 = (Tuple2<?, ?>) o;
        return Objects.equals(_0, tuple2._0) &&
                Objects.equals(_1, tuple2._1);
    }

    @Override
    public int hashCode() {
        return Objects.hash(_0, _1);
    }

    @Override
    public String toString() {
        return _0 + ":" + _1;
    }
}
