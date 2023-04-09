package authentication;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ TokenManagerTest.class, TokenTest.class, AccountTest.class, AccountManagerTest.class})
public class AuthoTests {

}
