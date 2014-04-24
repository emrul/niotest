package org.opencage.lindwurm.niotest;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.opencage.kleinod.paths.PathUtils;
import org.opencage.lindwurm.niotest.tests.PathTestIT;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.util.Collections;

/**
* Created by stephan on 19/04/14.
*/
@Ignore
public class JVFSTest extends PathTestIT {

    @BeforeClass
    public static void setUp() throws IOException {

        Path path = PathUtils.getOrCreate(URI.create("jvfs:///"), Collections.<String, Object>emptyMap()).getPath( "/play");

        setPlay(path);
        set2ndPlay(PathUtils.getOrCreate(URI.create("jvfs:///"), Collections.<String, Object>emptyMap()).getPath( "/play2"));

    }

    public JVFSTest() {

        capabilities.notClosable().doesNotSupportWatchService();
    }

}

