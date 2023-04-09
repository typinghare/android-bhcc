package document;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import com.google.gson.Gson;

public class DocumentTest {

	@Test
	public void gettertest() {
		Document document = new Document();
		
		String text = RandomStringUtils.randomAlphabetic(1200);
		String title = RandomStringUtils.randomAlphabetic(1200);

		document.setTitle(title);

		document.setText(text);
		
		assertNotNull(document.getID());
		
		assertNotNull(document.getCreationDate());
		
		assertEquals(text, document.getText());
		
		assertEquals(title, document.getTitle());
	
	}
	
	@Test
	public void jsonifyTest() {
		Document document = new Document();
		
		String text = RandomStringUtils.randomAlphabetic(1200);
		String title = RandomStringUtils.randomAlphabetic(1200);

		document.setTitle(title);

		document.setText(text);
		
		String json = new Gson().toJson(document);
		
		Map<String, Object> map = new Gson().fromJson(json, HashMap.class);
		
		assertEquals(document.getID().toString(), map.get(Document.ID).toString());
		
		assertEquals(Long.toString(document.getCreationDate().getTime()), Long.toString(((Number)map.get(Document.CREATION_DATE)).longValue()));
		
		assertEquals(text, map.get(Document.TEXT));
		
		assertEquals(title, map.get(Document.TITLE));
		
		Document document2 = new Gson().fromJson(json, Document.class);

		assertEquals(document.getID(), document2.getID());
		
		assertEquals(document.getCreationDate(), document2.getCreationDate());
		
		assertEquals(document.getText(), document2.getText());
		
		assertEquals(document.getTitle(), document2.getTitle());
		
		Document document3 = new Document(map);

		assertEquals(document.getID(), document3.getID());
		
		assertEquals(document.getCreationDate(), document3.getCreationDate());
		
		assertEquals(document.getText(), document3.getText());
		
		assertEquals(document.getTitle(), document3.getTitle());
	}

}
