package org.opencage.lindwurm.niotest.tests;

import org.hamcrest.CoreMatchers;
import org.junit.Test;
import org.opencage.kleinod.collection.Ref;
import org.opencage.kleinod.collection.Sets;
import org.opencage.lindwurm.niotest.matcher.WatchKeyMatcher;

import java.io.IOException;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.*;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.TimeUnit;

import static java.nio.file.StandardOpenOption.WRITE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.core.Is.is;
import static org.junit.Assume.assumeThat;
import static org.opencage.lindwurm.niotest.matcher.Has.has;
import static org.opencage.lindwurm.niotest.matcher.PathAbsolute.absolute;
import static org.opencage.lindwurm.niotest.matcher.WatchEventMatcher.isEvent;

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


    private WatchService watchService;

    @Test(expected = ProviderMismatchException.class)
    public void testRegisterOtherPath() throws Exception {
        assumeThat( capabilities.supportsWatchService(), is(true));

        WatchService ws = getOther().getFileSystem().newWatchService();

        getPathPABf().register(ws, ENTRY_DELETE);
    }


    @Test
    public void testWatchADelete() throws Exception {
        assumeThat(capabilities.supportsWatchService(), is(true));

        Path toBeDeleted = createPathWAf();
        watcherSetup(ENTRY_DELETE);
        Files.delete(toBeDeleted);

        assertThat( waitForWatchService().poll(), WatchKeyMatcher.correctKey( toBeDeleted, ENTRY_DELETE ));
    }

    @Test( timeout = 20000 )
    public void testWatchADeleteTake() throws Exception {
        assumeThat(capabilities.supportsWatchService(), is(true));

        Path toBeDeleted = createPathWAf();
        watcherSetup(ENTRY_DELETE);
        Files.delete(toBeDeleted);

        assertThat( waitForWatchService().take(), WatchKeyMatcher.correctKey( toBeDeleted, ENTRY_DELETE ));
    }

    @Test( timeout = 20000 )
    public void testWatchADeletePollTimeOut() throws Exception {
        assumeThat(capabilities.supportsWatchService(), is(true));

        Path toBeDeleted = createPathWAf();
        watcherSetup(ENTRY_DELETE);
        Files.delete(toBeDeleted);

        assertThat( waitForWatchService().poll( 1000, TimeUnit.MILLISECONDS), WatchKeyMatcher.correctKey( toBeDeleted, ENTRY_DELETE ));
    }

    @Test( timeout = 3000 )
    public void testPollWithTimeoutTimesOut() throws Exception {
        assumeThat(capabilities.supportsWatchService(), is(true));

        watcherSetup(ENTRY_DELETE);
        // no events

        getWatchService().poll( 1000, TimeUnit.MILLISECONDS);
    }


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
    public void testWatchADeleteFromAMove() throws Exception {
        assumeThat(capabilities.supportsWatchService(), is(true));

        Path toBeMoved = createPathWAf();
        watcherSetup(ENTRY_DELETE);
        Files.move(toBeMoved, getPathPB());

        assertThat( waitForWatchService().poll(), WatchKeyMatcher.correctKey( toBeMoved, ENTRY_DELETE ));
    }

    @Test
    public void testWatchAModify() throws Exception {
        assumeThat(capabilities.supportsWatchService(), is(true));

        Path toBeModified = createPathWAf();
        watcherSetup(ENTRY_MODIFY);
        Files.write(toBeModified, CONTENT_OTHER);

        assertThat( waitForWatchService().poll(), WatchKeyMatcher.correctKey( toBeModified, ENTRY_MODIFY ));
    }

    @Test
    public void testKidsChangesOfADirIsNotAModify() throws Exception {
        assumeThat(capabilities.supportsWatchService(), is(true));

        Path toBeCreated = getPathWAB();
        Path dir = createPathWAd();
        watcherSetup(ENTRY_MODIFY);
        Files.write(toBeCreated, CONTENT);

        assertThat( waitForWatchService().poll(), nullValue() );
    }

    @Test
    public void testReadIsNotModify() throws Exception {
        assumeThat(capabilities.supportsWatchService(), is(true));

        Path toBeModified = createPathWAf();
        watcherSetup(ENTRY_MODIFY);
        Files.readAllBytes(toBeModified );

        assertThat( waitForWatchService().poll(), nullValue() );
    }

    @Test
    public void testWatchForOtherEventCatchesNothing() throws Exception {
        assumeThat(capabilities.supportsWatchService(), is(true));

        Path toBeModified = createPathWAf();
        watcherSetup(ENTRY_CREATE );
        Files.write(toBeModified, CONTENT_OTHER);

        assertThat( waitForWatchService().poll(), nullValue() );
    }

    @Test
    public void testWatchInOtherDirCatchesNothing() throws Exception {
        assumeThat(capabilities.supportsWatchService(), is(true));

        watcherSetup(ENTRY_CREATE );
        getPathPAf();

        assertThat( waitForWatchService().poll(), nullValue() );
    }

    @Test( timeout = 90000 )
    public void testNotResetWatchKeyDoesNotQue() throws Exception {
        assumeThat(capabilities.supportsWatchService(), is(true));

        watcherSetup(ENTRY_CREATE );
        createPathWAf();

        WatchKey key = waitForWatchService().take();
        key.pollEvents();

        createPathWBf();

        assertThat(waitForWatchService().poll(), nullValue() );
    }

    @Test( timeout = 90000 )
    public void testResetWatchKeyDoesQue() throws Exception {
        assumeThat(capabilities.supportsWatchService(), is(true));

        watcherSetup(ENTRY_CREATE );
        createPathWAf();

        WatchKey key = waitForWatchService().take();
        key.pollEvents();
        key.reset();

        Path file = createPathWBf();

        assertThat(waitForWatchService().poll(),  WatchKeyMatcher.correctKey( file, ENTRY_CREATE ) );
    }


    // watch several events one dir
    @Test
    public void testWatchSeveralEvents() throws Exception {
        assumeThat(capabilities.supportsWatchService(), is(true));

        Path toBeModified = createPathWAf();
        Path toBeCreated = getPathWB();
        watcherSetup(ENTRY_CREATE, ENTRY_MODIFY);
        Files.write(toBeModified, CONTENT_OTHER);
        Files.write(toBeCreated, CONTENT_OTHER);


        WatchKey key = waitForWatchService().poll();
        assertThat(key, notNullValue());
        List<WatchEvent<?>> watchEvents = key.pollEvents();

        assertThat( watchEvents, has(isEvent(toBeModified, ENTRY_MODIFY), isEvent(toBeCreated, ENTRY_CREATE)));
    }

    // todo ?
//    @Test
//    public void testWatchSeveralEvents2() throws Exception {
//        assumeThat(capabilities.supportsWatchService(), is(true));
//        watcherSetup(ENTRY_CREATE);
//        createPathW().register(watchService, ENTRY_MODIFY);
//    }


    // several events multiple dir

    // change in unwatched dir => no effect



    @Test
    public void testWatchACreateBy2WatchServies() throws Exception {
        assumeThat(capabilities.supportsWatchService(), is(true));

        Path toBeCreated = getPathWA();
        watcherSetup(ENTRY_CREATE);
        WatchService watchService2 = FS.newWatchService();
        createPathW().register(watchService2, ENTRY_CREATE);

        Files.write(toBeCreated, CONTENT );

        assertThat( waitForWatchService().poll(), WatchKeyMatcher.correctKey( toBeCreated, ENTRY_CREATE ));
        assertThat( watchService2.poll(), WatchKeyMatcher.correctKey( toBeCreated, ENTRY_CREATE ));
    }


    @Test
    public void testWatchACreate() throws Exception {
        assumeThat(capabilities.supportsWatchService(), is(true));

        Path toBeCreated = getPathWA();
        watcherSetup(ENTRY_CREATE);
        Files.write(toBeCreated, CONTENT );

        assertThat( waitForWatchService().poll(), WatchKeyMatcher.correctKey( toBeCreated, ENTRY_CREATE ));
    }

    @Test
    public void testWatchACreateDir() throws Exception {
        assumeThat(capabilities.supportsWatchService(), is(true));

        Path toBeCreated = getPathWA();
        watcherSetup(ENTRY_CREATE);
        Files.createDirectories(toBeCreated);

        assertThat(waitForWatchService().poll(), WatchKeyMatcher.correctKey(toBeCreated, ENTRY_CREATE));
    }


    @Test
    public void testWatchACreateFromCopy() throws Exception {
        assumeThat( capabilities.supportsWatchService(), is(true));

        watcherSetup(ENTRY_CREATE);
        Files.copy( getPathPAf(), getPathWA());

        assertThat( waitForWatchService().poll(), WatchKeyMatcher.correctKey( getPathWA(), ENTRY_CREATE ));
    }

    @Test
    public void testWatchACreateFromCopyFromOtherFS() throws Exception {
        assumeThat(play2, notNullValue());
        assumeThat( capabilities.supportsWatchService(), is(true));


        watcherSetup(ENTRY_CREATE);
        Files.copy( getPathOtherPAf(), getPathWA());

        assertThat( waitForWatchService().poll(), WatchKeyMatcher.correctKey( getPathWA(), ENTRY_CREATE ));
    }

    @Test
    public void testWatchACreateFromMove() throws Exception {
        assumeThat( capabilities.supportsWatchService(), is(true));

        watcherSetup(ENTRY_CREATE);
        Files.move( getPathPAf(), getPathWA());

        assertThat( waitForWatchService().poll(), WatchKeyMatcher.correctKey( getPathWA(), ENTRY_CREATE ));
    }

    @Test
    public void testWatchACreateFromMoveFromOtherFS() throws Exception {
        assumeThat(play2, notNullValue());
        assumeThat( capabilities.supportsWatchService(), is(true));

        watcherSetup(ENTRY_CREATE);
        Files.move( getPathOtherPAf(), getPathWA());

        assertThat( waitForWatchService().poll(), WatchKeyMatcher.correctKey( getPathWA(), ENTRY_CREATE ));
    }

    @Test
    public void testCopyDirReplaceExistingOverwritesFile() throws Exception {
        assumeThat( capabilities.supportsWatchService(), is(true));

        final ConcurrentLinkedDeque<Path> que = new ConcurrentLinkedDeque<>();

        Path tgt = getPathPA();
        Files.write( tgt, CONTENT, standardOpen );
        Path src = getPathPB();
        Files.createDirectories( src );

        new Thread(new Watcher(tgt.getParent(), que, ENTRY_MODIFY )).start();
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
        assumeThat(capabilities.supportsWatchService(), is(true));

        Path file = createPathWAf();
        watcherSetup(ENTRY_MODIFY);
        try (SeekableByteChannel channel = FS.provider().newByteChannel(file, Sets.asSet(WRITE))) {
            channel.truncate(2);
        }

        assertThat(waitForWatchService().poll(), WatchKeyMatcher.correctKey(file, ENTRY_MODIFY));
    }



    @Test
    public void testTakeBlocks() throws Exception {
        assumeThat( capabilities.supportsWatchService(), is(true));

        Path dir = getPathPAd();
        final WatchService watcher = dir.getFileSystem().newWatchService();
        dir.register( watcher, ENTRY_CREATE );

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
        dir.register( watcher, ENTRY_CREATE );

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
        WatchKey key = dir.register( watcher, ENTRY_CREATE );

        watcher.close();
        Thread.sleep(getWatcherSleep());

        assertThat(key.isValid(), is(false));

    }


    @Test
    public void testPollAnEmptyWatchServiceReturnsNull() throws Exception {
        assumeThat( capabilities.supportsWatchService(), is(true));

        Path dir = getPathPAd();
        final WatchService watcher = dir.getFileSystem().newWatchService();
        dir.register( watcher, ENTRY_CREATE );

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
        WatchKey key = dir.register( watcher, ENTRY_CREATE );

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
        WatchKey key = dir.register( watcher, ENTRY_CREATE );

        Files.delete(dir);
        Thread.sleep(getWatcherSleep());

        assertThat(key.isValid(), is(false));
    }

    @Test
    public void testMovedWatchedDirCancelsKeys() throws Exception {
        assumeThat( capabilities.supportsWatchService(), is(true));

        Path dir = getPathPAd();
        final WatchService watcher = dir.getFileSystem().newWatchService();
        WatchKey key = dir.register( watcher, ENTRY_CREATE );

        Files.move( dir, getPathPB());
        Thread.sleep(getWatcherSleep());

        assertThat(key.isValid(), is(false));
    }

    // todo
    @Test
    public void testWatchTwoModifiesOneKey() throws Exception {
        assumeThat(capabilities.supportsWatchService(), is(true));

        Path toBeModified = createPathWAf();
        watcherSetup(ENTRY_MODIFY);
        Files.write(toBeModified, CONTENT_OTHER);
        Files.write(toBeModified, CONTENT );


        WatchKey key = waitForWatchService().poll();
        assertThat(key, notNullValue());
        List<WatchEvent<?>> watchEvents = key.pollEvents();

        assertThat( watchEvents, has(isEvent(toBeModified, ENTRY_MODIFY), isEvent(toBeModified, ENTRY_MODIFY)));
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

    public Path createPathWAf() throws IOException {
        Path ret = getPathWA();
        Files.write(ret, CONTENT, standardOpen );
        return ret;
    }

    public Path createPathWBf() throws IOException {
        Path ret = getPathWB();
        Files.write(ret, CONTENT, standardOpen );
        return ret;
    }

    public Path getPathWAB() throws IOException {
        return getPathWA().resolve(nameStr[1]);
    }

    public Path createPathWAd() throws IOException {
        Path ret = getPathWA();
        Files.createDirectories(ret);
        return ret;
    }

    public Path getPathWA() throws IOException {
        return createPathW().resolve(nameStr[0]);
    }

    public Path getPathWB() throws IOException {
        return createPathW().resolve(nameStr[1]);
    }

    public Path createPathW() throws IOException {
        Path ret = emptyDir().resolve( nameStr[3] );
        Files.createDirectories(ret);
        return ret;
    }

    public WatchService getWatchService() {
        return watchService;
    }

    public WatchService waitForWatchService() throws InterruptedException {
        Thread.sleep(getWatcherSleep());
        return watchService;
    }


    private WatchKey watcherSetup(WatchEvent.Kind<Path> ... kinds ) throws IOException {
        watchService = FS.newWatchService();
        return createPathW().register(watchService, kinds);
    }

}
