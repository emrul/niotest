package org.opencage.lindwurm.niotest.tests;

import org.junit.Test;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.ClosedFileSystemException;
import java.nio.file.Files;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
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
public abstract class PathTest7ClosedIT extends PathTest6AttributesIT {

    // closable FS

    @Test
    public void testAAA7PP() throws IOException {
        assumeThat( capabilities.isClosable(), is(true) );
        assertThat( getClosedFS(), notNullValue() );
    }


    @Test
    public void testClosedFSisClosed() throws Exception {
        assumeThat( capabilities.isClosable(), is(true) );
        assumeThat( message(), possible(), is(true) );

        assertThat( getClosedFS().isOpen(), is(false) );
    }

    @Test
    public void testStdFSisOpen() throws Exception {
        assumeThat( message(), possible(), is(true) );

        assertThat( FS.isOpen(), is(true) );
    }

    @Test( expected = ClosedFileSystemException.class )
    public void testClosedFSCantRead() throws Exception {
        assumeThat( capabilities.isClosable(), is(true) );
        assumeThat( message(), possible(), is(true) );

        Files.readAllBytes( getClosedAf() );
    }

    @Test( expected = ClosedFileSystemException.class )
    public void testClosedFSCantReadDir() throws Exception {
        assumeThat( capabilities.isClosable(), is(true) );
        assumeThat( message(), possible(), is(true) );

        Files.newDirectoryStream( getClosedBd() );
    }

    @Test( expected = ClosedFileSystemException.class )
    public void testClosedFSCantUseReadChannelPosition() throws Exception {
        assumeThat( capabilities.isClosable(), is(true) );
        assumeThat( message(), possible(), is(true) );

        getClosedReadChannel().position();
    }

    @Test( expected = ClosedFileSystemException.class )
    public void testClosedFSCantUseReadChannelRead() throws Exception {
        assumeThat( capabilities.isClosable(), is(true) );
        assumeThat( message(), possible(), is(true) );

        getClosedReadChannel().read( ByteBuffer.allocate(2) );
    }

    @Test( expected = ClosedFileSystemException.class )
    public void testClosedFSCantUseReadChannelSize() throws Exception {
        assumeThat( capabilities.isClosable(), is(true) );
        assumeThat( message(), possible(), is(true) );

        getClosedReadChannel().size();
    }
//
//
//    @Test( expected = ClosedFileSystemException.class )
//    public void testOpenDirectoryStreamFromClosedFSThrows() throws Exception {
//        assumeThat( capabilities.isClosable(), is(true) );
//
//        Path dir = getDefaultPath();
//        FS.close();
//        try ( DirectoryStream<Path> ch = Files.newDirectoryStream( dir )) {
//        }
//    }
//    @Test( expected = ClosedFileSystemException.class )

//    public void testReadFromClosedFSThrows() throws Exception {
//        assumeThat( capabilities.isClosable(), is(true) );
//
//        Path file = getPathPAf();
//        FS.close();
//        Files.readAllBytes( file );
//    }
//

}
