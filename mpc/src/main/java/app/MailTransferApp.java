package app;

import kafka.AvroMMProducer;
import kafka.MMProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tool.CheckIMAPMail;
import tool.cfg.MailBoxSetup;
import tool.cfg.ProcessingRange;
import util.Timer;

import javax.mail.Message;

/**
 * This application loads email messages from one mailbox, converts it into an event
 * ans sends it via a "Kafka Producer" to a Kafka topic, named "mail.communication.context".
 */
public class MailTransferApp {

    static Logger logger = LoggerFactory.getLogger(MailTransferApp.class);

    public final static int LOAD_FROM_MAILBOX = 0;
    public final static int LOAD_FROM_BUFFER = 1;


    public final static int PROCESS_INBOX = 0;
    public final static int PROCESS_SENT = 1;

    static MMProducer mmp;

    public static boolean verbose = true;

    public static void main(String[] ARGS) {

        // int PROCESSMODE = PROCESS_INBOX;
        int PROCESSMODE = PROCESS_SENT;

        // int PERSIST_MODE = MMProducer.STORE_EMAIL_AS_FILE;
        int PERSIST_MODE = MMProducer.PUSH_EMAIL_AS_AVRO;

        if ( verbose ) {
            showUsageNotes();
            //showSchemaOfMailMessageImplementation();
        }

        processPoll(PROCESSMODE,PERSIST_MODE);

    }

    public static String processPoll( int PROCESSMODE, int PERSIST_MODE )  {

        try {

            // mmp = new kafka.MMProducer();
            mmp = new AvroMMProducer();
            mmp.setupProducerImplementation();

            logger.info("> created an AvroMMProducer ...");

        }
        catch (Exception e) {
            e.printStackTrace();
        }

        logger.info(">>> PROCESS_MODE  : " + PROCESSMODE );
        logger.info(">>> PERSIST_MODE  : " + PERSIST_MODE );
        logger.info(">>> MAIL RANGE    : " + ProcessingRange.asString() );

        CheckIMAPMail cmi = new CheckIMAPMail();

        MailBoxSetup mbsINBOX = MailBoxSetup.getGoogleMailInbox();
        MailBoxSetup mbsSENT = MailBoxSetup.getGoogleMailSent();

        MailBoxSetup mbs = null;

        Message[] messages = null;

        try {

            Timer t1 = Timer.getTimer();

            logger.info("> created a the IMAP-Mailbox ...");

            try {

                if( PROCESSMODE == PROCESS_INBOX ) {
                    mbs = mbsINBOX;
                }
                if( PROCESSMODE == PROCESS_SENT ) {
                    mbs = mbsSENT;
                }

                cmi.login( mbs );

                logger.info( "[" + t1.getTimeElapsed_in_s() + "] :: CheckIMAPMail is connected: " + cmi.isLoggedIn()) ;
                logger.info( "[" + t1.getTimeElapsed_in_s() + "] :: # of messages available on server: " + cmi.getMessageCount()) ;

                messages = cmi.getMessages();

                logger.info("> loaded " + messages.length + " messages from mail server." );

            }
            catch(Exception ex ) {

                ex.printStackTrace();

                logger.info( "\n**** NO CONNECTION ***" );
                logger.info( "> mail server : " + mbs.host );
                logger.info( "> username    : " + mbs.username + "\n" );

                messages = cmi.readMessagesFromFile( mbs );

                logger.info("> loaded " + messages.length + " messages from local folder." );

            }

            logger.info( "[" + t1.getTimeElapsed_in_s() + "] :: # of messages loaded to array: " + messages.length );

            mmp.processMails( messages, ProcessingRange.MIN, ProcessingRange.MAX, PERSIST_MODE, mbs );

        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return "Process is active ...";

    }

    private static void showUsageNotes() {

        System.out.println("========================================================================================");
        System.out.println("*** MAIL CHECKER ***\n" +
                           "    => dive into your personal communication patterns and improve your performance.");
        System.out.println("----------------------------------------------------------------------------------------");

        System.out.println("> MailChecker loads a stream of inbound and outbound emails to a local buffer-folder." );
        System.out.println("> For analysis we load this data into a kafka topic. This allows recurring ad-hoc analysis fast development" +
                "  of a streaming application." );
        System.out.println("> Finally, the streaming application will run in a cloud based deployment,");
        System.out.println("  in which messages are ingested regularly via Kafka connect.\n\n" );
        System.out.println("----------------------------------------------------------------------------------------");
        System.out.println(" COMMANDS to verify the ingestion into the topic");
        System.out.println("----------------------------------------------------------------------------------------");
        System.out.println("$ export CONFLUENT_HOME=/Users/mkampf/bin/confluent-5.3.0/");
        System.out.println("$ confluent local status");
        System.out.println("$ cd $CONFLUENT_HOME/bin/");
        System.out.println("$ ./kafka-console-consumer --bootstrap-server localhost:9092 --topic example");

    }

/*    private static void showSchemaOfMailMessageImplementation() {

        MM mm1 = new MM();

        try {
            mm1.getAsAvroData();
        }
        catch (JsonMappingException e) {
            e.printStackTrace();
        }

    }*/



}


