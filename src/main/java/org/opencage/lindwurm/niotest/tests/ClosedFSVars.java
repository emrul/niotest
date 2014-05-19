package org.opencage.lindwurm.niotest.tests;

import java.io.IOException;
import java.net.URI;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.*;
import java.nio.file.spi.FileSystemProvider;

/**
 * Created by stephan on 18/05/14.
 */
public class ClosedFSVars {

    FileSystem fs;
    Path       play;
    Path       pathAf;
    Path       pathBd;
    Path       pathCf;
    SeekableByteChannel readChannel;
    URI        uri;
    DirectoryStream<Path> dirStream;
    WatchService watchService;
    public FileSystemProvider provider;

    public ClosedFSVars(Path play) throws IOException {
        this.play = play;
    }
}
