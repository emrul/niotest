package de.pfabulist.lindwurm.niotest.tests;

import de.pfabulist.kleinod.text.Strings;
import org.junit.Test;

import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemAlreadyExistsException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Map;
import java.util.function.Function;

import static de.pfabulist.kleinod.nio.PathIKWID.absoluteGetRoot;
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
@SuppressWarnings( { "PMD.TooManyMethods" } ) // todo ?
public abstract class Tests05URI extends Tests04Copy {

    public Tests05URI( FSDescription capa ) {
        super( capa );
    }

    @Test
    public void testFileSystemOfAPathIsTheConstructingOne() {
        assertThat( FS ).isEqualTo( FS.getPath( "" ).getFileSystem() );
    }

    @Test
    public void testSeparatorIsNotEmpty() {
        assertThat( FS.getSeparator().isEmpty() ).isFalse();
    }

    @Test
    public void testSchemeIsNotEmpty() {
        assertThat( FS.provider().getScheme().isEmpty() ).isFalse();
    }

    @Test
    public void testProviderGetFileSystemWithWrongSchemeFails() {
        assertThatThrownBy( () -> FS.provider().getFileSystem( URI.create( FS.provider().getScheme() + "N:" ) ) ).
                isInstanceOf( IllegalArgumentException.class );
    }

    @Test
    public void testWrongUriAtProviderNewFails() throws IOException {
        assertThatThrownBy( () -> FS.provider().newFileSystem(
                URI.create( FS.provider().getScheme() + "N:" ),
                Collections.EMPTY_MAP ) ).
                isInstanceOf( IllegalArgumentException.class );
    }

    @Test
    public void testToUriOfRelativePathIsTheUriOfTheAbsolute() throws Exception {
        assertThat( pathDefault().toAbsolutePath().toUri() ).isEqualTo( pathDefault().toUri() );
    }

//    @Test( expected = IllegalArgumentException.class )
//    public void testGetUnknownFileSystemThrows() {
//        FS.provider().getFileSystem( URI.create( "michgibtsnich:wohoo" ) );
//    }

//    @Test
//    public void testNewFileSystem() throws IOException {
////        assumeTrue( !p.getCapabilities().oneFileSystemOnly() );
//
//        FileSystem fs  = FS.provider().newFileSystem( p.getUriForNewFS(), p.getEnvForNewFS() );
//
//        assertThat( fs, CoreMatchers.notNullValue() );
//    }

    @Test
    public void testGetExistingFileSystem() throws IOException {
        FileSystem fs = FS.provider().getFileSystem( toURI( FS ) );
        assertThat( fs ).isEqualTo( FS );
    }

    @Test
    public void testNewFileSystemOfExistingThrows() throws IOException {
        assertThatThrownBy( () -> FS.provider().newFileSystem( toURI( FS ), getEnv() ) ).
                isInstanceOf( FileSystemAlreadyExistsException.class );
    }

    @Test
    public void testPathToUriAndBackIsSame() {
        Path path = getNonExistingPath();
        URI uri = path.toUri();

        assertThat( uri ).isNotNull();

        Path back = Paths.get( uri );
        assertThat( back ).isEqualTo( path );
    }

    @Test
    public void testPathWithWitespaceToUri() {
        Path path = getEmptyDir().resolve( "z z" );
        URI uri = path.toUri();

        assertThat( uri ).isNotNull();
    }

    @Test
    public void testPathWithWitespaceToUriAndBack() {
        Path path = getEmptyDir().resolve( "z z" );
        URI uri = path.toUri();

        assertThat( uri ).isNotNull();

        Path back = Paths.get( uri );
        assertThat( back ).isEqualTo( path );
    }

    // ----------------------------------

    public URI toURI( FileSystem fs ) {
        Function<FileSystem, URI> toURI = (Function<FileSystem, URI>) description.props.get( "fsToUri" );

        if( toURI == null ) {
            return toURIWithRoot( fs );
        }

        return toURI.apply( fs );
    }

    public Map<String, ?> getEnv() {
        Map<String, ?> env = (Map<String, ?>) description.props.get( "env" );
        if( env == null ) {
            return Collections.EMPTY_MAP;
        }

        return env;
    }

    public static URI toURIWithRoot( FileSystem fs ) {
        return absoluteGetRoot( fs.getPath( "" ).toAbsolutePath() ).toUri();
    }

    public static URI toURIWithoutPath( FileSystem fs ) {
        Path root = absoluteGetRoot( fs.getPath( "" ).toAbsolutePath() );
        return URI.create( Strings.withoutSuffix( root.toUri().toString(), root.toString().replace( '\\', '/' ) ) );
    }

}
