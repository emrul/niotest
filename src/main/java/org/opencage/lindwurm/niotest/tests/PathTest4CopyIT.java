package org.opencage.lindwurm.niotest.tests;

import org.hamcrest.core.Is;
import org.junit.Test;
import org.opencage.kleinod.collection.Forall;
import org.opencage.kleinod.collection.Iterators;


import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.number.OrderingComparison.greaterThan;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.junit.Assume.assumeThat;
import static org.opencage.lindwurm.niotest.matcher.IteratorMatcher.isIn;
import static org.opencage.lindwurm.niotest.matcher.PathExists.exists;
import static org.opencage.lindwurm.niotest.matcher.PathIsDirectory.isDirectory;

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
public abstract class PathTest4CopyIT extends PathTest3FileIT {
    //private Path tmp;
    private Path src;
    private Path tgt;

    private void copyMoveSetup() throws IOException {
        src = getPathPA();
        tgt = getPathPB();
        Files.write( src, CONTENT, standardOpen );
    }

    private void checkContentOTarget() throws IOException {
        assertArrayEquals( CONTENT, Files.readAllBytes( tgt ) );
    }

    @Test
    public void testCopyDuplicatesTheContent() throws IOException {
        copyMoveSetup();
        Files.copy( src, tgt );
        checkContentOTarget();
    }

    @Test( expected = FileAlreadyExistsException.class )
    public void testCopyAlreadyThereWithoutOptionThrows() throws IOException {
        copyMoveSetup();
        Files.write( tgt, CONTENT, standardOpen );

        Files.copy( src, tgt );

        fail("should not get here");
    }

    @Test
    public void testCopyAlreadyThereOverwrite() throws IOException {
        copyMoveSetup();
        Files.write( tgt, CONTENT, standardOpen );
        Files.copy( src, tgt, StandardCopyOption.REPLACE_EXISTING  );
        checkContentOTarget();
    }

    @Test
    public void testCopyViaProvider() throws IOException {
        copyMoveSetup();
        src.getFileSystem().provider().copy( src, tgt );
        checkContentOTarget();
    }

    @Test
    public void testCopyResultHasCreationTime() throws Exception {
        copyMoveSetup();
        FileTime before = Files.getLastModifiedTime( src );
        Thread.sleep( 2000 );
        Files.copy( src, tgt );
        FileTime created = Files.readAttributes( tgt, BasicFileAttributes.class ).creationTime();
        assertThat( created, greaterThan( before ) );
    }
//
    // todo more attis, but only lastModifiedTTime is required
    @Test
    public void testCopyAttributesCheckModifiedTime() throws Exception {
        copyMoveSetup();
        BasicFileAttributes srcAttis = Files.readAttributes( src, BasicFileAttributes.class );
        Thread.sleep( 2000 );

        Files.copy( src, tgt, StandardCopyOption.COPY_ATTRIBUTES );

        BasicFileAttributes tgtAttis = Files.readAttributes( tgt, BasicFileAttributes.class );
        assertThat( tgtAttis.lastModifiedTime(), is( srcAttis.lastModifiedTime()));
    }

    @Test
    public void testCopyDoesNotModifyOriginal() throws Exception {
        copyMoveSetup();
        FileTime beforeCopy = Files.getLastModifiedTime( src );
        Files.copy( src, tgt );
        assertThat( src, exists() );
        assertEquals( beforeCopy, Files.getLastModifiedTime( src ) );

    }

    @Test
    public void testMoveCreatesNewFileDeletesOriginal() throws IOException {
        copyMoveSetup();
        Files.move( src, tgt );
        checkContentOTarget();
        assertThat( src, not( exists()) );
    }

    @Test( expected = FileAlreadyExistsException.class )
    public void testMoveAlreadyThereThrows() throws IOException {
        copyMoveSetup();
        Files.write( tgt, CONTENT_OTHER, standardOpen );
        Files.move( src, tgt );
    }

    @Test
    public void testFailedMoveLeavesOriginal() throws Exception {
        copyMoveSetup();
        Files.write( tgt, CONTENT, standardOpen );

        try { Files.move( src, tgt );
        } catch( FileAlreadyExistsException exp ) {
        }

        assertThat( src, exists() );
    }

    @Test
    public void testMoveAlreadyThereOverwrite() throws Exception {
        copyMoveSetup();
        Files.write( tgt, CONTENT_OTHER, standardOpen );
        Files.move( src, tgt, StandardCopyOption.REPLACE_EXISTING  );

        checkContentOTarget();
        assertThat( src, not( exists() ) );
    }

    @Test( expected = FileAlreadyExistsException.class)
    public void testMoveAlreadyThereDirectory() throws Exception {
        Path src = getPathPABf();
        Path tgt = getPathPBd();
        Files.move( src, tgt );
    }

    @Test
    public void testMoveAlreadyThereDirectoryOverwrite() throws Exception {
        Path src = getPathPABf();
        Path tgt = getPathPBd();
        Files.move( src, tgt, StandardCopyOption.REPLACE_EXISTING  );

        assertThat( Files.readAllBytes(tgt), is(CONTENT ));
        assertThat( src, not( exists() ) );
    }

    @Test( expected = DirectoryNotEmptyException.class )
    public void testMoveAlreadyThereNonEmptyDirectoryOverwrite() throws Exception {
        Path src = getPathPABf();
        Path tgt = getPathPBCf().getParent();

        Files.move( src, tgt, StandardCopyOption.REPLACE_EXISTING  );
    }

    @Test
    public void testMoveViaProvider() throws IOException {
        copyMoveSetup();
        src.getFileSystem().provider().move( src, tgt );
        checkContentOTarget();
        assertThat( src, not( exists() ) );
    }

    @Test
    public void testMoveDir() throws IOException {
        Path src = getPathPAd();
        Files.move(src, getPathPB());
        assertThat( getPathPB(), isDirectory());
    }

    @Test
    public void testMoveNonEmptyDir() throws IOException {
        getPathPABf();
        Path src = getPathPA();
        Files.move( src, getPathPB());
        assertThat( getPathPBB(), exists());
    }

    @Test( expected = IOException.class )
    public void testMoveIntoItself() throws IOException {
        Path src = getPathPAd();
        Files.move(src, getPathPAB());
    }


    @Test( expected = FileSystemException.class )
    public void testMoveRoot() throws IOException {
        Files.move( getRoot(), getPathPB());
    }

//    @Test( expected = ClassCastException.class )
//    public void bugMoveRootThrowsClassCastException() throws IOException {
//        Files.move( getRoot(), getPathPB());
//    }

//    @Test
//    public void testMoveSetsLastModifiedTime() throws IOException, InterruptedException {
//        Path src = getPathPABf();
//        Path tgt = getPathPB();
//        FileTime modi = Files.getLastModifiedTime(src);
//        Thread.sleep(2000);
//
//        Files.move(src,tgt);
//
//        assertThat( Files.getLastModifiedTime(tgt), greaterThan(modi));
//    }


    @Test
    public void testCopyDirCreatesADirWithTheTargetName() throws Exception {

        Path kid = getPathPAB();
        Files.createDirectories( kid );
        Path tgt = getPathPC();

        Files.copy( kid.getParent(), tgt );
        assertThat( tgt, exists() );
    }

    @Test
    public void testCopyNonEmptyDirDoesNotCopyKids() throws Exception {

        Path src = nonEmptyDir();
        Path tgt = getPathPA();
        Files.copy( src, tgt );

        try ( DirectoryStream<Path> kids = Files.newDirectoryStream( tgt )) {
            assertEquals( 0, Iterators.size( kids ) );
        }
    }


    @Test
    public void testCopyDirReplaceExistingOverwritesFile() throws Exception {
        // that's a surprise, todo bug ?

        Path tgt = getPathPA();
        Files.write( tgt, CONTENT, standardOpen );
        Path src = getPathPB();
        Files.createDirectories( src );

        Files.copy( src, tgt, StandardCopyOption.REPLACE_EXISTING );

        assertThat( tgt, isDirectory() );
    }

    @Test( expected = DirectoryNotEmptyException.class )
    public void testCopyFileReplaceExistingDoesNotOverwriteExistingNonEmptyDir() throws Exception {

        copyMoveSetup();
        Files.createDirectories( tgt );
        Files.write( tgt.resolve( getPathA() ), CONTENT, standardOpen );

        Files.copy( src, tgt, StandardCopyOption.REPLACE_EXISTING );
    }

    @Test
    public void testCopyFileReplaceExistingOverwritesExistingDir() throws Exception {

        copyMoveSetup();
        Files.createDirectories( tgt );

        Files.copy( src, tgt, StandardCopyOption.REPLACE_EXISTING );
    }

    // todo: is that really supposed to work, loop danger
//    @Test( expected = FileSystemException.class )
//    public void testCopyIntoItself() throws IOException {
//        Path src = getPathPABf();
//        Files.copy(src.getParent(), getPathPAC());
//    }

    @Test
    public void testDeleteDeletes() throws Exception{
        final Path file = getPathPAf();
        Files.delete( file );
        assertThat( file, not( exists()) );
    }

    @Test
    public void testDeleteDirRemovesItFromParentsKids() throws IOException, InterruptedException {
        final Path file = getPathPAf();
        Files.delete( file );

        try ( DirectoryStream<Path> kids = Files.newDirectoryStream( file.getParent() ) ) {
            assertThat( file, not( isIn( kids ) ));
        }
    }


    @Test
    public void testDeleteFileRemovesItFromParentsKids() throws IOException, InterruptedException {
        final Path file = getPathPA();
        Files.write( file, CONTENT, standardOpen );
        Files.delete( file );
        try ( DirectoryStream<Path> kids = Files.newDirectoryStream( file.getParent() ) ) {
            assertFalse( "delete dir does not remove it from parent stream", Forall.forall( kids ).contains( file ));
        }
    }

    @Test( expected = java.nio.file.DirectoryNotEmptyException.class )
    public void testDeleteNonEmptyDirectoryThrows() throws IOException {
        Files.delete( getPathPABf().getParent() );
    }


    @Test
    public void testDeleteEmptyDir() throws IOException {
        Path dir = getPathPA();
        Files.createDirectory( dir );

        Files.delete( dir );
        assertThat( dir, not( exists() ) );
    }

    @Test
    public void testDeleteFileChangesParentsModificationTime() throws IOException, InterruptedException {
        Path       dir = getPathPA();
        final Path kid = getPathPABf();
        FileTime created = Files.getLastModifiedTime( dir );

        Thread.sleep( 2000 );

        Files.delete( kid );
        FileTime modified = Files.getLastModifiedTime( dir );

        assertThat( "delete does not modify time", modified, greaterThan( created ) );
    }

    @Test
    public void testDeleteFileDoesNotChangeParentCreationTime() throws IOException, InterruptedException {
        assumeThat( capabilities.supportsCreationTime(), is(true));

        Path parent = getPathPA();
        Path kid = getPathPABf();
        FileTime created = Files.readAttributes( parent, BasicFileAttributes.class ).creationTime();

        Thread.sleep( 2000 );

        Files.delete( kid );
        FileTime modified = Files.readAttributes( parent, BasicFileAttributes.class ).creationTime();

        assertThat( "delete does modify creation time", modified, is( created ) );
    }

    @Test
    public void testDeleteDirChangesParentsModificationTime() throws IOException, InterruptedException {
        Path       dir = getPathPA();
        final Path kid = getPathPAB();
        Files.createDirectories( kid );
        FileTime created = Files.getLastModifiedTime( dir );

        Thread.sleep( 2000 );

        Files.delete( kid );
        FileTime modified = Files.getLastModifiedTime( dir );

        assertThat( "delete does not modify time", modified, greaterThan( created ) );
    }


    @Test
    public void testDeleteDirNotChangeParentsCreationTime() throws IOException, InterruptedException {
        assumeThat( capabilities.supportsCreationTime(), is(true));

        Path parent = getPathPA();
        Path kid    = getPathPAB();
        Files.createDirectories( kid );
        FileTime created = Files.readAttributes( parent, BasicFileAttributes.class ).creationTime();

        Thread.sleep( 2000 );

        Files.delete( kid );
        FileTime modified = Files.readAttributes( parent, BasicFileAttributes.class ).creationTime();

        assertThat( "delete does modify creation time", modified, is( created ) );
    }

//        // another parent of root problem
////    @Ignore
//    @Test
//    public void testDeleteRoot() throws IOException {
//        Files.delete( getRoot() );
//    }





}
