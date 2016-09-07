package com.movile.pgle;

import com.movile.res.redis.RedisConnectionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEvent;
import org.springframework.core.env.Environment;

import java.util.Date;

public class LeaderElection {
    private RedisConnectionManager redis;
    private Integer peerGroupId;
    private Long ttl;
    private Date start;

    @Value("#{redis.leader.election.key.prefix ?: leader.election.group}")
    private String keyPrefix;

    @Value("#{systemProperties['HOSTNAME']}")
    String hostname;

    @Autowired
    private Environment env;

    public LeaderElection(RedisConnectionManager redis, Integer peerGroupId, Long ttl) {
        this.redis = redis;
        this.peerGroupId = peerGroupId;
        this.ttl = ttl;
        this.start = new Date();
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
