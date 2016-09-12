package com.movile.pgle.listener;

import com.movile.pgle.PeerGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

public abstract class DaemonWorker implements Runnable, Worker {

    protected PeerGroup peerGroup;
    private boolean alive = false;

    public DaemonWorker(PeerGroup peerGroup) {
        this.peerGroup = peerGroup;
    }

    public abstract void work();

    private Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    public void run() {
        while(true) {
            Instant start = Instant.now();
            if(alive) {
                log.debug("{} is working", peerGroup.getName());
                work();
                log.debug("{} took {}ms to run", peerGroup.getName(), Duration.between(start, Instant.now()).toMillis());
            }
            handleBusyWaiting(start);
        }
    }

    private void handleBusyWaiting(Instant start) {
        long elapsed = Duration.between(start, Instant.now()).toMillis();
        long minimumDuration = TimeUnit.SECONDS.toMillis(1);
        if(elapsed < minimumDuration)
            try {
                Thread.sleep(minimumDuration - elapsed);
            } catch (InterruptedException e) {
                log.debug(e.toString(), e);
            }
    }

    public void startWorking() {
        alive = true;
    }

    public void stop() {
        alive = false;
    }
}
