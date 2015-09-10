package de.pfabulist.lindwurm.niotest.tests;

import de.pfabulist.kleinod.collection.Sets;
import de.pfabulist.lindwurm.niotest.tests.topics.FileChannelT;
import de.pfabulist.lindwurm.niotest.tests.topics.Scatter;
import de.pfabulist.lindwurm.niotest.tests.topics.Writable;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.FileChannel;
import java.nio.channels.NonReadableChannelException;
import java.nio.channels.NonWritableChannelException;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;

import static java.nio.file.StandardOpenOption.CREATE_NEW;
import static java.nio.file.StandardOpenOption.READ;
import static java.nio.file.StandardOpenOption.WRITE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Fail.fail;

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
public abstract class Tests18FileChannels extends Tests17Windows {
    public Tests18FileChannels( FSDescription capa ) {
        super( capa );
    }

    @Test
    @Category( FileChannelT.class )
    public void testOpenFilChannel() throws IOException {
        try( FileChannel fch = FileChannel.open( fileTA(), Sets.asSet( WRITE, READ ) ) ) {
        } catch( Exception e ) {
            fail( "channels should be supported" );
        }
    }

    @Test
    @Category( FileChannelT.class )
    public void testTransferFromOfClosedFileChannelThrows() throws IOException {

        try( SeekableByteChannel ch = Files.newByteChannel( fileTB() );
             FileChannel fch = FileChannel.open( fileTA(), Sets.asSet( WRITE, READ ) ) ) {

            fch.close();
            assertThatThrownBy( () -> fch.transferFrom( ch, 0, 2 ) ).isInstanceOf( ClosedChannelException.class );
        }
    }

    @Test
    @Category( { FileChannelT.class, Writable.class } )
    public void testTransferToClosedFileChannelThrows() throws IOException {

        try( SeekableByteChannel ch = Files.newByteChannel( fileTB() );
             FileChannel fch = FileChannel.open( fileTA(), Sets.asSet( WRITE, READ ) ) ) {

            fch.close();
            assertThatThrownBy( () -> fch.transferTo( 0, 2, ch ) ).isInstanceOf( ClosedChannelException.class );
        }
    }

    @Test
    @Category( { FileChannelT.class, Writable.class } )
    public void testTransferFromWrites() throws IOException {

        Files.write( absTB(), CONTENT_OTHER );

        try( SeekableByteChannel ch = Files.newByteChannel( absTB() );
             FileChannel fch = FileChannel.open( fileTA(), Sets.asSet( WRITE, READ ) ) ) {

            fch.transferFrom( ch, 0, CONTENT_OTHER.length );
        }

        assertThat( Files.readAllBytes( absTA() ) ).isEqualTo( CONTENT_OTHER );
    }

    @Test
    @Category( { FileChannelT.class, Writable.class } )
    public void testTransferFromLeavesPositionUnchanged() throws IOException {

        Files.write( absTB(), CONTENT_OTHER );

        try( SeekableByteChannel ch = Files.newByteChannel( absTB() );
             FileChannel fch = FileChannel.open( fileTA(), Sets.asSet( WRITE, READ ) ) ) {

            fch.transferFrom( ch, 1, CONTENT_OTHER.length );

            assertThat( fch.position() ).isEqualTo( 0 );
        }
    }

    @Test
    @Category( { FileChannelT.class, Writable.class } )
    public void testTransferFromWritesFromPosition() throws IOException {

        Files.write( absTB(), CONTENT_OTHER );

        try( SeekableByteChannel ch = Files.newByteChannel( absTB() );
             FileChannel fch = FileChannel.open( fileTA(), Sets.asSet( WRITE, READ ) ) ) {

            fch.transferFrom( ch, 1, CONTENT_OTHER.length );
        }

        assertThat( Files.readAllBytes( absTA() )[ 1 ] ).isEqualTo( CONTENT_OTHER[ 0 ] );
        assertThat( Files.readAllBytes( absTA() )[ 5 ] ).isEqualTo( CONTENT_OTHER[ 4 ] );
    }

    @Test
    @Category( FileChannelT.class )
    public void testTransferFromOnReadonlyChannelThrows() throws IOException {

        Files.write( absTB(), CONTENT_OTHER );

        try( SeekableByteChannel ch = Files.newByteChannel( absTB() );
             FileChannel fch = FileChannel.open( fileTA(), Sets.asSet( READ ) ) ) {

            assertThatThrownBy( () -> fch.transferFrom( ch, 0, CONTENT_OTHER.length ) ).isInstanceOf( NonWritableChannelException.class );
        }
    }

    @Test
    @Category( { FileChannelT.class, Writable.class } )
    public void testTransferFromWithNegativePositionThrows() throws IOException {

        Files.write( absTB(), CONTENT_OTHER );

        try( SeekableByteChannel ch = Files.newByteChannel( absTB() );
             FileChannel fch = FileChannel.open( fileTA(), Sets.asSet( WRITE, READ ) ) ) {

            assertThatThrownBy( () -> fch.transferFrom( ch, -1, CONTENT_OTHER.length ) ).isInstanceOf( IllegalArgumentException.class );
        }
    }

    @Test
    @Category( { FileChannelT.class, Writable.class } )
    public void testTransferFromWithNegativeLengthThrows() throws IOException {

        Files.write( absTB(), CONTENT_OTHER );

        try( SeekableByteChannel ch = Files.newByteChannel( absTB() );
             FileChannel fch = FileChannel.open( fileTA(), Sets.asSet( WRITE, READ ) ) ) {

            assertThatThrownBy( () -> fch.transferFrom( ch, 0, -1 ) ).isInstanceOf( IllegalArgumentException.class );
        }
    }

    @Test
    @Category( { FileChannelT.class, Writable.class } )
    public void testTransferFromPositionBeyondFileSizeDoesNothing() throws IOException {

        Files.write( absTB(), CONTENT_OTHER );

        try( SeekableByteChannel ch = Files.newByteChannel( absTB() );
             FileChannel fch = FileChannel.open( fileTA(), Sets.asSet( WRITE, READ ) ) ) {

            fch.transferFrom( ch, CONTENT.length * 2, CONTENT_OTHER.length );
        }

        assertThat( Files.readAllBytes( absTA() ) ).isEqualTo( CONTENT );
    }

    @Test
    @Category( { FileChannelT.class, Writable.class } )
    public void testTransferFromWithSourceChannelPositionNotZero() throws IOException {

        Files.write( absTB(), CONTENT_OTHER );

        try( SeekableByteChannel ch = Files.newByteChannel( absTB() );
             FileChannel fch = FileChannel.open( fileTA(), Sets.asSet( WRITE, READ ) ) ) {

            ch.position( 1 );

            fch.transferFrom( ch, 0, CONTENT_OTHER.length - 1);

            assertThat( ch.position() ).isEqualTo(  CONTENT_OTHER.length );
        }

        assertThat( Files.readAllBytes( absTA() )[ 0 ] ).isEqualTo( CONTENT_OTHER[ 1 ] );
        assertThat( Files.readAllBytes( absTA() )[ 5 ] ).isEqualTo( CONTENT_OTHER[ 6 ] );

    }

    @Test
    @Category( { FileChannelT.class, Writable.class } )
    public void testTransferFromFromNonReadableChannelThrows() throws IOException {

        Files.write( absTB(), CONTENT_OTHER );
        SeekableByteChannel ch = Files.newByteChannel( absTB(), WRITE );

        try( FileChannel fch = FileChannel.open( fileTA(), Sets.asSet( READ, WRITE ) ) ) {
            assertThatThrownBy( () -> fch.transferFrom( ch, 0, CONTENT_OTHER.length ) ).isInstanceOf( NonReadableChannelException.class );
        }
    }

    @Test
    @Category( { FileChannelT.class, Writable.class } )
    public void testTransferFromSourceWithLessThanRequestedBytesGetsWhatsThere() throws IOException {

        Files.write( absTB(), CONTENT_OTHER );
        SeekableByteChannel ch = Files.newByteChannel( absTB() );

        try( FileChannel fch = FileChannel.open( fileTA(), Sets.asSet( READ, WRITE ) ) ) {
            long count = fch.transferFrom( ch, 0, CONTENT_OTHER.length * 2 );

            assertThat( count ).isEqualTo( CONTENT_OTHER.length );
        }

        assertThat( Files.readAllBytes( absTA() ) ).isEqualTo( CONTENT_OTHER );
    }

    @Test
    @Category( { FileChannelT.class } )
    public void testTransferToWritesToChannel() throws IOException {

        try( SeekableByteChannel ch = Files.newByteChannel( absTB(), CREATE_NEW, WRITE );
             FileChannel fch = FileChannel.open( fileTA(), Sets.asSet( READ ) ) ) {

            fch.transferTo( 0, CONTENT.length, ch );
        }

        assertThat( Files.readAllBytes( absTB() ) ).isEqualTo( CONTENT );
    }

    @Test
    @Category( { FileChannelT.class } )
    public void testTransferToDoesNotModifyPosition() throws IOException {


        try( SeekableByteChannel ch = Files.newByteChannel( absTB(), CREATE_NEW, WRITE );
             FileChannel fch = FileChannel.open( fileTA(), Sets.asSet( READ ) ) ) {

            fch.position( 3 );
            fch.transferTo( 0, CONTENT.length - 3, ch );

            assertThat( fch.position() ).isEqualTo( 3 );
        }
    }

    @Test
    @Category( { FileChannelT.class, Writable.class } )
    public void testTransferToFromWriteOnlyThrows() throws IOException {


        try( SeekableByteChannel ch = Files.newByteChannel( absTB(), CREATE_NEW, WRITE );
             FileChannel fch = FileChannel.open( fileTA(), Sets.asSet( WRITE ) ) ) {

            assertThatThrownBy( () ->fch.transferTo( 0, CONTENT.length, ch )).isInstanceOf( NonReadableChannelException.class );
        }
    }

    @Test
    @Category( { FileChannelT.class, Writable.class } )
    public void testTransferToFromNegativePosition() throws IOException {


        try( SeekableByteChannel ch = Files.newByteChannel( absTB(), CREATE_NEW, WRITE );
             FileChannel fch = FileChannel.open( fileTA(), Sets.asSet( READ ) ) ) {

            assertThatThrownBy( () ->fch.transferTo( -1, CONTENT.length, ch )).isInstanceOf( IllegalArgumentException.class );
        }
    }

    @Test
    @Category( { FileChannelT.class, Writable.class } )
    public void testTransferToNegativeContentLengthThrows() throws IOException {

        try( SeekableByteChannel ch = Files.newByteChannel( absTB(), CREATE_NEW, WRITE );
             FileChannel fch = FileChannel.open( fileTA(), Sets.asSet( READ ) ) ) {

            assertThatThrownBy( () ->fch.transferTo( 0, -5 , ch )).isInstanceOf( IllegalArgumentException.class );
        }
    }

    @Test
    @Category( { FileChannelT.class, Writable.class } )
    public void testTransferToNonWritableChannelThrows() throws IOException {

        try( SeekableByteChannel ch = Files.newByteChannel( fileTB(), READ );
             FileChannel fch = FileChannel.open( fileTA(), Sets.asSet( READ ) ) ) {

            assertThatThrownBy( () ->fch.transferTo( 0, 3 , ch )).isInstanceOf( NonWritableChannelException.class );
        }
    }

    @Test
    @Category( { FileChannelT.class, Writable.class } )
    public void testTransferToTransfersOnlyWhatsThere() throws IOException {

        try( SeekableByteChannel ch = Files.newByteChannel( absTB(), CREATE_NEW, WRITE );
             FileChannel fch = FileChannel.open( fileTA(), Sets.asSet( READ ) ) ) {

            long count = fch.transferTo( 0, CONTENT.length * 2 , ch );

            assertThat( count ).isEqualTo( CONTENT.length );
        }

        assertThat( Files.readAllBytes( absTB() )).isEqualTo( CONTENT );
    }

    @Test
    @Category( { FileChannelT.class, Writable.class } )
    public void testTransferToFromPosition() throws IOException {

        try( SeekableByteChannel ch = Files.newByteChannel( absTB(), CREATE_NEW, WRITE );
             FileChannel fch = FileChannel.open( fileTA(), Sets.asSet( READ ) ) ) {

            fch.transferTo( 3, CONTENT.length - 3 , ch );
        }

        assertThat( Files.readAllBytes( absTB() )[0]).isEqualTo( CONTENT[3] );
        assertThat( Files.readAllBytes( absTB() )[2]).isEqualTo( CONTENT[5] );
    }

    // todo is this obvious from seekablebytechannel ?
    @Test
    @Category( { FileChannelT.class, Writable.class } )
    public void testFileChannelRead() throws IOException {

        ByteBuffer bb = ByteBuffer.allocate( CONTENT.length );
        try( FileChannel fch = FileChannel.open( fileTA(), Sets.asSet( READ ) ) ) {
            fch.read( bb );
        }

        assertThat( bb.array() ).isEqualTo( CONTENT );
    }

    @Test
    @Category( { FileChannelT.class, Writable.class, Scatter.class } )
    public void testFileChannelScatteredRead() throws IOException {

        ByteBuffer bb1 = ByteBuffer.allocate( 5 );
        ByteBuffer bb2 = ByteBuffer.allocate( CONTENT.length - 5 );
        try( FileChannel fch = FileChannel.open( fileTA(), Sets.asSet( READ ) ) ) {
            fch.read( new ByteBuffer[]{bb1, bb2} );
        }

        assertThat( bb1.array()[4] ).isEqualTo( CONTENT[4] );
        assertThat( bb2.array()[0] ).isEqualTo( CONTENT[5] );
    }


    @Test
    @Category( { FileChannelT.class } )
    public void testForce() throws IOException {

        try( SeekableByteChannel ch = Files.newByteChannel( absTB(), CREATE_NEW, WRITE );
             FileChannel fch = FileChannel.open( fileTA(), Sets.asSet( READ ) ) ) {

            fch.transferTo( 0, CONTENT.length, ch );
            fch.force( true );

            assertThat( Files.readAllBytes( absTB() ) ).isEqualTo( CONTENT );
        }
    }

//    @Test
//    @Category( { FileChannelT.class, FileLockT.class } )
//    public void testFileLock() throws IOException {
//
//        try( SeekableByteChannel ch = Files.newByteChannel( absTB(), CREATE_NEW, WRITE );
//             FileChannel fch = FileChannel.open( fileTA(), Sets.asSet( READ ) ) ) {
//
//            FileLock lock = fch.lock();
//
//            assertThat( Files.readAllBytes( absTB() ) ).isEqualTo( CONTENT );
//        }
//    }
}
