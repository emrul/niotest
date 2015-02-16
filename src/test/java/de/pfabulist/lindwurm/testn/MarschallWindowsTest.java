package de.pfabulist.lindwurm.testn;

import com.github.marschall.memoryfilesystem.MemoryFileSystemBuilder;
import de.pfabulist.lindwurm.niotest.testsn.AllTests;
import de.pfabulist.lindwurm.niotest.testsn.setup.Capa;
import org.junit.BeforeClass;

import java.io.IOException;

import static de.pfabulist.lindwurm.niotest.testsn.setup.CapBuilder00.FSType.NTFS;
import static de.pfabulist.lindwurm.niotest.testsn.setup.CapBuilder00.typ;

/**
* ** BEGIN LICENSE BLOCK *****
* BSD License (2 clause)
* Copyright (c) 2006 - 2014, Stephan Pfab
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
public class MarschallWindowsTest extends AllTests {
   
    private static Capa capa;


    @BeforeClass
    public static void before() throws IOException {

        capa = typ( NTFS ).rootComponent( false ).unc( false ).yes().
                playground( MemoryFileSystemBuilder.newWindows().build( "marschall" ).getPath( "play" ).toAbsolutePath() ).
                time().lastAccessTime( false ).yes().
                pathConstraints().no().
                closeable().no().
                hardlinks().no().
                symlinks().toDirs( false ).yes().
                watchService().no().
                bugScheme( "RelSymLink" ).
                bug( "testAppendAndReadThrows" ).
                bug("testEveryChannelWriteUpdatesLastModifiedTime").
                bug("testGetNameOfDefaultPathIsItself").
                bug("testPathMatcherKnowsGlob").
                bug("testPathMatherGlob").
                bug("testReadCreateNonExistingFileThrows").
                bug("testCopyIntoItself").
                bug("testCopyNonEmptyDirDoesNotCopyKids").
                bug("testUnsupportedAttributeViewReturnsNull").
                bug("testIsSameFileOfDifferentPathNonExistingFileIsNot").
                bug( "testIsSameFileOfDifferentPathNonExistingFile2IsNot").
                bug( "testCopyToSymLink").
                bug( "testSymLinkToUnnormalizedRelPath" ).
                bug("testIsSameFileOtherProvider2").
                bug( "testIsSameFileOnEqualPathElementsOtherProvider" ).
                bug( "testGetFileStoreOfNonExistent" ).
                bug( "testGetFileStoreOfBrokenSymLink" ).
                nitpick( "testIsSameFileOtherProvider", "strange anyway").
                nitpick("testGetPathOtherURI", "different exception").
                nitpick("testRegisterOnClosedWatchService", "different exception").
                build();
        
//                new FSDescription().
//                windows().UNCPaths( false ).yes().
//                closable().no().
//                hardLinks().no().
//                watchService().no().
//                symLinks().toDirs( false ).toOtherProviders(false).yes().
//                pathConstraints().no().
//                bug( "testCopyIntoItself").
//                bug( "testCopyNonEmptyDirDoesNotCopyKids" ).
//                bug( "testAppendAndReadThrows" ).
//                bug( "testCopyToSymLink").
//                bug("testGetNameOfDefaultPathIsItself")
////                unix(true).
//                watchService().delay(5500).yes().
//                symLinks().toOtherProviders(false).yes().
//                time().lastAccessTime(false).yes().
//                bugScheme("Unnormalized").
//                bugScheme("TooLong").
//                nitpick("testAppendAndReadThrows").
//                nitpick("testSetUnsupportedAttributeThrows" ).
//                nitpick("testReadUnsupportedAttributesThrows" ).
//                nitpick( "testGetUnsupportedAttributeThrows").
//                bug("testClosedFSGetFileStore")

    }


    public MarschallWindowsTest() {
        super(capa);
    }
}
