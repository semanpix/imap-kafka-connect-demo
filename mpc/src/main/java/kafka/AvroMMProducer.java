package kafka;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.dataformat.avro.AvroMapper;
import com.fasterxml.jackson.dataformat.avro.AvroSchema;
import com.fasterxml.jackson.dataformat.avro.schema.AvroSchemaGenerator;
import data.MM;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import tool.cfg.ClusterSetup;

import java.util.Properties;

public class AvroMMProducer extends MMProducer {

    public AvroMMProducer()   {
        super();

        try {
            getSchemaStringTest();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        }

    }

    public void setupProducerImplementation() {

        producer = new KafkaProducer<>( initPROPS() );

        Runtime.getRuntime().addShutdownHook(
                new Thread(() -> {
                    System.out.println("### Stopping kafka.MMProducer ###");
                    producer.close();
                })
        );

    }

    public Properties initPROPS() {

        System.out.println("### kafka.AvroMMProducer ###");

        Properties props = ClusterSetup.getProperties();

        return props;

    }

    public void pushToTopic(String topic, int i, MM mm) {

        ProducerRecord<Object,Object> record = new ProducerRecord<>(topic, i+"", mm.data );

        try {
            producer.send( record );
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * This is only for debugging and root cause analysis ...
     *
     * @throws JsonMappingException
     */
    public void getSchemaStringTest() throws JsonMappingException {

        AvroMapper mapper = new AvroMapper();
        AvroSchemaGenerator gen = new AvroSchemaGenerator();
        mapper.acceptJsonFormatVisitor(Test.class, gen);
        AvroSchema schema = gen.getGeneratedSchema();

        String json = schema.getAvroSchema().toString(true);
        System.out.println( json );

    }

}

/**
 * This class definition has been used for testing the Avro serialization in a Producer.
 */
class Test {

    public String receiver;
    public String sender;
    public String dateSent;
    public String size;
    public String subject;
    public Integer nrOfReciepients;

}