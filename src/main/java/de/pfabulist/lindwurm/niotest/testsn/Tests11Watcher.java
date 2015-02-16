package de.pfabulist.lindwurm.niotest.testsn;

import de.pfabulist.kleinod.collection.Ref;
import de.pfabulist.kleinod.collection.Sets;
import de.pfabulist.lindwurm.niotest.testsn.setup.AllCapabilitiesBuilder;
import de.pfabulist.lindwurm.niotest.testsn.setup.Capa;
import de.pfabulist.lindwurm.niotest.testsn.setup.DetailBuilder;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.IOException;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.*;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static de.pfabulist.lindwurm.niotest.matcher.WatchEventMatcher.isEvent;
import static de.pfabulist.lindwurm.niotest.matcher.WatchKeyMatcher.correctKey;
import static java.nio.file.StandardOpenOption.WRITE;
import static java.nio.file.StandardWatchEventKinds.*;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsCollectionContaining.hasItems;
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
public abstract class Tests11Watcher extends Tests10PathWithContent {

    public static final String WATCH_DELAY = "watchDelay";

    public Tests11Watcher( Capa capa) {
        super(capa);
    }

    public static class CapaBuilder11 extends CapaBuilder10 {

        public static class WatchBuilder extends DetailBuilder {
            public WatchBuilder(AllCapabilitiesBuilder builder) {
                super(builder);
                capa.attributes.put(WATCH_DELAY, 2000 );
            }
            
            public WatchBuilder delay( int millis ) {
                capa.attributes.put(WATCH_DELAY, millis );
                return this;
            }

            @Override
            public AllCapabilitiesBuilder onOff(boolean val) {
                capa.addFeature( "Watch", val );
                return builder;
            }
        }
        
        public WatchBuilder watchService() {
            return new WatchBuilder((AllCapabilitiesBuilder) this);
        }
    }
    
    
//    @Test(expected = ProviderMismatchException.class)
//    public void testRegisterOtherPath() throws Exception {
//        assumeThat( capabilities.supportsWatchService(), is(true));
//
//        WatchService ws = otherProviderAbsA().getFileSystem().newWatchService();
//
//        getPathPABf().register(ws, ENTRY_DELETE);
//    }
//
//
    @Test
    @Category( SlowTest.class )
    public void testWatchADelete() throws Exception {
        Path toBeDeleted = watchedFileA();
        watcherSetup(ENTRY_DELETE);
        Files.delete( toBeDeleted );
        
        Thread.sleep(4000); // todo why does this matter?

        assertThat( watchServicePoll(), correctKey(toBeDeleted, ENTRY_DELETE));
    }

    @Test( timeout = 20000 )
    @Category( SlowTest.class )
    public void testWatchADeleteTake() throws Exception {
        Path toBeDeleted = watchedFileA();
        watcherSetup(ENTRY_DELETE);
        Files.delete(toBeDeleted);

        assertThat( getWatchService().take(), correctKey(toBeDeleted, ENTRY_DELETE));
    }

    @Test( timeout = 30000 )
    @Category( SlowTest.class )
    public void testWatchADeletePollWithTimeOut() throws Exception {
        Path toBeDeleted = watchedFileA();
        watcherSetup(ENTRY_DELETE);
        Files.delete(toBeDeleted);

        assertThat( waitForWatchService().poll( 1000, TimeUnit.MILLISECONDS), correctKey(toBeDeleted, ENTRY_DELETE));
    }

    @Test( timeout = 30000 )
    @Category( SlowTest.class )
    public void testWatchPollWithTimeoutTimesOut() throws Exception {
        watcherSetup(ENTRY_DELETE);
        // no events

        getWatchService().poll( 1000, TimeUnit.MILLISECONDS);
    }


    @Test( expected = ClosedWatchServiceException.class )
    public void testRegisterOnClosedWatchService() throws IOException {
        WatchService watcher = FS.newWatchService();
        watcher.close();
        dirTAB().register(watcher, ENTRY_CREATE);
    }


    @Test( expected = ClosedWatchServiceException.class )
    public void testRegisterWatchServiceOfClosedFS() throws Exception {
        getClosedDirB().register( getClosedFSWatchService(), ENTRY_DELETE);
    }

    @Test
    @Category( SlowTest.class )
    public void testWatchADeleteFromAMove() throws Exception {
        Path toBeMoved = watchedFileA();
        watcherSetup(ENTRY_DELETE);
        Files.move( toBeMoved, absTA());
        assertThat( watchServicePoll(), correctKey( toBeMoved, ENTRY_DELETE));
    }

    @Test
    @Category( SlowTest.class )
    public void testWatchAModify() throws Exception {
        Path toBeModified = watchedFileA();
        watcherSetup( ENTRY_MODIFY );
        Files.write(toBeModified, CONTENT_OTHER);

        assertThat( watchServicePoll(), correctKey(toBeModified, ENTRY_MODIFY));
    }
//
//    // todo yes or no ?
////    @Test
////    public void testKidsChangesOfADirIsNotAModify() throws Exception {
////        assumeThat(capabilities.supportsWatchService(), is(true));
////
////        Path toBeCreated = watchedFileAB();
////        Path dir = createPathWAd();
////        watcherSetup(ENTRY_MODIFY);
////        Files.write(toBeCreated, CONTENT);
////
////        assertThat( waitForWatchService().poll(), nullValue() );
////    }
//
    // todo how long to wait?
    @Test
    @Category( SlowTest.class )
    public void testWatchReadIsNotModify() throws Exception {
        Path toBeModified = watchedFileA();
        watcherSetup(ENTRY_MODIFY);
        Files.readAllBytes(toBeModified );

        assertThat( watchServicePoll(), nullValue() );
    }

    @Test
    @Category( SlowTest.class )
    public void testWatchForOtherEventCatchesNothing() throws Exception {
        Path toBeModified = watchedFileA();
        watcherSetup(ENTRY_CREATE );
        Files.write(toBeModified, CONTENT_OTHER);

        assertThat( watchServicePoll(), nullValue() );
    }

    @Test
    @Category( SlowTest.class )
    public void testWatchInOtherDirCatchesNothing() throws Exception {
        watcherSetup(ENTRY_CREATE );
        fileTAB();
        assertThat( watchServicePoll(), nullValue() );
    }

    @Test( timeout = 30000 )
    @Category( SlowTest.class )
    public void testNotResetWatchKeyDoesNotQue() throws Exception {
        watcherSetup(ENTRY_CREATE);
        watchedFileA();
        WatchKey key = waitForWatchService().take();
        key.pollEvents();

        Files.write(watchedAbsB(), CONTENT);

        assertThat( watchServicePoll(), nullValue() );
    }

    @Test( timeout = 30000 )
    @Category( SlowTest.class )
    public void testResetWatchKeyDoesQue() throws Exception {
        watcherSetup(ENTRY_CREATE );
        watchedFileA();

        WatchKey key = waitForWatchService().take();
        key.pollEvents();
        key.reset();

        Path file = Files.write(watchedAbsB(), CONTENT );

        assertThat( getWatchService().take(), correctKey(file, ENTRY_CREATE));
    }

    @Test
    @Category( SlowTest.class )
    public void testWatchSeveralEventsInOneDir() throws Exception {
        Path toBeModified = watchedFileA();
        Path toBeCreated = watchedAbsB();
        watcherSetup(ENTRY_CREATE, ENTRY_MODIFY);
        Files.write(toBeModified, CONTENT_OTHER);
        Files.write(toBeCreated, CONTENT_OTHER);


        WatchKey key = watchServicePoll();
        assertThat(key, notNullValue());
        List<WatchEvent<?>> watchEvents = key.pollEvents();

        assertThat( watchEvents, hasItems( isEvent(toBeModified, ENTRY_MODIFY), isEvent(toBeCreated, ENTRY_CREATE)));
    }

//    @Test
//    public void testWatchSeveralEventsMultipleDirs() throws Exception {
//        Path toBeCreated = watchedFileB();
//        Path otherDir = createPathWAd();
//        Path toBeCreated2 = watchedFileAB();
//        watcherSetup(ENTRY_CREATE);
//        otherDir.register(watchService, ENTRY_CREATE);
//
//        Files.write(toBeCreated, CONTENT_OTHER);
//        Thread.sleep(2000);
//        Files.write(toBeCreated2, CONTENT_OTHER);
//
//        assertThat( waitForWatchService().poll(), anyOf(WatchKeyMatcher.correctKey(toBeCreated, ENTRY_CREATE), WatchKeyMatcher.correctKey(toBeCreated2, ENTRY_CREATE)));
//        assertThat( getWatchService().poll(), anyOf(WatchKeyMatcher.correctKey(toBeCreated, ENTRY_CREATE), WatchKeyMatcher.correctKey(toBeCreated2, ENTRY_CREATE)));
//    }


    @Test( timeout = 20000 )
    @Category( SlowTest.class )
    public void testWatchACreateBy2WatchServies() throws Exception {

        Path toBeCreated = watchedAbsA();
        watcherSetup(ENTRY_CREATE);
        WatchService watchService2 = FS.newWatchService();
        watchedDir().register(watchService2, ENTRY_CREATE);

        Files.write(toBeCreated, CONTENT );

        assertThat(getWatchService().take(), correctKey(toBeCreated, ENTRY_CREATE));
        assertThat(watchService2.take(), correctKey(toBeCreated, ENTRY_CREATE));
    }

    @Test
    @Category( SlowTest.class )
    public void testWatchACreate() throws Exception {
        Path toBeCreated = watchedAbsA();
        watcherSetup(ENTRY_CREATE);
        Files.write(toBeCreated, CONTENT );

        assertThat( watchServicePoll(), correctKey(toBeCreated, ENTRY_CREATE));
    }

    @Test
    @Category( SlowTest.class )
    public void testWatchACreateDir() throws Exception {
        Path toBeCreated = watchedAbsA();
        watcherSetup(ENTRY_CREATE);
        Files.createDirectory(toBeCreated);
        assertThat( watchServicePoll(), correctKey(toBeCreated, ENTRY_CREATE));
    }

    @Test
    @Category( SlowTest.class )
    public void testWatchACreateFromCopy() throws Exception {
        watcherSetup(ENTRY_CREATE);
        Files.copy( fileTB(), watchedAbsA());

        assertThat( watchServicePoll(), correctKey( watchedAbsA(), ENTRY_CREATE));
    }


    @Test
    @Category( SlowTest.class )
    public void testWatchACreateFromMove() throws Exception {
        watcherSetup( ENTRY_CREATE );
        Files.move( fileTAB(), watchedAbsA());
        assertThat( watchServicePoll(), correctKey( watchedAbsA(), ENTRY_CREATE));
    }


////    @Test
////    public void testCopyDirReplaceExistingOverwritesFile() throws Exception {
////        assumeThat( capabilities.supportsWatchService(), is(true));
////
////        final ConcurrentLinkedDeque<Path> que = new ConcurrentLinkedDeque<>();
////
////        Path tgt = getPathPA();
////        Files.write( tgt, CONTENT, standardOpen );
////        Path srcFile = getPathPB();
////        Files.createDirectories( srcFile );
////
////        new Thread(new Watcher(tgt.getParent(), que, ENTRY_MODIFY )).start();
////        Thread.sleep(2000);
////
////        Files.copy( srcFile, tgt, StandardCopyOption.REPLACE_EXISTING );
////
////        Thread.sleep(getWatcherSleep());
////        assertThat(que.size(), is(1));
////
////    }
//
    @Test
    @Category( SlowTest.class )
    public void testCanceledWatchKeyDoesNotWatch() throws Exception {
        WatchKey key = watcherSetup( ENTRY_CREATE );
        key.cancel();
        watchedFileA();

        assertThat( watchServicePoll(), nullValue() );
    }


    @Test
    @Category( SlowTest.class )
    public void testWatchATruncate() throws Exception {
        Path file = watchedFileA();
        watcherSetup(ENTRY_MODIFY);
        try (SeekableByteChannel channel = FS.provider().newByteChannel(file, Sets.asSet( WRITE))) {
            channel.truncate(2);
        }

        assertThat( watchServicePoll(), correctKey(file, ENTRY_MODIFY));
    }

    @Test
    @Category( SlowTest.class )
    public void testWatchServiceTakeBlocks() throws Exception {
        Path dir = dirTA();
        final WatchService watcher = dir.getFileSystem().newWatchService();
        dir.register( watcher, ENTRY_CREATE );

        final Ref<Boolean> interrupted = Ref.valueOf(false);

        new Thread(() -> {
            try {
                watcher.take();
            } catch (InterruptedException | ClosedWatchServiceException e) {
            } finally {
                interrupted.set(true);

            }
        }).start();

        Thread.sleep(1000);

        assertThat(interrupted.get(), is(false));

    }

    @Test
    @Category( SlowTest.class )
    public void testCloseAWatchServiceReleasesBlockedTreads() throws Exception {
        Path dir = dirTB();
        final WatchService watcher = dir.getFileSystem().newWatchService();
        dir.register( watcher, ENTRY_CREATE );

        final Ref<Boolean> interrupted = Ref.valueOf(false);

        new Thread(() -> {
            try {
                watcher.take();
            } catch (InterruptedException e) {
            } catch ( ClosedWatchServiceException e ) {
                interrupted.set(true);
            }
        }).start();

        Thread.sleep(1000);
        watcher.close();
        Thread.sleep(100);

        assertThat(interrupted.get(), is(true));

    }

    @Test
    @Category( SlowTest.class )
    public void testCloseAWatchServiceCancelsKeys() throws Exception {
        Path dir = dirTA();
        final WatchService watcher = dir.getFileSystem().newWatchService();
        WatchKey key = dir.register( watcher, ENTRY_CREATE );

        watcher.close();
        waitForWatchService();

        assertThat(key.isValid(), is(false));

    }

    @Test
    @Category( SlowTest.class )
    public void testPollAnEmptyWatchServiceReturnsNull() throws Exception {
        Path dir = dirTA();
        final WatchService watcher = dir.getFileSystem().newWatchService();
        dir.register( watcher, ENTRY_CREATE );

        assertThat(watcher.poll(), nullValue());
    }

    @Test
    @Category( SlowTest.class )
    public void testWatchKeyPollEventsEmptiesQue() throws Exception {
        Path dir = dirTA();
        Path toBeDeleted = dirTAB();
        final WatchService watcher = dir.getFileSystem().newWatchService();
        dir.register( watcher, ENTRY_DELETE );

        Thread.sleep(1000);
        Files.delete(toBeDeleted);

        waitForWatchService();

        WatchKey watchKey = watcher.poll();
        watchKey.pollEvents();
        assertThat( watchKey.pollEvents(), empty());
    }

    @Test
    @Category( SlowTest.class )
    public void testDeleteWatchedDirCancelsKeys() throws Exception {
        WatchKey key = watcherSetup( ENTRY_CREATE );
        Files.delete(watchedDir());
        watchServicePoll();

        assertThat(key.isValid(), is(false));
    }

    @Test
    @Category( SlowTest.class )
    public void testMovedWatchedDirCancelsKeys() throws Exception {
        Path dir = dirTA();
        final WatchService watcher = FS.newWatchService();
        WatchKey key = dir.register( watcher, ENTRY_CREATE );

        Files.move( dir, absTB());
        waitForWatchService();

        assertThat( key.isValid(), is(false));
    }

    @Test
    @Category( SlowTest.class )
    public void testWatchTwoModifiesOneKey() throws Exception {
        Path toBeModified = watchedFileA();
        watcherSetup(ENTRY_MODIFY);
        Files.write(toBeModified, CONTENT_OTHER);
        Files.write(toBeModified, CONTENT );


        WatchKey key = watchServicePoll();
        assertThat(key, notNullValue());
        List<WatchEvent<?>> watchEvents = key.pollEvents();

        // spec says one may be swallowed
        assertThat( watchEvents, hasItems( isEvent(toBeModified, ENTRY_MODIFY))); //, isEvent(toBeModified, ENTRY_MODIFY)));
    }

//
//    /*
//     * ------------------------------------------------------------------------------------
//     */
//
//    long watcherSleep = 1000;
//
//    public long getWatcherSleep() {
//        return watcherSleep;
//    }
//
////    public void setWatcherSleep(long watcherSleep) {
////        this.watcherSleep = watcherSleep;
////    }
//
    public Path watchedFileA() throws IOException {
        Path ret = watchedAbsA();
        Files.write(ret, CONTENT, standardOpen );
        return ret;
    }
    public Path watchedAbsA() throws IOException {
        return watchedDir().resolve( nameA());
    }

    public Path watchedAbsB() throws IOException {
        return watchedDir().resolve( nameB());
    }

//    public Path watchedFileB() throws IOException {
//        Path ret = watchedDir().resolve( nameB());
//        Files.write(ret, CONTENT, standardOpen );
//        return ret;
//    }

//    public Path createPathWBf() throws IOException {
//        Path ret = watchedFileB();
//        Files.write(ret, CONTENT, standardOpen );
//        return ret;
//    }
//
//    public Path watchedFileAB() throws IOException {
//        return watchedFileA().resolve(nameStr[1]);
//    }
//
//    public Path createPathWAd() throws IOException {
//        Path ret = watchedFileA();
//        Files.createDirectories(ret);
//        return ret;
//    }
//
//    public Path watchedFileA() throws IOException {
//        return watchedDir().resolve(nameStr[0]);
//    }
//
//    public Path watchedFileB() throws IOException {
//        return watchedDir().resolve(nameStr[1]);
//    }
//
    public Path watchedDir() throws IOException {
        Path ret = absT().resolve("watched");
        Files.createDirectories(ret);
        return ret;
    }

    public WatchService getWatchService() {
        return watchService;
    }

    public WatchService waitForWatchService() throws InterruptedException {
        Thread.sleep( capa.getInt( WATCH_DELAY ));
        return watchService;
    }

    public WatchKey watchServicePoll() throws InterruptedException {
        return watchService.poll( capa.getInt( WATCH_DELAY ), TimeUnit.MILLISECONDS );
    }

    WatchKey watcherSetup(WatchEvent.Kind<Path> ... kinds ) throws IOException {
        watchService = FS.newWatchService();
        return watchedDir().register(watchService, kinds);
    }

    private WatchService watchService;


}
