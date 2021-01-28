package kafka;

import data.MM;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import tool.cfg.ClusterSetup;
import tool.cfg.MailBoxSetup;
import tool.cfg.MailTopicSetup;
import util.Timer;

import javax.mail.Message;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Properties;

import static tool.cfg.MessageBufferSetup.*;

public class MMProducer {

    final public static Integer STORE_EMAIL_AS_FILE = 0;
    final public static Integer PUSH_EMAIL_AS_AVRO = 1;

    boolean verbose = true;

    Timer t1;

    Producer<Object, Object> producer;

    public static void main(String[] ARGS) {

        MMProducer mmp = new MMProducer();

        mmp.setupProducerImplementation();

        //mmp.runTest();

    }

    public MMProducer() {

        t1 = new Timer();

    }

    public void setupProducerImplementation() {

        producer = new KafkaProducer<>( initPROPS() );

        Runtime.getRuntime().addShutdownHook(
                new Thread(() -> {
                    System.out.println("### Stopping kafka.MMProducer ###");
                    producer.close();
                }));

    }

    public Properties initPROPS() {

        System.out.println("### kafka.MMProducer ###");

        Properties props = ClusterSetup.getProperties_LOCALHOST();

        return props;

    }

    public void processMails(Message[] messages, int firstID, int lastID, int mode, MailBoxSetup msb) throws Exception {

        int i = 0;
        boolean skip = true;

        String topic = MailTopicSetup.getTopicName( msb );
        String theFolder = getLocalBufferMailFolderFile( msb ).getAbsolutePath();

        System.out.println(">>> topic  :  " + topic );
        System.out.println(">>> folder :  " + theFolder );

        for ( Message m : messages ) {

            skip = (i < firstID) || ( i > lastID );

            if( !skip ) {

                MM mm = new MM( m );

                switch ( mode ) {

                    // STORE_EMAIL_AS_FILE
                    case 0 :  {
                        storeInFolder( theFolder, i, mm );
                        break;
                    }

                    // PUSH_EMAIL_AS_AVRO
                    case 1 : {
                        pushToTopic( topic , i, mm );
                        break;
                    }

                }



                if ( i % 1 == 0 ) {

                    if ( verbose ) {

                        System.out.println("[" + t1.getTimeStep(i + " loaded") + "] :: (" + i + ")");
                        producer.flush();

                    }
                }

            }

            i++;

        }

        producer.flush();
        producer.close();

        t1.getTimeStep( "finished" );

    }


    File folder = null;
    private void storeInFolder(String folderName, int i, MM mm) {

        try {

            if (folder == null) {
                folder = new File(folderName);
                folder.mkdirs();
            }

            File messageFile = new File(folder.getAbsolutePath() + "/" + i + ".eml");

            FileOutputStream fos = new FileOutputStream(messageFile);
            mm.message.writeTo( fos );

            fos.flush();
            fos.close();

        }
        catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public void pushToTopic(String topic, int i, MM mm) {

        producer.send(new ProducerRecord<>(topic, Integer.toString(i), mm.data ));

    }

    public void runTest() {

        t1.getTimeStep( "prepared" );

        for (int i = 0; i < 500; i++) {

            producer.send(new ProducerRecord<>(MailTopicSetup.topic, Integer.toString(i), Integer.toString(i)));

            //System.out.println( i );

        }

        t1.getTimeStep( "created" );

        producer.flush();
        producer.close();

        t1.getTimeStep( "finished" );

        t1.showEvents();

        System.out.println("Done.");

        System.exit(0);

    }
}
