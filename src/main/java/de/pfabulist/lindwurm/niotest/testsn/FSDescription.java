//package de.pfabulist.lindwurm.niotest.testsn;
//
//import de.pfabulist.kleinod.paths.Filess;
//import de.pfabulist.kleinod.paths.Pathss;
//
//import java.nio.file.FileSystems;
//import java.nio.file.Path;
//import java.util.HashSet;
//import java.util.Set;
//import java.util.function.Consumer;
//
///**
// * ** BEGIN LICENSE BLOCK *****
// * BSD License (2 clause)
// * Copyright (c) 2006 - 2014, Stephan Pfab
// * All rights reserved.
// * <p>
// * Redistribution and use in source and binary forms, with or without
// * modification, are permitted provided that the following conditions are met:
// * * Redistributions of source code must retain the above copyright
// * notice, this list of conditions and the following disclaimer.
// * * Redistributions in binary form must reproduce the above copyright
// * notice, this list of conditions and the following disclaimer in the
// * documentation and/or other materials provided with the distribution.
// * <p>
// * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
// * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
// * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
// * DISCLAIMED. IN NO EVENT SHALL Stephan Pfab BE LIABLE FOR ANY
// * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
// * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
// * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
// * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
// * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
// * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
// * **** END LICENSE BLOCK ****
// */
//public class FSDescription implements FSCapabilities{
//
//
//    private boolean hasUNCPaths = true;
//    protected Character[] illegalCharacters = new Character[0];
//    private int maxFilenameLength = 0;
//    private int maxPathLength = 7;
//    public boolean hasSymLinksToOtherProviders = true;
//    private int watchDelay = 2000;
//    public boolean hasSymLinksToDirs = true;
//
//    public static enum SimOS {
//        WINDOWS, UNIX, NONE
//    }
//
//    private Path playground;
//    private Path closablePlayground = null;
//    private boolean isClosable = true;
//
//    private boolean lastAccessTime = true;
//    private boolean creationTime = true;
//    private long attributeDelay    = 2000;
//
//    private boolean hasPosixAttributes = true;
//
//    private SimOS os = SimOS.NONE;
//
//    private boolean caseIgnorant = false;
//    private boolean caseRemembering = false;
//
//    protected boolean hasSymbolicLinks = true;
//    private boolean hasHardLinks = true;
//    private boolean hasHardLinksToDirs = true;
//    private boolean hasFileStores = true;
//    private boolean hasWatchService = true;
//
//
//
//
//
//    public FSDescription playground(Path path) {
//        if ( !path.isAbsolute()) {
//            throw new IllegalArgumentException("need absolute path");
//        }
//
//        Filess.createDirectories(path);
//
//        playground = path;
//        return this;
//    }
//
//    @Override
//    public Path getPlayground() {
//        return playground;
//    }
//
//
//    public TimeBuilder time() {
//        return new TimeBuilder(this);
//    }
//
//    public static class TimeBuilder {
//        private final FSDescription fsDescription;
//
//        public TimeBuilder(FSDescription fsDescription) {
//            this.fsDescription = fsDescription;
//        }
//
//        public TimeBuilder lastAccessTime( boolean val ) {
//            fsDescription.lastAccessTime = val;
//            return this;
//        }
//
//        public TimeBuilder creationTime( boolean val ) {
//            fsDescription.creationTime = val;
//            return this;
//        }
//
//        public TimeBuilder delay( long milli) {
//            fsDescription.attributeDelay = milli;
//            return this;
//        }
//
//
//        public FSDescription yes() {
//            return fsDescription;
//        }
//
//        public FSDescription no() {
//            fsDescription.lastAccessTime = false;
//            return fsDescription;
//        }
//
//    }
//
//    public WindowsBuilder windows() {
//        return new WindowsBuilder( this, b -> {
//            if (b) {
//                os = SimOS.WINDOWS;
//                caseIgnorant = true;
//                caseRemembering = true;
//                maxFilenameLength = 256;
//                maxPathLength = 32000; // todo
//
//                hasPosixAttributes = false;
//            }
//        });
//    }
//
//
//
//    public HardLinkBuilder hardLinks() {
//        return new HardLinkBuilder( this, b -> hasHardLinks = b );
//    }
//
//
//    public boolean hasLastAccessTime() {
//        return lastAccessTime;
//    }
//
//    public long attributeDelay() {
//        return attributeDelay;
//    }
//
//    @Override
//    public boolean hasCreationTime() {
//        return creationTime;
//    }
//
//    @Override
//    public boolean isWindows() {
//        return os.equals( SimOS.WINDOWS );
//    }
//
//    @Override
//    public boolean isCaseIgnorant() {
//        return caseIgnorant;
//    }
//
//    @Override
//    public boolean isCaseRemembering() {
//        return caseRemembering;
//    }
//
//    @Override
//    public boolean hasSymLinks() {
//        return hasSymbolicLinks;
//    }
//
//    @Override
//    public boolean hasHardLinks() {
//        return hasHardLinks;
//    }
//
//    @Override
//    public boolean hasHardLinksToDirs() {
//        return hasHardLinksToDirs;
//    }
//
//    @Override
//    public boolean hasFileStores() {
//        return hasFileStores;
//    }
//
//    public FileStoreBuilder fileStores() {
//        return new FileStoreBuilder(this, b -> hasFileStores = b);
//    }
//
//
//
//    @Override
//    public boolean isUnix() {
//        return os.equals( SimOS.UNIX );
//    }
//
//    @Override
//    public boolean hasPosixAttributes() {
//        return hasPosixAttributes;
//    }
//
//    @Override
//    public boolean isClosable() {
//        return isClosable;
//    }
//
//    @Override
//    public Path getClosablePlayground() {
//        return closablePlayground;
//    }
//
//
//    public FSDescription unix( boolean val ) {
//        if ( val ) {
//            os = SimOS.UNIX;
//            hasPosixAttributes = true;
//            maxFilenameLength = 255;
//        }
//        return this;
//    }
//
//    public SymLinkDescriptionBuilder symLinks() {
//        return new SymLinkDescriptionBuilder( this, b -> hasSymbolicLinks = b );
//    }
//
//    public static class HardLinkBuilder extends DetailBuilder {
//
//        public HardLinkBuilder(FSDescription descr, Consumer<Boolean> setter) {
//            super(descr, setter);
//        }
//
//        public HardLinkBuilder toDirs( boolean val ) {
//            fsDescription.hasHardLinksToDirs = val;
//            return this;
//        }
//    }
//
//    public CloseableBuilder closable() {
//        return new CloseableBuilder( this );
//    }
//
//    public static class CloseableBuilder {
//
//        private final FSDescription fsDescription;
//
//        private CloseableBuilder(FSDescription fsDescription) {
//            this.fsDescription = fsDescription;
//        }
//
//        public FSDescription yes( Path closable ) {
//            fsDescription.closablePlayground = closable;
//            return fsDescription;
//        }
//
//        public FSDescription no() {
//            fsDescription.isClosable = false;
//            return fsDescription;
//        }
//    }
//
//    protected Set<String> bugs = new HashSet<>();
//    protected Set<String> bugSchema = new HashSet<>();
//
//    public FSDescription bug(String testMethodName ) {
//        bugs.add(testMethodName);
//        return this;
//    }
//
//    public FSDescription nitpick(String testMethodName ) {
//        bugs.add(testMethodName);
//        return this;
//    }
//
//    public FSDescription bug(String testMethodName, boolean condition) {
//        if ( condition ) {
//            bugs.add(testMethodName);
//        }
//        return this;
//    }
//
//
//    public FSDescription bugScheme(String schem ) {
//        bugSchema.add( schem );
//        return this;
//    }
//
//    @Override
//    public boolean isBug(String testMethodName) {
//        if( bugs.contains( testMethodName )) {
//            return true;
//        }
//
//        for ( String schm : bugSchema ) {
//            if ( testMethodName.contains( schm )) {
//                return true;
//            }
//        }
//
//        return false;
//    }
//
//
//    protected Path otherProviderPlayground;
//
//    @Override
//    public Path getOtherProviderPlay() {
//
//        if (otherProviderPlayground == null ) {
//            if ( playground.getFileSystem().equals(FileSystems.getDefault())) {
//                throw new IllegalStateException();
//            }
//
//            otherProviderPlayground = Pathss.getTmpDir("OtherProviderFS");
//        }
//
//        return otherProviderPlayground;
//    }
//
//    @Override
//    public long getWatcherSleepTime() {
//        return watchDelay;
//    }
//
//    @Override
//    public boolean hasWatchService() {
//        return hasWatchService;
//    }
//
//    @Override
//    public int getMaxFilenameLength() {
//        return maxFilenameLength;
//    }
//
//    @Override
//    public boolean hasMaxFileName() {
//        return maxFilenameLength >= 0;
//    }
//
//    @Override
//    public boolean hasSymLinkToOtherProviders() {
//        return hasSymLinksToOtherProviders;
//    }
//
//    @Override
//    public boolean hasDirSymLinks() {
//        return hasSymLinksToDirs;
//    }
//
//    @Override
//    public boolean hasUNCPaths() {
//        return hasUNCPaths();
//    }
//
//    public FSDescription otherProviderPlayground(Path play ) {
//        otherProviderPlayground = play;
//        Filess.createDirectories( play );
//        return this;
//    }
//
//    public WatchBuilder watchService() {
//        return new WatchBuilder( this, b -> hasWatchService = b);
//    }
//
//    public static class WatchBuilder extends DetailBuilder {
//        public WatchBuilder(FSDescription descr, Consumer<Boolean> setter) {
//            super(descr, setter);
//        }
//
//        public WatchBuilder delay( int milli ) {
//            fsDescription.watchDelay = milli;
//            return this;
//        }
//    }
//
//    public static class FileStoreBuilder extends DetailBuilder {
//        public FileStoreBuilder(FSDescription descr, Consumer<Boolean> setter) {
//            super(descr, setter);
//        }
//    }
//
//    public PathConstraintsBuilder pathConstraints() {
//        return new PathConstraintsBuilder(this, b -> {} );
//    }
//
//
//    public static class PathConstraintsBuilder extends DetailBuilder {
//        public PathConstraintsBuilder(FSDescription descr, Consumer<Boolean> setter) {
//            super(descr, setter);
//        }
//
//        public PathConstraintsBuilder illegalCharacters(  Character ... pathIllegalCharacters ) {
//            fsDescription.illegalCharacters = pathIllegalCharacters;
//            return this;
//        }
//
//        public PathConstraintsBuilder maxFilenameLength( int len ) {
//            fsDescription.maxFilenameLength = len;
//            return this;
//
//        }
//
//        public PathConstraintsBuilder maxPathLength( int len ) {
//            fsDescription.maxPathLength = len;
//            return this;
//
//        }
//    }
//
//    public static class WindowsBuilder extends DetailBuilder {
//        public WindowsBuilder(FSDescription descr, Consumer<Boolean> setter) {
//            super(descr, setter);
//            fsDescription.hasUNCPaths = true;
//        }
//
//        public WindowsBuilder UNCPaths( boolean val ) {
//            fsDescription.hasUNCPaths = val;
//            return this;
//        }
//    }
//}
