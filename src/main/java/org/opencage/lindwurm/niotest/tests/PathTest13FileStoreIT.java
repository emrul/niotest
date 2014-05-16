package org.opencage.lindwurm.niotest.tests;

import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.hamcrest.number.OrderingComparison;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.FileStore;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributeView;
import java.util.logging.Logger;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.collection.IsEmptyIterable.emptyIterable;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.junit.Assume.assumeThat;

/**
 * Created by stephan on 01/05/14.
 */
public abstract class PathTest13FileStoreIT extends PathTest12DifferentFS {

    @Test
    public void testFileStoreIterable() {
        assumeThat( capabilities.supportsFileStores(), is(true));

        assertThat(FS.getFileStores(), not( emptyIterable()));
    }

    @Test
    public void testFileStoresHaveAName() {
        assumeThat( capabilities.supportsFileStores(), is(true));

        for (FileStore store : FS.getFileStores() ) {
            assertThat(store.name(), notNullValue());
        }
    }

    @Test
    public void testFileStoresHaveAType() {
        assumeThat( capabilities.supportsFileStores(), is(true));

        for (FileStore store : FS.getFileStores() ) {
            assertThat(store.type(), notNullValue());
        }
    }

    @Test
    public void testFileStoreTotalSpaceIsNonNegative() throws IOException {
        assumeThat( capabilities.supportsFileStores(), is(true));

        for (FileStore store : FS.getFileStores() ) {
            assertThat(store.getTotalSpace(), greaterThanOrEqualTo(0L));
        }
    }

    @Test
    public void testFileStoreUsableSpaceIsSmallerThanTotal() throws IOException {
        assumeThat( capabilities.supportsFileStores(), is(true));

        for (FileStore store : FS.getFileStores() ) {
            assertThat(store.getTotalSpace(), greaterThanOrEqualTo(store.getUsableSpace()));
        }
    }

    @Test
    public void testFileStoreUnallocatedSpaceIsSmallerUsableSpace() throws IOException {
        assumeThat( capabilities.supportsFileStores(), is(true));


        for (FileStore store : FS.getFileStores() ) {
            assertThat( store.getUnallocatedSpace(), greaterThanOrEqualTo(store.getUsableSpace()));
        }
    }

    @Test
    public void testPathFileStoreIsOneOfGeneralOnes() throws IOException {
        assumeThat( capabilities.supportsFileStores(), is(true));

        assertThat(FS.getFileStores(), hasItem(FS.provider().getFileStore(getPathPAf())));
    }

    // todo: other operations on the filesystem make it unclear what should happen
//    @Test
//    public void testPathFileStoreGrowingFileLowersUnallocatedSpace() throws IOException {
//        assumeThat( capabilities.supportsFileStores(), is(true));
//
//        Path      file = getPathPAf();
//        FileStore store = FS.provider().getFileStore(file);
//        long      before = store.getUnallocatedSpace();
//        Files.write(file, CONTENT50 );
//
////        assertThat( store.getUnallocatedSpace(), lessThanOrEqualTo( before + CONTENT.length - CONTENT50.length ));
//        assertThat( store.getUnallocatedSpace(), lessThanOrEqualTo( before  ));
//    }


    @Test
    public void testPathFileStoreGrowingFileLowersUsableSpace() throws IOException {
        assumeThat( capabilities.supportsFileStores(), is(true));

        Path      file = getPathPAf();
        FileStore store = FS.provider().getFileStore(file);
        long      before = store.getUnallocatedSpace();
        Files.write(file, CONTENT20k );

//        assertThat( store.getUnallocatedSpace(), lessThanOrEqualTo( before + CONTENT.length - CONTENT20k.length ));
        assertThat( store.getUsableSpace(), lessThanOrEqualTo( before  ));
    }

    @Test
    public void testFileStoreShowsThatBasicFileAttributeViewIsSupported() throws IOException {
        assumeThat( capabilities.supportsFileStores(), is(true));

        FileStore store = FS.provider().getFileStore( getDefaultPath() );

        Assert.assertThat(store.supportsFileAttributeView(BasicFileAttributeView.class), CoreMatchers.is(true));
    }


}
