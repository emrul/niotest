package de.pfabulist.lindwurm.niotest.tests;

import org.junit.Test;

import java.io.IOException;
import java.nio.file.FileStore;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.core.Is.is;
import static org.junit.Assume.assumeThat;

/**
 * ** BEGIN LICENSE BLOCK *****
 * BSD License (2 clause)
 * Copyright (c) 2006 - 2014, Stephan Pfab
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
public abstract class PathTest15LimitedFileStoreIT extends PathTest14Principals {

    @Test
    public void testAAA15() throws IOException {
        assumeThat( capabilities.supportsFileStores(), is(true));
        assumeThat( capabilities.hasSizeLimitedFileSystem(), is(true));

        FileStore store = sizeLimitedPlayground.getFileSystem().provider().getFileStore(sizeLimitedPlayground);
        long      limit = store.getUnallocatedSpace();

        assertThat( limit, lessThan(15000L));
        assumeThat( limit, greaterThan(1000L));
    }

    @Test()
    public void testCanCreateFileWithinLimits() throws IOException {
        assumeThat( capabilities.supportsFileStores(), is(true));
        assumeThat( capabilities.hasSizeLimitedFileSystem(), is(true));

        FileStore store = sizeLimitedPlayground.getFileSystem().provider().getFileStore(sizeLimitedPlayground);
//        long      before = store.getUnallocatedSpace();

        Path file = sizeLimitedPlayground.resolve( nameStr[0]);

        Files.write(file, CONTENT);
    }

    @Test( expected = IOException.class )
    public void testCanNotCreateFileToExeedStore() throws IOException {
        assumeThat( capabilities.supportsFileStores(), is(true));
        assumeThat( capabilities.hasSizeLimitedFileSystem(), is(true));

        FileStore store = sizeLimitedPlayground.getFileSystem().provider().getFileStore(sizeLimitedPlayground);
        long      before = store.getUnallocatedSpace();

        assumeThat( before, lessThan(15000L));

        Path file = sizeLimitedPlayground.resolve( nameStr[0]);
        Files.write(file, CONTENT20k);

    }

    @Test( expected = IOException.class )
    public void testCanNotModifyFileToExeedStore() throws IOException {
        assumeThat( capabilities.supportsFileStores(), is(true));
        assumeThat( capabilities.hasSizeLimitedFileSystem(), is(true));

        FileStore store = sizeLimitedPlayground.getFileSystem().provider().getFileStore(sizeLimitedPlayground);
        long      before = store.getUnallocatedSpace();

        Path file = sizeLimitedPlayground.resolve( nameStr[0]);

        assumeThat( before, lessThan(15000L));
        assumeThat( before, greaterThan(1000L));

        Files.write(file, CONTENT);
        Files.write(file, CONTENT20k);

    }

}
