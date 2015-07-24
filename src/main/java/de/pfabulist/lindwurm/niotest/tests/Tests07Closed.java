package de.pfabulist.lindwurm.niotest.tests;

import de.pfabulist.kleinod.collection.Sets;
import de.pfabulist.lindwurm.niotest.tests.topics.Closable;
import de.pfabulist.lindwurm.niotest.tests.topics.FileChannelT;
import de.pfabulist.lindwurm.niotest.tests.topics.Move;
import de.pfabulist.lindwurm.niotest.tests.topics.Writable;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.ClosedFileSystemException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.spi.FileSystemProvider;
import java.util.Collections;

import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.READ;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

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
public abstract class Tests07Closed extends Tests06Attributes {

    public static final String CLOSEABLE_PLAYGROUND = "closeablePlayground";

    public Tests07Closed( FSDescription description ) {
        super( description );
    }

    @Test
    @Category( Closable.class )
    public void testClosedFSisClosed() throws Exception {
        assertThat( getClosedFS().isOpen(), is( false ) );
    }

    @Test( expected = ClosedFileSystemException.class )
    @Category( Closable.class )
    public void testClosedFSCantRead() throws Exception {
        Files.readAllBytes( getClosedFileA() );
    }

    @Test( expected = ClosedFileSystemException.class )
    @Category( Closable.class )
    public void testClosedFSCantReadDir() throws Exception {
        Files.newDirectoryStream( getClosedDirB() );
    }

    @Test( expected = ClosedChannelException.class )
    @Category( Closable.class )
    public void testClosedFSCantUseReadChannelPosition() throws Exception {
        getClosedReadChannel().position();
    }

    @Test( expected = ClosedChannelException.class )
    @Category( Closable.class )
    public void testClosedFSCantUseReadChannelRead() throws Exception {
        getClosedReadChannel().read( ByteBuffer.allocate( 2 ) );
    }

    @Test( expected = ClosedChannelException.class )
    @Category( Closable.class )
    public void testClosedFSCantUseReadChannelSize() throws Exception {
        getClosedReadChannel().size();
    }

    //
//    // todo test all other methods on all other channels
//
    @Test( expected = FileSystemNotFoundException.class )
    @Category( Closable.class )
    public void testCantGetClosedFSViaURI() throws Exception {
        getClosedFSProvider().getFileSystem( getClosedURI() );
    }

    @Test( expected = ClosedFileSystemException.class )
    @Category( Closable.class )
    public void testClosedFSnewByteChannel() throws Exception {
        getClosedFS();

        FS.provider().newByteChannel( getClosedFileA(), Sets.asSet( READ ) );
    }

    @Test( expected = ClosedFileSystemException.class )
    @Category( Closable.class )
    public void testClosedFSGetBasicFileAttributeViewProvider() throws IOException {
        getClosedFS();
        FS.provider().getFileAttributeView( getClosedFileA(), BasicFileAttributeView.class );
    }

    @Test( expected = ClosedFileSystemException.class )
    @Category( Closable.class )
    public void testClosedFSCreateDirectoryOtherProvider() throws IOException {
        getClosedFSProvider().createDirectory( getClosedFileA() );
    }

    @Test( expected = ClosedFileSystemException.class )
    @Category({ Closable.class, FileChannelT.class })
    public void testClosedFSNewFileChannel() throws IOException {
        getClosedFSProvider().newFileChannel( getClosedFileA(), Collections.<OpenOption> emptySet() );
    }

    @Test( expected = ClosedFileSystemException.class )
    @Category( Closable.class )
    public void testClosedFSCheckAccess() throws IOException {
        getClosedFS();
        FS.provider().checkAccess( getClosedFileA() );
    }

    @Test( expected = ClosedFileSystemException.class )
    @Category({ Closable.class, Writable.class })
    public void testCopyFromClosedFS() throws IOException {
        getClosedFSProvider().copy( getClosedFileA(), dirTA() );
    }

    @Test( expected = ClosedFileSystemException.class )
    @Category({ Closable.class, Writable.class })
    public void testCopyToClosedFS() throws IOException {
        getClosedFSProvider().copy( fileTA(), getClosedFileA() );
    }

    @Test( expected = ClosedFileSystemException.class )
    @Category({ Closable.class, Writable.class, Move.class })
    public void testMoveToClosedFS() throws IOException {
        getClosedFSProvider().move( fileTA(), getClosedFileA() );
    }

    @Test( expected = ClosedFileSystemException.class )
    @Category( Closable.class )
    public void testClosedFSCreateHardLink() throws IOException {
        getClosedFSProvider().createLink( getClosedFileA(), getClosedFileA() );
    }

    //    //TODO
//    @Test( expected = ProviderMismatchException.class )
//    @Category( Closable.class  )     public void testCreateSymbolicLinkOtherProvider() throws IOException {
//        assumeThat( descriptionbilities.hasSymbolicLinks(), Is.is(true) );
//        assumeThat( descriptionbilities.isClosable(), is(true ) );
//
//        getClosedFSProvider().createSymbolicLink( getClosedFileA(), getClosedFileA() );
//    }
//
    @Test( expected = ClosedFileSystemException.class )
    @Category( Closable.class )
    public void testClosedFSDelete() throws IOException {
        getClosedFSProvider().delete( getClosedFileA() );
    }

    @Test( expected = ClosedFileSystemException.class )
    @Category( Closable.class )
    public void testClosedFSGetFileStore() throws IOException {
        getClosedFSProvider().getFileStore( getClosedFileA() );
    }

    @Test( expected = ClosedFileSystemException.class )
    @Category( Closable.class )
    public void testClosedFSIsHidden() throws IOException {
        getClosedFSProvider().isHidden( getClosedFileA() );
    }

    // todo
//    @Test( expected = ClosedFileSystemException.class )
//    @Category( Closable.class  )     public void testClosedFSNewAsynchronousFileChannel() throws IOException {
//        assumeThat( descriptionbilities.hasAsynchronousFileChannels(), Is.is(true) );
//
//        getClosedFSProvider().newAsynchronousFileChannel( getClosedFileA(), Collections.<OpenOption>emptySet(), null );
//    }
//
    @Test( expected = ClosedFileSystemException.class )
    @Category( Closable.class )
    public void testClosedFSNewInputStream() throws IOException {
        getClosedFSProvider().newOutputStream( getClosedFileA() );
    }

    //
    @Test( expected = ClosedFileSystemException.class )
    @Category( Closable.class )
    public void testClosedFSNewOutputStream() throws IOException {
        getClosedFSProvider().newOutputStream( getClosedFileA() );
    }

    @Test( expected = ClosedFileSystemException.class )
    @Category( Closable.class )
    public void testClosedFSReadAttributes() throws IOException {
        getClosedFSProvider().readAttributes( getClosedFileA(), BasicFileAttributes.class );
    }

    @Test( expected = ClosedFileSystemException.class )
    @Category( Closable.class )
    public void testClosedFSReadAttributesString() throws IOException {
        getClosedFSProvider().readAttributes( getClosedFileA(), "*" );
    }

    @Test( expected = ClosedFileSystemException.class )
    @Category( Closable.class )
    public void testAppendFilesInClosedFSThrows() throws IOException {
        Files.write( getClosedFileA(), CONTENT_OTHER, APPEND );
    }

    /*
     * ----------------------------------------------------------------------------------------
     */

    public FileSystem getClosedFS() throws IOException {

        if( description.closedFSVars == null ) {
            description.closedFSVars = new FSDescription.ClosedFSVars( description.get( Path.class, CLOSEABLE_PLAYGROUND ) );
        }

        if( description.closedFSVars.fs == null ) {
            description.closedFSVars.fs = description.closedFSVars.play.getFileSystem();
        }

        if( !description.closedFSVars.fs.isOpen() ) {
            return description.closedFSVars.fs;
        }

        description.closedFSVars.provider = description.closedFSVars.fs.provider();

        description.closedFSVars.dirB = description.closedFSVars.play.resolve( nameB() );
        Files.createDirectories( description.closedFSVars.dirB );

        description.closedFSVars.fileA = description.closedFSVars.play.resolve( nameA() );
        Files.write( description.closedFSVars.fileA, CONTENT, standardOpen );

        Path closedCf = description.closedFSVars.play.resolve( nameC() );
        Files.write( closedCf, CONTENT, standardOpen );
        description.closedFSVars.readChannel = Files.newByteChannel( closedCf, READ );

        description.closedFSVars.uri = description.closedFSVars.play.getRoot().toUri();

        description.closedFSVars.dirStream = Files.newDirectoryStream( description.closedFSVars.play );

        try {
            description.closedFSVars.watchService = description.closedFSVars.fs.newWatchService();
        } catch( IOException | UnsupportedOperationException e ) { // NOSONAR
            // no watchservice provided
        }

        description.closedFSVars.fs.close();

        return description.closedFSVars.fs;
    }

    public FileSystemProvider getClosedFSProvider() throws IOException {
        getClosedFS();
        return FS.provider();
    }

    public Path getClosedFileA() throws IOException {
        getClosedFS();
        return description.closedFSVars.fileA;
    }

    public Path getClosedDirB() throws IOException {
        getClosedFS();
        return description.closedFSVars.dirB;
    }

    public URI getClosedURI() throws IOException {
        getClosedFS();
        return description.closedFSVars.uri;
    }

    public SeekableByteChannel getClosedReadChannel() throws IOException {
        getClosedFileA();
        return description.closedFSVars.readChannel;
    }

    public WatchService getClosedFSWatchService() throws IOException {
        getClosedFS();
        return description.closedFSVars.watchService;
    }

}
