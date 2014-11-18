package de.pfabulist.lindwurm.niotest.tests;

import org.junit.Test;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.locks.ReentrantLock;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

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
public abstract class PathTest8ThreadSafeIT extends PathTest7ClosedIT {

    private static final ReentrantLock lock = new ReentrantLock();

    public static class KidsCounter implements Callable<Long> {


        private final Path parent;

        public KidsCounter( Path parent ) {
            this.parent = parent;
        }

        @Override
        public Long call() throws Exception {
            long sum = 0;
            try( DirectoryStream<Path> stream = Files.newDirectoryStream( parent ) ) {
                for ( Path kid : stream ) {
                    sum++;
                    if ( sum > 0 ) {
                        try {
                            lock.lock();
                        } finally {
                            lock.unlock();
                        }
                    }
                }
            }
            return sum;
        }
    }

    @Test
    public void testThreadedDirCount() throws IOException, ExecutionException, InterruptedException {

        // add a kid after opening the dir

        getPathPAf();
        getPathPBf();

        try {
            lock.lock();
            ExecutorService executor = Executors.newFixedThreadPool( 3 );

            Future<Long> future = executor.submit( new KidsCounter(getPathP()));
            executor.shutdown();

            Thread.sleep( 1000 );
            getPathPCf();
            lock.unlock();

            assertThat( future.get(), is( 2L ) );

        } finally {
            if ( lock.isHeldByCurrentThread() ) {
                lock.unlock();
            }
        }


    }



}
