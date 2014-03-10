package org.opencage.lindwurm.niotest.tests;

import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.junit.Test;
import org.opencage.kleinod.collection.Sets;
import org.opencage.kleinod.paths.PathUtils;
import org.opencage.lindwurm.niotest.matcher.Assumes;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collections;

import static java.nio.file.StandardOpenOption.READ;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assume.assumeThat;
import static org.junit.Assume.assumeTrue;
import static org.opencage.lindwurm.niotest.matcher.Assumes.*;

/**
 * ** BEGIN LICENSE BLOCK *****
 * BSD License (2 clause)
 * Copyright (c) 2006 - 2013, Stephan Pfab
 * All rights reserved.
 * <p/>
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * <p/>
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
        assumeThat( capabilities.isClosable(), is(true ) );
        assertThat( getClosedFS(), notNullValue() );
    }


    @Test

    public void testClosedFSisClosed() throws Exception {
        assumeThat( capabilities.isClosable(), is(true ) );

        assertThat( getClosedFS().isOpen(), is(false) );
    }



    @Test
    public void testStdFSisOpen() throws Exception {
        assumeThat( capabilities.isClosable(), is(true ) );
    }

    @Test( expected = ClosedFileSystemException.class )
    public void testClosedFSCantRead() throws Exception {
        assumeThat( capabilities.isClosable(), is(true ) );

        Files.readAllBytes( getClosedAf() );
    }

    @Test( expected = ClosedFileSystemException.class )
    public void testClosedFSCantReadDir() throws Exception {
        assumeThat( capabilities.isClosable(), is(true ) );

        Files.newDirectoryStream( getClosedBd() );
    }

    @Test( expected = ClosedChannelException.class )
    public void testClosedFSCantUseReadChannelPosition() throws Exception {
        assumeThat( capabilities.isClosable(), is(true ) );

        getClosedReadChannel().position();
    }

    @Test( expected = ClosedChannelException.class )
    public void testClosedFSCantUseReadChannelRead() throws Exception {
        assumeThat( capabilities.isClosable(), is(true ) );

        getClosedReadChannel().read( ByteBuffer.allocate(2) );
    }

    @Test( expected = ClosedChannelException.class )
    public void testClosedFSCantUseReadChannelSize() throws Exception {
        assumeThat( capabilities.isClosable(), is(true ) );

        getClosedReadChannel().size();
    }

    // todo test all other methods on all other channels

    @Test( expected = FileSystemNotFoundException.class )
    public void testCantGetClosedFSViaURI() throws Exception {
        assumeThat(capabilities.isClosable(), is(true));
        getClosedFSProvider().getFileSystem(getClosedURI());
    }

    @Test( expected = ClosedFileSystemException.class )
    public void testClosedFSnewByteChannel() throws Exception {
        assumeThat(capabilities.isClosable(), is(true));
        getClosedFS();
        FS.provider().newByteChannel( getClosedAf(), Sets.asSet( READ ) );
    }

    @Test( expected = ClosedFileSystemException.class )
    public void testClosedFSGetBasicFileAttributeViewProvider() throws IOException {
        assumeThat( capabilities.isClosable(), is(true ) );
        getClosedFS();
        FS.provider().getFileAttributeView( getClosedAf(), BasicFileAttributeView.class );
    }

    @Test( expected = ClosedFileSystemException.class )
    public void testClosedFSCreateDirectoryOtherProvider() throws IOException {
        assumeThat(capabilities.isClosable(), is(true));
        getClosedFSProvider().createDirectory(getClosedAf());
    }

    @Test( expected = ClosedFileSystemException.class )
    public void testClosedFSNewFileChannel() throws IOException {
        assumeThat( capabilities.isClosable(), is(true ) );
        assumeThat( capabilities.hasFileChannels(), is(true));
        getClosedFSProvider().newFileChannel( getClosedAf(), Collections.<OpenOption>emptySet() );
    }

    @Test( expected = ClosedFileSystemException.class )
    public void testClosedFSCheckAccess() throws IOException {
        assumeThat( capabilities.isClosable(), is(true ) );
        getClosedFS();
        FS.provider().checkAccess( getClosedAf() );
    }

    @Test( expected = ClosedFileSystemException.class )
    public void testCopyFromClosedFS() throws IOException {
        assumeThat( capabilities.isClosable(), is(true ) );
        getClosedFSProvider().copy( getClosedAf(), getPathPB() );
    }

    @Test( expected = ClosedFileSystemException.class )
    public void testCopyToClosedFS() throws IOException {
        assumeThat( capabilities.isClosable(), is(true ) );
        getClosedFSProvider().copy( getPathPB(), getClosedAf() );
    }

//    @Test
//    public void testCopyOtherProviderWithFiles() throws IOException {
//        Path defaultTarget = PathUtils.getTmpDir("foo").resolve("duh");
//        Files.createDirectories( defaultTarget.getParent());
//
//        Files.copy(getPathPABf(), defaultTarget);
//
//        MatcherAssert.assertThat(Files.readAllBytes(defaultTarget), Is.is(CONTENT));
//        Files.deleteIfExists(defaultTarget);
//    }
//
//    @Test( expected = ProviderMismatchException.class )
//    public void testMoveOtherProviderFrom() throws IOException {
//        FS.provider().move( getOther(), getPathPB() );
//    }
//
    @Test( expected = ClosedFileSystemException.class )
    public void testMoveToClosedFS() throws IOException {
        assumeThat( capabilities.isClosable(), is(true ) );
        getClosedFSProvider().move( getPathPABf(), getClosedAf() );
    }
//
//    @Test
//    public void testMoveOtherProviderWithFiles() throws IOException {
//        Path defaultTarget = PathUtils.getTmpDir("foo").resolve("duh");
//        Files.createDirectories( defaultTarget.getParent());
//
//        Files.move(getPathPABf(), defaultTarget);
//
//        MatcherAssert.assertThat(Files.readAllBytes(defaultTarget), Is.is(CONTENT));
//        Files.deleteIfExists(defaultTarget);
//    }
//
    @Test( expected = ClosedFileSystemException.class )
    public void testClosedFSCreateLink() throws IOException {
        assumeThat( capabilities.hasLinks(), is(true) );
        assumeThat( capabilities.isClosable(), is(true ) );

        getClosedFSProvider().createLink( getClosedAf(), getClosedAf() );
    }

    @Test( expected = ProviderMismatchException.class )
    public void testCreateSymbolicLinkOtherProvider() throws IOException {
        assumeThat( capabilities.hasSymbolicLinks(), Is.is(true) );
        assumeThat( capabilities.isClosable(), is(true ) );

        getClosedFSProvider().createSymbolicLink( getClosedAf(), getClosedAf() );
    }

    @Test( expected = ClosedFileSystemException.class )
    public void testClosedFSDelete() throws IOException {
        Assumes.assumeThat(capabilities.isClosable());
        getClosedFSProvider().delete( getClosedAf() );
    }

    @Test( expected = ClosedFileSystemException.class )
    public void testClosedFSGetFileStore() throws IOException {
        assumeTrue(capabilities.isClosable());
        getClosedFSProvider().getFileStore( getClosedAf() );
    }

//    @Test( expected = IllegalArgumentException.class )
//    public void testClosedFSGetPath() throws IOException {
//        assumeTrue(capabilities.isClosable());
//        getClosedFSProvider().getPath( getClosedURI() );
//    }


//    @Test( expected = IllegalArgumentException.class )
//    public void testClosedFSGetFileSystemOtherURI() throws IOException {
//        FS.provider().getFileSystem( getOther().toUri() );
//    }

    @Test( expected = ClosedFileSystemException.class )
    public void testClosedFSIsHidden() throws IOException {
        assumeTrue(capabilities.isClosable());
        getClosedFSProvider().isHidden( getClosedAf() );
    }




    @Test( expected = ClosedFileSystemException.class )
    public void testClosedFSNewAsynchronousFileChannel() throws IOException {
        assumeTrue(capabilities.isClosable());
        assumeThat( capabilities.hasAsynchronousFileChannels(), Is.is(true) );

        getClosedFSProvider().newAsynchronousFileChannel( getClosedAf(), Collections.<OpenOption>emptySet(), null );
    }

    @Test( expected = ClosedFileSystemException.class )
    public void testClosedFSNewInputStream() throws IOException {
        assumeTrue(capabilities.isClosable());
        getClosedFSProvider().newOutputStream( getClosedAf() );
    }
//
    @Test( expected = ClosedFileSystemException.class )
    public void testClosedFSNewOutputStream() throws IOException {
        assumeTrue(capabilities.isClosable());
        getClosedFSProvider().newOutputStream( getClosedAf() );
    }


    @Test( expected = ClosedFileSystemException.class )
    public void testClosedFSReadAttributes() throws IOException {
        assumeTrue(capabilities.isClosable());
        getClosedFSProvider().readAttributes( getClosedAf(), BasicFileAttributes.class );
    }

    @Test( expected = ClosedFileSystemException.class )
    public void testClosedFSReadAttributesString() throws IOException {
        assumeTrue(capabilities.isClosable());
        getClosedFSProvider().readAttributes( getClosedAf(), "*" );
    }
//
//    @Test( expected = ProviderMismatchException.class )
//    public void testReadSymbolicLinkOtherProvider() throws IOException {
//        assumeThat( capabilities.hasSymbolicLinks(), Is.is(true) );
//
//        FS.provider().readSymbolicLink( getOther() );
//    }

//
//
//    @Test( expected = ClosedFileSystemException.class )
//    public void testOpenDirectoryStreamFromClosedFSThrows() throws Exception {
//        assumeThat( capabilities.isClosable(), is(true) );
//
//        Path dir = getDefaultPath();
//        FS.close();
//        try ( DirectoryStream<Path> ch = Files.newDirectoryStream( dir )) {
//        }
//    }
//    @Test( expected = ClosedFileSystemException.class )

//    public void testReadFromClosedFSThrows() throws Exception {
//        assumeThat( capabilities.isClosable(), is(true) );
//
//        Path file = getPathPAf();
//        FS.close();
//        Files.readAllBytes( file );
//    }
//

}
