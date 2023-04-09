package authentication;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import httpServer.HTTPServer;
import httpServer.HandledServerException;

public class TokenManagerTest {
	
	private final String EMIAL = "test@test.com";
	private final String CORRECT_PASSWORD = "RightPassword123!";
	private final String WRONT_PASSWORD = "WRONG";
	
	@Test
	public void creationTest() {
		new TokenManagerTest();
	}

	@Test
	public void authenticateTest() throws InterruptedException{
		Account userAccount = new Account(EMIAL);
		
		userAccount.setPassword(CORRECT_PASSWORD);
		
		TokenManager authoManager = new TokenManager();
		
		try {
			authoManager.authenticate(userAccount, WRONT_PASSWORD, 2, TimeUnit.SECONDS);
			fail();
		} catch (HandledServerException e) {
			assertEquals(e.getCode(), HTTPServer.UNAUTHORIZED);
		}
		
		Token token = authoManager.authenticate(userAccount, CORRECT_PASSWORD, 50, TimeUnit.MILLISECONDS);
		
		assertNotNull(token);
		
		assertFalse(token.isExpired());
		
		Thread.sleep(100);
		
		assertTrue(token.isExpired());
		
		for(int i = 0; i < 10; i++) {
			authoManager.authenticate(userAccount, CORRECT_PASSWORD, 50, TimeUnit.MILLISECONDS);
		}
	}
	
	@Test
	public void refreshTest() throws InterruptedException{
		
		Account userAccount = new Account(EMIAL);
		
		userAccount.setPassword(CORRECT_PASSWORD);
		
		TokenManager authoManager = new TokenManager();
		
		Token token1 = authoManager.authenticate(userAccount, CORRECT_PASSWORD, 50, TimeUnit.MILLISECONDS);
		
		Token token2 = authoManager.refresh(token1.token, 50, TimeUnit.MILLISECONDS);
		
		assertNotEquals(token1, token2);
		
		assertFalse(token1.isExpired());
		
		assertFalse(token2.isExpired());
		
		assertFalse(authoManager.isTokenValid(token1.token));
		
		assertTrue(authoManager.isTokenValid(token2.token));
		
		Thread.sleep(100);
		
		try {
			authoManager.refresh(token1.token, 100, TimeUnit.MILLISECONDS);
			fail();
		} catch (HandledServerException e) {
			assertEquals(e.getCode(), HTTPServer.UNAUTHORIZED);
		}
		
		try {
			authoManager.refresh(token2.token, 100, TimeUnit.MILLISECONDS);
			fail();
		} catch (HandledServerException e) {
			assertEquals(e.getCode(), HTTPServer.UNAUTHORIZED);
		}
	}
	
	@Test
	public void signOutWithTokenTest() throws InterruptedException{
		
		Account userAccount = new Account(EMIAL);
		
		userAccount.setPassword(CORRECT_PASSWORD);
		
		TokenManager authoManager = new TokenManager();
		
		Token token1 = authoManager.authenticate(userAccount, CORRECT_PASSWORD, 100, TimeUnit.MILLISECONDS);
		
		assertTrue(authoManager.isTokenValid(token1.token));
		
		authoManager.signOutWithToken(token1.token);
		
		assertFalse(authoManager.isTokenValid(token1.token));
	}
	
	@Test
	public void signOutWithUserName() throws InterruptedException{
		Account userAccount = new Account(EMIAL);
		
		userAccount.setPassword(CORRECT_PASSWORD);
		
		TokenManager authoManager = new TokenManager();
		
		List<String> tokens = new ArrayList<>();
		
		for(int i = 0; i < 10; i++) {
			tokens.add(authoManager.authenticate(userAccount, CORRECT_PASSWORD, 10, TimeUnit.SECONDS).token);
		}
		
		tokens.forEach(token -> {
			assertTrue(authoManager.isTokenValid(token));
		});
				
		authoManager.signOutWithUserName(EMIAL);
		
		tokens.forEach(token -> {
			assertFalse(authoManager.isTokenValid(token));
		});
		
	}
	
	@Test
	public void isTokenValidTest() throws InterruptedException{
		
		Account userAccount = new Account(EMIAL);
		
		userAccount.setPassword(CORRECT_PASSWORD);
		
		TokenManager authoManager = new TokenManager();
		
		Token token1 = authoManager.authenticate(userAccount, CORRECT_PASSWORD, 100, TimeUnit.MILLISECONDS);
		
		assertTrue(authoManager.isTokenValid(token1.token));
		
		Thread.sleep(150);
		
		assertFalse(authoManager.isTokenValid(token1.token));
		
		token1 = authoManager.authenticate(userAccount, CORRECT_PASSWORD, 100, TimeUnit.SECONDS);
		
		Token token2 = authoManager.refresh(token1.token, 100, TimeUnit.SECONDS);
				
		assertFalse(authoManager.isTokenValid(token1.token));
		
		assertTrue(authoManager.isTokenValid(token2.token));
		
		token1 = authoManager.authenticate(userAccount, CORRECT_PASSWORD, 100, TimeUnit.SECONDS);
		
		authoManager.signOutWithToken(token1.token);
		
		assertFalse(authoManager.isTokenValid(token1.token));
		
		authoManager.signOutWithUserName(EMIAL);
		
		assertFalse(authoManager.isTokenValid(token2.token));
		
	}

}
