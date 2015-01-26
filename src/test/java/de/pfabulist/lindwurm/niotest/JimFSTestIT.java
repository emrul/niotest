//package de.pfabulist.lindwurm.niotest;
//
//import com.google.common.jimfs.Configuration;
//import com.google.common.jimfs.Jimfs;
//import de.pfabulist.kleinod.text.Strings;
//import org.junit.BeforeClass;
//import org.junit.Test;
//import de.pfabulist.lindwurm.niotest.tests.ClosedFSVars;
//import de.pfabulist.lindwurm.niotest.tests.FSDescription;
//import de.pfabulist.lindwurm.niotest.tests.PathTestIT;
//
//import java.io.IOException;
//import java.net.URI;
//import java.nio.file.FileSystemAlreadyExistsException;
//import java.nio.file.Path;
//import java.util.HashMap;
//import java.util.Map;
//
///**
// * ** BEGIN LICENSE BLOCK *****
// * BSD License (2 clause)
// * Copyright (c) 2006 - 2014, Stephan Pfab
// * All rights reserved.
// *
// * Redistribution and use in source and binary forms, with or without
// * modification, are permitted provided that the following conditions are met:
// * * Redistributions of source code must retain the above copyright
// * notice, this list of conditions and the following disclaimer.
// * * Redistributions in binary form must reproduce the above copyright
// * notice, this list of conditions and the following disclaimer in the
// * documentation and/or other materials provided with the distribution.
// *
// * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
// * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
// * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
// * DISCLAIMED. IN NO EVENT SHALL Stephan Pfab BE LIABLE FOR ANY
// * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
// * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
// * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
// * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
// * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
// * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
// * **** END LICENSE BLOCK ****
// */
//public class JimFSTestIT extends PathTestIT {
//    private static Path playground;
//    private static Path secondPlay;
//    private static ClosedFSVars closablePlayground;
//    private static Path sizeLimitedPlay;
//
//    @BeforeClass
//    public static void setUp() throws IOException {
//        playground  = Jimfs.newFileSystem(Configuration.unix().toBuilder().setAttributeViews("basic", "owner", "posix", "unix").build()).getPath("/play");
//        closablePlayground = new ClosedFSVars( Jimfs.newFileSystem(Configuration.unix()).getPath("/play"));
//        secondPlay  = Jimfs.newFileSystem(Configuration.unix()).getPath("/play");
//        sizeLimitedPlay  = Jimfs.newFileSystem(Configuration.unix().toBuilder().setMaxSize( 12000L).build()).getPath("/play");
//    }
//
//    public JimFSTestIT() {
//
//        describe().
//                playground(playground).
//                //watchService(). //watcherSleepTime(10000).
//                lastAccessTime(false).
//                fileStores(true).
//                secondPlayground(secondPlay).
//                closablePlayground(closablePlayground).
//                fileSystemURI(FSDescription::toURIWithoutPath).
//                sizeLimitedPlayground(sizeLimitedPlay).
//                setMaxFilenameLength(-1).
//                doesNotSupporForeignSymLinks().
//
//                bug("testWatchTwoModifiesOneKey").
//
//                bug( "testSymLinkToParent" ).
//                bug("testSymLinkComplexLoop").
//                bug( "testHardLinkToSymLinkIsSymLink" ).
//                bug( "testHardLinkToSymLinkDeleteSym" ).
//                bug( "testModifyHardLinkToSymLink" ).
//
//                bug("testCopyToClosedFS").
//                bug("testClosedFSGetFileStore").
//                bug("testAppendAndReadThrows").
//                bug("testCloseDirStreamInTheMiddleOfIteration").
//                bug("testGetIteratorOfClosedDirStream").
//                bug("testIsSameFileOfDifferentPathNonExistingFile2Throws").
//                bug("testIsSameFileOfDifferentPathNonExistingFileThrows").
//                bug("testReadUnsupportedAttributesThrows").
//                bug("testSetUnsupportedAttributeThrows").
//                bug("testGetUnsupportedAttributeThrows").
//                bug("testRegisterOtherPath").
//                bug("testGetFileSystemOtherURI").
//                bug("testNewFileSystemOfExistingThrows"); // needs env see below
//    }
//
//    @Test( expected = FileSystemAlreadyExistsException.class )
//    public void testJimFSNewFileSystemOfExistingThrows() throws IOException {
//        URI uriThisFS = URI.create( Strings.withoutSuffix(getRoot().toUri().toString(), getRoot().toString()));
//        Map<String, Object > env = new HashMap<>();
//        env.put("config", Configuration.unix());
//        FS.provider().newFileSystem(  uriThisFS, env );
//    }
//
//}
