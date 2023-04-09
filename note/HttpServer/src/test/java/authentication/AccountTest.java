package authentication;

import static org.junit.Assert.*;

import org.apache.commons.codec.binary.StringUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;
import org.passay.EnglishCharacterData;

import httpServer.HTTPServer;
import httpServer.HandledServerException;
import junit.framework.Assert;

public class AccountTest {
	
	private String EMIAL = "test@test.com";
	
	@Test
	public void nameTest() {
		Account userAccount = new Account(EMIAL);
		
		String value = RandomStringUtils.randomAlphanumeric(10);
		
		userAccount.setFirstName(value);
	}

	@Test
	public void creationTest() {
		try {
			new Account("test@.com");
			fail();
		}catch (HandledServerException e) {
			assertEquals(e.getCode(), HTTPServer.BAD_REQUEST);
		}
		
		try {
			new Account("test(@t.com");
			fail();
		}catch (HandledServerException e) {
			assertEquals(e.getCode(), HTTPServer.BAD_REQUEST);
		}
		
		try {
			new Account("test@t.");
			fail();
		}catch (HandledServerException e) {
			assertEquals(e.getCode(), HTTPServer.BAD_REQUEST);
		}
		
		try {
			new Account("test@t.k");
			fail();
		}catch (HandledServerException e) {
			assertEquals(e.getCode(), HTTPServer.BAD_REQUEST);
		}
		
		try {
			new Account("test@");
			fail();
		}catch (HandledServerException e) {
			assertEquals(e.getCode(), HTTPServer.BAD_REQUEST);
		}
		
		try {
			new Account("te)st@t.com");
			fail();
		}catch (HandledServerException e) {
			assertEquals(e.getCode(), HTTPServer.BAD_REQUEST);
		}
		
		try {
			new Account("@t.com");
			fail();
		}catch (HandledServerException e) {
			assertEquals(e.getCode(), HTTPServer.BAD_REQUEST);
		}
		
		try {
			new Account("@");
			fail();
		}catch (HandledServerException e) {
			assertEquals(e.getCode(), HTTPServer.BAD_REQUEST);
		}
		
		try {
			new Account("");
			fail();
		}catch (HandledServerException e) {
			assertEquals(e.getCode(), HTTPServer.BAD_REQUEST);
		}
		
		try {
			new Account(" ");
			fail();
		}catch (HandledServerException e) {
			assertEquals(e.getCode(), HTTPServer.BAD_REQUEST);
		}
		
		new Account(EMIAL);
	}
	
	@Test
	public void setPasswordTest() {
		Account userAccount = new Account(EMIAL);
		
		try {
			userAccount.setPassword("A");
			fail();
		}catch (HandledServerException e) {
			assertTrue(
					e.getMessage().contains(EnglishCharacterData.LowerCase.getErrorCode()) &&
					e.getMessage().contains(EnglishCharacterData.Digit.getErrorCode()) &&
					e.getMessage().contains("TOO_SHORT"));
		}
		
		try {
			userAccount.setPassword("1");
			fail();
		}catch (HandledServerException e) {
			assertTrue(e.getMessage().contains(EnglishCharacterData.UpperCase.getErrorCode()) &&
					e.getMessage().contains(EnglishCharacterData.LowerCase.getErrorCode()) &&
					e.getMessage().contains("TOO_SHORT"));
		}
		
		try {
			userAccount.setPassword("!");
			fail();
		}catch (HandledServerException e) {
			assertTrue(e.getMessage().contains(EnglishCharacterData.UpperCase.getErrorCode()) &&
					e.getMessage().contains(EnglishCharacterData.LowerCase.getErrorCode()) &&
					e.getMessage().contains(EnglishCharacterData.Digit.getErrorCode()) &&
					e.getMessage().contains("TOO_SHORT"));
		}
		
	}
	
	@Test
	public void formateEmail() {
		assertTrue(EMIAL.equalsIgnoreCase(Account.formateEmail(EMIAL)));
	}

}
