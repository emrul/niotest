package org.opencage.lindwurm.niotest.tests;

import org.hamcrest.core.Is;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;
import static org.junit.Assume.assumeThat;

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
    public void testWriteUnnormalized() throws IOException {
        getPathPAd();
        Path read = getPathPAB();
        Path write = read.getParent().resolve( ".." ).resolve( nameStr[0] ).resolve( nameStr[1] );

        Files.write( write, CONTENT, standardOpen );
        byte[] out = Files.readAllBytes( read );

        assertThat( out, Is.is( CONTENT ));
    }

    @Test
    public void testReadAttributesFromUnnormalizedPath() throws IOException {

        Path write = getPathPABf();
        Path read = write.getParent().resolve( ".." ).resolve( nameStr[0] ).resolve( nameStr[1] );

        assertThat( Files.size( read ), is( 0L+CONTENT.length));
    }





}
