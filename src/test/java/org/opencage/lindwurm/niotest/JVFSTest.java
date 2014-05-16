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

    private static Path playground;
    private static Path playground2;

    @BeforeClass
    public static void setUp() throws IOException {

        playground = PathUtils.getOrCreate(URI.create("jvfs:///"), Collections.<String, Object>emptyMap()).getPath( "/play");
        //playground2 = PathUtils.getOrCreate(URI.create("jvfs:///"), Collections.<String, Object>emptyMap()).getPath( "/play2");

    }

    public JVFSTest() {

        describe().
                playground( playground ).
                noSecondPlayground(). //secondPlayground( playground2 ).
                notClosable().
                doesNotSupportWatchService();
    }

}

