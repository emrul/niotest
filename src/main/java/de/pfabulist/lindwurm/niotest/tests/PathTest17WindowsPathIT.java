package de.pfabulist.lindwurm.niotest.tests;

import de.pfabulist.lindwurm.niotest.matcher.PathExists;
import org.junit.Test;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.*;

import static de.pfabulist.kleinod.text.Strings.getBytes;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assume.assumeThat;

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
public abstract class PathTest17WindowsPathIT extends PathTest16PosixIT {

    @Test
    public void testCaseIgnorantPath() {
        assumeThat( nameStrCase == null, is(false));

        for ( int i = 0; i < 10; i++ ) {
            assertThat(getPathA().resolve(nameStr[i]), is( getPathA().resolve(nameStrCase[i])));
        }
    }

    @Test
    public void testHashCodeMixedCase() throws IOException {
        assumeThat( nameStrCase == null, is(false));

        assertThat( getPathPA().resolve(nameStrCase[1]).hashCode(), is( getPathPAB().hashCode()));
    }

    @Test
    public void testExistenceMixedCase() throws IOException {
        assumeThat( nameStrCase == null, is(false));
        getPathPABf();

        assertThat(Files.exists(getPathPA().resolve(nameStrCase[1])), is(true));
    }

    @Test
    public void testOverwriteMixedCase() throws IOException {
        assumeThat( nameStrCase == null, is(false));
        getPathPABf();
        Files.write(getPathPA().resolve(nameStrCase[1]), CONTENT_OTHER);

        assertThat( Files.readAllBytes(getPathPAB()), is(CONTENT_OTHER));
    }


    @Test
    public void testRememberedPath() throws IOException {
        assumeThat( nameStrCase == null, is(false));

        getPathPAd();
        Files.write(getPathPA().resolve(nameStrCase[1]), CONTENT);

        try( DirectoryStream<Path> dstr = Files.newDirectoryStream(getPathPA())) {
            Path kid = dstr.iterator().next();
            assertThat( kid, is( getPathPAB()));
            assertThat( kid.toString(), not( is(getPathPAB().toString())));
        }
    }

    @Test
    public void testOverwriteDoesNotOverwriteRememberedName() throws IOException {
        assumeThat( nameStrCase == null, is(false));
        getPathPABf();
        Files.write(getPathPA().resolve(nameStrCase[1]), CONTENT_OTHER );

        try( DirectoryStream<Path> dstr = Files.newDirectoryStream(getPathPA())) {
            Path kid = dstr.iterator().next();
            assertThat( kid, is( getPathPAB()));
            assertThat( kid.toString(), is(getPathPAB().toString()));
        }
    }

    @Test
    public void testWindowsBase() {
        assertThat( FS.getPath( "C:\\").isAbsolute(), is(true));
    }

    @Test
    public void testWindowsJustRootComponentIsRelative() {
        assertThat( FS.getPath( "C:").isAbsolute(), is(false));
    }

    @Test
    public void testWindowsRelativeWithRootComponent() {
        assertThat( FS.getPath( "C:foo").isAbsolute(), is(false));
    }

    @Test
    public void testWindowsFilenameOfPathWithRootComponentHasNoRootComponent() {
        assertThat( FS.getPath( "C:\\foo\\duh").getFileName() , is(FS.getPath("duh")));
    }

    @Test
    public void testWindowsToAbsoluteWithoutRootComponent() {
        assertThat( FS.getPath( "C:\\foo\\duh").getFileName() , is(FS.getPath("duh")));
    }

    @Test
    public void testWindowsDifferentRootComponentResolvesToArgument() {
        assertThat(FS.getPath("C:\\foo").resolve("D:duh"), is(FS.getPath("D:duh")));
    }

    @Test
    public void testWindowsSameRootComponentResolves() {
        assertThat(FS.getPath("C:\\foo").resolve("C:duh"), is(FS.getPath("C:\\foo\\duh")));
    }

    @Test
    public void testWindowsImpliedRootComponentResolvesNot() {
        assertThat(FS.getPath("\\foo").resolve("C:duh"), is(FS.getPath("C:duh")));
    }

    @Test
    public void testWindowsNoRootComponentResolves() {
        assertThat(FS.getPath("C:\\foo").resolve("duh"), is(FS.getPath("C:\\foo\\duh")));
    }

    @Test
    public void testWindowsCaseDrive() {
        assertThat( FS.getPath("X:\\foo"), is(FS.getPath("x:\\foo")));
    }

    @Test
    public void testWindowsRootOfDefaultIsC() {
        assertThat( FS.getPath( "").toAbsolutePath().getRoot(), is(FS.getPath("C:\\")));
    }

    // todo setup with 2 exsiting paths in different compo
//    @Test
//    public void testWindowsIsSAmeFileDifferentRootComponent() throws IOException {
//        assertThat( FS.provider().isSameFile( FS.getPath( "C:\\foo"), FS.getPath("D:\\foo")), is(false));
//    }

    @Test
    public void testWindowsGetFileNameHasNoRootComponent() {
        assertThat( FS.getPath( "C:\\foo").getFileName().toString().startsWith("C:"), is(false));
    }


    @Test
    public void testWindowsNoRootComponentGetRootHasNoRootComponent() {
        assertThat(FS.getPath("\\foo").getRoot(), is(FS.getPath("\\")));
    }

    @Test
    public void testWindowsImpliedRootComponentPathExistsOnC() throws IOException {
        assertThat( FS.getPath( getPathPABf().toString().substring(2)), PathExists.exists());
    }

    @Test
    public void testWindowsNormalizeDoesNotAddC() throws IOException {

        Path one = getPathPABf();
        Path two = FS.getPath( one.toString().substring(2));

        assertThat( two.normalize().toString().startsWith("C:"), is(false));
    }

    @Test
    public void testWindowsToRealPathAddsC() throws IOException {

        Path one = getPathPABf();
        Path two = FS.getPath( one.toString().substring(2));

        assertThat( two.toRealPath().toString().startsWith("C:"), is(true));
    }

    @Test
    public void testWindowsIsSameFileShowsImpliedRootComponentIsC() throws IOException {

        Path one = getPathPABf();
        Path two = FS.getPath( one.toString().substring(2));

        assertThat( FS.provider().isSameFile( one, two ), is(true));
    }

    @Test
    public void testWindowsRealFileOfImpliedIsWithC() throws IOException {

        Path one = getPathPABf();
        Path two = FS.getPath( one.toString().substring(2));

        assertThat( two.toRealPath(), is(one));
    }

    @Test
    public void testWindowsForwardIsBackwardSlash() throws IOException {
        assertThat( FS.getPath("C:/"), is( FS.getPath("C:\\")));
    }

    @Test( expected = InvalidPathException.class )
    public void testWindowsdNoSuchDrive() throws IOException {
        FS.getPath("5:");
    }


    @Test
    public void testWindowsUNC1() throws IOException {
        assertThat(FS.getPath("\\\\mach\\foo\\ho").getNameCount(), is(1));
    }

    @Test( expected = InvalidPathException.class )
    public void testWindowsUNCNoHostName() throws IOException {
        FS.getPath("\\\\");
    }

    @Test( expected = InvalidPathException.class )
    public void testWindowsdUNCNoShareName() throws IOException {
        FS.getPath("\\\\localhost");
    }

    @Test
    public void testWindowsdUNCPlenty() throws IOException {
        FS.getPath( "\\//////\\\\localhost\\////foo");
    }


    @Test
    public void testWindowsUNCSlash() throws IOException {
        assertThat(FS.getPath("//mach/foo/ho").getNameCount(), is(1));
    }

    @Test
    public void testWindowsUNCAbsolute() throws IOException {
        assertThat(FS.getPath("\\\\mach\\foo").isAbsolute(), is(true));
    }

    @Test
    public void testWindowsUNCAbsolute2() throws IOException {
        assertThat(FS.getPath("\\\\mach\\C$").isAbsolute(), is(true));
    }

    @Test
    public void testWindowsUNCRoot() throws IOException {
        assertThat(FS.getPath("\\\\mach\\C$").getRoot(), is(FS.getPath("\\\\mach\\C$\\")));
    }

    // TODO check whether UNC is case sensitive
//    @Test
//    public void testWindowsUNCCase() throws IOException {
////        assertThat(FS.getPath("\\\\duH\\C$").getRoot(), is(FS.getPath("\\\\duh\\C$\\")));
//    }


    @Test
    public void testWindowsIllegalChars() {
        String ill = "?<>:*|\"";

        boolean thrown = false;
        for ( int i = 0; i < ill.length(); i++ ) {
            try {
                getPathAB().resolve( "a" + ill.charAt(i));
            } catch (Exception e) {
                thrown = true;
            }

            assertThat( "illegal char " + ill.charAt(i) + " did not throw" , thrown, is(true));
        }
    }


//    @Test
//    public void testWindowsMaxLenOfPathElementWorks() throws IOException {
//        String el = "";
//        for ( int i = 0; i < 25; i++ ) {
//            el += "0123456789";
//        }
//
//        String ok = el + "12345";
//        Files.write( getPathPAd().resolve( ok ), CONTENT);
//     }
//
//    @Test
//    public void testWindowsMaxLenOfPathElementWorksHasNoEffectOfPathConstruction() throws IOException {
//        String tooLong = "";
//        for ( int i = 0; i < 45; i++ ) {
//            tooLong += "0123456789";
//        }
//
//        getPathPAd().resolve( tooLong );
//    }
//
//    @Test( expected = FileSystemException.class )
//    public void testWindowsPathElementTooLong() throws IOException {
//        String el = "";
//        for ( int i = 0; i < 25; i++ ) {
//            el += "0123456789";
//        }
//
//        String ok = el + "12345";
//        String notOk = ok + "H";
//        Files.write( getPathPAd().resolve( notOk ), CONTENT);
//    }

    // todo max path length

    @Test
    public void testWindowsUNCToLocal() throws IOException {
        assumeThat( capabilities.canSeeLocalUNCShares(FS), is(true));

        Path file = getPathPAf();
        Path unc = FS.getPath( "\\\\localhost\\C$" + file.toString().substring(2));

        assertThat( Files.readAllBytes( unc ), is( CONTENT ));

    }

    @Test( expected = FileSystemException.class )
    public void testWindowsNul() throws IOException {
        Files.write( getPathPAd().resolve("nul"), CONTENT);
    }


//    @Test
//    public void testWindowsFoo() {
//        System.out.println(FS.getPath("$Upcase"));
//        System.out.println(Files.exists(FS.getPath("C:\\$Upcase")));
//    }

    @Test
    public void testWindowsDeleteIfEsdfxistsRecreate() throws IOException {
        Path path = getPathPAB();
        Files.createDirectories( path.getParent());
        try ( SeekableByteChannel ch = Files.newByteChannel(path, StandardOpenOption.WRITE, StandardOpenOption.READ, StandardOpenOption.CREATE )) {
            ch.write( ByteBuffer.wrap(getBytes("hallo")));
            Files.delete(path);
            ch.write( ByteBuffer.wrap( getBytes("duh")));
        }

        // and no throw
        assertThat(Files.exists( path ), is(false));
    }

    // todo stackoverflow
//    @Test( expected = AccessDeniedException.class)
//    public void testWindowsDeleteIfExistsRecreate() throws IOException {
//        Path path = getPathPAB();
//        Files.createDirectories( path.getParent());
//        SeekableByteChannel ch = Files.newByteChannel(path, StandardOpenOption.WRITE, StandardOpenOption.READ, StandardOpenOption.CREATE );
//        ch.write( ByteBuffer.wrap(getBytes("hallo")));
//        Files.delete(path);
//
//        Files.exists( path );
//    }


    // TODO machine name \\machinename\C$


    // no long unc in default
//    @Test
//    public void testFuh() {
//        FS.getPath("\\\\?\\UNC\\localhosr\\C$"));
//    }




//    @Test
//    public void testWindowsMovenOpenFile() throws IOException {
//        Path file = getPathPABf();
//        Path tgt = getPathPBd();
//
//        SeekableByteChannel bb = Files.newByteChannel(file, StandardOpenOption.READ, StandardOpenOption.WRITE );
//        bb.read(ByteBuffer.allocate(100));
//
//        Files.readAllBytes(file);
//
//        Files.move(file, tgt.resolve(nameStr[1]));
//
////        Files.write( file, CONTENT_OTHER);
//
//        Files.readAllBytes(file);
//
//
//
//        bb.close();
//
//    }


}
