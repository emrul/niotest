package org.opencage.lindwurm.niotest.tests;

import org.junit.Test;

import java.io.IOException;
import java.nio.file.Path;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assume.assumeThat;
import static org.opencage.lindwurm.niotest.matcher.PathExists.exists;

/**
 * Created with IntelliJ IDEA.
 * User: stephan
 * Date: 21/02/14
 * Time: 21:34
 * To change this template use File | Settings | File Templates.
 */
public abstract class PathTest12DifferentFS extends PathTest11WatcherIT {

    @Test
    public void testCopyDifferentFS() throws IOException {
        assumeThat(play2, notNullValue());

        Path src = getPathPABf();
        FS.provider().copy( src, getPathOtherPA());

        assertThat( getPathOtherPA(), exists());
    }

    @Test
    public void testIsSameFileDifferentFS() throws IOException {
        assumeThat(play2, notNullValue());

        assertThat(FS.provider().isSameFile(getPathPAf(), getPathOtherPAf()), is(false));
    }

    @Test
    public void testMoveDifferentFS() throws IOException {
        assumeThat(play2, notNullValue());

        Path src = getPathPABf();
        FS.provider().move(src, getPathOtherPA());

        assertThat( getPathOtherPA(), exists());
        assertThat( src, not(exists()));
    }
}