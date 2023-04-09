package email;

import static org.junit.Assert.*;

import org.junit.Test;

public class EmailSenderTest {
	private final String TO_EMAIL = "harriswilliams64@gmail.com";

	@Test
	public void constructorTest() {
		new EmailSender(EmailSender.DEFUALT_FROM_EMAIL, EmailSender.DEFUALT_PASSWORD);
		
		try {
			new EmailSender("TEST", EmailSender.DEFUALT_PASSWORD);
			fail();
		} catch (IllegalArgumentException e) {}
	}
	
	@Test
	public void sendEmailTest() {
		EmailSender sender = new EmailSender(EmailSender.DEFUALT_FROM_EMAIL, EmailSender.DEFUALT_PASSWORD);
		
		sender.sendEmail("TEST EMAIL", "TEST MESSAGE", TO_EMAIL);
	}

}
