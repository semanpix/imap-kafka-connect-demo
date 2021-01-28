package org.semanpix.kafka.connect.mail.api;

import app.MailTransferApp;
import kafka.MMProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import tool.cfg.MailBoxSetup;
import tool.cfg.ProcessingRange;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.Random;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * The MailPollControler handles all the REST API calls.
 *
 * It is a wrapper around the IMAPClient, the MailTransferApp
 */
@RestController
public class MailPollController {

    Logger logger = LoggerFactory.getLogger(MailPollController.class);

    private static Random random = new Random();

    @GetMapping("/poll/imap/mail")

    @RequestMapping(value = "/poll/imap/mail/{minid}/{maxid}/{PASSWORD}", method = GET)
    @ResponseBody
    String pollMailMetadataFromIMAPAccount(@PathVariable("minid") long minid, @PathVariable("maxid") long maxid, @PathVariable("PASSWORD") String pwd){

        MailBoxSetup.password = pwd;
        // logger.info(pwd);

        return getPolledMailsMetadata( minid, maxid );

    }

    private String getPolledMailsMetadata(long minid, long maxid) {

        logger.info("> Start processing an IMAP mail poll request using the class: [MailTransferApp] ... ");

        MailTransferApp app = new MailTransferApp();

        //ProcessingRange.setRange_10_after( minid );
        ProcessingRange.setRange_from_to( minid, maxid );

        String mailPullLogData = app.processPoll( app.PROCESS_SENT , MMProducer.PUSH_EMAIL_AS_AVRO );

        return mailPullLogData;

    }

}
