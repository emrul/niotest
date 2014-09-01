package org.opencage.lindwurm.niotest.tests;

import org.junit.*;
import org.junit.rules.TestName;
import org.opencage.kleinod.paths.PathUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.nio.file.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assume.assumeThat;

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

    private Path play;
    private Boolean dontDelete;

    protected static byte[] CONTENT;
    protected static byte[] CONTENT_OTHER;
    protected static byte[] CONTENT20k;
    protected static byte[] CONTENT50;

    public FileSystem FS;
    public static String[]   nameStr = {"aaa", "bbb", "ccc", "ddd", "eee", "fff", "ggg", "hhh", "iii", "jjj", "kkk"};
    private int       kidCount;

    protected Map<String, String> notSupported = new HashMap<>();

    public FSCapabilities capabilities;

    protected static OpenOption[] standardOpen = { StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE };


    @Rule
    public TestName testMethodName = new TestName();


    @BeforeClass
    public static void beforeClass3() {
        try {
            CONTENT = "hi there".getBytes( "UTF-8" );
            CONTENT_OTHER = "what's up, huh, huh".getBytes( "UTF-8" );

            CONTENT20k = new byte[20000];
            for ( int i = 0; i < 20000; i++ ) {
                CONTENT20k[i] = (byte) (i);
            }

            CONTENT50 = new byte[50];
            for ( int i = 0; i < 50; i++ ) {
                CONTENT50[i] = (byte) (i);
            }
        } catch( UnsupportedEncodingException e ) {
            throw new IllegalStateException( "huh" );
        }
    }

    @Before
    public void filter() {
        assumeThat( message(), possible(), is(testMethodName.getMethodName().startsWith("test")) );
    }



    // TODO
//    @AfterClass
//    public static void afterClass() {
//        if ( dontDelete != null ) {
//            PathUtils.delete( play );
//        }
//    }

    protected String message() {
        String ret = notSupported.get( testMethodName.getMethodName() );
        if ( ret != null ) {
            return ret;
        }

        return "";
    }

    protected boolean possible() {
        return !notSupported.containsKey( testMethodName.getMethodName() );
    }


    public Path getNonEmpty( String name, int kidCount ) throws IOException {

        this.kidCount = kidCount;
        Path base = play.resolve( name );
        Files.createDirectories( base );

        for ( int i = 0; i < kidCount; i++ ) {
            Files.createDirectory( base.resolve( "kid-" + i ) );
        }

        return base;
    }

    public int getKidCount() {
        return kidCount;
    }



    public Path getPathA() {
        return FS.getPath( nameStr[0] );
    }

    public Path getPathB() {
        return FS.getPath( nameStr[1] );
    }

    public Path getPathAB() {
        return FS.getPath( nameStr[0], nameStr[1] );
    }

    public Path getPathRAB() {
        return FS.getPath( nameStr[0], nameStr[1] ).toAbsolutePath();
    }

    public Path getPathBC() {
        return FS.getPath( nameStr[1], nameStr[2] );
    }

    public Path getPathABC() {
        return FS.getPath( nameStr[0], nameStr[1], nameStr[2] );
    }

    public Path getPathRABC() {
        return FS.getPath( nameStr[0], nameStr[1], nameStr[2] ).toAbsolutePath();
    }

    public Path getPathP() throws IOException {
        return emptyDir();
    }

    public Path getPathPA() throws IOException {
        return emptyDir().resolve( nameStr[0] );
    }

    public Path getPathPAC() throws IOException {
        return emptyDir().resolve( nameStr[0] ).resolve(nameStr[2]);
    }

    public Path getPathPAf() throws IOException {
        Path ret = emptyDir().resolve( nameStr[0] );
        Files.write(ret, CONTENT, standardOpen );
        capabilities.shake().run(); // NOSONAR
        return ret;
    }

    public Path getPathPAd() throws IOException {
        Path ret = emptyDir().resolve( nameStr[0] );
        Files.createDirectory( ret );
        return ret;
    }

    public Path getPathPBf() throws IOException {
        Path ret = emptyDir().resolve( nameStr[1] );
        Files.write(ret, CONTENT, standardOpen );
        return ret;
    }



    public Path getPathPBd() throws IOException {
        Path ret = emptyDir().resolve( nameStr[1] );
        Files.createDirectory(ret);
        return ret;
    }

    public Path getPathPCf() throws IOException {
        Path ret = emptyDir().resolve( nameStr[2] );
        Files.write(ret, CONTENT, standardOpen );
        return ret;
    }


    public Path getPathPABf() throws IOException {
        Path ret = getPathPAB();
        Files.createDirectories( ret.getParent() );
        Files.write(ret, CONTENT, standardOpen );
        return ret;
    }



    public Path getPathPACf() throws IOException {
        Path ret = getPathPAC();
        Files.createDirectories( ret.getParent() );
        Files.write(ret, CONTENT, standardOpen );
        return ret;
    }

    public Path getPathPADf() throws IOException {
        Path ret = getPathPA().resolve( nameStr[3]);
        Files.createDirectories( ret.getParent() );
        Files.write(ret, CONTENT, standardOpen );
        return ret;
    }

    public Path getPathPBCf() throws IOException {
        Path ret = getPathPB().resolve(nameStr[2]);
        Files.createDirectories( ret.getParent() );
        Files.write(ret, CONTENT, standardOpen );
        return ret;
    }

    public Path getPathPABr() throws IOException {
        return getDefaultPath().toAbsolutePath().relativize(getPathPAB());
    }

    public Path getPathPAu() throws IOException {
        return getPathPA().getParent().resolve(nameStr[2]).resolve("..").resolve( nameStr[0] );
    }

    public Path getPathPABu() throws IOException {
        return getPathPAB().getParent().resolve( ".." ).resolve( nameStr[0] ).resolve( nameStr[1] );
    }

    public Path getPathPB() throws IOException {
        return emptyDir().resolve( nameStr[1] );
    }

    public Path getPathPBB() throws IOException {
        return emptyDir().resolve( nameStr[1] ).resolve(nameStr[1]);
    }

    // P exists (freshly), A and B not
    public Path getPathPAB() throws IOException {
        return emptyDir().resolve( nameStr[0] ).resolve( nameStr[1] );
    }

    public Path getPathPC() throws IOException {
        return emptyDir().resolve( nameStr[3] );
    }

    public Path getPathPCD() throws IOException {
        return emptyDir().resolve( nameStr[3] ).resolve( nameStr[4] );
    }

    public Path getRoot() {
        return FS.getPath( "" ).toAbsolutePath().getRoot();
    }

    public Path getDefaultPath() {
        return FS.getPath( "" );
    }

    public Path getName( int i ) {
        return FS.getPath( nameStr[i] );
    }

    public String getSeparator() {
        return FS.getSeparator();
    }

    public Path nonEmptyDir() throws IOException {
        return getNonEmpty( testMethodName.getMethodName(), 4 );
    }

    public Path emptyDir() throws IOException {
        return getNonEmpty( testMethodName.getMethodName(), 0 );
    }

    @Test
    public void testAAA0() {
        assertThat( "set FS to the FileSystem to test", FS, notNullValue());
    }

    public void setPlay( Path play ) {
        this.play = play;
        this.FS = play.getFileSystem();
    }

    public Path getPlay() {
        return play;
    }

//    public static void setDontDelete() {
//        Setup.dontDelete = true;
//    }
//
//

    public Path getOther() throws IOException {
        if ( FS.equals(FileSystems.getDefault())) {
            return PathUtils.getOrCreate(URI.create("null:/"), Collections.EMPTY_MAP).getPath("/");
        }

        return FileSystems.getDefault().getPath("foo");
    }

//    protected static void set2ndPlay(Path play2) {
//        Setup.play2 = play2;
//    }


}
