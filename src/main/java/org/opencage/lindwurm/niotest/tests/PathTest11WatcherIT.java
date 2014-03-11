package org.opencage.lindwurm.niotest.tests;

import org.junit.Test;
import org.opencage.kleinod.collection.Ref;
import org.opencage.kleinod.collection.Sets;

import java.io.IOException;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.*;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

import static java.nio.file.StandardOpenOption.WRITE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.core.Is.is;
import static org.junit.Assume.assumeThat;
import static org.opencage.lindwurm.niotest.matcher.PathAbsolute.absolute;
import static org.opencage.lindwurm.niotest.matcher.PathExists.exists;

/**
 * ** BEGIN LICENSE BLOCK *****
 * BSD License (2 clause)
 * Copyright (c) 2006 - 2013, Stephan Pfab
 * All rights reserved.
 * <p/>
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * <p/>
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
public abstract class PathTest11WatcherIT extends PathTest10PathWithContentIT {


    @Test(expected = ProviderMismatchException.class)
    public void testRegisterOtherPath() throws Exception {
        assumeThat( capabilities.supportsWatchService(), is(true));

        WatchService ws = getOther().getFileSystem().newWatchService();

        getPathPABf().register(ws, ENTRY_DELETE);
    }

    @Test
    public void testWatchADelete() throws Exception {
        assumeThat( capabilities.supportsWatchService(), is(true));

        final ConcurrentLinkedDeque<Path> dels = new ConcurrentLinkedDeque<>();

        Path dir = getPathPA();
        Path toBeDeleted = getPathPABf();

        new Thread(new Watcher(dir, dels, ENTRY_DELETE)).start();

        Thread.sleep(1000);
        Files.delete(toBeDeleted);

        Thread.sleep(getWatcherSleep());

        assertThat(dels.size(), is(1));
    }


//    @Test
//    public void testWD() throws Exception {
//        assumeThat( capabilities.supportsWatchService(), is(true));
//
//        Path toDel = getPathWf();
//        watch( toDel.getParent(), ENTRY_DELETE );
//        Files.delete(toDel);
//        Thread.sleep(getWatcherSleep());
//
//        assertThat( polledEvents().size(), is(1));
//
//    }

    @Test( expected = ClosedWatchServiceException.class )
    public void testRegisterOnClosedWatchService() throws IOException {
        assumeThat( capabilities.supportsWatchService(), is(true));

        WatchService watcher = FS.newWatchService();
        watcher.close();
        getPathPAd().register(watcher, ENTRY_CREATE);
    }


    @Test( expected = ClosedWatchServiceException.class )
    public void testRegisterWatchServiceOfClosedFS() throws Exception {
        assumeThat( capabilities.supportsWatchService(), is(true));
        assumeThat( capabilities.isClosable(), is(true));

        getClosedBd().register(closedFSWatchService, ENTRY_DELETE);
    }

    @Test
    public void testWatchADeleteFromMove() throws Exception {
        assumeThat( capabilities.supportsWatchService(), is(true));

        final ConcurrentLinkedDeque<Path> dels = new ConcurrentLinkedDeque<>();

        Path dir = getPathPA();
        Path toBeDeleted = getPathPABf();

        new Thread(new Watcher(dir, dels, ENTRY_DELETE)).start();
        Thread.sleep(3000);

        Files.move(toBeDeleted, getPathPB());
        assertThat(toBeDeleted, not(exists()));

        Thread.sleep(getWatcherSleep());

        assertThat(dels.size(), is(1));
    }

    @Test
    public void testWatchAModify() throws Exception {
        assumeThat(capabilities.supportsWatchService(), is(true));

        final ConcurrentLinkedDeque<Path> que = new ConcurrentLinkedDeque<>();

        Path file = getPathPABf();
        // java 7 can only watch dirs
        new Thread(new Watcher(file.getParent(), que, StandardWatchEventKinds.ENTRY_MODIFY)).start();

        Thread.sleep(2000);

        Files.write(file, CONTENT_OTHER);

        Thread.sleep(getWatcherSleep());

        assertThat(que.size(), is(1));
    }

    @Test
    public void testWatchACreate() throws Exception {
        assumeThat( capabilities.supportsWatchService(), is(true));

        final ConcurrentLinkedDeque<Path> que = new ConcurrentLinkedDeque<>();

        Path dir = getPathPAd();
        new Thread(new Watcher(dir, que, StandardWatchEventKinds.ENTRY_CREATE )).start();

        Thread.sleep(2000);

        Files.write(getPathPAB(),CONTENT_OTHER);

        Thread.sleep(getWatcherSleep());

        assertThat(que.size(), is(1));
    }

    @Test
    public void testWatchACreateFromCopy() throws Exception {
        assumeThat( capabilities.supportsWatchService(), is(true));

        final ConcurrentLinkedDeque<Path> que = new ConcurrentLinkedDeque<>();

        Path file = getPathPABf();
        new Thread(new Watcher(file.getParent(), que, StandardWatchEventKinds.ENTRY_CREATE )).start();

        Thread.sleep(2000);

        Files.copy( file, getPathPAC());

        Thread.sleep(getWatcherSleep());

        assertThat(que.size(), is(1));
    }

    @Test
    public void testWatchACreateFromCopyFromOtherFS() throws Exception {
        assumeThat(play2, notNullValue());
        assumeThat( capabilities.supportsWatchService(), is(true));

        final ConcurrentLinkedDeque<Path> que = new ConcurrentLinkedDeque<>();

        Path src = getPathOtherPAf();
        Path dir = getPathPAd();

        new Thread(new Watcher(dir, que, StandardWatchEventKinds.ENTRY_CREATE )).start();

        Thread.sleep(2000);

        FS.provider().copy(src, getPathPAC());

        Thread.sleep(getWatcherSleep());

        assertThat(que.size(), is(1));
    }

    @Test
    public void testWatchACreateFromMove() throws Exception {
        assumeThat( capabilities.supportsWatchService(), is(true));

        final ConcurrentLinkedDeque<Path> que = new ConcurrentLinkedDeque<>();

        Path file = getPathPABf();
        new Thread(new Watcher(file.getParent(), que, StandardWatchEventKinds.ENTRY_CREATE )).start();

        Thread.sleep(2000);

        Files.move(file, getPathPAC());

        Thread.sleep(getWatcherSleep());

        assertThat(que.size(), is(1));
    }

    @Test
    public void testWatchACreateFromMoveFromOtherFS() throws Exception {
        assumeThat(play2, notNullValue());
        assumeThat( capabilities.supportsWatchService(), is(true));

        final ConcurrentLinkedDeque<Path> que = new ConcurrentLinkedDeque<>();

        Path src = getPathOtherPAf();
        Path dir = getPathPAd();

        new Thread(new Watcher(dir, que, StandardWatchEventKinds.ENTRY_CREATE )).start();

        Thread.sleep(2000);

        FS.provider().copy( src, getPathPAC());

        Thread.sleep(getWatcherSleep());

        assertThat(que.size(), is(1));
    }

    @Test
    public void testCopyDirReplaceExistingOverwritesFile() throws Exception {
        assumeThat( capabilities.supportsWatchService(), is(true));

        final ConcurrentLinkedDeque<Path> que = new ConcurrentLinkedDeque<>();

        Path tgt = getPathPA();
        Files.write( tgt, CONTENT, standardOpen );
        Path src = getPathPB();
        Files.createDirectories( src );

        new Thread(new Watcher(tgt.getParent(), que, StandardWatchEventKinds.ENTRY_MODIFY )).start();
        Thread.sleep(2000);

        Files.copy( src, tgt, StandardCopyOption.REPLACE_EXISTING );

        Thread.sleep(getWatcherSleep());
        assertThat(que.size(), is(1));

    }

    @Test
    public void testCanceledWatchKeyDoesNotWatch() throws Exception {
        assumeThat( capabilities.supportsWatchService(), is(true));

        final ConcurrentLinkedDeque<Path> dels = new ConcurrentLinkedDeque<>();

        Path dir = getPathPA();
        Path toBeDeleted = getPathPABf();

        new Thread(new Watcher(dir, dels, ENTRY_DELETE)).start();

        Thread.sleep(1000);
        Files.delete(toBeDeleted);

        Thread.sleep(getWatcherSleep());

        assertThat(dels.size(), is(1));
    }



    @Test
    public void testWatchATruncate() throws Exception {
        assumeThat( capabilities.supportsWatchService(), is(true));

        Path file = getPathPAf();
        final ConcurrentLinkedDeque<Path> que = new ConcurrentLinkedDeque<>();
        new Thread(new Watcher(file.getParent(), que, StandardWatchEventKinds.ENTRY_MODIFY )).start();
        Thread.sleep(2000);

        try( SeekableByteChannel channel =  FS.provider().newByteChannel( file, Sets.asSet(WRITE) )) {
            channel.truncate( 2 );
        }

        Thread.sleep(getWatcherSleep());

        assertThat(que.size(), is(1));
    }


    @Test
    public void testTakeBlocks() throws Exception {
        assumeThat( capabilities.supportsWatchService(), is(true));

        Path dir = getPathPAd();
        final WatchService watcher = dir.getFileSystem().newWatchService();
        dir.register( watcher, StandardWatchEventKinds.ENTRY_CREATE );

        final Ref<Boolean> interrupted = Ref.valueOf(false);

        new Thread( new Runnable() {
            @Override
            public void run() {
                try {
                    watcher.take();
                } catch (InterruptedException e) {
                } catch ( ClosedWatchServiceException e ) {
                } finally {
                    interrupted.set(true);

                }
            }
        }).start();

        Thread.sleep(1000);

        assertThat(interrupted.get(), is(false));

    }

    @Test
    public void testCloseAWatchServiceRelasesBlockedTreads() throws Exception {
        assumeThat( capabilities.supportsWatchService(), is(true));

        Path dir = getPathPAd();
        final WatchService watcher = dir.getFileSystem().newWatchService();
        dir.register( watcher, StandardWatchEventKinds.ENTRY_CREATE );

        final Ref<Boolean> interrupted = Ref.valueOf(false);

        new Thread( new Runnable() {
            @Override
            public void run() {
                try {
                    watcher.take();
                } catch (InterruptedException e) {
                } catch ( ClosedWatchServiceException e ) {
                    interrupted.set(true);
                }
            }
        }).start();

        watcher.close();
        Thread.sleep(1000);

        assertThat(interrupted.get(), is(true));

    }

    @Test
    public void testCloseAWatchServiceCancelsKeys() throws Exception {
        assumeThat( capabilities.supportsWatchService(), is(true));

        Path dir = getPathPAd();
        final WatchService watcher = dir.getFileSystem().newWatchService();
        WatchKey key = dir.register( watcher, StandardWatchEventKinds.ENTRY_CREATE );

        watcher.close();
        Thread.sleep(getWatcherSleep());

        assertThat(key.isValid(), is(false));

    }


    @Test
    public void testPollAnEmptyWatchServiceReturnsNull() throws Exception {
        assumeThat( capabilities.supportsWatchService(), is(true));

        Path dir = getPathPAd();
        final WatchService watcher = dir.getFileSystem().newWatchService();
        dir.register( watcher, StandardWatchEventKinds.ENTRY_CREATE );

        assertThat(watcher.poll(), nullValue());
    }

    @Test
    public void testWatchADeleteWithPoll() throws Exception {
        assumeThat( capabilities.supportsWatchService(), is(true));

        Path dir = getPathPA();
        Path toBeDeleted = getPathPABf();
        final WatchService watcher = dir.getFileSystem().newWatchService();
        dir.register( watcher, ENTRY_DELETE );

        Thread.sleep(1000);
        Files.delete(toBeDeleted);

        Thread.sleep(getWatcherSleep());

        WatchKey watchKey = watcher.poll();
        assertThat(watchKey, notNullValue());
        assertThat(watchKey.isValid(), is(true));
        //watchKey.pollEvents();
    }

    @Test
    public void testWatchKeyPollEventsEmptiesQue() throws Exception {
        assumeThat( capabilities.supportsWatchService(), is(true));

        Path dir = getPathPA();
        Path toBeDeleted = getPathPABf();
        final WatchService watcher = dir.getFileSystem().newWatchService();
        dir.register( watcher, ENTRY_DELETE );

        Thread.sleep(1000);
        Files.delete(toBeDeleted);

        Thread.sleep(getWatcherSleep());

        WatchKey watchKey = watcher.poll();
        watchKey.pollEvents();
        assertThat( watchKey.pollEvents(), empty());
    }

    @Test
    public void testSignaledWatchKeyWatchesCorrectDir() throws Exception {
        assumeThat( capabilities.supportsWatchService(), is(true));

        Path dir = getPathPA();
        Path toBeDeleted = getPathPABf();
        final WatchService watcher = dir.getFileSystem().newWatchService();
        dir.register( watcher, ENTRY_DELETE );

        Thread.sleep(1000);
        Files.delete(toBeDeleted);

        Thread.sleep(getWatcherSleep());

        WatchKey watchKey = watcher.poll();

        assertThat( watchKey.watchable(), is((Watchable) dir));
    }

    @Test
    public void testSignaledEventReportsCorrectPath() throws Exception {
        assumeThat( capabilities.supportsWatchService(), is(true));

        Path dir = getPathPA();
        Path toBeDeleted = getPathPABf();
        final WatchService watcher = dir.getFileSystem().newWatchService();
        dir.register( watcher, ENTRY_DELETE );

        Thread.sleep(1000);
        Files.delete(toBeDeleted);

        Thread.sleep(getWatcherSleep());

        WatchKey watchKey = watcher.poll();

        Path path = (Path) watchKey.pollEvents().iterator().next().context();

        assertThat(path,not(absolute()));
        assertThat( dir.resolve(path), is(toBeDeleted ));
    }


    @Test
    public void testCanceledKeyStopsWatching() throws Exception {
        assumeThat( capabilities.supportsWatchService(), is(true));

        Path dir = getPathPAd();
        final WatchService watcher = dir.getFileSystem().newWatchService();
        WatchKey key = dir.register( watcher, StandardWatchEventKinds.ENTRY_CREATE );

        final Ref<WatchKey> hm = Ref.valueOf(null);

        new Thread( new Runnable() {
            @Override
            public void run() {
                try {
                    hm.set( watcher.take());
                } catch (InterruptedException e) {
                } catch ( ClosedWatchServiceException e ) {
                }
            }
        }).start();

        Thread.sleep(1000);

        key.cancel();

        Thread.sleep(1000);

        getPathPABf();

        Thread.sleep(getWatcherSleep());

        assertThat(hm.get(), nullValue());
    }

    @Test
    public void testDeleteWatchedDirCancelsKeys() throws Exception {
        assumeThat( capabilities.supportsWatchService(), is(true));

        Path dir = getPathPAd();
        final WatchService watcher = dir.getFileSystem().newWatchService();
        WatchKey key = dir.register( watcher, StandardWatchEventKinds.ENTRY_CREATE );

        Files.delete(dir);
        Thread.sleep(getWatcherSleep());

        assertThat(key.isValid(), is(false));
    }

    @Test
    public void testMovedWatchedDirCancelsKeys() throws Exception {
        assumeThat( capabilities.supportsWatchService(), is(true));

        Path dir = getPathPAd();
        final WatchService watcher = dir.getFileSystem().newWatchService();
        WatchKey key = dir.register( watcher, StandardWatchEventKinds.ENTRY_CREATE );

        Files.move( dir, getPathPB());
        Thread.sleep(getWatcherSleep());

        assertThat(key.isValid(), is(false));
    }

    @Test
    public void testWatchTwoModifiesOneKey() throws Exception {
        assumeThat(capabilities.supportsWatchService(), is(true));

        final ConcurrentLinkedDeque<Path> que = new ConcurrentLinkedDeque<>();

        Path file = getPathPABf();
        // java 7 can only watch dirs
        new Thread(new Watcher(file.getParent(), que, StandardWatchEventKinds.ENTRY_MODIFY)).start();

        Thread.sleep(2000);

        Files.write(file, CONTENT_OTHER);
        Files.write(file, CONTENT);

        Thread.sleep(getWatcherSleep());

        assertThat(que.size(), is(1));
    }

    /*
     * ------------------------------------------------------------------------------------
     */

    // todo refactor
    private static class Watcher implements Runnable {
        private final Path dir;
        private final ConcurrentLinkedDeque<Path> dels;
        private final WatchEvent.Kind<Path> kind;

        public Watcher(Path dir, ConcurrentLinkedDeque<Path> dels, WatchEvent.Kind<Path> kind) {
            this.dir = dir;
            this.dels = dels;
            this.kind = kind;
        }

        @Override
        public void run() {
            try {
                WatchService watcher = dir.getFileSystem().newWatchService();
                dir.register(watcher, kind);
                WatchKey watckKey = watcher.take();
                List<WatchEvent<?>> events = watckKey.pollEvents();
                for (WatchEvent event : events) {
                    if ( event.kind().equals(kind)) {
                        dels.add((Path) event.context());
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Error: " + e.toString());
            }
        }
    }

    private long watcherSleep = 1000;

    public long getWatcherSleep() {
        return watcherSleep;
    }

    public void setWatcherSleep(long watcherSleep) {
        this.watcherSleep = watcherSleep;
    }
}
