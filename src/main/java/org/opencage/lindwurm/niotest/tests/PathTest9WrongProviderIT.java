package org.opencage.lindwurm.niotest.tests;

import org.junit.Test;
import org.opencage.kleinod.paths.PathUtils;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collections;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assume.assumeThat;

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
 * documentation and/or getOther() materials provided with the distribution.
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
public abstract class PathTest9WrongProviderIT extends PathTest8ThreadSafeIT {

    @Test( expected = ProviderMismatchException.class )
    public void testNewByteChannelOtherProvider() throws IOException {
        FS.provider().newByteChannel( getOther(), Collections.<OpenOption>emptySet() );
    }

    @Test( expected = ProviderMismatchException.class )
    public void testGetBasicFileAttributeViewProvider() throws IOException {
        FS.provider().getFileAttributeView( getOther(), BasicFileAttributeView.class );
    }

    @Test( expected = ProviderMismatchException.class )
    public void testCreateDirectoryOtherProvider() throws IOException {
        FS.provider().createDirectory( getOther() );
    }

    @Test( expected = ProviderMismatchException.class )
    public void testNewFileChannelOtherProvider() throws IOException {
        assumeThat( capabilities.hasFileChannels(), is(true) );

        FS.provider().newFileChannel( getOther(), Collections.<OpenOption>emptySet() );
    }

    @Test( expected = ProviderMismatchException.class )
    public void testCheckAccessOtherProvider() throws IOException {
        FS.provider().checkAccess( getOther() );
    }

//    @Test( expected = IllegalArgumentException.class )
//    public void bugCheckAccessOtherProviderThrowsWrongException() throws IOException {
//        FS.provider().checkAccess( getOther() );
//    }

    @Test( expected = ProviderMismatchException.class )
    public void testCopyOtherProviderFrom() throws IOException {
        FS.provider().copy( getOther(), getPathPABf() );
    }

    @Test( expected = ProviderMismatchException.class )
    public void testCopyOtherProviderTo() throws IOException {
        FS.provider().copy( getPathPABf(), getOther() );
    }

    @Test
    public void testCopyOtherProviderWithFiles() throws IOException {
        Path defaultTarget = PathUtils.getTmpDir("foo").resolve("duh");
        Files.createDirectories( defaultTarget.getParent());

        Files.copy(getPathPABf(), defaultTarget);

        assertThat( Files.readAllBytes(defaultTarget), is(CONTENT));
        Files.deleteIfExists(defaultTarget);
    }

    @Test( expected = ProviderMismatchException.class )
    public void testMoveOtherProviderFrom() throws IOException {
        FS.provider().move( getOther(), getPathPB() );
    }

    @Test( expected = ProviderMismatchException.class )
    public void testMoveOtherProviderTo() throws IOException {
        FS.provider().move( getPathPABf(), getOther() );
    }

    @Test
    public void testMoveOtherProviderWithFiles() throws IOException {
        Path defaultTarget = PathUtils.getTmpDir("foo").resolve("duh");
        Files.createDirectories( defaultTarget.getParent());

        Files.move(getPathPABf(), defaultTarget);

        assertThat( Files.readAllBytes(defaultTarget), is(CONTENT));
        Files.deleteIfExists(defaultTarget);
    }

    @Test( expected = ProviderMismatchException.class )
    public void testCreateLinkOtherProvider() throws IOException {
        assumeThat( capabilities.hasLinks(), is(true) );

        FS.provider().createLink( getOther(), getOther() );
    }

    @Test( expected = ProviderMismatchException.class )
    public void testCreateSymbolicLinkOtherProvider() throws IOException {
        assumeThat( capabilities.hasSymbolicLinks(), is(true) );

        FS.provider().createSymbolicLink( getOther(), getOther() );
    }

    @Test( expected = ProviderMismatchException.class )
    public void testDeleteOtherProvider() throws IOException {
        FS.provider().delete( getOther() );
    }

    @Test( expected = ProviderMismatchException.class )
    public void testDeleteIfExistsOtherProvider() throws IOException {
        FS.provider().deleteIfExists( getOther() );
    }

    @Test( expected = ProviderMismatchException.class )
    public void testGetFileStoreOtherProvider() throws IOException {
        FS.provider().getFileStore( getOther() );
    }

    @Test( expected = IllegalArgumentException.class )
    public void testGetPathOtherURI() throws IOException {
        FS.provider().getPath( getOther().toUri() );
    }

    @Test( expected = IllegalArgumentException.class )
    public void testGetFileSystemOtherURI() throws IOException {
        FS.provider().getFileSystem( capabilities.toURI().apply( getOther().getFileSystem()));
    }

    @Test( expected = ProviderMismatchException.class )
    public void testIsHiddenOtherProvider() throws IOException {
        FS.provider().isHidden( getOther() );
    }


    @Test( expected = ProviderMismatchException.class )
    public void testNewAsynchronousFileChannelOtherProvider() throws IOException {
        assumeThat( capabilities.hasAsynchronousFileChannels(), is(true) );

        FS.provider().newAsynchronousFileChannel( getOther(), Collections.<OpenOption>emptySet(), null );
    }

//    @Test( expected = ProviderMismatchException.class )
//    public void testNewInputStreamOtherProvider() throws IOException {
//        assumeThat( FS, not(is( FileSystems.getDefault())));
//
//        FS.provider().newInputStream( getOther() );
//    }

    @Test( expected = ProviderMismatchException.class )
    public void testNewOutputStreamOtherProvider() throws IOException {
        FS.provider().newOutputStream( getOther() );
    }

    @Test( expected = ProviderMismatchException.class )
    public void testNewDirectoryStreamOtherProvider() throws IOException {
        FS.provider().newDirectoryStream( getOther(), null );
    }

    @Test( expected = ProviderMismatchException.class )
    public void testReadAttributesOtherProvider() throws IOException {
        FS.provider().readAttributes( getOther(), BasicFileAttributes.class );
    }

    @Test( expected = ProviderMismatchException.class )
    public void testReadAttributesStringOtherProvider() throws IOException {
        FS.provider().readAttributes( getOther(), "*" );
    }

    @Test( expected = ProviderMismatchException.class )
    public void testReadSymbolicLinkOtherProvider() throws IOException {
        assumeThat( capabilities.hasSymbolicLinks(), is(true) );

        FS.provider().readSymbolicLink( getOther() );
    }

}
