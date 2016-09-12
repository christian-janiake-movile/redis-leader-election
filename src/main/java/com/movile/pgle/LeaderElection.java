package com.movile.pgle;

import com.movile.res.redis.RedisConnectionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Date;

@Component("leaderElection")
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class LeaderElection {
    private Integer peerGroupId;
    private Long ttl;
    private Date start;

    @Value("#{systemProperties['redis.leader.election.key.prefix'] ?: 'leader.election.group'}")
    private String keyPrefix;

    @Value("#{systemProperties['HOSTNAME']}")
    String hostname;

    @Autowired
    private Environment env;

    @Autowired
    private RedisConnectionManager redis;

    private PeerGroup peerGroup;

    @Autowired
    public LeaderElection(PeerGroup peerGroup) {
        this.start = new Date();
        this.peerGroup = peerGroup;
    }

    @PostConstruct
    private void initialize() {
        this.redis = redis;
        this.peerGroupId = peerGroup.getId();
        this.ttl = peerGroup.getElectionInterval();
    }

    public boolean isLeader() {
        String key = keyPrefix + this.peerGroupId.toString();
        String result = redis.setnx(key, hostname, ttl);

        return "OK".equals(result);
    }

    public void unregisterLeadership() {
        String key = keyPrefix + this.peerGroupId.toString();
        redis.del(key);
    }
}
