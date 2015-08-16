package de.pfabulist.lindwurm.niotest;

import com.github.marschall.memoryfilesystem.MemoryFileSystemBuilder;
import de.pfabulist.lindwurm.niotest.tests.AllTests;
import de.pfabulist.lindwurm.niotest.tests.FSDescription;
import de.pfabulist.lindwurm.niotest.tests.topics.DosAttributesT;
import de.pfabulist.lindwurm.niotest.tests.topics.FileOwnerView;
import de.pfabulist.lindwurm.niotest.tests.topics.Posix;
import org.junit.BeforeClass;

import java.io.IOException;
import java.nio.file.attribute.DosFileAttributeView;
import java.nio.file.attribute.DosFileAttributes;
import java.nio.file.attribute.FileOwnerAttributeView;
import java.nio.file.attribute.PosixFileAttributeView;
import java.nio.file.attribute.PosixFileAttributes;
import java.nio.file.attribute.UserDefinedFileAttributeView;

import static de.pfabulist.lindwurm.niotest.tests.attributes.AttributeDescriptionBuilder.attributeBuilding;
import static de.pfabulist.lindwurm.niotest.tests.descriptionbuilders.CombinedBuilder.build;

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

    private static FSDescription fsDescription;

    @BeforeClass
    public static void before() throws IOException {

        fsDescription = build().
                windows().noUNC().noRootComponents().next().
                playground().set( MemoryFileSystemBuilder.
                newWindows().
                addFileAttributeView( UserDefinedFileAttributeView.class ).
                //addFileAttributeView( FileOwnerAttributeView.class ).
                        build( "marschall" ).getPath( "play" ).toAbsolutePath() ).
                time().noLastAccessTime().next().
                pathConstraints().noMaxFilenameLength().noMaxPathLength().next().
                closable().no().
                hardlinks().no().
                symlinks().noDirs().next().
                watchable().no().
                fileStores().noLimitedPlayground().next().
                attributes().add( attributeBuilding( DosAttributesT.class, "dos", DosFileAttributeView.class, DosFileAttributes.class ).
                                          addAttribute( "hidden", DosFileAttributes::isHidden ).
                                          addAttribute( "archive", DosFileAttributes::isArchive ).
                                          addAttribute( "system", DosFileAttributes::isSystem )
                                  // .addAttribute( "readonly", DosFileAttributes::isReadOnly ) not supported by Marschal
                                  ).
                            remove( "owner", FileOwnerView.class ).next().
                bugScheme( "RelSymLink", true ).
                bug( "testAppendAndReadThrows" ).
                bug( "testEveryChannelWriteUpdatesLastModifiedTime" ).
                bug( "testGetNameOfDefaultPathIsItself" ).
                bug( "testPathMatcherKnowsGlob" ).
                bug( "testPathMatherGlob" ).
                bug( "testReadCreateNonExistingFileThrows" ).
                bug( "testCopyIntoItself" ).
                bug( "testCopyNonEmptyDirDoesNotCopyKids" ).
                bug( "testUnsupportedAttributeViewReturnsNull" ).
                bug( "testIsSameFileOfDifferentPathNonExistingFileIsNot" ).
                bug( "testIsSameFileOfDifferentPathNonExistingFile2IsNot" ).
                bug( "testCopyToSymLink" ).
                bug( "testSymLinkToUnnormalizedRelPath" ).
                bug( "testIsSameFileOtherProvider2" ).
                bug( "testIsSameFileOnEqualPathElementsOtherProvider" ).
                bug( "testGetFileStoreOfNonExistent" ).
                bug( "testGetFileStoreOfBrokenSymLink" ).
                bug( "testPathToUriAndBackIsSame" ).
                bug( "testPathWithWitespaceToUriAndBack" ).
                bug( "testAppendAndTruncateExistingThrows" ).
                bug( "testCasePreserving" ). // todo report
                nitpick( "testIsSameFileOtherProvider", "strange anyway" ).
                nitpick( "testGetPathOtherURI", "different exception" ).
                nitpick( "testRegisterOnClosedWatchService", "different exception" ).
                bug( "testTransferFromSourceWithLessThanRequestedBytesGetsWhatsThere" ).
                bug( "testTransferFromPositionBeyondFileSizeDoesNothing" ).
                done();

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
        super( fsDescription );
    }

//    @Test
//    public void testUA() throws IOException {
//
//        //Path path = FileSystems.getDefault().getPath( "/Users/stephan/TODO/temp.txt" ).toAbsolutePath();
//
//        UserDefinedFileAttributeView view = Files.getFileAttributeView( fileTA(), UserDefinedFileAttributeView.class );
//
////        System.out.println(view);
////        System.out.println(view.list().size());
//
////        MemoryFileSystemBuilder.newLinux().addFileAttributeView(  )
//    }

}
