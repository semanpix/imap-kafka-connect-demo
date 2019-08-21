package org.semanpix.kafka.connect.mail.api;

public class MailAPITester {

    public static void main(String[] ARGS) {
        MailGeneratorController mgc = new MailGeneratorController();
        System.out.println( mgc.randomMailMetadataAsMIMEString() );
    }

}
