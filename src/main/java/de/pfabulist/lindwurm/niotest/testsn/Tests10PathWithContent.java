package de.pfabulist.lindwurm.niotest.testsn;

import de.pfabulist.lindwurm.niotest.testsn.setup.AllCapabilitiesBuilder;
import de.pfabulist.lindwurm.niotest.testsn.setup.Capa;
import de.pfabulist.lindwurm.niotest.testsn.setup.DetailBuilder;
import org.junit.Test;

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
public abstract class Tests10PathWithContent extends Tests09WrongProvider {

    public static final String MAX_FILENAME_LENGTH = "maxFilenameLength";
    public static final String MAX_PATH_LENGTH = "maxPathLength";

    public Tests10PathWithContent( Capa capa) {
        super(capa);
//        features.put("MaxFilename", capa::hasMaxFileName );
    }
    
    public static class CapaBuilder10 extends CapBuilder09 {

        public PathConstraintsBuilder pathConstraints() {
            return new PathConstraintsBuilder((AllCapabilitiesBuilder) this);
            
        }

        public class PathConstraintsBuilder extends DetailBuilder {
            public PathConstraintsBuilder(AllCapabilitiesBuilder descr) {
                super(descr);
            }
            
            @Override
            public AllCapabilitiesBuilder onOff(boolean val) {
                capa.addFeature( "MaxFilename", val && (capa.getInt( MAX_FILENAME_LENGTH ) >= 0));
                capa.addFeature( "MaxPath", val && (capa.getInt( MAX_PATH_LENGTH ) >= 0));
                return builder;
            }

            public PathConstraintsBuilder maxFilenameLength( int len ) {
                capa.attributes.put( MAX_FILENAME_LENGTH, len );
                return this;
            }

            public PathConstraintsBuilder noMaxFilenameLength() {
                capa.attributes.put( MAX_FILENAME_LENGTH, -1 );
                return this;
            }

            public PathConstraintsBuilder maxPathLength( int len ) {
                capa.attributes.put(MAX_PATH_LENGTH, len );
                return this;
            }

            public PathConstraintsBuilder noMaxPathLength() {
                capa.attributes.put(MAX_PATH_LENGTH, -1 );
                return this;
            }
        }
    }

    @Test
    public void testIsSameFileOnEqualPath() throws IOException {
        assertThat( Files.isSameFile( absTA(), absTA()), is( true ) );
    }

    @Test
    public void testIsSameFileOnEqualPathElementsOtherProvider() throws IOException {
        assertThat( Files.isSameFile( absTA(), otherProviderAbsA()), is( false ) );
    }


    @Test
    public void testIsSameFileWithUnnormalizedPath() throws IOException {
        assertThat(FS.provider().isSameFile( fileTAB(), unnormalize( fileTAB())), is(true));
    }

    @Test
    public void testIsSameFileWithSpecialUnnormalizedPath() throws IOException {
        assertThat(FS.provider().isSameFile( fileTAB(), fileTAB().resolve("..").resolve(nameB())), is(true));
    }

    @Test
    public void testIsSameFileWithRelativePath() throws IOException {
        assertThat(FS.provider().isSameFile( fileTAB(), relativize(fileTAB())), is(true));
    }

    @Test
    public void testIsSameFileOfSameContentDifferentPathIsNot() throws IOException {
        assertThat(FS.provider().isSameFile(fileTA(), fileTB()), is(false));
    }

    @Test
    public void testIsSameFileOfDifferentPathNonExistingFileIsNot() throws IOException {
        assertThat( FS.provider().isSameFile(fileTAB(), absTB()), is( false ));
    }

    @Test
    public void testIsSameFileOfDifferentPathNonExistingFile2IsNot() throws IOException {
        assertThat( FS.provider().isSameFile( fileTA(), absTB()), is( false ));
    }

    @Test
    public void testWriteUnnormalized() throws IOException {
        Files.write( unnormalize( absTA() ), CONTENT, standardOpen );
        assertThat(Files.readAllBytes(absTA()), is(CONTENT));
    }

    @Test
    public void testReadAttributesFromUnnormalizedPath() throws IOException {
        fileTA();
        assertThat(Files.size(unnormalize(fileTA())), is((long) CONTENT.length));
    }

    @Test
    public void testCheckAccessUnnormalizedPath() throws IOException {
        // expect no throw
        FS.provider().checkAccess(unnormalize(fileTA()));
    }

    @Test
    public void testCheckAccessRelativePath() throws IOException {
        // expect no throw
        FS.provider().checkAccess( relativize( fileTA() ));
    }


    @Test
    public void testCheckAccessSupportesRead() throws IOException {
        // should not throw UnsupportedOperationException
        try {
            FS.provider().checkAccess( fileTAB(), AccessMode.READ );
        } catch ( AccessDeniedException e ) {
            //
        }
    }

    @Test
    public void testCheckAccessSupportesWrite() throws IOException {
        // should not throw UnsupportedOperationException
        try {
            FS.provider().checkAccess( fileTAB(), AccessMode.WRITE );
        } catch ( AccessDeniedException e ) {
            //
        }
    }

    @Test
    public void testCheckAccessSupportesExecute() throws IOException {
        // should not throw UnsupportedOperationException
        try {
            FS.provider().checkAccess( fileTAB(), AccessMode.EXECUTE );
        } catch ( AccessDeniedException e ) {
            //
        }
    }

    @Test( expected = NoSuchFileException.class )
    public void testCheckAccessNonExistingFile() throws IOException {
        FS.provider().checkAccess( absTAB() );
    }

    @Test
    public void testCopyUnnormalizedPath() throws IOException {
        FS.provider().copy( unnormalize(srcFile()), tgt());
        assertThat( Files.readAllBytes( tgt()), is(CONTENT));
    }

    @Test
    public void testMoveUnnormalizedPath() throws IOException {
        FS.provider().move(unnormalize(srcFile()), tgt());
        assertThat( Files.readAllBytes( tgt()), is(CONTENT));
    }
    @Test
    public void testCopyToUnnormalizedPath() throws IOException {
        FS.provider().copy( srcFile(), unnormalize(tgt()));
        assertThat( Files.readAllBytes( tgt()), is(CONTENT));
    }

    @Test
    public void testMoveToUnnormalizedPath() throws IOException {
        FS.provider().move( srcFile(), unnormalize(tgt()));
        assertThat( Files.readAllBytes( tgt()), is(CONTENT));
    }

    @Test
    public void testCreateDirectoryUnnormalizedPath() throws IOException {
        FS.provider().createDirectory( unnormalize(absTA()));
        assertThat(absTA(), exists());
    }

    @Test
    public void testCreateDirectoryWithRelativePath() throws IOException {
        Files.createDirectory( relTA() );
        assertThat( relTA(), exists() );
    }

    @Test
    public void testDeleteUnnormalizedPath() throws IOException {
        FS.provider().delete( unnormalize(fileTAC()));
        assertThat( absTAC(), not(exists()));
    }

    @Test
    public void testDeleteIfExistsUnnormalizedPath() throws IOException {
        FS.provider().deleteIfExists(unnormalize(fileTAC()));
        assertThat( absTAC(), not(exists()));
    }

    @Test
    public void testGetFileStoreUnnormalizedPath() throws IOException {
        FS.provider().getFileStore(unnormalize(fileTA()));
    }
    @Test
    public void testIsHiddenUnnormalizedPath() throws IOException {
        FS.provider().isHidden( unnormalize(fileTA()) );
    }

    @Test
    public void testToRealpathResturnsAnAbsolutePath() throws Exception {
        assertThat( relativize( fileTAB()).toRealPath(), absolute());
    }

    @Test
    public void testToRealPathOfUnnormalizedResturnsAnNormalizedPath() throws Exception {
        Path real = unnormalize( fileTAB()).toRealPath();
        assertThat( real.normalize(), is(real));
    }


    @Test
    public void testToRealPathOfUnnormalizedIsSamePath() throws Exception {
        assertThat( FS.provider().isSameFile( unnormalize(fileTAB()).toRealPath(), fileTAB() ), is(true));
    }

    @Test( expected = NoSuchFileException.class )
    public void testToRealPathOfNonExistingFileThrows() throws Exception {
        absTAB().toRealPath();
    }

    @Test
    public void testMaxFilenameHasNoEffectOfPathConstruction() throws IOException {
        absTTooLongFilename();
    }

    @Test
    public void testMaxFilenameWorks() throws IOException {
        Path loong = absTLongFilename();
        Files.createDirectories( loong.getParent() );
        Files.write( loong, CONTENT);
    }


    @Test( expected = FileSystemException.class )
    public void testMaxFilenameWriteTooLongThrows() throws IOException {
        Path loong = absTTooLongFilename();
        Files.createDirectories( loong.getParent() );
        Files.write( loong, CONTENT);
    }

    @Test
    public void testMaxFilenameDirOfLongWorks() throws IOException {
        Path loong = absTLongFilename();
        Files.createDirectories( loong );
    }


    @Test( expected = FileSystemException.class )
    public void testMaxFilenameDirTooLongThrows() throws IOException {
        Path loong = absTTooLongFilename();
        Files.createDirectories( loong );
    }

    @Test
    public void testMaxFilenameCopyWorks() throws IOException {
        Path loong = absTLongFilename();
        Files.copy( fileTAB(), loong );
    }


    @Test( expected = FileSystemException.class )
    public void testMaxFilenameCopyTooLongThrows() throws IOException {
        Path loong = absTTooLongFilename();
        Files.copy( fileTAB(), loong );
    }

    @Test
    public void testMaxFilenameHardLinkWorks() throws IOException {
        Path loong = absTLongFilename();
        Files.createLink( loong, fileTAB() );
    }


    @Test( expected = FileSystemException.class )
    public void testMaxFilenameHardLinkTooLongThrows() throws IOException {
        Path loong = absTTooLongFilename();
        Files.createLink( loong, fileTAB() );
    }

    @Test
    public void testMaxFilenameMoveWorks() throws IOException {
        Path loong = absTLongFilename();
        Files.move( fileTAB(), loong );
    }


    @Test( expected = FileSystemException.class )
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
        return path.getParent().resolve( ".." ).resolve( path.getParent().getFileName() ).resolve(path.getFileName());
    }

    public Path absTLongFilename()  {
        return FS.getPath( absTTooLongFilename().toString().substring(0, capa.get(Integer.class, MAX_FILENAME_LENGTH)));
    }

    public Path absTTooLongFilename() {
        
        int maxFilenameLength = capa.getInt( MAX_FILENAME_LENGTH);
        if ( maxFilenameLength < 2 ) {
            throw new IllegalStateException( "set max filename length" );
        }

        String name = nameA();

        while ( name.length() < maxFilenameLength + 2 ) {
            name += name;
        }

        return absT().resolve(name);
    }

}
