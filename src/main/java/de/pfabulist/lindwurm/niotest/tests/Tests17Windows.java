package de.pfabulist.lindwurm.niotest.tests;

import de.pfabulist.lindwurm.niotest.tests.topics.*;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.attribute.DosFileAttributeView;

import static de.pfabulist.kleinod.nio.PathIKWID.childGetParent;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Fail.fail;

/**
 * ** BEGIN LICENSE BLOCK *****
 * BSD License (2 clause)
 * Copyright (c) 2006 - 2015, Stephan Pfab
 * All rights reserved.
 * <p>
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * <p>
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
@SuppressWarnings( { "PMD.ExcessivePublicCount", "PMD.TooManyMethods" } )
public abstract class Tests17Windows extends Tests16Unix {

    public Tests17Windows( FSDescription capa ) {
        super( capa );
    }

    @Test
    @Category( Windows.class )
    public void testCaseIgnorantPathsAreEqual() {
        assertThat( absD().resolve( nameB() ) ).isEqualTo( mixCase( absD().resolve( nameB() ) ) );
    }

    @Test
    @Category( Windows.class )
    public void testCaseIgnorantPathHaveSameHashCode() throws IOException {
        assertThat( absD().resolve( nameB() ).hashCode() ).isEqualTo( mixCase( absD().resolve( nameB() ) ).hashCode() );
    }

    @Test
    @Category( Windows.class )
    public void testCaseIgnorantPathCompareTo0() throws IOException {
        assertThat( absD().resolve( nameB() ).compareTo( mixCase( absD().resolve( nameB() ) ) ) ).isEqualTo( 0 );
    }

    @Test
    @Category( Windows.class )
    public void testCaseIgnorantPathKeepCompareSaneGreater() throws IOException {
        assertThat( absABC().compareTo( mixCase( absAB()))).isGreaterThan( 0 );
    }

    @Test
    @Category( Windows.class )
    public void testCaseIgnorantPathKeepCompareSaneSmaller() throws IOException {
        assertThat( absAB().compareTo( mixCase( absABC()))).isLessThan( 0 );
    }

//    @Test @Category( Windows.class )
//    public void testExistenceMixedCase() throws IOException {
//        assumeThat( nameStrCase == null).isEqualTo(false));
//        getPathPABf();
//
//        assertThat(Files.exists(getPathPA().resolve(nameStrCase[1]))).isEqualTo(true));
//    }
//

    @Test
    @Category( Windows.class )
    public void testCaseRemembering() throws IOException {

        Path file = dirTA().resolve( nameD() );

        // create file where last filename is mixed case
        Files.write( mixCase( file ), CONTENT );

        try( DirectoryStream<Path> dstr = Files.newDirectoryStream( childGetParent( file ) ) ) {
            Path kid = dstr.iterator().next();
            assertThat( kid ).isEqualTo( file );
            assertThat( kid.toString() ).isNotEqualTo( file.toString() );
        }
    }

    @Test
    @Category( Windows.class )
    public void testWindowsBase() {
        assertThat( FS.getPath( "C:\\" ).isAbsolute() ).isTrue();
    }

    @Test
    @Category( { Windows.class, RootComponent.class } )
    public void testWindowsJustRootComponentIsRelative() {
        assertThat( FS.getPath( "C:" ).isAbsolute() ).isFalse();
    }

    @Test
    @Category( { Windows.class, RootComponent.class } )
    public void testWindowsRelativeWithRootComponent() {
        assertThat( FS.getPath( "C:foo" ).isAbsolute() ).isFalse();
    }

    @Test
    @Category( { Windows.class, RootComponent.class } )
    public void testWindowsFilenameOfPathWithRootComponentHasNoRootComponent() {
        assertThat( FS.getPath( "C:\\foo\\duh" ).getFileName() ).isEqualTo( FS.getPath( "duh" ) );
    }

    @Test
    @Category( { Windows.class, RootComponent.class } )
    public void testWindowsToAbsoluteWithoutRootComponent() {
        assertThat( FS.getPath( "C:\\foo\\duh" ).getFileName() ).isEqualTo( FS.getPath( "duh" ) );
    }

    @Test
    @Category( { Windows.class, RootComponent.class } )
    public void testWindowsDifferentRootComponentResolvesToArgument() {
        assertThat( FS.getPath( "C:\\foo" ).resolve( "D:duh" ) ).isEqualTo( FS.getPath( "D:duh" ) );
    }

    @Test
    @Category( { Windows.class, RootComponent.class } )
    public void testWindowsSameRootComponentResolves() {
        assertThat( FS.getPath( "C:\\foo" ).resolve( "C:duh" ) ).isEqualTo( FS.getPath( "C:\\foo\\duh" ) );
    }

    @Test
    @Category( { Windows.class, RootComponent.class } )
    public void testWindowsImpliedRootComponentResolvesNot() {
        assertThat( FS.getPath( "\\foo" ).resolve( "C:duh" ) ).isEqualTo( FS.getPath( "C:duh" ) );
    }

    @Test
    @Category( { Windows.class, RootComponent.class } )
    public void testWindowsNoRootComponentResolves() {
        assertThat( FS.getPath( "C:\\foo" ).resolve( "duh" ) ).isEqualTo( FS.getPath( "C:\\foo\\duh" ) );
    }

    @Test
    @Category( Windows.class )
    public void testWindowsCaseDrive() {
        assertThat( FS.getPath( "X:\\foo" ) ).isEqualTo( FS.getPath( "x:\\foo" ) );
    }

    // oops wrong: default on windows can be on different drive
//    @Test
//    @Category( Windows.class )
//    public void testWindowsRootOfDefaultIsC() {
//        assertThat( FS.getPath( "" ).toAbsolutePath().absoluteGetRoot()).isEqualTo( FS.getPath( "C:\\" ) ) );
//    }

    //
//    // todo setup with 2 exsiting paths in different compo
////    @Test @Category( Windows.class )
////    public void testWindowsIsSAmeFileDifferentRootComponent() throws IOException {
////        assertThat( FS.provider().isSameFile( FS.getPath( "C:\\foo"), FS.getPath("D:\\foo"))).isEqualTo(false));
////    }
//
    @Test
    @Category( { Windows.class, RootComponent.class } )
    public void testWindowsGetFileNameHasNoRootComponent() {
        assertThat( FS.getPath( "C:\\foo" ).getFileName().toString().startsWith( "C:" ) ).isFalse();
    }

    @Test
    @Category( { Windows.class, RootComponent.class } )
    public void testWindowsNoRootComponentGetRootHasNoRootComponent() {
        assertThat( FS.getPath( "\\foo" ).getRoot() ).isEqualTo( FS.getPath( "\\" ) );
    }

    @Test
    @Category( { Windows.class, DosAttributesT.class, Writable.class } )
    public void testWindowsIsHidden() throws IOException {
        assertThat( Files.isHidden( fileTA() ) ).isFalse();

        Files.getFileAttributeView( absTA(), DosFileAttributeView.class ).setHidden( true );
        assertThat( Files.isHidden( fileTA() ) ).isTrue();

        Files.setAttribute( absTA(), "dos:hidden", false );
        assertThat( Files.isHidden( fileTA() ) ).isFalse();
    }

    // TODO defaultfs bugs
//    @Test @Category( Windows.class )
//    public void testWindowsNoRootComasdsadponentGetRootHasNoRootComponent() {
//        System.out.println(absAB().resolve("\\foo"));
//        System.out.println( FS.getPath("\\foo").absoluteGetRoot());
//        System.out.println( FS.getPath("\\foo").absoluteGetRoot().resolve("huh").isAbsolute());
//        System.out.println( FS.getPath( "\\foo" ).toAbsolutePath());
//        assertThat(FS.getPath("\\foo").isAbsolute()).isEqualTo(true));
//        assertThat(FS.getPath("\\foo").isAbsolute()).isEqualTo(true));
//    }

    //  todo
//    @Test @Category( Windows.class )
//    public void testWindowsImpliedRootComponentPathExistsOnC() throws IOException {
//        assertThat( FS.getPath( getPathPABf().toString().substring(2)), exists());
//    }

    //    @Test @Category( Windows.class )
//    public void testWindowsNormalizeDoesNotAddC() throws IOException {
//
//        Path one = getPathPABf();
//        Path two = FS.getPath( one.toString().substring(2));
//
//        assertThat( two.normalize().toString().startsWith("C:")).isEqualTo(false));
//    }
//
//    @Test @Category( Windows.class )
//    public void testWindowsToRealPathAddsC() throws IOException {
//
//        Path one = getPathPABf();
//        Path two = FS.getPath( one.toString().substring(2));
//
//        assertThat( two.toRealPath().toString().startsWith("C:")).isEqualTo(true));
//    }
//
//    @Test @Category( Windows.class )
//    public void testWindowsIsSameFileShowsImpliedRootComponentIsC() throws IOException {
//
//        Path one = getPathPABf();
//        Path two = FS.getPath( one.toString().substring(2));
//
//        assertThat( FS.provider().isSameFile( one, two )).isEqualTo(true));
//    }
//
//    @Test @Category( Windows.class )
//    public void testWindowsRealFileOfImpliedIsWithC() throws IOException {
//
//        Path one = getPathPABf();
//        Path two = FS.getPath( one.toString().substring(2));
//
//        assertThat( two.toRealPath()).isEqualTo(one));
//    }
//
//    @Test @Category( Windows.class )
//    public void testWindowsForwardIsBackwardSlash() throws IOException {
//        assertThat( FS.getPath("C:/")).isEqualTo( FS.getPath("C:\\")));
//    }
//
//    @Test @Category( Windows.class )( expected = InvalidPathException.class )
//    public void testWindowsdNoSuchDrive() throws IOException {
//        FS.getPath("5:");
//    }
//
    @Test
    @Category( { Windows.class, UNC.class } )
    public void testWindowsUNC1() throws IOException {
        assertThat( FS.getPath( "\\\\mach\\foo\\ho" ).getNameCount() ).isEqualTo( 1 );
    }

    @Test( expected = InvalidPathException.class )
    @Category( Windows.class )
    public void testWindowsUNCNoHostName() throws IOException {
        FS.getPath( "\\\\" );
    }

    @Test( expected = InvalidPathException.class )
    @Category( Windows.class )
    public void testWindowsdUNCNoShareName() throws IOException {
        FS.getPath( "\\\\localhost" );
    }

    @Test
    @Category( { Windows.class, UNC.class } )
    public void testWindowsdUNCPlenty() throws IOException {
        try {
            FS.getPath( "\\//////\\\\localhost\\////foo" );
        } catch( InvalidPathException e ) {
            fail( "UNC paths are valid in Windows" );
        }
    }

    @Test
    @Category( { Windows.class, UNC.class } )
    public void testWindowsUNCSlash() throws IOException {
        assertThat( FS.getPath( "//mach/foo/ho" ).getNameCount() ).isEqualTo( 1 );
    }

    @Test
    @Category( { Windows.class, UNC.class } )
    public void testWindowsUNCAbsolute() throws IOException {
        assertThat( FS.getPath( "\\\\mach\\foo" ).isAbsolute() ).isTrue();
    }

    @Test
    @Category( { Windows.class, UNC.class } )
    public void testWindowsUNCAbsolute2() throws IOException {
        assertThat( FS.getPath( "\\\\mach\\C$" ).isAbsolute() ).isTrue();
    }

    @Test
    @Category( { Windows.class, UNC.class } )
    public void testWindowsUNCRoot() throws IOException {
        assertThat( FS.getPath( "\\\\mach\\C$" ).getRoot() ).isEqualTo( FS.getPath( "\\\\mach\\C$\\" ) );
    }
//
//    // TODO check whether UNC is case sensitive
////    @Test @Category( Windows.class )
////    public void testWindowsUNCCase() throws IOException {
//////        assertThat(FS.getPath("\\\\duH\\C$").absoluteGetRoot()).isEqualTo(FS.getPath("\\\\duh\\C$\\")));
////    }
//
//
//    @Test @Category( Windows.class )
//    public void testWindowsIllegalChars() {
//        String ill = "?<>:*|\"";
//
//        boolean thrown = false;
//        for ( int i = 0; i < ill.length(); i++ ) {
//            try {
//                getPathAB().resolve( "a" + ill.charAt(i));
//            } catch (Exception e) {
//                thrown = true;
//            }
//
//            assertThat( "illegal char " + ill.charAt(i) + " did not throw" , thrown).isEqualTo(true));
//        }
//    }
//
//
////    @Test @Category( Windows.class )
////    public void testWindowsMaxLenOfPathElementWorks() throws IOException {
////        String el = "";
////        for ( int i = 0; i < 25; i++ ) {
////            el += "0123456789";
////        }
////
////        String ok = el + "12345";
////        Files.write( getPathPAd().resolve( ok ), CONTENT);
////     }
////
////    @Test @Category( Windows.class )
////    public void testWindowsMaxLenOfPathElementWorksHasNoEffectOfPathConstruction() throws IOException {
////        String tooLong = "";
////        for ( int i = 0; i < 45; i++ ) {
////            tooLong += "0123456789";
////        }
////
////        getPathPAd().resolve( tooLong );
////    }
////
////    @Test @Category( Windows.class )( expected = FileSystemException.class )
////    public void testWindowsPathElementTooLong() throws IOException {
////        String el = "";
////        for ( int i = 0; i < 25; i++ ) {
////            el += "0123456789";
////        }
////
////        String ok = el + "12345";
////        String notOk = ok + "H";
////        Files.write( getPathPAd().resolve( notOk ), CONTENT);
////    }
//
//    // todo max path length
//
//    @Test @Category( Windows.class )
//    public void testWindowsUNCToLocal() throws IOException {
//        assumeThat( capabilities.canSeeLocalUNCShares(FS)).isEqualTo(true));
//
//        Path file = getPathPAf();
//        Path unc = FS.getPath( "\\\\localhost\\C$" + file.toString().substring(2));
//
//        assertThat( Files.readAllBytes( unc )).isEqualTo( CONTENT ));
//
//    }
//
//    @Test @Category( Windows.class )( expected = FileSystemException.class )
//    public void testWindowsNul() throws IOException {
//        Files.write( getPathPAd().resolve("nul"), CONTENT);
//    }
//
//
////    @Test @Category( Windows.class )
////    public void testWindowsFoo() {
////        System.out.println(FS.getPath("$Upcase"));
////        System.out.println(Files.exists(FS.getPath("C:\\$Upcase")));
////    }
//
//    @Test @Category( Windows.class )
//    public void testWindowsDeleteIfEsdfxistsRecreate() throws IOException {
//        Path path = getPathPAB();
//        Files.createDirectories( path.getParent());
//        try ( SeekableByteChannel ch = Files.newByteChannel(path, StandardOpenOption.WRITE, StandardOpenOption.READ, StandardOpenOption.CREATE )) {
//            ch.write( ByteBuffer.wrap(getBytes("hallo")));
//            Files.delete(path);
//            ch.write( ByteBuffer.wrap( getBytes("duh")));
//        }
//
//        // and no throw
//        assertThat(Files.exists( path )).isEqualTo(false));
//    }
//
//    // todo stackoverflow
////    @Test @Category( Windows.class )( expected = AccessDeniedException.class)
////    public void testWindowsDeleteIfExistsRecreate() throws IOException {
////        Path path = getPathPAB();
////        Files.createDirectories( path.getParent());
////        SeekableByteChannel ch = Files.newByteChannel(path, StandardOpenOption.WRITE, StandardOpenOption.READ, StandardOpenOption.CREATE );
////        ch.write( ByteBuffer.wrap(getBytes("hallo")));
////        Files.delete(path);
////
////        Files.exists( path );
////    }
//
//
//    // TODO machine name \\machinename\C$
//
//
//    // no long unc in default
////    @Test @Category( Windows.class )
////    public void testFuh() {
////        FS.getPath("\\\\?\\UNC\\localhosr\\C$"));
////    }
//
//
//
//
////    @Test @Category( Windows.class )
////    public void testWindowsMovenOpenFile() throws IOException {
////        Path file = getPathPABf();
////        Path tgt = getPathPBd();
////
////        SeekableByteChannel bb = Files.newByteChannel(file, StandardOpenOption.READ, StandardOpenOption.WRITE );
////        bb.read(ByteBuffer.allocate(100));
////
////        Files.readAllBytes(file);
////
////        Files.move(file, tgt.resolve(nameStr[1]));
////
//////        Files.write( file, CONTENT_OTHER);
////
////        Files.readAllBytes(file);
////
////
////
////        bb.close();
////
////    }

    /*
     * ------------------------------------------------------------------
     */

}
