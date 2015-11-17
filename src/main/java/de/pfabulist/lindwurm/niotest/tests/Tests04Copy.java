package de.pfabulist.lindwurm.niotest.tests;

import de.pfabulist.kleinod.collection.Sets;
import de.pfabulist.lindwurm.niotest.tests.topics.CreationTime;
import de.pfabulist.lindwurm.niotest.tests.topics.LastModifiedTime;
import de.pfabulist.unchecked.Filess;
import de.pfabulist.lindwurm.niotest.tests.topics.Attributes;
import de.pfabulist.lindwurm.niotest.tests.topics.Copy;
import de.pfabulist.lindwurm.niotest.tests.topics.Delete;
import de.pfabulist.lindwurm.niotest.tests.topics.Move;
import de.pfabulist.lindwurm.niotest.tests.topics.MoveWhile;
import de.pfabulist.lindwurm.niotest.tests.topics.SlowTest;
import de.pfabulist.lindwurm.niotest.tests.topics.Writable;
import org.hamcrest.Matchers;
import org.hamcrest.collection.IsIterableWithSize;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileSystemException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;

import static de.pfabulist.lindwurm.niotest.matcher.IteratorMatcher.isIn;
import static de.pfabulist.lindwurm.niotest.matcher.PathExists.exists;
import static de.pfabulist.lindwurm.niotest.matcher.PathIsDirectory.isDirectory;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.CREATE_NEW;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.number.OrderingComparison.greaterThan;
import static org.junit.Assert.assertEquals;

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
public abstract class Tests04Copy extends Tests03File {

    @Test
    @Category( { Writable.class, Copy.class } )
    public void testCopyDuplicatesTheContent() throws IOException {
        Files.copy( srcFile(), tgt() );
        assertThat( Files.readAllBytes( tgt() ), is( CONTENT ) );
        assertThat( Files.readAllBytes( srcFile() ), is( CONTENT ) );
    }

    @Test( expected = FileAlreadyExistsException.class )
    @Category( { Writable.class, Copy.class } )
    public void testCopyAlreadyThereWithoutOptionThrows() throws IOException {
        Files.write( tgt(), CONTENT, standardOpen );
        Files.copy( srcFile(), tgt() );
    }

    @Test
    @Category( { Writable.class, Copy.class } )
    public void testCopyAlreadyThereOverwrite() throws IOException {
        Files.write( tgt(), CONTENT_OTHER );
        Files.copy( srcFile(), tgt(), REPLACE_EXISTING );
        assertThat( Files.readAllBytes( tgt() ), is( CONTENT ) );
    }

    @Test
    @Category( { Writable.class, Copy.class } )
    public void testCopyViaProvider() throws IOException {
        srcFile().getFileSystem().provider().copy( srcFile(), tgt() );
        assertThat( Files.readAllBytes( tgt() ), is( CONTENT ) );
    }

    @Test
    @Category( { Writable.class, Copy.class, SlowTest.class, Attributes.class, CreationTime.class } )
    public void testCopyResultHasCreationTime() throws Exception {
        FileTime before = Files.getLastModifiedTime( srcFile() );
        waitForAttribute();
        Files.copy( srcFile(), tgt() );
        FileTime created = Files.readAttributes( tgt(), BasicFileAttributes.class ).creationTime();
        assertThat( created, greaterThan( before ) );
    }

    // todo more attis, but only lastModifiedTTime is required
    @Test
    @Category( { Writable.class, Copy.class, SlowTest.class, Attributes.class } )
    public void testCopyAttributesCheckModifiedTime() throws Exception {
        BasicFileAttributes srcAttis = Files.readAttributes( srcFile(), BasicFileAttributes.class );
        waitForAttribute();

        Files.copy( srcFile(), tgt(), StandardCopyOption.COPY_ATTRIBUTES );

        BasicFileAttributes tgtAttis = Files.readAttributes( tgt(), BasicFileAttributes.class );
        assertThat( tgtAttis.lastModifiedTime(), is( srcAttis.lastModifiedTime() ) );
    }

    @Test
    @Category( { Writable.class, Copy.class, SlowTest.class, Attributes.class } )
    public void testCopyDoesNotModifyOriginal() throws Exception {
        FileTime beforeCopy = Files.getLastModifiedTime( srcFile() );
        waitForAttribute();
        Files.copy( srcFile(), tgt() );
        assertThat( srcFile(), exists() );
        assertEquals( beforeCopy, Files.getLastModifiedTime( srcFile() ) );
    }

    @Test
    @Category( { Writable.class, Copy.class } )
    public void testModifyOriginalAfterCopyDoesNotChangeTarget() throws Exception {
        Files.copy( srcFile(), tgt() );
        Files.write( srcFile(), CONTENT_OTHER );

        assertThat( Files.readAllBytes( tgt() ), is( CONTENT ) );
    }

    @Test
    @Category( { Writable.class, Move.class } )
    public void testMoveCreatesNewFileDeletesOriginal() throws IOException {
        Files.move( srcFile(), tgt() );
        assertThat( src(), not( exists() ) );
    }

    @Test( expected = FileAlreadyExistsException.class )
    @Category( { Writable.class, Move.class } )
    public void testMoveAlreadyThereThrows() throws IOException {
        Files.write( tgt(), CONTENT_OTHER, standardOpen );
        Files.move( srcFile(), tgt() );
    }

    @Test
    @Category( { Writable.class, Move.class } )
    public void testFailedMoveLeavesOriginal() throws Exception {
        Files.write( tgt(), CONTENT, standardOpen );

        try {
            Files.move( srcFile(), tgt() );
        } catch( FileAlreadyExistsException exp ) { // NOSONAR
        }

        assertThat( src(), exists() );
    }

    @Test
    @Category( { Writable.class, Move.class } )
    public void testMoveAlreadyThereOverwrite() throws Exception {
        Files.write( tgt(), CONTENT_OTHER, standardOpen );
        Files.move( srcFile(), tgt(), StandardCopyOption.REPLACE_EXISTING );

        assertThat( Files.readAllBytes( tgt() ), is( CONTENT ) );
        assertThat( src(), not( exists() ) );
    }

    @Test( expected = FileAlreadyExistsException.class )
    @Category( { Writable.class, Move.class } )
    public void testMoveAlreadyThereDirectory() throws Exception {
        Files.createDirectory( tgt() );
        Files.move( srcFile(), tgt() );
    }

    @Test
    @Category( { Writable.class, Move.class } )
    public void testMoveAlreadyThereDirectoryOverwrite() throws Exception {
        Files.createDirectory( tgt() );
        Files.move( srcFile(), tgt(), StandardCopyOption.REPLACE_EXISTING );

        assertThat( Files.readAllBytes( tgt() ), is( CONTENT ) );
        assertThat( src(), not( exists() ) );
    }

    @Test( expected = DirectoryNotEmptyException.class )
    @Category( { Writable.class, Move.class } )
    public void testMoveAlreadyThereNonEmptyDirectoryOverwrite() throws Exception {
        Files.createDirectory( tgt() );
        Files.write( tgt().resolve( nameB() ), CONTENT );

        Files.move( srcFile(), tgt(), StandardCopyOption.REPLACE_EXISTING );
    }

    @Test
    @Category( { Writable.class, Move.class } )
    public void testMoveViaProvider() throws IOException {
        srcFile().getFileSystem().provider().move( src(), tgt() );
        assertThat( src(), not( exists() ) );
    }

    @Test
    @Category( { Writable.class, Move.class } )
    public void testMoveEmptyDir() throws IOException {
        Files.move( srcDir(), tgt() );
        assertThat( tgt(), isDirectory() );
    }

    @Test
    @Category( { Writable.class, Move.class } )
    public void testMoveNonEmptyDir() throws IOException {
        Files.write( srcDir().resolve( nameB() ), CONTENT );
        Files.write( src().resolve( nameC() ), CONTENT );                    // src/C
        Files.createDirectory( src().resolve( nameA() ) );                   // src/B
        Files.write( src().resolve( nameA() ).resolve( nameA() ), CONTENT );   // src/A/A

        Files.move( src(), tgt() );
        assertThat( tgt().resolve( nameA() ).resolve( nameA() ), exists() );
    }

    @Test( expected = IOException.class )
    @Category( { Writable.class, Move.class } )
    public void testMoveIntoItself() throws IOException {
        Files.move( srcDir(), srcDir().resolve( "tgt" ) );
    }

    @Test( expected = FileSystemException.class )
    @Category( { Writable.class, Move.class } )
    public void testMoveRoot() throws IOException {
        Files.move( defaultRoot(), tgt() );
    }

    @Test
    @Category( { SlowTest.class, Writable.class, Move.class, Attributes.class } )
    public void testMoveKeepsLastModifiedTime() throws IOException, InterruptedException {
        FileTime modi = Files.getLastModifiedTime( srcFile() );
        waitForAttribute();
        Files.move( src(), tgt() );
        assertThat( Files.getLastModifiedTime( tgt() ), is( modi ) );
    }

    @Test
    @Category( { SlowTest.class, Writable.class, Move.class, Attributes.class, LastModifiedTime.class } )
    public void testMoveChangesModifiedTimeOfParent() throws IOException, InterruptedException {
        FileTime modi = Files.getLastModifiedTime( srcFile().getParent() );
        waitForAttribute();
        Files.move( src(), tgt() );
        assertThat( Files.getLastModifiedTime( src().getParent() ), greaterThan( modi ) );
    }

    @Test
    @Category( { SlowTest.class, Writable.class, Move.class, Attributes.class, LastModifiedTime.class } )
    public void testMoveChangesModifiedTimeOfTargetsParent() throws IOException, InterruptedException {
        FileTime modi = Files.getLastModifiedTime( tgt().getParent() );
        waitForAttribute();

        Files.move( srcFile(), tgt() );
        assertThat( Files.getLastModifiedTime( tgt().getParent() ), greaterThan( modi ) );
    }

    @Test
    @Category( { Writable.class, Copy.class } )
    public void testCopyDirCreatesADirWithTheTargetName() throws Exception {
        Files.copy( srcDir(), tgt() );
        assertThat( tgt(), exists() );
    }

    @Test
    @Category( { Writable.class, Copy.class } )
    public void testCopyNonEmptyDirDoesNotCopyKids() throws Exception {
        Files.write( srcDir().resolve( nameB() ), CONTENT );

        Files.copy( src(), tgt() );

        try( DirectoryStream<Path> kids = Files.newDirectoryStream( tgt() ) ) {
            assertThat( kids, IsIterableWithSize.<Path> iterableWithSize( 0 ) );
        }
    }

    @Test
    @Category( { Writable.class, Copy.class } )
    public void testCopyDirReplaceExistingOverwritesFile() throws Exception {
        // that's a surprise, todo bugs ?

        Files.write( tgt(), CONTENT, standardOpen );
        Files.copy( srcDir(), tgt(), StandardCopyOption.REPLACE_EXISTING );
        assertThat( tgt(), isDirectory() );
    }

    @Test( expected = DirectoryNotEmptyException.class )
    @Category( { Writable.class, Copy.class } )
    public void testCopyFileReplaceExistingDoesNotOverwriteExistingNonEmptyDir() throws Exception {
        Path targetKid = tgt().resolve( "targetKid" );
        Files.createDirectories( tgt() );
        Files.write( targetKid, CONTENT );

        Files.copy( srcFile(), tgt(), REPLACE_EXISTING );
    }

    @Test
    @Category( { Writable.class, Copy.class } )
    public void testCopyFileReplaceExistingOverwritesExistingDir() throws Exception {
        Files.createDirectories( tgt() );
        Files.copy( srcFile(), tgt(), StandardCopyOption.REPLACE_EXISTING );
        assertThat( Files.readAllBytes( tgt() ), is( CONTENT ) );
    }

    @Test
    @Category( { Writable.class, Copy.class } )
    public void testCopyIntoItself() throws IOException {
        Path tgt = srcDir().resolve( "tgt" );
        Files.copy( tgt.getParent(), tgt );
        assertThat( tgt, exists() );
    }

    @Test
    @Category( { Writable.class, Delete.class } )
    public void testDeleteDeletes() throws Exception {
        Files.delete( fileTA() );
        assertThat( absTA(), not( exists() ) );
    }

    @Test
    @Category( { Writable.class, Delete.class } )
    public void testDeleteDirRemovesItFromParentsKids() throws IOException, InterruptedException {
        Path dir = dirTB();
        Files.delete( dir );
        try( DirectoryStream<Path> kids = Files.newDirectoryStream( dir.getParent() ) ) {
            assertThat( dir, not( isIn( kids ) ) );
        }
    }

    @Test
    @Category( { Writable.class, Delete.class } )
    public void testDeleteFileRemovesItFromParentsKids() throws IOException, InterruptedException {
        final Path file = fileTAB();
//        Files.write( file, CONTENT, standardOpen );
        Files.delete( file );
        try( DirectoryStream<Path> kids = Files.newDirectoryStream( file.getParent() ) ) {
            assertThat( file, not( isIn( kids ) ) );
        }
    }

    @Test( expected = DirectoryNotEmptyException.class )
    @Category( { Writable.class, Delete.class } )
    public void testDeleteNonEmptyDirectoryThrows() throws IOException {
        Files.delete( fileTAB().getParent() );
    }

    @Test
    @Category( { Writable.class, Delete.class } )
    public void testDeleteEmptyDir() throws IOException {
        Files.delete( dirTA() );
        assertThat( absTA(), not( exists() ) );
    }

    @Test
    @Category( { Writable.class, Delete.class } )
    public void testDeleteEmptiedDir() throws IOException {
        Path file = fileTAB();
        Files.delete( file );
        Files.delete( file.getParent() );
        assertThat( file.getParent(), not( exists() ) );
    }

    @Test
    @Category( { SlowTest.class, Writable.class, Delete.class, Attributes.class, LastModifiedTime.class } )
    public void testDeleteFileChangesParentsModificationTime() throws IOException, InterruptedException {
        FileTime modified = Files.getLastModifiedTime( fileTAB().getParent() );
        waitForAttribute();
        Files.delete( fileTAB() );
        assertThat( Files.getLastModifiedTime( absTA() ), greaterThan( modified ) );
    }

    @Test
    @Category( { SlowTest.class, Writable.class, Delete.class, Attributes.class, CreationTime.class } )
    public void testDeleteFileDoesNotChangeParentCreationTime() throws IOException, InterruptedException {
        Path file = fileTAB();
        Path parent = file.getParent();
        FileTime created = Files.readAttributes( parent, BasicFileAttributes.class ).creationTime();
        waitForAttribute();
        Files.delete( file );
        assertThat( Files.readAttributes( parent, BasicFileAttributes.class ).creationTime(), is( created ) );
    }

    @Test
    @Category( { SlowTest.class, Writable.class, Delete.class, Attributes.class, LastModifiedTime.class } )
    public void testDeleteDirChangesParentsModificationTime() throws IOException, InterruptedException {
        FileTime modified = Files.getLastModifiedTime( dirTAB().getParent() );
        waitForAttribute();
        Files.delete( absTAB() );
        assertThat( Files.getLastModifiedTime( absTA() ), greaterThan( modified ) );
    }

    @Test( expected = NoSuchFileException.class )
    @Category( { Writable.class, Delete.class } )
    public void testDeleteNonExistingFileThrows() throws IOException {
        Files.delete( absTA() );
    }

    // another parent of root problem
    @Test( expected = Exception.class )
    @Category( { Writable.class, Delete.class } )
    public void testDeleteRootThrows() throws IOException {
        Files.delete( defaultRoot() );
    }

    @Test
    @Category( { Writable.class, Delete.class } )
    public void testDeleteRecreate() throws IOException {
        Files.delete( fileTAB() );
        Files.write( absTAB(), CONTENT, CREATE_NEW, WRITE );
        assertThat( Files.readAllBytes( absTAB() ), is( CONTENT ) );
    }

    // check that no internal stuff is left laying around
    @Test
    @Category( { Writable.class, Delete.class } )
    public void testDeleteIfExistsRecreate() throws IOException {
        Files.deleteIfExists( fileTAB() );
        Files.write( absTAB(), CONTENT, CREATE, WRITE, TRUNCATE_EXISTING );
        assertThat( Files.readAllBytes( absTAB() ), is( CONTENT ) );
    }

    @Test
    @Category( { Writable.class, Move.class } )
    public void testRenamingAFileRemovesNameFromParentsDirStream() throws IOException {

        Path file = srcFile();

        Files.move( file, file.getParent().resolve( "tgt" ) );

        try( DirectoryStream<Path> kids = Files.newDirectoryStream( file.getParent() ) ) {
            assertThat( file, not( isIn( kids ) ) );
        }
    }

    @Test
    @Category( { Writable.class, Move.class } )
    public void testRenamingAFileAddsNameToParentsDirStream() throws IOException {
        Path tgt = src().getParent().resolve( "tgt" );
        Files.move( srcFile(), tgt );

        try( DirectoryStream<Path> kids = Files.newDirectoryStream( src().getParent() ) ) {
            assertThat( tgt, isIn( kids ) );
        }
    }

    @Test( expected = NoSuchFileException.class )
    @Category( { Writable.class, Move.class } )
    public void testMoveToFileWithNonExistingParentThrows() throws IOException {
        Files.move( srcFile(), absTAB() );
    }

    @Test
    @Category( { Writable.class, Delete.class, MoveWhile.class } )
    public void testDeleteWhileReading() throws IOException {
        Path file = fileTA();

        try( ByteChannel ch = Files.newByteChannel( file, Sets.asSet( StandardOpenOption.READ ) ) ) {
            Files.delete( file );
            assertThat( file, Matchers.not( exists() ) );

            int i = ch.read( ByteBuffer.allocate( 2 ) );
            assertThat( i, is( 2 ) );
        }

        assertThat( file, Matchers.not( exists() ) );
    }

    @Test
    @Category( { Writable.class, Delete.class, MoveWhile.class } )
    public void testDeleteWhileWriting() throws IOException {
        Path file = fileTA();

        try( ByteChannel ch = Files.newByteChannel( file, Sets.asSet( WRITE ) ) ) {
            Files.delete( file );
            assertThat( file, Matchers.not( exists() ) );

            int i = ch.write( ByteBuffer.wrap( CONTENT_OTHER ) );
        }

        assertThat( file, Matchers.not( exists() ) );
    }

    @Test
    @Category( { Writable.class, Delete.class, MoveWhile.class } )
    public void testMoveWhileWriting() throws IOException {
        Path file = fileTA();

        try( ByteChannel ch = Files.newByteChannel( file, Sets.asSet( WRITE ) ) ) {

            Files.move( file, absTB() );
            assertThat( file, Matchers.not( exists() ) );

            ByteBuffer bb = ByteBuffer.wrap( CONTENT_OTHER );
            ch.write( bb );
        }

        assertThat( Files.readAllBytes( absTB() ), is( CONTENT_OTHER ) );
    }


    /*
     * ---------------------------------------------------------------------------------------------
     */

    public Tests04Copy( FSDescription capa ) {
        super( capa );
    }

//    public static class CapaBuilder04 extends CapBuilder03 {
//        public CopyBuilder copy() {
//            return new CopyBuilder( (AllCapabilitiesBuilder) this );
//        }
//    }
//
//    public static class CopyBuilder extends DetailBuilder {
//        public CopyBuilder( AllCapabilitiesBuilder builder ) {
//            super( builder );
//        }
//
//        public CopyBuilder inode( boolean val ) {
//            capa.addFeature( "CopyWhile", val );
//            capa.addFeature( "DeleteWhile", val );
//            capa.addFeature( "MoveWhile", val );
//            return this;
//        }
//
//        @Override
//        public AllCapabilitiesBuilder onOff( boolean val ) {
//            capa.addFeature( "Copy", val );
//            capa.addFeature( "Move", val );
//            capa.addFeature( "Delete", val );
//            return builder;
//        }
//    }

    protected Path src() {
        return absT().resolve( "src" );
    }

    protected Path srcFile() {
        if( Files.exists( src() ) ) {
            return src();
        }

        Filess.write( src(), CONTENT );
        return src();
    }

    protected Path srcDir() {
        Path ret = src();
        if( Files.exists( ret ) ) {
            return ret;
        }

        Filess.createDirectory( ret );
        return ret;
    }

    protected Path tgt() {
        Path ret = absT().resolve( "tgt" );
        return ret;
    }

}
