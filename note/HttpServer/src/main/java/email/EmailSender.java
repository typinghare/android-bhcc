package email;

import java.util.Properties;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.lang3.StringUtils;

public class EmailSender {
	
	public static final String DEFUALT_FROM_EMAIL = "bhccdemo@gmail.com";
	public static final String DEFUALT_PASSWORD = "bhcc_test1234";
	
	private final String FROM_EMAIL, PASSWORD;
	
	public EmailSender(String fromEmail, String password) {
		
		fromEmail = StringUtils.defaultString(fromEmail);
		
		password = StringUtils.defaultString(password);
		
		if(!fromEmail.endsWith("@gmail.com")) {
			throw new IllegalArgumentException("The specified email must be a gmail account");
		}
		
		FROM_EMAIL = fromEmail;
		PASSWORD = password;
	}

	public void sendEmail(String subjectText, String messageText, String recipient) {
		
        Properties prop = new Properties();
		prop.put("mail.smtp.host", "smtp.gmail.com");
        prop.put("mail.smtp.port", "465");
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.socketFactory.port", "465");
        prop.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        
        Session session = Session.getInstance(prop,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(FROM_EMAIL, PASSWORD);
                    }
                });

        try {

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(FROM_EMAIL));
            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(recipient)
            );
            message.setSubject(subjectText);
            message.setText(messageText);

            Transport.send(message);

        } catch (MessagingException e) {
            e.printStackTrace();
        }
	}
}
