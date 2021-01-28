package org.semanpix.kafka.connect.mail.connector;

import data.MM;
import org.apache.kafka.connect.source.SourceRecord;
import org.apache.kafka.connect.source.SourceTask;
import org.semanpix.kafka.connect.mail.connector.config.IMAPMailSourceConnectorConfig;
import org.semanpix.kafka.connect.mail.connector.util.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tool.CheckIMAPMail;
import tool.cfg.MailBoxSetup;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.semanpix.kafka.connect.mail.connector.config.RandomLongSourceConnectorConfig.*;
import static org.semanpix.kafka.connect.mail.connector.util.RandomLongSchemas.VALUE_SCHEMA;

public class IMAPMailSourceTask extends SourceTask {
    private static Logger logger = LoggerFactory.getLogger(IMAPMailSourceTask.class);

    private IMAPMailSourceConnectorConfig imapMailSourceConnectorConfig;
    private CountDownLatch stopLatch = new CountDownLatch(1);
    private boolean shouldWait = false;
    private int sleepInSeconds;
    private String apiUrl;
    private String topic;

    private MailBoxSetup mbs = null;
    //private String mailbox = "";

    private boolean isReady = false;

    private CheckIMAPMail cmi = null;

    @Override
    public void start(Map<String, String> props) {

        logger.info("Starting IMAPMailSourceTask source task with properties {}", props);

        imapMailSourceConnectorConfig = new IMAPMailSourceConnectorConfig(props);
        apiUrl = imapMailSourceConnectorConfig.getString(API_URL_CONFIG);
        sleepInSeconds = imapMailSourceConnectorConfig.getInt(SLEEP_CONFIG);
        topic = imapMailSourceConnectorConfig.getString(TOPIC_CONFIG);


        mbs = MailBoxSetup.getGoogleMailSent();
        cmi = new CheckIMAPMail();


        try {

            cmi.login(mbs);
            isReady = cmi.isLoggedIn();

            cmi.initMessages();

        }
        catch (Exception ex){
            ex.printStackTrace();
            isReady = false;
        }

    }

    @Override
    public synchronized void stop() {
        logger.info("Stopping IMAPMailSourceTask");
        stopLatch.countDown();
    }

    @Override
    public List<SourceRecord> poll() throws InterruptedException {

        /**
         * Polling from IMAP Server ...
         */

        boolean shouldStop = false;

        if (shouldWait) {

            logger.info("Waiting for {} seconds for the next poll", sleepInSeconds);

            shouldStop = stopLatch.await(sleepInSeconds, TimeUnit.SECONDS);

            try {

                cmi.initMessages();

            }
            catch (Exception ex) {
                ex.printStackTrace();
            }

        }
        if (!shouldStop) {

            logger.info("Started new polling");

            shouldWait = !cmi.hasMoreMailsInPollBuffer();

            return getSourceRecords();

        }
        else {

            logger.debug("Received signal to stop, didn't poll anything");
            return null;

        }
    }

    private List<SourceRecord> getSourceRecords() {
        SourceRecord record = new SourceRecord(
                null,
                null,
                topic,
                VALUE_SCHEMA,
                getMailFromIMAPMailBox());
        return Collections.singletonList(record);
    }

    private MM getMailFromIMAPMailBox() {
        return cmi.getNextMessage();
    }

    @Override
    public String version() {
        return Version.getVersion();
    }

}
