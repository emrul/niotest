package org.opencage.lindwurm.niotest;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.opencage.lindwurm.niotest.tests.PathTestIT;

import java.io.InputStream;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;

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

@Ignore
public class ZipFSTest extends PathTestIT  {

    private static Path zipLocation;
    private static URI  roURI;
    private static FileSystem ro;

    @BeforeClass
    public static void setUp() throws Exception {
        zipLocation = Files.createTempDirectory( "zipTest" ).resolve( "ro.zip" );
        try( InputStream zip = ZipMarschallTest.class.getResourceAsStream( "ro.zip" )) {
            Files.copy( zip, zipLocation );
        }

        roURI = URI.create( "jar:" + zipLocation.toUri() + "!/" );
        ro = FileSystems.newFileSystem( roURI, Collections.EMPTY_MAP );

        setPlay( ro.getPath( "play" ).toAbsolutePath() );
    }

    @AfterClass
    public static void tearDown() throws Exception {

        ro.close();
        Files.delete( zipLocation );

    }

    public ZipFSTest() {
    }
}
