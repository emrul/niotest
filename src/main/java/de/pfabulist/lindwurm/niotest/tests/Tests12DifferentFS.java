package de.pfabulist.lindwurm.niotest.tests;


/**
* ** BEGIN LICENSE BLOCK *****
* BSD License (2 clause)
* Copyright (c) 2006 - 2015, Stephan Pfab
* All rights reserved.
*
* Redistribution and use in source and binary forms, with or without
* modification, are permitted provided that the following conditions are met:
* * Redistributions of source code must retain the above copyright
* notice, this list of conditions and the following disclaimer.
* * Redistributions in binary form must reproduce the above copyright
* notice, this list of conditions and the following disclaimer in the
* documentation and/or other materials provided with the distribution.
*
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
public abstract class Tests12DifferentFS extends Tests11Watcher {
    public Tests12DifferentFS( FSDescription capa ) {
        super(capa);
    }

//    @Test
//    public void testAAA12() {
//        assumeThat( capabilities.has2ndFileSystem(), is(true));
//
//        assertThat(play2, notNullValue() );
//
//        assertThat( FS.provider(), is(play2.getFileSystem().provider()));
//        assertThat( FS, not(is(play2.getFileSystem())));
//    }
//
//    @Test
//    public void testCopyDifferentFS() throws IOException {
//        assumeThat( capabilities.has2ndFileSystem(), is(true));
//
//        Path src = getPathPABf();
//        FS.provider().copy( src, getPathOtherPA());
//
//        assertThat( getPathOtherPA(), exists());
//    }
//
//    @Test
//    public void testIsSameFileDifferentFS() throws IOException {
//        assumeThat( capabilities.has2ndFileSystem(), is(true));
//
//        assertThat(FS.provider().isSameFile(getPathPAf(), getPathOtherPAf()), is(false));
//    }
//
//    @Test
//    public void testMoveDifferentFS() throws IOException {
//        assumeThat( capabilities.has2ndFileSystem(), is(true));
//
//        Path src = getPathPABf();
//        FS.provider().move(src, getPathOtherPA());
//
//        assertThat( getPathOtherPA(), exists());
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
//        Files.copy( getPathOtherPAf(), getPathWA());
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
//        Files.move(getPathOtherPAf(), getPathWA());
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
//    public Path getPathOtherPA() throws IOException {
//        Path dir = play2.resolve( testMethodName.getMethodName() );
//        Files.createDirectories(dir);
//        return dir.resolve(nameStr[0]);
//    }
//
//    public Path getPathOtherPAf() throws IOException {
//        Path dir = play2.resolve( testMethodName.getMethodName() );
//        Files.createDirectories(dir);
//        Path ret = dir.resolve(nameStr[0]);
//        Files.write( ret, CONTENT, standardOpen);
//        return ret;
//    }


}
