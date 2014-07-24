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

    public FileSystem            fs;
    public Path                  play;
    public Path                  pathAf;
    public Path                  pathBd;
    public Path                  pathCf;
    public SeekableByteChannel   readChannel;
    public URI                   uri;
    public DirectoryStream<Path> dirStream;
    public WatchService          watchService;
    public FileSystemProvider    provider;

    public ClosedFSVars(Path play) throws IOException {
        this.play = play;
    }
}
