package org.opencage.niotest.lindwurm;

import org.junit.Ignore;
import org.junit.Test;
import org.opencage.kleinod.collection.Iterators;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.ProviderMismatchException;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeThat;
import static org.junit.Assume.assumeTrue;
import static org.opencage.niotest.lindwurm.matcher.FileTimeMatcher.after;
import static org.opencage.niotest.lindwurm.matcher.PathAbsolute.absolute;
import static org.opencage.niotest.lindwurm.matcher.PathAbsolute.relative;
import static org.opencage.niotest.lindwurm.matcher.PathExists.exists;
import static org.opencage.niotest.lindwurm.matcher.PathIsDirectory.isDirectory;
import static org.opencage.niotest.lindwurm.matcher.PathIsDirectory.isNotDirectory;

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
public class DirTest extends JustPathTest {

    @Test
    public void testDefaultIsDir() throws Exception {
        assertThat( getDefaultPath(), isDirectory() );
    }


    @Test
    public void testContentOfDefault() throws IOException {
        try ( DirectoryStream<Path> stream = Files.newDirectoryStream( getDefaultPath() )) {}
    }

    @Test
    public void testContentOfNonEmptyDir() throws IOException {
        assumeTrue( p.hasNonEmptyDir() );

        try ( DirectoryStream<Path> stream = Files.newDirectoryStream( p.getNonEmptyDir("contentOfNonEmpty") )) {
            assertEquals( p.getCountOfNonEmptyDir(), Iterators.size(stream) );
        }
    }

    @Test
    public void testContentOfNonEmptyDirFiltered() throws IOException {
        assumeTrue( p.hasNonEmptyDir() );

        Path path = p.getNonEmptyDir( "contentOfNonEmptyFiltered" );

        // filter out first kid
        DirectoryStream.Filter<Path> filter = new DirectoryStream.Filter<Path>() {

            boolean first = true;

            @Override
            public boolean accept( Path entry ) throws IOException {
                if ( first ) {
                    first = false;
                    return false;
                }
                return true;
            }
        };

        try ( DirectoryStream<Path> stream =  Files.newDirectoryStream( path, filter )) {
            assertEquals( p.getCountOfNonEmptyDir() - 1, Iterators.size( stream ) );
        }
    }

    @Test
    public void testNewDirIsInParentsDirStream() throws IOException {
        assumeTrue( p.hasNonEmptyDir() );

        Path dir = getPathTmpA();
        Files.createDirectory( dir );

        try( DirectoryStream<Path> kids = Files.newDirectoryStream( dir.getParent() )) {
            assertThat( Iterators.contains( kids, dir ), is(true) );
        }
    }

    @Test
    public void testNewDirectoryExists() throws IOException {
        Path newDir = getPathTmpA();
        Files.createDirectory( newDir );
        assertThat( newDir, exists() );
    }

    @Test( expected = FileAlreadyExistsException.class )
    public void testCreateDirectoryTwiceThrows() throws IOException {
        Path newDir = getPathTmpA();
        Files.createDirectory( newDir );
        Files.createDirectory( newDir );
    }

    @Test( expected = NoSuchFileException.class )
    public void testCreateDirectoryFail() throws IOException {
        Files.createDirectory( p.getNonExistingPath().resolve( p.getLegalPathElement() ));
    }

    // bug in java Path
    @Ignore
    @Test( expected = FileAlreadyExistsException.class )
    public void testCreateDirectoryRoot() throws IOException {
        Files.createDirectory( getRoot() );
    }

    @Test
    public void testRootisADir() throws IOException {
        assertThat( getRoot(), isDirectory() );
    }

    @Test
    public void testDefaultExists() throws Exception {
        assertThat( getDefaultPath(), exists() );
    }


    // TODO
    @Test
    public void testIsDirectoryNotExists() throws IOException {

        assertThat( p.getNonExistingPath().getRoot().relativize( p.getNonExistingPath() ), isNotDirectory() );

        assertThat( p.getNonExistingPath().toAbsolutePath(), isNotDirectory() );
    }

    // try to create dir with same path as existing file

    @Test( expected = FileAlreadyExistsException.class )
    public void testCreateDirWithSamePathAsExistingFileFails() throws Exception {

        Path file = p.getTmpDir("").resolve( "foo" );
        Files.write( file, "hallo".getBytes( "UTF-8" ) );

        Files.createDirectory( file );
    }

    @Test
    public void testCreateDirSetsModifiedDateOfParent() throws IOException, InterruptedException {

        Path tmp = getTmpDir();

        FileTime created = Files.getLastModifiedTime( tmp );

        Thread.sleep( 2000 );

        Files.createDirectory( tmp.resolve( p.getLegalPathElement() ) );
        FileTime modified = Files.getLastModifiedTime( tmp );

        assertTrue( "created after modified", created.compareTo( modified ) < 0 );
    }

    @Test
    public void testCreateDirSetsCreationDate() throws IOException, InterruptedException {

        Path tmp = getTmpDir();
        FileTime before = Files.getLastModifiedTime( tmp );
        Thread.sleep( 1000 );

        Path dir = getPathTmpA( tmp );

        Files.createDirectory( dir );

        BasicFileAttributes atti = Files.readAttributes( dir, BasicFileAttributes.class );

        assertThat( atti.creationTime(), after( before ) );
    }

    @Test( expected = ProviderMismatchException.class )
    public void testCallNewDirectoryStreamWithForeignPath() throws Exception {

        try ( DirectoryStream<Path> ch = p.getProvider().newDirectoryStream( p.getOtherProviderFS().getPath( "" ), new DirectoryStream.Filter<Path>() {
            @Override
            public boolean accept( Path entry ) throws IOException {
                return false;
            }
        })) {}
    }

    @Test
    public void testKidsOfAbsoluteDirAreAbsolute() throws Exception {

        try ( DirectoryStream<Path> kids = Files.newDirectoryStream( p.getNonEmptyDir( "testKidsOfAbsoluteDirAreAbsolute" ) )) {
            for ( Path kid : kids ) {
                assertThat( kid, absolute() );
            }
        }

    }

    @Test
    public void testKidsOfRelativeDirAreRelative() throws Exception {

        Path rel = getPathRelTmpNonEmpty();

        try ( DirectoryStream<Path> kids = Files.newDirectoryStream( rel )) {
            for ( Path kid : kids ) {
                assertThat( kid, relative() );
            }
        }
    }

    @Test
    public void testKisOfRelDirAreLikeTheReslutOfResolve() throws Exception {

        Path rel = getPathRelTmpNonEmpty();

        try ( DirectoryStream<Path> kids = Files.newDirectoryStream( rel )) {
            for ( Path kid : kids ) {
                assertEquals( kid, rel.resolve( kid.getFileName() ) );
            }
        }


    }
}
