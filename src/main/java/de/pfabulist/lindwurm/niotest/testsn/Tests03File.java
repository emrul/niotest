package de.pfabulist.lindwurm.niotest.testsn;

import de.pfabulist.kleinod.collection.Sets;
import de.pfabulist.lindwurm.niotest.matcher.IteratorMatcher;
import de.pfabulist.lindwurm.niotest.tests.TimeConversion;
import de.pfabulist.lindwurm.niotest.testsn.setup.Capa;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.NonReadableChannelException;
import java.nio.channels.NonWritableChannelException;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.Arrays;
import java.util.Date;
import java.util.Set;

import static de.pfabulist.lindwurm.niotest.matcher.ExceptionMatcher.throwsException;
import static de.pfabulist.lindwurm.niotest.matcher.FileTimeMatcher.isCloseTo;
import static de.pfabulist.lindwurm.niotest.matcher.PathExists.exists;
import static java.nio.file.StandardOpenOption.*;
import static java.util.Collections.singleton;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.number.OrderingComparison.greaterThan;
import static org.junit.Assert.assertThat;

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
public abstract class Tests03File extends Tests02Dir {


    @Test( expected = NoSuchFileException.class )
    public void testReadCreateNonExistingFileThrows() throws IOException {
        Files.newByteChannel( absTA(), Sets.asSet(READ, CREATE_NEW, TRUNCATE_EXISTING, DELETE_ON_CLOSE, DSYNC, CREATE) );
    }


    @Test( expected = NoSuchFileException.class )
    public void testWriteFileWithoutExistingParentThrows() throws IOException {
        Files.write( absTAB(), CONTENT, CREATE, CREATE_NEW, WRITE );
    }

    @Test
    public void testWriteWithoutOptionsCreatesTheFile() throws IOException {
        Path target = absTA();
        Files.write( target, CONTENT );
        assertThat( target, exists() );
    }

    @Test( expected = IllegalArgumentException.class )
    public void testChannelSetNegativePositionThrows() throws IOException {
        try( SeekableByteChannel channel = Files.newByteChannel( fileTA(), WRITE, CREATE )) {
            channel.position( -1 );
        }
    }

    @Test
    public void testChannelGetSize() throws IOException {
        Path target = fileTA();
        try( SeekableByteChannel channel = Files.newByteChannel( target, WRITE, CREATE )) {
            assertThat( channel.size(), is( Files.size( target ) ));
        }
    }

    // todo
//    @Test( expected = IllegalArgumentException.class )
//    public void testChannelSetPositionBeyonedSizeEnlargesFile() throws IOException {
//        Path target = fileTA();
//        try( SeekableByteChannel channel = Files.newByteChannel( target, WRITE, CREATE )) {
//            channel.position( 100 );
//             write
//
//        }
//        assertThat( target, exists() );
//    }

    @Test
    public void testWriteWithoutOptionsOverwritesExisting() throws IOException {
        Path target = fileTA();
        Files.write( target, CONTENT_OTHER );
        assertThat( Files.readAllBytes( target ), is( CONTENT_OTHER ) );
    }


    @Test
    public void testRWBytes() throws IOException {
        Path target = absTA();

        Files.write( target, CONTENT, standardOpen );
        byte[] out = Files.readAllBytes( target );

        assertThat( out, is(CONTENT));
    }

    @Test
    public void testNewFileIsInDirStream() throws IOException {
        Path file = absTA();

        Files.write( file, CONTENT, standardOpen );

        try( DirectoryStream<Path> kids = Files.newDirectoryStream( file.getParent() )) {
            assertThat( file, IteratorMatcher.isIn(kids) );
        }
    }

    @Test
    public void testRWBytes20k() throws IOException {

        Path target = absTA();

        Files.write( target, CONTENT20k, standardOpen );
        byte[] out = Files.readAllBytes( target );

        assertThat( out, is( CONTENT20k ) );
    }

//    @Test
//    public void testRWBytesHugh() throws IOException {
//        byte[] in = new byte[5000000];
//        for ( int i = 0; i < 5000000; i++ ) {
//            in[i] = (byte) (i);
//        }
//
//        Path target = emptyDir("RWBytesHugh").resolve( p.getLegalPathElement() );
//
//        Files.write( target, in );
//
//        byte[] out = Files.readAllBytes( target );
//
//        assertArrayEquals( in,out );
//    }
//
//
    @Test
    public void testReadChunks() throws IOException {

        Path target = absTA();
        Files.write( target, CONTENT20k, standardOpen );


        byte[] out = new byte[2*CONTENT20k.length ];
        int sum = 0;
        try ( SeekableByteChannel readChannel = Files.newByteChannel( target, READ )) {

            while( true ) {
                ByteBuffer bb = ByteBuffer.wrap( out, sum, 8009 );
                int count = readChannel.read( bb );
                if ( count < 0 ) {
                    break;
                }

                sum += count;

                assertThat( sum, lessThan(CONTENT20k.length + 200) );
            }
        }

        assertThat( sum, is(CONTENT20k.length) );
        assertThat( Arrays.copyOfRange( out, 0, sum ), is(CONTENT20k) );
    }


    @Test
    public void testReadSmallChunks() throws IOException {

        Path target = absTA();
        Files.write( target, CONTENT50, standardOpen );


        byte[] out = new byte[2*CONTENT50.length ];
        int sum = 0;
        try ( SeekableByteChannel readChannel = Files.newByteChannel( target, READ )) {

            while( true ) {
                ByteBuffer bb = ByteBuffer.wrap( out, sum, 12 );
                int count = readChannel.read( bb );
                if ( count < 0 ) {
                    break;
                }

                sum += count;

                assertThat( sum, lessThan(CONTENT50.length + 20) );
            }
        }

        assertThat( sum, is(CONTENT50.length) );
        assertThat( Arrays.copyOfRange( out, 0, sum ), is(CONTENT50) );
    }

    @Test
    public void testWriteSmallChunks() throws Exception{
        Path target = absTA();

        Files.write( target, Arrays.copyOfRange( CONTENT, 0, 3 ));
        Files.write( target, Arrays.copyOfRange( CONTENT, 3, CONTENT.length ), APPEND );

        assertThat( Files.readAllBytes( target ), is(CONTENT) );
    }

    @Test
    public void testClosedChannelIsClosed() throws IOException {
        try ( SeekableByteChannel read =  Files.newByteChannel( fileTA() )) {
            read.close();
            assertThat( read.isOpen(), is( false ) );
        }
    }


    @Test
    public void testReadFromClosedChannelThrows() throws IOException {
        try ( SeekableByteChannel read =  Files.newByteChannel( fileTA() )) {
            read.close();
            assertThat( () -> {read.read( ByteBuffer.allocate(30) );}, throwsException( ClosedChannelException.class ));
        }
    }

    @Test
    public void testWriteToClosedChannelThrows() throws IOException {
        try ( SeekableByteChannel write =  Files.newByteChannel( fileTA(), WRITE )) {
            write.close();
            assertThat( () -> {write.write( ByteBuffer.allocate( 30 ) );}, throwsException( ClosedChannelException.class ));
        }
    }

    @Test
    public void testSize() throws IOException {

        Path target = absTA();
        Files.write( target, CONTENT, standardOpen );
        assertThat( Files.readAttributes( target, BasicFileAttributes.class ).size(), is( (long)CONTENT.length ));
    }


    @Test
    public void testFileAttributesAreImmutable() throws Exception {

        Path file = absTA();
        Files.write( file, CONTENT, standardOpen );
        BasicFileAttributes atti = Files.readAttributes( file, BasicFileAttributes.class );
        long oldSize = atti.size();
        Files.write( file, CONTENT_OTHER, APPEND );

        assertThat( atti.size(), is(oldSize) );
    }

    @Test( expected = NoSuchFileException.class )
    public void testWriteNonExistent() throws IOException {

        Path notthere = absTA();

        try ( SeekableByteChannel ch = FS.provider().newByteChannel( notthere, singleton(WRITE) )) {}
    }

    @Test
    public void testWriteNothingOverExistingFileDoesNotChangeIt() throws IOException {
        Path there = fileTA();
        try ( SeekableByteChannel ch = FS.provider().newByteChannel( there, singleton(WRITE) )) {}
        assertThat( Files.size( there ), is((long)CONTENT.length) );
    }

    @Test
    public void testWriteAndCreateNonExistentCreatesIt() throws IOException {
        try ( SeekableByteChannel ch = FS.provider().newByteChannel( absTA(), Sets.asSet( WRITE, CREATE ) )) {}
        assertThat( absTA(), exists() );
    }

    @Test
    public void testWriteAndCreateExistent() throws IOException {

        Path there = fileTA();

        Set<StandardOpenOption> options = Sets.asSet(
                WRITE, CREATE );

        try ( SeekableByteChannel ch = FS.provider().newByteChannel( there, options )) {}

        assertThat( there, exists() );
    }

    @Test
    public void testAppend() throws IOException {
        Path there = fileTA();
        Files.write( there, CONTENT, APPEND );

        assertThat( Files.size( there ), is(CONTENT.length*2L) );

        byte[] out = Files.readAllBytes( there );

        assertThat( Arrays.copyOfRange( out, 0, CONTENT.length ), is(CONTENT) );
        assertThat( Arrays.copyOfRange( out, CONTENT.length, 2 * CONTENT.length ), is(CONTENT) );
    }

    @Test( expected = IllegalArgumentException.class )
    public void testAppendAndReadThrows() throws IOException {
        try ( SeekableByteChannel ch = FS.provider().newByteChannel( fileTA(), Sets.asSet( APPEND, READ ) )) {}
    }



    @Test
    public void testWriteAndCreateNewNonExistent() throws IOException {

        Path notthere = absTA();

        Set<StandardOpenOption> options = Sets.asSet(
            WRITE, StandardOpenOption.CREATE_NEW );

        try ( SeekableByteChannel ch = FS.provider().newByteChannel( notthere, options )) {}

        assertThat( notthere, exists() );
    }

    @Test( expected = FileAlreadyExistsException.class )
    public void testWriteOnlyNew() throws IOException {

        Path there = fileTA();

        Set<StandardOpenOption> options = Sets.asSet(
                WRITE, StandardOpenOption.CREATE_NEW );

        try ( SeekableByteChannel ch = FS.provider().newByteChannel( there, options )) {}
    }

    @Test( expected = FileAlreadyExistsException.class )
    public void testWriteOnlyNewIfCreateIsThereToo() throws IOException {

        Path there = fileTA();

        Set<StandardOpenOption> options = Sets.asSet( WRITE, CREATE_NEW, CREATE );

        try ( SeekableByteChannel ch = FS.provider().newByteChannel( there, options )) {}
    }


    @Test
    public void testOverwriteTruncateExisting() throws IOException {

        Path there = fileTA();

        Set<StandardOpenOption> options = Sets.asSet(
                WRITE, TRUNCATE_EXISTING );

        try ( SeekableByteChannel ch = FS.provider().newByteChannel( there, options )) {}

        assertThat( there, exists() );
        assertThat( Files.size( there ), is(0L));
    }

    @Test
    public void testOverwriteTruncateExistingDoesNotChangeCreationTime() throws IOException {

        Path there = fileTA();

        FileTime created = Files.readAttributes( there, BasicFileAttributes.class ).creationTime();
        Set<StandardOpenOption> options = Sets.asSet( WRITE, TRUNCATE_EXISTING );

        try ( SeekableByteChannel ch = FS.provider().newByteChannel( there, options )) {}

        assertThat( Files.readAttributes( there, BasicFileAttributes.class ).creationTime(), is(created ));
    }


    @Test
    @Category( SlowTest.class )
    public void testOverwriteSetLastAccessTime() throws IOException, InterruptedException {
        Path there = fileTA();
        FileTime before = Files.readAttributes( there, BasicFileAttributes.class ).lastAccessTime();
        waitForAttribute();

        Set<StandardOpenOption> options = Sets.asSet(
                WRITE, TRUNCATE_EXISTING );
        try ( SeekableByteChannel ch = FS.provider().newByteChannel( there, options )) {}

        assertThat( Files.readAttributes( there, BasicFileAttributes.class ).lastAccessTime(), greaterThan( before ) );
    }
    
    @Test
    @Category( SlowTest.class )
    public void testOverwriteDoesNotSetLastAccessTimeOfParent() throws IOException, InterruptedException {
        Path there = fileTA();
        FileTime before = Files.readAttributes( there.getParent(), BasicFileAttributes.class ).lastAccessTime();
        waitForAttribute();

        Set<StandardOpenOption> options = Sets.asSet( WRITE, TRUNCATE_EXISTING );
        try ( SeekableByteChannel ch = FS.provider().newByteChannel( there, options )) {}

        assertThat( Files.readAttributes( there.getParent(), BasicFileAttributes.class ).lastAccessTime(), is( before ) );
    }

    @Test
    @Category( SlowTest.class )
    public void testCreateFileSetsModifiedTimeOfParent() throws IOException, InterruptedException {

        Path file = absTA();
        FileTime created = Files.getLastModifiedTime( file.getParent() );

        waitForAttribute();

        Files.write( file, CONTENT, standardOpen );
        FileTime modified = Files.getLastModifiedTime( file.getParent() );

        assertThat( "created after modified", modified, greaterThan( created ) );
    }

    @Test
    public void testCreateFileSetsLastAccessTime() throws IOException, InterruptedException {
        Path file = fileTA();
        BasicFileAttributes bfa = Files.readAttributes( file, BasicFileAttributes.class );

        assertThat( bfa.lastAccessTime(), isCloseTo( bfa.creationTime()));
    }

    @Test
    @Category( SlowTest.class )
    public void testCreateFileSetsLastAccessTimeOfParent() throws IOException, InterruptedException {
        Path dir = dirTA();
        FileTime before = Files.readAttributes( dir, BasicFileAttributes.class ).lastAccessTime();
        waitForAttribute();

        fileTAB();
        assertThat( Files.readAttributes( dir, BasicFileAttributes.class ).lastAccessTime(), greaterThan( before ));
    }
    
    @Test
    @Category( SlowTest.class )
    public void testCreateFileSetModifiedTime() throws Exception{
        Path     file          = absTA();
        FileTime parentCreated = Files.getLastModifiedTime( file.getParent() );
        waitForAttribute();

        fileTA(); // creates file

        assertThat( Files.getLastModifiedTime( file ), greaterThan( parentCreated) );
    }

    @Test
    @Category( SlowTest.class )
    public void testGetLastModifiedOnlyChangedByModification() throws Exception {
        Path file = fileTA();
        FileTime modified = Files.getLastModifiedTime( file );
        waitForAttribute();
        FileTime notmodified = Files.getLastModifiedTime( file );

        assertThat( notmodified, is( modified ) );
    }

    @Test
    public void testModifiedDateIsCloseToCurrentTime() throws Exception{
        Path     file   = fileTA();
        FileTime before = TimeConversion.toFileTime( new Date());
        assertThat( Files.getLastModifiedTime( file ), isCloseTo(before) );
    }

    @Test
    @Category( SlowTest.class )
    public void testReadFileSetsLastAccessTime() throws Exception{
        Path     file    = fileTA();
        FileTime before  = Files.readAttributes( file, BasicFileAttributes.class ).lastAccessTime();
        waitForAttribute();

        Files.readAllBytes( file );

        assertThat( Files.readAttributes( file, BasicFileAttributes.class ).lastAccessTime(), greaterThan( before ));
    }

    @Test
    @Category( SlowTest.class )
    public void testReadFileDoesNotSetParentsLastAccessTime() throws Exception{
        Path     file    = fileTA();
        FileTime before  = Files.readAttributes( file.getParent(), BasicFileAttributes.class ).lastAccessTime();
        waitForAttribute();

        Files.readAllBytes( file );

        assertThat( Files.readAttributes( file.getParent(), BasicFileAttributes.class ).lastAccessTime(), is( before ));
    }

    @Test
    public void testReadFromExhausted() throws IOException {

        try( SeekableByteChannel channel = FS.provider().newByteChannel( fileTA(), singleton(READ))) {
            channel.read( ByteBuffer.allocate( CONTENT.length * 2 ) );

            // should be empty now

            ByteBuffer bb = ByteBuffer.allocate( CONTENT.length * 2 );
            int ret = channel.read( bb );

            assertThat( ret, is(-1) );
            assertThat( bb.position(), is(0) );
        }
    }

    @Test
    public void testChannelSetPositionSetsPosition() throws IOException {

        Path file = fileTA();
        try( SeekableByteChannel out =  FS.provider().newByteChannel( file, singleton(WRITE) )) {
            out.position( 1 );
            assertThat( out.position(), is(1L) );
        }
    }


    @Test
    public void testRandomWrite() throws IOException {
        Path file = fileTA();

        try( SeekableByteChannel out =  FS.provider().newByteChannel( file, singleton(WRITE) )) {
            out.position( 1 );
            out.write( ByteBuffer.wrap( "waa".getBytes( "UTF-8" ) ) );
        }

        assertThat( new String(Files.readAllBytes( file ), "UTF-8"), is( "hwaahere" ));
    }

    @Test
    public void testWriteBeyondFileSize() throws IOException {
        Path file = fileTA();
        try( SeekableByteChannel out = FS.provider().newByteChannel( file, singleton(WRITE) )) {
            out.position( 100 );
            out.write( ByteBuffer.wrap( "ha".getBytes( "UTF-8" ) ) );
        }

        assertThat( Files.size( file ), is(102L));
    }

    // actually that should work: make the assert
//    @Test( expected = UnsupportedOperationException.class )
//    public void testWriteBeyondFileSizeUnsupported() throws IOException {
//
//        Path file = fileTA();
//        try( SeekableByteChannel out = FS.provider().newByteChannel( file, Collections.singleton(WRITE) )) {
//            out.position( 100 );
//            out.write( ByteBuffer.wrap( "ha".getBytes( "UTF-8" ) ) );
//        }
//
//    }


    @Test
    public void testRandomRead() throws IOException {
        Path file = fileTA();
        try( SeekableByteChannel channel =  FS.provider().newByteChannel( file, singleton(READ) )){
            channel.position( 1 );
            ByteBuffer buffer = ByteBuffer.allocate( 2 );
            channel.read( buffer );

            assertThat( buffer.array(), is( Arrays.copyOfRange( CONTENT, 1, 3 )));
        }
    }

    @Test
    public void testRandomReadPosition() throws IOException {
        Path file = fileTA();
        try( SeekableByteChannel channel =  FS.provider().newByteChannel( file, singleton(READ) )){
            channel.position( 1 );
            ByteBuffer buffer = ByteBuffer.allocate( 2 );
            channel.read( buffer );

            assertThat( channel.position(), is(3L));
        }
    }

    @Test
    public void testReadAndWrite() throws IOException {
        Path file = fileTA();
        try( SeekableByteChannel inout =  FS.provider().newByteChannel( file, Sets.asSet(WRITE, READ) )) {
            inout.position( 1 );
            inout.write( ByteBuffer.wrap( "waa".getBytes( "UTF-8" ) ) );

            assertThat( new String(Files.readAllBytes( file ), "UTF-8"), is( "hwaahere" ));

            inout.position( 2 );
            ByteBuffer buffer = ByteBuffer.allocate( 2 );
            inout.read( buffer );
            assertThat( buffer.array()[ 0 ], is( "a".getBytes()[ 0 ] ) );
        }
    }

    @Test( expected = NonReadableChannelException.class )
    public void testReadFromWriteOnlyChannelThrows() throws IOException {
        Path file = fileTA();
        try( SeekableByteChannel channel =  FS.provider().newByteChannel( file, Sets.asSet(WRITE) )) {

            channel.read( ByteBuffer.allocate( 2 ) );
        }
    }

    @Test( expected = NonWritableChannelException.class )
    public void testWriteToReadOnlyChannelThrows() throws IOException {
        Path file = fileTA();
        try( SeekableByteChannel channel =  FS.provider().newByteChannel( file, Sets.asSet(READ) )) {

            channel.write( ByteBuffer.allocate( 2 ) );
        }
    }

    @Test
    public void testTruncate() throws Exception{
        Path file = fileTA();
        try( SeekableByteChannel channel =  FS.provider().newByteChannel( file, Sets.asSet(WRITE) )) {
            channel.truncate( 2 );
        }

        assertThat( Files.size( file ), is(2L) );
    }

    @Test( expected = NonWritableChannelException.class )
    public void testTruncateOnReadChannelThrows() throws Exception{
        Path file = fileTA();
        try( SeekableByteChannel channel =  FS.provider().newByteChannel( file, Sets.asSet(READ) )) {
            channel.truncate( 2 );
        }
    }

    @Test( expected = ClosedChannelException.class )
    public void testTruncateOnClosedChannelThrows() throws Exception{
        Path file = fileTA();
        try( SeekableByteChannel channel =  FS.provider().newByteChannel( file, Sets.asSet(WRITE) )) {
            channel.close();
            channel.truncate( 2 );
        }
    }

    @Test( expected = IllegalArgumentException.class )
    public void testTruncateToNegativeSizeThrows() throws Exception{
        Path file = fileTA();
        try( SeekableByteChannel channel =  FS.provider().newByteChannel( file, Sets.asSet(WRITE) )) {
            channel.truncate( -7 );
        }
    }

    @Test( expected = FileSystemException.class )
    public void testWriteChannelOfDir() throws IOException {
        try( SeekableByteChannel channel =  FS.provider().newByteChannel( dirTA(), Sets.asSet(WRITE) )) {
        }
    }

    // TODO windows only
    @Test( expected = FileSystemException.class )
    public void testReadChannelOfDir() throws IOException {
        try( SeekableByteChannel channel =  FS.provider().newByteChannel( dirTA(), Sets.asSet(READ) )) {
        }
    }

    @Test
    @Category( SlowTest.class )
    public void testEveryChannelWriteUpdatesLastModifiedTime() throws IOException, InterruptedException {

        Path file =  fileTA();
        FileTime before = Files.getLastModifiedTime(file);

        try( SeekableByteChannel channel =  FS.provider().newByteChannel( file, Sets.asSet(WRITE) )) {
            for ( int i = 0; i < 3; i++ ) {
                waitForAttribute();
                ByteBuffer bb = ByteBuffer.allocate(20);
                bb.array()[5] = (byte) i;
                channel.write(bb);
                
                FileTime after = Files.getLastModifiedTime(file);
                assertThat( after, greaterThan( before ));
                before = after;
            }
        }
    }

    @Test
    @Category( SlowTest.class )
    public void testEveryChannelReadUpdatesLastAccessTime() throws IOException, InterruptedException {
        Path file =  fileTA();
        FileTime before = Files.readAttributes( file, BasicFileAttributes.class).lastAccessTime();

        try( SeekableByteChannel channel =  FS.provider().newByteChannel( file, Sets.asSet(READ) )) {
            for ( int i = 0; i < 3; i++ ) {
                waitForAttribute();
                ByteBuffer bb = ByteBuffer.allocate(3);
                channel.read( bb );

                FileTime after = Files.readAttributes( file, BasicFileAttributes.class).lastAccessTime();
                assertThat( after, greaterThan( before ));
                before = after;
            }
        }
    }



    // todo
//    @Test( expected = NonWritableChannelException.class )
//    public void testTruncateOnAppendChannelThrows() throws Exception{
//        Path file = fileTA();
//        try( SeekableByteChannel channel =  FS.provider().newByteChannel( file, Sets.asSet(APPEND) )) {
//            channel.truncate( 2 );
//        }
//    }

    // todo unclear
//    @Test
//    public void testTruncat2e() throws Exception{
//        Path file = fileTA();
//        try( SeekableByteChannel channel =  FS.provider().newByteChannel( file, Sets.asSet(WRITE) )) {
//            channel.position( 5 );
//            channel.truncate( 2 );
//
//            assertThat( channel.position(), is(5L) );
//        }
//    }

    
    /*
     * -----------------------
     */

    public Tests03File( Capa capa) {
        super(capa);
    }


    protected static OpenOption[] standardOpen = { CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE };

}
