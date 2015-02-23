package de.pfabulist.lindwurm.niotest.testsn;

import de.pfabulist.kleinod.collection.Sets;
import de.pfabulist.kleinod.paths.Filess;
import de.pfabulist.lindwurm.niotest.testsn.setup.AllCapabilitiesBuilder;
import de.pfabulist.lindwurm.niotest.testsn.setup.Capa;
import de.pfabulist.lindwurm.niotest.testsn.setup.DetailBuilder;
import org.junit.Test;

import java.io.IOException;
import java.nio.channels.ByteChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.FileTime;
import java.util.Set;

import static de.pfabulist.kleinod.errors.Unchecked.runtime;
import static de.pfabulist.lindwurm.niotest.matcher.PathExists.exists;
import static de.pfabulist.lindwurm.niotest.matcher.PathIsDirectory.isDirectory;
import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static java.nio.file.StandardOpenOption.READ;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;

/**
* ** BEGIN LICENSE BLOCK *****
* BSD License (2 clause)
* Copyright (c) 2006 - 2014, Stephan Pfab
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


    public Tests20SymLinks( Capa capa) {
        super(capa);
//        attributes.put( "SymLink", capabilities::hasSymLinks );
//        attributes.put( "SymLinkToOtherProvider", capabilities::hasSymLinkToOtherProviders );
//        attributes.put( "DirSymLink", capabilities::hasDirSymLinks );
//        attributes.put( "DeepSymLink", capabilities::hasDirSymLinks );
//        attributes.put( "DoubleSymLink", capabilities::hasDirSymLinks );
    }
    
    public static class CapaBuilder20 extends CapaBuilder19 {

        public SymlinkBuilder symlinks() {
            return new SymlinkBuilder((AllCapabilitiesBuilder) this);
        }
    }
    
    
    public static class SymlinkBuilder extends DetailBuilder {
        public SymlinkBuilder(AllCapabilitiesBuilder builder) {
            super(builder);
        }

        public SymlinkBuilder toOtherProviders( boolean cond ) {
            capa.addFeature( "SymLinkToOtherProvider", cond );
            return this;
        }


        public SymlinkBuilder toDirs( boolean cond ) {
            capa.addFeature( "DirSymLink", cond );
            capa.addFeature( "DeepSymLink", cond );
            capa.addFeature( "DoubleSymLink", cond );
            return this;
        }
        

        public SymlinkBuilder relativeTargets( boolean cond ) {
            capa.addFeature( "RelSymLink", cond );
            return this;
            
        }         

        @Override
        public AllCapabilitiesBuilder onOff(boolean val) {
            capa.addFeature( "SymLink", val );
            return builder;
        }
    } 

    @Test
    public void testCreateSymLink() throws IOException {
        Files.createSymbolicLink( symLink(), targetFile() );
        assertThat( symLink(), exists());
    }

    @Test( expected = FileAlreadyExistsException.class )
    public void testCreateSymLinkWhereLinkExistsThrows() throws IOException {
        Files.write(symLink(), CONTENT );
        Files.createSymbolicLink(symLink(), targetFile() );
    }

    @Test
    public void testCreateSymLinkToNonExisting() throws IOException {
        Files.createSymbolicLink( link(), absTA());
    }

    @Test
    public void testSymLinkAttributeNonLink() throws IOException {
        assertThat( Files.isSymbolicLink( targetFile() ), is( false ));
    }

    @Test
    public void testSymLinkAttributeLink() throws IOException {
        Files.createSymbolicLink( symLink(), targetDir() );
        assertThat( Files.isSymbolicLink( symLink() ), is(true));
    }

    // todo
    @Test
    public void testRealPathofAnUnnormalizedDeepSymLink() throws IOException {

        Files.createSymbolicLink( link(), targetDir());
        Files.write(link().getParent().resolve(nameB()), CONTENT);
        Files.write( targetDir().getParent().resolve(nameB()), CONTENT_OTHER );

        Path unnorm = link().resolve( "..").resolve( nameB());
        System.out.println(link());
        System.out.println(targetDir());
        System.out.println( unnorm.toRealPath());
        System.out.println( unnorm.toRealPath(NOFOLLOW_LINKS));
        System.out.println( link().getParent().resolve(nameB()).toRealPath());
    }


    @Test
    public void testReadFromSymLink() throws IOException {
        Files.createSymbolicLink( symLink(), targetFile());
        assertThat( Files.readAllBytes( symLink()), is(CONTENT));
    }

    @Test
    public void testReadFromSymLinkToSymLink() throws IOException {
        Files.createSymbolicLink( symLink(), targetFile() );
        Files.createSymbolicLink( symLink2(), symLink() );

        assertThat( Files.readAllBytes( symLink2() ), is(CONTENT));
    }

    @Test
    public void testWriteToSymLink() throws IOException {
        Files.createSymbolicLink( symLink(), targetFile() );
        Files.write( symLink(), CONTENT_OTHER );

        assertThat( Files.readAllBytes( targetFile() ), is(CONTENT_OTHER ));
    }

    @Test
    public void testDirSymLink() throws IOException {
        Files.createSymbolicLink( symLink(), targetDir() );

        assertThat( Files.isDirectory(symLink()), is(true));
    }

    @Test
    public void testStreamFromDirSymLink() throws IOException {
        Files.createSymbolicLink( symLink(), targetDir());

        try( DirectoryStream<Path> stream = Files.newDirectoryStream( symLink() )) {
            for ( Path kid : stream ) {
                System.out.println(kid);
                assertThat( kid.getParent(), is(symLink()));
                assertThat( Files.isSameFile( kid, targetDirKid()), is(true ));
            }
        }
    }

    @Test
    public void testCreateFileInDirSymLink() throws IOException {
        Files.createSymbolicLink( symLink(), targetDir() );
        Files.write( linkKid(), CONTENT );
        assertThat( linkKid(), exists());
    }

    @Test
    public void testFileInDirSymLinkIsNotSymLink() throws IOException {
        Files.createSymbolicLink( symLink(), targetDir() );
        Files.write( linkKid(), CONTENT );

        assertThat( Files.isSymbolicLink( linkKid() ), is( false ));
    }


    @Test
    public void testCreateDirInDirSymLink() throws IOException {
        Files.createSymbolicLink( symLink(), targetDir() );
        Files.createDirectory( linkKid() );
        assertThat( linkKid(), isDirectory());
    }

    @Test
    public void testDeleteSymLinkDoesNotDeleteTarget() throws IOException {
        Files.createSymbolicLink( symLink(), targetFile() );
        Files.delete(symLink());
        assertThat( targetFile(), exists());
        assertThat( symLink(), not(exists()));
    }

    @Test
    public void testDeleteSymLinkDoesNotDeleteTargetNonEmptyDirCase() throws IOException {
        Files.createSymbolicLink( symLink(), targetDir() );
        Files.write( symLink().resolve("kid"), CONTENT );
        Files.delete(symLink());
        assertThat( symLink(), not(exists()));
    }

    @Test
    public void testCopyFromSymLink() throws IOException {
        Files.createSymbolicLink( symLink(), targetFile() );
        Files.copy(symLink(), tgt());
        assertThat( Files.readAllBytes(tgt()), is(CONTENT));
    }

    @Test
    public void testCopyFromDeeperSymLink() throws IOException {
        Files.createSymbolicLink( symLink(), targetDir() );
        Files.write(linkKid(), CONTENT);

        Files.copy( linkKid(), tgt() );

        assertThat( Files.readAllBytes(tgt()), is(CONTENT));
    }

    @Test( expected = FileAlreadyExistsException.class )
    public void testCopyToBrokenSymLink() throws IOException {
        Files.createSymbolicLink( symLink(), absTA() );
        Files.copy(fileTAB(), symLink());
    }

    @Test
    public void testCopyBrokenSymLinkToItself() throws IOException {
        Files.createSymbolicLink(symLink(), absTA());
        Files.copy(symLink(), symLink()); // no throw
    }

    @Test
    public void testCopySymLinkToItself() throws IOException {
        Files.createSymbolicLink( symLink(), targetFile() );
        Files.copy(symLink(), symLink());
        assertThat(Files.readAllBytes(symLink()), is(CONTENT));
    }


    @Test
    public void testCopyToSymLink() throws IOException {
        // specified in Files.copy
        Files.createSymbolicLink( symLink(), targetFile() );
        Files.copy( fileTAB(), symLink(), REPLACE_EXISTING );
        assertThat( Files.isSymbolicLink( symLink() ), is(false));
    }

    @Test
    public void testCopyToDeepSymLink() throws IOException {
        Files.createSymbolicLink( symLink(), targetDir());
        Files.copy( fileTAB(), linkKid());

        assertThat( Files.readAllBytes( linkKid()), is(CONTENT));
        assertThat( Files.readAllBytes( linkKid().toRealPath()), is(CONTENT));
    }

    @Test
    public void testHardLinkToSymLink() throws IOException {
        Files.createSymbolicLink( symLink(), targetFile());
        Files.createLink( hardLink(), symLink() );
        assertThat( Files.readAllBytes( hardLink()), is(CONTENT));
    }

    // todo: it is not clear what the result should be
    // @Test
    // public void testHardLinkToSymLinkIsNotASymLink() throws IOException {
    //     Files.createSymbolicLink( symLink(), targetFile());
    //     Files.createLink( hardLink(), symLink() );
    //     assertThat( Files.isSymbolicLink( hardLink()), is( false ));
    // }

    @Test
    public void testHardLinkToSymLinkDeleteSymLeavesHardLinkUntouched() throws IOException {
        Files.createSymbolicLink(symLink(), targetFile());
        Files.createLink(hardLink(), symLink());

        Files.delete( symLink() );

        assertThat( Files.readAllBytes(hardLink()), is( CONTENT ));
    }

//    @Test( expected = FileSystemException.class )
//    public void testSymLinkToParent() throws IOException {
//        Files.createSymbolicLink( symLink(), symLink().getParent() );
//    }
//
//    //  think a -> b, b/d -> a
//    @Test( expected = FileSystemException.class )
//    public void testSymLinkComplexLoop() throws IOException {
//        Files.createSymbolicLink( symLink(), targetDir());
//        Files.createSymbolicLink( targetDir().resolve(nameD()), symLink());
//    }


    @Test( expected = NoSuchFileException.class ) // TODO correct error ?
    public void testBrokenSymLink() throws IOException {
        Files.createSymbolicLink( symLink(), targetFile() );
        Files.delete( targetFile() );
        Files.readAllBytes( symLink() );
    }

    @Test
    public void testBrokenSymLinkNotExists() throws IOException {
        Files.createSymbolicLink( symLink(), absTA() );
        assertThat( symLink(), not(exists()));
    }

    @Test
    public void testBrokenSymLinkNoFollowLinkExists() throws IOException {
        Files.createSymbolicLink( symLink(), absTA());
        assertThat( Files.exists( symLink(), LinkOption.NOFOLLOW_LINKS ), is(true));
    }

    @Test
    public void testBrokenSymLinkThrowsOnIsSymlink() throws IOException {
        Files.createSymbolicLink( symLink(), absTA());
        assertThat( Files.isSymbolicLink( symLink() ), is( true ) );
    }

    @Test
    public void testBrokenSymLinkGetAttisNoFollowLinkDoesWorks() throws IOException {
        Files.createSymbolicLink( symLink(), absTA());
        BasicFileAttributes aa = Files.readAttributes( symLink(), BasicFileAttributes.class, LinkOption.NOFOLLOW_LINKS );
        assertThat( aa.isSymbolicLink(), is(true));
    }

    @Test( expected = NoSuchFileException.class )
    public void testBrokenSymLinkDoesNotTestForSymlinkAttribute() throws IOException {
        Files.createSymbolicLink(symLink(), absTA());
        Files.readAttributes( symLink(), BasicFileAttributes.class);
    }

//    @Test
//    public void testBrokenSymLinkDoesNotTeastForSymlinkAttribute() throws IOException {
//        Files.createSymbolicLink(symLink(), absTA());
//        System.out.println( Files.readSymbolicLink( symLink()));
//    }


    @Test
    public void testDeleteBrokenSymLink() throws IOException {
        Files.createSymbolicLink(symLink(), absTA());
        Files.delete( symLink());
    }

    @Test
    public void testGetSymLinkTarget() throws IOException {
        Files.createSymbolicLink( symLink(), targetFile() );
        assertThat( Files.readSymbolicLink( symLink()), is(targetFile()));
    }

    @Test( expected = NotLinkException.class)
    public void testGetSymLinkOfNonLinkThrows() throws IOException {
        Files.readSymbolicLink( targetFile() );
    }

    @Test
    public void testReadAttributesFromSymLinkNoFollowLinks() throws IOException, InterruptedException {
        targetFile();
        Thread.sleep(2000);
        Files.createSymbolicLink( symLink(), targetFile() );

        FileTime orig     = Files.readAttributes( symLink(), BasicFileAttributes.class).creationTime();
        FileTime linkTime = Files.readAttributes( symLink(), BasicFileAttributes.class, NOFOLLOW_LINKS).creationTime();

        assertThat( linkTime, greaterThan( orig ));
    }

    @Test
    public void testAttributesFromSymLinkWithFollowLinkShowNoLink() throws IOException {
        Files.createSymbolicLink( symLink(), targetFile());

        BasicFileAttributes bfa = Files.readAttributes( symLink(), BasicFileAttributes.class );
        assertThat( bfa.isSymbolicLink(), is(false));
    }

    @Test
    public void testAttributesFromSymLinkWithFollowLinkShowNoLink_View() throws IOException {
        Files.createSymbolicLink( symLink(), targetFile());

        BasicFileAttributeView bfav = Files.getFileAttributeView(symLink(), BasicFileAttributeView.class);
        assertThat( bfav.readAttributes().isSymbolicLink(), is(false));
    }

    @Test
    public void testMoveSymLinkFileTestContent() throws IOException {
        Files.createSymbolicLink( symLink(), targetFile() );
        Files.move( symLink(), absTA() );

        assertThat( Files.readAllBytes( absTA()), is(CONTENT));
    }

    @Test
    public void testMoveSymLinkMovesLink() throws IOException {
        Files.createSymbolicLink( symLink(), targetFile() );
        Files.move(symLink(), absTC());
        assertThat(symLink(), not(exists()));
    }

    @Test
    public void testMoveSymLinkLeavesTarget() throws IOException {
        Files.createSymbolicLink( symLink(), targetFile() );
        Files.move(symLink(), absTC());
        assertThat( targetFile(), exists());
    }

    @Test
    public void testMoveSymLinkFileKeepsSymLinkStatus() throws IOException {
        Files.createSymbolicLink( symLink(), targetFile() );
        Files.move( symLink(), tgt() );

        assertThat( Files.isSymbolicLink( tgt() ), is(true ));
    }

    @Test
    public void testFileStoreOfSymLinkIsTargets() throws IOException {

        Files.createSymbolicLink( symLink(), targetFile() );

        assertThat( Files.getFileStore( symLink() ), is(Files.getFileStore(targetFile())));
    }

    @Test
    public void testSymLinkAndTargetAreSameFile() throws IOException {

        Files.createSymbolicLink( symLink(), targetFile() );

        assertThat( Files.isSameFile( symLink(), targetFile()), is( true ));
    }

    @Test
    public void testDeepSymLinkIsSameFile() throws IOException {

        Files.createSymbolicLink( symLink(), targetDir() );
        Files.write( linkKid(), CONTENT );

        assertThat(Files.isSameFile(linkKid(), targetDir().resolve(linkKid().getFileName())), is(true));
    }


    // no otherproviderlinks
//    @Test
//    public void testSymLinkToOtherProviderExists() throws IOException {
//        Files.createSymbolicLink( symLink(), otherProviderTargetFile() );
//        assertThat( symLink(), exists());
//    }

    @Test
    public void testDoubleSymLinkExists() throws IOException {
        Files.createSymbolicLink( symLink(), targetDir() );
        Files.createSymbolicLink( linkKid(), fileTAB());

        assertThat( linkKid(), exists() );
    }


    @Test
    public void testDoubleSymLinkSameFile() throws IOException {
        Files.createSymbolicLink( symLink(), targetDir() );
        Files.createSymbolicLink( linkKid(), fileTAB());

        assertThat(Files.isSameFile(linkKid(), fileTAB()), is(true));
    }

    @Test
    public void testDoubleSymLinkReadAttributes() throws IOException, InterruptedException {
        fileTAB();
        Thread.sleep(1100);
        Files.createSymbolicLink( symLink(), targetDir() );
        Files.createSymbolicLink( linkKid(), fileTAB());

        assertThat( Files.readAttributes( linkKid(), "basic:lastModifiedTime").get("lastModifiedTime"),
                    is(Files.readAttributes(fileTAB(), "basic:lastModifiedTime").get("lastModifiedTime")));

    }

    @Test
    public void testDeepSymLinkMove() throws IOException {
        Files.createSymbolicLink( symLink(), targetDir() );
        Files.write( linkKid(), CONTENT );
        Files.move( linkKid(), tgt() );

        assertThat( Files.readAllBytes( tgt()), is(CONTENT));
    }

    @Test
    public void testMoveToDeepSymLink() throws IOException {
        Files.createSymbolicLink( symLink(), targetDir() );
        Files.move( fileTAB(), linkKid() );

        assertThat( Files.readAllBytes( linkKid()), is(CONTENT));
    }

    @Test
    public void testDoubleSymLinkReadAttributesNoFollowLink() throws IOException, InterruptedException {
        fileTAB();
        Thread.sleep(1100);
        Files.createSymbolicLink( symLink(), targetDir() );
        Files.createSymbolicLink( linkKid(), fileTAB());

        assertThat( (FileTime)Files.readAttributes( linkKid(), "basic:lastModifiedTime", NOFOLLOW_LINKS).get("lastModifiedTime"),
                    greaterThan((FileTime) Files.readAttributes(fileTAB(), "basic:lastModifiedTime").get("lastModifiedTime")));

    }

    // no otherproviderlinks
//    @Test
//    public void testReadFromSymLinkToOtherProvider() throws IOException {
//        Files.createSymbolicLink( symLink(), otherProviderTargetFile() );
//        assertThat( Files.readAllBytes( symLink()), is(CONTENT));
//    }

    // no otherproviderlinks
//    @Test
//    public void testGetSymLinkToOtherProviderTarget() throws IOException {
//        Files.createSymbolicLink( symLink(), otherProviderTargetFile() );
//        assertThat( Files.readSymbolicLink( symLink()), is(otherProviderTargetFile()));
//    }

    // no otherproviderlinks
//    @Test
//    public void testGetDirStreamFromSymLinkToOtherProvider() throws IOException {
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
//    public void testIsSameFileSymLinkToOtherProvider() throws IOException {
//        Files.createSymbolicLink( symLink(), otherProviderTargetFile() );
//        Files.createSymbolicLink( symLink2(), otherProviderTargetFile() );
//        assertThat( Files.isSameFile( symLink(), symLink2()), is( true ));
//    }

    @Test
    public void testSymLinkChanges() throws IOException {
        Path target = targetFile();
        Files.createSymbolicLink( symLink(), target );
        Files.delete( target );
        Files.createDirectory( target );

        assertThat( symLink(), isDirectory() );
    }

    // no otherproviderlinks
//    @Test
//    public void testMoveToDeepSymLinkToOtherProvider() throws IOException {
//        Files.createSymbolicLink(symLink(), otherProviderTargetDir());
//        Files.move(fileTAB(), linkKid());
//
//        assertThat( Files.readAllBytes(linkKid()), is(CONTENT_OTHER));
//    }

//    // todo more tests with rel links
//    @Test
//    public void testRelSymLink() throws IOException {
//        Files.createSymbolicLink( symLink(), relA() );
//        Files.move( symLink(), absTB());
//
//        System.out.println( Files.readSymbolicLink(symLink()));
//
//    }


    @Test
    public void testRelSymLinkIsRelativeToLink() throws IOException {
        Files.createSymbolicLink(symLink(), FS.getPath("..").resolve(nameA()));
        Files.write(symLink().getParent().getParent().resolve(nameA()), CONTENT_OTHER);
        assertThat(Files.readAllBytes(symLink()), is(CONTENT_OTHER));
        //assertThat( Files.isSameFile( symLink(), symLink().getParent().resolve( nameA())), is(true));
    }

    @Test
    public void testMoveARelSymLink2() throws IOException {
        Files.createSymbolicLink( symLink(), FS.getPath("..").resolve(nameA()));
        Path to = dirTB().resolve(nameB());
        Files.move( symLink(), to);
        Files.write( to, CONTENT );
        assertThat(Files.isSameFile( to, absTA()), is(true));
    }

//    //Todo
//    @Test
//    public void testMoveSymLinkLeavesNormalPath() throws IOException {
//        Files.createSymbolicLink( dirTB().resolve(nameB()), FS.getPath("..").resolve(nameA()));
//
//        Files.write( symLink(), CONTENT );
//    }


//    @Test
//    public void testCreateSymlinkWithLastModifiedTime() throws IOException {
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
    public void testMoveSymLinkDoesNotChangeLastModifiedTime() throws IOException {
        Files.createSymbolicLink( symLink(), fileTA() );
        FileTime before = Files.getLastModifiedTime( symLink(), LinkOption.NOFOLLOW_LINKS );
        waitForAttribute();
        Files.move( symLink(), absTB() );
        assertThat( Files.getLastModifiedTime( absTB(), LinkOption.NOFOLLOW_LINKS ), is(before));
    }

    @Test
    public void testMoveARelSymLink() throws IOException {
        Files.createSymbolicLink( symLink(), relA());
        Files.move( symLink(), absTB());
        Files.write( absTB(), CONTENT );

        assertThat( Files.isSameFile( absTB(), absTA()), is(true));
    }

    @Test
    public void testMoveReplaceExistingToSymLink() throws IOException {
        Files.createSymbolicLink(symLink(), targetFile());
        Files.move( fileTA(), symLink(), StandardCopyOption.REPLACE_EXISTING );
        assertThat( Files.isSymbolicLink( symLink()), is(false));
    }

    @Test
    public void testUnnormalizedDeepSymLinkFollowsLinkBeforeNormalization() throws IOException {
        Path sym = dirTA().resolve("link");
        Files.createSymbolicLink( sym, dirTBB());

        Files.write( dirTA().resolve( nameC()), CONTENT);
        Files.write( dirTB().resolve( nameC()), CONTENT_OTHER);

        assertThat( Files.isSameFile( sym.resolve("..").resolve(nameC()), dirTB().resolve( nameC())), is(true));
    }

    @Test
    public void testSymLinkToUnnormalizedRelPath() throws IOException {
        Files.createSymbolicLink( symLink(), FS.getPath( nameA() ).resolve( ".." ) );
        Files.createSymbolicLink( symLink().getParent().resolve( nameA() ), targetFile() );

        // System.out.println( symLink().toRealPath());
        // System.out.println( targetFile().getParent().toRealPath());
        assertThat( Files.isSameFile( symLink(), targetFile().getParent() ), is( true ) );

    }

    @Test
    public void testGetFileStoreOfSymLink() throws IOException {
        Files.createSymbolicLink( symLink(), targetFile() );
        assertThat( Files.getFileStore( symLink() ), is( Files.getFileStore( targetFile() ) ) );
    }

    @Test( expected = NoSuchFileException.class )
    public void testGetFileStoreOfBrokenSymLink() throws IOException {
        Files.createSymbolicLink( symLink(), absTA() );
        Files.getFileStore( symLink() );
    }

    @Test( expected = FileAlreadyExistsException.class )
    public void testCreateDirAtSymLinkThrows() throws IOException {
        Files.createSymbolicLink( symLink(), absTA());
        Files.createDirectory( symLink() );
    }

    @Test( expected = FileAlreadyExistsException.class )
    public void testCreateHardLinkAtSymLinkThrows() throws IOException {
        Files.createSymbolicLink( symLink(), absTA());
        Files.createLink( symLink(), targetFile() );
    }


//    @Test
//    public void testUnnormalizedDeepSymLinkFollowsLinkBeforeNormalizationButNotWithNoFollowLink() throws IOException {
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
        Path target = absT().resolve("targetSpace").resolve("target");
        if ( Files.exists( target )) {
            return target;
        }

        Filess.createDirectories( target.getParent());
        Filess.write( target, CONTENT );
        return target;
    }

    protected Path otherProviderTargetFile() {
        try {
            Path target = otherProviderAbsA().resolve( nameB());

            if ( !Files.exists( target )) {
                Files.createDirectories( target.getParent());
                Files.write( target, CONTENT );
            }

            return target;
        } catch (IOException e) {
            throw runtime(e);
        }
    }

    protected Path otherProviderTargetDir() {
        try {
            Path target = otherProviderAbsA().resolve( "target" );

            if ( !Files.exists( target )) {
                Files.createDirectories( target );
                Files.write( otherProviderTargetDirKid(), CONTENT); // one kid
            }

            return target;
        } catch (IOException e) {
            throw runtime(e);
        }
    }

    protected Path otherProviderTargetDirKid() {
        return otherProviderAbsA().resolve( "target" ).resolve("kid");
    }



    private Path targetDir() {
        try {
            Path target = targetDirKid().getParent();
            if ( !Files.exists( target )) {
                Files.createDirectories( target );
                Files.write( targetDirKid(), CONTENT ); // one kid
            }

            return target;
        } catch (IOException e) {
            throw runtime(e);
        }
    }

    private Path targetDirKid( ) {
        return absT().resolve("targetSpace").resolve( "targetDir" ).resolve( "targetKid" );
    }

    protected  Path symLinkSpace() {
        Path ret = absT().resolve("forsyml");
        Filess.createDirectories(ret);
        return ret;
    }
    protected Path symLink() {
        return symLinkSpace().resolve("link");
    }
    protected Path symLink2() {
        return symLinkSpace().resolve("link2");
    }

    protected Path hardLink() {
        return symLinkSpace().resolve("hardLink");
    }


    private Path linkKid() {
        return symLink().resolve( "linkKid" );
    }

}
