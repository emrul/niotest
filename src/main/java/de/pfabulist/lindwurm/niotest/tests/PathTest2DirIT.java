package de.pfabulist.lindwurm.niotest.tests;

import de.pfabulist.lindwurm.niotest.Utils;
import de.pfabulist.lindwurm.niotest.matcher.IteratorMatcher;
import de.pfabulist.lindwurm.niotest.matcher.PathAbsolute;
import de.pfabulist.lindwurm.niotest.matcher.PathExists;
import de.pfabulist.lindwurm.niotest.matcher.PathIsDirectory;
import org.hamcrest.Matchers;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.number.OrderingComparison.greaterThan;
import static org.junit.Assert.*;
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
public abstract class PathTest2DirIT extends PathTest1NoContentIT {

    @Test
    public void testAAA2HasAPlayfield() throws Exception {
        assertThat( "assign a playfield", getPlay(), notNullValue() );
    }

    @Test
    public void testDefaultIsDir() throws Exception {
        assertThat( getDefaultPath(), PathIsDirectory.isDirectory() );
    }


    @Test
    public void testContentOfNonEmptyDir() throws IOException {

        try( DirectoryStream<Path> stream = Files.newDirectoryStream( nonEmptyDir()) ) {
            assertThat( Utils.getSize(stream), is(getKidCount()));
        }
    }



    @Test( expected = IllegalStateException.class )
    public void testIteratorCanOnlyBeCalledOnceOnDirStream() throws IOException {

        try( DirectoryStream<Path> stream = Files.newDirectoryStream( getPathPAd()) ) {
            stream.iterator();
            stream.iterator();
        }
    }

    @Test( expected = UnsupportedOperationException.class )
    public void testDirStreamIteratorHasNoRemove() throws IOException {

        try( DirectoryStream<Path> stream = Files.newDirectoryStream( getPathPAd()) ) {
            stream.iterator().remove();
        }
    }


    @Test
    public void testContentOfNonEmptyDirFiltered() throws IOException {

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
            assertThat(Utils.getSize(stream), is(getKidCount() - 1));

//            assertEquals( getKidCount() - 1, Iterators.size( stream ) );
        }
    }

    @Test
    public void testNewDirIsInParentsDirStream() throws IOException {

        Path dir = getPathPA();
        Files.createDirectory( dir );

        try( DirectoryStream<Path> kids = Files.newDirectoryStream( dir.getParent() ) ) {
            assertThat( dir, IteratorMatcher.isIn(kids) );
        }
    }

    @Test
    public void testNewDirectoryExists() throws IOException {
        Path newDir = getPathPA();
        Files.createDirectory( newDir );
        assertThat( newDir, PathExists.exists() );
    }



    @Test( expected = FileAlreadyExistsException.class )
    public void testCreateDirectoryTwiceThrows() throws IOException {
        Path newDir = getPathPA();
        Files.createDirectory( newDir );
        Files.createDirectory( newDir );
    }

    @Test( expected = NoSuchFileException.class )
    public void testCreateDirectoryWithoutExistingParantFails() throws IOException {
        Files.createDirectory( getPathPAB() );
    }

    @Test( expected = FileSystemException.class )
    public void testCreateDirectoryWithInNoDirectoryFails() throws IOException {
        Files.createDirectory( getPathPAf().resolve( nameStr[2]) );
    }

    @Test( expected = FileAlreadyExistsException.class )
    public void testCreateDirectoryRoot() throws IOException {
        Files.createDirectory( getRoot() );
    }

    @Test
    public void testRootisADir() throws IOException {
        assertThat( getRoot(), PathIsDirectory.isDirectory() );
    }

    @Test
    public void testDefaultExists() throws Exception {
        assertThat( getDefaultPath(), PathExists.exists() );
    }


    @Test
    public void testNonExistingAbsolutePathIsNotADirectory() throws IOException {
        assertThat( getPathPA(), Matchers.not(PathIsDirectory.isDirectory()) );
    }

    @Test
    public void testNonExistingRelativePathIsNotADirectory() throws IOException {
        assertThat( getPathA(), Matchers.not(PathIsDirectory.isDirectory()) );
    }


    @Test( expected = FileAlreadyExistsException.class )
    public void testCreateDirWithSamePathAsExistingFileFails() throws Exception {
        Path file = getPathPAf();
        Files.createDirectory( file );
    }

    @Test
    public void testCreateDirSetsModifiedTimeOfParent() throws IOException, InterruptedException {
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
        assumeThat( capabilities.supportsLastAccessTime(), is(true));

        Path dir = getPathPAd();
        FileTime before = Files.readAttributes( dir, BasicFileAttributes.class ).lastAccessTime();
        Thread.sleep( 2000 );

        Files.createDirectory( getPathPAB() );
        assertThat( Files.readAttributes( dir, BasicFileAttributes.class ).lastAccessTime(), greaterThan( before ));
    }

//    @Test
//    public void bugCreateDirDoesNotSetLastAccessTimeOfParent() throws IOException, InterruptedException {
//        Path dir = getPathPAd();
//        FileTime before = Files.readAttributes( dir, BasicFileAttributes.class ).lastAccessTime();
//        Thread.sleep( 2000 );
//
//        Files.createDirectory( getPathPAB() );
//        assertThat( Files.readAttributes( dir, BasicFileAttributes.class ).lastAccessTime(), is( before ));
//    }

    @Test
    public void testCreateDirSetsCreationTime() throws IOException, InterruptedException {
        Path dir = getPathPA();
        FileTime before = Files.getLastModifiedTime( dir.getParent() );
        Thread.sleep( 1000 );

        Files.createDirectory( dir );

        BasicFileAttributes atti = Files.readAttributes( dir, BasicFileAttributes.class );

        assertThat( atti.creationTime(), greaterThan( before ) );
    }

    @Test
    public void testKidsOfAbsoluteDirAreAbsolute() throws Exception {
        try( DirectoryStream<Path> kids = Files.newDirectoryStream( nonEmptyDir() ) ) {
            for( Path kid : kids ) {
                assertThat( kid, PathAbsolute.absolute() );
            }
        }
    }

    @Test
    public void testKidsOfRelativeDirAreRelative() throws Exception {
        Path abs = nonEmptyDir();
        Path rel = getDefaultPath().toAbsolutePath().relativize( abs );

        try( DirectoryStream<Path> kids = Files.newDirectoryStream( rel ) ) {
            for( Path kid : kids ) {
                assertThat( kid, PathAbsolute.relative() );
            }
        }
    }

    @Test
    public void testFilterOfRel() throws Exception { // TODO
        Path abs = nonEmptyDir();
        Path rel = getDefaultPath().toAbsolutePath().relativize( abs );

        try( DirectoryStream<Path> kids = Files.newDirectoryStream( rel ) ) {
            for( Path kid : kids ) {
                assertThat( kid, PathAbsolute.relative() );
            }
        }
    }

    @Test
    public void testKidsOfRelDirAreLikeTheResultOfResolve() throws Exception {
        Path abs = nonEmptyDir();
        Path rel = getDefaultPath().toAbsolutePath().relativize( abs );

        try( DirectoryStream<Path> kids = Files.newDirectoryStream( rel ) ) {
            for( Path kid : kids ) {
                assertThat( kid, is(rel.resolve( kid.getFileName())));
            }
        }
    }

    @Test
    public void testReadDirStreamSetsLastAccessTime() throws Exception{
        assumeThat( capabilities.supportsLastAccessTime(), is(true));

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
        assumeThat( capabilities.supportsLastAccessTime(), is(true));

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
        assumeThat( capabilities.supportsLastAccessTime(), is(true));

        Path     dir    = getPathPAd();
        FileTime before  = Files.readAttributes( dir.getParent(), BasicFileAttributes.class ).lastAccessTime();
        Thread.sleep( 2000 );

        try( DirectoryStream<Path> kids = Files.newDirectoryStream( dir ) ) {
            for( Path kid : kids ) {
            }
        }

        assertThat( Files.readAttributes( dir.getParent(), BasicFileAttributes.class ).lastAccessTime(), is( before ));
    }

    @Test
    public void testGetIteratorOfClosedDirStream() throws Exception{
        Path     file    = getPathPABf();
        getPathPACf(); // 2 kids


        try( DirectoryStream<Path> kids = Files.newDirectoryStream( file.getParent() ) ) {
            kids.close();
            int count = 0;
            for ( Path kid : kids ) {
                count++;
            }

            assertThat( count, lessThan(2) );
        }
    }

    @Test
    public void testCloseDirStreamInTheMiddleOfIteration() throws Exception{
        Path     file    = getPathPABf();
        getPathPACf(); // 2nd kid
        getPathPADf(); // 3rd kid


        try( DirectoryStream<Path> kids = Files.newDirectoryStream( file.getParent() ) ) {
            int count = 0;
            for ( Path kid : kids ) {
                count++;

                if ( count == 1 ) {
                    kids.close();
                }

            }

            assertThat( count, lessThan(3) );
        }
    }

    @Test
    public void testReadBytesFromDirectoryThrows() throws IOException {
        try {
            Files.readAllBytes(getPathPAd());
        } catch( Exception e ) {
            // good
            return;
        }

        fail( "reading directly from a dir should fail" );
    }

    @Test( expected = NoSuchFileException.class )
    public void testNewDirectoryStreamFromNonExistingDirThrows() throws IOException {
        try( DirectoryStream<Path> kids = Files.newDirectoryStream( getPathA() ) ) {
        }

    }



}
