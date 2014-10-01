package org.opencage.lindwurm.zero;

import java.io.IOException;
import java.net.URI;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.FileAttributeView;
import java.nio.file.spi.FileSystemProvider;
import java.util.Map;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: stephan
 * Date: 21/02/14
 * Time: 19:31
 * To change this template use File | Settings | File Templates.
 */
public class NullFSProvider extends FileSystemProvider {

    private FileSystem nullFS;

    @Override
    public String getScheme() {
        return "null";
    }

    @Override
    public FileSystem newFileSystem(URI uri, Map<String, ?> env) throws IOException {
        if ( !uri.getScheme().equals( "null")) {
            throw new IllegalArgumentException( "scheme is not 'null'" );
        }

        if ( nullFS != null ) {
            throw new FileSystemAlreadyExistsException();
        }

        nullFS = new NullFS( this );

        return nullFS;
    }

    @Override
    public FileSystem getFileSystem(URI uri) {
        if ( !uri.getScheme().equals( "null")) {
            throw new IllegalArgumentException( "scheme is not 'null'" );
        }

        if ( nullFS == null ) {
            throw new FileSystemNotFoundException();
        }

        return nullFS;
    }

    @Override
    public Path getPath(URI uri) {
        if ( !uri.getScheme().equals( "null")) {
            throw new IllegalArgumentException( "scheme is not 'null'" );
        }

        return null;
    }

    @Override
    public SeekableByteChannel newByteChannel(Path path, Set<? extends OpenOption> options, FileAttribute<?>... attrs) throws IOException {
        return null;
    }

    @Override
    public DirectoryStream<Path> newDirectoryStream(Path dir, DirectoryStream.Filter<? super Path> filter) throws IOException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void createDirectory(Path dir, FileAttribute<?>... attrs) throws IOException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void delete(Path path) throws IOException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void copy(Path source, Path target, CopyOption... options) throws IOException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void move(Path source, Path target, CopyOption... options) throws IOException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isSameFile(Path path, Path path2) throws IOException {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isHidden(Path path) throws IOException {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public FileStore getFileStore(Path path) throws IOException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void checkAccess(Path path, AccessMode... modes) throws IOException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public <V extends FileAttributeView> V getFileAttributeView(Path path, Class<V> type, LinkOption... options) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public <A extends BasicFileAttributes> A readAttributes(Path path, Class<A> type, LinkOption... options) throws IOException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Map<String, Object> readAttributes(Path path, String attributes, LinkOption... options) throws IOException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setAttribute(Path path, String attribute, Object value, LinkOption... options) throws IOException {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
