package noteServer;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Timer;

import org.junit.After;
import org.junit.Test;

import authentication.Account;
import document.Document;
import io.AbstractDiskResource;

public class ServerArbiterTest {
	
	private ServerArbiter TEST;
	private Timer timer;
	private Account account1, account2;
	private final String EMIAL = "test@test.com";
	private final String EMIAL2 = "test2@test2.com";
	
	@After
	public void afterTest() {
		
		if(timer != null) {
			timer.cancel();
		}
		
		TEST.SAVE_FILE.delete();
	}

	@Test
	public void constructorTest() throws ClassNotFoundException, IOException {
		
		TEST = new ServerArbiter();
		
	}
	
	public void beforeTests() {
		account1 = new Account(EMIAL);
		account2 = new Account(EMIAL2);
	}
	

}
