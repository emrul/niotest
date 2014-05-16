//package org.opencage.lindwurm.niotest;
//
//import org.junit.BeforeClass;
//import org.junit.Ignore;
//import org.opencage.kleinod.paths.PathUtils;
//import org.opencage.lindwurm.niotest.tests.PathTestIT;
//
//import java.io.IOException;
//import java.net.URI;
//import java.nio.file.Path;
//import java.util.Collections;
//
///**
// * Created with IntelliJ IDEA.
// * User: stephan
// * Date: 21/02/14
// * Time: 20:14
// * To change this template use File | Settings | File Templates.
// */
//// null is a simplistic FS, readonly, PathTestIT is not made for these FS
//@Ignore
//public class NullFSTestIT extends PathTestIT {
//
//    @BeforeClass
//    public static void setUp() throws IOException {
//        setPlay(PathUtils.getOrCreate(URI.create("null:/"), Collections.EMPTY_MAP).getPath(""));
//    }
//
//}
