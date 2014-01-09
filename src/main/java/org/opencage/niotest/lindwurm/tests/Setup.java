package org.opencage.niotest.lindwurm.tests;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.opencage.kleinod.paths.PathUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

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
public abstract class Setup {

    protected Params p;
    protected static Path play;

    protected static byte[] CONTENT;
    protected static byte[] CONTENT_OTHER;
    protected static byte[] CONTENT20k;


    @Rule
    public TestName testMethodName = new TestName();



    @BeforeClass
    public static void beforeClass3() {
        try {
            CONTENT = "hi there".getBytes( "UTF-8" );
            CONTENT_OTHER = "what's up".getBytes( "UTF-8" );

            CONTENT20k = new byte[20000];
            for ( int i = 0; i < 20000; i++ ) {
                CONTENT20k[i] = (byte) (i);
            }
        } catch( UnsupportedEncodingException e ) {
            throw new IllegalStateException( "huh" );
        }
    }



    @AfterClass
    public static void afterClass() {
        PathUtils.delete( play );
    }


    public Path getPathA() {
        return p.FS.getPath( p.nameStr[0] );
    }

    public Path getPathB() {
        return p.FS.getPath( p.nameStr[1] );
    }

    public Path getPathAB() {
        return p.FS.getPath( p.nameStr[0], p.nameStr[1] );
    }

    public Path getPathRAB() {
        return p.FS.getPath( p.nameStr[0], p.nameStr[1] ).toAbsolutePath();
    }

    public Path getPathBC() {
        return p.FS.getPath( p.nameStr[1], p.nameStr[2] );
    }

    public Path getPathABC() {
        return p.FS.getPath( p.nameStr[0], p.nameStr[1], p.nameStr[2] );
    }

    public Path getPathRABC() {
        return p.FS.getPath( p.nameStr[0], p.nameStr[1], p.nameStr[2] ).toAbsolutePath();
    }

    public Path getPathPA() throws IOException {
        return emptyDir().resolve( p.nameStr[0] );
    }

    public Path getPathPAe() throws IOException {
        Path ret = emptyDir().resolve( p.nameStr[0] );
        Files.write(ret, CONTENT);
        return ret;
    }

    public Path getPathPB() throws IOException {
        return emptyDir().resolve( p.nameStr[1] );
    }

    // P exists (freshly), A and B not
    public Path getPathPAB() throws IOException {
        return emptyDir().resolve( p.nameStr[0] ).resolve( p.nameStr[1] );
    }

    public Path getPathPC() throws IOException {
        return emptyDir().resolve( p.nameStr[3] );
    }

    public Path getPathPCD() throws IOException {
        return emptyDir().resolve( p.nameStr[3] ).resolve( p.nameStr[4] );
    }

    public Path getRoot() {
        return p.FS.getPath( "" ).toAbsolutePath().getRoot();
    }

    public Path getDefaultPath() {
        return p.FS.getPath( "" );
    }

    public Path getName( int i ) {
        return p.FS.getPath( p.nameStr[i] );
    }

    public String getSeparator() {
        return p.FS.getSeparator();
    }

    public Path nonEmptyDir() throws IOException {
        return p.getNonEmpty( testMethodName.getMethodName(), 4 );
    }

    public Path emptyDir() throws IOException {
        return p.getNonEmpty( testMethodName.getMethodName(), 0 );
    }



    @Test
    public void testAAA0() {
        assertThat( "set the test params in constructor", p, notNullValue());
    }

}
