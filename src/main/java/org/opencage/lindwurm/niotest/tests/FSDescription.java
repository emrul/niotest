package org.opencage.lindwurm.niotest.tests;

import org.opencage.kleinod.text.Strings;

import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
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
    private Collection<Character> pathIllegalCharacters = Collections.emptyList();
    private boolean principals = false;

    private boolean hasSizeLimitedFileSystem = false; // todo turn true when implemented
    private boolean supportsPosixAttributes = false;

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
    public boolean hasSizeLimitedFileSystem() {
        return hasSizeLimitedFileSystem;
    }

    @Override
    public Runnable shake() {
        return shake;
    }

    @Override
    public Function<FileSystem,URI> toURI() {
        return toURI;
    }

    @Override
    public Collection<Character> getPathIllegalCharacters() {
        return pathIllegalCharacters;
    }

    @Override
    public boolean supportsPrincipals() {
        return principals;
    }

    @Override
    public boolean supportsPosixAttributes() {
        return supportsPosixAttributes;
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

    public FSDescription unix() {
        pathIllegalCharacters = Arrays.asList('\u0000');
        principals = true;
        return this;
    }

    public void pathIllegalCharacters(Collection<Character> getPathIllegalCharacters) {
        this.pathIllegalCharacters = getPathIllegalCharacters;
    }

    public FSDescription principals( boolean p ) {
        principals = p;
        return this;
    }

    public FSDescription sizeLimitedPlayground( Path limitedPlayground ) {
        hasSizeLimitedFileSystem = true;
        setup.sizeLimitedPlayground = limitedPlayground;
        try {
            Files.createDirectories( limitedPlayground );
        } catch (IOException e) {
            e.printStackTrace();
        }
        return this;
    }

    public FSDescription doesSupportPosixAttributes( boolean on ) {
        supportsPosixAttributes = on;
        return this;
    }
}
