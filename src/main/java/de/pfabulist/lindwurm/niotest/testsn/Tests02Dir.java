package de.pfabulist.lindwurm.niotest.testsn;

import de.pfabulist.kleinod.paths.Filess;
import de.pfabulist.lindwurm.niotest.Utils;
import de.pfabulist.lindwurm.niotest.testsn.setup.Capa;
import org.hamcrest.Matchers;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;

import static de.pfabulist.kleinod.text.Strings.getBytes;
import static de.pfabulist.lindwurm.niotest.matcher.ExceptionMatcher.throwsException;
import static de.pfabulist.lindwurm.niotest.matcher.IteratorMatcher.isIn;
import static de.pfabulist.lindwurm.niotest.matcher.PathAbsolute.absolute;
import static de.pfabulist.lindwurm.niotest.matcher.PathAbsolute.relative;
import static de.pfabulist.lindwurm.niotest.matcher.PathExists.exists;
import static de.pfabulist.lindwurm.niotest.matcher.PathIsDirectory.isDirectory;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.number.OrderingComparison.greaterThan;
import static org.junit.Assert.*;

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
public abstract class Tests02Dir extends Tests01NoContent {

    public Tests02Dir( Capa capa) {
        super(capa);
    }

    @Test
    public void testDefaultIsDir() throws Exception {
        assertThat( pathDefault(), isDirectory() );
    }
    
    @Test
    public void testContentOfNonEmptyDir() throws IOException {
        fileTAB();
        fileTAC();
        
        try( DirectoryStream<Path> stream = Files.newDirectoryStream( dirTA()) ) {
            assertThat( Utils.getSize(stream), is(2));
        }
    }
    
    @Test
    public void testIteratorCanOnlyBeCalledOnceOnDirStream() throws IOException {
        try( DirectoryStream<Path> stream = Files.newDirectoryStream( dirTA() )) {
            stream.iterator();
            assertThat( stream::iterator, throwsException( IllegalStateException.class ));
        }
    }

    @Test( expected = UnsupportedOperationException.class )
    public void testDirStreamIteratorHasNoRemove() throws IOException {
        try( DirectoryStream<Path> stream = Files.newDirectoryStream( dirTA()) ) {
            stream.iterator().remove();
        }
    }


    @Test
    public void testContentOfNonEmptyDirFiltered() throws IOException {

        Path path = fileTAB().getParent();
        fileTAC(); // 2nd kid

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

        try( DirectoryStream<Path> stream = Files.newDirectoryStream( path, filter ) ) {
            assertThat(Utils.getSize(stream), is( 1 ));
        }
    }

    @Test
    public void testNewDirIsInParentsDirStream() throws IOException {

        Path dir = dirTA().resolve( nameB() );
        Files.createDirectory( dir );

        try( DirectoryStream<Path> kids = Files.newDirectoryStream( dir.getParent() ) ) {
            assertThat( dir, isIn( kids ));
        }
    }

    @Test
    public void testNewDirectoryExists() throws IOException {
        Files.createDirectory( absTA() );
        assertThat( absTA(), exists() );
    }

    @Test
    public void testNewRelDirectoryExists() throws IOException {
        Files.createDirectory( relTA() );
        assertThat( relTA(), exists() );
    }


    @Test
    public void testCreateDirectoryTwiceThrows() throws IOException {
        Path newDir = absTA();
        Files.createDirectory( newDir );
        assertThat( () -> {Files.createDirectory(newDir);}, throwsException( FileAlreadyExistsException.class ));
    }

    @Test( expected = NoSuchFileException.class )
    public void testCreateDirectoryWithoutExistingParentFails() throws IOException {
        Files.createDirectory( absTAB() );
    }

    @Test( expected = FileSystemException.class )
    public void testCreateDirectoryWithInFileFails() throws IOException {
        Files.createDirectory( fileTA().resolve( nameC()) );
    }
    
    @Test
    public void testRootisADir() throws IOException {
        assertThat( defaultRoot(), isDirectory() );
    }

    @Test
    public void testDefaultExists() throws Exception {
        assertThat( pathDefault(), exists() );
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
        assertThat( absTA(), not( isDirectory()));
    }

    @Test
    public void testNonExistingAbsolutePathIsNotADirectoryEvenIfParent() throws IOException {
        assertThat( absTAB().getParent(), not( isDirectory()));
    }
    

    // todo how to guarantee not existence ?
    @Test
    public void testNonExistingRelativePathIsNotADirectory() throws IOException {
        assertThat( relA(), not( isDirectory()));
    }
    
    @Test( expected = FileAlreadyExistsException.class )
    public void testCreateDirWithSamePathAsExistingFileFails() throws Exception {
        Files.createDirectory( fileTA() );
    }

    @Test
    @Category( SlowTest.class )
    public void testCreateDirSetsModifiedTimeOfParent() throws IOException, InterruptedException {
        Path dir = dirTA();
        FileTime created = Files.getLastModifiedTime( dir );
        waitForAttribute();

        Files.createDirectory( dir.resolve( nameB() ));
        assertThat(Files.getLastModifiedTime(dir), Matchers.greaterThan( created ));
    }

    @Test
    @Category( SlowTest.class )
    public void testCreateDirSetsLastAccessTimeOfParent() throws IOException, InterruptedException {
        Path dir = dirTA();
        FileTime before = Files.readAttributes( dir, BasicFileAttributes.class ).lastAccessTime();
        waitForAttribute();

        Files.createDirectory( dir.resolve(nameB()) );
        assertThat(Files.readAttributes(dir, BasicFileAttributes.class).lastAccessTime(), greaterThan(before));
    }

    @Test
    @Category( SlowTest.class )
    public void testCreateDirSetsCreationTime() throws IOException, InterruptedException {
        Path dir = absTA();
        FileTime before = Files.getLastModifiedTime( dir.getParent() );
        waitForAttribute();

        Files.createDirectory( dir );

        BasicFileAttributes atti = Files.readAttributes( dir, BasicFileAttributes.class );

        assertThat( atti.creationTime(), greaterThan( before ) );
    }

    @Test
    public void testKidsOfAbsoluteDirAreAbsolute() throws Exception {
        try( DirectoryStream<Path> kids = Files.newDirectoryStream( fileTAB().getParent() ) ) {
            for( Path kid : kids ) {
                assertThat( kid, absolute() );
            }
        }
    }

    @Test
    public void testKidsOfRelativeDirAreRelative() throws Exception {
        try( DirectoryStream<Path> kids = Files.newDirectoryStream( relativize(fileTAB()).getParent() ) ) {
            for( Path kid : kids ) {
                assertThat( kid, relative() );
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
        Path dir = relativize(fileTAB()).getParent();

        try( DirectoryStream<Path> kids = Files.newDirectoryStream( dir )) {
            for( Path kid : kids ) {
                assertThat( kid, is( dir.resolve( kid.getFileName())));
            }
        }
    }

    @Test
    @Category( SlowTest.class )
    public void testReadDirStreamSetsLastAccessTime() throws Exception{

        Path     dir    = fileTAB().getParent();
        FileTime before  = Files.readAttributes( dir, BasicFileAttributes.class ).lastAccessTime();
        waitForAttribute();

        try( DirectoryStream<Path> kids = Files.newDirectoryStream( dir ) ) {
            for( Path kid : kids ) {
            }
        }

        assertThat( Files.readAttributes( dir, BasicFileAttributes.class ).lastAccessTime(), greaterThan( before ));
    }

    @Test
    @Category( SlowTest.class )
    public void testReadEmptyDirStreamSetsLastAccessTime() throws Exception{
        Path     dir    = dirTA();
        FileTime before  = Files.readAttributes( dir, BasicFileAttributes.class ).lastAccessTime();
        waitForAttribute();

        try( DirectoryStream<Path> kids = Files.newDirectoryStream( dir ) ) {
            for( Path kid : kids ) {
            }
        }

        assertThat( Files.readAttributes( dir, BasicFileAttributes.class ).lastAccessTime(), greaterThan( before ));
    }

    @Test
    public void testReadDirStreamDoesNotSetParentsLastAccessTime() throws Exception{
        Path     dir    = dirTA();
        FileTime before  = Files.readAttributes( dir.getParent(), BasicFileAttributes.class ).lastAccessTime();
        waitForAttribute();

        try( DirectoryStream<Path> kids = Files.newDirectoryStream( dir ) ) {
            for( Path kid : kids ) {
            }
        }

        assertThat( Files.readAttributes( dir.getParent(), BasicFileAttributes.class ).lastAccessTime(), is( before ));
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
    public void testCloseDirStreamInTheMiddleOfIteration() throws Exception{
        Path     file    = fileTAB();
        fileTAC(); // 2nd kid
        fileTAD(); // 3rd kid


        try( DirectoryStream<Path> kids = Files.newDirectoryStream( file.getParent() ) ) {
            int count = 0;
            for ( Path kid : kids ) {
                count++;

                if ( count == 1 ) {
                    kids.close();
                }

            }

            assertThat( count, lessThan(3) );
        }
    }

    // todo should that work on unix
    // or only the open part ?
    @Test( expected = Exception.class )
    public void testReadBytesFromDirectoryThrows() throws IOException {
        Files.readAllBytes( dirTA());
    }

    @Test( expected = NoSuchFileException.class )
    public void testNewDirectoryStreamFromNonExistingDirThrows() throws IOException {
        try( DirectoryStream<Path> kids = Files.newDirectoryStream( absTA() ) ) {
        }

    }


    /*
     * ------------------------------------------------------------------------------
     */

    protected static byte[] CONTENT;
    protected static byte[] CONTENT_OTHER;
    protected static byte[] CONTENT20k;
    protected static byte[] CONTENT50;

    @BeforeClass
    public static void beforeDir() {
        CONTENT = getBytes("hi there" );
        CONTENT_OTHER = getBytes( "what's up, huh, huh" );

        CONTENT20k = new byte[20000];
        for ( int i = 0; i < 20000; i++ ) {
            CONTENT20k[i] = (byte) (i);
        }

        CONTENT50 = new byte[50];
        for ( int i = 0; i < 50; i++ ) {
            CONTENT50[i] = (byte) (i);
        }
    }



    public Path absT() {
        Path ret = capa.get( Path.class, "playground" ).resolve( testMethodName.getMethodName() );
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
        return pathDefault().toAbsolutePath().relativize(abs);
    }

    public Path absTAB() {
        return absTA().resolve(nameB());
    }

    public Path absTAC() {
        return absTA().resolve(nameC());
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

    public Path dirTB() {
        Path ret = absTB();
        Filess.createDirectories( ret );
        return ret;
    }

    public Path fileTA() {
        Path ret = absTA();
        if ( !Files.exists( ret )) {
            Filess.write(ret, CONTENT);
        }
        return ret;
    }

    public Path fileTB() {
        Path ret = absTB();
        if ( !Files.exists( ret )) {
            Filess.write(ret, CONTENT);
        }
        return ret;
    }

    public Path fileTAB() {
        Path ret = dirTA().resolve( nameB() );
        if ( !Files.exists( ret )) {
            Filess.write(ret, CONTENT);
        }
        return ret;
    }

    public Path relativize( Path path ) {
        return pathDefault().toAbsolutePath().relativize( path );
    }

    public Path fileTAC() {
        Path ret = dirTA().resolve( nameC() );
        if ( !Files.exists( ret )) {
            Filess.write(ret, CONTENT);
        }
        return ret;
    }

    public Path fileTAD()  {
        Path ret = dirTA().resolve( nameD() );
        if ( !Files.exists( ret )) {
            Filess.write(ret, CONTENT);
        }
        return ret;
    }


    public void waitForAttribute() {
    try {
        Thread.sleep( 2000 ); //capabilities.attributeDelay() );
    } catch (InterruptedException e) {
    }
    }

}
