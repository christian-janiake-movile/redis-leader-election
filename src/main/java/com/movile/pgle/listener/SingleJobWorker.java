package com.movile.pgle.listener;

import com.movile.pgle.PeerGroup;

import java.util.concurrent.TimeUnit;

public abstract class SingleJobWorker implements Runnable, Worker {

    private PeerGroup peerGroup;
    private boolean alive = false;
    private boolean runOnce = false;

    public SingleJobWorker(PeerGroup peerGroup) {
        this.peerGroup = peerGroup;
    }

    public abstract void work();

    public void startWorking() {
        work();
    }

    public void stop() {
    }
}
