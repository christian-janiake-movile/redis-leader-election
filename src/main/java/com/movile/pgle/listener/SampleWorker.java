package com.movile.pgle.listener;

import com.movile.pgle.PeerGroup;

import java.util.concurrent.TimeUnit;

public class SampleWorker implements Worker, Runnable {

    private PeerGroup peerGroup;
    private boolean onDuty = false;

    public SampleWorker(PeerGroup peerGroup) {
        this.peerGroup = peerGroup;
    }

    @Override
    public void work() throws InterruptedException {
        onDuty = true;
    }

    @Override
    public void run() {
        while(true) {
            if(onDuty) {
                System.out.println(peerGroup.getName() + " is working");
            }
            try {
                Thread.sleep(TimeUnit.SECONDS.toMillis(2));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
