package org.opencage.lindwurm.nul;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.*;
import java.util.Iterator;

/**
 * Created with IntelliJ IDEA.
 * User: stephan
 * Date: 21/02/14
 * Time: 20:05
 * To change this template use File | Settings | File Templates.
 */
public class NullPath implements Path {

    private final NullFS fileSystem;
    private boolean absolute;

    public NullPath(NullFS nullFS, boolean absolute) {
        fileSystem = nullFS;
        this.absolute  = absolute;
    }

    @Override
    public FileSystem getFileSystem() {
        return fileSystem;
    }

    @Override
    public boolean isAbsolute() {
        return absolute;
    }

    @Override
    public Path getRoot() {
        return fileSystem.root;
    }

    @Override
    public Path getFileName() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Path getParent() {
        return null;
    }

    @Override
    public int getNameCount() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Path getName(int index) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Path subpath(int beginIndex, int endIndex) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean startsWith(Path other) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean startsWith(String other) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean endsWith(Path other) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean endsWith(String other) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Path normalize() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Path resolve(Path other) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Path resolve(String other) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Path resolveSibling(Path other) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Path resolveSibling(String other) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Path relativize(Path other) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public URI toUri() {
        return URI.create( "null:/");
    }

    @Override
    public Path toAbsolutePath() {
        return fileSystem.root;
    }

    @Override
    public Path toRealPath(LinkOption... options) throws IOException {
        return fileSystem.root;
    }

    @Override
    public File toFile() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public WatchKey register(WatchService watcher, WatchEvent.Kind<?>[] events, WatchEvent.Modifier... modifiers) throws IOException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public WatchKey register(WatchService watcher, WatchEvent.Kind<?>... events) throws IOException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Iterator<Path> iterator() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int compareTo(Path other) {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NullPath paths = (NullPath) o;

        if (absolute != paths.absolute) return false;
        if (fileSystem != null ? !fileSystem.equals(paths.fileSystem) : paths.fileSystem != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = fileSystem != null ? fileSystem.hashCode() : 0;
        result = 31 * result + (absolute ? 1 : 0);
        return result;
    }
}
