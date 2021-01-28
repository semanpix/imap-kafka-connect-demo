package org.semanpix.kafka.connect.mail.connector.util;

import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Version {

    private static final Logger log = LoggerFactory.getLogger(Version.class);

    private static String version = "0.1.0";

    public static String getVersion() {
        return version;
    }
}
