package de.pfabulist.lindwurm.niotest.tests;

import de.pfabulist.lindwurm.niotest.tests.topics.Readonly;
import de.pfabulist.lindwurm.niotest.tests.topics.SymLink;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.AccessMode;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.ReadOnlyFileSystemException;
import java.nio.file.attribute.FileTime;

import static java.nio.file.StandardOpenOption.APPEND;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;

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

public abstract class Tests21Readonly extends Tests20SymLinks {

    public Tests21Readonly( FSDescription capa ) {
        super( capa );
    }

    @Test( expected = ReadOnlyFileSystemException.class )
    @Category( { Readonly.class } )
    public void testCanNotWriteToReadonlyFile() throws IOException {
        Files.write( getFile(), CONTENT );
    }

    @Test( expected = ReadOnlyFileSystemException.class )
    @Category( { Readonly.class } )
    public void testCanNotAppendToReadonlyFile() throws IOException {
        Files.write( getFile(), CONTENT, APPEND );
    }

    @Test
    @Category( { Readonly.class } )
    public void testReadonyFileInReadonlyFS() throws IOException {
        assertThat( getFile().getFileSystem().isReadOnly(), is( true ) );
    }

    @Test
    @Category( { Readonly.class } )
    public void testReadFromReadonly() throws IOException {
        assertThat( Files.readAllBytes( getFile() ), is( CONTENT ));
    }

    @Test
    @Category( { Readonly.class } )
    public void testReadAttributefromReadonly() throws IOException {
        Files.getLastModifiedTime( getFile() );
        assertThat( "got here", is("got here") );
    }

    @Test( expected = ReadOnlyFileSystemException.class )
    @Category( { Readonly.class } )
    public void testSetAttributeToReadonlyThrows() throws IOException {
        Files.setLastModifiedTime( getFile(), FileTime.fromMillis( 500 ) );
    }

    @Test
    @Category( { Readonly.class } )
    public void testReadDirFromReadonly()  {
        try {
            Files.list( getNonEmptyDir() ).forEach( p -> {} );
        } catch( IOException e ) {
            fail( "read or readonly dir should work" );
        }
    }

    @Test( expected = ReadOnlyFileSystemException.class )
    @Category( { Readonly.class } )
    public void testWriteNewFileToReadonly() throws IOException {
        Files.write( getNonEmptyDir().resolve( "newfile" ), CONTENT );
    }

    @Test( expected = ReadOnlyFileSystemException.class )
    @Category( { Readonly.class } )
    public void testMoveFileInReadonly() throws IOException {
        Files.move( getFile(), getNonEmptyDir().resolve( "newfile" ) );
    }

    @Test( expected = ReadOnlyFileSystemException.class )
    @Category( { Readonly.class } )
    public void testCopyFileInReadonly() throws IOException {
        Files.copy( getFile(), getNonEmptyDir().resolve( "newfile" ) );
    }

    @Test( expected = ReadOnlyFileSystemException.class )
    @Category( { Readonly.class } )
    public void testDeleteFileInReadonly() throws IOException {
        Files.delete( getFile() );
    }

    @Test( expected = ReadOnlyFileSystemException.class )
    @Category( { Readonly.class } )
    public void testDeleteDirInReadonly() throws IOException {
        Files.delete( getEmptyDir() );
    }

    @Test( expected = ReadOnlyFileSystemException.class )
    @Category( { Readonly.class } )
    public void testCreateHardLinkInReadonly() throws IOException {
        Files.createLink( getNonEmptyDir().resolve( "newLink" ), getFile() );
    }

    @Test( expected = ReadOnlyFileSystemException.class )
    @Category( { Readonly.class, SymLink.class } )
    public void testCreateSymLinkInReadonly() throws IOException {
        Files.createSymbolicLink( getNonEmptyDir().resolve( "newLink" ), getFile() );
    }

    @Test( expected = AccessDeniedException.class )
    @Category( Readonly.class )
    public void testCheckAccessDoesNotSupportesWrite() throws IOException {
        // should not throw UnsupportedOperationException
        FS.provider().checkAccess( getFile(), AccessMode.WRITE );
    }

    // --------------------------------------------------------------

}
