package org.opencage.lindwurm.niotest.matcher;

import org.hamcrest.core.Is;
import org.junit.Assume;

/**
 * Created with IntelliJ IDEA.
 * User: stephan
 * Date: 06/03/14
 * Time: 16:17
 * To change this template use File | Settings | File Templates.
 */
public class Assumes {

    public static void assumeThat( boolean predicate ) {
        Assume.assumeThat(predicate, Is.is(true));
    }
}
