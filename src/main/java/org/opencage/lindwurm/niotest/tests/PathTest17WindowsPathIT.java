package org.opencage.lindwurm.niotest.tests;

import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assume.assumeThat;

/**
 * Created by spfab on 06.10.2014.
 */
public abstract class PathTest17WindowsPathIT extends PathTest16PosixIT {

    @Test
    public void testCaseIgnorantPath() {
        assumeThat( nameStrCase == null, is(false));

        for ( int i = 0; i < 10; i++ ) {
            assumeThat(getPathA().resolve(nameStr[i]).equals(getPathA().resolve(nameStrCase[i])), is(true));
        }
    }
}
