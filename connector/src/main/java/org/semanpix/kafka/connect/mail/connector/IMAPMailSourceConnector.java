package org.semanpix.kafka.connect.mail.connector;

import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.connector.Task;
import org.apache.kafka.connect.source.SourceConnector;
import org.semanpix.kafka.connect.mail.connector.config.IMAPMailSourceConnectorConfig;
import org.semanpix.kafka.connect.mail.connector.config.RandomLongSourceConnectorConfig;
import org.semanpix.kafka.connect.mail.connector.util.Version;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.semanpix.kafka.connect.mail.connector.config.RandomLongSourceConnectorConfig.*;

public class IMAPMailSourceConnector extends SourceConnector {

    private IMAPMailSourceConnectorConfig imapMailSourceConnectorConfig;

    @Override
    public void start(Map<String, String> props) {
        imapMailSourceConnectorConfig = new IMAPMailSourceConnectorConfig(props);
    }

    @Override
    public void stop() {}

    @Override
    public Class<? extends Task> taskClass() {
        return IMAPMailSourceTask.class;
    }

    @Override
    public List<Map<String, String>> taskConfigs(int maxTasks) {
        List<Map<String, String>> configs = new ArrayList<>(maxTasks);
        for (int i = 0; i < maxTasks; i++) {
            Map<String, String> config = new HashMap<>(3);
            config.put(API_URL_CONFIG, imapMailSourceConnectorConfig.getString(API_URL_CONFIG));
            config.put(SLEEP_CONFIG, Integer.toString(imapMailSourceConnectorConfig.getInt(SLEEP_CONFIG)));
            config.put(TOPIC_CONFIG, imapMailSourceConnectorConfig.getString(TOPIC_CONFIG));
            configs.add(config);
        }
        return configs;
    }

    @Override
    public ConfigDef config() {
        return IMAPMailSourceConnectorConfig.config();
    }

    @Override
    public String version() {
        return Version.getVersion();
    }

}
