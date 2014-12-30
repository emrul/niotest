package de.pfabulist.lindwurm.niotest.tests;

import org.junit.Test;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;

import static de.pfabulist.kleinod.errors.Unchecked.runtime;
import static de.pfabulist.lindwurm.niotest.matcher.PathExists.exists;
import static de.pfabulist.lindwurm.niotest.matcher.PathIsDirectory.isDirectory;
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
public abstract class PathTest20SymLinksIT extends PathTest19HardLinksIT {



    @Test
    public void testCreateSymLink() throws IOException {
        Files.createSymbolicLink( link(), targetFile() );
        assertThat( link(), exists());
    }

    @Test( expected = FileAlreadyExistsException.class )
    public void testCreateSymLinkWhereLinkExistsThrows() throws IOException {
        Files.write(link(), CONTENT );
        Files.createSymbolicLink(link(), targetFile() );
    }

    @Test
    public void testSymLinkAttributeNonLink() throws IOException {
        assertThat( Files.isSymbolicLink( targetFile() ), is( false ));
    }

    @Test
    public void testSymLinkAttributeLink() throws IOException {
        Files.createSymbolicLink( link(), targetDir() );
        assertThat( Files.isSymbolicLink( link() ), is(true));
    }

    @Test
    public void testReadFromSymLink() throws IOException {
        Files.createSymbolicLink( link(), targetFile());
        assertThat( Files.readAllBytes( link()), is(CONTENT));
    }

    @Test
    public void testReadFromSymLinkToSymLink() throws IOException {
        Files.createSymbolicLink( link(), targetFile() );
        Files.createSymbolicLink( link2(), link() );

        assertThat( Files.readAllBytes( link2() ), is(CONTENT));
    }

    @Test
    public void testWriteToSymLink() throws IOException {
        Files.createSymbolicLink( link(), targetFile() );
        Files.write( link(), CONTENT_OTHER );

        assertThat( Files.readAllBytes( targetFile() ), is(CONTENT_OTHER ));
    }

    @Test
    public void testDirSymLink() throws IOException {
        Files.createSymbolicLink( link(), targetDir() );

        assertThat( Files.isDirectory(link()), is(true));
    }

    @Test
    public void testDirStreamFromSymLink() throws IOException {
        Files.createSymbolicLink( link(), targetDir());

        try( DirectoryStream<Path> stream = Files.newDirectoryStream( link() )) {
            for ( Path kid : stream ) {
                System.out.println(kid);
                assertThat( kid.getParent(), is(link()));
                assertThat( Files.isSameFile( kid, targetDirKid()), is(true ));
            }
        }
    }

    @Test
    public void testCreateFileInSymLinkDir() throws IOException {
        Files.createSymbolicLink( link(), targetDir() );
        Files.write( linkKid(), CONTENT );
        assertThat( linkKid(), exists());
    }

    @Test
    public void testFileInSymLinkDirIsNotSymLink() throws IOException {
        Files.createSymbolicLink( link(), targetDir() );
        Files.write( linkKid(), CONTENT );

        assertThat( Files.isSymbolicLink( linkKid() ), is( false ));
    }


    @Test
    public void testCreateDirInSymLinkDir() throws IOException {
        Files.createSymbolicLink( link(), targetDir() );

        Files.createDirectory( linkKid() );

        assertThat( linkKid(), isDirectory());
    }

    @Test
    public void testDeleteSymLinkDoesNotDeleteTarget() throws IOException {
        Files.createSymbolicLink( link(), targetFile() );
        Files.delete( link() );
        assertThat( targetFile(), exists());
    }

    @Test
    public void testCopyFromSymLink() throws IOException {
        Files.createSymbolicLink( link(), targetFile() );
        Files.copy( link(), copyTarget() );
        assertThat( Files.readAllBytes( copyTarget()), is(CONTENT));
    }

    @Test
    public void testCopyFromDeeperSymLink() throws IOException {
        Files.createSymbolicLink( link(), targetDir() );
        Files.write(linkKid(), CONTENT);

        Files.copy( linkKid(), copyTarget() );

        assertThat( Files.readAllBytes(copyTarget()), is(CONTENT));
    }

    @Test
    public void testCopyToSymLink() throws IOException {
        Files.createSymbolicLink( link(), targetFile() );
        Files.copy( someOtherFile(), link(), StandardCopyOption.REPLACE_EXISTING );

        assertThat( Files.isSymbolicLink( link() ), is(false));
    }

    @Test
    public void testCopyToDeepSymLink() throws IOException {
        Files.createSymbolicLink(link(), targetDir());
        Files.write( linkKid(), CONTENT);

        Files.copy( someOtherFile(), linkKid(), StandardCopyOption.REPLACE_EXISTING );

        assertThat( Files.readAllBytes( linkKid()), is(CONTENT_OTHER));
        assertThat( Files.readAllBytes( linkKid().toRealPath()), is(CONTENT_OTHER));
    }

    @Test
    public void testHardLinkToSymLink() throws IOException {
        Files.createSymbolicLink( link(), targetFile());
        Files.createLink( link2(),link() );
        assertThat( Files.readAllBytes( link2()), is(CONTENT));
    }

    @Test
    public void testHardLinkToSymLinkIsSymLink() throws IOException {
        Files.createSymbolicLink( link(), targetFile());
        Files.createLink( link2(),link() );
        assertThat( Files.isSymbolicLink( link2()), is( true ));
    }

    @Test
    public void testHardLinkToSymLinkDeleteSym() throws IOException {
        Files.createSymbolicLink( link(), targetFile());
        Files.createLink(link2(), link());
        assertThat(Files.isSymbolicLink(link2()), is(true));

        Files.delete( link() );

        Files.write( link2(), CONTENT_OTHER );
        assertThat( Files.readAllBytes( targetFile() ), is( CONTENT_OTHER ));
        assertThat(Files.isSymbolicLink( link2()), is(true));
    }

    @Test
    public void testModifyHardLinkToSymLink() throws IOException {
        Files.createSymbolicLink( link(), targetFile());
        Files.createLink(link2(), link());
        assertThat(Files.isSymbolicLink(link2()), is(true));

        Files.write(link2(), CONTENT_OTHER);
        assertThat(Files.readAllBytes(targetFile()), is(CONTENT_OTHER));
        assertThat(Files.isSymbolicLink(link2()), is(true));
    }

    @Test( expected = FileSystemException.class )
    public void testSymLinkToParent() throws IOException {
        Files.createSymbolicLink( link(), link().getParent() );
    }

    //  think a -> b, b/d -> a
    @Test( expected = FileSystemException.class )
    public void testSymLinkComplexLoop() throws IOException {
        Files.createSymbolicLink( link(), targetDir());
        Files.createSymbolicLink( targetDir().resolve(nameStr[5]), link());
    }


    @Test( expected = NoSuchFileException.class ) // TODO correct error ?
    public void testBrokenSymLink() throws IOException {
        Files.createSymbolicLink( link(), targetFile() );
        Files.delete( targetFile() );
        Files.readAllBytes( link() );
    }

    @Test
    public void testBrokenSymLinkExists() throws IOException {
        Files.createSymbolicLink( link(), targetFile() );
        Files.delete( targetFile() );
        Files.exists( link() );
    }

    @Test
    public void testGetSymLinkTarget() throws IOException {
        Files.createSymbolicLink( link(), targetFile() );
        assertThat( Files.readSymbolicLink( link()), is(targetFile()));
    }

    @Test( expected = NotLinkException.class)
    public void testGetSymLinkOfNonLinkThrows() throws IOException {
        Files.readSymbolicLink( targetFile() );
    }

    @Test
    public void testReadAttributesFromSymLinkNoFollowLinks() throws IOException, InterruptedException {
        targetFile();
        Thread.sleep(2000);
        Files.createSymbolicLink( link(), targetFile() );

        FileTime orig     = Files.readAttributes( link(), BasicFileAttributes.class).creationTime();
        FileTime linkTime = Files.readAttributes( link(), BasicFileAttributes.class, LinkOption.NOFOLLOW_LINKS).creationTime();

        assertThat( linkTime, greaterThan( orig ));
    }

    @Test
    public void testAttributesFromSymLinkWithFollowLinkShowNoLink() throws IOException {
        Files.createSymbolicLink( link(), targetFile());

        BasicFileAttributes bfa = Files.readAttributes( link(), BasicFileAttributes.class );
        assertThat( bfa.isSymbolicLink(), is(false));
    }

    @Test
    public void testAttributesFromSymLinkWithFollowLinkShowNoLink_View() throws IOException {
        Files.createSymbolicLink( link(), targetFile());

        BasicFileAttributeView bfav = Files.getFileAttributeView(link(), BasicFileAttributeView.class);
        assertThat( bfav.readAttributes().isSymbolicLink(), is(false));
    }

    @Test
    public void testMoveSymLinkFileTestContent() throws IOException {
        Path target  = getPathPABf();
        Path sym     = getPathPB();
        Path newPath = getPathPC().resolve( nameStr[1]);
        Files.createDirectory( newPath.getParent() );
        Files.createSymbolicLink( sym, target );

        Files.move( sym, newPath );

        assertThat( Files.readAllBytes( newPath), is(CONTENT));
    }

    @Test
    public void testMoveSymLinkFileOrigIsGone() throws IOException {
        Path target  = getPathPABf();
        Path sym     = getPathPB();
        Path newPath = getPathPC().resolve( nameStr[1]);
        Files.createDirectory( newPath.getParent() );
        Files.createSymbolicLink( sym, target );

        Files.move( sym, newPath );

        assertThat( sym, not(exists()));
    }

    @Test
    public void testMoveSymLinkFileKeepsSymLinkStatus() throws IOException {
        Files.createSymbolicLink( link(), targetFile() );
        Files.move( link(), copyTarget() );

        assertThat( Files.isSymbolicLink( copyTarget() ), is(true ));
    }

    @Test
    public void testFileStoreOfSymLinkIsTargets() throws IOException {

        Path target  = getPathPABf();
        Path sym     = getPathPB();
        Files.createSymbolicLink( sym, target );

        assertThat( Files.getFileStore( sym ), is(Files.getFileStore(target)));
    }

    @Test
    public void testSymLinkAndTargetAreSameFile() throws IOException {

        Path target  = getPathPABf();
        Path sym     = getPathPB();
        Files.createSymbolicLink( sym, target );

        assertThat( Files.isSameFile(sym, target), is( true ));
    }

    @Test
    public void testDeeperSymLinkIsSameFile() throws IOException {

        Path target  = getPathPABf();
        Path sym     = getPathPB();
        Files.createSymbolicLink( sym, target );

        assertThat( Files.isSameFile( sym, target ), is( true ));
    }


    @Test
    public void testForeignSymLinkExists() throws IOException {
        Files.createSymbolicLink( link(), foreignTargetFile() );
        assertThat( link(), exists());
    }

    @Test
    public void testDoubleSymLinkExists() throws IOException {
        Files.createSymbolicLink( link(), targetDir() );
        Files.createSymbolicLink( linkKid(), someOtherFile());

        assertThat( linkKid(), exists() );
    }


    @Test
    public void testDoubleSymLinkSameFile() throws IOException {
        Files.createSymbolicLink( link(), targetDir() );
        Files.createSymbolicLink( linkKid(), someOtherFile());

        assertThat(Files.isSameFile(linkKid(), someOtherFile()), is(true));
    }

    @Test
    public void testDoubleSymLinkReadAttributes() throws IOException, InterruptedException {
        someOtherFile();
        Thread.sleep(1100);
        Files.createSymbolicLink( link(), targetDir() );
        Files.createSymbolicLink( linkKid(), someOtherFile());

        assertThat( Files.readAttributes( linkKid(), "basic:lastModifiedTime").get("lastModifiedTime"),
                    is(Files.readAttributes(someOtherFile(), "basic:lastModifiedTime").get("lastModifiedTime")));

    }

    @Test
    public void testDeepSymLinkMove() throws IOException {
        Files.createSymbolicLink( link(), targetDir() );
        Files.write( linkKid(), CONTENT );
        Files.move( linkKid(), copyTarget() );

        assertThat( Files.readAllBytes( copyTarget()), is(CONTENT));
    }

    @Test
    public void testMoveToDeepSymLink() throws IOException {
        Files.createSymbolicLink( link(), targetDir() );
        Files.move( someOtherFile(), linkKid() );

        assertThat( Files.readAllBytes( linkKid()), is(CONTENT_OTHER));
    }

    @Test
    public void testDoubleSymLinkReadAttributesNoFollowLink() throws IOException, InterruptedException {
        someOtherFile();
        Thread.sleep(1100);
        Files.createSymbolicLink( link(), targetDir() );
        Files.createSymbolicLink( linkKid(), someOtherFile());

        assertThat( (FileTime)Files.readAttributes( linkKid(), "basic:lastModifiedTime", LinkOption.NOFOLLOW_LINKS).get("lastModifiedTime"),
                    greaterThan((FileTime) Files.readAttributes(someOtherFile(), "basic:lastModifiedTime").get("lastModifiedTime")));

    }

    @Test
    public void testReadFromForeignSymLink() throws IOException {
        Files.createSymbolicLink( link(), foreignTargetFile() );
        assertThat( Files.readAllBytes( link()), is(CONTENT));
    }

    @Test
    public void testGetForeignSymLinkTarget() throws IOException {
        Files.createSymbolicLink( link(), foreignTargetFile() );
        assertThat( Files.readSymbolicLink( link()), is(foreignTargetFile()));
    }

    @Test
    public void testGetDirStreamFromForeignSymLink() throws IOException {
        Files.createSymbolicLink(link(), foreignTargetDir());

        try ( DirectoryStream<Path> stream = Files.newDirectoryStream( link())) {
            for ( Path kid : stream ) {
                assertThat( Files.readAllBytes( kid), is(CONTENT));
            }
        }
    }

    @Test
    public void testIsSameFileForeignSymLink() throws IOException {
        Files.createSymbolicLink( link(), foreignTargetFile() );
        Files.createSymbolicLink( link2(), foreignTargetFile() );
        assertThat( Files.isSameFile( link(), link2()), is( true ));
    }

    @Test
    public void testSymLinkChanges() throws IOException {
        Files.createSymbolicLink( link(), targetFile() );
        Files.delete( targetFile());
        targetDir();

        assertThat( link(), isDirectory() );
    }

    @Test
    public void testMoveToDeepForeignSymLink() throws IOException {
        Files.createSymbolicLink(link(), foreignTargetDir());
        Files.move(someOtherFile(), linkKid());

        assertThat( Files.readAllBytes(linkKid()), is(CONTENT_OTHER));
    }

    // todo watch,

    // ----------------------------------------------------------------------------------------------------------------

    private Path targetFile() {
        try {
            Path target = getPathPAB();
            if ( Files.exists( target )) {
                return target;
            }

            return getPathPABf();
        } catch (IOException e) {
            throw runtime(e);
        }
    }

    private Path foreignTargetFile() {
        try {
            Path target = getForeignPathP().resolve(nameStr[0]).resolve( nameStr[0]);

            if ( !Files.exists( target )) {
                Files.createDirectories( target.getParent());
                Files.write( target, CONTENT );
            }

            return target;
        } catch (IOException e) {
            throw runtime(e);
        }
    }

    private Path foreignTargetDir() {
        try {
            Path target = getForeignPathP().resolve(nameStr[0]).resolve( nameStr[0]);

            if ( !Files.exists( target )) {
                Files.createDirectories( target );
                Files.write( foreignTargetDirKid(), CONTENT); // one kid
            }

            return target;
        } catch (IOException e) {
            throw runtime(e);
        }
    }

    private Path targetDir() {
        try {
            Path target = getPathPAB();
            if ( !Files.exists( target )) {
                Files.createDirectories( target );
                Files.write( targetDirKid(), CONTENT ); // one kid
            }

            return target;
        } catch (IOException e) {
            throw runtime(e);
        }
    }

    private Path someOtherFile() {
        try {
            Path ret = getPathPA().resolve( nameStr[4]);
            if ( !Files.exists( ret )) {
                Files.createDirectories(ret.getParent());
                Files.write( ret, CONTENT_OTHER );
            }

            return ret;

        } catch (IOException e) {
            throw runtime(e);
        }
    }

    private Path targetDirKid( ) {
        try {
            return getPathPAB().resolve( nameStr[0]);
        } catch (IOException e) {
            throw runtime(e);
        }
    }

    private Path foreignTargetDirKid( ) {
        return getForeignPathP().resolve(nameStr[0]).resolve( nameStr[0]).resolve( nameStr[1]);
    }

    private Path link() {
        try {
            Files.createDirectories( getPathPB());
            return getPathPB().resolve( getName( "link"));
        } catch (IOException e) {
            throw runtime(e);
        }
    }

    private Path link2() {
        try {
            return getPathPB().resolve( nameStr[3]);
        } catch (IOException e) {
            throw runtime(e);
        }
    }

    private Path linkKid() {
        return link().resolve(getName("linkKid"));
    }

    private Path copyTarget() {
        try {
            Path target = getPathPC().resolve(nameStr[4]);
            if ( !Files.exists( target.getParent() )) {
                Files.createDirectories( target.getParent() );
            }

            return target;
        } catch (IOException e) {
            throw runtime(e);
        }
    }
}
