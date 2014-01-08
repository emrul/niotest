package org.opencage.niotest;

import org.opencage.kleinod.emergent.Todo;
import org.opencage.kleinod.lambda.F0;
import org.opencage.kleinod.lambda.Function;
import org.opencage.kleinod.paths.Capabilities;
import org.opencage.kleinod.paths.CapabilitiesProvider;

import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.spi.FileSystemProvider;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.opencage.kleinod.emergent.Todo.nullCheck;
import static org.opencage.kleinod.emergent.Todo.todo;

/**
* ** BEGIN LICENSE BLOCK *****
* BSD License (2 clause)
* Copyright (c) 2006 - 2013, Stephan Pfab
* All rights reserved.
* <p/>
* Redistribution and use in source and binary forms, with or without
* modification, are permitted provided that the following conditions are met:
* * Redistributions of source code must retain the above copyright
* notice, this list of conditions and the following disclaimer.
* * Redistributions in binary form must reproduce the above copyright
* notice, this list of conditions and the following disclaimer in the
* documentation and/or other materials provided with the distribution.
* <p/>
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
public class TestParams {
    // a file system for non modifying test
    public FileSystem readOnlyFileSystem;
    // non existing in above fileSystem or in all
    public String noSuchFilePath;
    public Path fileWithContent;
    TmpResource<? extends Path> tmpDir;
    private List<String> legalPathElement;
    private Path nonExistingPath;
    private F0<Path> createTmp;
    private Function<Path, Void> deleteTmp;
    TmpResource<Path> nonEmptyDir;
    int nonEmptyDirCount;
    private URI uriForUnknownFS;
    private URI uriForNewFS;
    private URI uriForExistingFS;
    private Map<String, ?> envForNewFS;
    private FileSystem otherProviderFS;


    public TestParams() {
        otherProviderFS =  FileSystems.getDefault();

    }

    public TestParams readOnlyFileSystem( FileSystem fileSystem ) {
        this.readOnlyFileSystem = fileSystem;
        return this;
    }

    public TestParams setOtherProviderFS( FileSystem fs ) {
        this.otherProviderFS = fs;
        return this;
    }

    public FileSystem getOtherProviderFS() {
        return otherProviderFS;
    }

    public Path getFileWithContent() {
        if ( fileWithContent == null ) {
            todo();
        }
        return fileWithContent;
    }

    public Path getTmpDir() {
        if ( tmpDir == null ) {
            todo();
        }
        return tmpDir.get(""); // NOSONAR
    }

    public Path getTmpDir( String prefix ) {
        if ( tmpDir == null ) {
            todo();
        }
        return tmpDir.get( prefix ); // NOSONAR
    }

    public boolean hasTmpDir() {
        return tmpDir != null;
    }

    public String getLegalPathElement() {
        return getNameStr( 0 );
    }

    public String getLegalPathElement(int i ) {
        return getNameStr( i );
    }

    public String getNameStr( int i ) {
        switch( i ) {
            case 0: return "aaa";
            case 1: return "bbb";
            case 2: return "cc";
            case 3: return "d";
            default: return "XyZZ";
        }
    }

    public String getNameStr() {
        return getNameStr( 0 );
    }

    public Path getName( int i ) {
        return readOnlyFileSystem.getPath( "" ).resolve( getNameStr(i) );
    }

    public TestParams legalPathElement( String ... abc ) {
        legalPathElement = Arrays.asList( abc );
        return this;
    }

    public <T extends Path> TestParams tmpDir( TmpResource<T> tmpResourceTmp ) {
        tmpDir = tmpResourceTmp;
        return this;
    }

    public TestParams tmpDir( Path mutualRoot ) {
        tmpDir = new StdTmpDir( 0, mutualRoot );
        nonEmptyDir = new StdTmpDir( 3, mutualRoot );
        nonEmptyDirCount = 3;
        return this;

    }

    public Path getNonExistingPath() {
        return nullCheck( nonExistingPath );
    }

    public TestParams nonExistingPath( Path path ) {
        nonExistingPath = path;
        return this;
    }

    public Path getNonEmptyDir() {
        nullCheck( nonEmptyDir );
        return nonEmptyDir.get("");
    }

    public Path getNonEmptyDir( String suff ) {
        nullCheck( nonEmptyDir );
        return nonEmptyDir.get(suff);
    }

    public int getCountOfNonEmptyDir() {
        return nonEmptyDirCount;
    }

    public TestParams nonEmptyDir( TmpResource<Path> dir, int count ) {
        nonEmptyDir = dir;
        nonEmptyDirCount = count;
        return this;
    }

    public URI getUriForUnknownFS() {
        return nullCheck( uriForUnknownFS );
    }

    public TestParams setUriForUnknownFS( URI uriForUnknownFS ) {
        this.uriForUnknownFS = uriForUnknownFS;
        return this;
    }

    public URI getUriForNewFS() {
        return Todo.nullCheck(uriForNewFS);
    }

    public TestParams setUriForNewFS( URI uriForNewFS ) {
        this.uriForNewFS = uriForNewFS;
        return this;
    }

    public URI getUriForExistingFS() {
        return uriForExistingFS;
    }

    public TestParams setUriForExistingFS( URI uriForExistingFS ) {
        this.uriForExistingFS = uriForExistingFS;
        return this;
    }

    public Capabilities getCapabilities() {
        return new CapabilitiesProvider().getCapabilities( readOnlyFileSystem );
    }

    public Map<String, ?> getEnvForNewFS() {
        return nullCheck( envForNewFS );
    }

    public TestParams setEnvForNewFS( Map<String, ?> envForNewFS ) {
        this.envForNewFS = envForNewFS;
        return this;

    }

    public FileSystemProvider getProvider() {
        return readOnlyFileSystem.provider();
    }

    public Path getA() {
        return readOnlyFileSystem.getPath( getLegalPathElement() );
    }

    public boolean hasNonEmptyDir() {
        return nonEmptyDir != null;
    }

    public Path getName() {
        return getName( 0 );
    }
}
