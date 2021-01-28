package tool.cfg;

import java.util.Properties;

public class ClusterSetup {

    public static Properties getProperties() {

        Properties props = new Properties();

        String schemaUrl = "http://192.168.3.240:8081";

        props.put("client.id", "MailTransferApp");

        props.put("bootstrap.servers", "192.168.3.240:9092");

        props.put("acks", "all");
        props.put("retries", 1);
        props.put("batch.size", 16384);
        props.put("linger.ms", 1);
        props.put("buffer.memory", 33554432);

        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "io.confluent.kafka.serializers.KafkaAvroSerializer");

        props.put("schema.registry.url", schemaUrl);

        return props;

    }

    public static Properties getProperties_LOCALHOST() {

        Properties props = new Properties();

        String schemaUrl = "http://MirkoKampfMBP15:8081";
        props.put("bootstrap.servers", "MirkoKampfMBP15:9092");

        props.put("client.id", "MailTransferApp");

        props.put("acks", "all");
        props.put("retries", 1);
        props.put("batch.size", 16384);
        props.put("linger.ms", 1);
        props.put("buffer.memory", 33554432);

        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "io.confluent.kafka.serializers.KafkaAvroSerializer");

        props.put("schema.registry.url", schemaUrl);

        return props;

    }
}
