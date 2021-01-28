package tool.cfg;

import java.io.File;

/**
 *
 */
public class MessageBufferSetup {

    // Where to store the email files temporarily?
    static String folderName = "/app/DATA/tmp/example_";

    // compose a foldername for emails per mailbox
    public static File getLocalBufferMailFolderFile(MailBoxSetup mbs ) {

        String fn = mbs.getFolderName_normalized();

        // REMOVE CHARACTERS WHICH DO NOT FIT WELL INTO A FILESYSTEM PATH
        File f = new File( folderName + fn );

        return f;

    }

}
