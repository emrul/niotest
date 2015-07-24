package de.pfabulist.lindwurm.niotest.tests;

import de.pfabulist.unchecked.Filess;
import de.pfabulist.kleinod.paths.Pathss;
import de.pfabulist.lindwurm.niotest.tests.topics.AsynchronousFileChannel;
import de.pfabulist.lindwurm.niotest.tests.topics.Copy;
import de.pfabulist.lindwurm.niotest.tests.topics.Delete;
import de.pfabulist.lindwurm.niotest.tests.topics.FileChannelT;
import de.pfabulist.lindwurm.niotest.tests.topics.HardLink;
import de.pfabulist.lindwurm.niotest.tests.topics.Move;
import de.pfabulist.lindwurm.niotest.tests.topics.SymLink;
import de.pfabulist.lindwurm.niotest.tests.topics.Writable;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.ProviderMismatchException;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

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
 * documentation and/or otherProviderAbsA() materials provided with the distribution.
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
public abstract class Tests09WrongProvider extends Tests08ThreadSafe {

    public static final String OTHER_PROVIDER_PLAYGROUND = "otherProviderPlayground";

    public Tests09WrongProvider( FSDescription capa ) {
        super( capa );
    }

//    public static class CapBuilder09 extends CapBuilder07 {
//        public AllCapabilitiesBuilder otherProviderPlayground( Path path ) {
//            capa.attributes.put("otherProviderPlayground", path );
//            return (AllCapabilitiesBuilder) this;
//        }
//    }

    @Test( expected = ProviderMismatchException.class )
    public void testNewByteChannelOtherProvider() throws IOException {
        FS.provider().newByteChannel( otherProviderAbsA(), Collections.<OpenOption> emptySet() );
    }

    @Test( expected = ProviderMismatchException.class )
    public void testGetBasicFileAttributeViewProvider() throws IOException {
        FS.provider().getFileAttributeView( otherProviderAbsA(), BasicFileAttributeView.class );
    }

    @Test( expected = ProviderMismatchException.class )
    @Category( { Writable.class } )
    public void testCreateDirectoryOtherProvider() throws IOException {
        FS.provider().createDirectory( otherProviderAbsA() );
    }

    @Test( expected = ProviderMismatchException.class )
    @Category( FileChannelT.class )
    public void testNewFileChannelOtherProvider() throws IOException {
        FS.provider().newFileChannel( otherProviderAbsA(), Collections.<OpenOption> emptySet() );
    }

    @Test( expected = ProviderMismatchException.class )
    public void testCheckAccessOtherProvider() throws IOException {
        FS.provider().checkAccess( otherProviderAbsA() );
    }

    @Test( expected = ProviderMismatchException.class )
    @Category( { Copy.class, Writable.class } )
    public void testCopyOtherProviderFrom() throws IOException {
        FS.provider().copy( otherProviderAbsA(), absTA() );
    }

    @Test( expected = ProviderMismatchException.class )
    @Category( { Copy.class, Writable.class } )
    public void testCopyOtherProviderTo() throws IOException {
        FS.provider().copy( fileTA(), otherProviderAbsA() );
    }

//    @Test
//    public void testCopyOtherProviderWithFiles() throws IOException {
//        Path defaultTarget = PathUtils.getTmpDir("foo").resolve("duh");
//        Files.createDirectories( defaultTarget.getParent());
//
//        Files.copy(fileTAB(), defaultTarget);
//
//        assertThat( Files.readAllBytes(defaultTarget), is(CONTENT));
//        Files.deleteIfExists(defaultTarget);
//    }

    @Test( expected = ProviderMismatchException.class )
    @Category( { Move.class, Writable.class } )
    public void testMoveOtherProviderFrom() throws IOException {
        FS.provider().move( otherProviderAbsA(), absTA() );
    }

    @Test( expected = ProviderMismatchException.class )
    @Category( { Copy.class, Writable.class } )
    public void testMoveOtherProviderTo() throws IOException {
        FS.provider().move( fileTAB(), otherProviderAbsA() );
    }

//    @Test
//    public void testMoveOtherProviderWithFiles() throws IOException {
//        Path defaultTarget = PathUtils.getTmpDir("foo").resolve("duh");
//        Files.createDirectories( defaultTarget.getParent());
//
//        Files.move(fileTAB(), defaultTarget);
//
//        assertThat( Files.readAllBytes(defaultTarget), is(CONTENT));
//        Files.deleteIfExists(defaultTarget);
//    }

    @Test( expected = ProviderMismatchException.class )
    @Category( { HardLink.class, Writable.class } )
    public void testHardLinkOfDifferentProvider() throws IOException {
        FS.provider().createLink( otherProviderAbsA(), fileTA() );
    }

    @Test( expected = ProviderMismatchException.class )
    @Category( { Writable.class, SymLink.class } )
    public void testCreateSymLinkOtherProvider() throws IOException {
        FS.provider().createSymbolicLink( otherProviderAbsA(), otherProviderAbsA() );
    }

    @Test( expected = ProviderMismatchException.class )
    @Category( { Delete.class, Writable.class } )
    public void testDeleteOtherProvider() throws IOException {
        FS.provider().delete( otherProviderAbsA() );
    }

    @Test( expected = ProviderMismatchException.class )
    @Category( { Delete.class, Writable.class } )
    public void testDeleteIfExistsOtherProvider() throws IOException {
        FS.provider().deleteIfExists( otherProviderAbsA() );
    }

    @Test( expected = ProviderMismatchException.class )
    public void testGetFileStoreOtherProvider() throws IOException {
        FS.provider().getFileStore( otherProviderAbsA() );
    }

    @Test( expected = IllegalArgumentException.class )
    public void testGetPathOtherURI() throws IOException {
        FS.provider().getPath( otherProviderAbsA().toUri() );
    }

//    @Test( expected = IllegalArgumentException.class )
//    public void testGetFileSystemOtherURI() throws IOException {
//        FS.provider().getFileSystem( capabilities.toURI().apply( otherProviderAbsA().getFileSystem()));
//    }

    @Test( expected = ProviderMismatchException.class )
    public void testIsHiddenOtherProvider() throws IOException {
        FS.provider().isHidden( otherProviderAbsA() );
    }

    @Test( expected = ProviderMismatchException.class )
    @Category( { FileChannelT.class, AsynchronousFileChannel.class } )
    public void testNewAsynchronousFileChannelOtherProvider() throws IOException {
        FS.provider().newAsynchronousFileChannel( otherProviderAbsA(), Collections.<OpenOption> emptySet(), null );
    }

//    @Test( expected = ProviderMismatchException.class )
//    public void testNewInputStreamOtherProvider() throws IOException {
//        assumeThat( FS, not(is( FileSystems.getDefault())));
//
//        FS.provider().newInputStream( otherProviderAbsA() );
//    }

    @Test( expected = ProviderMismatchException.class )
    @Category( Writable.class )
    public void testNewOutputStreamOtherProvider() throws IOException {
        FS.provider().newOutputStream( otherProviderAbsA() );
    }

    @Test( expected = ProviderMismatchException.class )
    public void testNewDirectoryStreamOtherProvider() throws IOException {
        FS.provider().newDirectoryStream( otherProviderAbsA(), null );
    }

    @Test( expected = ProviderMismatchException.class )
    public void testReadAttributesOtherProvider() throws IOException {
        FS.provider().readAttributes( otherProviderAbsA(), BasicFileAttributes.class );
    }

    @Test( expected = ProviderMismatchException.class )
    public void testReadAttributesStringOtherProvider() throws IOException {
        FS.provider().readAttributes( otherProviderAbsA(), "*" );
    }

    @Test( expected = ProviderMismatchException.class )
    @Category( SymLink.class )
    public void testReadSymLinkOtherProvider() throws IOException {
        FS.provider().readSymbolicLink( otherProviderAbsA() );
    }

    @Test( expected = ProviderMismatchException.class )
    public void testResolveWithPathFromOtherProvider() throws IOException {
        absD().resolve( otherProviderAbsA() ); //, is(otherProviderAbsA()));
    }

    @Test
    public void testIsSameFileOtherProvider() throws IOException {
        assertThat( FS.provider().isSameFile( otherProviderAbsA(), getFile() ), is( false ) );
    }

    @Test
    public void testIsSameFileOtherProvider2() throws IOException {
        assertThat( FS.provider().isSameFile( getFile(), otherProviderAbsA() ), is( false ) );
    }

    /*
     * ---------------------------------------------
     */

    protected Path otherProviderAbsA() {
        return getOtherProviderPlayground().resolve( nameA() );
    }

    protected Path getOtherProviderPlayground() {

        if( description.otherProviderPlayground == null ) {
            Path other = (Path) description.get( OTHER_PROVIDER_PLAYGROUND );
            if( other != null ) {
                description.otherProviderPlayground = other;
            } else {
                if( FS.equals( FileSystems.getDefault() ) ) {
                    throw new IllegalStateException();
                }

                description.otherProviderPlayground = Pathss.getTmpDir( "other" );
            }
        }

        return description.otherProviderPlayground;
    }

    protected Path otherProviderFileA() {
        Path ret = otherProviderAbsA();
        if( !Files.exists( ret ) ) {
            Filess.createDirectories( ret.getParent() );
            Filess.write( ret, CONTENT );
        }
        return ret;
    }

}
