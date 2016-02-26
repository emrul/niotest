package de.pfabulist.lindwurm.niotest.tests;

import de.pfabulist.kleinod.nio.PathIKWID;
import de.pfabulist.lindwurm.niotest.tests.descriptionbuilders.SecondFS;
import de.pfabulist.lindwurm.niotest.tests.topics.Copy;
import de.pfabulist.lindwurm.niotest.tests.topics.SecondFileSystem;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.ProviderMismatchException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * ** BEGIN LICENSE BLOCK *****
 * BSD License (2 clause)
 * Copyright (c) 2006 - 2016, Stephan Pfab
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
// same provider different FS
public abstract class Tests12DifferentFS extends Tests11Watcher {
    public Tests12DifferentFS( FSDescription capa ) {
        super( capa );
    }

    @Test
    @Category( { SecondFileSystem.class, Copy.class } )
    public void testCopyToDifferentFS() throws IOException {
        Path src = fileTA();
        FS.provider().copy( src, otherFSabsTA() );

        assertThat( otherFSabsTA() ).exists();
    }

    @Test
    @Category( { SecondFileSystem.class, Copy.class } )
    public void testMoveToDifferentFS() throws IOException {
        Path src = fileTA();
        FS.provider().move( src, otherFSabsTA() );

        assertThat( otherFSabsTA() ).exists();
        assertThat( absTA() ).doesNotExist();
    }

    @Test
    @Category( { SecondFileSystem.class } )
    public void testIsSameFileDifferentFSIsFalse() throws IOException {
        assertThat( FS.provider().isSameFile( absAB(), otherFSFileTA() )).isFalse();
    }

    @Test
    @Category( { SecondFileSystem.class } )
    public void testCompareToDifferentFSPathSameToStringButDifferent() throws IOException {
        assertThat( absAB().toString().compareTo( otherFSAbsAB().toString() ) ).isEqualTo( 0 );
        assertThat( absAB().compareTo( otherFSAbsAB() )).isNotEqualTo( 0 );
    }

    @Test
    @Category( { SecondFileSystem.class } )
    public void testStartsWithDifferentFSIsFalse() throws IOException {
        assertThat( absAB().startsWith( otherFSAbsAB() )).isFalse();
    }

    @Test
    @Category( { SecondFileSystem.class } )
    public void testEndsWithDifferentFSIsFalse() throws IOException {
        assertThat( absAB().endsWith( otherFSAbsAB() )).isFalse();
    }

    @Test
    @Category( { SecondFileSystem.class } )
    public void testRelativize() {
        assertThatThrownBy( () -> absAB().relativize( otherFSAbsA() )).isInstanceOf( ProviderMismatchException.class );
    }

    //    @Test
//    public void testMoveDifferentFS() throws IOException {
//        assumeThat( capabilities.has2ndFileSystem(), is(true));
//
//        Path src = getPathPABf();
//        FS.provider().move(src, otherFSabsTA());
//
//        assertThat( otherFSabsTA(), exists());
//        assertThat( src, not(exists()));
//    }
//
//    @Test
//    public void testWatchACreateFromCopyFromOtherFS() throws Exception {
//        assumeThat(play2, CoreMatchers.notNullValue());
//        assumeThat( capabilities.supportsWatchService(), Is.is(true));
//
//
//        watcherSetup(ENTRY_CREATE);
//        Files.copy( otherFSFileTA(), getPathWA());
//
//        assertThat( waitForWatchService().poll(), WatchKeyMatcher.correctKey(getPathWA(), ENTRY_CREATE));
//    }
//
//    @Test
//    public void testWatchACreateFromMoveFromOtherFS() throws Exception {
//        assumeThat(play2, CoreMatchers.notNullValue());
//        assumeThat( capabilities.supportsWatchService(), Is.is(true));
//
//        watcherSetup(ENTRY_CREATE);
//        Files.move(otherFSFileTA(), getPathWA());
//
//        assertThat( waitForWatchService().poll(), WatchKeyMatcher.correctKey(getPathWA(), ENTRY_CREATE));
//    }
//
//
//    /*
//     * ------------------------------------------------------------------------------------------------------
//     */
//
//    protected Path play2;
//

    public Path otherFSAbsA() throws IOException {
        FileSystem fs = description.get( Path.class, SecondFS.PLAYGROUND2 ).getFileSystem();
        return PathIKWID.absoluteGetRoot( fs.getPath( "" ).toAbsolutePath()).resolve( nameA() );
    }
    public Path otherFSAbsAB() throws IOException {
        return otherFSAbsA().resolve( nameB() );
    }


    public Path otherFSabsTA() throws IOException {
        Path dir = description.get( Path.class, SecondFS.PLAYGROUND2 ).resolve( testMethodName.getMethodName() );
        Files.createDirectories( dir );
        return dir.resolve( nameA() );
    }

    public Path otherFSFileTA() throws IOException {
        Path ret = otherFSabsTA();
        if ( !Files.exists( ret )) {
            Files.write( ret, CONTENT, standardOpen);
        }
        return ret;
    }

}
