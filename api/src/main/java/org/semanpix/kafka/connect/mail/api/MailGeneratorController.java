package org.semanpix.kafka.connect.mail.api;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Random;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

@RestController
public class MailGeneratorController {

    private static Random random = new Random();

    @GetMapping("/random/mail")
    String randomMailMetadataAsMIMEString() {
        return newRandomMail();
    }

    private String newRandomMail() {

        String mailData = null;

        try {

            Properties props = System.getProperties();
            props.put("mail.host", "imap.gmail.com");
            props.put("mail.transport.protocol", "imap");

            Session mailSession = Session.getDefaultInstance(props, null);

            MimeMessage message = new MimeMessage(mailSession);
            message.setSubject("TESTMAIL " + random.nextInt() );

            InternetAddress[] a = new InternetAddress[1];
            a[0] = new InternetAddress();
            a[0].setAddress("they@somewhere.io");
            message.addFrom( a );

            InternetAddress b = new InternetAddress();
            b.setAddress("me@here.io");
            message.addRecipient( Message.RecipientType.TO, b );
            message.setContent( "MailContent ... " + random.nextDouble(), "text/html;charset=utf-8");

            // We serialize the message into a String using the built-in encoding
            //
            //    see also: https://stackoverflow.com/questions/17609046/how-to-serialize-a-mimemessage-instance
            //
            ByteArrayOutputStream result = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length = 0;

            message.writeTo( result );
            mailData = result.toString("UTF-8");

        } catch (IOException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return mailData;
    }

}
