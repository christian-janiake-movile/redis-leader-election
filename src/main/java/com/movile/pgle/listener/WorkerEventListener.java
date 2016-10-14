package com.movile.pgle.listener;

import com.movile.pgle.PeerGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class WorkerEventListener extends EventListener {
    private Map<Integer, Worker> workers = new HashMap<>();

    @Autowired
    private ApplicationContext ctx;

    @Override
    public void peerGroupRegister(PeerGroup peerGroup) {
        if(peerGroup.hasWorker()) {
            try {
//                Class workerClass = Class.forName(peerGroup.getWorkerClass());
                Worker workerInstance = (Worker) ctx.getBean(peerGroup.getWorkerClass(), peerGroup);
//                if(workerInstance == null) {
//                    log.info("No bean found on context for worker class {}", peerGroup.getWorkerClass());
//                    workerInstance = (Worker) workerClass.getConstructor(PeerGroup.class).newInstance(peerGroup);
//                }
                new Thread((Runnable) workerInstance).start();
                workers.put(peerGroup.getId(), workerInstance);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void isLeader(PeerGroup peerGroup) {
        if(workers.containsKey(peerGroup.getId())) {
            try {
                workers.get(peerGroup.getId()).work();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void isNotLeader(PeerGroup peerGroup) {
//        if(workers.containsKey(peerGroup.getId())) {
//            workers.get(peerGroup.getId()).stop();
//        }
    }
}
