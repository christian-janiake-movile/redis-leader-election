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
import java.util.Map;
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

                registerPeerGroup(currentId++, groupProperties);
            }

            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            File[] jsonFiles = groupConfigFilesFolder.listFiles(new JsonFilter());
            for(File jsonFile:jsonFiles) {
                log.info("Loading " + jsonFile.getPath());

                List<Map<String, String>> data = mapper.readValue(jsonFile, new TypeReference<List<Map<String, String>>>(){});

                for(Map<String, String> groupDef:data) {

                    Properties groupProperties = new Properties();
                    groupProperties.putAll(groupDef);
                    log.info(groupProperties.toString());

                    registerPeerGroup(currentId++, groupProperties);
                }
            }

        } catch(IOException e) {
            log.error(e.toString(), e);
        }


    }

    private void registerPeerGroup(int id, Properties groupProperties) {
        PeerGroup peerGroup = (PeerGroup) ctx.getBean("peerGroup", id, groupProperties);
    }

}
