package com.movile.pgle;

import com.movile.res.redis.RedisConnectionManager;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.Scope;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@PropertySource({"application.properties"})
@Component("dualClusterLeaderElection")
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class DualDCLeaderElection extends LeaderElection {

    @Autowired
    @Qualifier("remoteRedis")
    protected RedisConnectionManager remoteRedis;

    @Autowired
    ApplicationContext ctx;

    @Autowired
    Logger log;

    private Boolean localIsPrimary;

    public DualDCLeaderElection(Integer peerGroupId, Long ttl) {
        super(peerGroupId, ttl);
    }

    @PostConstruct
    public void initialize() {
        this.localIsPrimary = Boolean.valueOf(ctx.getEnvironment().getProperty("redis.local.is.primary"));
    }

    @Override
    public boolean isLeader() {
        if(super.isLeader()) {
            log.info("{} is leader local", peerGroupId);
            String key = keyPrefix + this.peerGroupId.toString();

            // subtract from ttl time elapsed in previous locking operation
            long elapsedPrevious = System.currentTimeMillis() - start.getTime();

            String result = null;
            if(elapsedPrevious < ttl) {

                try {
                    // try lock remote
                    log.debug("{} try remote lock", peerGroupId);
                    result = remoteRedis.setnx(key, hostname, ttl - elapsedPrevious);
                    log.debug("{} remote lock result: {}", peerGroupId, result);
                } catch(Exception e) {
                    log.info("Remote Redis connection failed: {}", e.toString());
                    if(localIsPrimary) {
                        log.info("Leader was elected locally");
                        return true;
                    }
                }
            } else {
                log.debug("{} elapsed time greater than ttl", peerGroupId);
            }

            if("OK".equals(result)) {
                // if success return true
                return true;
            } else {
                if(localIsPrimary) {
                    // else if primary is locked retry secondary (remote)
                    return retryRemote(key);
                } else {
                    // else release lock return false
                    super.unregisterLeadership();
                    return false;
                }
            }
        } else {
            return false;
        }
    }

    @Retryable(maxAttempts = 150, backoff = @Backoff(delay = 200l), value = RuntimeException.class)
    private boolean retryRemote(String key) {
        String result = null;
        long elapsedPrevious = System.currentTimeMillis() - start.getTime();
        if(elapsedPrevious > (ttl *0.75)) {
            log.info("Conflict on leader election: local leadership is primary, but remote could not be locked. Give up and unlock local");
            super.unregisterLeadership();
            return false;
        }
        try {
            // try lock remote
            log.debug("{} retry remote lock", peerGroupId);
            result = remoteRedis.setnx(key, hostname, ttl - elapsedPrevious);
            log.debug("{} retry remote lock result: {}", peerGroupId, result);
        } catch(Exception e) {
            log.info("Remote Redis connection failed: {}", e.toString());
            throw e;
        }
        if ("OK".equals(result)) {
            return true;
        } else {
            throw new RuntimeException("Conflict on leader election: local leadership is primary, but could not lock remote yet. Trying again");
        }
    }

    public void unregisterLeadership() {
        String key = keyPrefix + this.peerGroupId.toString();
        remoteRedis.del(key);
    }
}
