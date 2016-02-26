package de.pfabulist.lindwurm.niotest;

import com.github.marschall.memoryfilesystem.MemoryFileSystemBuilder;
import de.pfabulist.lindwurm.niotest.tests.AllTests;
import de.pfabulist.lindwurm.niotest.tests.FSDescription;
import de.pfabulist.lindwurm.niotest.tests.topics.FileOwnerView;
import org.junit.BeforeClass;

import java.io.IOException;
import java.nio.file.attribute.FileOwnerAttributeView;
import java.nio.file.attribute.UserDefinedFileAttributeView;

import static de.pfabulist.lindwurm.niotest.tests.descriptionbuilders.CombinedBuilder.build;

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

public class MarschallUnixTest extends AllTests {

    private static FSDescription fsDescription;

    @BeforeClass
    public static void before() throws IOException {

        fsDescription = build().
                playgrounds().
                    std( MemoryFileSystemBuilder.
                            newLinux().
                            addFileAttributeView( UserDefinedFileAttributeView.class ).
                                         build( "marschallu" ).
                                    getPath( "play" ).
                                    toAbsolutePath() ).
                    noSizeLimit().
                    sameProviderDifferentFileSystem(
                            MemoryFileSystemBuilder.
                            newLinux().
                            addFileAttributeView( UserDefinedFileAttributeView.class ).
                                                                     build( "marschall2" ).
                                    getPath( "play" ).
                                    toAbsolutePath() ).
                    next().
                unix().next().
                time().noLastAccessTime().next().
                pathConstraints().
                    noMaxFilenameLength().
                    noMaxPathLength(). // timeout
                    next().
                closable().no().
                hardlinks().no().
                symlinks().noDirs().next().
                watchable().no().
                attributes().remove( "owner", FileOwnerView.class ).next().
                bugScheme( "RelSymLink" ).
                bug( "testAppendAndReadThrows" ).
                bug( "testAppendAndTruncateExistingThrows" ).
                bug( "testGetNameOfDefaultPathIsItself" ).
                bug( "testCopyIntoItself" ).
                bug( "testCopyNonEmptyDirDoesNotCopyKids" ).
                bug( "testIsSameFileOfDifferentPathNonExistingFileIsNot" ).
                bug( "testSymLinkToUnnormalizedRelPath" ).
                bug( "testGetFileStoreOfNonExistent" ).
                bug( "testGetFileStoreOfBrokenSymLink" ).
                bug( "testTransferFromSourceWithLessThanRequestedBytesGetsWhatsThere" ).
                bug( "testTransferFromPositionBeyondFileSizeDoesNothing" ).
                bug( "testDeleteWhileReading" ).

                bug( "testDeleteWhileWriting" ).
                bug( "testDifferentOwnerCanNotWrite" ).
                bug( "testDotFilesAreHidden" ).
                bug( "testFilesHaveOwners" ).
                bug( "testOwnerByTwoMethods" ).
                bug( "testReadChannelOfDirDoesNotThrow" ).
                bug( "testEveryChannelWriteUpdatesLastModifiedTime" ).

                bug( "testCopyToDifferentFS" ).
                bug( "testMoveToDifferentFS" ).

                done();

    }

    public MarschallUnixTest() {
        super( fsDescription );
    }


}
