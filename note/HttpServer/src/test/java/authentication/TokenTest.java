package authentication;

import static org.junit.Assert.*;

import java.lang.reflect.Field;
import java.util.concurrent.TimeUnit;

import org.junit.Test;


public class TokenTest {

	@Test
	public void creationTest() {
		try {
			new Token("", 1, TimeUnit.DAYS);
			fail();
		} catch (Exception e) {

		}

		try {
			new Token(" ", 1, TimeUnit.DAYS);
			fail();
		} catch (Exception e) {

		}

		try {
			new Token("t", 0, TimeUnit.DAYS);
			fail();
		} catch (Exception e) {

		}

		try {
			new Token("t", -1, TimeUnit.DAYS);
			fail();
		} catch (Exception e) {

		}

		try {
			new Token("t", 1, null);
			fail();
		} catch (Exception e) {

		}

		new Token("t", 1, TimeUnit.DAYS);
	}

	@Test
	public void equalityTest() throws Exception {

		Token token1 = new Token("t", 1, TimeUnit.DAYS);

		Token token2 = new Token("t", 1, TimeUnit.DAYS);


		Field f = token1.getClass().getDeclaredField("token");
		f.setAccessible(true);
		f.set(token2, token1.token);

		f = token1.getClass().getDeclaredField("creation_date");
		f.setAccessible(true);
		f.set(token2, token1.creation_date);

		assertEquals(token1, token2);

		Token token3 = new Token("t", 1, TimeUnit.DAYS);

		assertNotEquals(token1, token3);

	}
	
	@Test
	public void hashTest() throws Exception {

		Token token1 = new Token("t", 1, TimeUnit.DAYS);

		Token token2 = new Token("t", 1, TimeUnit.DAYS);


		Field f = token1.getClass().getDeclaredField("token");
		f.setAccessible(true);
		f.set(token2, token1.token);

		assertEquals(token1.hashCode(), token2.hashCode());

		Token token3 = new Token("t", 1, TimeUnit.DAYS);

		assertNotEquals(token1.hashCode(), token3.hashCode());

	}
	
	@Test
	public void expirationTest() throws Exception {

		Token token1 = new Token("t", 1, TimeUnit.SECONDS);

		assertFalse(token1.isExpired());
		
		Thread.sleep(1000);
		
		assertTrue(token1.isExpired());

	}

}
