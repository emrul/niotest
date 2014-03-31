package org.opencage.lindwurm.niotest;

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

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.opencage.kleinod.paths.PathUtils;
import org.opencage.kleinod.type.ImmuDate;
import org.opencage.lindwurm.niotest.tests.PathTestIT;

import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.util.Collections;


public class MarschallTest extends PathTestIT {

    @BeforeClass
    public static void setUp() throws IOException {
        setPlay( getOrCreate( "marschall" ).getPath( "play" ).toAbsolutePath() );
        set2ndPlay(getOrCreate("marschallother").getPath("play").toAbsolutePath());


        String suf = ImmuDate.now().toStringFSFriendly();
        System.out.println(suf);
        setClosablePlay(getOrCreate("marschallClose" + suf).getPath("play").toAbsolutePath());
    }

    public MarschallTest() throws IOException {

        capabilities.
                noLinks().
                notClosable().
                doesNotSupportWatchService();

        bug( "testCreateDirectoryRoot", "bugCreateDirectoryRootThrowsWrongException" );
        bug( "testIsSameFileOnEqualPathElementsDifferentProvider", "" );
        bug( "testCopyDirCreatesADirWithTheTargetName", "" );
        bug( "testCopyNonEmptyDirDoesNotCopyKids", "" );
        bug( "testGetNameOfDefaultPathIsItself", "" );
        bug( "testMoveIntoItself", "" );
        bug( "testReadCreateNonExistingFileThrows", "" );
        bug( "testReadDirStreamSetsLastAccessTime", "" );
        bug( "testReadEmptyDirStreamSetsLastAccessTime", "" );
        bug( "testUnsupportedAttributeViewReturnsNull", "" );
        bug( "testAppendAndReadThrows", "" );
        bug( "testGetFileSystemOtherURI", "" );
        bug( "testGetPathOtherURI", "" );
//
        bug( "testGetIteratorOfClosedDirStream" );

    }

    @SuppressWarnings("unchecked")
    private static FileSystem getOrCreate( String name ) throws IOException {
        URI uri = URI.create( "memory:" + name + ":///p" );
        return PathUtils.getOrCreate( uri, Collections.EMPTY_MAP );
    }


    @Test
    public void testCloseOpen() throws IOException {
        FileSystem fs  = getOrCreate("closeopen");
        fs.close();

        FileSystems.newFileSystem( URI.create( "memory:closeopen:///p"), Collections.EMPTY_MAP );
    }

}
