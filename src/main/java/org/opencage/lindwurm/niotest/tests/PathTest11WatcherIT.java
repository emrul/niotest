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
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assume.assumeThat;
import static org.opencage.lindwurm.niotest.matcher.PathExists.exists;

/**
 * Created with IntelliJ IDEA.
 * User: stephan
 * Date: 19/02/14
 * Time: 14:46
 * To change this template use File | Settings | File Templates.
 */
public abstract class PathTest11WatcherIT extends PathTest10PathWithContentIT {


    private long watcherSleep = 1000;

    @Test(expected = ProviderMismatchException.class)
    public void testRegisterOtherPath() throws Exception {
        assumeThat( capabilities.supportsWatchService(), is(true));

        WatchService ws = getOther().getFileSystem().newWatchService();

        getPathPABf().register(ws, StandardWatchEventKinds.ENTRY_DELETE);
    }

    @Test
    public void testWatchADelete() throws Exception {
        assumeThat( capabilities.supportsWatchService(), is(true));

        final ConcurrentLinkedDeque<Path> dels = new ConcurrentLinkedDeque<>();

        Path dir = getPathPA();
        Path toBeDeleted = getPathPABf();

        new Thread(new Watcher(dir, dels, StandardWatchEventKinds.ENTRY_DELETE)).start();

        Thread.sleep(1000);
        Files.delete(toBeDeleted);

        Thread.sleep(getWatcherSleep());

        assertThat(dels.size(), is(1));
    }

    @Test
    public void testWatchADeleteFromMove() throws Exception {
        assumeThat( capabilities.supportsWatchService(), is(true));

        final ConcurrentLinkedDeque<Path> dels = new ConcurrentLinkedDeque<>();

        Path dir = getPathPA();
        Path toBeDeleted = getPathPABf();

        new Thread(new Watcher(dir, dels, StandardWatchEventKinds.ENTRY_DELETE)).start();
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

    @Test( expected = ClosedWatchServiceException.class )
    public void testRegisterOnClosedWatchService() throws IOException {
        assumeThat( capabilities.supportsWatchService(), is(true));

        Path dir = getPathPAd();

        WatchService watcher = dir.getFileSystem().newWatchService();
        watcher.close();

        dir.register( watcher, ENTRY_CREATE );
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

                //System.out.println("watcher is running. watching " + dir);

                WatchKey watckKey = watcher.take();

                //System.out.println("watcher is running2");


                List<WatchEvent<?>> events = watckKey.pollEvents();

                // System.out.println("watcher is running3");

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

    private static Path watchablePlay;

    public static void setWatchable(Path observablePlay) {
        watchablePlay = observablePlay;
    }

    public long getWatcherSleep() {
        return watcherSleep;
    }

    public void setWatcherSleep(long watcherSleep) {
        this.watcherSleep = watcherSleep;
    }
}
