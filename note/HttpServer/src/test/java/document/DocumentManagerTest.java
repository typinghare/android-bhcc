package document;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;

import authentication.Account;
import httpServer.HTTPServer;
import httpServer.HandledServerException;

public class DocumentManagerTest {

	private final String EMIAL = "test@test.com";
	private final String EMIAL2 = "test2@test2.com";

	@Test
	public void constructorTest() {
		new DocumentManager();
	}

	@Test
	public void getDocumentTest() throws HandledServerException, DocumentNotFoundException {
		DocumentManager manager = new DocumentManager();

		UUID uuid = UUID.randomUUID();

		try {
			manager.getDocument(EMIAL, uuid);
			fail();
		} catch (DocumentNotFoundException e) {
			assertEquals(uuid, e.getUuid());
		}

		Document document = new Document(RandomStringUtils.random(5));

		manager.setDocument(EMIAL, document);

		Document returnedDocument = manager.getDocument(EMIAL, document.getID());

		assertEquals(document, returnedDocument);

		assertSame(document, returnedDocument);

		try {
			manager.getDocument(EMIAL2, document.getID());
			fail();
		} catch (HandledServerException e) {
			assertEquals(HTTPServer.UNAUTHORIZED, e.getCode());
		}
	}


	@Test
	public void getDocumentsTest() throws HandledServerException, DocumentNotFoundException {
		DocumentManager manager = new DocumentManager();

		assertTrue(manager.getDocuments(EMIAL, DocumentManager.MINE).isEmpty());

		ArrayList<Document> notSharedDocs = new ArrayList<>();

		for(int i = 0; i < 10; i++) {
			Document document = new Document(RandomStringUtils.random(5));

			notSharedDocs.add(document);

			manager.setDocument(EMIAL, document);
		}

		List<Document> returnedDocs = manager.getDocuments(EMIAL, DocumentManager.MINE);

		assertTrue(returnedDocs.containsAll(notSharedDocs) && notSharedDocs.containsAll(returnedDocs));

		returnedDocs = manager.getDocuments(EMIAL, DocumentManager.MINE_AND_SHARED);

		assertTrue(returnedDocs.containsAll(notSharedDocs) && notSharedDocs.containsAll(returnedDocs));

		assertTrue(manager.getDocuments(EMIAL, DocumentManager.SHARED).isEmpty());

		ArrayList<Document> sharedDocs = new ArrayList<>();

		for(int i = 0; i < 10; i++) {
			Document document = new Document(RandomStringUtils.random(5));

			sharedDocs.add(document);

			manager.setDocument(EMIAL2, document);

			manager.setDocumentAccess(EMIAL2, document.getID(), Arrays.asList(EMIAL));
		}

		ArrayList<Document> notSharedDocs2 = new ArrayList<>();

		for(int i = 0; i < 10; i++) {
			Document document = new Document(RandomStringUtils.random(5));

			notSharedDocs2.add(document);

			manager.setDocument(EMIAL2, document);

		}

		ArrayList<Document> expectedSharedAndMineDocs = new ArrayList<>();

		expectedSharedAndMineDocs.addAll(sharedDocs);

		expectedSharedAndMineDocs.addAll(notSharedDocs);

		returnedDocs = manager.getDocuments(EMIAL, DocumentManager.SHARED);

		assertTrue(returnedDocs.containsAll(sharedDocs) && sharedDocs.containsAll(returnedDocs));

		returnedDocs = manager.getDocuments(EMIAL, DocumentManager.MINE_AND_SHARED);

		assertTrue(returnedDocs.containsAll(expectedSharedAndMineDocs) && expectedSharedAndMineDocs.containsAll(returnedDocs));

		returnedDocs = manager.getDocuments(EMIAL, DocumentManager.MINE);

		assertTrue(returnedDocs.containsAll(notSharedDocs) && notSharedDocs.containsAll(returnedDocs));

		returnedDocs = manager.getDocuments(EMIAL2, DocumentManager.SHARED);

		assertTrue(returnedDocs.isEmpty());

		returnedDocs = manager.getDocuments(EMIAL2, DocumentManager.MINE);

		assertTrue(returnedDocs.containsAll(notSharedDocs2) && returnedDocs.containsAll(sharedDocs));

		returnedDocs = manager.getDocuments(EMIAL2, DocumentManager.MINE_AND_SHARED);

		assertTrue(returnedDocs.containsAll(notSharedDocs2) && returnedDocs.containsAll(sharedDocs));
	}

	@Test
	public void deleteDocumentTest() throws HandledServerException, DocumentNotFoundException {
		DocumentManager manager = new DocumentManager();

		UUID uuid = UUID.randomUUID();

		manager.deleteDocument(EMIAL, uuid);

		Document document = new Document(RandomStringUtils.random(5));

		manager.setDocument(EMIAL, document);

		try {
			manager.deleteDocument(EMIAL2, document.getID());
			fail();
		} catch (HandledServerException e) {
			assertEquals(HTTPServer.UNAUTHORIZED, e.getCode());
		}

		manager.setDocumentAccess(EMIAL, document.getID(), Arrays.asList(EMIAL2));

		try {
			manager.deleteDocument(EMIAL2, document.getID());
			fail();
		} catch (HandledServerException e) {
			assertEquals(HTTPServer.UNAUTHORIZED, e.getCode());
		}

		manager.deleteDocument(EMIAL, document.getID());

		try {
			manager.getDocument(EMIAL, document.getID());
			fail();
		} catch (DocumentNotFoundException e) {
			assertEquals(document.getID(), e.getUuid());
		}
	}

	@Test
	public void setDocumentTest() throws HandledServerException, DocumentNotFoundException {
		DocumentManager manager = new DocumentManager();

		Document document = new Document(RandomStringUtils.random(5));

		manager.setDocument(EMIAL, document);

		try {

			document.setText("TEST");

			manager.setDocument(EMIAL2, document);
			fail();
		} catch (HandledServerException e) {
			assertEquals(HTTPServer.UNAUTHORIZED, e.getCode());
		}

		manager.setDocumentAccess(EMIAL, document.getID(), Arrays.asList(EMIAL2));

		manager.setDocument(EMIAL2, document);

		Document returnedDocument = manager.getDocument(EMIAL2, document.getID());

		assertEquals(document.getText(), returnedDocument.getText());

		document.setText("");

		manager.setDocument(EMIAL, document);

		returnedDocument = manager.getDocument(EMIAL, document.getID());

		assertEquals(document.getText(), returnedDocument.getText());

	}

	@Test
	public void setDocumentAccessTest() throws HandledServerException, DocumentNotFoundException {
		DocumentManager manager = new DocumentManager();

		Document document = new Document(RandomStringUtils.random(5));
		
		manager.setDocument(EMIAL, document);

		try {
			manager.getDocument(EMIAL2, document.getID());
			fail();
		} catch (HandledServerException e) {
			assertEquals(HTTPServer.UNAUTHORIZED, e.getCode());
		}
		
		manager.setDocumentAccess(EMIAL, document.getID(), Arrays.asList(EMIAL2));

		manager.getDocument(EMIAL2, document.getID());
		
		assertTrue(manager.getDocumentAccess(EMIAL, document.getID()).contains(Account.formateEmail(EMIAL2)));
		
		manager.setDocumentAccess(EMIAL, document.getID(), Arrays.asList());
		
		try {
			manager.getDocument(EMIAL2, document.getID());
			fail();
		} catch (HandledServerException e) {
			assertEquals(HTTPServer.UNAUTHORIZED, e.getCode());
		}
		
		manager.getDocument(EMIAL, document.getID());
		
		assertTrue(manager.getDocumentAccess(EMIAL, document.getID()).isEmpty());
	}
	
	
	
	@Test
	public void getDocumentAccessTest() throws HandledServerException, DocumentNotFoundException {
		DocumentManager manager = new DocumentManager();

		Document document = new Document(RandomStringUtils.random(5));
		
		Document document2 = new Document(RandomStringUtils.random(5));
		
		manager.setDocument(EMIAL, document);
		
		manager.setDocument(EMIAL, document2);
		
		assertTrue(manager.getDocumentAccess(EMIAL, document.getID()).isEmpty());
		
		assertTrue(manager.getDocumentAccess(EMIAL, document2.getID()).isEmpty());
		
		manager.setDocumentAccess(EMIAL, document.getID(), Arrays.asList(EMIAL2));
		
		assertTrue(manager.getDocumentAccess(EMIAL, document.getID()).contains(Account.formateEmail(EMIAL2)));
		
		assertTrue(manager.getDocumentAccess(EMIAL, document2.getID()).isEmpty());
		
		manager.setDocumentAccess(EMIAL, document.getID(), Arrays.asList());
		
		assertTrue(manager.getDocumentAccess(EMIAL, document.getID()).isEmpty());
		
		assertTrue(manager.getDocumentAccess(EMIAL, document2.getID()).isEmpty());
	}

}
