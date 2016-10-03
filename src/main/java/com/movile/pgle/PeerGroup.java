package com.movile.pgle;

import com.movile.res.redis.RedisConnectionManager;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Date;
import java.util.Properties;

@Component("peerGroup")
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PeerGroup {
    private Integer id;
    private Properties properties;
    private String name;
    private Long electionInterval;
    private Long leadershipInterval;
    private String workerClass;        // optional: Class implementing Worker interface

    private Boolean isLeader = Boolean.FALSE;
    private Date isLeaderUntil;

    @Autowired
    private ApplicationContext ctx;

    @Autowired
    ThreadPoolTaskScheduler scheduler;

    @Autowired
    RedisConnectionManager redis;

    @Autowired
    Logger log;

    @Autowired
    PeerGroupEventPublisher peerGroupEventPublisher;

    @PostConstruct
    public void schedule() {
        final PeerGroup peerGroup = this;
        peerGroupEventPublisher.publishEvent(new Event(Event.EventType.PEER_GROUP_REGISTER, peerGroup));
        long nextElection = electionInterval - (System.currentTimeMillis() % electionInterval);
        scheduler.scheduleAtFixedRate(new Thread() {
            @Override
            public void run() {
                if(isLeader && System.currentTimeMillis() < isLeaderUntil.getTime()) {
                    log.info("{} already leader, no election (heartbeat maybe?)", name);
                } else {
                    LeaderElection election = (LeaderElection) ctx.getBean("leaderElection", peerGroup);
                    if (election.isLeader()) {
                        log.info("{} Leadership acquired", name);
                        isLeader = Boolean.TRUE;
                        isLeaderUntil = new Date(System.currentTimeMillis() + leadershipInterval);
                        peerGroupEventPublisher.publishEvent(new Event(Event.EventType.IS_LEADER, peerGroup));
                    } else {
                        log.info("{} not my turn", name);
                        if(isLeader) {
                            peerGroupEventPublisher.publishEvent(new Event(Event.EventType.IS_NOT_LEADER, peerGroup));
                            isLeader = false;
                        }
                    }
                }
            }
        }, new Date(nextElection), electionInterval);
    }

    @PreDestroy
    public void unregister() {
        if(isLeader && System.currentTimeMillis() < isLeaderUntil.getTime()) {
            LeaderElection election = (LeaderElection) ctx.getBean("leaderElection", this);
            election.unregisterLeadership();
        }
    }

    public PeerGroup(Integer id, Properties groupProperties) {
        this.id = id;
        this.name = groupProperties.getProperty("group.name");
        this.electionInterval = Long.parseLong(groupProperties.getProperty("group.electionInterval"));
        this.leadershipInterval = Long.parseLong(groupProperties.getProperty("group.leadershipInterval"));
        this.workerClass = groupProperties.getProperty("group.workerClass", null);
        this.properties = groupProperties;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Properties getProperties() {
        return properties;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getElectionInterval() {
        return electionInterval;
    }

    public void setElectionInterval(Long electionInterval) {
        this.electionInterval = electionInterval;
    }

    public Long getLeadershipInterval() {
        return leadershipInterval;
    }

    public void setLeadershipInterval(Long leadershipInterval) {
        this.leadershipInterval = leadershipInterval;
    }

    public Boolean getIsLeader() {
        return isLeader;
    }

    public void setIsLeader(Boolean isLeader) {
        this.isLeader = isLeader;
    }

    public Date getIsLeaderUntil() {
        return isLeaderUntil;
    }

    public void setIsLeaderUntil(Date isLeaderUntil) {
        this.isLeaderUntil = isLeaderUntil;
    }

    public String getWorkerClass() {
        return workerClass;
    }

    public void setWorkerClass(String workerClass) {
        this.workerClass = workerClass;
    }

    public boolean hasWorker() {
        return StringUtils.isNotEmpty(workerClass);
    }

    @Override
    public String toString() {
        return "PeerGroup{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PeerGroup peerGroup = (PeerGroup) o;

        return id.equals(peerGroup.id);

    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}


