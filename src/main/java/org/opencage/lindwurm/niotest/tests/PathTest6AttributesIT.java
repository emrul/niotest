package org.opencage.lindwurm.niotest.tests;

import org.hamcrest.core.Is;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.FileStore;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.Map;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.collection.IsIn.isIn;
import static org.hamcrest.collection.IsMapContaining.hasKey;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assume.assumeThat;
import static org.junit.Assume.assumeTrue;
import static org.opencage.lindwurm.niotest.matcher.FileTimeMatcher.isCloseTo;

/**
 * ** BEGIN LICENSE BLOCK *****
 * BSD License (2 clause)
 * Copyright (c) 2006 - 2013, Stephan Pfab
 * All rights reserved.
 * <p/>
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * <p/>
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
public abstract class PathTest6AttributesIT extends PathTest5URIIT {

    @Test
    public void testGetLastModifiedTime() throws IOException {
        // expect no throw
        Files.readAttributes( getDefaultPath(), BasicFileAttributes.class ).lastModifiedTime();
    }

    @Test
    public void testGetCreationTimeIsRecent() throws IOException {
        // expect no throw
        FileTime created = Files.readAttributes( getPathPA().getParent(), BasicFileAttributes.class ).creationTime();

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
    public void testReadAttributesAskingForUnknownAttributesThrows() throws Exception {

        Files.readAttributes( getDefaultPath(), SomeFileAttributes.class );
    }


    @Test
    public void testSizeOfDirDoesNotThrow() throws IOException {
        // the behaviour of the size of a dir is unspecified (e.g. whether it changes with new kids)
        // but it should not throw

        Files.size( getDefaultPath() );
    }

    @Test
    public void testRootIsNotASymbolicLink() {
        assertThat( "root is a symbolic link", Files.isSymbolicLink( getRoot() ), not(true));
    }



    @Test
    public void testBasicIsASupportedFileAttributeView() {
        assertThat( FS.supportedFileAttributeViews(), hasItem( "basic" ));
    }

    @Test
    public void testGetLastModifiedViewAllMethodsDeliverSame() throws IOException {

        Path path = getPathPAf();

        FileTime last0 = Files.getLastModifiedTime( path );
        FileTime last1 = FS.provider().readAttributes( path, BasicFileAttributes.class ).lastModifiedTime();
        FileTime last2 = (FileTime) FS.provider().readAttributes( path, "basic:lastModifiedTime" ).get( "lastModifiedTime" );
        FileTime last3 = FS.provider().getFileAttributeView( path, BasicFileAttributeView.class ).readAttributes().lastModifiedTime();

        assertThat( last0, is(last1));
        assertThat( last1, is(last2));
        assertThat( last2, is(last3));
        assertThat( last3, is(last0));
    }

    @Test
    public void testGetAllBasicAttributes() throws IOException {
        Path path = getPathPAf();
        Map<String,Object> attis = FS.provider().readAttributes( path, "basic:*" );

        assertThat( attis.keySet(), containsInAnyOrder( "size",
                                                        "creationTime",
                                                        "lastAccessTime",
                                                        "lastModifiedTime",
                                                        "fileKey",
                                                        "isDirectory",
                                                        "isRegularFile",
                                                        "isSymbolicLink",
                                                        "isOther" ));


    }

    public static interface UnsiView extends BasicFileAttributeView {}

    @Test
    public void testUnsupportedAttributeViewReturnsNull() {
        assertThat( FS.provider().getFileAttributeView( getDefaultPath(), UnsiView.class ), nullValue() );
    }

    public static interface UnsiAttris extends BasicFileAttributes {}

    @Test( expected = UnsupportedOperationException.class )
    public void testUnsupportedAttributesThrows() throws IOException {
        FS.provider().readAttributes( getDefaultPath(), UnsiAttris.class );
    }

    @Test( expected = UnsupportedOperationException.class )
    public void testReadUnsupportedAttributesThrows() throws IOException {
        FS.provider().readAttributes( getDefaultPath(), "thisissuchasillyattributesname:duda" );
    }

    @Test( expected = IllegalArgumentException.class )
    public void testReadAttributesUnknownAttributeThrows() throws IOException {
        FS.provider().readAttributes( getDefaultPath(), "basic:duda" );
    }

    @Test( expected = UnsupportedOperationException.class)
    public void testSetUnsupportedAttributeThrows() throws IOException {
        FS.provider().setAttribute( getPathPAf(), "whoaa:freeze", true );
    }

    //    * @throws  UnsupportedOperationException
//    *          if the attribute view is not available
//    * @throws  IllegalArgumentException
//    *          if the attribute name is not specified or is not recognized
    @Test( expected = UnsupportedOperationException.class)
    public void testGetUnsupportedAttributeThrows() throws IOException {
        Files.getAttribute( getPathPAf(), "whoaa:freeze" );
    }
//    * @throws  UnsupportedOperationException
//    *          if the attribute view is not available
//    * @throws  IllegalArgumentException
//    *          if the attribute name is not specified or is not recognized
    @Test( expected = IllegalArgumentException.class )
    public void testGetUnsupportedAttributeThrows2() throws IOException {
        Files.getAttribute( getPathPAf(), "basic:freeze" );
    }


    @Test( expected = IllegalArgumentException.class )
    public void testReadAttributesOneUnknownAttributeThrows() throws IOException {
        FS.provider().readAttributes( getDefaultPath(), "basic:lastModifiedTime,duda" );
    }

    @Test
    public void testSetLastModifiedTimeViaString() throws IOException {
        FileTime past = FileTime.fromMillis( System.currentTimeMillis() - 100000 );
        final Path file = getPathPAf();
        FS.provider().setAttribute( file, "basic:lastModifiedTime", past );

        assertThat( Files.getLastModifiedTime( file ), isCloseTo( past ) );
    }

    @Test
    public void testSetLastModifiedTimeViaFiles() throws IOException {
        FileTime past = FileTime.fromMillis( System.currentTimeMillis() - 100000 );
        final Path file = getPathPAf();
        Files.setLastModifiedTime( file, past );

        assertThat( Files.getLastModifiedTime( file ), isCloseTo( past ) );
    }

    @Test
    public void testSetLastModifiedTimeViaView() throws IOException {
        FileTime past = FileTime.fromMillis( System.currentTimeMillis() - 100000 );
        final Path file = getPathPAf();
        FS.provider().getFileAttributeView( file, BasicFileAttributeView.class ).setTimes( past, null, null );

        assertThat( Files.getLastModifiedTime( file ), isCloseTo( past ) );
    }

//    @Test
//    public void testSetasdcdsLastModifiedTimeViaView() throws IOException {
//        final Path file = getPathPAf();
//        FileTime past = FileTime.fromMillis( System.currentTimeMillis() - 100000 );
//        BasicFileAttributeView view = FS.provider().getFileAttributeView( file, BasicFileAttributeView.class );
//        BasicFileAttributes attis = view.readAttributes();
//        view.setTimes( past, null, null );
//
//        assertThat( attis.lastModifiedTime(), isCloseTo( past ) );
//    }



    @Test( expected = IllegalArgumentException.class )
    public void testSetSizeThrows() throws IOException {
        FS.provider().setAttribute( getPathPAf(), "basic:size", 7 );
    }

    @Test( expected = IllegalArgumentException.class )
    public void testSetIsLinkThrows() throws IOException {
        FS.provider().setAttribute( getPathPAf(), "basic:isSymbolicLink", true );
    }

    @Test( expected = IllegalArgumentException.class )
    public void testSetIsDirectoryThrows() throws IOException {
        FS.provider().setAttribute( getPathPAf(), "basic:isDirectory", true );
    }

    @Test( expected = IllegalArgumentException.class )
    public void testSetIsRegularFileThrows() throws IOException {
        FS.provider().setAttribute( getPathPAf(), "basic:isRegularFile", true );
    }

    @Test( expected = IllegalArgumentException.class )
    public void testSetFileKeyThrows() throws IOException {
        FS.provider().setAttribute( getPathPAf(), "basic:fileKey", true );
    }

    @Test( expected = IllegalArgumentException.class )
    public void testSetIsOtherThrows() throws IOException {
        FS.provider().setAttribute( getPathPAf(), "basic:isOther", true );
    }

    @Test
    public void testSetCreationTimeViaString() throws IOException {
        assumeThat( capabilities.supportsCreationTime(), is(true));

        FileTime past = FileTime.fromMillis( System.currentTimeMillis() - 100000 );
        final Path file = getPathPAf();
        FS.provider().setAttribute( file, "basic:creationTime", past );

        assertThat( Files.readAttributes( file, BasicFileAttributes.class ).creationTime(), isCloseTo( past ) );
    }

    @Test
    public void testSetCreationTimeDoesNotChangeLastAccessTime() throws IOException, InterruptedException {
        assumeThat( capabilities.supportsLastAccessTime(), Is.is(true));

        final Path file = getPathPAf();
        FileTime before = Files.readAttributes( file, BasicFileAttributes.class ).lastAccessTime();
        Thread.sleep( 2000 );

        FS.provider().setAttribute( file, "basic:creationTime", FileTime.fromMillis( System.currentTimeMillis() - 100000 ));
        assertThat( Files.readAttributes( file, BasicFileAttributes.class ).lastAccessTime(), is(before) );
    }

    @Test
    public void testSetCreationTimeViaView() throws IOException {
        assumeThat( capabilities.supportsCreationTime(), is(true));

        FileTime past = FileTime.fromMillis( System.currentTimeMillis() - 100000 );
        final Path file = getPathPAf();
        FS.provider().getFileAttributeView( file, BasicFileAttributeView.class ).setTimes( null, null, past );

        assertThat( Files.readAttributes( file, BasicFileAttributes.class ).creationTime(), isCloseTo( past ) );
    }

    @Test
    public void testSetLastAccessTimeViaString() throws IOException {
        assumeThat( capabilities.supportsLastAccessTime(), is(true));

        FileTime past = FileTime.fromMillis( System.currentTimeMillis() - 100000 );
        final Path file = getPathPAf();
        FS.provider().setAttribute( file, "basic:lastAccessTime", past );

        assertThat( Files.readAttributes( file, BasicFileAttributes.class ).lastAccessTime(), isCloseTo( past ) );
    }

    @Test
    public void testSetLastAccessTimeViaView() throws IOException {
        assumeThat( capabilities.supportsLastAccessTime(), is(true));

        FileTime past = FileTime.fromMillis( System.currentTimeMillis() - 100000 );
        final Path file = getPathPAf();
        FS.provider().getFileAttributeView( file, BasicFileAttributeView.class ).setTimes( null, past, null );

        assertThat( Files.readAttributes( file, BasicFileAttributes.class ).lastAccessTime(), isCloseTo( past ) );
    }

    @Test( expected = NoSuchFileException.class )
    public void testGetAttributeFromNonExsitingFile() throws IOException {
        Files.getLastModifiedTime( getPathPA() );
    }

    @Test( expected = NoSuchFileException.class )
    public void testReadAttributesByStringFromNonExsitingFile() throws IOException {
        Files.readAttributes( getPathPA(), "basic:size" );
    }

    @Test
    public void testReadAttributesViewFromNonExsitingFile() throws IOException {
        assertThat( FS.provider().getFileAttributeView( getPathPA(), BasicFileAttributeView.class ), notNullValue() );
    }

    @Test( expected = NoSuchFileException.class )
    public void testReadAttributesViewAndReadFromNonExsitingFile() throws IOException {
        BasicFileAttributeView view =  FS.provider().getFileAttributeView( getPathPA(), BasicFileAttributeView.class );
        view.readAttributes();
    }

    @Test
    public void testReadAttributesViewFutureExistingFile() throws IOException {
        assumeThat( message(), possible(), is(true) );

        BasicFileAttributeView view =  FS.provider().getFileAttributeView( getPathPA(), BasicFileAttributeView.class );

        getPathPAf();
        view.readAttributes();
    }

//    @Test( expected = NoSuchFileException.class )
//    public void bugReadAttributesViewFutureExistingFileThrows() throws IOException {
//        assumeThat( message(), possible(), is(false) );
//
//        BasicFileAttributeView view =  FS.provider().getFileAttributeView( getPathPA(), BasicFileAttributeView.class );
//
//        getPathPAf();
//        view.readAttributes();
//    }


    @Test( expected = NoSuchFileException.class )
    public void testReadAttributesFromNonExsitingFile() throws IOException {
        Files.readAttributes( getPathPA(), BasicFileAttributes.class );
    }




//
//    @Test
//    public void testCopyResultHatCreationTime() throws Exception {
//
//        //define a folder root
//        Path myDir = FileSystems.getDefault().getPath( "/Users/stephan/tmp/foo" );//p.getTmpDir("");
//
//        try {
//            WatchService watcher = myDir.getFileSystem().newWatchService();
//            myDir.register(watcher, StandardWatchEventKinds.ENTRY_CREATE,
//                    StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY);
//
//            WatchKey watckKey = watcher.take();
//
//            List<WatchEvent<?>> events = watckKey.pollEvents();
//            for (WatchEvent event : events) {
//                if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
//                    System.out.println("Created: " + event.context().toString());
//                } else if (event.kind() == StandardWatchEventKinds.ENTRY_DELETE) {
//                    System.out.println("Delete: " + event.context().toString());
//                } else if (event.kind() == StandardWatchEventKinds.ENTRY_MODIFY) {
//                    System.out.println("Modify: " + event.context().toString());
//                } else {
//                    System.out.println("other: " + event.context());
//                }
//            }
//
//        } catch (Exception e) {
//            System.out.println("Error: " + e.toString());
//        }
//    }
//
//


}
