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
import org.opencage.kleinod.paths.PathUtils;
import org.opencage.lindwurm.niotest.tests.PathTestIT;

import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.util.Collections;


public class MarschallTest extends PathTestIT {

    @BeforeClass
    public static void setUp() throws IOException {
        setPlay( getOrCreate( "marschall" ).getPath( "play" ).toAbsolutePath() );
        setClosablePlay( getOrCreate( "marschallClose" ).getPath( "play" ).toAbsolutePath() );
        set2ndPlay(getOrCreate( "marschall22" ).getPath( "play" ).toAbsolutePath() );
    }

    public MarschallTest() throws IOException {

        capabilities.doesNotSupportWatchService();

        bug( "testCreateDirectoryRoot", "bugCreateDirectoryRootThrowsWrongException" );

        bug( "testCheckAccessOtherProvider", "bugCheckAccessOtherProviderThrowsWrongException" );
        bug( "testCopyOtherProviderFrom", "" );
        bug( "testCopyOtherProviderTo", "" );
        bug( "testCreateDirectoryOtherProvider", "" );
        bug( "testCreateLinkOtherProvider", "" );
        bug( "testCreateSymbolicLinkOtherProvider", "" );
        bug( "testDeleteIfExistsOtherProvider", "" );
        bug( "testDeleteOtherProvider", "" );
        bug( "testGetFileStoreOtherProvider", "" );
        bug( "testIsHiddenOtherProvider", "" );
        bug( "testIsSameFileOnEqualPathElementsDifferentProvider", "" );
        bug( "testMoveOtherProviderFrom", "" );
        bug( "testMoveOtherProviderTo", "" );
        bug( "testNewAsynchronousFileChannelOtherProvider", "" );
        bug( "testNewByteChannelOtherProvider", "" );
        bug( "testNewDirectoryStreamOtherProvider", "" );
        bug( "testNewFileChannelOtherProvider", "" );
        bug( "testNewOutputStreamOtherProvider", "" );
        bug( "testReadAttributesOtherProvider", "" );
        bug( "testReadAttributesStringOtherProvider", "" );
        bug( "testReadSymbolicLinkOtherProvider", "" );
        bug( "testGetBasicFileAttributeViewProvider", "" );
//
//
        bug( "testMoveRoot", "bugMoveRootThrowsClassCastException" );
        bug( "testClosedFSisClosed", "bugClosedFSisClosed" );
        bug( "testCopyDirCreatesADirWithTheTargetName", "" );
        bug( "testCopyNonEmptyDirDoesNotCopyKids", "" );
//        bug( "testFileAttributesAreImmutable", "" ); //fix in 5.0
        bug( "testGetNameOfDefaultPathIsItself", "" );
        bug( "testMoveIntoItself", "" );
//        bug( "testNewDirectoryStreamUnnormalizedPath", "" ); // fixed in 5.0
        bug( "testReadCreateNonExistingFileThrows", "" );
        bug( "testReadDirStreamSetsLastAccessTime", "" );
        bug( "testReadEmptyDirStreamSetsLastAccessTime", "" );
        bug( "testTruncateToNegativeSizeThrows", "" );
        bug( "testUnsupportedAttributeViewReturnsNull", "" );
        bug( "testAppendAndReadThrows", "" );
        bug( "testGetFileSystemOtherURI", "" );
        bug( "testGetPathOtherURI", "" );



    }

    @SuppressWarnings("unchecked")
    private static FileSystem getOrCreate( String name ) throws IOException {
        URI uri = URI.create( "memory:" + name + ":///p" );
        return PathUtils.getOrCreate( uri, Collections.EMPTY_MAP );
    }

}
