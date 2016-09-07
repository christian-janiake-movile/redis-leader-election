package com.movile.pgle.listener;

import com.movile.pgle.PeerGroup;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class WorkerEventListener extends EventListener {
    private Map<Integer, Worker> workers = new HashMap<>();

    @Override
    void peerGroupRegister(PeerGroup peerGroup) {
        if(peerGroup.hasWorker()) {
            try {
                Worker workerInstance = (Worker) Class.forName(peerGroup.getWorkerClass()).getConstructor(PeerGroup.class).newInstance(peerGroup);
                new Thread((Runnable) workerInstance).start();
                workers.put(peerGroup.getId(), workerInstance);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    void isLeader(PeerGroup peerGroup) {
        if(workers.containsKey(peerGroup.getId())) {
            try {
                workers.get(peerGroup.getId()).work();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    void isNotLeader(PeerGroup peerGroup) {
        if(workers.containsKey(peerGroup.getId())) {
            workers.get(peerGroup.getId()).stop();
        }
    }
}
