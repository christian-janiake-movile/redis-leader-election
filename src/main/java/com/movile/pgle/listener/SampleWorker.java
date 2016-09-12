package com.movile.pgle.listener;

import com.movile.pgle.PeerGroup;

public class SampleWorker extends DaemonWorker {

    public SampleWorker(PeerGroup peerGroup) {
        super(peerGroup);
    }

    @Override
    public void work() {
        System.out.println("working");
    }
}
