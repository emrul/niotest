package org.opencage.lindwurm.niotest;

import com.google.jimfs.Configuration;
import com.google.jimfs.Jimfs;
import org.junit.BeforeClass;
import org.opencage.lindwurm.niotest.tests.PathTestIT;

import java.io.IOException;
import java.nio.file.Path;

/**
* Created with IntelliJ IDEA.
* User: stephan
* Date: 28/02/14
* Time: 22:06
* To change this template use File | Settings | File Templates.
*/
public class JimFSTest extends PathTestIT {
    private static Path playground;
    private static Path secondPlay;
    private static Path closablePlayground;

    @BeforeClass
    public static void setUp() throws IOException {
        playground  = Jimfs.newFileSystem(Configuration.unix()).getPath("/play");
//        closablePlayground = Jimfs.newFileSystem(Configuration.unix()).getPath("/play");
        secondPlay  = Jimfs.newFileSystem(Configuration.unix()).getPath("/play");

    }

    public JimFSTest() {

        describe().
                playground(playground).
                watcherSleepTime(7000).
                lastAccessTime(false).
                fileStores(true).
                secondPlayground(secondPlay).
                notClosable(). //closablePlayground( closablePlayground ).

                bug("testAppendAndReadThrows").
                bug("testCloseDirStreamInTheMiddleOfIteration").
                bug("testGetExistingFileSystem").
                bug("testGetFileSystemOtherURI").
                bug("testGetIteratorOfClosedDirStream").
                bug("testIsSameFileOfDifferentPathNonExistingFile2Throws").
                bug("testIsSameFileOfDifferentPathNonExistingFileThrows").
                bug("testReadUnsupportedAttributesThrows").
                bug("testRegisterOtherPath").
                bug("testNewFileSystemOfExistingThrows").
                bug("testRegisterOtherPath").
                bug("testWatchAModify").

                // fixed but not released
                bug( "testFileStoreUnallocatedSpaceIsSmallerUsableSpace").
                bug( "testPathFileStoreGrowingFileLowersUsableSpace");


    }
}
