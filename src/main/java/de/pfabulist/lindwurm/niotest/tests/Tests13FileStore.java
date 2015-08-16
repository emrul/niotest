package de.pfabulist.lindwurm.niotest.tests;

import de.pfabulist.lindwurm.niotest.tests.topics.Exclusive;
import de.pfabulist.lindwurm.niotest.tests.topics.FileStores;
import de.pfabulist.lindwurm.niotest.tests.topics.SizeLimit;
import de.pfabulist.lindwurm.niotest.tests.topics.Writable;
import de.pfabulist.unchecked.Filess;
import de.pfabulist.unchecked.Unchecked;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.IOException;
import java.nio.file.FileStore;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributeView;
import java.util.IntSummaryStatistics;
import java.util.UUID;
import java.util.stream.IntStream;

import static de.pfabulist.unchecked.Unchecked.u;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.collection.IsEmptyIterable.emptyIterable;
import static org.hamcrest.core.Is.is;

/**
 * ** BEGIN LICENSE BLOCK *****
 * BSD License (2 clause)
 * Copyright (c) 2006 - 2015, Stephan Pfab
 * All rights reserved.
 * <p>
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * <p>
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

    public Tests13FileStore( FSDescription capa ) {
        super( capa );
    }

    @Test
    @Category( FileStores.class )
    public void testFileStoreIterable() {
        assertThat( FS.getFileStores(), not( emptyIterable() ) );
    }

    @Test( expected = NoSuchFileException.class )
    @Category( FileStores.class )
    public void testGetFileStoreOfNonExistent() throws IOException {
        Files.getFileStore( getNonExistingPath() );
    }

    @Test
    @Category( FileStores.class )
    public void testFileStoresHaveAName() {
        for( FileStore store : FS.getFileStores() ) {
            assertThat( store.name(), notNullValue() );
        }
    }

    @Test
    @Category( FileStores.class )
    public void testFileStoresHaveAType() {
        for( FileStore store : FS.getFileStores() ) {
            assertThat( store.type(), notNullValue() );
        }
    }

    @Test
    @Category( FileStores.class )
    public void testFileStoreTotalSpaceIsNonNegative() throws IOException {
        for( FileStore store : FS.getFileStores() ) {
            assertThat( store.getTotalSpace(), greaterThanOrEqualTo( 0L ) );
        }
    }

    @Test
    @Category( FileStores.class )
    public void testFileStoreUsableSpaceIsSmallerThanTotal() throws IOException {
        for( FileStore store : FS.getFileStores() ) {
            assertThat( store.getTotalSpace(), greaterThanOrEqualTo( store.getUsableSpace() ) );
        }
    }

    @Test
    @Category( FileStores.class )
    public void testFileStoreUnallocatedSpaceIsSmallerUsableSpace() throws IOException {
        for( FileStore store : FS.getFileStores() ) {
            assertThat( store.getUnallocatedSpace(), greaterThanOrEqualTo( store.getUsableSpace() ) );
        }
    }

//    @Test
//    public void testPathFileStoreIsOneOfGeneralOnes() throws IOException {
//        assertThat(FS.getFileStores(), hasItem(FS.provider().getFileStore(getPathPAf())));
//    }

    @Test
    @Category( { FileStores.class, Exclusive.class, Writable.class } )
    public void testPathFileStoreGrowingFileLowersUnallocatedSpace() throws IOException {

        Path file = fileTA();
        FileStore store = FS.provider().getFileStore( file );
        long before = store.getUnallocatedSpace();
        Files.write( file, CONTENT50 );

        assertThat( store.getUnallocatedSpace(), lessThanOrEqualTo( before ) );
    }

    @Test
    @Category( { FileStores.class, Exclusive.class, Writable.class } )
    public void testPathFileStoreGrowingFileLowersUsableSpace() throws IOException {
        Path file = fileTA();
        FileStore store = FS.provider().getFileStore( file );
        long before = store.getUnallocatedSpace();
        for ( int i = 0; i < 10; i++ ) {
            Files.write( file, CONTENT20k, StandardOpenOption.APPEND );
        }

        assertThat( store.getUsableSpace(), lessThanOrEqualTo( before ) );
    }

    @Test
    @Category( { FileStores.class } )
    public void testFileStoreShowsThatBasicFileAttributeViewIsSupported() throws IOException {
        FileStore store = FS.provider().getFileStore( FS.getPath( "" ).toAbsolutePath() );

        assertThat( store.supportsFileAttributeView( BasicFileAttributeView.class ), is( true ) );
    }

    @Test
    @Category( { FileStores.class, SizeLimit.class, Exclusive.class } )
    public void testFileStoreLimitPreventsFileCreation() throws IOException {

        FileStore store = sizeLimitedRoot().getFileSystem().provider().getFileStore( sizeLimitedRoot() );

        while( store.getUsableSpace() > 20000 ) {
            Files.write( sizeLimitedRoot().resolve( UUID.randomUUID().toString() ), CONTENT20k );
        }

        assertThatThrownBy( () -> Files.write( sizeLimitedRoot().resolve( UUID.randomUUID().toString() ), CONTENT20k ) ).
                isInstanceOf( IOException.class );
    }

    @Test
    @Category( { FileStores.class, SizeLimit.class, Exclusive.class } )
    public void testFileStoreLimitPreventsFileModification() throws IOException {

        FileStore store = sizeLimitedRoot().getFileSystem().provider().getFileStore( sizeLimitedRoot() );

        while( store.getUsableSpace() > 20000 ) {
            Files.write( sizeLimitedRoot().resolve( UUID.randomUUID().toString() ), CONTENT20k );
        }

        assertThatThrownBy( () -> Files.write( fileInLimitedPlayground(), CONTENT20k, StandardOpenOption.APPEND ) ).
                isInstanceOf( IOException.class );
    }

    @Test
    @Category( { FileStores.class, SizeLimit.class, Exclusive.class } )
    public void testFileStoreLimitPreventsCopy() throws IOException {

        FileStore store = sizeLimitedRoot().getFileSystem().provider().getFileStore( sizeLimitedRoot() );

        while( store.getUsableSpace() > 20000 ) {
            Files.write( sizeLimitedRoot().resolve( UUID.randomUUID().toString() ), CONTENT20k );
        }

        Path target = sizeLimitedRoot().resolve( "target" );

        assertThatThrownBy( () -> Files.copy( fileInLimitedPlayground(), target ) ).
                            isInstanceOf( IOException.class );
    }

    @Test()
    @Category( { FileStores.class, SizeLimit.class, Exclusive.class } )
    public void testCanCreateFileWithinLimits() throws IOException {

        FileStore store = sizeLimitedRoot().getFileSystem().provider().getFileStore( sizeLimitedRoot() );

        assertThat( store.getUsableSpace(), greaterThanOrEqualTo( (long) CONTENT.length ) );

        Path file = sizeLimitedRoot().resolve( UUID.randomUUID().toString() );
        Files.write( file, CONTENT );

        assertThat( Files.readAllBytes( file ), is( CONTENT ) );
    }


    public Path sizeLimitedRoot() {
        Path ret = description.get( Path.class, "sizeLimitedPlayground" ).resolve( testMethodName.getMethodName() );
        Filess.createDirectories( ret );
        fileInLimitedPlayground();

        return ret;
    }

    public Path fileInLimitedPlayground() {
        Path ret = description.get( Path.class, "sizeLimitedPlayground" ).resolve( "bigggg" );

        if ( !Files.exists( ret )) {
            try {
                Files.write( ret, CONTENT20k );
            } catch( IOException e ) {
                throw u( e );
            }
        }

        return ret;
    }

}
