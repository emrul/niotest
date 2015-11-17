package de.pfabulist.lindwurm.niotest.tests;

import de.pfabulist.kleinod.collection.P;
import de.pfabulist.lindwurm.niotest.tests.topics.*;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
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
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.fail;

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

    public static final String MAX_FILENAME_LENGTH = "getMaxFilenameLength";
    public static final String MAX_PATH_LENGTH = "maxPathLength";
    public static final String ONE_CHAR_COUNT = "oneCharCount";
    public static final String GET_FILENAME_LENGTH = "filenameLenth";
    public static final String GET_PATH_LENGTH = "pathLength";

    public Tests10PathWithContent( FSDescription capa ) {
        super( capa );
    }

    @Test
    public void testIsSameFileOnEqualPath() throws IOException {
        Path file = getNonExistingPath();
        assertThat( Files.isSameFile( file, file ) ).isTrue();
    }

//    @Test // todo really same path elements ?
//    public void testIsSameFileOnEqualPathElementsOtherProvider() throws IOException {
//        assertThat( Files.isSameFile( absTA(), otherProviderAbsA() ), is( false ) );
//    }

    @Test
    public void testIsSameFileWithUnnormalizedPath() throws IOException {
        assertThat( FS.provider().isSameFile( getFile(), unnormalize( getFile() ) ) ).isTrue();
    }

//    @Test todo what is special ?
//    public void testIsSameFileWithSpecialUnnormalizedPath() throws IOException {
//        assertThat( FS.provider().isSameFile( fileTAB(), fileTAB().resolve( ".." ).resolve( nameB() ) ), is( true ) );
//    }

    @Test
    @Category( WorkingDirectoryInPlaygroundTree.class )
    public void testIsSameFileWithRelativePath() throws IOException {
        assertThat( FS.provider().isSameFile( getFile(), relativize( getFile() ) ) ).isTrue();
    }

    @Test
    public void testIsSameFileOfSameContentDifferentPathIsNot() throws IOException {
        assertThat( FS.provider().isSameFile( fileTA(), fileTB() ) ).isFalse();
    }

    @Test
    public void testIsSameFileOfDifferentPathNonExistingFileIsNot() throws IOException {
        assertThat( FS.provider().isSameFile( getFile(), getNonExistingPath() ) ).isFalse();
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
        assertThat( Files.readAllBytes( absTA() ) ).isEqualTo( CONTENT );
    }

    @Test
    public void testReadAttributesFromUnnormalizedPath() throws IOException {
        Path file = getFile();
        long size = Files.size( file );
        assertThat( Files.size( unnormalize( file ) ) ).isEqualTo( size );
    }

    @Test
    public void testCheckAccessUnnormalizedPath() throws IOException {
        try {
            FS.provider().checkAccess( unnormalize( getFile() ) );
        } catch( Exception e ) {
            fail( "checkAccess fails to normalize" );
        }
    }

    @Test
    @Category( WorkingDirectoryInPlaygroundTree.class )
    public void testCheckAccessRelativePath() throws IOException {
        try {
            FS.provider().checkAccess( relativize( getFile() ) );
        } catch( IOException e ) {
            fail( "checkAccess does not work with relative paths" );
        }
    }

    @Test
    public void testCheckAccessSupportesRead() throws IOException {
        try {
            FS.provider().checkAccess( getFile(), AccessMode.READ );
        } catch( AccessDeniedException e ) {
            //
        } catch( UnsupportedOperationException e ) {
            fail( "checkAccess must support READ" );
        }
    }

    @Test
    @Category( Writable.class )
    public void testCheckAccessSupportesWrite() throws IOException {
        try {
            FS.provider().checkAccess( fileTAB(), AccessMode.WRITE );
        } catch( AccessDeniedException e ) {
            //
        } catch( UnsupportedOperationException e ) {
            fail( "checkAccess must support WRITE" );
        }
    }

    @Test
    public void testCheckAccessSupportesExecute() throws IOException {
        try {
            FS.provider().checkAccess( getFile(), AccessMode.EXECUTE );
        } catch( AccessDeniedException e ) {
            //
        } catch( UnsupportedOperationException e ) {
            fail( "checkAccess must support EXECUTE" );
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
        assertThat( Files.readAllBytes( tgt() ) ).isEqualTo( CONTENT );
    }

    @Test
    @Category( { Move.class, Writable.class } )
    public void testMoveUnnormalizedPath() throws IOException {
        FS.provider().move( unnormalize( srcFile() ), tgt() );
        assertThat( Files.readAllBytes( tgt() ) ).isEqualTo( CONTENT );
    }

    @Test
    @Category( { Copy.class, Writable.class } )
    public void testCopyToUnnormalizedPath() throws IOException {
        FS.provider().copy( srcFile(), unnormalize( tgt() ) );
        assertThat( Files.readAllBytes( tgt() ) ).isEqualTo( CONTENT );
    }

    @Test
    @Category( { Move.class, Writable.class } )
    public void testMoveToUnnormalizedPath() throws IOException {
        FS.provider().move( srcFile(), unnormalize( tgt() ) );
        assertThat( Files.readAllBytes( tgt() ) ).isEqualTo( CONTENT );
    }

    @Test
    @Category( { Writable.class } )
    public void testCreateDirectoryUnnormalizedPath() throws IOException {
        FS.provider().createDirectory( unnormalize( absTA() ) );
        assertThat( absTA() ).exists();
    }

    @Test
    @Category( { Writable.class, WorkingDirectoryInPlaygroundTree.class } )
    public void testCreateDirectoryWithRelativePath() throws IOException {
        Files.createDirectory( relTA() );
        assertThat( relTA() ).exists();
    }

    @Test
    @Category( { Delete.class, Writable.class } )
    public void testDeleteUnnormalizedPath() throws IOException {
        FS.provider().delete( unnormalize( fileTAC() ) );
        assertThat( absTAC() ).doesNotExist();
    }

    @Test
    @Category( { Delete.class, Writable.class } )
    public void testDeleteIfExistsUnnormalizedPath() throws IOException {
        FS.provider().deleteIfExists( unnormalize( fileTAC() ) );
        assertThat( absTAC() ).doesNotExist();
    }

    @Test
    @Category( FileStores.class )
    public void testGetFileStoreUnnormalizedPath() throws IOException {
        try {
            FS.provider().getFileStore( unnormalize( getFile() ) );
        } catch( Exception e ) {
            fail( "getFileStore should accept unnormalized paths" );
        }
    }

    @Test
    public void testIsHiddenUnnormalizedPath() throws IOException {
        try {
            FS.provider().isHidden( unnormalize( getFile() ) );
        } catch( Exception e ) {
            fail( "isHidden should accept unnormalized paths" );
        }
    }

    @Test
    @Category( WorkingDirectoryInPlaygroundTree.class )
    public void testToRealPathReturnsAnAbsolutePath() throws Exception {
        assertThat( relativize( getFile() ).toRealPath() ).isAbsolute();
    }

    @Test
    public void testToRealPathOfUnnormalizedResturnsAnNormalizedPath() throws Exception {
        Path real = unnormalize( getFile() ).toRealPath();
        assertThat( real.normalize() ).isEqualTo( real );
    }

    @Test
    public void testToRealPathOfUnnormalizedIsSamePath() throws Exception {
        Path file = getFile();
        assertThat( FS.provider().isSameFile( unnormalize( file ).toRealPath(), file ) ).isTrue();
    }

    @Test( expected = NoSuchFileException.class )
    public void testToRealPathOfNonExistingFileThrows() throws Exception {
        getNonExistingPath().toRealPath();
    }

    @Test
    @Category( MaxFilename.class )
    public void testTooLongFilenameHasNoEffectOnPathConstruction() throws IOException {
        try {
            getEmptyDir().resolve( tooLongFileName() );
        } catch( Exception e ) {
            fail( "too long paths should be buildable" );
        }
    }

    @Test
    @Category({ MaxPath.class,  Writable.class })  //todo absTToolongPath for readonlies
    public void testTooLongPathHasNoEffectOnPathConstruction() throws IOException {
        try {
            absTTooLongPath();
        } catch( Exception e ) {
            fail( "too long paths should be buildable" );
        }
    }

    @Test
    @Category( { Writable.class, MaxFilename.class } )
    public void testWriteToMaxFilenameWorks() throws IOException {
        Path loong = absTLongFilename();
        Files.createDirectories( loong.getParent() );
        Files.write( loong, CONTENT );
        assertThat( Files.readAllBytes( loong ) ).isEqualTo( CONTENT );
    }

    @Test
    @Category( { Writable.class, MaxPath.class } )
    public void testWriteToMaxPathWorks() throws IOException {
        Path loong = absTLongPath();
        Files.createDirectories( loong.getParent() );
        Files.write( loong, CONTENT );
        assertThat( Files.readAllBytes( loong ) ).isEqualTo( CONTENT );
    }

    @Test
    @Category( { Writable.class, MaxFilename.class } )
    public void testMaxFilenameWriteTooLongThrows() throws IOException {
        Path loong = absTTooLongFilename();
        Files.createDirectories( loong.getParent() );
        assertThatThrownBy( () -> Files.write( loong, CONTENT ) ).isInstanceOf( FileSystemException.class );
    }

    @Test
    @Category( { Writable.class, MaxPath.class, LimitedPath.class } )
    public void testCreateDirWithTooLongPathThrows() throws IOException {
        Path loong = absTTooLongPath();
        Files.createDirectories( loong.getParent() );
        assertThatThrownBy( () -> Files.write( loong, CONTENT ) ).isInstanceOf( IOException.class );
    }

    @Test
    @Category( { Writable.class, MaxFilename.class } )
    public void testCreateDirWithMaxFilenameWorks() throws IOException {
        Path loong = absTLongFilename();
        Files.createDirectories( loong );
        assertThat( loong ).exists();
    }

    @Test
    @Category( { Writable.class, MaxPath.class } )
    public void testCreateDirOfMaxPathWorks() throws IOException {
        Path loong = absTLongPath();
        Files.createDirectories( loong );
        assertThat( loong ).exists();
    }

    @Test( expected = FileSystemException.class )
    @Category( { Writable.class, MaxFilename.class } )
    public void testMaxFilenameDirTooLongThrows() throws IOException {
        Path loong = absTTooLongFilename();
        Files.createDirectories( loong );
    }

    @Test
    @Category( { Writable.class, MaxPath.class, LimitedPath.class } )
    public void testMaxPathDirTooLongThrows() throws IOException {
        Path loong = absTTooLongPath();
        Files.createDirectories( loong.getParent() );
        assertThatThrownBy( () -> Files.createDirectory( loong ) ).isInstanceOf( IOException.class );
    }

    @Test
    @Category( { Writable.class, Copy.class, MaxFilename.class } )
    public void testMaxFilenameCopyWorks() throws IOException {
        Path loong = absTLongFilename();
        Files.createDirectories( loong.getParent() );
        Files.copy( fileTAB(), loong );
        assertThat( loong ).exists();
    }

    @Test
    @Category( { Writable.class, Copy.class, MaxPath.class } )
    public void testMaxPathCopyWorks() throws IOException {
        Path loong = absTLongPath();
        Files.createDirectories( loong.getParent() );
        Files.copy( fileTAB(), loong );
        assertThat( loong ).exists();
    }

    @Test( expected = FileSystemException.class )
    @Category( { Writable.class, Copy.class, MaxFilename.class } )
    public void testMaxFilenameCopyTooLongThrows() throws IOException {
        Path loong = absTTooLongFilename();
        Files.copy( fileTAB(), loong );
    }

    @Test
    @Category( { Writable.class, Copy.class, MaxPath.class, LimitedPath.class } )
    public void testMaxPathCopyTooLongThrows() throws IOException {
        Path loong = absTTooLongPath();
        Files.createDirectories( loong.getParent() );
        assertThatThrownBy( () -> Files.copy( fileTAB(), loong ) ).isInstanceOf( IOException.class );
    }

    @Test
    @Category( { Writable.class, HardLink.class, MaxFilename.class } )
    public void testMaxFilenameHardLinkWorks() throws IOException {
        Path loong = absTLongFilename();
        Files.createLink( loong, fileTAB() );
        assertThat( loong ).exists();
    }

    @Test
    @Category( { Writable.class, HardLink.class, MaxPath.class } )
    public void testMaxPathHardLinkWorks() throws IOException {
        Path loong = absTLongPath();
        Files.createDirectories( loong.getParent() );
        Files.createLink( loong, fileTAB() );
        assertThat( loong ).exists();
    }

    @Test( expected = FileSystemException.class )
    @Category( { HardLink.class, Writable.class, MaxFilename.class } )
    public void testMaxFilenameHardLinkTooLongThrows() throws IOException {
        Path loong = absTTooLongFilename();
        Files.createLink( loong, fileTAB() );
    }

    @Test
    @Category( { HardLink.class, Writable.class, MaxPath.class, LimitedPath.class } )
    public void testMaxPathHardLinkTooLongThrows() throws IOException {
        Path loong = absTTooLongPath();
        Files.createDirectories( loong.getParent() );
        assertThatThrownBy( () -> Files.createLink( loong, fileTAB() ) ).isInstanceOf( FileSystemException.class );
    }

    @Test
    @Category( { Move.class, Writable.class, MaxFilename.class } )
    public void testMaxFilenameMoveWorks() throws IOException {
        Path loong = absTLongFilename();
        Files.move( fileTAB(), loong );
        assertThat( loong ).exists();
    }

    @Test
    @Category( { Move.class, Writable.class, MaxPath.class } )
    public void testMaxPathMoveWorks() throws IOException {
        Path loong = absTLongPath();
        Files.createDirectories( loong.getParent() );
        Files.move( fileTAB(), loong );
        assertThat( loong ).exists();

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
        Files.createDirectories( loong.getParent() );
        assertThatThrownBy( () -> Files.move( fileTAB(), loong ) ).isInstanceOf( IOException.class );
    }

    //    Examples of systems with various case-sensitivity and case-preservation exist among file systems:
    //
    //                          Case-sensitive	   Case-insensitive
    //    Case-preserving	    UFS, ext3, ext4,   HFS Plus (optional), NTFS (in unix)	VFAT, FAT32 which is basically always used with long filename support, NTFS, HFS Plus
    //    Non-case-preserving	Impossible	       FAT12, FAT16 only when without long filename support.

    @Test
    @Category({ CaseInsensitive.class, Writable.class  })
    public void testCaseInsensitiveWriting() throws IOException {
        Files.write( absTA(), CONTENT );
        Files.write( mixCase( absTA() ), CONTENT_OTHER );

        assertThat( Files.readAllBytes( absTA() ) ).isEqualTo( CONTENT_OTHER );
    }

    @Test
    @Category({ CaseInsensitive.class, Writable.class })
    public void testCaseInsensitivePathsPointToSameFile() throws IOException {

        Path file = dirTA().resolve( nameD() );

        // create file where last filename is mixed case
        Files.write( mixCase( file ), CONTENT );
        assertThat( Files.isSameFile( file, mixCase( file ) ) ).isTrue();
    }

    @Test
    @Category( { CasePreserving.class, CaseInsensitive.class, Writable.class } )
    public void testCasePreserving() throws IOException {

        Path file = dirTA().resolve( nameD() );
        Path mixed = dirTA().resolve( mixCase( nameD() ) );

        // create file where last filename is mixed case
        Files.write( mixed, CONTENT );

        try( DirectoryStream<Path> dir = Files.newDirectoryStream( file.getParent() ) ) {
            for( Path kid : dir ) {
                assertThat( kid.toString() ).isEqualTo( mixed.toString() );
                assertThat( kid.toString() ).isNotEqualTo( file.toString() );
                assertThat( kid.toString() ).isNotEqualTo( file.toString().toUpperCase() );
            }
        }
    }

    @Test
    @Category( { CasePreserving.class, CaseInsensitive.class, Writable.class } )
    public void testCaseRememberingOverwriteDoesNotOverwriteRememberedName() throws IOException {
        Path file = dirTA().resolve( nameD() );

        // create file where last filename is mixed case
        Files.write( mixCase( file ), CONTENT );
        Files.write( file, CONTENT );

        try( DirectoryStream<Path> dstr = Files.newDirectoryStream( file.getParent() ) ) {
            Path kid = dstr.iterator().next();
            assertThat( kid.toString().toLowerCase() ).isEqualTo( file.toString().toLowerCase() );
            assertThat( kid.toString() ).isNotEqualTo( file.toString() );
        }
    }

    @Test
    @Category( NonCasePreserving.class )
    public void testNonCasePreserving() throws IOException {

        Path file = dirTA().resolve( nameD() );

        // create file where last filename is mixed case
        Files.write( mixCase( file ), CONTENT );

        try( DirectoryStream<Path> dir = Files.newDirectoryStream( file.getParent() ) ) {
            for( Path kid : dir ) {
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
        return absT().resolve( maxFileName() );
    }

    public Path absTTooLongFilename() {
        return absT().resolve( tooLongFileName() );
    }

    public String tooLongFileName() {
        return maxFileName() + description.get( ONE_CHAR_COUNT );
    }

    public String maxFileName() {
        return longFileName( getMaxFilenameLength() );
    }

    public String longFileName( int len ) {
        return longFileName( len, (String) description.get( ONE_CHAR_COUNT ));
    }

    @SuppressFBWarnings()
    public String longFileName( int len, String one ) {

        String ret = (String)description.rem.get( P.of( len, one ));

        if ( ret != null    ) {
            return ret;
        }

        ret = "";

        for( int i = 0; i < len; i++ ) {
            ret += one;
        }

        description.rem.put( P.of( len, one ), ret );

        return ret;

    }

    public Path maxPath() {
        return maxPath( getMaxPathLength() );
    }

    @SuppressFBWarnings()
    public Path maxPath( int len ) {

        Path ret = description.longPaths.get( len );

        if ( ret != null ) {
            try {
                Files.deleteIfExists( ret );
            } catch( IOException e ) {
                //
            }

            return ret;
        }

        ret = getCommon();

        Function<String, Integer> counting = (Function<String, Integer>) description.get( GET_PATH_LENGTH );
        int max = len - counting.apply(ret.toString()) - 1;
        int maxFN = getMaxFilenameLength();

        String fname = longFileName(maxFN / 8); // a must always work

        String str = "";

        while (counting.apply(str) < (max - maxFN - 1)) {
            str += (str.isEmpty() ? "" : FS.getSeparator()) + fname;
        }

        String foo = longFileName(max - counting.apply(str) - 1, "b");

//        System.out.println(max - counting.apply( str ) - 1);
//        System.out.println("++++++++++++++ " + counting.apply( ret.resolve( str ).toString() ) + " " + ret.resolve( str ).toString().length());

        if (foo.length() > 0) {
            str += FS.getSeparator() + foo;
        }

        //str += FS.getSeparator() + "-";
        //str += FS.getSeparator() + longFileName( max - counting.apply( str ) - 1);

//        ret = ret.resolve( "ab" ).resolve( str );
        ret = ret.resolve(str);

        description.longPaths.put( len, ret );

//        System.out.println("++++++++++++++ " + counting.apply( ret.toString() ) + " " + ret.toString().length());


        return ret;
    }

//    public String longFileName() {
//        int maxFilenameLength = getMaxFilenameLength();
//
//        return tooLongFileName().substring( 0, maxFilenameLength );
//    }

    private int getMaxFilenameLength() {
        int maxFilenameLength = description.getInt( MAX_FILENAME_LENGTH );
        if( maxFilenameLength < 2 ) {
            throw new IllegalStateException( "set max filename length" );
        }
        return maxFilenameLength;
    }

    public Path absTLongPath() {
        return maxPath();
//        Path ret = absT();
//        return ret.resolve( tooLongPath( getMaxPathLength() - ret.toString().length() - FS.getSeparator().length() ) );
    }

//    public Path absTLongPath( int len ) {
//        return maxPath().resolve( "a" );
////        Path ret = absT();
////        return ret.resolve( tooLongPath( len - ret.toString().length() - FS.getSeparator().length() ) );
//    }

    public Path absTTooLongPath() {
        return maxPath( getMaxPathLength() + 1 );
//        Path ret = absT();
//        return ret.resolve( tooLongPath( getMaxPathLength() - ret.toString().length() - FS.getSeparator().length() + 5 ) );
    }

//    public String tooLongPath( int len ) {
//
//        String longname = maxFileName();
//        String sep = FS.getSeparator();
//
//        Function<String, Integer> counting = (Function<String, Integer>) description.get( GET_FILENAME_LENGTH );
//
//        return IntStream.range( 0, ( len / (counting.apply( longname ) + sep.length()))).
//                mapToObj( n -> longname ).
//                reduce( longname, ( x, y ) -> x + sep + y ).
//                substring( 0, len );
//    }

    private int getMaxPathLength() {
        int maxPathLength = description.getInt( MAX_PATH_LENGTH );
        if( maxPathLength < 2 ) {
            throw new IllegalStateException( "set max path length" );
        }
        return maxPathLength;
    }

    public Path mixCase( Path in ) {
        return in.getFileSystem().getPath( mixCase( in.toString() ) );
    }

    // todo work with unicode
    @SuppressFBWarnings()
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
