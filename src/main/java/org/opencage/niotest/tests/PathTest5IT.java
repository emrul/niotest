package org.opencage.niotest.tests;

import org.hamcrest.CoreMatchers;
import org.junit.Test;

import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemAlreadyExistsException;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assume.assumeTrue;

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
public abstract class PathTest5IT extends PathTest4IT {

    @Test
    public void testFileSystemOfAPathIsTheConstructingOne() {
        assertEquals( p.FS, p.FS.getPath( "" ).getFileSystem() );
    }

    @Test
    public void testSeparatorIsNotEmpty() {
        assertThat( p.FS.getSeparator().isEmpty(), is( false ));
    }

    @Test
    public void testSchemeIsNotEmpty() {
        assertThat( p.FS.provider().getScheme().isEmpty(), is(false));
    }

    @Test( expected = IllegalArgumentException.class )
    public void testProviderGetFileSytemWithWrongSchemeFails() {
        p.FS.provider().getFileSystem(
                URI.create( p.FS.provider().getScheme() + "N:" ));
    }


    @Test( expected = IllegalArgumentException.class )
    public void testWrongUriAtProviderNewFails() throws IOException {
        p.FS.provider().newFileSystem(
                URI.create( p.FS.provider().getScheme()+ "N:" ),
                Collections.EMPTY_MAP );
    }

    @Test
    public void testToUriOfRelativePathIsTheUriOfTheAbsolute() throws Exception {
        assertEquals( getDefaultPath().toAbsolutePath().toUri(),getDefaultPath().toUri());
    }


//    @Test( expected = FileSystemNotFoundException.class )
//    public void testGetUnknownFileSystem() {
//        assumeTrue( !p.getCapabilities().oneFileSystemOnly() );
//
//        p.FS.provider().getFileSystem( p.getUriForUnknownFS() );
//    }
//
//    @Test
//    public void testNewFileSystem() throws IOException {
//        assumeTrue( !p.getCapabilities().oneFileSystemOnly() );
//
//        FileSystem fs  = p.FS.provider().newFileSystem( p.getUriForNewFS(), p.getEnvForNewFS() );
//
//        assertThat( fs, CoreMatchers.notNullValue() );
//    }
//
//    @Test( expected = FileSystemAlreadyExistsException.class )
//    public void testNewFileSystemAgain() throws IOException {
//        assumeTrue( !p.getCapabilities().oneFileSystemOnly() );
//        p.FS.provider().newFileSystem( p.getUriForNewFS(), p.getEnvForNewFS() );
//        p.FS.provider().newFileSystem( p.getUriForNewFS(), p.getEnvForNewFS() );
//    }
//
//    @Test
//    public void testGetExistingFileSystem() throws IOException {
//        if ( p.getUriForExistingFS() != null ) {
//            FileSystem fs  = p.FS.provider().getFileSystem( p.getUriForExistingFS() );
//
//            assertThat( fs, notNullValue() );
//        }
//    }
//
//    @Test
//    public void testPathToUri() {
//        Path path = p.FS.getPath( p.getLegalPathElement() ).toAbsolutePath();
//
//        URI uri = path.toUri();
//
//        assertThat( uri, notNullValue() );
//
//        Path back = p.FS.provider().getPath( uri );
//
//        assertEquals( path, back );
//    }
//
//    @Test
//    public void testUriToPath() throws Exception {
//        Path path = p.FS.getPath( p.getLegalPathElement() ).toAbsolutePath();
//        URI uri = path.toUri();
//
//        Path p2 = Paths.get( uri );
//
//        assertThat( p2, is( path ));
//    }


}
