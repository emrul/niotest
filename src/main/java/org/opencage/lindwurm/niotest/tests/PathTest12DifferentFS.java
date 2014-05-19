package org.opencage.lindwurm.niotest.tests;

import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assume.assumeThat;
import static org.opencage.lindwurm.niotest.matcher.PathExists.exists;
import static org.opencage.lindwurm.niotest.matcher.WatchKeyMatcher.correctKey;

/**
 * Created with IntelliJ IDEA.
 * User: stephan
 * Date: 21/02/14
 * Time: 21:34
 * To change this template use File | Settings | File Templates.
 */
public abstract class PathTest12DifferentFS extends PathTest11WatcherIT {

    @Test
    public void testAAA12() {
        assumeThat( capabilities.has2ndFileSystem(), is(true));

        assertThat(play2, notNullValue() );

        assertThat( FS.provider(), is(play2.getFileSystem().provider()));
        assertThat( FS, not(is(play2.getFileSystem())));
    }

    @Test
    public void testCopyDifferentFS() throws IOException {
        assumeThat( capabilities.has2ndFileSystem(), is(true));

        Path src = getPathPABf();
        FS.provider().copy( src, getPathOtherPA());

        assertThat( getPathOtherPA(), exists());
    }

    @Test
    public void testIsSameFileDifferentFS() throws IOException {
        assumeThat( capabilities.has2ndFileSystem(), is(true));

        assertThat(FS.provider().isSameFile(getPathPAf(), getPathOtherPAf()), is(false));
    }

    @Test
    public void testMoveDifferentFS() throws IOException {
        assumeThat( capabilities.has2ndFileSystem(), is(true));

        Path src = getPathPABf();
        FS.provider().move(src, getPathOtherPA());

        assertThat( getPathOtherPA(), exists());
        assertThat( src, not(exists()));
    }

    @Test
    public void testWatchACreateFromCopyFromOtherFS() throws Exception {
        assumeThat(play2, CoreMatchers.notNullValue());
        assumeThat( capabilities.supportsWatchService(), Is.is(true));


        watcherSetup(ENTRY_CREATE);
        Files.copy( getPathOtherPAf(), getPathWA());

        assertThat( waitForWatchService().poll(), correctKey(getPathWA(), ENTRY_CREATE));
    }

    @Test
    public void testWatchACreateFromMoveFromOtherFS() throws Exception {
        assumeThat(play2, CoreMatchers.notNullValue());
        assumeThat( capabilities.supportsWatchService(), Is.is(true));

        watcherSetup(ENTRY_CREATE);
        Files.move(getPathOtherPAf(), getPathWA());

        assertThat( waitForWatchService().poll(), correctKey(getPathWA(), ENTRY_CREATE));
    }


    /*
     * ------------------------------------------------------------------------------------------------------
     */

    protected Path play2;

    public Path getPathOtherPA() throws IOException {
        Path dir = play2.resolve( testMethodName.getMethodName() );
        Files.createDirectories(dir);
        return dir.resolve(nameStr[0]);
    }

    public Path getPathOtherPAf() throws IOException {
        Path dir = play2.resolve( testMethodName.getMethodName() );
        Files.createDirectories(dir);
        Path ret = dir.resolve(nameStr[0]);
        Files.write( ret, CONTENT, standardOpen);
        return ret;
    }


}
