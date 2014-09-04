package org.opencage.lindwurm.niotest.tests;

import org.junit.Test;

import java.io.IOException;
import java.nio.file.FileStore;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.core.Is.is;
import static org.junit.Assume.assumeThat;

/**
 * Created by stephan on 03/09/14.
 */
public abstract class PathTest15LimitedFileStoreIT extends PathTest14Principals {

    @Test
    public void testAAA15() throws IOException {
        assumeThat( capabilities.supportsFileStores(), is(true));
        assumeThat( capabilities.hasSizeLimitedFileSystem(), is(true));

        FileStore store = sizeLimitedPlayground.getFileSystem().provider().getFileStore(sizeLimitedPlayground);
        long      limit = store.getUnallocatedSpace();

        assertThat( limit, lessThan(15000L));
        assumeThat( limit, greaterThan(1000L));
    }

    @Test()
    public void testCanCreateFileWithinLimits() throws IOException {
        assumeThat( capabilities.supportsFileStores(), is(true));
        assumeThat( capabilities.hasSizeLimitedFileSystem(), is(true));

        FileStore store = sizeLimitedPlayground.getFileSystem().provider().getFileStore(sizeLimitedPlayground);
//        long      before = store.getUnallocatedSpace();

        Path file = sizeLimitedPlayground.resolve( nameStr[0]);

        Files.write(file, CONTENT);
    }

    @Test( expected = IOException.class )
    public void testCanNotCreateFileToExeedStore() throws IOException {
        assumeThat( capabilities.supportsFileStores(), is(true));
        assumeThat( capabilities.hasSizeLimitedFileSystem(), is(true));

        FileStore store = sizeLimitedPlayground.getFileSystem().provider().getFileStore(sizeLimitedPlayground);
        long      before = store.getUnallocatedSpace();

        assumeThat( before, lessThan(15000L));

        Path file = sizeLimitedPlayground.resolve( nameStr[0]);
        Files.write(file, CONTENT20k);

    }

    @Test( expected = IOException.class )
    public void testCanNotModifyFileToExeedStore() throws IOException {
        assumeThat( capabilities.supportsFileStores(), is(true));
        assumeThat( capabilities.hasSizeLimitedFileSystem(), is(true));

        FileStore store = sizeLimitedPlayground.getFileSystem().provider().getFileStore(sizeLimitedPlayground);
        long      before = store.getUnallocatedSpace();

        Path file = sizeLimitedPlayground.resolve( nameStr[0]);

        assumeThat( before, lessThan(15000L));
        assumeThat( before, greaterThan(1000L));

        Files.write(file, CONTENT);
        Files.write(file, CONTENT20k);

    }

}
