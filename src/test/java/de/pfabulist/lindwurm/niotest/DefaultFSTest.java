package de.pfabulist.lindwurm.niotest;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import de.pfabulist.kleinod.nio.Pathss;
import de.pfabulist.kleinod.os.OS;
import de.pfabulist.lindwurm.niotest.tests.AllTests;
import de.pfabulist.lindwurm.niotest.tests.FSDescription;
import de.pfabulist.lindwurm.niotest.tests.Tests05URI;
import org.junit.BeforeClass;

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

public class DefaultFSTest extends AllTests {

    private static FSDescription descr;

    public DefaultFSTest() {
        super( descr );
    }

    @BeforeClass
    public static void before() {

        OS os = new OS();

        if( os.isOSX() ) {
            descr = build().
                    playgrounds().
                        std( Pathss.getTmpDir( "DefaultFileSystemTest" )).
                        noSizeLimit().
                        noClosable().
                        differentProvider( Jimfs.newFileSystem( Configuration.unix().toBuilder().setAttributeViews( "basic", "owner", "posix", "unix" ).build() ).getPath( "/other" ) ).
                        noSameProviderDifferentFileSystem().
                        noSameFileSystemDifferentStore().
                        next().
                    unix().hfsPlus().noPermissionChecks().next().
//                    playground().set( Pathss.getTmpDir( "DefaultFileSystemTest" ) ).
                    fileStores().noLimitedPlayground().notExclusive().next().
                    //closable().no().
                    watchable().delay( 12000 ).
                    time().noLastAccessTime().noCreationTime().attributeDelay( 2000 ).next().
                    bug( "testEveryChannelWriteUpdatesLastModifiedTime", os.isWindows() ).
                    bug( "testIsSameFileOfDifferentPathNonExistingFileIsNot" ).
                    bug( "testIsSameFileOfDifferentPathNonExistingFile2IsNot" ).
                    bug( "testWatchTwoModifiesOneKey" ).
                    bug( "testWatchSeveralEventsInOneDir" ).
                    bug( "testWatchAModify" ).
                    bug( "testWatchATruncate" ).
                    bug( "testDeleteWatchedDirCancelsKeys" ).
                    bug( "testIsSameFileWithSpecialUnnormalizedPath" ).
                    bug( "testCopySymLinkToItself" ).
                    bug( "testCopyBrokenSymLinkToItself" ).
                    bug( "testSymLinkToUnnormalizedRelPath" ).
                    bug( "testMovedWatchedDirCancelsKeys" ).
                    bug( "testTruncateOnAppendChannelThrows" ).
                    nitpick( "testIsSameFileOtherProvider", "strange anyway" ).

//                    fastOnly().
                    done();

        } else if( os.isUnix() ) {
            descr = build().
                    unix().noPermissionChecks().next().
                    playground().set( Pathss.getTmpDir( "DefaultFileSystemTest" ) ).
                    fileStores().noLimitedPlayground().notExclusive().next().
                    watchable().delay( 12000 ).
                    closable().no().
                    otherProviderplayground().set( Jimfs.newFileSystem( Configuration.unix().toBuilder().setAttributeViews( "basic", "owner", "posix", "unix" ).build() ).getPath( "/other" ) ).
                    time().noLastAccessTime().noCreationTime().attributeDelay( 2000 ).next().
                    bug( "testIsSameFileOfDifferentPathNonExistingFileIsNot" ).
                    bug( "testWatchSeveralEventsInOneDir" ).
                    bug( "testCopySymLinkToItself" ).
                    bug( "testCopyBrokenSymLinkToItself" ).
                    bug( "testSymLinkToUnnormalizedRelPath" ).
                    bug( "testMovedWatchedDirCancelsKeys" ).
                    bug( "testTruncateOnAppendChannelThrows" ).
                    nitpick( "testIsSameFileOtherProvider", "strange anyway" ).
                    done();
        } else if( os.isWindows() ) {
            descr = build().
                    windows().next().
                    playground().set( Pathss.getTmpDir( "DefaultFileSystemTest" ) ).
                    watchable().delay( 12000 ).
                    time().noLastAccessTime().attributeDelay( 2000 ).next().
                    pathConstraints().
                        pathLength( 32000).
                        //filenameLength( 100 ).

                        //noMaxFilenameLength().
                        //noMaxPathLength().
                        next().
                    closable().no().
                    hardlinks().no().
                    fileStores().noLimitedPlayground().notExclusive().next().
                    symlinks().no(). // privilege problem
                    otherProviderplayground().set( Jimfs.newFileSystem( Configuration.unix().toBuilder().setAttributeViews( "basic", "owner", "posix", "unix" ).build() ).getPath( "/other" ) ).
                    fsCreation().
                    uri( Tests05URI::toURIWithoutPath ).next().

                    bug( "testIsSameFileOfDifferentPathNonExistingFileIsNot" ).
                    bug( "testEveryChannelWriteUpdatesLastModifiedTime" ).
                    bug( "testMovedWatchedDirCancelsKeys" ).
                    bug( "testTruncateOnAppendChannelThrows" ).
                    nitpick( "testIsSameFileOtherProvider", "strange anyway" ).
                    done();

        } else {
            throw new UnsupportedOperationException( "os not supported: " + os );
        }

    }

}
