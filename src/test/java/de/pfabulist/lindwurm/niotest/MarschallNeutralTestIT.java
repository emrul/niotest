//package de.pfabulist.lindwurm.niotest;
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
//
//import de.pfabulist.kleinod.paths.Pathss;
//import de.pfabulist.lindwurm.niotest.tests.PathTestIT;
//import org.junit.BeforeClass;
//import org.junit.Test;
//
//import java.io.IOException;
//import java.net.URI;
//import java.nio.file.FileSystem;
//import java.nio.file.FileSystems;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.util.Collections;
//
//import static de.pfabulist.lindwurm.niotest.tests.SymLinkDescriptionBuilder.SymLinkOption.DELAYED_LOOP_CHEFCKING;
//import static de.pfabulist.lindwurm.niotest.tests.SymLinkDescriptionBuilder.SymLinkOption.TO_DIRECTORIES;
//import static de.pfabulist.lindwurm.niotest.tests.SymLinkDescriptionBuilder.SymLinkOption.TO_OTHER_FILESYSTEM;
//
//
//public class MarschallNeutralTestIT extends PathTestIT {
//
//    private static Path playground;
//    private static Path playground2;
//
//    @BeforeClass
//    public static void setUp() throws IOException {
//        playground = getOrCreate("mm").getPath("play").toAbsolutePath();
//        playground2 = getOrCreate("mm2").getPath("play").toAbsolutePath();
//    }
//
//    public MarschallNeutralTestIT() throws IOException {
//
//        describe().
//                playground(playground).
////                windows( true ).
//                fileStores(true).
//                symLinks().with( DELAYED_LOOP_CHEFCKING ).withOut( TO_DIRECTORIES, TO_OTHER_FILESYSTEM ).yes().
//                notClosable().
//                watchService().no().
//                secondPlayground( playground2 ).
//                setMaxFilenameLength(-1).
//
//        bug( "testCreateDirectoryRoot" ).
//        bug( "testIsSameFileOnEqualPathElementsDifferentProvider" ).
//        bug( "testCopyDirCreatesADirWithTheTargetName" ).
//        bug( "testCopyNonEmptyDirDoesNotCopyKids" ).
//        bug( "testGetNameOfDefaultPathIsItself" ).
//        bug( "testMoveIntoItself" ).
//        bug( "testReadCreateNonExistingFileThrows" ).
//        bug( "testReadDirStreamSetsLastAccessTime" ).
//        bug( "testReadEmptyDirStreamSetsLastAccessTime" ).
//        bug("testUnsupportedAttributeViewReturnsNull").
//        bug("testAppendAndReadThrows").
//        bug("testGetFileSystemOtherURI").
//        bug("testGetPathOtherURI").
//        bug("testGetIteratorOfClosedDirStream").
//        bug("testEveryChannelReadUpdatesLastAccessTime").
//        bug("testEveryChannelWriteUpdatesLastModifiedTime");
//
//    }
//
////    @Test
////    public void testBBB() throws IOException {
//////        FileSystem fs = getOrCreate( "marschall" );
////
////        FileSystem fs = MemoryFileSystemBuilder.newWindows().
////        /*newLinux().*/build("MoreFilesZipTest");
////        Path path = fs.getPath("existing.zip");
////        Files.createFile(path);
////        final FileTime time = FileTime.fromMillis(System.currentTimeMillis());
////        Files.setAttribute(path, "basic:lastModifiedTime", time);
////        path.getFileSystem().provider().setAttribute(path, "basic:lastModifiedTime", time);
////    }
////
////    @Test
////    public void testSymLinkToParent() throws IOException {
////
////        Path parent = FS.getPath( "linkParent").toAbsolutePath();
////        Files.createDirectories( parent );
////
////        Files.createSymbolicLink( parent.resolve("link"), parent );
////    }
//
//    @Test
//    public void testSymLinkToParent() throws IOException {
//
//        Path parent = FS.getPath( "linkParent").toAbsolutePath();
//        Files.createDirectories( parent );
//
//        Path target = FS.getPath( "target").toAbsolutePath();
//        Files.createDirectories( target );
//
//        Files.createSymbolicLink( parent.resolve("link"), target );
//
//        Files.write( parent.resolve("link").resolve("kid"), "hallo".getBytes("UTF-8"));
//    }
//
//    @SuppressWarnings("unchecked")
//    private static FileSystem getOrCreate( String name ) throws IOException {
//        URI uri = URI.create( "memory:" + name + ":///p" );
//        return Pathss.getOrCreate(uri, Collections.EMPTY_MAP);
//    }
//
//
//    @Test
//    public void testCloseOpen() throws IOException {
//        FileSystem fs  = getOrCreate("closeopen");
//        fs.close();
//
//        FileSystems.newFileSystem( URI.create( "memory:closeopen:///p"), Collections.EMPTY_MAP );
//    }
//
//}
