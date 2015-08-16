package de.pfabulist.lindwurm.niotest.tests;

import de.pfabulist.lindwurm.niotest.tests.topics.CaseInsensitive;
import de.pfabulist.lindwurm.niotest.tests.topics.CasePreserving;
import de.pfabulist.lindwurm.niotest.tests.topics.Copy;
import de.pfabulist.lindwurm.niotest.tests.topics.Delete;
import de.pfabulist.lindwurm.niotest.tests.topics.FileStores;
import de.pfabulist.lindwurm.niotest.tests.topics.HardLink;
import de.pfabulist.lindwurm.niotest.tests.topics.LimitedPath;
import de.pfabulist.lindwurm.niotest.tests.topics.MaxFilename;
import de.pfabulist.lindwurm.niotest.tests.topics.MaxPath;
import de.pfabulist.lindwurm.niotest.tests.topics.Move;
import de.pfabulist.lindwurm.niotest.tests.topics.NonCasePreserving;
import de.pfabulist.lindwurm.niotest.tests.topics.SlowTest;
import de.pfabulist.lindwurm.niotest.tests.topics.Writable;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.AccessMode;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystemException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;

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
    }


    @Test
    public void testIsSameFileOnEqualPath() throws IOException {
        Path file = getNonExistingPath();
        assertThat( Files.isSameFile( file, file )).isTrue();
    }

//    @Test // todo really same path elements ?
//    public void testIsSameFileOnEqualPathElementsOtherProvider() throws IOException {
//        assertThat( Files.isSameFile( absTA(), otherProviderAbsA() ), is( false ) );
//    }

    @Test
    public void testIsSameFileWithUnnormalizedPath() throws IOException {
        assertThat( FS.provider().isSameFile( getFile(), unnormalize( getFile()))).isTrue();
    }

//    @Test todo what is special ?
//    public void testIsSameFileWithSpecialUnnormalizedPath() throws IOException {
//        assertThat( FS.provider().isSameFile( fileTAB(), fileTAB().resolve( ".." ).resolve( nameB() ) ), is( true ) );
//    }

    @Test
    public void testIsSameFileWithRelativePath() throws IOException {
        assertThat( FS.provider().isSameFile( getFile(), relativize( getFile()))).isTrue();
    }

    @Test
    public void testIsSameFileOfSameContentDifferentPathIsNot() throws IOException {
        assertThat( FS.provider().isSameFile( fileTA(), fileTB())).isFalse();
    }

    @Test
    public void testIsSameFileOfDifferentPathNonExistingFileIsNot() throws IOException {
        assertThat( FS.provider().isSameFile( getFile(), getNonExistingPath() )).isFalse();
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
        assertThat( Files.readAllBytes( absTA() )).isEqualTo( CONTENT );
    }

    @Test
    public void testReadAttributesFromUnnormalizedPath() throws IOException {
        Path file = getFile();
        long size = Files.size( file );
        assertThat( Files.size( unnormalize( file ))).isEqualTo( size );
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
        assertThat( Files.readAllBytes( tgt() )).isEqualTo( CONTENT );
    }

    @Test
    @Category( { Move.class, Writable.class } )
    public void testMoveUnnormalizedPath() throws IOException {
        FS.provider().move( unnormalize( srcFile() ), tgt() );
        assertThat( Files.readAllBytes( tgt() )).isEqualTo( CONTENT );
    }

    @Test
    @Category( { Copy.class, Writable.class } )
    public void testCopyToUnnormalizedPath() throws IOException {
        FS.provider().copy( srcFile(), unnormalize( tgt() ) );
        assertThat( Files.readAllBytes( tgt() )).isEqualTo( CONTENT );
    }

    @Test
    @Category( { Move.class, Writable.class } )
    public void testMoveToUnnormalizedPath() throws IOException {
        FS.provider().move( srcFile(), unnormalize( tgt() ) );
        assertThat( Files.readAllBytes( tgt() )).isEqualTo( CONTENT );
    }

    @Test
    @Category( { Writable.class } )
    public void testCreateDirectoryUnnormalizedPath() throws IOException {
        FS.provider().createDirectory( unnormalize( absTA() ) );
        assertThat( absTA()).exists();
    }

    @Test
    @Category( { Writable.class } )
    public void testCreateDirectoryWithRelativePath() throws IOException {
        Files.createDirectory( relTA() );
        assertThat( relTA()).exists();
    }

    @Test
    @Category( { Delete.class, Writable.class } )
    public void testDeleteUnnormalizedPath() throws IOException {
        FS.provider().delete( unnormalize( fileTAC() ) );
        assertThat( absTAC()).doesNotExist();
    }

    @Test
    @Category( { Delete.class, Writable.class } )
    public void testDeleteIfExistsUnnormalizedPath() throws IOException {
        FS.provider().deleteIfExists( unnormalize( fileTAC() ) );
        assertThat( absTAC()).doesNotExist();
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
        assertThat( relativize( getFile() ).toRealPath()).isAbsolute();
    }

    @Test
    public void testToRealPathOfUnnormalizedResturnsAnNormalizedPath() throws Exception {
        Path real = unnormalize( getFile() ).toRealPath();
        assertThat( real.normalize()).isEqualTo( real );
    }

    @Test
    public void testToRealPathOfUnnormalizedIsSamePath() throws Exception {
        Path file = getFile();
        assertThat( FS.provider().isSameFile( unnormalize( file ).toRealPath(), file )).isTrue();
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

    @Test
    @Category( { Writable.class, MaxPath.class, SlowTest.class } )
    public void testMaxPathWorks() throws IOException {
        Path loong = absTLongPath();
        Files.createDirectories( loong.getParent() );
        Files.write( loong, CONTENT );
    }

    @Test
    @Category( { Writable.class, MaxFilename.class  } )
    public void testMaxFilenameWriteTooLongThrows() throws IOException {
        Path loong = absTTooLongFilename();
        Files.createDirectories( loong.getParent() );
        assertThatThrownBy( () -> Files.write( loong, CONTENT ) ).isInstanceOf( FileSystemException.class );
    }

    @Test( expected = FileSystemException.class )
    @Category( { Writable.class, MaxPath.class, LimitedPath.class } )
    public void testMaxPathWriteTooLongThrows() throws IOException {
        Path loong = absTTooLongPath();
        Files.createDirectories( loong.getParent() );
        Files.write( loong, CONTENT );
    }

    @Test
    @Category( { Writable.class, MaxFilename.class } )
    public void testMaxFilenameDirOfLongWorks() throws IOException {
        Path loong = absTLongFilename();
        Files.createDirectories( loong );
    }

    @Test
    @Category( { Writable.class, MaxPath.class } )
    public void testMaxPathDirOfLongWorks() throws IOException {
        Path loong = absTLongPath();
        Files.createDirectories( loong );
    }

    @Test( expected = FileSystemException.class )
    @Category( { Writable.class, MaxFilename.class } )
    public void testMaxFilenameDirTooLongThrows() throws IOException {
        Path loong = absTTooLongFilename();
        Files.createDirectories( loong );
    }

    @Test( expected = FileSystemException.class )
    @Category( { Writable.class, MaxPath.class, SlowTest.class, LimitedPath.class } )
    public void testMaxPathDirTooLongThrows() throws IOException {
        Path loong = absTTooLongPath();
        Path now = loong.getRoot();
        int i = 0;
        for ( Path elem : loong ) {
            now = now.resolve( elem );
            System.out.println( "creating " + now.toString().length() + " " + now );

            if ( i++ < 12 ) {
                Files.createDirectories( now );
            } else {
                Files.createDirectory( now );
            }
        }
    }

    @Test
    @Category( { Writable.class, Copy.class, MaxFilename.class } )
    public void testMaxFilenameCopyWorks() throws IOException {
        Path loong = absTLongFilename();
        for ( Path el : loong ) {
            System.out.println( el.toString().length() );
        }
        Files.createDirectories( loong.getParent());
        Files.copy( fileTAB(), loong );
    }

    @Test
    @Category( { Writable.class, Copy.class, MaxPath.class } )
    public void testMaxPathCopyWorks() throws IOException {
        Path loong = absTLongPath();
        Files.createDirectories( loong.getParent());
        Files.copy( fileTAB(), loong );
    }

    @Test( expected = FileSystemException.class )
    @Category( { Writable.class, Copy.class, MaxFilename.class } )
    public void testMaxFilenameCopyTooLongThrows() throws IOException {
        Path loong = absTTooLongFilename();
        Files.copy( fileTAB(), loong );
    }


    @Test( expected = FileSystemException.class )
    @Category( { Writable.class, Copy.class, MaxPath.class, LimitedPath.class } )
    public void testMaxPathCopyTooLongThrows() throws IOException {

        Path loong = absTTooLongPath();
        Files.createDirectories( loong.getParent());
        Files.copy( fileTAB(), loong );
    }

    @Test
    @Category( { Writable.class, HardLink.class, MaxFilename.class } )
    public void testMaxFilenameHardLinkWorks() throws IOException {
        Path loong = absTLongFilename();
        Files.createLink( loong, fileTAB() );
    }

    @Test
    @Category( { Writable.class, HardLink.class, MaxPath.class } )
    public void testMaxPathHardLinkWorks() throws IOException {
//        try {
//            Thread.sleep( 100000 );
//        } catch( InterruptedException e ) {
//            e.printStackTrace();
//        }


        Path loong = absTLongPath();
        Files.createDirectories( loong.getParent());
        Files.createLink( loong, fileTAB() );
    }

    @Test( expected = FileSystemException.class )
    @Category( { HardLink.class, Writable.class, MaxFilename.class } )
    public void testMaxFilenameHardLinkTooLongThrows() throws IOException {
        Path loong = absTTooLongFilename();
        Files.createLink( loong, fileTAB() );
    }

    @Test( expected = FileSystemException.class )
    @Category( { HardLink.class, Writable.class, MaxPath.class, LimitedPath.class } )
    public void testMaxPathHardLinkTooLongThrows() throws IOException {
        Path loong = absTTooLongPath();
        Files.createDirectories( loong.getParent());
        Files.createLink( loong, fileTAB() );
    }

    @Test
    @Category( { Move.class, Writable.class, MaxFilename.class } )
    public void testMaxFilenameMoveWorks() throws IOException {
        Path loong = absTLongFilename();
        Files.move( fileTAB(), loong );
    }

    @Test
    @Category( { Move.class, Writable.class, MaxPath.class } )
    public void testMaxPathMoveWorks() throws IOException {
        Path loong = absTLongPath();
        Files.createDirectories( loong.getParent());
        Files.move( fileTAB(), loong );
    }

    @Test( expected = FileSystemException.class )
    @Category( { Writable.class, Move.class, MaxFilename.class } )
    public void testMaxFilenameMoveTooLongThrows() throws IOException {
        Path loong = absTTooLongFilename();
        Files.move( fileTAB(), loong );
    }

    @Test
    @Category( { Writable.class, Move.class, MaxPath.class, LimitedPath.class } )
    public void testMaxPathMoveTooLongThrows() throws IOException {
        Path loong = absTTooLongPath();
        Files.createDirectories( loong.getParent());
        assertThatThrownBy( () -> Files.move( fileTAB(), loong )).isInstanceOf( FileSystemException.class );
    }

    //    Examples of systems with various case-sensitivity and case-preservation exist among file systems:
    //
    //                          Case-sensitive	   Case-insensitive
    //    Case-preserving	    UFS, ext3, ext4,   HFS Plus (optional), NTFS (in unix)	VFAT, FAT32 which is basically always used with long filename support, NTFS, HFS Plus
    //    Non-case-preserving	Impossible	       FAT12, FAT16 only when without long filename support.



    @Test
    @Category( CaseInsensitive.class )
    public void testCaseInsensitiveWriting() throws IOException {
        Files.write( absTA(), CONTENT );
        Files.write( mixCase( absTA() ), CONTENT_OTHER );

        MatcherAssert.assertThat( Files.readAllBytes( absTA() ), is( CONTENT_OTHER ) );
    }

    @Test
    @Category( CaseInsensitive.class )
    public void testCaseInsensitivePathsPointToSameFile() throws IOException {

        Path file = dirTA().resolve( nameD() );

        // create file where last filename is mixed case
        Files.write( mixCase( file ), CONTENT );

        Files.isSameFile( file,  mixCase( file ));
    }

    @Test
    @Category({ CasePreserving.class, CaseInsensitive.class })
    public void testCasePreserving() throws IOException {

        Path file = dirTA().resolve( nameD() );
        Path mixed = dirTA().resolve( mixCase( nameD() ) );

        // create file where last filename is mixed case
        Files.write( mixed, CONTENT );

        try( DirectoryStream<Path> dir = Files.newDirectoryStream( file.getParent() )) {
            for ( Path kid : dir ) {
                assertThat( kid.toString() ).isEqualTo( mixed.toString() );
                assertThat( kid.toString() ).isNotEqualTo( file.toString() );
                assertThat( kid.toString() ).isNotEqualTo( file.toString().toUpperCase() );
            }
        }
    }

    @Test
    @Category({ CasePreserving.class, CaseInsensitive.class })
    public void testCaseRememberingOverwriteDoesNotOverwriteRememberedName() throws IOException {
        Path file = dirTA().resolve( nameD() );

        // create file where last filename is mixed case
        Files.write( mixCase( file ), CONTENT );
        Files.write( file, CONTENT );

        try( DirectoryStream<Path> dstr = Files.newDirectoryStream( file.getParent() ) ) {
            Path kid = dstr.iterator().next();
            MatcherAssert.assertThat( kid.toString().toLowerCase(), is( file.toString().toLowerCase() ) );
            MatcherAssert.assertThat( kid.toString(), not( is( file.toString() ) ) );
        }
    }


    @Test
    @Category( NonCasePreserving.class )
    public void testNonCasePreserving() throws IOException {

        Path file = dirTA().resolve( nameD() );

        // create file where last filename is mixed case
        Files.write( mixCase( file ), CONTENT );

        try( DirectoryStream<Path> dir = Files.newDirectoryStream( file.getParent() )) {
            for ( Path kid : dir ) {
                assertThat( kid.toString() ).isNotEqualTo( mixCase( file ).toString() );
                assertThat( kid.toString() ).isNotEqualTo( file.toString() );
                assertThat( kid.toString() ).isEqualTo( file.toString().toUpperCase() );
            }
        }
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
    /*
     * ---------------------------------------------------------------
     */
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
        return absT().resolve( tooLongFileName().substring( 0, getMaxFilenameLength() ) );
    }

    public Path absTTooLongFilename() {
        return absT().resolve( tooLongFileName() );
    }

    public String tooLongFileName() {
        int    maxFilenameLength = getMaxFilenameLength();
        String name = nameA();

        while( name.length() < maxFilenameLength + 2 ) {
            name += name;
        }

        return name;
    }

    public String longFileName() {
        int maxFilenameLength = getMaxFilenameLength();

        return tooLongFileName().substring( 0, maxFilenameLength );
    }

    private int getMaxFilenameLength() {
        int maxFilenameLength = description.getInt( MAX_FILENAME_LENGTH );
        if( maxFilenameLength < 2 ) {
            throw new IllegalStateException( "set max filename length" );
        }
        return maxFilenameLength;
    }

    public Path absTLongPath() {
        Path ret = absT();
        return ret.resolve( tooLongPath( getMaxPathLength() - ret.toString().length() - FS.getSeparator().length()));
    }

    public Path absTLongPath( int len) {
        Path ret = absT();
        return ret.resolve( tooLongPath( len - ret.toString().length() - FS.getSeparator().length()));
    }

    public Path absTTooLongPath() {
        Path ret = absT();
        return ret.resolve( tooLongPath( getMaxPathLength() - ret.toString().length() - FS.getSeparator().length() + 5 ));
    }

    public String tooLongPath( int len ) {

        String longname = longFileName();
        String sep = FS.getSeparator();


        return IntStream.range( 0, (len / (longname.length() + sep.length()))).
                mapToObj( n -> longname ).
                reduce( longname, (x,y) -> x + sep + y ).
                substring( 0, len );
    }

    private int getMaxPathLength() {
        int maxPathLength = description.getInt( MAX_PATH_LENGTH );
        if( maxPathLength < 2 ) {
            throw new IllegalStateException( "set max path length" );
        }
        return maxPathLength;
    }



    public Path mixCase( Path in ) {
        return in.getFileSystem().getPath( mixCase(  in.toString() ) );
    }

    // todo work with unicode
    public String mixCase( String inStr ) {
        String mix = "";

        for( int i = 0; i < inStr.length(); i++ ) {
            if( i % 2 == 0 ) {
                mix += inStr.substring( i, i + 1 ).toUpperCase();
            } else {
                mix += inStr.substring( i, i + 1 ).toLowerCase();
            }
        }

        return mix;

    }


}
