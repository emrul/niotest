package org.opencage.lindwurm.zero;

import java.nio.file.WatchService;


import java.io.IOException;
import java.nio.file.WatchKey;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * User: stephan
 * Date: 21/02/14
 * Time: 20:59
 * To change this template use File | Settings | File Templates.
 */
public class NullWatchService implements WatchService {
    @Override
    public void close() throws IOException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public WatchKey poll() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public WatchKey poll(long timeout, TimeUnit unit) throws InterruptedException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public WatchKey take() throws InterruptedException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}