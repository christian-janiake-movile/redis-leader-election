package com.movile.pgle;

import com.movile.res.redis.RedisConfig;
import com.movile.res.redis.RedisConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@EnableRetry
@Configuration
@ComponentScan("com.movile.pgle")
@PropertySource({"application.properties", "auth.properties"})
public class Coordinator {

    @Bean
    public Logger logger(@Value("com.movile.pgle") String loggerName) {
        return LoggerFactory.getLogger(loggerName);
    }

    @Bean
    public ThreadPoolTaskScheduler scheduler() {
        return new ThreadPoolTaskScheduler();
    };

    @Autowired Environment env;

    @Bean
    public RedisConnectionManager redis() {
        String host = env.getProperty("redis.host");
        int port = Integer.parseInt(env.getProperty("redis.port"));
        return new RedisConnectionManager(new RedisConfig(host, port));
    };

    @Bean
    @Qualifier("remoteRedis")
    public RedisConnectionManager remoteRedis() {
        String host = env.getProperty("redis.remote.host");
        Integer port = Integer.valueOf(env.getProperty("redis.remote.port"));
        if(host != null && port != null) {
            return new RedisConnectionManager(new RedisConfig(host, port));
        } else {
            return null;
        }
    };

    public static void main(String[] args) {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.register(Coordinator.class);
        ctx.refresh();
        Registerer registerer = (Registerer) ctx.getBean(Registerer.class);
        registerer.initialize();
    }

}
