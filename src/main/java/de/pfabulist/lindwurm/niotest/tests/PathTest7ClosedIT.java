package de.pfabulist.lindwurm.niotest.tests;

import de.pfabulist.kleinod.collection.Sets;
import org.hamcrest.core.Is;
import org.junit.Test;

import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.spi.FileSystemProvider;
import java.util.Collections;

import static java.nio.file.StandardOpenOption.READ;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assume.assumeThat;

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
public abstract class PathTest7ClosedIT extends PathTest6AttributesIT {

    // closable FS

    @Test
    public void testAAA7ClosableHasClosedFS() throws Exception {
        assertThat( getClosedFS(), notNullValue() );
    }


    @Test

    public void testClosedFSisClosed() throws Exception {
        assertThat( getClosedFS().isOpen(), is(false) );
    }


    // TODO
    @Test
    public void testStdFSisOpen() throws Exception {
        assumeThat( capabilities.isClosable(), is(true ) );
    }

    @Test( expected = ClosedFileSystemException.class )
    public void testClosedFSCantRead() throws Exception {
        Files.readAllBytes( getClosedAf() );
    }

    @Test( expected = ClosedFileSystemException.class )
    public void testClosedFSCantReadDir() throws Exception {
        Files.newDirectoryStream( getClosedBd() );
    }

    @Test( expected = ClosedChannelException.class )
    public void testClosedFSCantUseReadChannelPosition() throws Exception {
        getClosedReadChannel().position();
    }

    @Test( expected = ClosedChannelException.class )
    public void testClosedFSCantUseReadChannelRead() throws Exception {
        getClosedReadChannel().read( ByteBuffer.allocate(2) );
    }

    @Test( expected = ClosedChannelException.class )
    public void testClosedFSCantUseReadChannelSize() throws Exception {
        getClosedReadChannel().size();
    }

    // todo test all other methods on all other channels

    @Test( expected = FileSystemNotFoundException.class )
    public void testCantGetClosedFSViaURI() throws Exception {
        getClosedFSProvider().getFileSystem(getClosedURI());
    }

    @Test( expected = ClosedFileSystemException.class )
    public void testClosedFSnewByteChannel() throws Exception {
        getClosedFS();
        FS.provider().newByteChannel( getClosedAf(), Sets.asSet(READ) );
    }

    @Test( expected = ClosedFileSystemException.class )
    public void testClosedFSGetBasicFileAttributeViewProvider() throws IOException {
        getClosedFS();
        FS.provider().getFileAttributeView( getClosedAf(), BasicFileAttributeView.class );
    }

    @Test( expected = ClosedFileSystemException.class )
    public void testClosedFSCreateDirectoryOtherProvider() throws IOException {
        getClosedFSProvider().createDirectory(getClosedAf());
    }

    @Test( expected = ClosedFileSystemException.class )
    public void testClosedFSNewFileChannel() throws IOException {
        getClosedFSProvider().newFileChannel( getClosedAf(), Collections.<OpenOption>emptySet() );
    }

    @Test( expected = ClosedFileSystemException.class )
    public void testClosedFSCheckAccess() throws IOException {
        getClosedFS();
        FS.provider().checkAccess( getClosedAf() );
    }

    @Test( expected = ClosedFileSystemException.class )
    public void testCopyFromClosedFS() throws IOException {
        getClosedFSProvider().copy( getClosedAf(), getPathPB() );
    }

    @Test( expected = ClosedFileSystemException.class )
    public void testCopyToClosedFS() throws IOException {
        getClosedFSProvider().copy( getPathPB(), getClosedAf() );
    }

    @Test( expected = ClosedFileSystemException.class )
    public void testMoveToClosedFS() throws IOException {
        getClosedFSProvider().move( getPathPABf(), getClosedAf() );
    }

    @Test( expected = ClosedFileSystemException.class )
    public void testClosedFSCreateHardLink() throws IOException {
        getClosedFSProvider().createLink( getClosedAf(), getClosedAf() );
    }

    //TODO
    @Test( expected = ProviderMismatchException.class )
    public void testCreateSymbolicLinkOtherProvider() throws IOException {
        assumeThat( capabilities.hasSymbolicLinks(), Is.is(true) );
        assumeThat( capabilities.isClosable(), is(true ) );

        getClosedFSProvider().createSymbolicLink( getClosedAf(), getClosedAf() );
    }

    @Test( expected = ClosedFileSystemException.class )
    public void testClosedFSDelete() throws IOException {
        getClosedFSProvider().delete( getClosedAf() );
    }

    @Test( expected = ClosedFileSystemException.class )
    public void testClosedFSGetFileStore() throws IOException {
        getClosedFSProvider().getFileStore( getClosedAf() );
    }


    @Test( expected = ClosedFileSystemException.class )
    public void testClosedFSIsHidden() throws IOException {
        getClosedFSProvider().isHidden( getClosedAf() );
    }




    @Test( expected = ClosedFileSystemException.class )
    public void testClosedFSNewAsynchronousFileChannel() throws IOException {
        assumeThat( capabilities.hasAsynchronousFileChannels(), Is.is(true) );

        getClosedFSProvider().newAsynchronousFileChannel( getClosedAf(), Collections.<OpenOption>emptySet(), null );
    }

    @Test( expected = ClosedFileSystemException.class )
    public void
    testClosedFSNewInputStream() throws IOException {
        getClosedFSProvider().newOutputStream( getClosedAf() );
    }
//
    @Test( expected = ClosedFileSystemException.class )
    public void testClosedFSNewOutputStream() throws IOException {
        getClosedFSProvider().newOutputStream( getClosedAf() );
    }


    @Test( expected = ClosedFileSystemException.class )
    public void testClosedFSReadAttributes() throws IOException {
        getClosedFSProvider().readAttributes( getClosedAf(), BasicFileAttributes.class );
    }

    @Test( expected = ClosedFileSystemException.class )
    public void testClosedFSReadAttributesString() throws IOException {
        getClosedFSProvider().readAttributes( getClosedAf(), "*" );
    }

    private ClosedFSVars closed;

    public void setClosablePlay( ClosedFSVars vars ) {
        this.closed = vars;
    }

    public FileSystem getClosedFS() throws IOException {

        if ( closed.fs == null ) {
            closed.fs = closed.play.getFileSystem();
        }

        if ( !closed.fs.isOpen() ) {
            return closed.fs;
        }

        closed.provider = closed.fs.provider();

        closed.pathAf = closed.play.resolve( nameStr[0] );
        closed.pathBd = closed.play.resolve( nameStr[1] );

        Files.createDirectories(closed.play);
        Files.write( closed.pathAf, CONTENT, standardOpen );
        closed.provider.createDirectory(closed.pathBd);


        Path closedCf = closed.play.resolve( nameStr[2] );
        Files.write( closedCf, CONTENT, standardOpen );
        closed.readChannel = Files.newByteChannel( closedCf, StandardOpenOption.READ );

        closed.uri = closed.play.getRoot().toUri();

        closed.dirStream = Files.newDirectoryStream( closed.play );

        if ( capabilities.supportsWatchService() ) {
            closed.watchService = closed.fs.newWatchService();
        }

        closed.fs.close();

        return closed.fs;
    }

    public FileSystemProvider getClosedFSProvider() throws IOException {
        getClosedFS();
        return FS.provider();
    }

    public Path getClosedAf() throws IOException {
        getClosedFS();
        return closed.pathAf;
    }

    public Path getClosedBd() throws IOException {
        getClosedFS();
        return closed.pathBd;
    }

    public URI getClosedURI() throws IOException {
        getClosedFS();
        return closed.uri;
    }

    public SeekableByteChannel getClosedReadChannel() throws IOException {
        getClosedAf();
        return closed.readChannel;
    }

    public WatchService getClosedFSWatchService() throws IOException {
        getClosedFS();
        return closed.watchService;
    }





}
