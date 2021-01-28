package tool;

import data.MM;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tool.cfg.MailBoxSetup;
import tool.cfg.MessageBufferSetup;

import javax.mail.*;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Vector;

public class CheckIMAPMail {

    int cursor = 0;

    Message[] messages = null;

    Logger logger = LoggerFactory.getLogger(CheckIMAPMail.class);

    private Session session;
        private Store store;
        private Folder folder;

        // FOLDER NAME
        private String file = null;

        public CheckIMAPMail() { }

        public boolean isLoggedIn() {
            return store.isConnected();
        }

        /**
         * to login to the mail host server
         */
        public void login(MailBoxSetup mbs)  throws Exception {
            login(  mbs.protocol, mbs.host, mbs.port, mbs.username, mbs.password, mbs.folder );
        }

        public void login(String protocol, String host, int port, String username, String password, String _file)
                throws Exception {

            file = _file;

            URLName url = new URLName(protocol, host, port, file, username, password);

            if (session == null) {
                Properties props = null;
                try {
                    props = System.getProperties();
                } catch (SecurityException sex) {
                    props = new Properties();
                }
                session = Session.getInstance(props, null);
            }

            store = session.getStore(url);
            store.connect( username, password );

            System.out.println("> URL: " + url);

            logger.info("> Login to URL: " + url + " as {"+username+"}->("+ isLoggedIn() +") ");

            folder = store.getFolder( url );
            folder.open(Folder.READ_ONLY);

        }

        public void showFolders() throws Exception {

            Folder[] FOLDERS = folder.list();

            for( Folder f : FOLDERS ) {
                try {

                        System.out.print(">>> " + f.getName());
                        f.open(Folder.READ_ONLY);
                        int z = f.getMessageCount();
                        f.close(true);
                        System.out.println(" " + z);

                }
                catch (Exception ex2) {
                    ex2.printStackTrace();
                }
            }

        }
        /**
         * to logout from the mail host server
         */
        public void logout() throws MessagingException {
            folder.close(false);
            store.close();
            store = null;
            session = null;
        }

        public int getMessageCount() {
            int messageCount = 0;
            try {
                messageCount = folder.getMessageCount();
            } catch (MessagingException me) {
                me.printStackTrace();
            }
            return messageCount;
        }

    public void initMessages() throws MessagingException {
        messages = folder.getMessages();
    }

    public Message[] getMessages() throws MessagingException {
        if( messages == null )
            initMessages();

        return messages;
    }

    public Message[] readMessagesFromFile(MailBoxSetup mbs) throws FileNotFoundException, MessagingException {

        Message[] m = null;

        File messageFile = MessageBufferSetup.getLocalBufferMailFolderFile( mbs );

        System.out.println( "> Start processing mails from local transfer buffer : " + messageFile.getAbsolutePath() + " (canRead:"+messageFile.canRead() +")");

        File[] ff = messageFile.listFiles();
        Vector<File> fff = new Vector<File>();

        for( File f : ff ) {
            if( f.length() != 0 && f.getName().endsWith("eml"))
                fff.add(f);
        }

        int i = 0;
        m = new Message[fff.size()];
        for( File f : fff ) {

            Properties props = System.getProperties();
            props.put("mail.host", "imap.gmail.com");
            props.put("mail.transport.protocol", "smtp");

            Session mailSession = Session.getDefaultInstance(props, null);
            InputStream source = new FileInputStream(f.getAbsolutePath());
            MimeMessage message = new MimeMessage(mailSession, source);
            m[i] = message;
            System.out.println( i + " > " + message.getSentDate() + " : " + message.getReceivedDate() );
            i++;

        }

        return m;
    }

    public boolean hasMoreMailsInPollBuffer(){
        if ( cursor > messages.length ) {
            return false;
        }
        else return true;
    }

    public MM getNextMessage() {
        Message m = messages[ cursor ];
        cursor++;

        MM mm = new MM( m );

        return mm;
    }

}
