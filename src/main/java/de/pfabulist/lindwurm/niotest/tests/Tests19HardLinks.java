package de.pfabulist.lindwurm.niotest.tests;

import de.pfabulist.kleinod.nio.Filess;
import de.pfabulist.lindwurm.niotest.tests.topics.Delete;
import de.pfabulist.lindwurm.niotest.tests.topics.Move;
import de.pfabulist.lindwurm.niotest.tests.topics.Writable;
import de.pfabulist.lindwurm.niotest.tests.topics.HardLink;
import de.pfabulist.lindwurm.niotest.tests.topics.SlowTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.ProviderMismatchException;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
@SuppressWarnings( { "PMD.ExcessivePublicCount", "PMD.TooManyMethods" } )
public abstract class Tests19HardLinks extends Tests18FileChannels {

    public Tests19HardLinks( FSDescription capa ) {
        super( capa );
    }

    @Test
    @Category( { HardLink.class, Writable.class } )
    public void testHardLinkCreate() throws IOException {
        Files.createLink( link(), orig() );
        assertThat( link() ).exists();
    }

    @Test
    @Category( { HardLink.class, Writable.class } )
    public void testHardLinkChangeOneChangesTheOther() throws IOException {
        Files.createLink( link(), orig() );
        Files.write( link(), CONTENT_OTHER );
        assertThat( Files.readAllBytes( orig() ) ).isEqualTo( CONTENT_OTHER );
    }

    @Test
    @Category( { HardLink.class, Writable.class, Delete.class } )
    public void testHardLinkDeleteOrigDoesNotAffectTheOther() throws IOException {
        Files.createLink( link(), orig() );
        Files.delete( orig() );
        assertThat( Files.readAllBytes( link() ) ).isEqualTo( CONTENT );
    }

    @Test
    @Category( { HardLink.class, Writable.class, Delete.class } )
    public void testHardLinkDeleteLinkDoesNotAffectTheOther() throws IOException {
        Files.createLink( link(), orig() );
        Files.delete( link() );
        assertThat( Files.readAllBytes( orig() ) ).isEqualTo( CONTENT );
    }

    @Test
    @Category( { SlowTest.class, HardLink.class, Writable.class } )
    public void testHardLinkModifyOneModifiedDateOfOtherChanged() throws IOException, InterruptedException {
        Files.createLink( link(), orig() );
        FileTime before = Files.getLastModifiedTime( orig() );
        waitForAttribute();

        Files.write( link(), CONTENT_OTHER );
        assertThat( Files.getLastModifiedTime( orig() ) ).isGreaterThan( before );
    }

    @Test
    @Category( { HardLink.class, Writable.class } )
    public void testHardLinkToExistingFileThrows() throws IOException, InterruptedException {
        assertThatThrownBy( () -> Files.createLink( fileTB(), orig() ) ).isInstanceOf( FileAlreadyExistsException.class );
    }

    @Test
    @Category( { HardLink.class, Writable.class } )
    public void testHardLinkToOtherProviderThrows() throws IOException {
        assertThatThrownBy( () -> Files.createLink( link(), otherProviderFileA() ) ).isInstanceOf( ProviderMismatchException.class );
    }

    // todo
//    @Test( expected = Exception.class )
//    @Category({ HardLink.class})  public void testHardLinkToOtherThrows() throws IOException {
//        assumeThat( capabilities.has2ndFileSystem()).isEqualTo(true));
//
//        Path orig = otherFSFileTA();
//        Path link = getPathPAC();
//        Files.createLink(link, orig);
//    }

    @Test
    @Category( { HardLink.class, Writable.class } )
    public void testHardLinkToDirThrows() throws IOException {
        Files.createLink( link(), orig() );
        assertThat( link() ).exists();
    }

    @Test
    @Category( { HardLink.class, Writable.class } )
    public void testHardLinkToHardLink() throws IOException {
        Files.createLink( link(), orig() );
        Files.createLink( link2(), link() );

        assertThat( link2() ).exists();
    }

    @Test
    @Category( { HardLink.class, Writable.class } )
    public void test2ndHardLink() throws IOException {
        Files.createLink( link(), fileTA() );
        Files.createLink( link2(), fileTA() );

        assertThat( link2() ).exists();
    }

    @Test
    @Category( { HardLink.class } )
    public void testIsSameFileWithHardLink() throws IOException {
        Files.createLink( link(), orig() );

        assertThat( Files.isSameFile( link(), orig() ) ).isTrue();
    }

    @Test
    @Category( { HardLink.class } )
    public void testHardLinkHasSameFileKey() throws IOException {
        Files.createLink( link(), orig() );

        Object fk1 = Files.readAttributes( orig(), BasicFileAttributes.class ).fileKey();
        Object fk2 = Files.readAttributes( link(), BasicFileAttributes.class ).fileKey();

        assertThat( fk1 ).isEqualTo( fk2 );
    }

    @Test
    @Category( { HardLink.class, Writable.class } )
    public void testHardLinkToRelative() throws IOException {
        Files.createLink( link(), relativize( orig() ) );
        assertThat( Files.isSameFile( link(), orig() ) ).isEqualTo( true );
    }

    @Test
    @Category( { HardLink.class, Writable.class, Move.class } )
    public void testMoveHardLinkToRelDoesNotMoveTarget() throws IOException {
        Files.createLink( link(), relativize( orig() ) );
        Files.move( link(), dirTB().resolve( nameC() ) );
        assertThat( Files.isSameFile( dirTB().resolve( nameC() ), orig() ) ).isEqualTo( true );

    }

    /*
     * ----------------------------------------------------------------------------
     */

    protected Path orig() {
        Path ret = dirTA().resolve( "orig" );
        if( !Files.exists( ret ) ) {
            Filess.write( ret, CONTENT );
        }

        return ret;
    }

    protected Path link() {
        return dirTB().resolve( "link" );
    }

    protected Path link2() {
        return dirTB().resolve( "link2" );
    }

}
