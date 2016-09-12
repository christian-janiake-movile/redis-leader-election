package com.movile.pgle;

import com.movile.res.redis.RedisConnectionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component("singleClusterLeaderElection")
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class LeaderElection {
    protected Integer peerGroupId;
    protected Long ttl;
    protected Date start;

    @Autowired
    private RedisConnectionManager redis;

    @Value("#{systemProperties['redis.leader.election.key.prefix'] ?: 'leader.election.group'}")
    protected String keyPrefix;

    @Value("#{systemProperties['HOSTNAME']}")
    String hostname;

    @Autowired
    private Environment env;

    public LeaderElection(Integer peerGroupId, Long ttl) {
        this.redis = redis;
        this.peerGroupId = peerGroupId;
        this.ttl = ttl;
        this.start = new Date();
    }

    public boolean isLeader() {
        String key = keyPrefix + this.peerGroupId.toString();

        // try to lock
        String result = redis.setnx(key, hostname, ttl);

        return "OK".equals(result);
    }

    public void unregisterLeadership() {
        String key = keyPrefix + this.peerGroupId.toString();
        redis.del(key);
    }
}
