package com.movile.pgle.listener;

import com.movile.pgle.PeerGroup;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SampleEventListener extends EventListener {

    @Autowired
    Logger log;

    @Override
    void peerGroupRegister(PeerGroup peerGroup) {
        log.info("SampleEventListener {}", peerGroup);
    }

    @Override
    void isLeader(PeerGroup peerGroup) {
        log.info("Is Leader {}", peerGroup);
    }

    @Override
    void isNotLeader(PeerGroup peerGroup) {
        log.info("Is Not Leader {}", peerGroup);
    }
}
