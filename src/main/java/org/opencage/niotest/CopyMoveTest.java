package org.opencage.niotest;

import org.junit.Test;
import org.opencage.kleinod.collection.Iterators;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;

import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.opencage.niotest.matcher.FileTimeMatcher.after;
import static org.opencage.niotest.matcher.PathExists.exists;
import static org.opencage.niotest.matcher.PathIsDirectory.isDirectory;

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
public class CopyMoveTest extends FileTest {

    private Path tmp;
    private Path src;
    private Path tgt;
    private final String content = "Hallo World";

    private void copyMoveSetup( String method ) throws IOException {
        tmp = p.getTmpDir(method);
        src = tmp.resolve( "src" );
        tgt = tmp.resolve( "tgt" );
        Files.write( src, content.getBytes( "UTF-8" ) );
    }

    private void checkContentOTarget() throws IOException {
        assertEquals( content, Files.readAllLines( tgt, Charset.forName( "UTF-8" ) ).get( 0 ) );
    }

    @Test
    public void testCopyDuplicatesTheContent() throws IOException {
        copyMoveSetup( "testCopyDuplicatesTheContent" );
        Files.copy( src, tgt );
        checkContentOTarget();
    }

    @Test( expected = FileAlreadyExistsException.class )
    public void testCopyAlreadyThereWithoutOptionThrows() throws IOException {
        copyMoveSetup( "testCopyAlreadyThereWithoutOptionThrows" );
        Files.write( tgt, "duh".getBytes("UTF-8") );

        Files.copy( src, tgt );

        fail("should not get here");
    }

    @Test
    public void testCopyAlreadyThereOverwrite() throws IOException {
        copyMoveSetup( "testCopyAlreadyThereOverwrite" );
        Files.write( tgt, "duh".getBytes("UTF-8") );
        Files.copy( src, tgt, StandardCopyOption.REPLACE_EXISTING  );
        checkContentOTarget();
    }

    @Test
    public void testCopyViaProvider() throws IOException {
        copyMoveSetup( "testCopyViaProvider" );
        tmp.getFileSystem().provider().copy( src, tgt );
        checkContentOTarget();
    }

    @Test
    public void testCopyResultHasCreationTime() throws Exception {
        copyMoveSetup( "testCopyResultHasCreationTime" );
        FileTime before = Files.getLastModifiedTime( tmp );
        Files.copy( src, tgt );
        FileTime created = Files.readAttributes( tgt, BasicFileAttributes.class ).creationTime();
        assertThat( created, after( before ) );
    }

    // todo more attis
    @Test
    public void testCopyAttributesCheckModifiedTime() throws Exception {
        copyMoveSetup( "" );
        BasicFileAttributes srcAttis = Files.readAttributes( src, BasicFileAttributes.class );
        Thread.sleep( 2000 );
        Files.copy( src, tgt, StandardCopyOption.COPY_ATTRIBUTES );
        BasicFileAttributes tgtAttis = Files.readAttributes( tgt, BasicFileAttributes.class );

        assertEquals( srcAttis.lastModifiedTime(), tgtAttis.lastModifiedTime() );
    }

    @Test
    public void testCopyDoesNotModifyOriginal() throws Exception {
        copyMoveSetup( "testCopyDoesNotModifyOriginal" );
        FileTime beforeCopy = Files.getLastModifiedTime( src );
        Files.copy( src, tgt );
        assertThat( src, exists() );
        assertEquals( beforeCopy, Files.getLastModifiedTime( src ) );

    }

    @Test
    public void testMoveCreatesNewFileDeletesOriginal() throws IOException {
        copyMoveSetup( "testMoveCreatesNewFileDeletesOriginal" );
        Files.move( src, tgt );
        checkContentOTarget();
        assertThat( src, not( exists()) );
    }

    @Test( expected = FileAlreadyExistsException.class )
    public void testMoveAlreadyThereThrows() throws IOException {
        copyMoveSetup( "testMoveAlreadyThereThrows" );
        Files.write( tgt, "duh".getBytes("UTF-8") );
        Files.move( src, tgt );
    }

    @Test
    public void testFailedMOveLeavesOriginal() throws Exception {
        copyMoveSetup( "testMoveAlreadyThereThrows" );
        Files.write( tgt, "duh".getBytes("UTF-8") );

        try { Files.move( src, tgt );
        } catch( FileAlreadyExistsException exp ) {
        }

        assertThat( src, exists() );
    }

    @Test
    public void testMoveAlreadyThereOverwrite() throws IOException {
        copyMoveSetup( "testMoveAlreadyThereOverwrite" );
        Files.write( tgt, "duh".getBytes("UTF-8") );
        Files.move( src, tgt, StandardCopyOption.REPLACE_EXISTING  );

        checkContentOTarget();
        assertThat( src, not( exists() ) );
    }


    @Test
    public void testMoveViaProvider() throws IOException {
        copyMoveSetup( "testMoveViaProvider" );
        tmp.getFileSystem().provider().move( src, tgt );
        checkContentOTarget();
        assertThat( src, not( exists() ) );
    }

    @Test
    public void testCopyDirCreatesADirWithTheTargetName() throws Exception {

        Path kid = getPathTmpAB();
        Files.createDirectories( kid );

        Path tgt = kid.getParent().getParent().resolve( p.getLegalPathElement(3) ).resolve( p.getLegalPathElement(3) );
        Files.createDirectories( tgt.getParent() );

        Files.copy( kid.getParent(), tgt );

        assertThat( tgt, exists() );
    }

    @Test
    public void testCopyNonEmptyDirDoesNotCopyKids() throws Exception {

        Path kid = getPathTmpAB();
        Files.createDirectories( kid );

        Path tgt = kid.getParent().getParent().resolve( p.getLegalPathElement(3) ).resolve( p.getLegalPathElement(3) );
        Files.createDirectories( tgt.getParent() );

        Files.copy( kid.getParent(), tgt );

        try ( DirectoryStream<Path> kids = Files.newDirectoryStream( tgt )) {
            assertEquals( 0, Iterators.size( kids ) );
        }
    }


    @Test
    public void testCopyDirReplaceExistingOverwritesFile() throws Exception {
        // that's a surprise, todo bug ?

        Path kid = getPathTmpAB();
        Files.createDirectories( kid );

        Path tgt = kid.getParent().getParent().resolve( p.getLegalPathElement(3) );
        Files.write( tgt, "Hallo World".getBytes("UTF-8") );

        Files.copy( kid.getParent(), tgt, StandardCopyOption.REPLACE_EXISTING );

        assertThat( tgt, isDirectory() );
    }

    @Test( expected = DirectoryNotEmptyException.class )
    public void testCopyFileReplaceExistingDoesNotOverwriteExistingNonEmptyDir() throws Exception {

        Path kid = getPathTmpAB();
        Files.createDirectories( kid );
        Path src = kid.getParent().getParent().resolve( p.getLegalPathElement(3) );
        Files.write( src, "Hallo World".getBytes( "UTF-8" ) );

        Files.copy( src, kid.getParent(), StandardCopyOption.REPLACE_EXISTING );

    }

    @Test
    public void testCopyFileReplaceExistingOverwritesExistingDir() throws Exception {

        Path kid = getPathTmpA();
        Files.createDirectories( kid );
        Path src = kid.getParent().resolve( p.getLegalPathElement(3) );
        Files.write( src, "Hallo World".getBytes( "UTF-8" ) );

        Files.copy( src, kid, StandardCopyOption.REPLACE_EXISTING );

        assertThat( src, exists() );
        assertThat( src, not( isDirectory()) );

    }
}
