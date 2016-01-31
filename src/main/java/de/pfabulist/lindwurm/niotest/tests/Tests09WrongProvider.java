package de.pfabulist.lindwurm.niotest.tests;

import de.pfabulist.kleinod.nio.Filess;
import de.pfabulist.kleinod.nio.Pathss;
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

import static de.pfabulist.kleinod.nio.PathIKWID.childGetParent;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * ** BEGIN LICENSE BLOCK *****
 * BSD License (2 clause)
 * Copyright (c) 2006 - 2016, Stephan Pfab
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
@SuppressWarnings( { "PMD.TooManyMethods" } ) // todo ??
public abstract class Tests09WrongProvider extends Tests08ThreadSafe {

    public static final String OTHER_PROVIDER_PLAYGROUND = "otherProviderPlayground";

    public Tests09WrongProvider( FSDescription capa ) {
        super( capa );
    }

    @Test
    public void testNewByteChannelOtherProvider() throws IOException {
        assertThatThrownBy( () -> FS.provider().newByteChannel( otherProviderAbsA(), Collections.<OpenOption> emptySet() ) ).isInstanceOf( ProviderMismatchException.class );
    }

    @Test
    public void testGetBasicFileAttributeViewProvider() throws IOException {
        assertThatThrownBy( () -> FS.provider().getFileAttributeView( otherProviderAbsA(), BasicFileAttributeView.class ) ).isInstanceOf( ProviderMismatchException.class );
    }

    @Test
    @Category( { Writable.class } )
    public void testCreateDirectoryOtherProvider() throws IOException {
        assertThatThrownBy( () -> FS.provider().createDirectory( otherProviderAbsA() ) ).isInstanceOf( ProviderMismatchException.class );
    }

    @Test
    @Category( FileChannelT.class )
    public void testNewFileChannelOtherProvider() throws IOException {
        assertThatThrownBy( () -> FS.provider().newFileChannel( otherProviderAbsA(), Collections.<OpenOption> emptySet() ) ).isInstanceOf( ProviderMismatchException.class );
    }

    @Test
    public void testCheckAccessOtherProvider() throws IOException {
        assertThatThrownBy( () -> FS.provider().checkAccess( otherProviderAbsA() ) ).isInstanceOf( ProviderMismatchException.class );
    }

    @Test
    @Category( { Copy.class, Writable.class } )
    public void testCopyOtherProviderFrom() throws IOException {
        assertThatThrownBy( () -> FS.provider().copy( otherProviderAbsA(), absTA() ) ).isInstanceOf( ProviderMismatchException.class );
    }

    @Test
    @Category( { Copy.class, Writable.class } )
    public void testCopyOtherProviderTo() throws IOException {
        assertThatThrownBy( () -> FS.provider().copy( fileTA(), otherProviderAbsA() ) ).isInstanceOf( ProviderMismatchException.class );
    }

    // todo
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

    @Test
    @Category( { Move.class, Writable.class } )
    public void testMoveOtherProviderFrom() throws IOException {
        assertThatThrownBy( () -> FS.provider().move( otherProviderAbsA(), absTA() ) ).isInstanceOf( ProviderMismatchException.class );
    }

    @Test
    @Category( { Copy.class, Writable.class } )
    public void testMoveOtherProviderTo() throws IOException {
        assertThatThrownBy( () -> FS.provider().move( fileTAB(), otherProviderAbsA() ) ).isInstanceOf( ProviderMismatchException.class );
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

    @Test
    @Category( { HardLink.class, Writable.class } )
    public void testHardLinkOfDifferentProvider() throws IOException {
        assertThatThrownBy( () -> FS.provider().createLink( otherProviderAbsA(), fileTA() ) ).isInstanceOf( ProviderMismatchException.class );
    }

    @Test
    @Category( { Writable.class, SymLink.class } )
    public void testCreateSymLinkFromOtherProviderPath() throws IOException {
        assertThatThrownBy( () -> FS.provider().createSymbolicLink( otherProviderAbsA(), fileTA() ) ).isInstanceOf( ProviderMismatchException.class );
    }

    @Test
    @Category( { Writable.class, SymLink.class } )
    public void testCreateSymLinkToPathFromOtherProvider() throws IOException {
        assertThatThrownBy( () -> FS.provider().createSymbolicLink( dirTA().resolve( "link" ), otherProviderAbsA() ) ).isInstanceOf( ProviderMismatchException.class );
    }

    @Test
    @Category( { Delete.class, Writable.class } )
    public void testDeleteOtherProvider() throws IOException {
        assertThatThrownBy( () -> FS.provider().delete( otherProviderAbsA() ) ).isInstanceOf( ProviderMismatchException.class );
    }

    @Test
    @Category( { Delete.class, Writable.class } )
    public void testDeleteIfExistsOtherProvider() throws IOException {
        assertThatThrownBy( () -> FS.provider().deleteIfExists( otherProviderAbsA() ) ).isInstanceOf( ProviderMismatchException.class );
    }

    @Test
    public void testGetFileStoreOtherProvider() throws IOException {
        assertThatThrownBy( () -> FS.provider().getFileStore( otherProviderAbsA() ) ).isInstanceOf( ProviderMismatchException.class );
    }

    @Test
    public void testGetPathOtherURI() throws IOException {
        assertThatThrownBy( () -> FS.provider().getPath( otherProviderAbsA().toUri() ) ).isInstanceOf( IllegalArgumentException.class );
    }

    // todo
//    @Test( expected = IllegalArgumentException.class )
//    public void testGetFileSystemOtherURI() throws IOException {
//        FS.provider().getFileSystem( capabilities.toURI().apply( otherProviderAbsA().getFileSystem()));
//    }

    @Test
    public void testIsHiddenOtherProvider() throws IOException {
        assertThatThrownBy( () -> FS.provider().isHidden( otherProviderAbsA() ) ).isInstanceOf( ProviderMismatchException.class );
    }

    @Test
    @Category( { FileChannelT.class, AsynchronousFileChannel.class } )
    public void testNewAsynchronousFileChannelOtherProvider() throws IOException {
        assertThatThrownBy( () -> FS.provider().newAsynchronousFileChannel( otherProviderAbsA(), Collections.<OpenOption> emptySet(), null ) ).isInstanceOf( ProviderMismatchException.class );
    }

    // todo
//    @Test
//    public void testNewInputStreamOtherProvider() throws IOException {
//        assumeThat( FS, not(is( FileSystems.getDefault())));
//
//        FS.provider().newInputStream( otherProviderAbsA() );
//    }

    @Test
    @Category( Writable.class )
    public void testNewOutputStreamOtherProvider() throws IOException {
        assertThatThrownBy( () -> FS.provider().newOutputStream( otherProviderAbsA() ) ).isInstanceOf( ProviderMismatchException.class );
    }

    @Test
    public void testNewDirectoryStreamOtherProvider() throws IOException {
        assertThatThrownBy( () -> FS.provider().newDirectoryStream( otherProviderAbsA(), null ) ).isInstanceOf( ProviderMismatchException.class );
    }

    @Test
    public void testReadAttributesOtherProvider() throws IOException {
        assertThatThrownBy( () -> FS.provider().readAttributes( otherProviderAbsA(), BasicFileAttributes.class ) ).isInstanceOf( ProviderMismatchException.class );
    }

    @Test
    public void testReadAttributesStringOtherProvider() throws IOException {
        assertThatThrownBy( () -> FS.provider().readAttributes( otherProviderAbsA(), "*" ) ).isInstanceOf( ProviderMismatchException.class );
    }

    @Test
    @Category( SymLink.class )
    public void testReadSymLinkOtherProvider() throws IOException {
        assertThatThrownBy( () -> FS.provider().readSymbolicLink( otherProviderAbsA() ) ).isInstanceOf( ProviderMismatchException.class );
    }

    @Test
    public void testResolveWithPathFromOtherProviderThrows() throws IOException {
        assertThatThrownBy( () -> absD().resolve( otherProviderAbsA() ) ).isInstanceOf( ProviderMismatchException.class );
    }

    @Test
    public void testIsSameFileOtherProvider() throws IOException {
        assertThat( FS.provider().isSameFile( otherProviderAbsA(), getFile() ) ).isFalse();
    }

    @Test
    public void testIsSameFileOtherProvider2() throws IOException {
        assertThat( FS.provider().isSameFile( getFile(), otherProviderAbsA() ) ).isFalse();
    }

    @Test
    public void testResolveSiblingOtherProviderThrows() {
        assertThatThrownBy( () -> absD().resolveSibling( otherProviderAbsA() ) ).isInstanceOf( ProviderMismatchException.class );
    }

    /*
     * ---------------------------------------------
     */

    protected Path otherProviderAbsA() {
        return getOtherProviderPlayground().resolve( nameA() );
    }

    @SuppressWarnings( "PMD.ConfusingTernary" ) // != null is positive case
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
            Filess.createDirectories( childGetParent( ret ) );
            Filess.write( ret, CONTENT );
        }
        return ret;
    }

}
