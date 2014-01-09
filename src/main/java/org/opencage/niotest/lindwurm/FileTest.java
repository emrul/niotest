package org.opencage.niotest.lindwurm;

import org.hamcrest.CoreMatchers;
import org.junit.Test;

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
import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;
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
public class FileTest extends DirTest {

    @Test( expected = NoSuchFileException.class )
    public void testWriteFileWithoutExistingParent() throws IOException {
        Path tmp = p.getTmpDir( "testWriteFileWithoutExistingParent" );

        Files.write( tmp.resolve( "not-there" ).resolve( "duda.txt" ), "hhh".getBytes( "UTF-8" ) );

    }

    @Test
    public void testRWBytes() throws IOException {
        byte[] in = new byte[20];
        for ( int i = 0; i < 20; i++ ) {
            in[i] = (byte) (i -5);
        }

        Path tmp = p.getTmpDir( "RWBytes" );
        Path target = tmp.resolve( p.getLegalPathElement() );

        Files.write( target, in );

        byte[] out = Files.readAllBytes( target );

        assertArrayEquals( in,out );
    }

    @Test
    public void testNewFileIsInDirStream() throws IOException {
        Path dir = p.getTmpDir("testNewFileIsInDirStream");
        Path file = dir.resolve( p.getLegalPathElement() );

        Files.write( file, "haaa".getBytes("UTF-8") );

        boolean found = false;
        try( DirectoryStream<Path> kids = Files.newDirectoryStream( dir )) {
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
    public void testRWBytes2() throws IOException {
        byte[] in = new byte[20000];
        for ( int i = 0; i < 20000; i++ ) {
            in[i] = (byte) (i);
        }

        Path target = p.getTmpDir("RWBytes2").resolve( p.getLegalPathElement() );

        Files.write( target, in );

        byte[] out = Files.readAllBytes( target );

        assertArrayEquals( in,out );
    }

    @Test
    public void testRWBytesLarge() throws IOException {
        byte[] in = new byte[200000];
        for ( int i = 0; i < 200000; i++ ) {
            in[i] = (byte) (i);
        }

        Path target = p.getTmpDir("RWBytesLarge").resolve( p.getLegalPathElement() );

        Files.write( target, in );

        byte[] out = Files.readAllBytes( target );

        assertArrayEquals( in,out );
    }

    @Test
    public void testRWBytesHugh() throws IOException {
        byte[] in = new byte[5000000];
        for ( int i = 0; i < 5000000; i++ ) {
            in[i] = (byte) (i);
        }

        Path target = p.getTmpDir("RWBytesHugh").resolve( p.getLegalPathElement() );

        Files.write( target, in );

        byte[] out = Files.readAllBytes( target );

        assertArrayEquals( in,out );
    }


    @Test
    public void testReadChunks() throws IOException {

        assumeTrue( p.hasTmpDir() );

        byte[] in = new byte[10000];
        for ( int i = 0; i < 10000; i++ ) {
            in[i] = (byte) (i);
        }

        Path target = p.getTmpDir("testReadChunks").resolve( p.getLegalPathElement() );

        Files.write( target, in );

        byte[] out = new byte[20000];
        int sum = 0;
        try ( SeekableByteChannel readChannel = Files.newByteChannel( target, StandardOpenOption.READ )) {

            while( true ) {
                ByteBuffer bb = ByteBuffer.wrap( out, sum, 8009 );
                int count = readChannel.read( bb );
                if ( count < 0 ) {
                    break;
                }

                sum += count;

                assertTrue( sum < 12000 );
            }
        }


        assertThat( sum, is(10000) );
        assertArrayEquals( in, Arrays.copyOfRange( out, 0, sum ));
    }

    @Test( expected = ClosedChannelException.class )
    public void testReadFromClosedChannel() throws IOException {
        byte[] in = new byte[20];
        for ( int i = 0; i < 20; i++ ) {
            in[i] = (byte) (i -5);
        }
        Path target = p.getTmpDir("readClosed").resolve( p.getLegalPathElement() );
        Files.write( target, in );

        try ( SeekableByteChannel read =  Files.newByteChannel( target )) {
            read.close();
            assertThat( read.isOpen(), CoreMatchers.is( false ));
            read.read( ByteBuffer.allocate(30) ); // should throw
        }
    }

    @Test
    public void testSize() throws IOException {

        int size = 20000;
        byte[] in = new byte[size];
        for ( int i = 0; i < size; i++ ) {
            in[i] = (byte) (i);
        }

        Path tmp = p.getTmpDir( "testSize" );
        Path target = tmp.resolve( p.getLegalPathElement() );

        Files.write( target, in );

        assertEquals( size, Files.readAttributes( target, BasicFileAttributes.class ).size() );
    }

    @Test
    public void testFileAttributesAreImmutable() throws Exception {

        Path file = getPathTmpA();
        Files.write( file, "hi".getBytes( "UTF-8" ) );
        BasicFileAttributes atti = Files.readAttributes( file, BasicFileAttributes.class );
        long oldSize = atti.size();

        Files.write( file, "wohaaaaa".getBytes( "UTF-8" ) );
        assertEquals( oldSize, atti.size() );
    }

    @Test( expected = NoSuchFileException.class )
    public void testWriteNonExistent() throws IOException {

        Path notthere = p.getTmpDir( "testWriteNonExistent" ).resolve( "not-exists" );

        try ( SeekableByteChannel ch = p.readOnlyFileSystem.provider().newByteChannel( notthere, Collections.singleton( StandardOpenOption.WRITE ) )) {}
    }

    @Test
    public void testWriteNothingOverExistingFileDoesNotChangeIt() throws IOException {

        Path there = p.getTmpDir( "testWriteExists" ).resolve( "not-exists" );
        Files.write( there, "huh".getBytes( "UTF-8" ) );

        try ( SeekableByteChannel ch = p.readOnlyFileSystem.provider().newByteChannel( there, Collections.singleton( StandardOpenOption.WRITE ) )) {}

        assertEquals( 3, Files.size( there ) );
    }

    @Test
    public void testWriteAndCreateNonExistent() throws IOException {

        Path notthere = p.getTmpDir( "testWriteAndCreateNonExistent" ).resolve( "not-exists" );

        Set<StandardOpenOption> options = new HashSet<>();
        options.add( StandardOpenOption.WRITE );
        options.add( StandardOpenOption.CREATE );

        try ( SeekableByteChannel ch = p.readOnlyFileSystem.provider().newByteChannel( notthere, options )) {}

        assertThat( notthere, exists() );
    }

    @Test
    public void testWriteAndCreateExistent() throws IOException {

        Path there = p.getTmpDir( "testWriteAndCreateNonExistent" ).resolve( "not-exists" );
        Files.write( there, "huh".getBytes( "UTF-8" ) );

        Set<StandardOpenOption> options = new HashSet<>();
        options.add( StandardOpenOption.WRITE );
        options.add( StandardOpenOption.CREATE );

        try ( SeekableByteChannel ch = p.readOnlyFileSystem.provider().newByteChannel( there, options )) {}

        assertThat( there, exists() );
    }

    @Test
    public void testWriteAndCreateNewNonExistent() throws IOException {

        Path notthere = p.getTmpDir( "testWriteAndCreateNewNonExistent" ).resolve( "not-exists" );

        Set<StandardOpenOption> options = new HashSet<>();
        options.add( StandardOpenOption.WRITE );
        options.add( StandardOpenOption.CREATE_NEW );

        try ( SeekableByteChannel ch = p.readOnlyFileSystem.provider().newByteChannel( notthere, options )) {}

        assertThat( notthere, exists() );
    }

    @Test( expected = FileAlreadyExistsException.class )
    public void testWriteOnlyNew() throws IOException {

        Path there = p.getTmpDir( "testWriteOnlyNew" ).resolve( "not-exists" );
        Files.write( there, "huh".getBytes( "UTF-8" ) );

        Set<StandardOpenOption> options = new HashSet<>();
        options.add( StandardOpenOption.WRITE );
        options.add( StandardOpenOption.CREATE_NEW );

        try ( SeekableByteChannel ch = p.readOnlyFileSystem.provider().newByteChannel( there, options )) {}
    }

    @Test
    public void testOverwriteTruncateExisting() throws IOException {

        Path there = p.getTmpDir( "testWriteOnlyNew" ).resolve( "not-exists" );
        Files.write( there, "huh".getBytes( "UTF-8" ) );

        Set<StandardOpenOption> options = new HashSet<>();
        options.add( StandardOpenOption.WRITE );
        options.add( StandardOpenOption.TRUNCATE_EXISTING );

        try ( SeekableByteChannel ch = p.readOnlyFileSystem.provider().newByteChannel( there, options )) {}

        assertThat( there, exists() );
        assertEquals( 0, Files.size( there ) );
    }

    @Test
    public void testCreateFileSetsModifiedDateOfParent() throws IOException, InterruptedException {

        Path tmp = p.getTmpDir("testCreateFileSetsModifiedDateOfParent");

        FileTime created = Files.getLastModifiedTime( tmp );

        Thread.sleep( 2000 );

        Files.write( tmp.resolve( "foo" ), "hh".getBytes() );
        FileTime modified = Files.getLastModifiedTime( tmp );

        assertTrue( "created after modified", created.compareTo( modified ) < 0 );
    }

    @Test
    public void testName() throws Exception {


    }
}
