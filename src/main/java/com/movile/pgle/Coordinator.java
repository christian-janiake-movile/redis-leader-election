package com.movile.pgle;

import com.movile.res.redis.RedisConfig;
import com.movile.res.redis.RedisConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.UUID;

@Configuration
@ComponentScan("com.movile.pgle")
@PropertySource({"application.properties", "auth.properties"})
public class Coordinator {

    @Bean
    public Logger log(@Value("cmo.movile.pgle") String loggerName) {
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

    @Bean(name="hostname")
    public String hostname() {
        if(env.containsProperty("HOSTNAME")) {
            return env.getProperty("HOSTNAME");
        } else {
//            log.warn("Could not retrieve HOSTNAME environment variable, generate UUID as identifier");
            return UUID.randomUUID().toString();
        }

    }

    public static void main(String[] args) {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.register(Coordinator.class);
        ctx.refresh();
        Registerer registerer = (Registerer) ctx.getBean(Registerer.class);
        registerer.initialize();
    }

}
