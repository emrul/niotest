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
public abstract class PathTest9WrongProviderIT extends PathTest8ThreadSafeIT {

    private Path other = Paths.get( "foo" );

    @Test( expected = ProviderMismatchException.class )
    public void testNewByteChannelOtherProvider() throws IOException {
        assumeThat( FS, not(is( FileSystems.getDefault())));

        FS.provider().newByteChannel( other, Collections.<OpenOption>emptySet() );
    }

    @Test( expected = ProviderMismatchException.class )
    public void testGetBasicFileAttributeViewProvider() throws IOException {
        assumeThat( FS, not(is( FileSystems.getDefault())));

        FS.provider().getFileAttributeView( other, BasicFileAttributeView.class );
    }

    @Test( expected = ProviderMismatchException.class )
    public void testCreateDirectoryOtherProvider() throws IOException {
        assumeThat( FS, not(is( FileSystems.getDefault())));

        FS.provider().createDirectory( other );
    }

    @Test( expected = ProviderMismatchException.class )
    public void testNewFileChannelOtherProvider() throws IOException {
        assumeThat( FS, not(is( FileSystems.getDefault())));
        assumeThat( capabilities.hasFileChannels(), is(true) );

        FS.provider().newFileChannel( other, Collections.<OpenOption>emptySet() );
    }

    @Test( expected = ProviderMismatchException.class )
    public void testCheckAccessOtherProvider() throws IOException {
        assumeThat( FS, not(is( FileSystems.getDefault())));

        FS.provider().checkAccess( other );
    }

    @Test( expected = ProviderMismatchException.class )
    public void testCopyOtherProviderFrom() throws IOException {
        assumeThat( FS, not(is( FileSystems.getDefault())));

        FS.provider().copy( other, getPathPABf() );
    }

    @Test( expected = ProviderMismatchException.class )
    public void testCopyOtherProviderTo() throws IOException {
        assumeThat( FS, not(is( FileSystems.getDefault())));

        FS.provider().copy( getPathPABf(), other );
    }

    @Test
    public void testCopyOtherProviderWithFiles() throws IOException {
        assumeThat( FS, not(is( FileSystems.getDefault())));

        Path defaultTarget = PathUtils.getTmpDir("foo").resolve("duh");
        Files.createDirectories( defaultTarget.getParent());

        Files.copy(getPathPABf(), defaultTarget);

        assertThat( Files.readAllBytes(defaultTarget), is(CONTENT));
        Files.deleteIfExists(defaultTarget);
    }

    @Test( expected = ProviderMismatchException.class )
    public void testMoveOtherProviderFrom() throws IOException {
        assumeThat( FS, not(is( FileSystems.getDefault())));

        FS.provider().move( other, getPathPB() );
    }

    @Test( expected = ProviderMismatchException.class )
    public void testMoveOtherProviderTo() throws IOException {
        assumeThat( FS, not(is( FileSystems.getDefault())));

        FS.provider().move( getPathPABf(), other );
    }

    @Test
    public void testMoveOtherProviderWithFiles() throws IOException {
        assumeThat( FS, not(is( FileSystems.getDefault())));

        Path defaultTarget = PathUtils.getTmpDir("foo").resolve("duh");
        Files.createDirectories( defaultTarget.getParent());

        Files.move(getPathPABf(), defaultTarget);

        assertThat( Files.readAllBytes(defaultTarget), is(CONTENT));
        Files.deleteIfExists(defaultTarget);
    }

    @Test( expected = ProviderMismatchException.class )
    public void testCreateLinkOtherProvider() throws IOException {
        assumeThat( FS, not(is( FileSystems.getDefault())));
        assumeThat( capabilities.hasLinks(), is(true) );

        FS.provider().createLink( other, other );
    }

    @Test( expected = ProviderMismatchException.class )
    public void testCreateSymbolicLinkOtherProvider() throws IOException {
        assumeThat( FS, not(is( FileSystems.getDefault())));
        assumeThat( capabilities.hasSymbolicLinks(), is(true) );

        FS.provider().createSymbolicLink( other, other );
    }

    @Test( expected = ProviderMismatchException.class )
    public void testDeleteOtherProvider() throws IOException {
        assumeThat( FS, not(is( FileSystems.getDefault())));

        FS.provider().delete( other );
    }

    @Test( expected = ProviderMismatchException.class )
    public void testDeleteIfExistsOtherProvider() throws IOException {
        assumeThat( FS, not(is( FileSystems.getDefault())));

        FS.provider().deleteIfExists( other );
    }

    @Test( expected = ProviderMismatchException.class )
    public void testGetFileStoreOtherProvider() throws IOException {
        assumeThat( FS, not(is( FileSystems.getDefault())));

        FS.provider().getFileStore( other );
    }

    @Test( expected = IllegalArgumentException.class )
    public void testGetPathOtherURI() throws IOException {
        assumeThat( FS, not(is( FileSystems.getDefault())));

        FS.provider().getPath( other.toUri() );
    }

    @Test( expected = IllegalArgumentException.class )
    public void testGetFileSystemOtherURI() throws IOException {
        assumeThat( FS, not(is( FileSystems.getDefault())));

        FS.provider().getFileSystem( other.toUri() );
    }

    @Test( expected = ProviderMismatchException.class )
    public void testIsHiddenOtherProvider() throws IOException {
        assumeThat( FS, not(is( FileSystems.getDefault())));

        FS.provider().isHidden( other );
    }



    @Test( expected = ProviderMismatchException.class )
    public void testNewAsynchronousFileChannelOtherProvider() throws IOException {
        assumeThat( FS, not(is( FileSystems.getDefault())));
        assumeThat( capabilities.hasAsynchronousFileChannels(), is(true) );

        FS.provider().newAsynchronousFileChannel( other, Collections.<OpenOption>emptySet(), null );
    }

//    @Test( expected = ProviderMismatchException.class )
//    public void testNewInputStreamOtherProvider() throws IOException {
//        assumeThat( FS, not(is( FileSystems.getDefault())));
//
//        FS.provider().newInputStream( other );
//    }

    @Test( expected = ProviderMismatchException.class )
    public void testNewOutputStreamOtherProvider() throws IOException {
        assumeThat( FS, not(is( FileSystems.getDefault())));

        FS.provider().newOutputStream( other );
    }

    @Test( expected = ProviderMismatchException.class )
    public void testNewDirectoryStreamOtherProvider() throws IOException {
        assumeThat( FS, not(is( FileSystems.getDefault())));

        FS.provider().newDirectoryStream( other, null );
    }

    @Test( expected = ProviderMismatchException.class )
    public void testReadAttributesOtherProvider() throws IOException {
        assumeThat( FS, not(is( FileSystems.getDefault())));

        FS.provider().readAttributes( other, BasicFileAttributes.class );
    }

    @Test( expected = ProviderMismatchException.class )
    public void testReadAttributesStringOtherProvider() throws IOException {
        assumeThat( FS, not(is( FileSystems.getDefault())));

        FS.provider().readAttributes( other, "*" );
    }

    @Test( expected = ProviderMismatchException.class )
    public void testReadSymbolicLinkOtherProvider() throws IOException {
        assumeThat( FS, not(is( FileSystems.getDefault())));
        assumeThat( capabilities.hasSymbolicLinks(), is(true) );

        FS.provider().readSymbolicLink( other );
    }

}
