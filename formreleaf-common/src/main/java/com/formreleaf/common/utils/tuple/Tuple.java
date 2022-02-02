package com.formreleaf.common.utils.tuple;

import java.io.Serializable;
import java.util.Objects;
import java.util.function.IntFunction;

/**
 * @author Bazlur Rahman Rokon
 * @date 5/21/15.
 */
public interface Tuple<T extends Tuple> extends IntFunction, Cloneable, Serializable, Comparable<T> {
    int size();

    static <T0, T1> Tuple2<T0, T1> valueOf(T0 _0, T1 _1) {
        return Tuple2.valueOf(_0, _1);
    }

    @Override
    default int compareTo(T o) {
        Objects.requireNonNull(o);
        if (!getClass().equals(o.getClass())) {
            throw new ClassCastException(o.getClass() + " must equal " + getClass());
        }

        for (int i = 0; i < size(); i++) {
            @SuppressWarnings("unchecked")
            Comparable<Object> l = (Comparable<Object>) apply(i);
            Object r = o.apply(i);
            int c = l.compareTo(r);
            if (c != 0) {
                return c;
            }
        }

        return 0;
    }
}
