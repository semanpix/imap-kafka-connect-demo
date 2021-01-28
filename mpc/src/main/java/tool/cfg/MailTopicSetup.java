package tool.cfg;

public class MailTopicSetup {

    public static String topic = "example_emails_";

    public static String getTopicName(MailBoxSetup mbs ) {

        return topic + mbs.getFolderName_normalized();

    }

}
