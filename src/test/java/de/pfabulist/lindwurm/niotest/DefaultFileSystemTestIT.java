package de.pfabulist.lindwurm.niotest;

import de.pfabulist.kleinod.os.OS;
import de.pfabulist.kleinod.paths.PathUtils;
import org.junit.BeforeClass;

import de.pfabulist.lindwurm.niotest.tests.FSDescription;
import de.pfabulist.lindwurm.niotest.tests.PathTestIT;

import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * ** BEGIN LICENSE BLOCK *****
 * BSD License (2 clause)
 * Copyright (c) 2006 - 2014, Stephan Pfab
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *
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
public class DefaultFileSystemTestIT extends PathTestIT {

    private static Path playground;

    @BeforeClass
    public static void setUp() {
        // create filesystems just once, not for every test
        playground = PathUtils.getTmpDir("DefaultFileSystemTest");
    }

    public DefaultFileSystemTestIT() {

        FSDescription description = describe().
                playground(playground).
                fileStores(true).
                notClosable().
                lastAccessTime( false ).
                creationTime( new OS().isWindows() ).    // OSX only ?
                watcherSleepTime( 12000 ).
                noSecondPlayground().
                doesSupportsFileChannels().
                unix( new OS().isUnix()).
                windows( new OS().isWindows() ).
                bug( "testGetIteratorOfClosedDirStream" )
        ;

        if ( new OS().isWindows() ) {
            description.otherRoot(Paths.get("G:")).
                    fileSystemURI( (fs) -> URI.create(fs.provider().getScheme() + ":///")).
                    alternativeNames( "aAa", "BBB", "CCc", "DdD", "EEE", "FFF", "GGG", "HHH", "III", "JJJ", "KKK").
                    pathIllegalCharacters( '|', '?', '<', '>', ':', '*', '|').
                    bug("testMovedWatchedDirCancelsKeys").
                    bug("testEveryChannelWriteUpdatesLastModifiedTime");

        } else {
            description.
                bug( "testCreateDirectoryRoot" ).
                bug( "testWatchSeveralEvents").
                bug( "testWatchAModify").
                bug( "testWatchATruncate").
                bug( "testWatchTwoModifiesOneKey");
        }

        if ( new OS().isLinux()) {
            description.bug("testDeleteWatchedDirCancelsKeys").
                        bug("testMovedWatchedDirCancelsKeys");
        }
    }

}
