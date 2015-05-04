package de.pfabulist.lindwurm.niotest.tests;

import de.pfabulist.lindwurm.niotest.tests.topics.Copy;
import de.pfabulist.lindwurm.niotest.tests.topics.Delete;
import de.pfabulist.lindwurm.niotest.tests.topics.FileStores;
import de.pfabulist.lindwurm.niotest.tests.topics.HardLink;
import de.pfabulist.lindwurm.niotest.tests.topics.MaxFilename;
import de.pfabulist.lindwurm.niotest.tests.topics.Move;
import de.pfabulist.lindwurm.niotest.tests.topics.Writable;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.AccessMode;
import java.nio.file.FileSystemException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;

import static de.pfabulist.lindwurm.niotest.matcher.PathAbsolute.absolute;
import static de.pfabulist.lindwurm.niotest.matcher.PathExists.exists;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

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
public abstract class Tests10PathWithContent extends Tests09WrongProvider {

    public static final String MAX_FILENAME_LENGTH = "maxFilenameLength";
    public static final String MAX_PATH_LENGTH = "maxPathLength";

    public Tests10PathWithContent( FSDescription capa ) {
        super( capa );
//        features.put("MaxFilename", description::hasMaxFileName );
    }

//    public static class CapaBuilder10 extends CapBuilder09 {
//
//        public PathConstraintsBuilder pathConstraints() {
//            return new PathConstraintsBuilder( (AllCapabilitiesBuilder) this );
//
//        }
//
//        public class PathConstraintsBuilder extends DetailBuilder {
//            public PathConstraintsBuilder( AllCapabilitiesBuilder descr ) {
//                super( descr );
//            }
//
//            @Override
//            public AllCapabilitiesBuilder onOff( boolean val ) {
//                capa.addFeature( "MaxFilename", val && ( capa.getInt( MAX_FILENAME_LENGTH ) >= 0 ) );
//                capa.addFeature( "MaxPath", val && ( capa.getInt( MAX_PATH_LENGTH ) >= 0 ) );
//                return builder;
//            }
//
//            public PathConstraintsBuilder maxFilenameLength( int len ) {
//                capa.attributes.put( MAX_FILENAME_LENGTH, len );
//                return this;
//            }
//
//            public PathConstraintsBuilder noMaxFilenameLength() {
//                capa.attributes.put( MAX_FILENAME_LENGTH, -1 );
//                return this;
//            }
//
//            public PathConstraintsBuilder maxPathLength( int len ) {
//                capa.attributes.put( MAX_PATH_LENGTH, len );
//                return this;
//            }
//
//            public PathConstraintsBuilder noMaxPathLength() {
//                capa.attributes.put( MAX_PATH_LENGTH, -1 );
//                return this;
//            }
//        }
//    }

    @Test
    public void testIsSameFileOnEqualPath() throws IOException {
        Path file = getNonExistingPath();
        assertThat( Files.isSameFile( file, file ), is( true ) );
    }

//    @Test // todo really same path elements ?
//    public void testIsSameFileOnEqualPathElementsOtherProvider() throws IOException {
//        assertThat( Files.isSameFile( absTA(), otherProviderAbsA() ), is( false ) );
//    }

    @Test
    public void testIsSameFileWithUnnormalizedPath() throws IOException {
        assertThat( FS.provider().isSameFile( getFile(), unnormalize( getFile() ) ), is( true ) );
    }

//    @Test todo what is special ?
//    public void testIsSameFileWithSpecialUnnormalizedPath() throws IOException {
//        assertThat( FS.provider().isSameFile( fileTAB(), fileTAB().resolve( ".." ).resolve( nameB() ) ), is( true ) );
//    }

    @Test
    public void testIsSameFileWithRelativePath() throws IOException {
        assertThat( FS.provider().isSameFile( getFile(), relativize( getFile() ) ), is( true ) );
    }

    @Test
    public void testIsSameFileOfSameContentDifferentPathIsNot() throws IOException {
        assertThat( FS.provider().isSameFile( fileTA(), fileTB() ), is( false ) );
    }

    @Test
    public void testIsSameFileOfDifferentPathNonExistingFileIsNot() throws IOException {
        assertThat( FS.provider().isSameFile( getFile(), getNonExistingPath() ), is( false ) );
    }

    // todo diff to above ?
//    @Test
//    public void testIsSameFileOfDifferentPathNonExistingFile2IsNot() throws IOException {
//        assertThat( FS.provider().isSameFile( fileTA(), absTB() ), is( false ) );
//    }

    @Test
    @Category( { Writable.class } )
    public void testWriteUnnormalized() throws IOException {
        Files.write( unnormalize( absTA() ), CONTENT, standardOpen );
        assertThat( Files.readAllBytes( absTA() ), is( CONTENT ) );
    }

    @Test
    public void testReadAttributesFromUnnormalizedPath() throws IOException {
        Path file = getFile();
        long size = Files.size( file );
        assertThat( Files.size( unnormalize( file ) ), is( size ) );
    }

    @Test
    public void testCheckAccessUnnormalizedPath() throws IOException {
        // expect no throw
        FS.provider().checkAccess( unnormalize( getFile() ) );
    }

    @Test
    public void testCheckAccessRelativePath() throws IOException {
        // expect no throw
        FS.provider().checkAccess( relativize( getFile() ) );
    }

    @Test
    public void testCheckAccessSupportesRead() throws IOException {
        // should not throw UnsupportedOperationException
        try {
            FS.provider().checkAccess( getFile(), AccessMode.READ );
        } catch( AccessDeniedException e ) {
            //
        }
    }

    @Test
    @Category( Writable.class )
    public void testCheckAccessSupportesWrite() throws IOException {
        // should not throw UnsupportedOperationException
        try {
            FS.provider().checkAccess( fileTAB(), AccessMode.WRITE );
        } catch( AccessDeniedException e ) {
            //
        }
    }

    @Test
    public void testCheckAccessSupportesExecute() throws IOException {
        // should not throw UnsupportedOperationException
        try {
            FS.provider().checkAccess( getFile(), AccessMode.EXECUTE );
        } catch( AccessDeniedException e ) {
            //
        }
    }

    @Test( expected = NoSuchFileException.class )
    public void testCheckAccessNonExistingFile() throws IOException {
        FS.provider().checkAccess( getNonExistingPath() );
    }

    @Test
    @Category( { Copy.class, Writable.class } )
    public void testCopyUnnormalizedPath() throws IOException {
        FS.provider().copy( unnormalize( srcFile() ), tgt() );
        assertThat( Files.readAllBytes( tgt() ), is( CONTENT ) );
    }

    @Test
    @Category( { Move.class, Writable.class } )
    public void testMoveUnnormalizedPath() throws IOException {
        FS.provider().move( unnormalize( srcFile() ), tgt() );
        assertThat( Files.readAllBytes( tgt() ), is( CONTENT ) );
    }

    @Test
    @Category( { Copy.class, Writable.class } )
    public void testCopyToUnnormalizedPath() throws IOException {
        FS.provider().copy( srcFile(), unnormalize( tgt() ) );
        assertThat( Files.readAllBytes( tgt() ), is( CONTENT ) );
    }

    @Test
    @Category( { Move.class, Writable.class } )
    public void testMoveToUnnormalizedPath() throws IOException {
        FS.provider().move( srcFile(), unnormalize( tgt() ) );
        assertThat( Files.readAllBytes( tgt() ), is( CONTENT ) );
    }

    @Test
    @Category( { Writable.class } )
    public void testCreateDirectoryUnnormalizedPath() throws IOException {
        FS.provider().createDirectory( unnormalize( absTA() ) );
        assertThat( absTA(), exists() );
    }

    @Test
    @Category( { Writable.class } )
    public void testCreateDirectoryWithRelativePath() throws IOException {
        Files.createDirectory( relTA() );
        assertThat( relTA(), exists() );
    }

    @Test
    @Category( { Delete.class, Writable.class } )
    public void testDeleteUnnormalizedPath() throws IOException {
        FS.provider().delete( unnormalize( fileTAC() ) );
        assertThat( absTAC(), not( exists() ) );
    }

    @Test
    @Category( { Delete.class, Writable.class } )
    public void testDeleteIfExistsUnnormalizedPath() throws IOException {
        FS.provider().deleteIfExists( unnormalize( fileTAC() ) );
        assertThat( absTAC(), not( exists() ) );
    }

    @Test
    @Category( FileStores.class )
    public void testGetFileStoreUnnormalizedPath() throws IOException {
        FS.provider().getFileStore( unnormalize( getFile() ) );
    }

    @Test
    public void testIsHiddenUnnormalizedPath() throws IOException {
        FS.provider().isHidden( unnormalize( getFile() ) );
    }

    @Test
    public void testToRealPathReturnsAnAbsolutePath() throws Exception {
        assertThat( relativize( getFile() ).toRealPath(), absolute() );
    }

    @Test
    public void testToRealPathOfUnnormalizedResturnsAnNormalizedPath() throws Exception {
        Path real = unnormalize( getFile() ).toRealPath();
        assertThat( real.normalize(), is( real ) );
    }

    @Test
    public void testToRealPathOfUnnormalizedIsSamePath() throws Exception {
        Path file = getFile();
        assertThat( FS.provider().isSameFile( unnormalize( file ).toRealPath(), file ), is( true ) );
    }

    @Test( expected = NoSuchFileException.class )
    public void testToRealPathOfNonExistingFileThrows() throws Exception {
        getNonExistingPath().toRealPath();
    }

    @Test
    @Category( MaxFilename.class )
    public void testMaxFilenameHasNoEffectOnPathConstruction() throws IOException {
        getNonEmptyDir().resolve( tooLongFileName() );
    }

    @Test
    @Category( { Writable.class, MaxFilename.class } )
    public void testMaxFilenameWorks() throws IOException {
        Path loong = absTLongFilename();
        Files.createDirectories( loong.getParent() );
        Files.write( loong, CONTENT );
    }

    @Test( expected = FileSystemException.class )
    @Category( { Writable.class, MaxFilename.class } )
    public void testMaxFilenameWriteTooLongThrows() throws IOException {
        Path loong = absTTooLongFilename();
        Files.createDirectories( loong.getParent() );
        Files.write( loong, CONTENT );
    }

    @Test
    @Category( { Writable.class, MaxFilename.class } )
    public void testMaxFilenameDirOfLongWorks() throws IOException {
        Path loong = absTLongFilename();
        Files.createDirectories( loong );
    }

    @Test( expected = FileSystemException.class )
    @Category( { Writable.class, MaxFilename.class } )
    public void testMaxFilenameDirTooLongThrows() throws IOException {
        Path loong = absTTooLongFilename();
        Files.createDirectories( loong );
    }

    @Test
    @Category( { Writable.class, Copy.class, MaxFilename.class } )
    public void testMaxFilenameCopyWorks() throws IOException {
        Path loong = absTLongFilename();
        Files.copy( fileTAB(), loong );
    }

    @Test( expected = FileSystemException.class )
    @Category( { Writable.class, Copy.class, MaxFilename.class } )
    public void testMaxFilenameCopyTooLongThrows() throws IOException {
        Path loong = absTTooLongFilename();
        Files.copy( fileTAB(), loong );
    }

    @Test
    @Category( { Writable.class, HardLink.class, MaxFilename.class } )
    public void testMaxFilenameHardLinkWorks() throws IOException {
        Path loong = absTLongFilename();
        Files.createLink( loong, fileTAB() );
    }

    @Test( expected = FileSystemException.class )
    @Category( { HardLink.class, Writable.class, MaxFilename.class } )
    public void testMaxFilenameHardLinkTooLongThrows() throws IOException {
        Path loong = absTTooLongFilename();
        Files.createLink( loong, fileTAB() );
    }

    @Test
    @Category( { Move.class, Writable.class, MaxFilename.class } )
    public void testMaxFilenameMoveWorks() throws IOException {
        Path loong = absTLongFilename();
        Files.move( fileTAB(), loong );
    }

    @Test( expected = FileSystemException.class )
    @Category( { Writable.class, Move.class, MaxFilename.class } )
    public void testMaxFilenameMoveTooLongThrows() throws IOException {
        Path loong = absTTooLongFilename();
        Files.move( fileTAB(), loong );
    }
//
//    // todo needs more understanding, e.g. nul throws, com not
////    @Test
////    public void testWriteToIllegalFilenameThrows() throws IOException {
////        absTAd();
////        for ( String ill : capabilities.getIllegalFilenames()) {
////            try {
////                Files.write( absTA().resolve( ill), CONTENT );
////            } catch (IOException e) {
////                continue;
////            }
////
////            assertThat( "was allowed to write to illegal filename " + ill, false, is(true) );
////        }
////    }
//
//
//    @Test( expected = FileSystemException.class )
//    public void testMaxFilenameMsdaoveTooLongThrows() throws IOException {
//        Path loong = absTTooLongFilename();
//        Files.move( absTABf(), loong );
//    }
//
//
//
//
////    @Test
////    public void testNewByteChannelUnnormalizedPath() throws IOException {
////        try ( SeekableByteChannel ch = FS.provider().newByteChannel( absTAu(), Sets.asSet(WRITE, CREATE_NEW ) )) {}
////
////        assertThat( absTA(), exists());
////    }
//
////    @Test( expected = NoSuchFileException.class )
////    public void bugNewByteChannelUnnormalizedPath() throws IOException {
////        try ( SeekableByteChannel ch = FS.provider().newByteChannel( absTAu(), Sets.asSet(WRITE, CREATE_NEW ) )) {}
////    }
//
////    @Test
////    public void testNewDirectoryStreamUnnormalizedPath() throws IOException {
////        absTABf();
////        try ( DirectoryStream<Path> stream = FS.provider().newDirectoryStream( absTAu(), getFilterAll())) {
////            for ( Path kid : stream ) {
////                assertThat( kid.toAbsolutePath().normalize(), is(absTAB()));
////            }
////        }
////    }
//
////    @Test( expected = NoSuchFileException.class )
////    public void bugNewDirectoryStreamUnnormalizedPath() throws IOException {
////        absTABf();
////        try ( DirectoryStream<Path> stream = FS.provider().newDirectoryStream( absTAu(), getFilterAll())) {}
////    }
//
//    /*
//     * ---------------------------------------------------------------
//     */
//
//    private DirectoryStream.Filter<Path> getFilterAll() {
//        return new DirectoryStream.Filter<Path>() {
//            @Override
//            public boolean accept(Path entry) throws IOException {
//                return true;
//            }
//        };
//    }

    public Path unnormalize( Path path ) {
        return path.getParent().resolve( ".." ).resolve( path.getParent().getFileName() ).resolve( path.getFileName() );
    }

    public Path absTLongFilename() {
        return FS.getPath( absTTooLongFilename().toString().substring( 0, description.getInt( MAX_FILENAME_LENGTH ) ) );
    }

    public Path absTTooLongFilename() {
        return absT().resolve( tooLongFileName() );
    }

    public String tooLongFileName() {
        int maxFilenameLength = description.getInt( MAX_FILENAME_LENGTH );
        if( maxFilenameLength < 2 ) {
            throw new IllegalStateException( "set max filename length" );
        }

        String name = nameA();

        while( name.length() < maxFilenameLength + 2 ) {
            name += name;
        }

        return name;
    }

}
