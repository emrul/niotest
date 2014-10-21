package org.opencage.lindwurm.niotest.tests;

import org.junit.Test;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.*;
import java.security.InvalidParameterException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assume.assumeThat;
import static org.opencage.lindwurm.niotest.matcher.PathExists.exists;

/**
 * Created by spfab on 06.10.2014.
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
        assertThat( FS.getPath( getPathPABf().toString().substring(2)), exists());
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

//    @Test
//    public void testWindowsUNCCase() throws IOException {
//        System.out.println(FS.getPath("\\\\duH\\C$"));
////        assertThat(FS.getPath("\\\\duH\\C$").getRoot(), is(FS.getPath("\\\\mach\\C$\\")));
//    }




    @Test
    public void testWindowsUNCToLocal() throws IOException {
        assumeThat( capabilities.canSeeLocalUNCShares(FS), is(true));

        Path file = getPathPAf();
        Path unc = FS.getPath( "\\\\localhost\\C$" + file.toString().substring(2));

        System.out.println(unc);

        assertThat( Files.readAllBytes( unc ), is( CONTENT ));

        try( DirectoryStream<Path> st = Files.newDirectoryStream(unc.getParent().getParent().getParent())) {
            for ( Path kid : st ) {
                System.out.println(kid);
            }
        }
    }

   // TODO machine name \\machiname\C$


    // no long unc in default
//    @Test
//    public void testFuh() {
//        System.out.println(FS.getPath("\\\\?\\UNC\\localhosr\\C$"));
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
