package de.pfabulist.lindwurm.niotest.tests;

import org.hamcrest.core.Is;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.*;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;
import static org.junit.Assume.assumeThat;
import static de.pfabulist.lindwurm.niotest.matcher.PathAbsolute.absolute;
import static de.pfabulist.lindwurm.niotest.matcher.PathExists.exists;

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
public abstract class PathTest10PathWithContentIT extends PathTest9WrongProviderIT {

    @Test
    public void testIsSameFileOnEqualPath() throws IOException {
        assertThat( Files.isSameFile( getPathPA(), getPathPA()), is( true ) );
    }

    @Test
    public void testIsSameFileOnEqualPathElementsDifferentProvider() throws IOException {
        assumeThat( FS, not( is( FileSystems.getDefault() )));

        assertThat( Files.isSameFile( getPathA(), FileSystems.getDefault().getPath( nameStr[0] )), is( false ) );
    }


    @Test
    public void testIsSameFileWithUnnormalizedPath() throws IOException {
        assertThat(FS.provider().isSameFile( getPathPABf(), getPathPABu()), is(true));
    }

    @Test
    public void testIsSameFileWithRelativePath() throws IOException {
        assertThat(FS.provider().isSameFile(getPathPABf(), getPathPABr()), is(true));
    }

    @Test
    public void testIsSameFileOfSameContentDifferentPathIsNot() throws IOException {

        assertThat(FS.provider().isSameFile(getPathPAf(), getPathPBf()), is(false));
    }

    @Test( expected = NoSuchFileException.class )
    public void testIsSameFileOfDifferentPathNonExistingFileThrows() throws IOException {
        FS.provider().isSameFile( getPathPABf(), getPathPB() );
    }

    @Test( expected = NoSuchFileException.class )
    public void testIsSameFileOfDifferentPathNonExistingFile2Throws() throws IOException {
        FS.provider().isSameFile( getPathPA(), getPathPBf() );
    }


    @Test
    public void testWriteUnnormalized() throws IOException {
        getPathPAd();
        Path read = getPathPAB();
        Path write = getPathPABu();

        Files.write( write, CONTENT, standardOpen );
        byte[] out = Files.readAllBytes( read );

        assertThat( out, Is.is( CONTENT ));
    }

    @Test
    public void testReadAttributesFromUnnormalizedPath() throws IOException {
        getPathPABf();
        Path read = getPathPABu();

        assertThat( Files.size( read ), is( 0L+CONTENT.length));
    }

    @Test
    public void testCheckAccessUnnormalizedPath() throws IOException {
        getPathPABf();
        Path read = getPathPABu();

        // expect no throw
        FS.provider().checkAccess(read);
    }

    @Test
    public void testCheckAccessRelativePath() throws IOException {
        getPathPABf();
        Path read = getPathPABr();

        // expect no throw
        FS.provider().checkAccess( read );
    }


    @Test
    public void testCheckAccessSupportesRead() throws IOException {
        // should not throw UnsupportedOperationException
        try {
            FS.provider().checkAccess( getPathPABf(), AccessMode.READ );
        } catch ( AccessDeniedException e ) {
            //
        }
    }

    @Test
    public void testCheckAccessSupportesWrite() throws IOException {
        // should not throw UnsupportedOperationException
        try {
            FS.provider().checkAccess( getPathPABf(), AccessMode.WRITE );
        } catch ( AccessDeniedException e ) {
            //
        }
    }

    @Test
    public void testCheckAccessSupportesExecute() throws IOException {
        // should not throw UnsupportedOperationException
        try {
            FS.provider().checkAccess( getPathPABf(), AccessMode.EXECUTE );
        } catch ( AccessDeniedException e ) {
            //
        }
    }

    @Test( expected = NoSuchFileException.class )
    public void testCheckAccessNonExistingFile() throws IOException {
        FS.provider().checkAccess( getPathPAB() );
    }

    @Test
    public void testCopyUnnormalizedPath() throws IOException {
        getPathPABf();
        FS.provider().copy( getPathPABu(), getPathPB());

        assertThat( Files.readAllBytes(getPathPAB()), is(CONTENT));
    }

    @Test
    public void testMoveUnnormalizedPath() throws IOException {
        getPathPABf();
        FS.provider().move(getPathPABu(), getPathPB());

        assertThat( Files.readAllBytes(getPathPB()), is(CONTENT));
    }

//    @Test
//    public void testCreateDirectoryUnnormalizedPath() throws IOException {
//        assumeThat( message(), possible(), is(true) );
//
//        FS.provider().createDirectory( getPathPAu());
//        assertThat(getPathPA(), exists());
//    }

    @Test
    public void testCreateDirectoryWithRelativePath() throws IOException {

        getPathPAd();
        Path rel = getPathPABr();

        Files.createDirectory( rel );
        assertThat( rel, exists() );
    }


//    @Test( expected = NoSuchFileException.class )
//    public void bugCreateDirectoryUnnormalizedPath() throws IOException {
//        assumeThat( message(), possible(), is(false) );
//
//        FS.provider().createDirectory(getPathPAu());
//    }

    @Test
    public void testDeleteUnnormalizedPath() throws IOException {
        Path file = getPathPABf();
        FS.provider().delete( getPathPABu());

        assertThat(file, not(exists()));
    }

    @Test
    public void testDeleteIfExistsUnnormalizedPath() throws IOException {
        Path file = getPathPABf();
        FS.provider().deleteIfExists(getPathPABu());

        assertThat(file, not(exists()));
    }

//    @Test
//    public void testGetFileStoreUnnormalizedPath() throws IOException {
//        FS.provider().getFileStore(getPathPABu());
//    }

//    @Test( expected = NoSuchFileException.class )
//    public void bugGetFileStoreUnnormalizedPath() throws IOException {
//        FS.provider().getFileStore(getPathPABu());
//    }

    @Test
    public void testIsHiddenUnnormalizedPath() throws IOException {
        Path file = getPathPABf();
        FS.provider().isHidden( file );
    }

    @Test
    public void testToRealpathResturnsAnAbsolutePath() throws Exception {
        getPathPABf();
        assertThat( getPathPABr().toRealPath(), absolute());
    }

    @Test
    public void testToRealpathResturnsAnNormalizedPath() throws Exception {
        getPathPABf();
        Path real = getPathPABu().toRealPath();
        assertThat( real.normalize(), is(real));
    }

    @Test
    public void testToRealpathIsSamePath() throws Exception {
        getPathPABf();
        Path path = getPathPABu();
        assertThat( FS.provider().isSameFile( path.toRealPath(), path ), is(true));
    }

    @Test( expected = NoSuchFileException.class )
    public void testToRealpathOfNonExistingFileThrows() throws Exception {
        getPathPAB().toRealPath();
    }


    @Test
    public void testMaxFilenameHasNoEffectOfPathConstruction() throws IOException {
        getPathPTooLongFileName();
    }

    @Test
    public void testMaxFilenameWorks() throws IOException {
        Path loong = getPathPLongFileName();
        Files.createDirectories( loong.getParent() );
        Files.write( loong, CONTENT);
    }


    @Test( expected = FileSystemException.class )
    public void testMaxFilenameWriteTooLongThrows() throws IOException {
        Path loong = getPathPTooLongFileName();
        Files.createDirectories( loong.getParent() );
        Files.write( loong, CONTENT);
    }

    @Test
    public void testMaxFilenameDirWorks() throws IOException {
        Path loong = getPathPLongFileName();
        Files.createDirectories( loong );
    }


    @Test( expected = FileSystemException.class )
    public void testMaxFilenameDirTooLongThrows() throws IOException {
        Path loong = getPathPTooLongFileName();
        Files.createDirectories( loong );
    }

    @Test
    public void testMaxFilenameCopyWorks() throws IOException {
        Path loong = getPathPLongFileName();
        Files.copy( getPathPABf(), loong );
    }


    @Test( expected = FileSystemException.class )
    public void testMaxFilenameCopyTooLongThrows() throws IOException {
        Path loong = getPathPTooLongFileName();
        Files.copy( getPathPABf(), loong );
    }

    @Test
    public void testMaxFilenameHardLinkWorks() throws IOException {
        Path loong = getPathPLongFileName();
        Files.createLink( loong, getPathPABf() );
    }


    @Test( expected = FileSystemException.class )
    public void testMaxFilenameHardLinkTooLongThrows() throws IOException {
        Path loong = getPathPTooLongFileName();
        Files.createLink( loong, getPathPABf() );
    }

    @Test
    public void testMaxFilenameMoveWorks() throws IOException {
        Path loong = getPathPLongFileName();
        Files.move( getPathPABf(), loong );
    }


    @Test( expected = FileSystemException.class )
    public void testMaxFilenameMoveTooLongThrows() throws IOException {
        Path loong = getPathPTooLongFileName();
        Files.move( getPathPABf(), loong );
    }

    // todo needs more understanding, e.g. nul throws, com not
//    @Test
//    public void testWriteToIllegalFilenameThrows() throws IOException {
//        getPathPAd();
//        for ( String ill : capabilities.getIllegalFilenames()) {
//            try {
//                Files.write( getPathPA().resolve( ill), CONTENT );
//            } catch (IOException e) {
//                continue;
//            }
//
//            assertThat( "was allowed to write to illegal filename " + ill, false, is(true) );
//        }
//    }


    @Test( expected = FileSystemException.class )
    public void testMaxFilenameMsdaoveTooLongThrows() throws IOException {
        Path loong = getPathPTooLongFileName();
        Files.move( getPathPABf(), loong );
    }




//    @Test
//    public void testNewByteChannelUnnormalizedPath() throws IOException {
//        try ( SeekableByteChannel ch = FS.provider().newByteChannel( getPathPAu(), Sets.asSet(WRITE, CREATE_NEW ) )) {}
//
//        assertThat( getPathPA(), exists());
//    }

//    @Test( expected = NoSuchFileException.class )
//    public void bugNewByteChannelUnnormalizedPath() throws IOException {
//        try ( SeekableByteChannel ch = FS.provider().newByteChannel( getPathPAu(), Sets.asSet(WRITE, CREATE_NEW ) )) {}
//    }

//    @Test
//    public void testNewDirectoryStreamUnnormalizedPath() throws IOException {
//        getPathPABf();
//        try ( DirectoryStream<Path> stream = FS.provider().newDirectoryStream( getPathPAu(), getFilterAll())) {
//            for ( Path kid : stream ) {
//                assertThat( kid.toAbsolutePath().normalize(), is(getPathPAB()));
//            }
//        }
//    }

//    @Test( expected = NoSuchFileException.class )
//    public void bugNewDirectoryStreamUnnormalizedPath() throws IOException {
//        getPathPABf();
//        try ( DirectoryStream<Path> stream = FS.provider().newDirectoryStream( getPathPAu(), getFilterAll())) {}
//    }

    /*
     * ---------------------------------------------------------------
     */

    private DirectoryStream.Filter<Path> getFilterAll() {
        return new DirectoryStream.Filter<Path>() {
            @Override
            public boolean accept(Path entry) throws IOException {
                return true;
            }
        };
    }
}
