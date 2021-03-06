package de.pfabulist.lindwurm.niotest.tests;

import de.pfabulist.kleinod.nio.Filess;
import de.pfabulist.lindwurm.niotest.tests.topics.*;
import de.pfabulist.lindwurm.niotest.Utils;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileSystemException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;

import static de.pfabulist.kleinod.nio.PathIKWID.childGetParent;
import static de.pfabulist.kleinod.text.Strings.getBytes;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * ** BEGIN LICENSE BLOCK *****
 * BSD License (2 clause)
 * Copyright (c) 2006 - 2016, Stephan Pfab
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
public abstract class Tests02Dir extends Tests01NoContent {

    public Tests02Dir( FSDescription capa ) {
        super( capa );
    }

    @Test
    public void testDefaultIsDir() throws Exception {
        assertThat( pathDefault() ).isDirectory();
    }

    @Test
    public void testContentOfNonEmptyDir() throws IOException {
        assertThat( Files.list( getNonEmptyDir() ).findAny() ).isPresent();
//        try( DirectoryStream<Path> stream = Files.newDirectoryStream( getNonEmptyDir() ) ) {
//            assertThat( Utils.getSize( stream )).isNotEqualTo( 0 );
//        }
    }

    @Test
    public void testIteratorCanOnlyBeCalledOnceOnDirStream() throws IOException {
        try( DirectoryStream<Path> stream = Files.newDirectoryStream( getNonEmptyDir() ) ) {
            stream.iterator();
            assertThatThrownBy( stream::iterator ).isInstanceOf( IllegalStateException.class );
        }
    }

    @Test
    public void testDirStreamIteratorHasNoRemove() throws IOException {
        try( DirectoryStream<Path> stream = Files.newDirectoryStream( getNonEmptyDir() ) ) {
            assertThatThrownBy( () -> stream.iterator().remove() ).isInstanceOf( UnsupportedOperationException.class );
        }
    }

    @Test
    public void testContentOfNonEmptyDirFiltered() throws IOException {

        Path dir = getNonEmptyDir();

        int unfilteredSize;
        try( DirectoryStream<Path> stream = Files.newDirectoryStream( dir ) ) {
            unfilteredSize = Utils.getSize( stream );
        }

        // filter out first kid
        DirectoryStream.Filter<Path> filter = new DirectoryStream.Filter<Path>() {

            boolean first = true;

            @Override
            public boolean accept( Path entry ) throws IOException {
                if( first ) {
                    first = false;
                    return false;
                }
                return true;
            }
        };

        try( DirectoryStream<Path> stream = Files.newDirectoryStream( dir, filter ) ) {
            assertThat( Utils.getSize( stream ) ).isEqualTo( unfilteredSize - 1 );
        }
    }

    @Test
    @Category( Writable.class )
    public void testNewDirIsInParentsDirStream() throws IOException {

        Path dir = dirTA().resolve( nameB() );
        Files.createDirectory( dir );

        assertThat( Files.list( childGetParent( dir ) ).filter( c -> c.equals( dir ) ).findAny() ).isPresent();

//        try( DirectoryStream<Path> kids = Files.newDirectoryStream( childGetParent( dir ) ) ) {
//            assertThat( dir, isIn( kids ) );
//        }
    }

    @Test
    @Category( Writable.class )
    public void testNewDirectoryExists() throws IOException {
        Files.createDirectory( absTA() );
        assertThat( absTA() ).exists();
    }

    @Test
    @Category( { Writable.class, WorkingDirectoryInPlaygroundTree.class } )
    public void testNewRelDirectoryExists() throws IOException {
        Files.createDirectory( relTA() );
        assertThat( relTA() ).exists();
    }

    @Test
    @Category( Writable.class )
    public void testCreateDirectoryTwiceThrows() throws IOException {
        Path newDir = absTA();
        Files.createDirectory( newDir );
        assertThatThrownBy( () -> Files.createDirectory( newDir ) ).isInstanceOf( FileAlreadyExistsException.class );
    }

    @Test( expected = NoSuchFileException.class )
    @Category( Writable.class )
    public void testCreateDirectoryWithoutExistingParentFails() throws IOException {
        Files.createDirectory( absTAB() );
    }

    @Test( expected = FileSystemException.class )
    @Category( Writable.class )
    public void testCreateDirectoryWithInFileFails() throws IOException {
        Files.createDirectory( fileTA().resolve( nameC() ) );
    }

    @Test
    public void testRootisADir() throws IOException {
        assertThat( defaultRoot() ).isDirectory();
    }

    @Test
    public void testDefaultExists() throws Exception {
        assertThat( pathDefault() ).exists();
    }

    // todo defaultfs windows has e: not exists, and can create
//    @Test
//    public void testCreateRootFails() throws IOException {
//        for( Path root : FS.getRootDirectories()) {
//            assertThat( () -> {Files.createDirectory(root);}, throwsException( FileAlreadyExistsException.class ));
//        }
//    }

    @Test
    public void testNonExistingAbsolutePathIsNotADirectory() throws IOException {
        assertThat( Files.isDirectory( getNonExistingPath() ) ).isFalse();
    }

    @Test
    public void testNonExistingAbsolutePathIsNotADirectoryEvenIfParent() throws IOException {
        assertThat( Files.isDirectory( childGetParent( getNonExistingPath().resolve( "child" ) ) ) ).isFalse();
    }

    @Test
    public void testNonExistingRelativePathIsNotADirectory() throws IOException {
        assertThat( Files.isDirectory( getNonExistingPath() ) ).isFalse();
    }

    @Test( expected = FileAlreadyExistsException.class )
    @Category( Writable.class )
    public void testCreateDirWithSamePathAsExistingFileFails() throws Exception {
        Files.createDirectory( fileTA() );
    }

    @Test
    @Category( { SlowTest.class, Writable.class, Attributes.class, LastModifiedTime.class } )
    public void testCreateDirSetsModifiedTimeOfParent() throws IOException, InterruptedException {
        Path dir = dirTA();
        FileTime created = Files.getLastModifiedTime( dir );
        waitForAttribute();

        Files.createDirectory( dir.resolve( nameB() ) );
        assertThat( Files.getLastModifiedTime( dir ) ).isGreaterThan( created );
    }

    @Test
    @Category( { SlowTest.class, Writable.class, Attributes.class, LastAccessTime.class } )
    public void testCreateDirSetsLastAccessTimeOfParent() throws IOException, InterruptedException {
        Path dir = dirTA();
        FileTime before = Files.readAttributes( dir, BasicFileAttributes.class ).lastAccessTime();
        waitForAttribute();

        Files.createDirectory( dir.resolve( nameB() ) );
        assertThat( Files.readAttributes( dir, BasicFileAttributes.class ).lastAccessTime() ).isGreaterThan( before );
    }

    @Test
    @Category( { SlowTest.class, Writable.class, CreationTime.class } )
    public void testCreateDirSetsCreationTime() throws IOException, InterruptedException {
        Path dir = absTA();
        FileTime before = Files.getLastModifiedTime( childGetParent( dir ) );
        waitForAttribute();

        Files.createDirectory( dir );

        BasicFileAttributes atti = Files.readAttributes( dir, BasicFileAttributes.class );

        assertThat( atti.creationTime() ).isGreaterThan( before );
    }

    @Test
    public void testKidsOfAbsoluteDirAreAbsolute() throws Exception {
        try( DirectoryStream<Path> kids = Files.newDirectoryStream( getNonEmptyDir() ) ) {
            for( Path kid : kids ) {
                assertThat( kid ).isAbsolute();
            }
        }
    }

    @Test
    @Category( WorkingDirectoryInPlaygroundTree.class )
    public void testKidsOfRelativeDirAreRelative() throws Exception {
        try( DirectoryStream<Path> kids = Files.newDirectoryStream( childGetParent( relativize( getNonEmptyDir() ) ) ) ) {
            for( Path kid : kids ) {
                assertThat( kid ).isRelative();
            }
        }
    }

    // todo filter
//    @Test
//    public void testFilterOfRel() throws Exception { // TODO
//        Path abs = nonEmptyDir();
//        Path rel = pathDefault().toAbsolutePath().relativize( abs );
//
//        try( DirectoryStream<Path> kids = Files.newDirectoryStream( rel ) ) {
//            for( Path kid : kids ) {
//                assertThat( kid, relative() );
//            }
//        }
//    }

    @Test
    public void testKidsOfRelDirAreLikeTheResultOfResolve() throws Exception {
        Path dir = getNonEmptyDir();

        try( DirectoryStream<Path> kids = Files.newDirectoryStream( dir ) ) {
            for( Path kid : kids ) {
                assertThat( kid ).isEqualTo( dir.resolve( kid.getFileName() ) );
            }
        }
    }

    @Test
    @Category( { SlowTest.class, Writable.class, Attributes.class, LastAccessTime.class } )
    @SuppressWarnings( "PMD.UnusedLocalVariable" )
    public void testReadDirStreamSetsLastAccessTime() throws Exception {

        Path dir = childGetParent( fileTAB() );
        FileTime before = Files.readAttributes( dir, BasicFileAttributes.class ).lastAccessTime();
        waitForAttribute();

        try( DirectoryStream<Path> kids = Files.newDirectoryStream( dir ) ) {
            for( Path kid : kids ) {
            }
        }

        assertThat( Files.readAttributes( dir, BasicFileAttributes.class ).lastAccessTime() ).isGreaterThan( before );
    }

    @Test
    @Category( { SlowTest.class, Writable.class, Attributes.class, LastAccessTime.class } )
    @SuppressWarnings( "PMD.UnusedLocalVariable" )
    public void testReadEmptyDirStreamSetsLastAccessTime() throws Exception {
        Path dir = dirTA();
        FileTime before = Files.readAttributes( dir, BasicFileAttributes.class ).lastAccessTime();
        waitForAttribute();

        try( DirectoryStream<Path> kids = Files.newDirectoryStream( dir ) ) {
            for( Path kid : kids ) {
            }
        }

        assertThat( Files.readAttributes( dir, BasicFileAttributes.class ).lastAccessTime() ).isGreaterThan( before );
    }

    @Test
    @Category( { SlowTest.class, Writable.class, Attributes.class } )
    // changed attis are only relevant in writable cases
    public void testReadDirStreamDoesNotSetParentsLastAccessTime() throws Exception {
        Path dir = dirTA();
        FileTime before = Files.readAttributes( childGetParent( dir ), BasicFileAttributes.class ).lastAccessTime();
        waitForAttribute();

        Files.list( dir ).forEach( p -> {
        } );

        assertThat( Files.readAttributes( childGetParent( dir ), BasicFileAttributes.class ).lastAccessTime() ).isEqualTo( before );
    }

    // todo not fully defines
//    @Test
//    public void testGetIteratorOfClosedDirStream() throws Exception{
//        Path     file    = fileTAB();
//        fileTAC(); // 2nd kid
//        fileTAD(); // 3rd kid
//
//
//        try( DirectoryStream<Path> kids = Files.newDirectoryStream( file.getParent() ) ) {
//            kids.close();
//            int count = 0;
//            for ( Path kid : kids ) {
//                count++;
//            }
//
//            assertThat( count, lessThan(2) );
//        }
//    }

    @Test
    @SuppressWarnings( "PMD.UnusedLocalVariable" )
    public void testCloseDirStreamInTheMiddleOfIteration() throws Exception {

        Path dir = getNonEmptyDir();

        int size;
        try( DirectoryStream<Path> kids = Files.newDirectoryStream( dir ) ) {
            size = Utils.getSize( kids );
        }

        try( DirectoryStream<Path> kids = Files.newDirectoryStream( dir ) ) {
            int count = 0;
            for( Path kid : kids ) {
                count++;

                if( count == 1 ) {
                    kids.close();
                }

            }

            assertThat( count ).isLessThan( size );
        }
    }

    // todo should that work on unix
    // or only the open part ?
    @Test
    public void testReadBytesFromDirectoryThrows() throws IOException {
        assertThatThrownBy( () -> Files.readAllBytes( dirTA() ) ).isInstanceOf( Exception.class );
    }

    @Test
    public void testNewDirectoryStreamFromNonExistingDirThrows() throws IOException {
        assertThatThrownBy( () -> {
            try( DirectoryStream<Path> kids = Files.newDirectoryStream( getNonExistingPath() ) ) {
            }
        } ).isInstanceOf( NoSuchFileException.class );

    }


    /*
     * ------------------------------------------------------------------------------
     */

    @SuppressFBWarnings()
    protected static byte[] CONTENT;
    @SuppressFBWarnings()
    protected static byte[] CONTENT_OTHER;
    @SuppressFBWarnings()
    protected static byte[] CONTENT_BIG;
    @SuppressFBWarnings()
    protected static byte[] CONTENT50;

    @BeforeClass
    @SuppressFBWarnings
    public static void beforeDir() {
        CONTENT = getBytes( "hi there" );
        CONTENT_OTHER = getBytes( "what's up, huh, huh" );

        String str = new String( Character.toChars( 0x10400 ) );
        for( int i = 0; i < 12; i++ ) {
            str = str + str;
        }

        CONTENT_BIG = getBytes( str + "abcde" ); // not on 2^x bounderies

//        for( int i = 0; i < 20000; i++ ) {
//            CONTENT_BIG[ i ] = (byte) ( i );
//        }

        CONTENT50 = new byte[ 50 ];
        for( int i = 0; i < 50; i++ ) {
            CONTENT50[ i ] = (byte) ( i );
        }
    }

    public Path absT() {
        Path ret = description.get( Path.class, "playground" ).resolve( testMethodName.getMethodName() );
        Filess.createDirectories( ret );

        return ret;
    }

    public Path absTA() {
        return absT().resolve( nameA() );
    }

    public Path absTB() {
        return absT().resolve( nameB() );
    }

    public Path absTC() {
        return absT().resolve( nameC() );
    }

    public Path relTA() {
        Path abs = absTA();
        return pathDefault().toAbsolutePath().relativize( abs );
    }

    public Path absTAB() {
        return absTA().resolve( nameB() );
    }

    public Path absTAC() {
        return absTA().resolve( nameC() );
    }

    public Path dirTA() {
        Path ret = absTA();
        Filess.createDirectories( ret );
        return ret;
    }

    public Path dirTAB() {
        Path ret = absTAB();
        Filess.createDirectories( ret );
        return ret;
    }

    public Path dirTBB() {
        Path ret = absTB().resolve( nameB() );
        Filess.createDirectories( ret );
        return ret;
    }

    public Path dirTB() {
        Path ret = absTB();
        Filess.createDirectories( ret );
        return ret;
    }

    public Path fileTA() {
        Path ret = absTA();
        if( !Files.exists( ret ) ) {
            Filess.write( ret, CONTENT );
        }
        return ret;
    }

    public Path fileTB() {
        Path ret = absTB();
        if( !Files.exists( ret ) ) {
            Filess.write( ret, CONTENT );
        }
        return ret;
    }

    public Path fileTAB() {
        Path ret = dirTA().resolve( nameB() );
        if( !Files.exists( ret ) ) {
            Filess.write( ret, CONTENT );
        }
        return ret;
    }

    public Path relativize( Path path ) {

        //      return path.absoluteGetRoot().resolve( nameE()).relativize( path );
        return pathDefault().toAbsolutePath().relativize( path );
    }

    public Path fileTAC() {
        Path ret = dirTA().resolve( nameC() );
        if( !Files.exists( ret ) ) {
            Filess.write( ret, CONTENT );
        }
        return ret;
    }

    public Path fileTAD() {
        Path ret = dirTA().resolve( nameD() );
        if( !Files.exists( ret ) ) {
            Filess.write( ret, CONTENT );
        }
        return ret;
    }

    public void waitForAttribute() {
        try {
            Object del = description.props.get( "attributeDelay" );
            if( del != null ) {
                Thread.sleep( (Integer) del );
            } else {
                Thread.sleep( 50 );
            }
        } catch( InterruptedException e ) {
        }
    }

    public Path getNonEmptyDir() {
        if( !description.provides( Writable.class ) ) {
            return description.get( Path.class, "nonemptyDir" );
        }

        Path dir = dirTBB();
        Filess.write( dir.resolve( "one" ), CONTENT );
        Filess.write( dir.resolve( "two" ), CONTENT );
        return dir;
    }

    public Path getEmptyDir() {
        if( !description.provides( Writable.class ) ) {
            return description.get( Path.class, "emptyDir" );
        }

        return dirTA();
    }

    public Path getNonExistingPath() {
        if( !description.provides( Writable.class ) ) {
            return description.get( Path.class, "nonexisting" );
        }

        return absTA().resolve( "notthere" );
    }
}
