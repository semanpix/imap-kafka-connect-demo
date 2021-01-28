package tool.cfg;

public class MailBoxSetup {

    public String protocol = "imaps";
    public String host = "imap.gmail.com";
    public int port = 993;

    public String folder = null;
    public String topic = null;


    public String username = "mirko.kaempf@gmail.com";
    public static String password = "???";



    public static MailBoxSetup getGoogleMailSent() {
        MailBoxSetup mbs = new MailBoxSetup();
        mbs.folder = "[Google Mail]/Gesendet";
        mbs.topic = "sent";

        java.io.Console console = System.console();
        password = new String(console.readPassword("Mailbox-Password: "));

        return mbs;

    }

    public static MailBoxSetup getGoogleMailInbox() {
        MailBoxSetup mbs = new MailBoxSetup();
        mbs.folder = "inbox";
        mbs.topic = "inbox";

        return mbs;
    }

    // Folder name and topic name are equal.
    public String getFolderName_normalized() {
        return topic;
    }
}
