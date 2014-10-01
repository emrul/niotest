package org.opencage.lindwurm.zero;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.UserPrincipalLookupService;
import java.nio.file.spi.FileSystemProvider;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: stephan
 * Date: 21/02/14
 * Time: 20:02
 * To change this template use File | Settings | File Templates.
 */
public class NullFS extends FileSystem {
    private final NullFSProvider provider;
    final NullPath root;
    private final NullPath dflt;

    public NullFS(NullFSProvider nullFSProvider) {
        provider = nullFSProvider;
        root = new NullPath( this, true );
        dflt = new NullPath( this, false );
    }

    @Override
    public FileSystemProvider provider() {
        return provider;
    }

    @Override
    public void close() throws IOException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isOpen() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isReadOnly() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getSeparator() {
        return "/";
    }

    @Override
    public Iterable<Path> getRootDirectories() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Iterable<FileStore> getFileStores() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Set<String> supportedFileAttributeViews() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Path getPath(String first, String... more) {
        if ( more.length > 0 ) {
            throw new IllegalArgumentException();
        }

        if ( first.equals("")) {
            return dflt;
        }

        if ( first.equals("/")) {
            return root;
        }

        throw new IllegalArgumentException();
    }

    @Override
    public PathMatcher getPathMatcher(String syntaxAndPattern) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public UserPrincipalLookupService getUserPrincipalLookupService() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public WatchService newWatchService() throws IOException {
        return new NullWatchService();
    }
}
