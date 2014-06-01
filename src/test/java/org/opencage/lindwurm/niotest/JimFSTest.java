package org.opencage.lindwurm.niotest;

import com.google.jimfs.Configuration;
import com.google.jimfs.Jimfs;
import org.junit.BeforeClass;
import org.junit.Test;
import org.opencage.kleinod.text.Strings;
import org.opencage.lindwurm.niotest.tests.ClosedFSVars;
import org.opencage.lindwurm.niotest.tests.FSDescription;
import org.opencage.lindwurm.niotest.tests.PathTestIT;

import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystemAlreadyExistsException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

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
    private static ClosedFSVars closablePlayground;

    @BeforeClass
    public static void setUp() throws IOException {
        playground  = Jimfs.newFileSystem(Configuration.unix()).getPath("/play");
        closablePlayground = new ClosedFSVars( Jimfs.newFileSystem(Configuration.unix()).getPath("/play"));
        secondPlay  = Jimfs.newFileSystem(Configuration.unix()).getPath("/play");

    }

    public JimFSTest() {

        describe().
                playground(playground).
                watcherSleepTime(7000).
                lastAccessTime(false).
                fileStores(true).
                secondPlayground(secondPlay).
                closablePlayground(closablePlayground).
                fileSystemURI(FSDescription::toURIWithoutPath).

                bug("testCopyToClosedFS").
                bug("testClosedFSGetFileStore").
                bug("testAppendAndReadThrows").
                bug("testCloseDirStreamInTheMiddleOfIteration").
//                bug("testGetExistingFileSystem").
//                bug("testGetFileSystemOtherURI").
                bug("testGetIteratorOfClosedDirStream").
                bug("testIsSameFileOfDifferentPathNonExistingFile2Throws").
                bug("testIsSameFileOfDifferentPathNonExistingFileThrows").
                bug("testReadUnsupportedAttributesThrows").
                bug("testRegisterOtherPath").
                bug("testNewFileSystemOfExistingThrows"); // needs env see below
//                bug("testRegisterOtherPath").
//                bug("testWatchAModify").
//
//                // fixed but not released
//                bug( "testFileStoreUnallocatedSpaceIsSmallerUsableSpace").
//                bug( "testPathFileStoreGrowingFileLowersUsableSpace")


    }

    @Test( expected = FileSystemAlreadyExistsException.class )
    public void testJimFSNewFileSystemOfExistingThrows() throws IOException {
        URI uriThisFS = URI.create( Strings.withoutEnd(getRoot().toUri().toString(), getRoot().toString()));
        Map<String, Object > env = new HashMap<>();
        env.put("config", Configuration.unix());
        FS.provider().newFileSystem(  uriThisFS, env );
    }

}
