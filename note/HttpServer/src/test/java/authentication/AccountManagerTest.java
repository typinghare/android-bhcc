package authentication;

import static org.junit.Assert.*;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;

import httpServer.HTTPServer;
import httpServer.HandledServerException;

public class AccountManagerTest {
	
	private final String EMIAL = "test@test.com";
	
	private final String EMIAL2 = "test2@test2.com";

	@Test
	public void constructorTest() {
		new AccountManager();
	}
	
	@Test
	public void createAccountTest() {
		AccountManager manager = new AccountManager();
		
		Account account = manager.createAccount(EMIAL);
		
		assertNotNull(account);
		
		assertEquals(account.getEmail(), Account.formateEmail(EMIAL));
		
		try {
			manager.createAccount(EMIAL);
			fail();
		}catch (HandledServerException e) {
			assertEquals(e.getCode(), HTTPServer.BAD_REQUEST);
		}
	}
	
	@Test
	public void getAccountTest() throws UserAccountNotFoundException{
		AccountManager manager = new AccountManager();
		
		Account account1 = manager.createAccount(EMIAL);
		
		Account account2 = manager.getAccount(EMIAL);
		
		assertEquals(account1, account2);
		
		assertSame(account1, account2);
		
		Account account3 = manager.createAccount(EMIAL2);
		
		assertNotEquals(account1, account3);
		
		assertNotSame(account1, account3);
		
		String randomEmail = RandomStringUtils.randomAlphabetic(EMIAL.length()+3);
		
		try {
			manager.getAccount(randomEmail);
			fail();
		}catch (UserAccountNotFoundException e) {
			assertEquals(e.getEmail(), Account.formateEmail(randomEmail));
		}
	}
	
	@Test
	public void accountExistsTest() throws UserAccountNotFoundException{
		
		AccountManager manager = new AccountManager();
		
		manager.createAccount(EMIAL);
		
		assertTrue(manager.accountExists(EMIAL));
		
		String randomEmail = RandomStringUtils.randomAlphabetic(EMIAL.length()+3);
		
		assertFalse(manager.accountExists(randomEmail));
		
		manager.createAccount(EMIAL2);
		
		assertTrue(manager.accountExists(EMIAL2));
		
		assertTrue(manager.accountExists(EMIAL));
	}
	
	@Test
	public void deleteAccountTest() throws UserAccountNotFoundException{
		
		AccountManager manager = new AccountManager();
		
		manager.createAccount(EMIAL);
		
		assertTrue(manager.accountExists(EMIAL));
		
		manager.deleteAccount(EMIAL2);
		
		assertTrue(manager.accountExists(EMIAL));
		
		manager.deleteAccount(EMIAL);
		
		assertFalse(manager.accountExists(EMIAL));
		
		manager.createAccount(EMIAL);
		
		manager.createAccount(EMIAL2);
		
		assertTrue(manager.accountExists(EMIAL));
		
		assertTrue(manager.accountExists(EMIAL2));
		
		manager.deleteAccount(EMIAL);
		
		assertFalse(manager.accountExists(EMIAL));
		
		assertTrue(manager.accountExists(EMIAL2));
		
		manager.deleteAccount(EMIAL2);
		
		assertFalse(manager.accountExists(EMIAL2));
	}
	

}
