package data;


//import com.fasterxml.jackson.databind.JsonMappingException;
//import com.fasterxml.jackson.dataformat.avro.AvroMapper;
//import com.fasterxml.jackson.dataformat.avro.AvroSchema;
//import com.fasterxml.jackson.dataformat.avro.schema.AvroSchemaGenerator;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

/**
 * We define a MMPojo to wrap the data and to generate a Schema out of hte data.
 */
public class MM {

/*
    public static class MMPojo extends data.MMPojo {

        public String asString() {
            return dateSent + " :: " + nrOfReciepients + " - (" + receiver + ") - [" + sender + "] - " + size + " - " + subject;
        }

    }
*/

    public MimeMessage message = null;

    public MMPojo data = null;

    //protected AvroSchema _mmSchema;

    //protected AvroMapper _sharedMapper;


    public MM() {
        data = new MMPojo();
        data.sender = "me@here.com";
        data.dateSent = System.currentTimeMillis()+"";
        data.size = "123";
        data.subject = "Hey, Ho!";
    }

    public MM( Message m ) {

        message = (MimeMessage) m;

        try {
            data = new MMPojo();

            data.sender = asString( m.getFrom() );
            data.dateSent = m.getSentDate().toString();
            data.size = "" + m.getSize();
            data.subject = m.getSubject();

            if ( m.getAllRecipients() != null ) {
                data.receiver = asString( m.getAllRecipients() );
                data.nrOfReciepients = m.getAllRecipients().length;
            }
        }
        catch (MessagingException e) {
            e.printStackTrace();
        }

    }

    public String asString(Address[] as) {
        StringBuffer sb = new StringBuffer();
        for( Address a : as) {
            sb.append( a.toString() + "; " );
        }
        return sb.toString();
    }

/*    protected AvroMapper getMapper() {
        if (_sharedMapper == null) {
            _sharedMapper = newMapper();
        }
        return _sharedMapper;
    }*/

    /*protected AvroMapper newMapper() {
        return new AvroMapper();
    }*/


/*    public void getAsAvroData() throws JsonMappingException {

        AvroMapper mapper = getMapper();
        AvroSchemaGenerator gen = new AvroSchemaGenerator();
        mapper.acceptJsonFormatVisitor(MMPojo.class, gen);
        AvroSchema schema = gen.getGeneratedSchema();

        String json = schema.getAvroSchema().toString(true);
        System.out.println( json );

        try {

            AvroSchema s2 = mapper.schemaFrom(json);

            // conversion into Avro Record has to be done here ...

        } catch (IOException e) {
            e.printStackTrace();
        }

    }*/

}

