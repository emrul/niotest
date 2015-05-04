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

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

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
public abstract class Tests05URI extends Tests04Copy {

    public Tests05URI( FSDescription capa ) {
        super( capa );
    }

    @Test
    public void testFileSystemOfAPathIsTheConstructingOne() {
        assertEquals( FS, FS.getPath( "" ).getFileSystem() );
    }

    @Test
    public void testSeparatorIsNotEmpty() {
        assertThat( FS.getSeparator().isEmpty(), is( false ) );
    }

    @Test
    public void testSchemeIsNotEmpty() {
        assertThat( FS.provider().getScheme().isEmpty(), is( false ) );
    }

    @Test( expected = IllegalArgumentException.class )
    public void testProviderGetFileSystemWithWrongSchemeFails() {
        FS.provider().getFileSystem(
                URI.create( FS.provider().getScheme() + "N:" ) );
    }

    @Test( expected = IllegalArgumentException.class )
    public void testWrongUriAtProviderNewFails() throws IOException {
        FS.provider().newFileSystem(
                URI.create( FS.provider().getScheme() + "N:" ),
                Collections.EMPTY_MAP );
    }

    @Test
    public void testToUriOfRelativePathIsTheUriOfTheAbsolute() throws Exception {
        assertThat( pathDefault().toAbsolutePath().toUri(), is( pathDefault().toUri() ) );
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
        assertThat( fs, is( FS ) );
    }

    @Test( expected = FileSystemAlreadyExistsException.class )
    public void testNewFileSystemOfExistingThrows() throws IOException {
        FS.provider().newFileSystem( toURI( FS ), getEnv() );
    }

    @Test
    public void testPathToUriAndBackIsSame() {
        Path path = getNonExistingPath();
        URI uri = path.toUri();

        assertThat( uri, notNullValue() );

        Path back = Paths.get( uri );
        assertThat( back, is( path ) );
    }

    @Test
    public void testPathWithWitespaceToUri() {
        Path path = getEmptyDir().resolve( "z z" );
        URI uri = path.toUri();

        assertThat( uri, notNullValue() );
    }

    @Test
    public void testPathWithWitespaceToUriAndBack() {
        Path path = getEmptyDir().resolve( "z z" );
        URI uri = path.toUri();

        assertThat( uri, notNullValue() );

        Path back = Paths.get( uri );
        assertThat( back, is( path ) );
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
        URI ret = fs.getPath( "" ).toAbsolutePath().getRoot().toUri();
        return ret;
    }

    public static URI toURIWithoutPath( FileSystem fs ) {
        Path root = fs.getPath( "" ).toAbsolutePath().getRoot();
        return URI.create( Strings.withoutSuffix( root.toUri().toString(), root.toString() ) );
    }

}
