package de.pfabulist.lindwurm.niotest.tests;

import de.pfabulist.kleinod.os.OS;
import de.pfabulist.kleinod.os.PathLimits;
import de.pfabulist.lindwurm.niotest.tests.topics.Attributes;
import de.pfabulist.lindwurm.niotest.tests.topics.FileOwnerView;
import de.pfabulist.lindwurm.niotest.tests.topics.HiddenDotFiles;
import de.pfabulist.lindwurm.niotest.tests.topics.MaxFilename;
import de.pfabulist.lindwurm.niotest.tests.topics.NotOSX;
import de.pfabulist.lindwurm.niotest.tests.topics.OwnerView;
import de.pfabulist.lindwurm.niotest.tests.topics.PermissionChecks;
import de.pfabulist.lindwurm.niotest.tests.topics.Posix;
import de.pfabulist.lindwurm.niotest.tests.topics.Principals;
import de.pfabulist.lindwurm.niotest.tests.topics.Unix;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.FileSystemException;
import java.nio.file.Files;
import java.nio.file.attribute.PosixFileAttributes;
import java.nio.file.attribute.UserPrincipal;
import java.nio.file.attribute.UserPrincipalLookupService;

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
public abstract class Tests16Unix extends Tests13FileStore {
    public Tests16Unix( FSDescription capa ) {
        super( capa );
    }

    //    @Test
//    public void testDefaultOwnerIsFoundInLookpupService() throws IOException {
//        assumeThat( capabilities.supportsPrincipals(), is(true));
//        assumeThat( capabilities.supportsPosixAttributes(), is(true));
//
//        UserPrincipal owner = Files.readAttributes(getDefaultPath(), PosixFileAttributes.class).owner();
//
//        assertThat( owner, is(FS.getUserPrincipalLookupService().lookupPrincipalByName(owner.getName())));
//    }
//
    @Test
    @Category( { Attributes.class, Posix.class, Unix.class } )
    public void testPosixGetAttributeView() throws IOException {
        assertThat( Files.readAttributes( getFile(), PosixFileAttributes.class ) ).isNotNull();
    }

    @Test
    @Category( Unix.class )
    public void testUnixSeparatorIsSlash() {
        assertThat( FS.getSeparator() ).isEqualTo( "/" );
    }

    @Test
    @Category({ Unix.class, HiddenDotFiles.class })
    public void testDotFilesAreHidden() throws IOException {
        assertThat( Files.isHidden( getFile().resolve( ".dot" ) ) ).isTrue();
    }

    @Test
    @Category( { Attributes.class, Posix.class, OwnerView.class } )
    public void testOwnerByTwoMethods() throws IOException {

        assertThat( Files.getOwner( pathDefault() )).isEqualTo(
                    Files.readAttributes( pathDefault(), PosixFileAttributes.class ).owner() );
    }

    @Test
    @Category( { Principals.class, Posix.class, Unix.class, PermissionChecks.class } )
    public void testDifferentOwnerCantWrite() throws IOException {
        UserPrincipal none = FS.getUserPrincipalLookupService().lookupPrincipalByName( "root" );

        Files.setOwner( fileTA(), none );
        //System.out.println( Files.getPosixFilePermissions( fileTA() ) );

        assertThatThrownBy( () -> Files.write( absTA(), CONTENT_OTHER ) ).isInstanceOf( AccessDeniedException.class );
    }

    @Test
    @Category( { Posix.class, Unix.class, Principals.class } )
    public void testFilesHaveOwners() throws IOException {
        assertThat( Files.getOwner( getFile() ) ).isNotNull();
    }

    @Test
    @Category( { Principals.class } )
    public void testGetPrincipalsLookupServiceDoesNotThrow() throws IOException {
        assertThat( FS.getUserPrincipalLookupService() ).isNotNull();
    }

    @Test
    @Category( { Principals.class, Attributes.class, FileOwnerView.class } )
    public void testFindOwner() throws IOException {
        UserPrincipalLookupService lookupService = FS.getUserPrincipalLookupService();

        UserPrincipal owner = Files.getOwner( FS.getPath( "" ).toAbsolutePath() );

        assertThat( lookupService.lookupPrincipalByName( owner.getName() ) ).isEqualTo( owner );
    }

    @Test
    @Category( { Unix.class, NotOSX.class, MaxFilename.class } )
    public void testFilenameTooLongBecauseUnicode() throws IOException {
        String str = new String( Character.toChars( 0x10400 ) );
        PathLimits limits = new PathLimits( OS.UNIX );

        String fname = longFileName( limits.getMaxPathLength() - str.length(), str );

        assertThat( fname.length() ).isLessThan( limits.filenameCount( fname ) );

        assertThatThrownBy( () -> Files.write( absT().resolve( fname ), CONTENT ) ).isInstanceOf( FileSystemException.class );
    }

    // ?? should be true for osx
//    @Test
//    public void testColonIsNoFileName() {
//        Path path = FS.getPath( nameD() + ":" + nameA() );
//
//        for( Path elem : path ) {
//            Assert.assertThat( elem.toString(), not( containsString( ":" ) ) );
//        }
//
//    }

}
