package de.pfabulist.lindwurm.niotest.tests;

import de.pfabulist.kleinod.errors.Runnnable;
import de.pfabulist.kleinod.paths.Filess;
import de.pfabulist.kleinod.text.Strings;

import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;

/**
 * ** BEGIN LICENSE BLOCK *****
 * BSD License (2 clause)
 * Copyright (c) 2006 - 2014, Stephan Pfab
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL Stephan Pfab BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * **** END LICENSE BLOCK ****
 */
public class FSDescription implements FSCapabilities {

    private final PathTestIT setup;

    private boolean closable = true;
    private boolean hasLinks = true;
    boolean hasSymbolicLinks = true;
    private boolean hasAsynchronousFileChannels = true;
    private boolean hasFileChannels = true;
    private boolean supportsCreationTime = true;
    private boolean supportsLastAccessTime = true;
    boolean supportsWatchService = true;
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
    boolean supportsForeignSymLinks = true;
    public boolean hasDirSymLinks = true;
    public boolean delayedSymLinkLoopChecking = false;
    public boolean immediateSymLinkLoopChecking = true;
    int watcherSleepTime = 1200;

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

    public static class WatchServiceBuilder {


        private final FSDescription fsDescription;

        public WatchServiceBuilder(FSDescription fsDescription) {
            this.fsDescription = fsDescription;
        }

        public FSDescription no() {
            fsDescription.supportsWatchService = false;
            return fsDescription;
        }

        public FSDescription yes() {
            fsDescription.supportsWatchService = true;
            return fsDescription;
        }

        public WatchServiceBuilder sleepTime( int seconds ) {
            fsDescription.watcherSleepTime = seconds;
            return this;
        }


    }
    
    public WatchServiceBuilder watchService() {
        return new WatchServiceBuilder( this ) ;
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
    public boolean supportsForeignSymLinks() {
        return supportsForeignSymLinks;
    }

    @Override
    public int getWatcherSleepTime() {
        return watcherSleepTime;
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
        return URI.create( Strings.withoutSuffix(root.toUri().toString(), root.toString()));
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
        pathIllegalCharacters(':', /*'\\', '/', */ '|', '"');

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

    public FSDescription fileChannels( boolean val ) {
        this.fileChannels = val;
        return this;
    }

    public FSDescription doesNotSupporForeignSymLinks() {
        this.supportsForeignSymLinks = false;
        return this;
    }
    
    public SymLinkDescriptionBuilder symLinks() {
        return new SymLinkDescriptionBuilder( this );
    }
}
