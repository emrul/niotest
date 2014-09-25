package org.opencage.lindwurm.niotest.tests;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assume.assumeThat;

/**
 * Created by stephan on 25/09/14.
 */
public abstract class PathTest16PosixIT extends PathTest15LimitedFileStoreIT  {

    @Test
    public void testGetOwnerAttribute() {
        assumeThat( capabilities.supportsPosixAttributes(), is(true));

        // oops check principals

    }

}
