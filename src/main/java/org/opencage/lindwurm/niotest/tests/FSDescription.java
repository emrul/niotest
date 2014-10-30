package org.opencage.lindwurm.niotest.tests;

import org.opencage.kleinod.errors.Runnnable;
import org.opencage.kleinod.paths.Filess;
import org.opencage.kleinod.text.Strings;

import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
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
    private Runnnable shake = () -> {};
    private Function<FileSystem, URI> toURI = FSDescription::toURIWithRoot;
    private Collection<Character> pathIllegalCharacters = Collections.emptyList();
    private boolean principals = false;

    private boolean hasSizeLimitedFileSystem = false; // todo turn true when implemented
    private boolean supportsPosixAttributes = false;
    private Path otherRoot;
    private boolean windows = false;
    private boolean canSeeLocalUNCSharesSet;
    private boolean canSeeLocalUNCShares;
    private int maxFilenameLength = 255;
    private List<String> illegalFilenames = new ArrayList<>();
    private boolean fileChannels = false;

    FSDescription(PathTestIT setup) {
        this.setup = setup;
    }

    public FSDescription playground(Path path) {
        if ( !path.isAbsolute()) {
            throw new IllegalArgumentException("need absolute path");
        }
        setup.setPlay( path );
        return this;
    }

    public FSDescription secondPlayground(Path path) {
        if ( !path.isAbsolute()) {
            throw new IllegalArgumentException("need absolute path");
        }
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

    public FSDescription shake( Runnnable f ) {
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
    public Runnnable shake() {
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
    public Collection<String> getIllegalFilenames() {
        return illegalFilenames;
    }

    @Override
    public boolean supportsFileChannels() {
        return fileChannels;
    }

    @Override
    public boolean supportsPrincipals() {
        return principals;
    }

    @Override
    public boolean supportsPosixAttributes() {
        return supportsPosixAttributes;
    }

    @Override
    public Path getOtherRoot() {
        return otherRoot;
    }

    @Override
    public boolean hasOtherRoot() {
        return otherRoot != null;
    }

    @Override
    public boolean isWindows() {
        return windows;
    }

    @Override
    public boolean canSeeLocalUNCShares(FileSystem fs) {
        if ( !isWindows() ) {
            return false;
        }

        if ( !canSeeLocalUNCSharesSet ) {
            canSeeLocalUNCShares = Files.exists( fs.getPath( "\\\\localhost\\C$"));
            canSeeLocalUNCSharesSet = true;
        }

        return canSeeLocalUNCShares;
    }

    @Override
    public int getMaxFilenameLength() {
        return maxFilenameLength;
    }

    public static URI toURIWithRoot( FileSystem fs ) {
        URI ret = fs.getPath("").toAbsolutePath().getRoot().toUri();
        return ret;
    }

    public static URI toURIWithoutPath( FileSystem fs ) {
        Path root =  fs.getPath("").toAbsolutePath().getRoot();
        return URI.create( Strings.withoutSuffix( root.toUri().toString(), root.toString()));
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
        doesSupportPosixAttributes( true );
        return this;
    }

    public FSDescription unix( boolean on ) {
        if ( on ) {
            return unix();
        }
        return this;
    }

    public FSDescription pathIllegalCharacters( Character ... getPathIllegalCharacters) {
        this.pathIllegalCharacters = Arrays.asList(getPathIllegalCharacters);
        return this;
    }

    public FSDescription principals( boolean p ) {
        principals = p;
        return this;
    }

    public FSDescription sizeLimitedPlayground( Path limitedPlayground ) {
        hasSizeLimitedFileSystem = true;
        setup.sizeLimitedPlayground = limitedPlayground;
        Filess.createDirectories(limitedPlayground);
        return this;
    }

    public FSDescription doesSupportPosixAttributes( boolean on ) {
        supportsPosixAttributes = on;
        return this;
    }

    public FSDescription otherRoot( Path path ) {
        otherRoot = path;
        return this;
    }

    public FSDescription alternativeNames( String... alt ) {
        setup.nameStrCase = alt;
        return this;
    }

    public FSDescription windows( boolean on ) {
        this.windows = on;

        setIllegalFilenames( "nul", "com" ); // todo more
        pathIllegalCharacters(':', '\\', '/', '|', '"');

        return this;
    }

    public FSDescription setMaxFilenameLength(int maxFilenameLength) {
        this.maxFilenameLength = maxFilenameLength;
        return this;
    }

    public FSDescription setIllegalFilenames( String ... ill ) {
        this.illegalFilenames = Arrays.asList( ill );
        return this;
    }

    public FSDescription doesSupportsFileChannels() {
        this.fileChannels = true;
        return this;
    }
}
