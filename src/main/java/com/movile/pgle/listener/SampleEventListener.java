package com.movile.pgle.listener;

import com.movile.pgle.PeerGroup;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

//@Component
public class SampleEventListener extends EventListener {

    @Autowired
    Logger log;

    @Override
    public void peerGroupRegister(PeerGroup peerGroup) {
        log.info("SampleEventListener {}", peerGroup);
    }

    @Override
    public void isLeader(PeerGroup peerGroup) {
        log.info("Is Leader {}", peerGroup);
    }

    @Override
    public void isNotLeader(PeerGroup peerGroup) {
        log.info("Is Not Leader {}", peerGroup);
    }
}
