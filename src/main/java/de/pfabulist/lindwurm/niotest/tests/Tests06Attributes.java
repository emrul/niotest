package de.pfabulist.lindwurm.niotest.tests;

import de.pfabulist.lindwurm.niotest.tests.attributes.AttributeDescription;
import de.pfabulist.lindwurm.niotest.tests.topics.Attributes;
import de.pfabulist.lindwurm.niotest.tests.topics.CreationTime;
import de.pfabulist.lindwurm.niotest.tests.topics.LastAccessTime;
import de.pfabulist.lindwurm.niotest.tests.topics.SlowTest;
import de.pfabulist.lindwurm.niotest.tests.topics.Writable;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.nio.file.attribute.UserDefinedFileAttributeView;
import java.util.Map;

import static de.pfabulist.lindwurm.niotest.matcher.ExceptionMatcher.throwsException;
import static de.pfabulist.lindwurm.niotest.matcher.FileTimeMatcher.isCloseTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertThat;
import static org.junit.Assume.assumeThat;

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
public abstract class Tests06Attributes extends Tests05URI {

    public Tests06Attributes( FSDescription capa ) {
        super( capa );
    }

    @Test
    @Category( Attributes.class )
    public void testGetLastModifiedTime() throws IOException {
        // expect no throw
        Files.readAttributes( pathDefault(), BasicFileAttributes.class ).lastModifiedTime();
    }

    @Test
    @Category( { Attributes.class, Writable.class } )
    public void testGetCreationTimeIsRecent() throws IOException {
        // expect no throw
        FileTime created = Files.readAttributes( fileTA(), BasicFileAttributes.class ).creationTime();

        assertThat( created, isCloseTo( FileTime.fromMillis( System.currentTimeMillis() ) ) );
    }

    public static class SomeFileAttributes implements BasicFileAttributes {

        @Override
        public FileTime lastModifiedTime() {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public FileTime lastAccessTime() {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public FileTime creationTime() {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public boolean isRegularFile() {
            return false;  //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public boolean isDirectory() {
            return false;  //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public boolean isSymbolicLink() {
            return false;  //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public boolean isOther() {
            return false;  //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public long size() {
            return 0;  //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public Object fileKey() {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }
    }

    @Test( expected = UnsupportedOperationException.class )
    @Category( Attributes.class )
    public void testReadAttributesAskingForUnknownAttributesThrows() throws Exception {
        Files.readAttributes( pathDefault(), SomeFileAttributes.class );
    }

    @Test
    @Category( Attributes.class )
    public void testSizeOfDirDoesNotThrow() throws IOException {
        // the behaviour of the size of a dir is unspecified (e.g. whether it changes with new kids)
        // but it should not throw
        Files.size( pathDefault() );
    }

    @Test
    @Category( Attributes.class )
    public void testRootIsNotASymbolicLink() {
        assertThat( "root is a symbolic link", Files.isSymbolicLink( defaultRoot() ), not( true ) );
    }

    @Test
    @Category( Attributes.class )
    public void testBasicIsASupportedFileAttributeView() {
        assertThat( FS.supportedFileAttributeViews(), hasItem( "basic" ) );
    }

    @Test
    @Category( Attributes.class )
    public void testGetLastModifiedAllMethodsDeliverSame() throws IOException {

        Path path = getFile();

        FileTime last0 = Files.getLastModifiedTime( path );
        FileTime last1 = FS.provider().readAttributes( path, BasicFileAttributes.class ).lastModifiedTime();
        FileTime last2 = (FileTime) FS.provider().readAttributes( path, "basic:lastModifiedTime" ).get( "lastModifiedTime" );
        FileTime last3 = FS.provider().getFileAttributeView( path, BasicFileAttributeView.class ).readAttributes().lastModifiedTime();

        assertThat( last0, is( last1 ) );
        assertThat( last1, is( last2 ) );
        assertThat( last2, is( last3 ) );
        assertThat( last3, is( last0 ) );
    }

    @Test
    @Category( Attributes.class )
    public void testAllGetAttributeMethodsDeliverSame() throws IOException {

        description.getAttributeDescriptions().forEach( ad -> {
            try {

                Path path = getFile();

                for( Object okey : ad.getAttributeNames() ) {

                    String key = (String) okey;

                    Object last1 = ad.get( FS.provider().readAttributes( path, ad.getReadType() ), key );
                    Object last2 = FS.provider().readAttributes( path, ad.getName() + ":" + key ).get( key );
                    Object last3 = ad.get( FS.provider().getFileAttributeView( path, ad.getViewType() ).readAttributes(), key );

                    assertThat( "get attribute " + ad.getName() + ":" + key, last1, is( last2 ) );
                    assertThat( "get attribute " + ad.getName() + ":" + key, last2, is( last3 ) );
                }
            } catch( IOException e ) {
            }
        } );
    }

    @Test
    @Category( Attributes.class )
    public void testGetLastModifiedViaStringOfRelativePath() throws IOException {
        Path path = getFile();
        assertThat( FS.provider().readAttributes( pathDefault().toAbsolutePath().relativize( path ), "basic:lastModifiedTime" ).get( "lastModifiedTime" ),
                    is( FS.provider().readAttributes( path, "basic:lastModifiedTime" ).get( "lastModifiedTime" ) ) );
    }

    @Test
    @Category( Attributes.class )
    public void testGetAllBasicAttributes() throws IOException {
        Map<String, Object> attis = FS.provider().readAttributes( getFile(), "basic:*" );

        assertThat( attis.keySet(), containsInAnyOrder( "size",
                                                        "creationTime",
                                                        "lastAccessTime",
                                                        "lastModifiedTime",
                                                        "fileKey",
                                                        "isDirectory",
                                                        "isRegularFile",
                                                        "isSymbolicLink",
                                                        "isOther" ) );

    }

    @Test
    @Category( Attributes.class )
    public void testGetAllFooAttributes() {

        description.getAttributeDescriptions().forEach( ad -> {

            try {
                Map<String, Object> attis = FS.provider().readAttributes( getFile(), ad.getName() + ":*" );

                for( String key : ad.getAttributeNames() ) {
                    assertThat( ad.getName() + ":" + key, attis.containsKey( key ), is( true ) );
                }
            } catch( IOException e ) {
                assertThat( "failed", is( "not thrown" ) );
            }
        } );
    }

    public static interface UnsiView extends BasicFileAttributeView {
    }

    @Test
    @Category( Attributes.class )
    public void testUnsupportedAttributeViewReturnsNull() {
        assertThat( FS.provider().getFileAttributeView( pathDefault(), UnsiView.class ), nullValue() );
    }

    public static interface UnsiAttris extends BasicFileAttributes {
    }

    @Test( expected = UnsupportedOperationException.class )
    @Category( Attributes.class )
    public void testUnsupportedAttributesThrows() throws IOException {
        FS.provider().readAttributes( pathDefault(), UnsiAttris.class );
    }

    @Test( expected = UnsupportedOperationException.class )
    @Category( Attributes.class )
    public void testReadUnsupportedAttributeThrows() throws IOException {
        FS.provider().readAttributes( pathDefault(), "thisissuchasillyattributesname:duda" );
    }

    @Test( expected = IllegalArgumentException.class )
    @Category( Attributes.class )
    public void testReadAttributesUnknownAttributeThrows() throws IOException {
        FS.provider().readAttributes( pathDefault(), "basic:duda" );
    }

    @Test( expected = UnsupportedOperationException.class )
    @Category( Attributes.class )
    public void testSetUnsupportedAttributeThrows() throws IOException {
        FS.provider().setAttribute( fileTA(), "whoaa:freeze", true );
    }

    //    * @throws  UnsupportedOperationException
//    *          if the attribute view is not available
//    * @throws  IllegalArgumentException
//    *          if the attribute name is not specified or is not recognized
    @Test( expected = UnsupportedOperationException.class )
    @Category( Attributes.class )
    public void testGetUnsupportedAttributeThrows() throws IOException {
        Files.getAttribute( fileTA(), "whoaa:freeze" );
    }

    @Test
    @Category( Attributes.class )
    public void testGetUnsupportedAttributeThrows2() throws IOException {

        description.getAttributeDescriptions().forEach(
                ad -> assertThat( "attribute view " + ad.getName(),
                                  () -> Files.getAttribute( getFile(), ad.getName() + ":areallystupidname" ),
                                  throwsException( IllegalArgumentException.class ) ) );
    }

    @Test( expected = IllegalArgumentException.class )
    @Category( Attributes.class )
    public void testReadAttributesOneUnknownAttributeThrows() throws IOException {
        FS.provider().readAttributes( pathDefault(), "basic:lastModifiedTime,duda" );
    }

    @Test
    @Category( { Attributes.class, Writable.class } )
    public void testSetLastModifiedTimeViaString() throws IOException {
        FileTime past = FileTime.fromMillis( System.currentTimeMillis() - 100000 );
        final Path file = getFile();
        FS.provider().setAttribute( file, "basic:lastModifiedTime", past );

        assertThat( Files.getLastModifiedTime( file ), isCloseTo( past ) );
    }

    @Test
    @Category( { Attributes.class, Writable.class } )
    public void testSetLastModifiedTimeViaFiles() throws IOException {
        FileTime past = FileTime.fromMillis( System.currentTimeMillis() - 100000 );
        final Path file = fileTA();
        Files.setLastModifiedTime( file, past );

        assertThat( Files.getLastModifiedTime( file ), isCloseTo( past ) );
    }

    @Test
    @Category( { Attributes.class, Writable.class } )
    public void testSetLastModifiedTimeViaView() throws IOException {
        FileTime past = FileTime.fromMillis( System.currentTimeMillis() - 100000 );
        final Path file = getFile();
        FS.provider().getFileAttributeView( file, BasicFileAttributeView.class ).setTimes( past, null, null );

        assertThat( Files.getLastModifiedTime( file ), isCloseTo( past ) );
    }

    //todo
//    @Test
//    @Category( Attributes.class )     public void testSetasdcdsLastModifiedTimeViaView() throws IOException {
//        final Path file = fileTA();
//        FileTime past = FileTime.fromMillis( System.currentTimeMillis() - 100000 );
//        BasicFileAttributeView view = FS.provider().getFileAttributeView( file, BasicFileAttributeView.class );
//        BasicFileAttributes attis = view.readAttributes();
//        view.setTimes( past, null, null );
//
//        assertThat( attis.lastModifiedTime(), isCloseTo( past ) );
//    }

    @Test( expected = IllegalArgumentException.class )
    @Category( { Attributes.class, Writable.class } )
    public void testSetSizeThrows() throws IOException {
        FS.provider().setAttribute( fileTA(), "basic:size", 7 );
    }

    @Test( expected = IllegalArgumentException.class )
    @Category( { Attributes.class, Writable.class } )
    public void testSetIsLinkThrows() throws IOException {
        FS.provider().setAttribute( fileTA(), "basic:isSymbolicLink", true );
    }

    @Test( expected = IllegalArgumentException.class )
    @Category( { Attributes.class, Writable.class } )
    public void testSetIsDirectoryThrows() throws IOException {
        FS.provider().setAttribute( fileTA(), "basic:isDirectory", true );
    }

    @Test( expected = IllegalArgumentException.class )
    @Category( { Attributes.class, Writable.class } )
    public void testSetIsRegularFileThrows() throws IOException {
        FS.provider().setAttribute( fileTA(), "basic:isRegularFile", true );
    }

    @Test( expected = IllegalArgumentException.class )
    @Category( { Attributes.class, Writable.class } )
    public void testSetFileKeyThrows() throws IOException {
        FS.provider().setAttribute( fileTA(), "basic:fileKey", true );
    }

    @Test( expected = IllegalArgumentException.class )
    @Category( { Attributes.class, Writable.class } )
    public void testSetIsOtherThrows() throws IOException {
        FS.provider().setAttribute( fileTA(), "basic:isOther", true );
    }

    @Test
    @Category( { Attributes.class, Writable.class, Attributes.class, CreationTime.class } )
    public void testSetCreationTimeViaString() throws IOException {
        FileTime past = FileTime.fromMillis( System.currentTimeMillis() - 100000 );
        final Path file = fileTA();
        FS.provider().setAttribute( file, "basic:creationTime", past );

        assertThat( Files.readAttributes( file, BasicFileAttributes.class ).creationTime(), isCloseTo( past ) );
    }

    @Test
    @Category( { Attributes.class, SlowTest.class, Writable.class } )
    public void testSetCreationTimeDoesNotChangeLastAccessTime() throws IOException, InterruptedException {
        final Path file = fileTA();
        FileTime before = Files.readAttributes( file, BasicFileAttributes.class ).lastAccessTime();
        waitForAttribute();

        FS.provider().setAttribute( file, "basic:creationTime", FileTime.fromMillis( System.currentTimeMillis() - 100000 ) );
        assertThat( Files.readAttributes( file, BasicFileAttributes.class ).lastAccessTime(), is( before ) );
    }

    @Test
    @Category( { Attributes.class, Writable.class, Attributes.class, CreationTime.class } )
    public void testSetCreationTimeViaView() throws IOException {
        FileTime past = FileTime.fromMillis( System.currentTimeMillis() - 100000 );
        final Path file = fileTA();
        FS.provider().getFileAttributeView( file, BasicFileAttributeView.class ).setTimes( null, null, past );

        assertThat( Files.readAttributes( file, BasicFileAttributes.class ).creationTime(), isCloseTo( past ) );
    }

    @Test
    @Category( { Attributes.class, Writable.class, LastAccessTime.class } )
    public void testSetLastAccessTimeViaString() throws IOException {
        FileTime past = FileTime.fromMillis( System.currentTimeMillis() - 100000 );
        final Path file = fileTA();
        FS.provider().setAttribute( file, "basic:lastAccessTime", past );

        assertThat( Files.readAttributes( file, BasicFileAttributes.class ).lastAccessTime(), isCloseTo( past ) );
    }

    @Test
    @Category( { Attributes.class, Writable.class, LastAccessTime.class } )
    public void testSetLastAccessTimeViaView() throws IOException {
        FileTime past = FileTime.fromMillis( System.currentTimeMillis() - 100000 );
        final Path file = fileTA();
        FS.provider().getFileAttributeView( file, BasicFileAttributeView.class ).setTimes( null, past, null );

        assertThat( Files.readAttributes( file, BasicFileAttributes.class ).lastAccessTime(), isCloseTo( past ) );
    }

    @Test( expected = NoSuchFileException.class )
    @Category( Attributes.class )
    public void testGetAttributeFromNonExistingFile() throws IOException {
        Files.getLastModifiedTime( getNonExistingPath() );
    }

    @Test( expected = NoSuchFileException.class )
    @Category( Attributes.class )
    public void testReadAttributesByStringFromNonExistingFile() throws IOException {
        Files.readAttributes( getNonExistingPath(), "basic:size" );
    }

    @Test
    @Category( Attributes.class )
    public void testReadAttributesViewFromNonExistingFile() throws IOException {
        assertThat( FS.provider().getFileAttributeView( getNonExistingPath(), BasicFileAttributeView.class ), notNullValue() );
    }

    @Test( expected = NoSuchFileException.class )
    @Category( Attributes.class )
    public void testReadAttributesViewAndReadFromNonExistingFile() throws IOException {
        BasicFileAttributeView view = FS.provider().getFileAttributeView( getNonExistingPath(), BasicFileAttributeView.class );
        view.readAttributes();
    }

    @Test
    @Category( { Attributes.class, Writable.class } )
    public void testReadAttributesViewFutureExistingFile() throws IOException {
        BasicFileAttributeView view = FS.provider().getFileAttributeView( absTA(), BasicFileAttributeView.class );

        fileTA();
        view.readAttributes();
    }

    @Test( expected = NoSuchFileException.class )
    @Category( Attributes.class )
    public void testReadAttributesFromNonExistingFile() throws IOException {
        Files.readAttributes( getNonExistingPath(), BasicFileAttributes.class );
    }

    @Test
    @Category( Attributes.class )
    public void testFileKeyIsId() throws IOException {
        assumeThat( Files.readAttributes( getFile(), BasicFileAttributes.class ).fileKey(), notNullValue() );

        assertThat( Files.readAttributes( getFile(), "basic:fileKey" ),
                    is( not( Files.readAttributes( getNonEmptyDir(), "basic:fileKey" ) ) ) ); // nonEmptyDir here is just another existing path
    }

    @Test
    @Category( Attributes.class )
    public void testUserDefinedAttributes() {
        Files.getFileAttributeView( getFile(), UserDefinedFileAttributeView.class );
    }

}
