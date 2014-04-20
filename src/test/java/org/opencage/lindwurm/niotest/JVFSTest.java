package org.opencage.lindwurm.niotest;

import com.google.jimfs.Configuration;
import com.google.jimfs.Jimfs;
import org.junit.BeforeClass;
import org.opencage.kleinod.paths.PathUtils;
import org.opencage.lindwurm.niotest.tests.PathTestIT;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;

/**
* Created by stephan on 19/04/14.
*/
public class JVFSTest extends PathTestIT {

    @BeforeClass
    public static void setUp() throws IOException {

        Path path = PathUtils.getOrCreate(URI.create("jvfs:///"), Collections.<String, Object>emptyMap()).getPath( "/play");

        //Path path = Paths.get(URI.create("jvfs:///")).resolve( "play");

        setPlay(path);
//        setClosablePlay(Jimfs.newFileSystem(Configuration.unix()).getPath("/play"));
//
//        set2ndPlay(Jimfs.newFileSystem(Configuration.unix()).getPath("/play"));

    }

    public JVFSTest() {

        capabilities.notClosable().doesNotSupportWatchService();
    }
}

