package noteServer;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import authentication.AuthoTests;
import document.DocTests;
import document.DocumentManagerTest;
import io.AbstractDiskResourceTest;
import io.IOTests;

@RunWith(Suite.class)
@SuiteClasses({DocTests.class, AuthoTests.class, IOTests.class, ServerArbiterTest.class, NoteServerTest.class})
public class AllTests {

}
