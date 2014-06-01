package org.opencage.lindwurm.niotest;

import java.util.Iterator;

/**
 * Created by stephan on 29/05/14.
 */
public class Utils {

    public static <E> int getSize(Iterable<E> it) {
        int size = 0;
        for (E e : it ) {
            size++;
        }
        return size;
    }
}
