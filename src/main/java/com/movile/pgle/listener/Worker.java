package com.movile.pgle.listener;

import com.movile.pgle.PeerGroup;

public interface Worker {
    public void work();
    public void startWorking();
    public void stop();
}
