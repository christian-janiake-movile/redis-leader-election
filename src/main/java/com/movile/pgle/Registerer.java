package com.movile.pgle;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

@Component
public class Registerer {
    @Autowired Logger log;

    @Autowired
    private ApplicationContext ctx;

    static class JsonFilter implements FilenameFilter {
        @Override
        public boolean accept(File dir, String name) {
            return name.endsWith("-group.json");
        }
    };

    static class PropertiesFilter implements FilenameFilter {
        @Override
        public boolean accept(File dir, String name) {
            return name.endsWith("-group.properties");
        }
    };

    static class PeerGroupDefinition {
        public Integer id;
        public String name;
        public Long electionInterval;
        public Long leadershipInterval;
        public String workerClass;

        @Override
        public String toString() {
            return "PeerGroupDefinition{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    ", electionInterval=" + electionInterval +
                    ", leadershipInterval=" + leadershipInterval +
                    ", workerClass=" + workerClass +
                    '}';
        }
    }

    public void initialize() {
        try {
            String groupConfigFilesPath = ctx.getEnvironment().getProperty("leader.election.config.path", ".");
            File groupConfigFilesFolder = new File(groupConfigFilesPath);
            int currentId = 1;

            File[] propertiesFiles = groupConfigFilesFolder.listFiles(new PropertiesFilter());
            for(File propertiesFile:propertiesFiles){
                log.info("Loading " + propertiesFile.getPath());

                Properties groupProperties = new Properties();
                groupProperties.load(new FileReader(propertiesFile));
                log.info(groupProperties.toString());

                String name = groupProperties.getProperty("group.name");
                Integer id = currentId++;
                Long electionInterval = Long.parseLong(groupProperties.getProperty("group.electionInterval"));
                Long leadershipInterval = Long.parseLong(groupProperties.getProperty("group.leadershipInterval"));
                String workerClass = groupProperties.getProperty("group.workerClass", null);

                PeerGroup peerGroup = (PeerGroup) ctx.getBean("peerGroup", id, name, electionInterval, leadershipInterval, workerClass);
            }

            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            File[] jsonFiles = groupConfigFilesFolder.listFiles(new JsonFilter());
            for(File jsonFile:jsonFiles) {
                log.info("Loading " + jsonFile.getPath());

                List<PeerGroupDefinition> data = mapper.readValue(jsonFile, new TypeReference<List<PeerGroupDefinition>>(){});

                for(PeerGroupDefinition def:data) {

                    log.info(def.toString());
                    PeerGroup peerGroup = (PeerGroup) ctx.getBean("peerGroup", currentId++, def.name, def.electionInterval, def.leadershipInterval, def.workerClass);
                }
            }

        } catch(IOException e) {
            log.error(e.toString(), e);
        }


    }

}
