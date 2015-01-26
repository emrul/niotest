package de.pfabulist.lindwurm.niotest.testsn;

import de.pfabulist.lindwurm.niotest.testsn.setup.AllCapabilitiesBuilder;
import de.pfabulist.lindwurm.niotest.testsn.setup.Capa;
import de.pfabulist.lindwurm.niotest.testsn.setup.DetailBuilder;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.FileStore;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributeView;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.collection.IsEmptyIterable.emptyIterable;
import static org.hamcrest.core.IsCollectionContaining.hasItem;

/**
* ** BEGIN LICENSE BLOCK *****
* BSD License (2 clause)
* Copyright (c) 2006 - 2014, Stephan Pfab
* All rights reserved.
*
* Redistribution and use in source and binary forms, with or without
* modification, are permitted provided that the following conditions are met:
* * Redistributions of source code must retain the above copyright
* notice, this list of conditions and the following disclaimer.
* * Redistributions in binary form must reproduce the above copyright
* notice, this list of conditions and the following disclaimer in the
* documentation and/or other materials provided with the distribution.
*
* THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
* ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
* WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
* DISCLAIMED. IN NO EVENT SHALL Stephan Pfab BE LIABLE FOR ANY
* DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
* (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
* LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
* ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
* (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
* SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
* **** END LICENSE BLOCK ****
*/
public abstract class Tests13FileStore extends Tests12DifferentFS {

    public Tests13FileStore( Capa capa) {
        super(capa);
//        attributes.put( "FileStore", capa::hasFileStores );
    }

    public static class CapaBuilder13 extends CapaBuilder11 {

        public FileStoreBuilder fileStores() {
            return new FileStoreBuilder((AllCapabilitiesBuilder) this);
        }
        
        public static class FileStoreBuilder extends DetailBuilder {
            public FileStoreBuilder(AllCapabilitiesBuilder builder) {
                super(builder);
            }

            @Override
            public AllCapabilitiesBuilder onOff(boolean val) {
                capa.addFeature( "FileStore", val );
                return builder;
            }
        }
        
    }
    
    @Test
    public void testFileStoreIterable() {
        assertThat(FS.getFileStores(), not( emptyIterable()));
    }

    @Test
    public void testFileStoresHaveAName() {
        for (FileStore store : FS.getFileStores() ) {
            assertThat(store.name(), notNullValue());
        }
    }

    @Test
    public void testFileStoresHaveAType() {
        for (FileStore store : FS.getFileStores() ) {
            assertThat(store.type(), notNullValue());
        }
    }

    @Test
    public void testFileStoreTotalSpaceIsNonNegative() throws IOException {
        for (FileStore store : FS.getFileStores() ) {
            assertThat(store.getTotalSpace(), greaterThanOrEqualTo(0L));
        }
    }

    @Test
    public void testFileStoreUsableSpaceIsSmallerThanTotal() throws IOException {
        for (FileStore store : FS.getFileStores() ) {
            assertThat(store.getTotalSpace(), greaterThanOrEqualTo(store.getUsableSpace()));
        }
    }

    @Test
    public void testFileStoreUnallocatedSpaceIsSmallerUsableSpace() throws IOException {
        for (FileStore store : FS.getFileStores() ) {
            assertThat( store.getUnallocatedSpace(), greaterThanOrEqualTo(store.getUsableSpace()));
        }
    }

//    @Test
//    public void testPathFileStoreIsOneOfGeneralOnes() throws IOException {
//        assertThat(FS.getFileStores(), hasItem(FS.provider().getFileStore(getPathPAf())));
//    }

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


//    @Test
//    public void testPathFileStoreGrowingFileLowersUsableSpace() throws IOException {
//        Path      file = getPathPAf();
//        FileStore store = FS.provider().getFileStore(file);
//        long      before = store.getUnallocatedSpace();
//        Files.write(file, CONTENT20k );
//
////        assertThat( store.getUnallocatedSpace(), lessThanOrEqualTo( before + CONTENT.length - CONTENT20k.length ));
//        assertThat( store.getUsableSpace(), lessThanOrEqualTo( before  ));
//    }
//
//    @Test
//    public void testFileStoreShowsThatBasicFileAttributeViewIsSupported() throws IOException {
//        FileStore store = FS.provider().getFileStore( getDefaultPath() );
//
//        Assert.assertThat(store.supportsFileAttributeView(BasicFileAttributeView.class), CoreMatchers.is(true));
//    }


    ////    @Test
////    public void testAAA15() throws IOException {
////        assumeThat( capabilities.supportsFileStores(), is(true));
////        assumeThat( capabilities.hasSizeLimitedFileSystem(), is(true));
////
////        FileStore store = sizeLimitedPlayground.getFileSystem().provider().getFileStore(sizeLimitedPlayground);
////        long      limit = store.getUnallocatedSpace();
////
////        assertThat( limit, lessThan(15000L));
////        assumeThat( limit, greaterThan(1000L));
////    }
////
////    @Test()
////    public void testCanCreateFileWithinLimits() throws IOException {
////        assumeThat( capabilities.supportsFileStores(), is(true));
////        assumeThat( capabilities.hasSizeLimitedFileSystem(), is(true));
////
////        FileStore store = sizeLimitedPlayground.getFileSystem().provider().getFileStore(sizeLimitedPlayground);
//////        long      before = store.getUnallocatedSpace();
////
////        Path file = sizeLimitedPlayground.resolve( nameStr[0]);
////
////        Files.write(file, CONTENT);
////    }
////
////    @Test( expected = IOException.class )
////    public void testCanNotCreateFileToExeedStore() throws IOException {
////        assumeThat( capabilities.supportsFileStores(), is(true));
////        assumeThat( capabilities.hasSizeLimitedFileSystem(), is(true));
////
////        FileStore store = sizeLimitedPlayground.getFileSystem().provider().getFileStore(sizeLimitedPlayground);
////        long      before = store.getUnallocatedSpace();
////
////        assumeThat( before, lessThan(15000L));
////
////        Path file = sizeLimitedPlayground.resolve( nameStr[0]);
////        Files.write(file, CONTENT20k);
////
////    }
////
////    @Test( expected = IOException.class )
////    public void testCanNotModifyFileToExeedStore() throws IOException {
////        assumeThat( capabilities.supportsFileStores(), is(true));
////        assumeThat( capabilities.hasSizeLimitedFileSystem(), is(true));
////
////        FileStore store = sizeLimitedPlayground.getFileSystem().provider().getFileStore(sizeLimitedPlayground);
////        long      before = store.getUnallocatedSpace();
////
////        Path file = sizeLimitedPlayground.resolve( nameStr[0]);
////
////        assumeThat( before, lessThan(15000L));
////        assumeThat( before, greaterThan(1000L));
////
////        Files.write(file, CONTENT);
////        Files.write(file, CONTENT20k);
////
////    }

}
