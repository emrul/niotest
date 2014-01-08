package org.opencage.niotest.tests;

import org.junit.Ignore;
import org.junit.Test;
import org.opencage.kleinod.collection.Forall;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.ClosedFileSystemException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

import static java.nio.file.StandardOpenOption.READ;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;
import static org.opencage.kleinod.text.Strings.getBytes;
import static org.opencage.niotest.matcher.PathAbsolute.absolute;
import static org.opencage.niotest.matcher.PathExists.exists;

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
public abstract class PathTest6IT extends PathTest5IT {

    @Test
    public void testGetLastModifiedTime() throws IOException {
        // expect no throw
        Files.readAttributes( getDefaultPath(), BasicFileAttributes.class ).lastModifiedTime();
    }

//    @Test
//    public void testGetCreationTime() throws IOException {
//        // expect no throw
//        Files.readAttributes( p.readOnlyFileSystem.getPath( "" ), BasicFileAttributes.class ).creationTime();
//    }
//
//    @Test
//    public void testGetLastModifiedTimeCorrect() throws IOException {
//        Path tmp = p.getTmpDir( "getLastModifiedTimeCorrect" );
//        Path tgt = tmp.resolve( "tgt" );
//
//        long first = System.currentTimeMillis();
//
//        Files.write( tgt, getBytes( "Hallo World" ));
//
//        long then = System.currentTimeMillis();
//
//        FileTime time = Files.readAttributes( tgt, BasicFileAttributes.class ).lastModifiedTime();
//
//        assertTrue( "file time before creation", (first - 1000) <= time.toMillis() );
//        assertTrue( "file time after finish", (then + 1000)>= time.toMillis() );
//    }
//
//
//    public static class SomeFileAttributes implements BasicFileAttributes {
//
//        @Override
//        public FileTime lastModifiedTime() {
//            return null;  //To change body of implemented methods use File | Settings | File Templates.
//        }
//
//        @Override
//        public FileTime lastAccessTime() {
//            return null;  //To change body of implemented methods use File | Settings | File Templates.
//        }
//
//        @Override
//        public FileTime creationTime() {
//            return null;  //To change body of implemented methods use File | Settings | File Templates.
//        }
//
//        @Override
//        public boolean isRegularFile() {
//            return false;  //To change body of implemented methods use File | Settings | File Templates.
//        }
//
//        @Override
//        public boolean isDirectory() {
//            return false;  //To change body of implemented methods use File | Settings | File Templates.
//        }
//
//        @Override
//        public boolean isSymbolicLink() {
//            return false;  //To change body of implemented methods use File | Settings | File Templates.
//        }
//
//        @Override
//        public boolean isOther() {
//            return false;  //To change body of implemented methods use File | Settings | File Templates.
//        }
//
//        @Override
//        public long size() {
//            return 0;  //To change body of implemented methods use File | Settings | File Templates.
//        }
//
//        @Override
//        public Object fileKey() {
//            return null;  //To change body of implemented methods use File | Settings | File Templates.
//        }
//    }
//
//    @Test( expected = UnsupportedOperationException.class )
//    public void testReadAttributesAskingForUnknownAttributesThrows() throws Exception {
//
//        Files.readAttributes( p.readOnlyFileSystem.getPath( "" ), SomeFileAttributes.class );
//    }
//
//
//    @Test
//    public void testSizeOfDir() throws IOException {
//        // the behaviour of the size of a dir is unspecified (e.g. whether it changes with new kids)
//        // but it should not throw
//
//        Files.size( p.readOnlyFileSystem.getPath( "" ) );
//    }
//
//
//
//    @Test
//    public void testModifiedDateDoesNotChangeModifiedDateOfParent() throws IOException, InterruptedException {
//
//        Path tmp = p.getTmpDir("testModifiedDateDoesNotChangeModifiedDateOfParent");
//        Files.createDirectory( tmp.resolve( p.getLegalPathElement() ) );
//        FileTime created = Files.getLastModifiedTime( tmp );
//
//        Thread.sleep( 2000 );
//
//        Files.createDirectory( tmp.resolve( p.getLegalPathElement() ).resolve( p.getLegalPathElement( 1 )));
//        FileTime modified = Files.getLastModifiedTime( tmp );
//
//        assertEquals( created, modified );
//    }
//
//
//    @Test
//    public void testModifyFileNotSetsModifiedDateOfParent() throws IOException, InterruptedException {
//
//        Path tmp = p.getTmpDir("testCreateFileSetsModifiedDateOfParent");
//        Files.write( tmp.resolve( "foo" ), "hh".getBytes() );
//
//        FileTime created = Files.getLastModifiedTime( tmp );
//
//        Thread.sleep( 2000 );
//
//        FileTime modified = Files.getLastModifiedTime( tmp );
//        Files.write( tmp.resolve( "foo" ), getBytes( "duh" ) );
//
//        assertEquals( created, modified  );
//    }
//
//    @Test
//    public void testDeleteDirChangesParentTime() throws IOException, InterruptedException {
//        Path tmp = p.getTmpDir("testDeleteDirChangesParentTime");
//        final Path dir = tmp.resolve( p.getLegalPathElement() );
//        Files.createDirectory( dir );
//        FileTime created = Files.getLastModifiedTime( tmp );
//
//        Thread.sleep( 2000 );
//
//        Files.delete( dir );
//        FileTime modified = Files.getLastModifiedTime( tmp );
//
//        assertTrue( "delete does not modify time", created.compareTo( modified ) < 0 );
//    }
//
//    @Test
//    public void testDeleteFileChangesParentTime() throws IOException, InterruptedException {
//        Path tmp = p.getTmpDir("testDeleteDirChangesParentTime");
//        final Path file = tmp.resolve( p.getLegalPathElement() );
//        Files.write( file, "Hallo duh".getBytes( "UTF-8" ) );
//        FileTime created = Files.getLastModifiedTime( tmp );
//
//        Thread.sleep( 2000 );
//
//        Files.delete( file );
//        FileTime modified = Files.getLastModifiedTime( tmp );
//
//        assertTrue( "delete does not modify time", created.compareTo( modified ) < 0 );
//    }
//
//    @Test
//    public void testDeleteDirRemovesItFromParentsKids() throws IOException, InterruptedException {
//        Path tmp = p.getTmpDir("testDeleteDirRemovesItFromParentsKids");
//        final Path dir = tmp.resolve( p.getLegalPathElement() );
//        Files.createDirectory( dir );
//        Files.delete( dir );
//
//        try ( DirectoryStream<Path> kids = Files.newDirectoryStream( tmp ) ) {
//            assertFalse( "delete dir does not remove it from parent stream", Forall.forall( kids ).contains( dir ));
//        }
//    }
//
//

    @Test
    public void testDeleteDeletes() throws Exception{
        final Path file = getPathPA();
        Files.write( file, CONTENT );
        Files.delete( file );
        assertThat( file, not( exists()) );
    }

    @Test
    public void testDeleteFileRemovesItFromParentsKids() throws IOException, InterruptedException {
        final Path file = getPathPA();
        Files.write( file, CONTENT );
        Files.delete( file );
        try ( DirectoryStream<Path> kids = Files.newDirectoryStream( file.getParent() ) ) {
            assertFalse( "delete dir does not remove it from parent stream", Forall.forall( kids ).contains( file ));
        }
    }
//
//    @Test
//    public void testCreateRelativeDirectory() throws IOException {
//        Path abs = p.getTmpDir("createRelativeDirectory").resolve( p.getLegalPathElement() );
//        Path rel = abs.getFileSystem().getPath( "" ).toAbsolutePath().relativize( abs );
//
//        Files.createDirectory( rel );
//        assertThat( rel, exists() );
//    }
//
//
//
//
//
//    @Test( expected = java.nio.file.DirectoryNotEmptyException.class )
//    public void testDeleteNonEmptyDirectoryThrows() throws IOException {
//        Files.delete( p.getNonEmptyDir("deleteNonEmpty"));
//    }
//
//    @Test
//    public void testDeleteEmptyDir() throws IOException {
//        Path dir = p.getTmpDir("testDeleteEmptyDir").resolve( p.getLegalPathElement() );
//        Files.createDirectory( dir );
//        assertThat( dir, exists() );
//        Files.delete( dir );
//        assertThat( dir, not( exists() ) );
//    }
//
//
//    // another parent of root problem
//    @Ignore
//    @Test
//    public void testDeleteRoot() throws IOException {
//        Files.delete( p.readOnlyFileSystem.getPath( "" ).getRoot() );
//    }
//
//
//    @Ignore
//    @Test
//    public void testFSClosable() throws IOException {
//        p.readOnlyFileSystem.close();
//    }
//
//    @Test
//    public void testRootIsSymbolicLinkNot() {
//        assertFalse( "root is a symbolic link", Files.isSymbolicLink( p.readOnlyFileSystem.getPath( p.readOnlyFileSystem.getSeparator() )));
//    }
//
//    @Test
//    public void testReadFromExausted() throws IOException {
//        Path tmp = p.getTmpDir( "testReadFormExausted" ).resolve( p.getLegalPathElement() );
//
//        Files.write( tmp, "hallo".getBytes( "UTF-8" ) );
//        SeekableByteChannel channel = tmp.getFileSystem().provider().newByteChannel( tmp, Collections.singleton( READ ));
//
//        channel.read( ByteBuffer.allocate( 500 ) );
//        ByteBuffer bb = ByteBuffer.allocate( 500 );
//        int ret = channel.read( bb );
//
//        assertEquals( -1, ret );
//        assertEquals( 0, bb.position() );
//    }
//
//
//    @Test
//    public void testIsSameFileOnEqualPath() throws IOException {
//        Path tmp = p.getTmpDir("testIsSameFile");
//        Path path1 = tmp.resolve( "path1" );
//        Path path2 = tmp.resolve( "path1" );
//
//        assertTrue( "should be same", tmp.getFileSystem().provider().isSameFile( path1, path2 ) );
//    }
//
//    @Ignore // todo: is this a java 7 bug ?
//    @Test
//    public void testIsSameFileDespiteUnormalizedPath() throws IOException {
//        Path tmp = p.getTmpDir("testIsSameFile");
//        Path path1 = tmp.resolve( "path1" );
//        Files.createDirectories( path1 );
//        assertThat( path1, exists() );
//
//        Path path2 = tmp.resolve( "a" ).resolve( ".." ).resolve( "path1" );
//        // normalize() and then it works
//
//        assertTrue( "should be same", tmp.getFileSystem().provider().isSameFile( path1, path2 ) );
//    }
//
//    @Ignore // todo: is this a java 7 bug ?
//    @Test
//    public void testIsSameFileDespiteUnormalizedPath3() throws IOException {
//        Path tmp = p.getTmpDir("testIsSameFile");
//        Path path1 = tmp.resolve( "path1" );
//        Files.write( path1, "huhu".getBytes("UTF-8") );
//        assertThat( path1, exists() );
//
//        Path path2 = tmp.resolve( "a" ).resolve( ".." ).resolve( "path1" );
//
//        assertThat( path2, exists() );
//
//        assertTrue( "should be same", tmp.getFileSystem().provider().isSameFile( path1, path2 ) );
//    }
//
//    @Ignore // todo: is this a java 7 bug ?
//    @Test
//    public void testIsSameFileDespiteUnormalizedPath4() throws IOException {
//        Path tmp = p.getTmpDir("testIsSameFile");
//        Path path1 = tmp.resolve( "path1" );
//        Files.write( path1, "huhu".getBytes("UTF-8") );
//        assertThat( path1, exists() );
//
//        Path path2 = tmp.getFileSystem().getPath( "" ).toAbsolutePath().relativize( path1 );
//        Path path3 = path2.toAbsolutePath();
//
//        assertThat( path2, exists() );
//
//        assertTrue( "should be same", tmp.getFileSystem().provider().isSameFile( path1, path2 ) );
//        assertTrue( "should be same", tmp.getFileSystem().provider().isSameFile( path1, path3 ) );
//    }
//
//    //@Ignore // todo: is this a java 7 bug ?
//    @Test
//    public void testIsSameFileDespiteUnormalizedPath2() throws IOException {
//        Path tmp = p.getTmpDir("testIsSameFile");
//        Path path1 = tmp.resolve( "path1" );
//        Files.write( path1, "hi".getBytes( "UTF-8" ) );
//
//        Path path2 = tmp.resolve( "a" ).resolve( ".." ).resolve( "path1" );
//
//        assertTrue( "should be same", tmp.getFileSystem().provider().isSameFile( path1, path2.normalize() ) );
//    }
//
//    @Test
//    public void testIsSameFileNot() throws IOException {
//        Path tmp = p.getTmpDir("testIsSameFileNot");
//        Path path1 = tmp.resolve( "path1" );
//        Path path2 = tmp.resolve( "path2" );
//
//        Files.write( path1, "Hallo World".getBytes("UTF-8") );
//        Files.write( path2, "Hallo World".getBytes("UTF-8") );
//
//
//        assertFalse( "should not be same", tmp.getFileSystem().provider().isSameFile( path1, path2 ) );
//    }
//
//    @Test( expected = NoSuchFileException.class )
//    public void testIsSameFileNotExists() throws IOException {
//        Path tmp = p.getTmpDir("testIsSameFileNotExists");
//        Path path1 = tmp.resolve( "path1" );
//        Path path2 = tmp.resolve( "path2" );
//
//        tmp.getFileSystem().provider().isSameFile( path1, path2 );
//    }
//
//
//    @Test
//    public void testClosedFSisClosed() throws Exception {
//
//        assumeTrue( p.getCapabilities().canBeClosed());
//
//        FileSystem fs = p.getTmpDir( "testClosedFSisClosed" ).getFileSystem();
//        fs.close();
//
//        assertFalse( "should be closed", fs.isOpen() );
//
//    }
//
//    @Test( expected = ClosedFileSystemException.class )
//    public void testOpenDirectoryStreamFromClosedFSThrows() throws Exception {
//        assumeTrue( p.getCapabilities().canBeClosed());
//
//        p.readOnlyFileSystem.close();
//
//        try ( DirectoryStream<Path> ch = Files.newDirectoryStream( p.readOnlyFileSystem.getPath( "" ) )) {
//        }
//    }
//
//    @Test( expected = NoSuchFileException.class)
//    public void testToRealpathOnNonExistingFileThrows() throws Exception {
//        Path path = p.getTmpDir(  ).resolve( "foooooooooo" );
//        path.toRealPath();
//    }
//
//    @Test
//    public void testToRealOnExistingFileIsAbsolute() throws Exception {
//        Path path = p.getTmpDir(  ).resolve( "foooooooooo" );
//        Files.write( path, "hhh".getBytes( "UTF-8" ) );
//        assertThat( path.toRealPath(), absolute() );
//    }
//
//    @Test
//    public void testToRealOnExistingFileIsNormalized() throws Exception {
//        Path path = p.getTmpDir(  ).resolve( "foooooooooo" );
//        Files.write( path, "hhh".getBytes( "UTF-8" ) );
//        assertEquals( path.toRealPath(), path.toRealPath().normalize() );
//    }
//
//
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
//    @Test( expected = UnsupportedOperationException.class )
//    public void testNotWatchableThrowsUnsuportedOp() throws IOException {
//        assumeTrue( !p.getCapabilities().isWatchable() );
//
//        p.readOnlyFileSystem.newWatchService();
//
//    }
//
//    @Test
//    @Ignore
//    public void testWatchADelete() throws Exception {
//        assumeTrue( p.getCapabilities().isWatchable() );
//
//        final ConcurrentLinkedDeque<Path> dels = new ConcurrentLinkedDeque<>();
//
//        Path dir = p.getTmpDir();
//        Path toBeDeleted = dir.resolve( "toDel" );
//        Files.write( toBeDeleted, getBytes( "foo" ));
//
//        new Thread( new Watcher( dir, dels ) ).start();
//
//        System.out.println("t 1");
//        Thread.sleep( 5000 );
//        System.out.println( "t 2" );
//
//        Thread.sleep( 5000 );
//        Files.delete( toBeDeleted );
//
//        System.out.println("t 3");
//
//
//        Thread.sleep( 1000 );
//
//        System.out.println("t 4");
//
//
//        assertEquals( "there should be a delete", 1, dels.size() );
//
//    }
//
//    private class Watcher implements Runnable {
//        private final Path dir;
//        private final ConcurrentLinkedDeque<Path> dels;
//
//        public Watcher( Path dir, ConcurrentLinkedDeque<Path> dels ) {
//            this.dir = dir;
//            this.dels = dels;
//        }
//
//        @Override
//        public void run() {
//            try {
//                WatchService watcher = dir.getFileSystem().newWatchService();
//                dir.register(watcher, StandardWatchEventKinds.ENTRY_DELETE );
//
//                System.out.println("watcher is running. watching " + dir);
//
//                WatchKey watckKey = watcher.take();
//
//                System.out.println("watcher is running2");
//
//
//                List<WatchEvent<?>> events = watckKey.pollEvents();
//
//                System.out.println("watcher is running3");
//
//                for (WatchEvent event : events) {
//                    if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
//                        System.out.println("Created: " + event.context().toString());
//                    } else if (event.kind() == StandardWatchEventKinds.ENTRY_DELETE) {
//                        System.out.println("Delete: " + event.context().toString());
//                        dels.add( (Path) event.context() );
//                    } else if (event.kind() == StandardWatchEventKinds.ENTRY_MODIFY) {
//                        System.out.println("Modify: " + event.context().toString());
//                    } else {
//                        System.out.println("other: " + event.context());
//                    }
//                }
//
//            } catch (Exception e) {
//                e.printStackTrace();
//                System.out.println("Error: " + e.toString());
//            }
//        }
//    }




}
