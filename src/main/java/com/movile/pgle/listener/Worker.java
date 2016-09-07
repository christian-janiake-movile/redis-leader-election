package com.movile.pgle.listener;

import com.movile.pgle.PeerGroup;

public interface Worker {
    public void work() throws InterruptedException;
    public void stop();
}
