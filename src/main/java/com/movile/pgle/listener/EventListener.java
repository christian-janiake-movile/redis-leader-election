package com.movile.pgle.listener;

import com.movile.pgle.Event;
import com.movile.pgle.PeerGroup;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

public abstract class EventListener implements ApplicationListener<Event> {

    @Autowired
    Logger log;

    @Override
    public void onApplicationEvent(Event event) {
        switch(event.type) {
            case PEER_GROUP_REGISTER:
                log.info("PEER GROUP REGISTER");
                peerGroupRegister((PeerGroup) event.getSource());
                break;
            case IS_LEADER:
                log.info("* * * * * * * * * * * LEADER * * * * * * * * * * *");
                isLeader((PeerGroup) event.getSource());
                break;
            case IS_NOT_LEADER:
                log.info("* * * NOT LEADER * * *");
                isNotLeader((PeerGroup) event.getSource());
                break;
        }
    }

    abstract void peerGroupRegister(PeerGroup peerGroup);
    abstract void isLeader(PeerGroup peerGroup);
    abstract void isNotLeader(PeerGroup peerGroup);
}
