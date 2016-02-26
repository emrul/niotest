package de.pfabulist.lindwurm.niotest.tests;

import de.pfabulist.kleinod.collection.Ref;
import de.pfabulist.kleinod.collection.Sets;
import de.pfabulist.lindwurm.niotest.tests.topics.Closable;
import de.pfabulist.lindwurm.niotest.tests.topics.Delete;
import de.pfabulist.lindwurm.niotest.tests.topics.SlowTest;
import de.pfabulist.lindwurm.niotest.tests.topics.Watchable;
import de.pfabulist.lindwurm.niotest.tests.topics.Writable;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.ClosedWatchServiceException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static de.pfabulist.kleinod.nio.PathIKWID.childGetParent;
import static de.pfabulist.kleinod.nio.PathIKWID.namedGetFilename;
import static java.nio.file.StandardOpenOption.WRITE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

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
@SuppressWarnings( { "PMD.TooManyMethods" } ) // todo
public abstract class Tests11Watcher extends Tests10PathWithContent {

    public static final String WATCH_DELAY = "watchDelay";

    private
    @Nullable
    WatchService watchService;

    public Tests11Watcher( FSDescription capa ) {
        super( capa );
    }

    @Test
    @Category( { SlowTest.class, Watchable.class, Writable.class, Delete.class } )
    public void testWatchADelete() throws Exception {
        Path toBeDeleted = watchedFileA();
        watcherSetup( ENTRY_DELETE );
        Files.delete( toBeDeleted );

        Thread.sleep( 4000 ); // todo why does this matter?

        assertThat( correctKey( watchServicePoll(), toBeDeleted, ENTRY_DELETE ) ).isTrue();
    }

    @Test( timeout = 20000 )
    @Category( { SlowTest.class, Watchable.class, Writable.class, Delete.class } )
    public void testWatchADeleteTake() throws Exception {
        Path toBeDeleted = watchedFileA();
        watcherSetup( ENTRY_DELETE );
        Files.delete( toBeDeleted );

        assertThat( correctKey( getWatchService().take(), toBeDeleted, ENTRY_DELETE ) ).isTrue();
    }

    @Test( timeout = 30000 )
    @Category( { SlowTest.class, Watchable.class, Writable.class, Delete.class } )
    public void testWatchADeletePollWithTimeOut() throws Exception {
        Path toBeDeleted = watchedFileA();
        watcherSetup( ENTRY_DELETE );
        Files.delete( toBeDeleted );

        assertThat( correctKey( waitForWatchService().poll( 1000, TimeUnit.MILLISECONDS ), toBeDeleted, ENTRY_DELETE ) ).isTrue();
    }

    @Test( timeout = 30000 )
    @Category( { SlowTest.class, Watchable.class, Writable.class } )
    public void testWatchPollWithTimeoutTimesOut() throws Exception {
        watcherSetup( ENTRY_DELETE );
        // no events
        getWatchService().poll( 1000, TimeUnit.MILLISECONDS );
        assertThat( "did we reach that?" ).isNotNull();
    }

    @Test( expected = ClosedWatchServiceException.class )
    @Category( { Watchable.class, Writable.class } )
    public void testRegisterOnClosedWatchService() throws IOException {
        WatchService watcher = FS.newWatchService();
        watcher.close();
        dirTAB().register( watcher, ENTRY_CREATE );
    }

    @Test( expected = ClosedWatchServiceException.class )
    @Category( { Watchable.class, Writable.class, Closable.class } )
    public void testRegisterWatchServiceOfClosedFS() throws Exception {
        getClosedDirB().register( getClosedFSWatchService(), ENTRY_DELETE );
    }

    @Test
    @Category( { SlowTest.class, Watchable.class, Writable.class } )
    public void testWatchADeleteFromAMove() throws Exception {
        Path toBeMoved = watchedFileA();
        watcherSetup( ENTRY_DELETE );
        Files.move( toBeMoved, absTA() );
        assertThat( correctKey( watchServicePoll(), toBeMoved, ENTRY_DELETE ) ).isTrue();
    }

    @Test
    @Category( { SlowTest.class, Watchable.class, Writable.class } )
    public void testWatchAModify() throws Exception {
        Path toBeModified = watchedFileA();
        watcherSetup( ENTRY_MODIFY );
        Files.write( toBeModified, CONTENT_OTHER );

        assertThat( correctKey( watchServicePoll(), toBeModified, ENTRY_MODIFY ) ).isTrue();
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
////        assertThat( waitForWatchService().poll()).isNull();
////    }
//
    // todo how long to wait?
    @Test
    @Category( { SlowTest.class, Watchable.class, Writable.class } )
    public void testWatchReadIsNotModify() throws Exception {
        Path toBeModified = watchedFileA();
        watcherSetup( ENTRY_MODIFY );
        Files.readAllBytes( toBeModified );

        assertThat( watchServicePoll() ).isNull();
    }

    @Test
    @Category( { SlowTest.class, Watchable.class, Writable.class } )
    public void testWatchForOtherEventCatchesNothing() throws Exception {
        Path toBeModified = watchedFileA();
        watcherSetup( ENTRY_CREATE );
        Files.write( toBeModified, CONTENT_OTHER );

        assertThat( watchServicePoll() ).isNull();
    }

    @Test
    @Category( { SlowTest.class, Watchable.class } )
    public void testWatchInOtherDirCatchesNothing() throws Exception {
        watcherSetup( ENTRY_CREATE );
        fileTAB();
        assertThat( watchServicePoll() ).isNull();
    }

    @Test( timeout = 30000 )
    @Category( { SlowTest.class, Watchable.class, Writable.class } )
    public void testNotResetWatchKeyDoesNotQue() throws Exception {
        watcherSetup( ENTRY_CREATE );
        watchedFileA();
        WatchKey key = waitForWatchService().take();
        key.pollEvents();

        Files.write( watchedAbsB(), CONTENT );

        assertThat( watchServicePoll() ).isNull();
    }

    @Test( timeout = 30000 )
    @Category( { SlowTest.class, Watchable.class, Writable.class } )
    public void testResetWatchKeyDoesQue() throws Exception {
        watcherSetup( ENTRY_CREATE );
        watchedFileA();

        WatchKey key = waitForWatchService().take();
        key.pollEvents();
        key.reset();

        Path file = Files.write( watchedAbsB(), CONTENT );

        assertThat( correctKey( getWatchService().take(), file, ENTRY_CREATE ) ).isTrue();
    }

    // todo assertj
//    @Test
//    @Category( { SlowTest.class, Watchable.class, Writable.class } )
//    public void testWatchSeveralEventsInOneDir() throws Exception {
//        Path toBeModified = watchedFileA();
//        Path toBeCreated = watchedAbsB();
//        watcherSetup( ENTRY_CREATE, ENTRY_MODIFY );
//        Files.write( toBeModified, CONTENT_OTHER );
//        Files.write( toBeCreated, CONTENT_OTHER );
//
//        WatchKey key = watchServicePoll();
//        assertThat( key ).isNotNull();
//        List<WatchEvent<?>> watchEvents = key.pollEvents();
//
//        assertThat( watchEvents ).contains(  ) hasItems( isEvent( toBeModified, ENTRY_MODIFY ), isEvent( toBeCreated, ENTRY_CREATE ) ) );
//    }

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
    @Category( { SlowTest.class, Watchable.class, Writable.class } )
    public void testWatchACreateBy2WatchServies() throws Exception {

        Path toBeCreated = watchedAbsA();
        watcherSetup( ENTRY_CREATE );
        WatchService watchService2 = FS.newWatchService();
        watchedDir().register( watchService2, ENTRY_CREATE );

        Files.write( toBeCreated, CONTENT );

        assertThat( correctKey( getWatchService().take(), toBeCreated, ENTRY_CREATE ) ).isTrue();
        assertThat( correctKey( watchService2.take(), toBeCreated, ENTRY_CREATE ) ).isTrue();
    }

    @Test
    @Category( { SlowTest.class, Watchable.class, Writable.class } )
    public void testWatchACreate() throws Exception {
        Path toBeCreated = watchedAbsA();
        watcherSetup( ENTRY_CREATE );
        Files.write( toBeCreated, CONTENT );

        assertThat( correctKey( watchServicePoll(), toBeCreated, ENTRY_CREATE ) ).isTrue();
    }

    @Test
    @Category( { SlowTest.class, Watchable.class, Writable.class, Writable.class } )
    public void testWatchACreateDir() throws Exception {
        Path toBeCreated = watchedAbsA();
        watcherSetup( ENTRY_CREATE );
        Files.createDirectory( toBeCreated );
        assertThat( correctKey( watchServicePoll(), toBeCreated, ENTRY_CREATE ) ).isTrue();
    }

    @Test
    @Category( { SlowTest.class, Watchable.class, Writable.class, Writable.class } )
    public void testWatchACreateFromCopy() throws Exception {
        watcherSetup( ENTRY_CREATE );
        Files.copy( fileTB(), watchedAbsA() );

        assertThat( correctKey( watchServicePoll(), watchedAbsA(), ENTRY_CREATE ) ).isTrue();
    }

    @Test
    @Category( { SlowTest.class, Watchable.class, Writable.class, Writable.class } )
    public void testWatchACreateFromMove() throws Exception {
        watcherSetup( ENTRY_CREATE );
        Files.move( fileTAB(), watchedAbsA() );
        assertThat( correctKey( watchServicePoll(), watchedAbsA(), ENTRY_CREATE ) ).isTrue();
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
    @Category( { SlowTest.class, Watchable.class, Writable.class } )
    public void testCanceledWatchKeyDoesNotWatch() throws Exception {
        WatchKey key = watcherSetup( ENTRY_CREATE );
        key.cancel();
        watchedFileA();

        assertThat( watchServicePoll() ).isNull();
    }

    @Test
    @Category( { SlowTest.class, Watchable.class, Writable.class } )
    public void testWatchATruncate() throws Exception {
        Path file = watchedFileA();
        watcherSetup( ENTRY_MODIFY );
        try( SeekableByteChannel channel = FS.provider().newByteChannel( file, Sets.asSet( WRITE ) ) ) {
            channel.truncate( 2 );
        }

        assertThat( correctKey( watchServicePoll(), file, ENTRY_MODIFY ) ).isTrue();
    }

    @Test
    @Category( { SlowTest.class, Watchable.class, Writable.class } )
    @SuppressWarnings( "PMD.EmptyCatchBlock" )
    public void testWatchServiceTakeBlocks() throws Exception {
        Path dir = dirTA();
        final WatchService watcher = dir.getFileSystem().newWatchService();
        dir.register( watcher, ENTRY_CREATE );

        final Ref<Boolean> interrupted = Ref.valueOf( false );

        new Thread( () -> {
            try {
                watcher.take();
            } catch( InterruptedException | ClosedWatchServiceException e ) {
                // nothing to do
            } finally {
                interrupted.set( true );

            }
        } ).start();

        Thread.sleep( 1000 );

        assertThat( interrupted.get() ).isFalse();

    }

    @Test
    @Category( { SlowTest.class, Watchable.class, Writable.class } )
    public void testCloseAWatchServiceReleasesBlockedTreads() throws Exception {
        Path dir = dirTB();
        final WatchService watcher = dir.getFileSystem().newWatchService();
        dir.register( watcher, ENTRY_CREATE );

        final Ref<Boolean> interrupted = Ref.valueOf( false );

        new Thread( () -> {
            try {
                watcher.take();
            } catch( InterruptedException e ) {
            } catch( ClosedWatchServiceException e ) {
                interrupted.set( true );
            }
        } ).start();

        Thread.sleep( 1000 );
        watcher.close();
        Thread.sleep( 100 );

        assertThat( interrupted.get() ).isTrue();

    }

    @Test
    @Category( { SlowTest.class, Watchable.class, Writable.class } )
    public void testCloseAWatchServiceCancelsKeys() throws Exception {
        Path dir = dirTA();
        final WatchService watcher = dir.getFileSystem().newWatchService();
        WatchKey key = dir.register( watcher, ENTRY_CREATE );

        watcher.close();
        waitForWatchService();

        assertThat( key.isValid() ).isFalse();

    }

    @Test
    @Category( { SlowTest.class, Watchable.class, Writable.class } )
    public void testPollAnEmptyWatchServiceReturnsNull() throws Exception {
        Path dir = dirTA();
        final WatchService watcher = dir.getFileSystem().newWatchService();
        dir.register( watcher, ENTRY_CREATE );

        assertThat( watcher.poll() ).isNull();
    }

    @Test
    @Category( { SlowTest.class, Watchable.class, Writable.class } )
    public void testWatchKeyPollEventsEmptiesQue() throws Exception {
        Path dir = dirTA();
        Path toBeDeleted = dirTAB();
        final WatchService watcher = dir.getFileSystem().newWatchService();
        dir.register( watcher, ENTRY_DELETE );

        Thread.sleep( 1000 );
        Files.delete( toBeDeleted );

        waitForWatchService();

        WatchKey watchKey = watcher.poll();
        watchKey.pollEvents();
        assertThat( watchKey.pollEvents() ).isEmpty();
    }

    @Test
    @Category( { SlowTest.class, Watchable.class, Writable.class } )
    public void testDeleteWatchedDirCancelsKeys() throws Exception {
        WatchKey key = watcherSetup( ENTRY_CREATE );
        Files.delete( watchedDir() );
        watchServicePoll();

        assertThat( key.isValid() ).isFalse();
    }

    @Test
    @Category( { SlowTest.class, Watchable.class, Writable.class } )
    public void testMovedWatchedDirCancelsKeys() throws Exception {
        Path dir = dirTA();
        final WatchService watcher = FS.newWatchService();
        WatchKey key = dir.register( watcher, ENTRY_CREATE );

        Files.move( dir, absTB() );
        waitForWatchService();

        assertThat( key.isValid() ).isFalse();
    }

    // todo assertj
//    @Test
//    @Category( { SlowTest.class, Watchable.class, Writable.class } )
//    public void testWatchTwoModifiesOneKey() throws Exception {
//        Path toBeModified = watchedFileA();
//        watcherSetup( ENTRY_MODIFY );
//        Files.write( toBeModified, CONTENT_OTHER );
//        Files.write( toBeModified, CONTENT );
//
//        WatchKey key = watchServicePoll();
//        assertThat( key ).isNotNull();
//        List<WatchEvent<?>> watchEvents = key.pollEvents();
//
//        // spec says one may be swallowed
//        assertThat( watchEvents, hasItems( isEvent( toBeModified, ENTRY_MODIFY ) ) ); //, isEvent(toBeModified, ENTRY_MODIFY)));
//    }
//
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
        Files.write( ret, CONTENT, standardOpen );
        return ret;
    }

    public Path watchedAbsA() throws IOException {
        return watchedDir().resolve( nameA() );
    }

    public Path watchedAbsB() throws IOException {
        return watchedDir().resolve( nameB() );
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
        Path ret = absT().resolve( "watched" );
        Files.createDirectories( ret );
        return ret;
    }

    public WatchService getWatchService() {
        return watchService;
    }

    public WatchService waitForWatchService() throws InterruptedException {
        Thread.sleep( getDelay() );
        return watchService;
    }

    private int getDelay() {
        int delay = description.getInt( WATCH_DELAY );
        if( delay < 10 ) {
            return 2000;
        }

        return delay;
    }

    public WatchKey watchServicePoll() throws InterruptedException {
        return watchService.poll( getDelay(), TimeUnit.MILLISECONDS );
    }

    WatchKey watcherSetup( WatchEvent.Kind<Path>... kinds ) throws IOException {
        watchService = FS.newWatchService();
        return watchedDir().register( watchService, kinds );
    }

    public static boolean correctKey( WatchKey key, Path file, WatchEvent.Kind<Path> kind ) {
        if( key == null ) {
            return false;
        }
        if( !key.isValid() ) {
            return false;
        }

        if( !childGetParent( file ).equals( key.watchable() ) ) {
            return false;
        }

        List<WatchEvent<?>> events = key.pollEvents();

        return events.stream().filter( ev -> {
                                        if( !namedGetFilename( file ).equals( ev.context() ) ) {
                                            return false;
                                        }

                                        return ev.kind().equals( kind );
                                    } ).
                findAny().isPresent();
    }

}
