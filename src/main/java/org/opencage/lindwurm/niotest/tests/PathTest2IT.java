package org.opencage.lindwurm.niotest.tests;

import org.junit.Test;
import org.opencage.kleinod.collection.Iterators;
import org.opencage.lindwurm.niotest.matcher.PathIsDirectory;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileSystemException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.number.OrderingComparison.greaterThan;
import static org.hamcrest.number.OrderingComparison.lessThan;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeThat;
import static org.opencage.lindwurm.niotest.matcher.PathAbsolute.absolute;
import static org.opencage.lindwurm.niotest.matcher.PathAbsolute.relative;
import static org.opencage.lindwurm.niotest.matcher.PathExists.exists;

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
public abstract class PathTest2IT extends PathTest1IT {

    @Test
    public void testAAA2HasAPlayfield() throws Exception {
        assertThat( "assign a playfield", play, notNullValue() );
    }

    @Test
    public void testDefaultIsDir() throws Exception {
        assertThat( getDefaultPath(), PathIsDirectory.isDirectory() );
    }


    // todo use?
//    @Test
//    public void testContentOfDefault() throws IOException {
//        try( DirectoryStream<Path> stream = Files.newDirectoryStream( getDefaultPath() ) ) {
//        }
//    }

    @Test
    public void testContentOfNonEmptyDir() throws IOException {
//        assumeTrue( p.hasNonEmptyDir() );

        try( DirectoryStream<Path> stream = Files.newDirectoryStream( nonEmptyDir()) ) {
            assertEquals( getKidCount(), Iterators.size( stream ) );
        }
    }

    @Test
    public void testContentOfNonEmptyDirFiltered() throws IOException {
//        assumeTrue( p.hasNonEmptyDir() );

        Path path = nonEmptyDir();

        // filter out first kid
        DirectoryStream.Filter<Path> filter = new DirectoryStream.Filter<Path>() {

            boolean first = true;

            @Override
            public boolean accept( Path entry ) throws IOException {
                if( first ) {
                    first = false;
                    return false;
                }
                return true;
            }
        };

        try( DirectoryStream<Path> stream = Files.newDirectoryStream( path, filter ) ) {
            assertEquals( getKidCount() - 1, Iterators.size( stream ) );
        }
    }

    @Test
    public void testNewDirIsInParentsDirStream() throws IOException {
//        assumeTrue( p.hasNonEmptyDir() );

        Path dir = getPathPA();
        Files.createDirectory( dir );

        try( DirectoryStream<Path> kids = Files.newDirectoryStream( dir.getParent() ) ) {
            assertThat( kids, containsInAnyOrder( dir ) );
        }
    }


    @Test
    public void testNewDirectoryExists() throws IOException {
        Path newDir = getPathPA();
        Files.createDirectory( newDir );
        assertThat( newDir, exists() );
    }

    @Test( expected = FileAlreadyExistsException.class )
    public void testCreateDirectoryTwiceThrows() throws IOException {
        Path newDir = getPathPA();
        Files.createDirectory( newDir );
        Files.createDirectory( newDir );
    }

    @Test( expected = NoSuchFileException.class )
    public void testCreateDirectoryFail() throws IOException {
        Files.createDirectory( getPathPAB() );
    }

    // bug in java Path
    @Test( expected = FileAlreadyExistsException.class )
    public void testCreateDirectoryRoot() throws IOException {
        assumeThat( message(), possible(), is( true ) );

        Files.createDirectory( getRoot() );
    }

    @Test( expected = FileSystemException.class )
    public void testCreateDirectoryRootThrowsWrongException() throws IOException {
        assumeThat( message(), possible(), is( false ) );

        Files.createDirectory( getRoot() );
    }

    @Test
    public void testRootisADir() throws IOException {
        assertThat( getRoot(), PathIsDirectory.isDirectory() );
    }

    @Test
    public void testDefaultExists() throws Exception {
        assertThat( getDefaultPath(), exists() );
    }


//    // TODO
//    @Test
//    public void testIsDirectoryNotExists() throws IOException {
//
//        assertThat( p.getNonExistingPath().getRoot().relativize( p.getNonExistingPath() ), isNotDirectory() );
//
//        assertThat( p.getNonExistingPath().toAbsolutePath(), isNotDirectory() );
//    }


    // todo
//    @Test( expected = FileAlreadyExistsException.class )
//    public void testCreateDirWithSamePathAsExistingFileFails() throws Exception {
//
//        Path file = p.getTmpDir( "" ).resolve( "foo" );
//        Files.write( file, "hallo".getBytes( "UTF-8" ) );
//
//        Files.createDirectory( file );
//    }

    @Test
    public void testCreateDirSetsModifiedTimeOfParent() throws IOException, InterruptedException {
        assumeThat( message(), possible(), is( true ) );

        Path tmp = emptyDir();

        FileTime created = Files.getLastModifiedTime( tmp );

        // time resolution is not terrible precise
        Thread.sleep( 2000 );

        Files.createDirectory( tmp.resolve( nameStr[0] ) );
        FileTime modified = Files.getLastModifiedTime( tmp );

        assertTrue( "created after modified", created.compareTo( modified ) < 0 );
    }

    @Test
    public void testCreateDirSetsLastAccessTimeOfParent() throws IOException, InterruptedException {
        assumeThat( message(), possible(), is( true ) );

        Path dir = getPathPAd();
        FileTime before = Files.readAttributes( dir, BasicFileAttributes.class ).lastAccessTime();
        Thread.sleep( 2000 );

        Files.createDirectory( getPathPAB() );
        assertThat( Files.readAttributes( dir, BasicFileAttributes.class ).lastAccessTime(), greaterThan( before ));
    }


    @Test
    public void testCreateDirSetsCreationTime() throws IOException, InterruptedException {
        assumeThat( message(), possible(), is( true ) );

        Path dir = getPathPA();
        FileTime before = Files.getLastModifiedTime( dir.getParent() );
        Thread.sleep( 1000 );

        Files.createDirectory( dir );

        BasicFileAttributes atti = Files.readAttributes( dir, BasicFileAttributes.class );

        assertThat( atti.creationTime(), greaterThan( before ) );
    }

    // todo
//    @Test( expected = ProviderMismatchException.class )
//    public void testCallNewDirectoryStreamWithForeignPath() throws Exception {
//
//        try( DirectoryStream<Path> ch = p.FS.provider().newDirectoryStream( p.getOtherProviderFS().getPath( "" ), new DirectoryStream.Filter<Path>() {
//            @Override
//            public boolean accept( Path entry ) throws IOException {
//                return false;
//            }
//        } ) ) {
//        }
//    }

    @Test
    public void testKidsOfAbsoluteDirAreAbsolute() throws Exception {
        assumeThat( message(), possible(), is( true ) );

        try( DirectoryStream<Path> kids = Files.newDirectoryStream( nonEmptyDir() ) ) {
            for( Path kid : kids ) {
                assertThat( kid, absolute() );
            }
        }

    }

    @Test
    public void testKidsOfRelativeDirAreRelative() throws Exception {
        assumeThat( message(), possible(), is( true ) );

        Path abs = nonEmptyDir();
        Path rel = getDefaultPath().toAbsolutePath().relativize( abs );

        try( DirectoryStream<Path> kids = Files.newDirectoryStream( rel ) ) {
            for( Path kid : kids ) {
                assertThat( kid, relative() );
            }
        }
    }

    @Test
    public void testKidsOfRelDirAreLikeTheResultOfResolve() throws Exception {
        assumeThat( message(), possible(), is( true ) );

        Path abs = nonEmptyDir();
        Path rel = getDefaultPath().toAbsolutePath().relativize( abs );

        try( DirectoryStream<Path> kids = Files.newDirectoryStream( rel ) ) {
            for( Path kid : kids ) {
                assertEquals( kid, rel.resolve( kid.getFileName() ) );
            }
        }
    }

    @Test
    public void testReadDirStreamSetsLastAccessTime() throws Exception{
        Path     dir    = getPathPAd().getParent();
        FileTime before  = Files.readAttributes( dir, BasicFileAttributes.class ).lastAccessTime();
        Thread.sleep( 2000 );

        try( DirectoryStream<Path> kids = Files.newDirectoryStream( dir ) ) {
            for( Path kid : kids ) {
            }
        }

        assertThat( Files.readAttributes( dir, BasicFileAttributes.class ).lastAccessTime(), greaterThan( before ));
    }

    @Test
    public void testReadEmptyDirStreamSetsLastAccessTime() throws Exception{
        Path     dir    = getPathPAd();
        FileTime before  = Files.readAttributes( dir, BasicFileAttributes.class ).lastAccessTime();
        Thread.sleep( 2000 );

        try( DirectoryStream<Path> kids = Files.newDirectoryStream( dir ) ) {
            for( Path kid : kids ) {
            }
        }

        assertThat( Files.readAttributes( dir, BasicFileAttributes.class ).lastAccessTime(), greaterThan( before ));
    }

    @Test
    public void testReadDirStreamDoesNotSetParentsLastAccessTime() throws Exception{
        Path     dir    = getPathPAd();
        FileTime before  = Files.readAttributes( dir.getParent(), BasicFileAttributes.class ).lastAccessTime();
        Thread.sleep( 2000 );

        try( DirectoryStream<Path> kids = Files.newDirectoryStream( dir ) ) {
            for( Path kid : kids ) {
            }
        }

        assertThat( Files.readAttributes( dir.getParent(), BasicFileAttributes.class ).lastAccessTime(), is( before ));
    }

}
