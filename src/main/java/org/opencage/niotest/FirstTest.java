package org.opencage.niotest;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.opencage.kleinod.paths.PathUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.opencage.niotest.matcher.PathIsDirectory.isDirectory;

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
public class FirstTest {

    protected TestParams p;

    @Rule
    public TestName testMethodName = new TestName();

    @Before
    public void beforeEachMethods() {
        afterEachMethod();
    }

    @After
    public void afterEachMethod() {
        if ( p.tmpDir != null ) {
            p.tmpDir.cleanup();
        }

        if ( p.nonEmptyDir != null ) {
            p.nonEmptyDir.cleanup();
        }
    }

    public FirstTest() {

        p = new TestParams().readOnlyFileSystem( FileSystems.getDefault() );

        p.nonExistingPath( FileSystems.getDefault().getPath( "sdfgwqrefqvrqerfaerf" ).toAbsolutePath());
        p.tmpDir( new StdTmpDir(0, PathUtils.getTmpDir( "PathTest" )));

        p.nonEmptyDir( new StdTmpDir( 1, PathUtils.getTmpDir( "PathTest" ) ), 1 );
        p.setEnvForNewFS( Collections.EMPTY_MAP );

        Path zipLocation = null;
        try {
            zipLocation = Files.createTempDirectory( "zipTest" ).resolve( "ro.zip" );
            try( InputStream zip = FirstTest.class.getResourceAsStream( "zip/ro.zip" )) {
                Files.copy( zip, zipLocation );
            }

            URI roURI = URI.create( "jar:" + zipLocation.toUri() + "!/" );
            p.setOtherProviderFS( FileSystems.newFileSystem( roURI, Collections.EMPTY_MAP ));
        } catch( IOException e ) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }


    }

    @Test
    public void testTestNameIsSet() throws Exception {
        assertEquals( "testTestNameIsSet", testMethodName.getMethodName() );
    }

    @Test
    public void testTestNameIsSetForEveryMethod() throws Exception {
        assertEquals( "testTestNameIsSetForEveryMethod", testMethodName.getMethodName() );
    }


    /**
     * ----------------
     */

    public Path getPathA() {
        return p.readOnlyFileSystem.getPath( p.getLegalPathElement() );
    }

    public Path getPathB() {
        return p.readOnlyFileSystem.getPath( p.getLegalPathElement(1) );
    }

    public Path getPathAB() {
        return p.readOnlyFileSystem.getPath( p.getLegalPathElement(), p.getLegalPathElement( 1 ) );
    }

    public Path getPathBC() {
        return p.readOnlyFileSystem.getPath( p.getLegalPathElement( 1 ), p.getLegalPathElement( 2 ) );
    }

    public Path getPathABC() {
        return p.readOnlyFileSystem.getPath( p.getLegalPathElement(), p.getLegalPathElement( 1 ), p.getLegalPathElement( 2 ) );
    }

    public Path getPathRAB() {
        return getRoot().resolve( p.getLegalPathElement() ).resolve( p.getLegalPathElement(1) );
    }

    public Path getPathRABC() {
        return getRoot().resolve( p.getLegalPathElement() ).resolve( p.getLegalPathElement(1) ).resolve( p.getLegalPathElement( 2 ) );
    }

    public Path getRoot() {
        return p.readOnlyFileSystem.getPath( "" ).toAbsolutePath().getRoot();
    }

    public Path getDefaultPath() {
        return p.readOnlyFileSystem.getPath( "" );
    }

    public String getSeparator() {
        return p.readOnlyFileSystem.getSeparator();
    }

    public Path getPathTmpA( Path tmpDir ) {
        return tmpDir.resolve( p.getLegalPathElement() );
    }

    public Path getPathTmpA() {
        return p.getTmpDir( testMethodName.getMethodName() ).resolve( p.getLegalPathElement() );
    }

    public Path getPathTmpAB() {
        return p.getTmpDir( testMethodName.getMethodName() ).resolve( p.getLegalPathElement() ).resolve( p.getLegalPathElement(1) );
    }


    public Path getPathRelTmpNonEmpty( ) {
        Path abs = p.getNonEmptyDir( testMethodName.getMethodName() );
        return abs.getFileSystem().getPath( "" ).toAbsolutePath().relativize( abs );
    }

    public Path getTmpDir() {
        return p.getTmpDir( testMethodName.getMethodName() );
    }


}
