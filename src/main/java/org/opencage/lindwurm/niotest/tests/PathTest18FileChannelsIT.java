package org.opencage.lindwurm.niotest.tests;

import org.junit.Test;
import org.opencage.kleinod.collection.Sets;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

/**
 * Created by spfab on 28.10.2014.
 */
public abstract class PathTest18FileChannelsIT extends PathTest17WindowsPathIT {

    @Test
    public void testFileChannelSimple() throws IOException {
        try ( FileChannel fch = FS.provider().newFileChannel(getPathPABf(), Sets.asSet(StandardOpenOption.WRITE, StandardOpenOption.READ))) {
            try ( FileLock lock = fch.tryLock()) {
                System.out.println(lock);
            }

        }
    }


}
