package org.opencage.lindwurm.niotest;

import org.opencage.lindwurm.niotest.tests.PathTestIT;

//import com.github.sylvainjuge.memoryfs.MemoryFileSystem;
//import com.github.sylvainjuge.memoryfs.MemoryFileSystemProvider;
//import org.junit.BeforeClass;
//import org.junit.Ignore;
//import org.opencage.kleinod.paths.PathUtils;
//import org.opencage.lindwurm.niotest.tests.PathTestIT;
//
//import java.io.IOException;
//import java.net.URI;
//import java.nio.file.FileSystem;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.util.Collections;
//
///**
//* Created by stephan on 05/05/14.
//*/
//@Ignore
public class SylvainjugeMemoryFSTestIT { //extends PathTestIT {

//    @BeforeClass
//    public static void setUp() throws IOException {
//
//
//////        FileSystem mem = PathUtils.getOrCreate(URI.create("memory:/testing2"), Collections.<String, Object>emptyMap());
////        FileSystem mem = MemoryFileSystem
////                .builder( new MemoryFileSystemProvider())
//////                .id("id")
////                .build();
//
//        FileSystem mem = new MemoryFileSystemProvider().newFileSystem(URI.create("memory:/testing"), null);
//
//        Path play = mem.getPath( "/play");
//        Files.createDirectories( play );
//        setPlay( play );
//
//
////        Path path = Jimfs.newFileSystem(Configuration.unix()).getPath("/play");
////        setPlay( path );
////        setClosablePlay(Jimfs.newFileSystem(Configuration.unix()).getPath("/play"));
////
////        set2ndPlay(Jimfs.newFileSystem(Configuration.unix()).getPath("/play"));
//
//    }
//
//
//    public SylvainjugeMemoryFSTestIT() {
//
//        capabilities.
//                doesNotSupportLastAccessTime().
//                noFileChannels().
//                doesNotSupportCreationTime().
//                noAsynchronousFileChannels().
//                notClosable().
//                noLinks().
//                noSymLinks().
//                doesNotSupportWatchService();
    //}
}
