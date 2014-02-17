package org.opencage.lindwurm.niotest.tests;

import org.hamcrest.core.Is;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.*;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;
import static org.junit.Assume.assumeThat;
import static org.opencage.lindwurm.niotest.matcher.PathExists.exists;

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
public abstract class PathTest10PathWithContentIT extends PathTest9WrongProviderIT {

    @Test
    public void testIsSameFileOnEqualPath() throws IOException {
        assertThat( Files.isSameFile( getPathPA(), getPathPA()), is( true ) );
    }

    @Test
    public void testIsSameFileOnEqualPathElementsDifferentProvider() throws IOException {
        assumeThat( FS, not( Is.is( FileSystems.getDefault() )));

        assertThat( Files.isSameFile( getPathA(), FileSystems.getDefault().getPath( nameStr[0] )), is( false ) );
    }


    @Test
    public void testIsSameFileWithUnnormalizedPath() throws IOException {
        assertThat(FS.provider().isSameFile( getPathPABf(), getPathPABu()), is(true));
    }

    @Test
    public void testIsSameFileWithRelativePath() throws IOException {
        assertThat(FS.provider().isSameFile(getPathPABf(), getPathPABr()), is(true));
    }


    @Test
    public void testWriteUnnormalized() throws IOException {
        getPathPAd();
        Path read = getPathPAB();
        Path write = getPathPABu();

        Files.write( write, CONTENT, standardOpen );
        byte[] out = Files.readAllBytes( read );

        assertThat( out, Is.is( CONTENT ));
    }

    @Test
    public void testReadAttributesFromUnnormalizedPath() throws IOException {
        getPathPABf();
        Path read = getPathPABu();

        assertThat( Files.size( read ), is( 0L+CONTENT.length));
    }

    @Test
    public void testCheckAccessUnnormalizedPath() throws IOException {
        getPathPABf();
        Path read = getPathPABu();

        // expect no throw
        FS.provider().checkAccess(read);
    }

    @Test
    public void testCheckAccessRelativePath() throws IOException {
        getPathPABf();
        Path read = getPathPABr();

        // expect no throw
        FS.provider().checkAccess( read );
    }


    @Test
    public void testCheckAccessSupportesRead() throws IOException {
        // should not throw UnsupportedOperationException
        try {
            FS.provider().checkAccess( getPathPABf(), AccessMode.READ );
        } catch ( AccessDeniedException e ) {
            //
        }
    }

    @Test
    public void testCheckAccessSupportesWrite() throws IOException {
        // should not throw UnsupportedOperationException
        try {
            FS.provider().checkAccess( getPathPABf(), AccessMode.WRITE );
        } catch ( AccessDeniedException e ) {
            //
        }
    }

    @Test
    public void testCheckAccessSupportesExecute() throws IOException {
        // should not throw UnsupportedOperationException
        try {
            FS.provider().checkAccess( getPathPABf(), AccessMode.EXECUTE );
        } catch ( AccessDeniedException e ) {
            //
        }
    }

    @Test( expected = NoSuchFileException.class )
    public void testCheckAccessNonExistingFile() throws IOException {
        FS.provider().checkAccess( getPathPAB() );
    }

    @Test
    public void testCopyUnnormalizedPath() throws IOException {
        getPathPABf();
        FS.provider().copy( getPathPABu(), getPathPB());

        assertThat( Files.readAllBytes(getPathPAB()), is(CONTENT));
    }

    @Test
    public void testMoveUnnormalizedPath() throws IOException {
        getPathPABf();
        System.out.println(getPathPABu());
        FS.provider().move(getPathPABu(), getPathPB());

        assertThat( Files.readAllBytes(getPathPB()), is(CONTENT));
    }

    @Test
    public void testCreateDirectoryUnnormalizedPath() throws IOException {
        assumeThat( message(), possible(), is(true) );

        FS.provider().createDirectory( getPathPAu());
        assertThat(getPathPA(), exists());
    }

    @Test( expected = NoSuchFileException.class )
    public void bugCreateDirectoryUnnormalizedPath() throws IOException {
        assumeThat( message(), possible(), is(false) );

        FS.provider().createDirectory( getPathPAu());
    }

    @Test
    public void testDeleteUnnormalizedPath() throws IOException {
        Path file = getPathPABf();
        FS.provider().delete( getPathPABu());

        assertThat(file, not(exists()));
    }

    @Test
    public void testDeleteIfExistsUnnormalizedPath() throws IOException {
        Path file = getPathPABf();
        FS.provider().deleteIfExists( getPathPABu());

        assertThat(file, not(exists()));
    }

    @Test
    public void testD2eleteIfExistsUnnormalizedPath() throws IOException {
        //FS.provider().getFileStore(getPathPA());
        Path file = getPathPABf();
        FS.provider().getFileStore( getPathPABu());
    }


}
