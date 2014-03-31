package org.opencage.lindwurm.niotest;

import com.google.jimfs.Configuration;
import com.google.jimfs.Jimfs;
import org.junit.BeforeClass;
import org.junit.Ignore;
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
@Ignore
public class JimFSTest extends PathTestIT {

    //Jimfs.newFileSystem(Configuration.unix())

    @BeforeClass
    public static void setUp() throws IOException {
        Path path = Jimfs.newFileSystem(Configuration.unix()).getPath("/play");
        setPlay( path );
        setClosablePlay(Jimfs.newFileSystem(Configuration.unix()).getPath("/play"));

        set2ndPlay(Jimfs.newFileSystem(Configuration.unix()).getPath("/play"));

    }

    public JimFSTest() {

        capabilities.notClosable();
        setWatcherSleep( 7000 );
    }
}
