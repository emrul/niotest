package de.pfabulist.lindwurm.niotest.tests;

import de.pfabulist.lindwurm.niotest.tests.topics.Attributes;
import de.pfabulist.lindwurm.niotest.tests.topics.SlowTest;
import de.pfabulist.unchecked.Filess;
import de.pfabulist.lindwurm.niotest.tests.topics.Copy;
import de.pfabulist.lindwurm.niotest.tests.topics.Delete;
import de.pfabulist.lindwurm.niotest.tests.topics.DirSymLink;
import de.pfabulist.lindwurm.niotest.tests.topics.FileStores;
import de.pfabulist.lindwurm.niotest.tests.topics.HardLink;
import de.pfabulist.lindwurm.niotest.tests.topics.Move;
import de.pfabulist.lindwurm.niotest.tests.topics.SymLink;
import de.pfabulist.lindwurm.niotest.tests.topics.Writable;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.NoSuchFileException;
import java.nio.file.NotLinkException;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;

import static de.pfabulist.lindwurm.niotest.matcher.PathExists.exists;
import static de.pfabulist.lindwurm.niotest.matcher.PathIsDirectory.isDirectory;
import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
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
public abstract class Tests20SymLinks extends Tests19HardLinks {

    public Tests20SymLinks( FSDescription capa ) {
        super( capa );
    }


    @Test
    @Category( { SymLink.class, Writable.class } )
    public void testCreateSymLink() throws IOException {
        Files.createSymbolicLink( symLink(), targetFile() );
        assertThat( symLink(), exists() );
    }

    @Test( expected = FileAlreadyExistsException.class )
    @Category( { SymLink.class, Writable.class } )
    public void testCreateSymLinkWhereLinkExistsThrows() throws IOException {
        Files.write( symLink(), CONTENT );
        Files.createSymbolicLink( symLink(), targetFile() );
    }

    @Test
    @Category( { SymLink.class, Writable.class } )
    public void testCreateSymLinkToNonExisting() throws IOException {
        Files.createSymbolicLink( link(), absTA() );
    }

    @Test
    @Category( { SymLink.class } )
    public void testSymLinkAttributeNonLink() throws IOException {
        assertThat( Files.isSymbolicLink( targetFile() ), is( false ) );
    }

    @Test
    @Category( { SymLink.class } )
    public void testSymLinkAttributeLink() throws IOException {
        Files.createSymbolicLink( symLink(), targetDir() );
        assertThat( Files.isSymbolicLink( symLink() ), is( true ) );
    }

//    // todo
//    @Test
//    @Category({ SymLink.class})  public void testRealPathofAnUnnormalizedDeepSymLink() throws IOException {
//
//        Files.createSymbolicLink( link(), targetDir());
//        Files.write(link().getParent().resolve(nameB()), CONTENT);
//        Files.write( targetDir().getParent().resolve(nameB()), CONTENT_OTHER );
//
//        Path unnorm = link().resolve( "..").resolve( nameB());
////        System.out.println(link());
////        System.out.println(targetDir());
////        System.out.println( unnorm.toRealPath());
////        System.out.println( unnorm.toRealPath(NOFOLLOW_LINKS));
////        System.out.println( link().getParent().resolve(nameB()).toRealPath());
//    }

    @Test
    @Category( { SymLink.class } )
    public void testReadFromSymLink() throws IOException {
        Files.createSymbolicLink( symLink(), targetFile() );
        assertThat( Files.readAllBytes( symLink() ), is( CONTENT ) );
    }

    @Test
    @Category( { SymLink.class } )
    public void testReadFromSymLinkToSymLink() throws IOException {
        Files.createSymbolicLink( symLink(), targetFile() );
        Files.createSymbolicLink( symLink2(), symLink() );

        assertThat( Files.readAllBytes( symLink2() ), is( CONTENT ) );
    }

    @Test
    @Category( { SymLink.class, Writable.class } )
    public void testWriteToSymLink() throws IOException {
        Files.createSymbolicLink( symLink(), targetFile() );
        Files.write( symLink(), CONTENT_OTHER );

        assertThat( Files.readAllBytes( targetFile() ), is( CONTENT_OTHER ) );
    }

    @Test
    @Category( { SymLink.class, DirSymLink.class } )
    public void testDirSymLink() throws IOException {
        Files.createSymbolicLink( symLink(), targetDir() );

        assertThat( Files.isDirectory( symLink() ), is( true ) );
    }

    @Test
    @Category( { SymLink.class, DirSymLink.class } )
    public void testStreamFromDirSymLink() throws IOException {
        Files.createSymbolicLink( symLink(), targetDir() );

        try( DirectoryStream<Path> stream = Files.newDirectoryStream( symLink() ) ) {
            for( Path kid : stream ) {
//                System.out.println(kid);
                assertThat( kid.getParent(), is( symLink() ) );
                assertThat( Files.isSameFile( kid, targetDirKid() ), is( true ) );
            }
        }
    }

    @Test
    @Category( { SymLink.class, DirSymLink.class } )
    public void testCreateFileInDirSymLink() throws IOException {
        Files.createSymbolicLink( symLink(), targetDir() );
        Files.write( linkKid(), CONTENT );
        assertThat( linkKid(), exists() );
    }

    @Test
    @Category( { SymLink.class, DirSymLink.class } )
    public void testFileInDirSymLinkIsNotSymLink() throws IOException {
        Files.createSymbolicLink( symLink(), targetDir() );
        Files.write( linkKid(), CONTENT );

        assertThat( Files.isSymbolicLink( linkKid() ), is( false ) );
    }

    @Test
    @Category( { SymLink.class, DirSymLink.class } )
    public void testCreateDirInDirSymLink() throws IOException {
        Files.createSymbolicLink( symLink(), targetDir() );
        Files.createDirectory( linkKid() );
        assertThat( linkKid(), isDirectory() );
    }

    @Test
    @Category( { SymLink.class, Writable.class, Delete.class } )
    public void testDeleteSymLinkDoesNotDeleteTarget() throws IOException {
        Files.createSymbolicLink( symLink(), targetFile() );
        Files.delete( symLink() );
        assertThat( targetFile(), exists() );
        assertThat( symLink(), not( exists() ) );
    }

    @Test
    @Category( { SymLink.class } )
    public void testDeleteSymLinkDoesNotDeleteTargetNonEmptyDirCase() throws IOException {
        Files.createSymbolicLink( symLink(), targetDir() );
        Files.write( symLink().resolve( "kid" ), CONTENT );
        Files.delete( symLink() );
        assertThat( symLink(), not( exists() ) );
    }

    @Test
    @Category( { SymLink.class, Writable.class, Copy.class } )
    public void testCopyFromSymLink() throws IOException {
        Files.createSymbolicLink( symLink(), targetFile() );
        Files.copy( symLink(), tgt() );
        assertThat( Files.readAllBytes( tgt() ), is( CONTENT ) );
    }

    @Test
    @Category( { SymLink.class, DirSymLink.class, Writable.class, Copy.class } )
    public void testCopyFromDeeperSymLink() throws IOException {
        Files.createSymbolicLink( symLink(), targetDir() );
        Files.write( linkKid(), CONTENT );

        Files.copy( linkKid(), tgt() );

        assertThat( Files.readAllBytes( tgt() ), is( CONTENT ) );
    }

    @Test( expected = FileAlreadyExistsException.class )
    @Category( { SymLink.class } )
    public void testCopyToBrokenSymLink() throws IOException {
        Files.createSymbolicLink( symLink(), absTA() );
        Files.copy( fileTAB(), symLink() );
    }

    @Test
    @Category( { SymLink.class, Copy.class, Writable.class } )
    public void testCopyBrokenSymLinkToItself() throws IOException {
        Files.createSymbolicLink( symLink(), absTA() );
        Files.copy( symLink(), symLink() ); // no throw
    }

    @Test
    @Category( { SymLink.class, Copy.class, Writable.class } )
    public void testCopySymLinkToItself() throws IOException {
        Files.createSymbolicLink( symLink(), targetFile() );
        Files.copy( symLink(), symLink() );
        assertThat( Files.readAllBytes( symLink() ), is( CONTENT ) );
    }

    @Test
    @Category( { SymLink.class, Copy.class, Writable.class } )
    public void testCopyToSymLink() throws IOException {
        // specified in Files.copy
        Files.createSymbolicLink( symLink(), targetFile() );
        Files.copy( fileTAB(), symLink(), REPLACE_EXISTING );
        assertThat( Files.isSymbolicLink( symLink() ), is( false ) );
    }

    @Test
    @Category( { SymLink.class, DirSymLink.class, Copy.class, Writable.class } )
    public void testCopyToDeepSymLink() throws IOException {
        Files.createSymbolicLink( symLink(), targetDir() );
        Files.copy( fileTAB(), linkKid() );

        assertThat( Files.readAllBytes( linkKid() ), is( CONTENT ) );
        assertThat( Files.readAllBytes( linkKid().toRealPath() ), is( CONTENT ) );
    }

    @Test
    @Category( { SymLink.class, HardLink.class, Writable.class } )
    public void testHardLinkToSymLink() throws IOException {
        Files.createSymbolicLink( symLink(), targetFile() );
        Files.createLink( hardLink(), symLink() );
        assertThat( Files.readAllBytes( hardLink() ), is( CONTENT ) );
    }

    // todo: it is not clear what the result should be
    // @Test
    // @Category({ SymLink.class})  public void testHardLinkToSymLinkIsNotASymLink() throws IOException {
    //     Files.createSymbolicLink( symLink(), targetFile());
    //     Files.createLink( hardLink(), symLink() );
    //     assertThat( Files.isSymbolicLink( hardLink()), is( false ));
    // }

    @Test
    @Category( { SymLink.class, HardLink.class, Delete.class, Writable.class } )
    public void testHardLinkToSymLinkDeleteSymLeavesHardLinkUntouched() throws IOException {
        Files.createSymbolicLink( symLink(), targetFile() );
        Files.createLink( hardLink(), symLink() );

        Files.delete( symLink() );

        assertThat( Files.readAllBytes( hardLink() ), is( CONTENT ) );
    }

//    @Test( expected = FileSystemException.class )
//    @Category({ SymLink.class})  public void testSymLinkToParent() throws IOException {
//        Files.createSymbolicLink( symLink(), symLink().getParent() );
//    }
//
//    //  think a -> b, b/d -> a
//    @Test( expected = FileSystemException.class )
//    @Category({ SymLink.class})  public void testSymLinkComplexLoop() throws IOException {
//        Files.createSymbolicLink( symLink(), targetDir());
//        Files.createSymbolicLink( targetDir().resolve(nameD()), symLink());
//    }

    @Test( expected = NoSuchFileException.class ) // TODO correct error ?
    @Category( { SymLink.class } )
    public void testBrokenSymLink() throws IOException {
        Files.createSymbolicLink( symLink(), targetFile() );
        Files.delete( targetFile() );
        Files.readAllBytes( symLink() );
    }

    @Test
    @Category( { SymLink.class } )
    public void testBrokenSymLinkNotExists() throws IOException {
        Files.createSymbolicLink( symLink(), absTA() );
        assertThat( symLink(), not( exists() ) );
    }

    @Test
    @Category( { SymLink.class } )
    public void testBrokenSymLinkNoFollowLinkExists() throws IOException {
        Files.createSymbolicLink( symLink(), absTA() );
        assertThat( Files.exists( symLink(), LinkOption.NOFOLLOW_LINKS ), is( true ) );
    }

    @Test
    @Category( { SymLink.class } )
    public void testBrokenSymLinkThrowsOnIsSymlink() throws IOException {
        Files.createSymbolicLink( symLink(), absTA() );
        assertThat( Files.isSymbolicLink( symLink() ), is( true ) );
    }

    @Test
    @Category( { SymLink.class } )
    public void testBrokenSymLinkGetAttisNoFollowLinkDoesWorks() throws IOException {
        Files.createSymbolicLink( symLink(), absTA() );
        BasicFileAttributes aa = Files.readAttributes( symLink(), BasicFileAttributes.class, LinkOption.NOFOLLOW_LINKS );
        assertThat( aa.isSymbolicLink(), is( true ) );
    }

    @Test( expected = NoSuchFileException.class )
    @Category( { SymLink.class } )
    public void testBrokenSymLinkDoesNotTestForSymlinkAttribute() throws IOException {
        Files.createSymbolicLink( symLink(), absTA() );
        Files.readAttributes( symLink(), BasicFileAttributes.class );
    }

//    @Test
//    @Category({ SymLink.class})  public void testBrokenSymLinkDoesNotTeastForSymlinkAttribute() throws IOException {
//        Files.createSymbolicLink(symLink(), absTA());
//        System.out.println( Files.readSymbolicLink( symLink()));
//    }

    @Test
    @Category( { SymLink.class } )
    public void testDeleteBrokenSymLink() throws IOException {
        Files.createSymbolicLink( symLink(), absTA() );
        Files.delete( symLink() );
    }

    @Test
    @Category( { SymLink.class } )
    public void testGetSymLinkTarget() throws IOException {
        Files.createSymbolicLink( symLink(), targetFile() );
        assertThat( Files.readSymbolicLink( symLink() ), is( targetFile() ) );
    }

    @Test( expected = NotLinkException.class )
    @Category( { SymLink.class } )
    public void testGetSymLinkOfNonLinkThrows() throws IOException {
        Files.readSymbolicLink( targetFile() );
    }

    @Test
    @Category( { SymLink.class } )
    public void testReadAttributesFromSymLinkNoFollowLinks() throws IOException, InterruptedException {
        targetFile();
        Thread.sleep( 2000 );
        Files.createSymbolicLink( symLink(), targetFile() );

        FileTime orig = Files.readAttributes( symLink(), BasicFileAttributes.class ).creationTime();
        FileTime linkTime = Files.readAttributes( symLink(), BasicFileAttributes.class, NOFOLLOW_LINKS ).creationTime();

        assertThat( linkTime, greaterThan( orig ) );
    }

    @Test
    @Category( { SymLink.class } )
    public void testAttributesFromSymLinkWithFollowLinkShowNoLink() throws IOException {
        Files.createSymbolicLink( symLink(), targetFile() );

        BasicFileAttributes bfa = Files.readAttributes( symLink(), BasicFileAttributes.class );
        assertThat( bfa.isSymbolicLink(), is( false ) );
    }

    @Test
    @Category( { SymLink.class } )
    public void testAttributesFromSymLinkWithFollowLinkShowNoLink_View() throws IOException {
        Files.createSymbolicLink( symLink(), targetFile() );

        BasicFileAttributeView bfav = Files.getFileAttributeView( symLink(), BasicFileAttributeView.class );
        assertThat( bfav.readAttributes().isSymbolicLink(), is( false ) );
    }

    @Test
    @Category( { SymLink.class } )
    public void testMoveSymLinkFileTestContent() throws IOException {
        Files.createSymbolicLink( symLink(), targetFile() );
        Files.move( symLink(), absTA() );

        assertThat( Files.readAllBytes( absTA() ), is( CONTENT ) );
    }

    @Test
    @Category( { SymLink.class } )
    public void testMoveSymLinkMovesLink() throws IOException {
        Files.createSymbolicLink( symLink(), targetFile() );
        Files.move( symLink(), absTC() );
        assertThat( symLink(), not( exists() ) );
    }

    @Test
    @Category( { SymLink.class, Writable.class, Move.class } )
    public void testMoveSymLinkLeavesTarget() throws IOException {
        Files.createSymbolicLink( symLink(), targetFile() );
        Files.move( symLink(), absTC() );
        assertThat( targetFile(), exists() );
    }

    @Test
    @Category( { SymLink.class, Writable.class, Move.class } )
    public void testMoveSymLinkFileKeepsSymLinkStatus() throws IOException {
        Files.createSymbolicLink( symLink(), targetFile() );
        Files.move( symLink(), tgt() );

        assertThat( Files.isSymbolicLink( tgt() ), is( true ) );
    }

    @Test
    @Category( { SymLink.class, FileStores.class } )
    public void testFileStoreOfSymLinkIsTargets() throws IOException {

        Files.createSymbolicLink( symLink(), targetFile() );

        assertThat( Files.getFileStore( symLink() ), is( Files.getFileStore( targetFile() ) ) );
    }

    @Test
    @Category( { SymLink.class } )
    public void testSymLinkAndTargetAreSameFile() throws IOException {

        Files.createSymbolicLink( symLink(), targetFile() );

        assertThat( Files.isSameFile( symLink(), targetFile() ), is( true ) );
    }

    @Test
    @Category( { SymLink.class, DirSymLink.class } )
    public void testDeepSymLinkIsSameFile() throws IOException {

        Files.createSymbolicLink( symLink(), targetDir() );
        Files.write( linkKid(), CONTENT );

        assertThat( Files.isSameFile( linkKid(), targetDir().resolve( linkKid().getFileName() ) ), is( true ) );
    }

    // no otherproviderlinks
//    @Test
//    @Category({ SymLink.class})  public void testSymLinkToOtherProviderExists() throws IOException {
//        Files.createSymbolicLink( symLink(), otherProviderTargetFile() );
//        assertThat( symLink(), exists());
//    }

    @Test
    @Category( { SymLink.class } )
    public void testDoubleSymLinkExists() throws IOException {
        Files.createSymbolicLink( symLink(), targetDir() );
        Files.createSymbolicLink( linkKid(), fileTAB() );

        assertThat( linkKid(), exists() );
    }

    @Test
    @Category( { SymLink.class, DirSymLink.class } )
    public void testDoubleSymLinkSameFile() throws IOException {
        Files.createSymbolicLink( symLink(), targetDir() );
        Files.createSymbolicLink( linkKid(), fileTAB() );

        assertThat( Files.isSameFile( linkKid(), fileTAB() ), is( true ) );
    }

    @Test
    @Category( { SymLink.class, DirSymLink.class } )
    public void testDoubleSymLinkReadAttributes() throws IOException, InterruptedException {
        fileTAB();
        Thread.sleep( 1100 );
        Files.createSymbolicLink( symLink(), targetDir() );
        Files.createSymbolicLink( linkKid(), fileTAB() );

        assertThat( Files.readAttributes( linkKid(), "basic:lastModifiedTime" ).get( "lastModifiedTime" ),
                    is( Files.readAttributes( fileTAB(), "basic:lastModifiedTime" ).get( "lastModifiedTime" ) ) );

    }

    @Test
    @Category( { SymLink.class } )
    public void testDeepSymLinkMove() throws IOException {
        Files.createSymbolicLink( symLink(), targetDir() );
        Files.write( linkKid(), CONTENT );
        Files.move( linkKid(), tgt() );

        assertThat( Files.readAllBytes( tgt() ), is( CONTENT ) );
    }

    @Test
    @Category( { SymLink.class, DirSymLink.class, Writable.class, Move.class } )
    public void testMoveToDeepSymLink() throws IOException {
        Files.createSymbolicLink( symLink(), targetDir() );
        Files.move( fileTAB(), linkKid() );

        assertThat( Files.readAllBytes( linkKid() ), is( CONTENT ) );
    }

    @Test
    @Category( { SymLink.class, DirSymLink.class } )
    public void testDoubleSymLinkReadAttributesNoFollowLink() throws IOException, InterruptedException {
        fileTAB();
        Thread.sleep( 1100 );
        Files.createSymbolicLink( symLink(), targetDir() );
        Files.createSymbolicLink( linkKid(), fileTAB() );

        assertThat( (FileTime) Files.readAttributes( linkKid(), "basic:lastModifiedTime", NOFOLLOW_LINKS ).get( "lastModifiedTime" ),
                    greaterThan( (FileTime) Files.readAttributes( fileTAB(), "basic:lastModifiedTime" ).get( "lastModifiedTime" ) ) );

    }

    // todo check access and sym links

    // no otherproviderlinks
//    @Test
//    @Category({ SymLink.class})  public void testReadFromSymLinkToOtherProvider() throws IOException {
//        Files.createSymbolicLink( symLink(), otherProviderTargetFile() );
//        assertThat( Files.readAllBytes( symLink()), is(CONTENT));
//    }

    // no otherproviderlinks
//    @Test
//    @Category({ SymLink.class})  public void testGetSymLinkToOtherProviderTarget() throws IOException {
//        Files.createSymbolicLink( symLink(), otherProviderTargetFile() );
//        assertThat( Files.readSymbolicLink( symLink()), is(otherProviderTargetFile()));
//    }

    // no otherproviderlinks
//    @Test
//    @Category({ SymLink.class})  public void testGetDirStreamFromSymLinkToOtherProvider() throws IOException {
//        Files.createSymbolicLink(symLink(), otherProviderTargetDir());
//
//        try ( DirectoryStream<Path> stream = Files.newDirectoryStream( symLink())) {
//            for ( Path kid : stream ) {
//                assertThat( Files.readAllBytes( kid), is(CONTENT));
//            }
//        }
//    }

    // no otherproviderlinks
//    @Test
//    @Category({ SymLink.class})  public void testIsSameFileSymLinkToOtherProvider() throws IOException {
//        Files.createSymbolicLink( symLink(), otherProviderTargetFile() );
//        Files.createSymbolicLink( symLink2(), otherProviderTargetFile() );
//        assertThat( Files.isSameFile( symLink(), symLink2()), is( true ));
//    }

    @Test
    @Category( { SymLink.class } )
    public void testSymLinkChanges() throws IOException {
        Path target = targetFile();
        Files.createSymbolicLink( symLink(), target );
        Files.delete( target );
        Files.createDirectory( target );

        assertThat( symLink(), isDirectory() );
    }

    // no otherproviderlinks
//    @Test
//    @Category({ SymLink.class})  public void testMoveToDeepSymLinkToOtherProvider() throws IOException {
//        Files.createSymbolicLink(symLink(), otherProviderTargetDir());
//        Files.move(fileTAB(), linkKid());
//
//        assertThat( Files.readAllBytes(linkKid()), is(CONTENT_OTHER));
//    }

//    // todo more tests with rel links
//    @Test
//    @Category({ SymLink.class})  public void testRelSymLink() throws IOException {
//        Files.createSymbolicLink( symLink(), relA() );
//        Files.move( symLink(), absTB());
//
//        System.out.println( Files.readSymbolicLink(symLink()));
//
//    }

    @Test
    @Category( { SymLink.class } )
    public void testRelSymLinkIsRelativeToLink() throws IOException {
        Files.createSymbolicLink( symLink(), FS.getPath( ".." ).resolve( nameA() ) );
        Files.write( symLink().getParent().getParent().resolve( nameA() ), CONTENT_OTHER );
        assertThat( Files.readAllBytes( symLink() ), is( CONTENT_OTHER ) );
        //assertThat( Files.isSameFile( symLink(), symLink().getParent().resolve( nameA())), is(true));
    }

    @Test
    @Category( { SymLink.class } )
    public void testMoveARelSymLink2() throws IOException {
        Files.createSymbolicLink( symLink(), FS.getPath( ".." ).resolve( nameA() ) );
        Path to = dirTB().resolve( nameB() );
        Files.move( symLink(), to );
        Files.write( to, CONTENT );
        assertThat( Files.isSameFile( to, absTA() ), is( true ) );
    }

//    //Todo
//    @Test
//    @Category({ SymLink.class})  public void testMoveSymLinkLeavesNormalPath() throws IOException {
//        Files.createSymbolicLink( dirTB().resolve(nameB()), FS.getPath("..").resolve(nameA()));
//
//        Files.write( symLink(), CONTENT );
//    }

//    @Test
//    @Category({ SymLink.class})  public void testCreateSymlinkWithLastModifiedTime() throws IOException {
//        Files.createSymbolicLink( symLink(), absTA(), new FileAttribute<FileTime>() {
//            @Override
//            public String name() {
//                return "basic:lastModifiedTime";
//            }
//
//            @Override
//            public FileTime value() {
//                return FileTime.fromMillis( 1 );
//            }
//        } );
//
//        System.out.println( Files.getLastModifiedTime( symLink(), LinkOption.NOFOLLOW_LINKS ));
//    }

    @Test
    @Category( { SymLink.class, SlowTest.class, Attributes.class } )
    public void testMoveSymLinkDoesNotChangeLastModifiedTime() throws IOException {
        Files.createSymbolicLink( symLink(), fileTA() );
        FileTime before = Files.getLastModifiedTime( symLink(), LinkOption.NOFOLLOW_LINKS );
        waitForAttribute();
        Files.move( symLink(), absTB() );
        assertThat( Files.getLastModifiedTime( absTB(), LinkOption.NOFOLLOW_LINKS ), is( before ) );
    }

    @Test
    @Category( { SymLink.class } )
    public void testMoveARelSymLink() throws IOException {
        Files.createSymbolicLink( symLink(), relA() );
        Files.move( symLink(), absTB() );
        Files.write( absTB(), CONTENT );

        assertThat( Files.isSameFile( absTB(), absTA() ), is( true ) );
    }

    @Test
    @Category( { SymLink.class } )
    public void testMoveReplaceExistingToSymLink() throws IOException {
        Files.createSymbolicLink( symLink(), targetFile() );
        Files.move( fileTA(), symLink(), StandardCopyOption.REPLACE_EXISTING );
        assertThat( Files.isSymbolicLink( symLink() ), is( false ) );
    }

    @Test
    @Category( { SymLink.class, DirSymLink.class } )
    public void testUnnormalizedDeepSymLinkFollowsLinkBeforeNormalization() throws IOException {
        Path sym = dirTA().resolve( "link" );
        Files.createSymbolicLink( sym, dirTBB() );

        Files.write( dirTA().resolve( nameC() ), CONTENT );
        Files.write( dirTB().resolve( nameC() ), CONTENT_OTHER );

        assertThat( Files.isSameFile( sym.resolve( ".." ).resolve( nameC() ), dirTB().resolve( nameC() ) ), is( true ) );
    }

    @Test
    @Category( { SymLink.class } )
    public void testSymLinkToUnnormalizedRelPath() throws IOException {
        Files.createSymbolicLink( symLink(), FS.getPath( nameA() ).resolve( ".." ) );
        Files.createSymbolicLink( symLink().getParent().resolve( nameA() ), targetFile() );

        // System.out.println( symLink().toRealPath());
        // System.out.println( targetFile().getParent().toRealPath());
        assertThat( Files.isSameFile( symLink(), targetFile().getParent() ), is( true ) );

    }

    @Test
    @Category( { SymLink.class, FileStores.class } )
    public void testGetFileStoreOfSymLink() throws IOException {
        Files.createSymbolicLink( symLink(), targetFile() );
        assertThat( Files.getFileStore( symLink() ), is( Files.getFileStore( targetFile() ) ) );
    }

    @Test( expected = NoSuchFileException.class )
    @Category( { SymLink.class } )
    public void testGetFileStoreOfBrokenSymLink() throws IOException {
        Files.createSymbolicLink( symLink(), absTA() );
        Files.getFileStore( symLink() );
    }

    @Test( expected = FileAlreadyExistsException.class )
    @Category( { SymLink.class } )
    public void testCreateDirAtSymLinkThrows() throws IOException {
        Files.createSymbolicLink( symLink(), absTA() );
        Files.createDirectory( symLink() );
    }

    @Test( expected = FileAlreadyExistsException.class )
    @Category( { SymLink.class, HardLink.class } )
    public void testCreateHardLinkAtSymLinkThrows() throws IOException {
        Files.createSymbolicLink( symLink(), absTA() );
        Files.createLink( symLink(), targetFile() );
    }

//    @Test
//    @Category({ SymLink.class})  public void testUnnormalizedDeepSymLinkFollowsLinkBeforeNormalizationButNotWithNoFollowLink() throws IOException {
//        Path sym = dirTA().resolve("link");
//        Files.createSymbolicLink( sym, dirTBB());
//
//        Files.write( dirTA().resolve( nameC()), CONTENT);
//        Files.write(dirTB().resolve(nameC()), CONTENT_OTHER);
//
////        assertThat( Files.isSameFile( sym.resolve("..").resolve(nameC()), dirTB().resolve( nameC())), is(true));
//
//
//        try ( SeekableByteChannel ch = FS.provider().newByteChannel(sym.resolve("..").resolve(nameC()), dirTB().resolve(nameC()), Sets.asSet(NOFOLLOW_LINKS))
//        assertThat(Files.readAllBytes(sym.resolve("..").resolve(nameC()), dirTB().resolve( nameC()), ))
//    }

    // todo watch,

    // ----------------------------------------------------------------------------------------------------------------

    private Path targetFile() {
        Path target = absT().resolve( "targetSpace" ).resolve( "target" );
        if( Files.exists( target ) ) {
            return target;
        }

        Filess.createDirectories( target.getParent() );
        Filess.write( target, CONTENT );
        return target;
    }

    protected Path otherProviderTargetFile() {
        Path target = otherProviderAbsA().resolve( nameB() );

        if( !Files.exists( target ) ) {
            Filess.createDirectories( target.getParent() );
            Filess.write( target, CONTENT );
        }

        return target;
    }

    protected Path otherProviderTargetDir() {
        Path target = otherProviderAbsA().resolve( "target" );

        if( !Files.exists( target ) ) {
            Filess.createDirectories( target );
            Filess.write( otherProviderTargetDirKid(), CONTENT ); // one kid
        }

        return target;
    }

    protected Path otherProviderTargetDirKid() {
        return otherProviderAbsA().resolve( "target" ).resolve( "kid" );
    }

    private Path targetDir() {
        Path target = targetDirKid().getParent();
        if( !Files.exists( target ) ) {
            Filess.createDirectories( target );
            Filess.write( targetDirKid(), CONTENT ); // one kid
        }

        return target;
    }

    private Path targetDirKid() {
        return absT().resolve( "targetSpace" ).resolve( "targetDir" ).resolve( "targetKid" );
    }

    protected Path symLinkSpace() {
        Path ret = absT().resolve( "forsyml" );
        Filess.createDirectories( ret );
        return ret;
    }

    protected Path symLink() {
        return symLinkSpace().resolve( "link" );
    }

    protected Path symLink2() {
        return symLinkSpace().resolve( "link2" );
    }

    protected Path hardLink() {
        return symLinkSpace().resolve( "hardLink" );
    }

    private Path linkKid() {
        return symLink().resolve( "linkKid" );
    }

}
