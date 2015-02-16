package de.pfabulist.lindwurm.niotest.testsn;

import de.pfabulist.kleinod.collection.Sets;
import de.pfabulist.lindwurm.niotest.tests.ClosedFSVars;
import de.pfabulist.lindwurm.niotest.testsn.setup.*;
import org.junit.Test;

import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.spi.FileSystemProvider;
import java.util.Collections;

import static java.nio.file.StandardOpenOption.READ;
import static org.hamcrest.CoreMatchers.is;
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
public abstract class Tests07Closed extends Tests06Attributes {

    public static final String CLOSEABLE_PLAYGROUND = "closeablePlayground";

    public Tests07Closed( Capa capa) {
        super(capa);
    }
    
    public static class CapBuilder07 extends CapBuilder03 {
        public CloseableBuilder closeable() {
            return new CloseableBuilder((AllCapabilitiesBuilder) this);
        }

        public static class CloseableBuilder extends DetailBuilder {
            public CloseableBuilder(AllCapabilitiesBuilder descr) {
                super(descr);
            }

            @Override
            public AllCapabilitiesBuilder onOff(boolean val) {
                capa.addFeature( "ClosedFS", val );
                return builder;
            }
            
            public CloseableBuilder playground(Path path) {
                capa.attributes.put( CLOSEABLE_PLAYGROUND, path );
                return this;
            }
        }
    }    

    @Test
    public void testClosedFSisClosed() throws Exception {
        assertThat( getClosedFS().isOpen(), is(false) );
    }
//

//    // TODO
//    @Test
//    public void testStdFSisOpen() throws Exception {
//        assumeThat( capabilities.isClosable(), is(true ) );
//    }
//
    @Test( expected = ClosedFileSystemException.class )
    public void testClosedFSCantRead() throws Exception {
        Files.readAllBytes( getClosedFileA() );
    }

    @Test( expected = ClosedFileSystemException.class )
    public void testClosedFSCantReadDir() throws Exception {
        Files.newDirectoryStream( getClosedDirB() );
    }

    @Test( expected = ClosedChannelException.class )
    public void testClosedFSCantUseReadChannelPosition() throws Exception {
        getClosedReadChannel().position();
    }

    @Test( expected = ClosedChannelException.class )
    public void testClosedFSCantUseReadChannelRead() throws Exception {
        getClosedReadChannel().read( ByteBuffer.allocate(2) );
    }

    @Test( expected = ClosedChannelException.class )
    public void testClosedFSCantUseReadChannelSize() throws Exception {
        getClosedReadChannel().size();
    }
//
//    // todo test all other methods on all other channels
//
    @Test( expected = FileSystemNotFoundException.class )
    public void testCantGetClosedFSViaURI() throws Exception {
        getClosedFSProvider().getFileSystem(getClosedURI());
    }

    @Test( expected = ClosedFileSystemException.class )
    public void testClosedFSnewByteChannel() throws Exception {
        getClosedFS();

        FS.provider().newByteChannel( getClosedFileA(), Sets.asSet(READ) );
    }

    @Test( expected = ClosedFileSystemException.class )
    public void testClosedFSGetBasicFileAttributeViewProvider() throws IOException {
        getClosedFS();
        FS.provider().getFileAttributeView( getClosedFileA(), BasicFileAttributeView.class );
    }

    @Test( expected = ClosedFileSystemException.class )
    public void testClosedFSCreateDirectoryOtherProvider() throws IOException {
        getClosedFSProvider().createDirectory(getClosedFileA());
    }

    @Test( expected = ClosedFileSystemException.class )
    public void testClosedFSNewFileChannel() throws IOException {
        getClosedFSProvider().newFileChannel( getClosedFileA(), Collections.<OpenOption>emptySet() );
    }

    @Test( expected = ClosedFileSystemException.class )
    public void testClosedFSCheckAccess() throws IOException {
        getClosedFS();
        FS.provider().checkAccess( getClosedFileA() );
    }

    @Test( expected = ClosedFileSystemException.class )
    public void testCopyFromClosedFS() throws IOException {
        getClosedFSProvider().copy( getClosedFileA(), dirTA() );
    }

    @Test( expected = ClosedFileSystemException.class )
    public void testCopyToClosedFS() throws IOException {
        getClosedFSProvider().copy( fileTA(), getClosedFileA() );
    }

    @Test( expected = ClosedFileSystemException.class )
    public void testMoveToClosedFS() throws IOException {
        getClosedFSProvider().move( fileTA(), getClosedFileA() );
    }

    @Test( expected = ClosedFileSystemException.class )
    public void testClosedFSCreateHardLink() throws IOException {
        getClosedFSProvider().createLink( getClosedFileA(), getClosedFileA() );
    }

//    //TODO
//    @Test( expected = ProviderMismatchException.class )
//    public void testCreateSymbolicLinkOtherProvider() throws IOException {
//        assumeThat( capabilities.hasSymbolicLinks(), Is.is(true) );
//        assumeThat( capabilities.isClosable(), is(true ) );
//
//        getClosedFSProvider().createSymbolicLink( getClosedFileA(), getClosedFileA() );
//    }
//
    @Test( expected = ClosedFileSystemException.class )
    public void testClosedFSDelete() throws IOException {
        getClosedFSProvider().delete( getClosedFileA() );
    }

    @Test( expected = ClosedFileSystemException.class )
    public void testClosedFSGetFileStore() throws IOException {
        getClosedFSProvider().getFileStore( getClosedFileA() );
    }


    @Test( expected = ClosedFileSystemException.class )
    public void testClosedFSIsHidden() throws IOException {
        getClosedFSProvider().isHidden( getClosedFileA() );
    }

    // todo
//    @Test( expected = ClosedFileSystemException.class )
//    public void testClosedFSNewAsynchronousFileChannel() throws IOException {
//        assumeThat( capabilities.hasAsynchronousFileChannels(), Is.is(true) );
//
//        getClosedFSProvider().newAsynchronousFileChannel( getClosedFileA(), Collections.<OpenOption>emptySet(), null );
//    }
//
    @Test( expected = ClosedFileSystemException.class )
    public void
    testClosedFSNewInputStream() throws IOException {
        getClosedFSProvider().newOutputStream( getClosedFileA() );
    }
//
    @Test( expected = ClosedFileSystemException.class )
    public void testClosedFSNewOutputStream() throws IOException {
        getClosedFSProvider().newOutputStream( getClosedFileA() );
    }

    @Test( expected = ClosedFileSystemException.class )
    public void testClosedFSReadAttributes() throws IOException {
        getClosedFSProvider().readAttributes( getClosedFileA(), BasicFileAttributes.class );
    }

    @Test( expected = ClosedFileSystemException.class )
    public void testClosedFSReadAttributesString() throws IOException {
        getClosedFSProvider().readAttributes( getClosedFileA(), "*" );
    }

    
    public FileSystem getClosedFS() throws IOException {

        if ( capa.closedFSVars == null ) {
            capa.closedFSVars = new ClosedFSVars( capa.get( Path.class, CLOSEABLE_PLAYGROUND));
        }

        if ( capa.closedFSVars.fs == null ) {
            capa.closedFSVars.fs = capa.closedFSVars.play.getFileSystem();
        }

        if ( !capa.closedFSVars.fs.isOpen() ) {
            return capa.closedFSVars.fs;
        }

        capa.closedFSVars.provider = capa.closedFSVars.fs.provider();

        capa.closedFSVars.dirB = capa.closedFSVars.play.resolve( nameB() );
        Files.createDirectories( capa.closedFSVars.dirB);

        capa.closedFSVars.fileA = capa.closedFSVars.play.resolve( nameA() );
        Files.write( capa.closedFSVars.fileA, CONTENT, standardOpen);



        Path closedCf = capa.closedFSVars.play.resolve( nameC() );
        Files.write( closedCf, CONTENT, standardOpen );
        capa.closedFSVars.readChannel = Files.newByteChannel( closedCf, READ );

        capa.closedFSVars.uri = capa.closedFSVars.play.getRoot().toUri();

        capa.closedFSVars.dirStream = Files.newDirectoryStream( capa.closedFSVars.play );

        try {
            capa.closedFSVars.watchService = capa.closedFSVars.fs.newWatchService();
        } catch( IOException e ) {
            // no watchservice provided
        }

        capa.closedFSVars.fs.close();

        return capa.closedFSVars.fs;
    }

    public FileSystemProvider getClosedFSProvider() throws IOException {
        getClosedFS();
        return FS.provider();
    }

    public Path getClosedFileA() throws IOException {
        getClosedFS();
        return capa.closedFSVars.fileA;
    }

    public Path getClosedDirB() throws IOException {
        getClosedFS();
        return capa.closedFSVars.dirB;
    }

    public URI getClosedURI() throws IOException {
        getClosedFS();
        return capa.closedFSVars.uri;
    }

    public SeekableByteChannel getClosedReadChannel() throws IOException {
        getClosedFileA();
        return capa.closedFSVars.readChannel;
    }

    public WatchService getClosedFSWatchService() throws IOException {
        getClosedFS();
        return capa.closedFSVars.watchService;
    }





}
