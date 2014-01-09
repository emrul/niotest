package org.opencage.niotest.lindwurm.tests;

import org.junit.Test;
import org.opencage.kleinod.collection.Sets;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.DirectoryStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.opencage.niotest.lindwurm.matcher.FileTimeMatcher.after;
import static org.opencage.niotest.lindwurm.matcher.PathExists.exists;

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
public abstract class PathTest3IT extends PathTest2IT{


    @Test( expected = NoSuchFileException.class )
    public void testWriteFileWithoutExistingParent() throws IOException {
        Files.write( getPathPAB(), CONTENT );

    }

    @Test
    public void testRWBytes() throws IOException {
        Path target = getPathPA();

        Files.write( target, CONTENT );
        byte[] out = Files.readAllBytes( target );

        assertArrayEquals( CONTENT, out );
    }

    @Test
    public void testNewFileIsInDirStream() throws IOException {
        Path file = getPathPA();

        Files.write( file, CONTENT );

        boolean found = false;
        try( DirectoryStream<Path> kids = Files.newDirectoryStream( file.getParent() )) {
            for( Path kid : kids ) {
                if ( kid.equals( file )) {
                    found = true;
                    break;
                }
            }
        }

        assertTrue( "new dir not in dirstream of parent", found );
    }



    @Test
    public void testRWBytes20k() throws IOException {

        Path target = getPathPA();

        Files.write( target, CONTENT20k );
        byte[] out = Files.readAllBytes( target );

        assertArrayEquals( CONTENT20k, out );
    }
//
//    @Test
//    public void testRWBytesLarge() throws IOException {
//        byte[] in = new byte[200000];
//        for ( int i = 0; i < 200000; i++ ) {
//            in[i] = (byte) (i);
//        }
//
//        Path target = emptyDir("RWBytesLarge").resolve( p.getLegalPathElement() );
//
//        Files.write( target, in );
//
//        byte[] out = Files.readAllBytes( target );
//
//        assertArrayEquals( in,out );
//    }
//
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

//        assumeTrue( p.hasTmpDir() );

        Path target = getPathPA();
        Files.write( target, CONTENT20k );


        byte[] out = new byte[2*CONTENT20k.length ];
        int sum = 0;
        try ( SeekableByteChannel readChannel = Files.newByteChannel( target, StandardOpenOption.READ )) {

            while( true ) {
                ByteBuffer bb = ByteBuffer.wrap( out, sum, 8009 );
                int count = readChannel.read( bb );
                if ( count < 0 ) {
                    break;
                }

                sum += count;

                assertTrue( sum < CONTENT20k.length + 200 );
            }
        }


        assertThat( sum, is(CONTENT20k.length) );
        assertArrayEquals( CONTENT20k, Arrays.copyOfRange( out, 0, sum ));
    }

    @Test( expected = ClosedChannelException.class )
    public void testReadFromClosedChannel() throws IOException {
        Path target = getPathPA();
        Files.write( target, CONTENT );

        try ( SeekableByteChannel read =  Files.newByteChannel( target )) {
            read.close();
            assertThat( read.isOpen(), is( false ));
            read.read( ByteBuffer.allocate(30) ); // should throw
        }
    }

    @Test
    public void testSize() throws IOException {

        Path target = getPathPA();
        Files.write( target, CONTENT );

        assertEquals( CONTENT.length, Files.readAttributes( target, BasicFileAttributes.class ).size() );
    }

    @Test
    public void testFileAttributesAreImmutable() throws Exception {

        Path file = getPathPA();
        Files.write( file, CONTENT );
        BasicFileAttributes atti = Files.readAttributes( file, BasicFileAttributes.class );
        long oldSize = atti.size();
        Files.write( file, CONTENT_OTHER );

        assertEquals( oldSize, atti.size() );
    }

    @Test( expected = NoSuchFileException.class )
    public void testWriteNonExistent() throws IOException {

        Path notthere = getPathPA();

        try ( SeekableByteChannel ch = p.FS.provider().newByteChannel( notthere, Collections.singleton( StandardOpenOption.WRITE ) )) {}
    }

    @Test
    public void testWriteNothingOverExistingFileDoesNotChangeIt() throws IOException {

        Path there = getPathPAe();

        try ( SeekableByteChannel ch = p.FS.provider().newByteChannel( there, Collections.singleton( StandardOpenOption.WRITE ) )) {}

        assertEquals( CONTENT.length, Files.size( there ) );
    }

    @Test
    public void testWriteAndCreateNonExistent() throws IOException {

        Path notthere = getPathPA();

        Set<StandardOpenOption> options = Sets.asSet(
            StandardOpenOption.WRITE, StandardOpenOption.CREATE );

        try ( SeekableByteChannel ch = p.FS.provider().newByteChannel( notthere, options )) {}

        assertThat( notthere, exists() );
    }

    @Test
    public void testWriteAndCreateExistent() throws IOException {

        Path there = getPathPAe();

        Set<StandardOpenOption> options = Sets.asSet(
            StandardOpenOption.WRITE, StandardOpenOption.CREATE );

        try ( SeekableByteChannel ch = p.FS.provider().newByteChannel( there, options )) {}

        assertThat( there, exists() );
    }

    @Test
    public void testWriteAndCreateNewNonExistent() throws IOException {

        Path notthere = getPathPA();

        Set<StandardOpenOption> options = Sets.asSet(
            StandardOpenOption.WRITE, StandardOpenOption.CREATE_NEW );

        try ( SeekableByteChannel ch = p.FS.provider().newByteChannel( notthere, options )) {}

        assertThat( notthere, exists() );
    }

    @Test( expected = FileAlreadyExistsException.class )
    public void testWriteOnlyNew() throws IOException {

        Path there = getPathPAe();

        Set<StandardOpenOption> options = Sets.asSet(
                StandardOpenOption.WRITE, StandardOpenOption.CREATE_NEW );

        try ( SeekableByteChannel ch = p.FS.provider().newByteChannel( there, options )) {}
    }

    @Test
    public void testOverwriteTruncateExisting() throws IOException {

        Path there = getPathPAe();

        Set<StandardOpenOption> options = Sets.asSet(
            StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING );

        try ( SeekableByteChannel ch = p.FS.provider().newByteChannel( there, options )) {}

        assertThat( there, exists() );
        assertEquals( 0, Files.size( there ) );
    }

    @Test
    public void testCreateFileSetsModifiedDateOfParent() throws IOException, InterruptedException {

        Path tmp = emptyDir();

        FileTime created = Files.getLastModifiedTime( tmp );

        Thread.sleep( 2000 );

        Files.write( tmp.resolve( p.nameStr[0] ), CONTENT );
        FileTime modified = Files.getLastModifiedTime( tmp );

        assertThat( "created after modified", modified, after( created ) );
    }

    @Test
    public void testCreateFileSetModifiedDate() throws Exception{
        Path     file          = getPathPA();
        FileTime parentCreated = Files.getLastModifiedTime( file.getParent() );
        Thread.sleep( 2000 );

        getPathPAe(); // creates file

        assertThat( Files.getLastModifiedTime( file ), after(parentCreated) );
    }

    @Test
    public void testGetLastModifiedOnlyChangedByModification() throws Exception {
        Path file = getPathPAe();
        FileTime modified = Files.getLastModifiedTime( file );
        Thread.sleep( 2000 );
        FileTime notmodified = Files.getLastModifiedTime( file );

        assertThat( notmodified, is( modified ) );
    }


}
