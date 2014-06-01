package org.opencage.lindwurm.niotest.tests;

import org.opencage.kleinod.text.Strings;

import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.util.function.Function;

/**
 * Created by stephan on 15/05/14.
 */
public class FSDescription implements FSCapabilities {

    private final PathTestIT setup;

    private boolean closable = true;
    private boolean hasLinks = true;
    private boolean hasSymbolicLinks = true;
    private boolean hasAsynchronousFileChannels = true;
    private boolean hasFileChannels = true;
    private boolean supportsCreationTime = true;
    private boolean supportsLastAccessTime = true;
    private boolean supportsWatchService = true;
    private boolean has2ndFileSystem = true;
    private boolean filestores = false;
    private Runnable shake = () -> {};
    private Function<FileSystem, URI> toURI = FSDescription::toURIWithRoot;

    FSDescription(PathTestIT setup) {
        this.setup = setup;
    }

    public FSDescription playground(Path path) {
        setup.setPlay( path );
        return this;
    }

    public FSDescription secondPlayground(Path path) {
        setup.play2 = path;
        if ( path == null ) {
            has2ndFileSystem = false;
        }
        return this;
    }

    public FSDescription noSecondPlayground() {
        has2ndFileSystem = false;
        return this;
    }



    public FSDescription notClosable() {
        closable = false;
        return this;
    }

    public FSDescription fileStores(boolean fs) {
        filestores = fs;
        return this;
    }

    public FSDescription lastAccessTime( boolean la) {
        supportsLastAccessTime = la;
        return this;
    }

    @Override
    public boolean isClosable() {
        return closable;
    }

    public boolean hasLinks() {
        return hasLinks;
    }

    public boolean hasSymbolicLinks() {
        return hasSymbolicLinks;
    }

    public boolean hasAsynchronousFileChannels() {
        return hasAsynchronousFileChannels;
    }

    public boolean hasFileChannels() {
        return hasFileChannels;
    }

    public boolean supportsCreationTime() {
        return supportsCreationTime;
    }

    public FSDescription creationTime( boolean ct ) {
        this.supportsCreationTime = ct;
        return this;
    }

    public boolean supportsLastAccessTime() {
        return supportsLastAccessTime;
    }

    public FSDescription lastAccessTime() {
        this.supportsLastAccessTime = false;
        return this;
    }

    public boolean supportsWatchService() {
        return supportsWatchService;
    }

    public FSDescription doesNotSupportWatchService() {
        this.supportsWatchService = false;
        return this;
    }

    public FSDescription noLinks() {
        this.hasLinks = false;
        return this;
    }

    public FSDescription noSymLinks() {
        this.hasSymbolicLinks = false;
        return this;
    }

    public FSDescription noAsynchronousFileChannels() {
        this.hasAsynchronousFileChannels = false;
        return this;
    }

    public FSDescription noFileChannels() {
        this.hasFileChannels = false;
        return this;
    }

    public FSDescription shake( Runnable f ) {
        this.shake = f;
        return this;
    }


    public boolean supportsFileStores() {
        return filestores;
    }

    @Override
    public boolean has2ndFileSystem() {
        return has2ndFileSystem;
    }

    @Override
    public Runnable shake() {
        return shake;
    }

    @Override
    public Function<FileSystem,URI> toURI() {
        return toURI;
    }

    public static URI toURIWithRoot( FileSystem fs ) {
        return fs.getPath("").toAbsolutePath().getRoot().toUri();
    }

    public static URI toURIWithoutPath( FileSystem fs ) {
        Path root =  fs.getPath("").toAbsolutePath().getRoot();
        return URI.create( Strings.withoutEnd( root.toUri().toString(), root.toString()));
    }

    public FSDescription watcherSleepTime( long seconds ) {
        this.setup.watcherSleep = seconds;
        return this;
    }


    public FSDescription bug(String method) {
        this.setup.notSupported.put( method, "" );
        return this;
    }

    public FSDescription closablePlayground(ClosedFSVars closedVars) {
        this.setup.setClosablePlay( closedVars );
        return this;
    }


    public FSDescription fileSystemURI( Function<FileSystem,URI> func ) {
        toURI = func;
        return this;
    }
}
