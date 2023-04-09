package noteServer;

import static org.junit.Assert.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchEvent.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.api.client.util.Data;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import authentication.Account;
import authentication.AuthoManager;
import authentication.Token;
import authentication.TokenManager;
import document.Document;
import document.DocumentManager;
import document.DocumentNotFoundException;
import httpServer.HandledServerException;
import rawhttp.core.RawHttp;
import rawhttp.core.RawHttpHeaders;
import rawhttp.core.RawHttpHeaders.Builder;
import rawhttp.core.RawHttpRequest;
import rawhttp.core.RawHttpResponse;
import rawhttp.core.client.TcpRawHttpClient;

public class NoteServerTest {

	NoteServer noteServer;
	TcpRawHttpClient client;
	Gson gson = new GsonBuilder().setPrettyPrinting().create();
	PrintWriter writer;
	RawHttp http;
	String url;

	final static String EMAIL = "bot@graderthan.com";
	final static String EMAIL2 = "bot2@graderthan.com";
	final static String FIRST_NAME = "Tony";
	final static String LAST_NAME = "Stark";
	final static String EXTRA = "//Your own custom information";
	final static String GOOD_PASSWORD = "Password123";
	final static String WRONG_PASSWORD = "Password1234";
	final static String REQUEST_TITLE = "REQUEST BODY";
	final static String RESPONSE_TITLE = "RESPONCE BODY";

	@Before
	public void beforeTest() throws Exception {
		http = new RawHttp();
		client = new TcpRawHttpClient();
		noteServer = new NoteServer(12345, 1, TimeUnit.SECONDS);
		noteServer.setVerbrose(true);
		url = noteServer.getEndPoint();
	}

	@After
	public void afterTest() throws Exception {
		if(noteServer != null) {
			noteServer.stop();
			Files.deleteIfExists(noteServer.getArbiter().SAVE_FILE.toPath());
		}

		if(client != null)
			client.close();

		if(writer != null)
			writer.close();
	}

	@Test
	public void createAccountTest() throws Exception {

		Map<String, Object> map = new HashMap<>();

		map.put(ServerParams.FIRST_NAME, FIRST_NAME);
		map.put(ServerParams.METHOD, "createAccount");
		map.put(ServerParams.LAST_NAME, LAST_NAME);
		map.put(ServerParams.EMAIL, EMAIL);
		map.put(ServerParams.EXTRA, EXTRA);

		String mapJson = gson.toJson(map);

		String filePath = "." + File.separator + "JsonExamples" + File.separator + "createAccount.json";

		new File("." + File.separator + "JsonExamples" ).mkdirs();

		writer = new PrintWriter(filePath, "UTF-8");

		writer.println("REQUEST BODY");

		writer.println(mapJson);

		RawHttpRequest request = http.parseRequest("\nPOST "+url+"\n"+
				"Content-Type: application/json\n" +
				"Content-Length: "+mapJson.length()+"\n" +
				"Date: "+ new Date()+"\n"+
				"\n"
				+mapJson);

		RawHttpResponse<?> response = client.send(request);

		assertTrue(response.getStatusCode() >= 200 && response.getStatusCode() < 300);

		String json = response.getBody().get().decodeBodyToString(StandardCharsets.UTF_8);

		writer.println("RESPONCE BODY");

		writer.println(json);

		Map<String, Object> jsonMap = gson.fromJson(json, HashMap.class);

		assertEquals(FIRST_NAME, jsonMap.get(ServerParams.FIRST_NAME));

		assertEquals(LAST_NAME, jsonMap.get(ServerParams.LAST_NAME));

		assertEquals(EXTRA, jsonMap.get(ServerParams.EXTRA));

		assertEquals(EMAIL, jsonMap.get(ServerParams.EMAIL).toString().toLowerCase());

		assertTrue(noteServer.getArbiter().getCurrentValue().accountExists(Account.formateEmail(EMAIL)));
	}

	private void writeToFile(File file, String title, String toWrite) throws IOException {

		new File(file.getParentFile().getPath().toString()).mkdirs();

		//PrintWriter writer = new PrintWriter(file, "UTF-8");

		FileWriter writer = new FileWriter(file, true);

		writer.write(title+"\n\n");

		writer.write(toWrite+"\n\n");

		writer.flush();

		writer.close();
	}

	private Account createAccount(int accountStatus) {
		Account account;
		try {
			account = noteServer.getArbiter().getCurrentValue().getAuthoManager().getAccountManager().createAccount(EMAIL);
		}catch (HandledServerException e) {
			account = noteServer.getArbiter().getCurrentValue().getAuthoManager().getAccountManager().createAccount(EMAIL2);
		}
		account.setFirstName(FIRST_NAME);
		account.setLastName(LAST_NAME);
		account.setExtra(EXTRA);
		account.setAccountStatus(accountStatus);

		if(accountStatus == Account.CONFIRMED) {
			account.registerAccount(account.getTempPassword(), GOOD_PASSWORD);
		}

		return account;
	}

	private Token createAndAutho(long lifeSpan, TimeUnit timeUnit) {
		Account account = createAccount(Account.CONFIRMED);

		return noteServer.getArbiter().getCurrentValue().getAuthoManager().getTokenManager().authenticate(account, account.getPassword(), lifeSpan, timeUnit);
	}

	private Document createDocument() {

		Document document = new Document();

		document.setTitle(RandomStringUtils.randomAlphabetic(12));

		document.setText(RandomStringUtils.randomAlphabetic(1200));

		return document;

	}

	public RawHttpResponse<?> sendRequest(String body) throws IOException{
		return sendRequest(body, "");
	}

	public RawHttpResponse<?> sendRequest(String body, String authoToken) throws IOException{
		client = new TcpRawHttpClient();
				
		RawHttpRequest request = http.parseRequest("\nPOST "+url+"\n"+
				"Content-Type: application/json\n" +
				"Content-Length: "+body.length()+"\n" +
				"Date: "+ new Date()+"\n"+
				ServerParams.TOKEN+": "+ StringUtils.defaultString(authoToken)+"\n"+
				"\n"
				+body);

		return client.send(request);
	}

	@Test 
	public void registerAccount_401_Test() throws Exception {

		Account account = createAccount(Account.UNREGISTERED);

		Map<String, Object> map = new HashMap<>();

		map.put(ServerParams.METHOD, "registerAccount");
		map.put(ServerParams.EMAIL, EMAIL);
		map.put(ServerParams.TEMP_PWD, WRONG_PASSWORD);
		map.put(ServerParams.PWD, "");

		String mapJson = gson.toJson(map);

		RawHttpResponse<?> response = sendRequest(mapJson);

		assertTrue(response.getStatusCode() == 401);

		String json = response.getBody().get().decodeBodyToString(StandardCharsets.UTF_8);

		assertTrue(json.contains(TokenManager.WRONG_USER_OR_PW_MSG));
	}

	@Test 
	public void registerAccount_400_Test() throws Exception {

		Account account = createAccount(Account.UNREGISTERED);

		Map<String, Object> map = new HashMap<>();

		map.put(ServerParams.METHOD, "registerAccount");
		map.put(ServerParams.EMAIL, EMAIL);
		map.put(ServerParams.TEMP_PWD, account.getTempPassword());
		map.put(ServerParams.PWD, "test");

		String mapJson = gson.toJson(map);

		RawHttpResponse<?> response = sendRequest(mapJson);

		assertEquals(400, response.getStatusCode());
	}

	@Test 
	public void registerAccount_200_Test() throws Exception {

		Account account = createAccount(Account.UNREGISTERED);

		Map<String, Object> map = new HashMap<>();

		map.put(ServerParams.METHOD, "registerAccount");
		map.put(ServerParams.EMAIL, EMAIL);
		map.put(ServerParams.TEMP_PWD, account.getTempPassword());
		map.put(ServerParams.PWD, GOOD_PASSWORD);

		String mapJson = gson.toJson(map);

		writeToFile(new File("." + File.separator + "JsonExamples" + File.separator + "registerAccount.json"), REQUEST_TITLE, mapJson);

		RawHttpResponse<?> response = sendRequest(mapJson);

		assertEquals(200, response.getStatusCode());

		writeToFile(new File("." + File.separator + "JsonExamples" + File.separator + "registerAccount.json"), RESPONSE_TITLE, "NO CONTENT");
	}

	@Test
	public void forgotPassword_200_0_Test() throws Exception {
		Account account = createAccount(Account.CONFIRMED);

		Map<String, Object> map = new HashMap<>();

		map.put(ServerParams.METHOD, "forgotPassword");
		map.put(ServerParams.EMAIL, EMAIL);

		String mapJson = gson.toJson(map);

		writeToFile(new File("." + File.separator + "JsonExamples" + File.separator + "forgotPassword.json"), REQUEST_TITLE, mapJson);

		RawHttpResponse<?> response = sendRequest(mapJson);

		assertEquals(200, response.getStatusCode());

	}

	@Test
	public void forgotPassword_200_1_Test() throws Exception {
		Account account = createAccount(Account.CONFIRMED);

		Map<String, Object> map = new HashMap<>();

		map.put(ServerParams.METHOD, "forgotPassword");
		map.put(ServerParams.EMAIL, "Test");

		String mapJson = gson.toJson(map);

		RawHttpResponse<?> response = sendRequest(mapJson);

		assertEquals(200, response.getStatusCode());
	}


	@Test
	public void authenticate_200_Test() throws Exception {
		Account account = createAccount(Account.CONFIRMED);

		Map<String, Object> map = new HashMap<>();

		map.put(ServerParams.METHOD, "authenticate");
		map.put(ServerParams.EMAIL, EMAIL);
		map.put(ServerParams.PWD, GOOD_PASSWORD);
		map.put(ServerParams.TIME_SPAN, 1);
		map.put(ServerParams.TIME_UNIT, TimeUnit.SECONDS.name());

		String mapJson = gson.toJson(map);

		writeToFile(new File("." + File.separator + "JsonExamples" + File.separator + "authenticate.json"), REQUEST_TITLE, mapJson);

		RawHttpResponse<?> response = sendRequest(mapJson);

		String json = response.getBody().get().decodeBodyToString(StandardCharsets.UTF_8);

		assertEquals(200, response.getStatusCode());

		writeToFile(new File("." + File.separator + "JsonExamples" + File.separator + "authenticate.json"), RESPONSE_TITLE, json);

		Map<String, Object> jsonMap = gson.fromJson(json, HashMap.class);

		assertNotNull(jsonMap.get("token"));

		assertNotNull(jsonMap.get("owner"));

		assertNotNull(jsonMap.get("ttl"));

		assertNotNull(jsonMap.get("creation_date"));

		assertTrue(noteServer.getArbiter().getCurrentValue().getAuthoManager().getTokenManager().isTokenValid(jsonMap.get("token").toString()));

		map = new HashMap<>();

		map.put(ServerParams.METHOD, "getAccount");
		map.put(ServerParams.TOKEN, jsonMap);
		map.put(ServerParams.EMAIL, EMAIL);

		mapJson = gson.toJson(map);

		response = sendRequest(mapJson, jsonMap.get("token").toString());

		String accountPreview = response.getBody().get().decodeBodyToString(StandardCharsets.UTF_8);

		assertEquals(200, response.getStatusCode());

		Thread.sleep(1000);

		map = new HashMap<>();

		map.put(ServerParams.METHOD, "getAccount");
		map.put(ServerParams.TOKEN, jsonMap);
		map.put(ServerParams.EMAIL, EMAIL);

		mapJson = gson.toJson(map);

		response = sendRequest(mapJson);

		assertEquals(401, response.getStatusCode());

	}

	@Test
	public void authenticate_401_0_Test() throws Exception {
		Account account = createAccount(Account.CONFIRMED);

		Map<String, Object> map = new HashMap<>();

		map.put(ServerParams.METHOD, "authenticate");
		map.put(ServerParams.EMAIL, "Test");
		map.put(ServerParams.PWD, GOOD_PASSWORD);
		map.put(ServerParams.TIME_SPAN, 500);
		map.put(ServerParams.TIME_UNIT, TimeUnit.MILLISECONDS.name());

		String mapJson = gson.toJson(map);

		RawHttpResponse<?> response = sendRequest(mapJson);

		String json = response.getBody().get().decodeBodyToString(StandardCharsets.UTF_8);

		assertEquals(401, response.getStatusCode());

		assertTrue(json.contains(TokenManager.WRONG_USER_OR_PW_MSG));
	}

	@Test
	public void authenticate_401_1_Test() throws Exception {
		Account account = createAccount(Account.CONFIRMED);

		Map<String, Object> map = new HashMap<>();

		map.put(ServerParams.METHOD, "authenticate");
		map.put(ServerParams.EMAIL, EMAIL);
		map.put(ServerParams.PWD, WRONG_PASSWORD);
		map.put(ServerParams.TIME_SPAN, 500);
		map.put(ServerParams.TIME_UNIT, TimeUnit.MILLISECONDS.name());

		String mapJson = gson.toJson(map);

		RawHttpResponse<?> response = sendRequest(mapJson);

		String json = response.getBody().get().decodeBodyToString(StandardCharsets.UTF_8);

		assertEquals(401, response.getStatusCode());

		assertTrue(json.contains(TokenManager.WRONG_USER_OR_PW_MSG));
	}

	@Test
	public void getAccountTest() throws Exception {
		Map<String, Object> map = new HashMap<>();

		Token token = createAndAutho(1L, TimeUnit.SECONDS);

		Map<String, Object> jsonMap;

		map.put(ServerParams.METHOD, "getAccount");
		map.put(ServerParams.EMAIL, EMAIL);

		String mapJson = gson.toJson(map);

		writeToFile(new File("." + File.separator + "JsonExamples" + File.separator + "getAccount.json"), REQUEST_TITLE, mapJson);

		RawHttpResponse<?> response = sendRequest(mapJson, token.token);

		assertEquals(200, response.getStatusCode());

		String json = response.getBody().get().decodeBodyToString(StandardCharsets.UTF_8);

		writeToFile(new File("." + File.separator + "JsonExamples" + File.separator + "getAccount.json"), RESPONSE_TITLE, json);

		jsonMap = gson.fromJson(json, HashMap.class);

		assertEquals(FIRST_NAME, jsonMap.get(ServerParams.FIRST_NAME));

		assertEquals(LAST_NAME, jsonMap.get(ServerParams.LAST_NAME));

		assertEquals(EXTRA, jsonMap.get(ServerParams.EXTRA));

		assertEquals(EMAIL, jsonMap.get(ServerParams.EMAIL).toString().toLowerCase());

		map = new HashMap<>();

		map.put(ServerParams.METHOD, "getAccount");
		map.put(ServerParams.EMAIL, "test");

		mapJson = gson.toJson(map);

		response = sendRequest(mapJson, token.token);

		json = response.getBody().get().decodeBodyToString(StandardCharsets.UTF_8);

		assertEquals(400, response.getStatusCode());

	}

	@Test
	public void deleteAccountTest() throws Exception {
		Token token = createAndAutho(1L, TimeUnit.SECONDS);

		Map<String, Object> map = new HashMap<>();

		Map<String, Object> jsonMap;

		map.put(ServerParams.METHOD, "deleteAccount");

		String mapJson = gson.toJson(map);

		writeToFile(new File("." + File.separator + "JsonExamples" + File.separator + "deleteAccount.json"), REQUEST_TITLE, mapJson);

		RawHttpResponse<?> response = sendRequest(mapJson, token.token);

		assertEquals(200, response.getStatusCode());

		writeToFile(new File("." + File.separator + "JsonExamples" + File.separator + "authenticate.json"), RESPONSE_TITLE, "NO CONTENT");

		assertFalse(noteServer.getArbiter().getCurrentValue().getAuthoManager().getAccountManager().accountExists(EMAIL));

		map.put(ServerParams.METHOD, "deleteAccount");

		mapJson = gson.toJson(map);

		response = sendRequest(mapJson, token.token+"l");

		assertEquals(401, response.getStatusCode());
	}

	@Test
	public void setAccountTest() throws Exception {
		Token token = createAndAutho(1L, TimeUnit.SECONDS);

		Map<String, Object> map = new HashMap<>();

		Map<String, Object> jsonMap;

		Account account = new Account(EMAIL);

		account.setFirstName(RandomStringUtils.randomAlphabetic(12));
		account.setLastName(RandomStringUtils.randomAlphabetic(12));
		account.setExtra(RandomStringUtils.randomAlphabetic(12));

		map.put(ServerParams.METHOD, "setAccount");
		map.put(ServerParams.ACCOUNT, account);

		String mapJson = gson.toJson(map);

		writeToFile(new File("." + File.separator + "JsonExamples" + File.separator + "setAccount.json"), REQUEST_TITLE, mapJson);

		RawHttpResponse<?> response = sendRequest(mapJson, token.token);

		assertEquals(200, response.getStatusCode());

		writeToFile(new File("." + File.separator + "JsonExamples" + File.separator + "setAccount.json"), RESPONSE_TITLE, "NO CONTENT");

		Account accountInternal = noteServer.getArbiter().getCurrentValue().getAuthoManager().getAccountManager().getAccount(EMAIL);

		assertEquals(account.getFirstName(), accountInternal.getFirstName());

		assertEquals(account.getLastName(), accountInternal.getLastName());

		assertEquals(account.getExtra(), accountInternal.getExtra());

		map.put(ServerParams.METHOD, "setAccount");
		map.put(ServerParams.ACCOUNT, "bingo");

		mapJson = gson.toJson(map);

		response = sendRequest(mapJson, token.token);

		assertEquals(400, response.getStatusCode());
	}

	@Test
	public void signOutTest() throws Exception {
		Token token = createAndAutho(1L, TimeUnit.SECONDS);

		Map<String, Object> map = new HashMap<>();

		Map<String, Object> jsonMap;

		map.put(ServerParams.METHOD, "signOut");

		String mapJson = gson.toJson(map);

		writeToFile(new File("." + File.separator + "JsonExamples" + File.separator + "signOut.json"), REQUEST_TITLE, mapJson);

		RawHttpResponse<?> response = sendRequest(mapJson, token.token);

		map = new HashMap<>();

		map.put(ServerParams.METHOD, "getAccount");
		map.put(ServerParams.EMAIL, EMAIL);

		mapJson = gson.toJson(map);

		response = sendRequest(mapJson, token.token);

		assertEquals(401, response.getStatusCode());
	}

	@Test
	public void signOut_401_Test() throws Exception {
		Token token = createAndAutho(1L, TimeUnit.SECONDS);

		Map<String, Object> map = new HashMap<>();

		Map<String, Object> jsonMap;

		map.put(ServerParams.METHOD, "signOut");

		String mapJson = gson.toJson(map);

		RawHttpResponse<?> response = sendRequest(mapJson, token.token+"l");

		assertEquals(401, response.getStatusCode());

	}

	/*
	setDocument
	getDocument
	getDocuments
	deleteDocument
	 */
	@Test
	public void documentTests() throws IOException, HandledServerException, DocumentNotFoundException, ClassNotFoundException {
		Token token = createAndAutho(1L, TimeUnit.MINUTES);

		String email = noteServer.getArbiter().getCurrentValue().getAccountWithToken(token.token);

		Document document1 = createDocument();

		Document document2 = createDocument();

		Map<String, Object> map = new HashMap<>();

		Map<String, Object> jsonMap;

		//________________setDocument________________//

		map.put(ServerParams.METHOD, "setDocument");
		map.put(ServerParams.DOCUMENT, document1);

		String requestJson = gson.toJson(map);

		writeToFile(new File("." + File.separator + "JsonExamples" + File.separator + "setDocument.json"), REQUEST_TITLE, requestJson);
		
		RawHttpResponse<?> response = sendRequest(requestJson, token.token);

		writeToFile(new File("." + File.separator + "JsonExamples" + File.separator + "setDocument.json"), RESPONSE_TITLE, "NO CONTENT");

		assertEquals(200, response.getStatusCode());

		Document serverInternal = noteServer.getArbiter().getCurrentValue().getDocumentManager().getDocument(email, document1.getID());

		assertEquals(document1.getID(), serverInternal.getID());

		assertEquals(document1.getTitle(), serverInternal.getTitle());

		assertEquals(document1.getText(), serverInternal.getText());

		assertEquals(document1.getCreationDate(), serverInternal.getCreationDate());
		
		map.put(ServerParams.METHOD, "setDocument");
		map.put(ServerParams.DOCUMENT, document1);
		
		document1.setText(RandomStringUtils.randomAlphabetic(120));
		
		document1.setTitle(RandomStringUtils.randomAlphabetic(120));

		requestJson = gson.toJson(map);

		writeToFile(new File("." + File.separator + "JsonExamples" + File.separator + "setDocument.json"), REQUEST_TITLE, requestJson);

		response = sendRequest(requestJson, token.token);

		writeToFile(new File("." + File.separator + "JsonExamples" + File.separator + "setDocument.json"), RESPONSE_TITLE, "NO CONTENT");

		assertEquals(200, response.getStatusCode());

		serverInternal = noteServer.getArbiter().getCurrentValue().getDocumentManager().getDocument(email, document1.getID());

		assertEquals(document1.getID(), serverInternal.getID());

		assertEquals(document1.getTitle(), serverInternal.getTitle());

		assertEquals(document1.getText(), serverInternal.getText());

		assertEquals(document1.getCreationDate(), serverInternal.getCreationDate());
		
		noteServer.getArbiter().save();
				
		noteServer.getArbiter().load();
		
		serverInternal = noteServer.getArbiter().getCurrentValue().getDocumentManager().getDocument(email, document1.getID());

		assertEquals(document1.getID(), serverInternal.getID());

		assertEquals(document1.getTitle(), serverInternal.getTitle());

		assertEquals(document1.getText(), serverInternal.getText());

		assertEquals(document1.getCreationDate(), serverInternal.getCreationDate());
		

		//_______________getDocument_________________//

		map.clear();

		map.put(ServerParams.METHOD, "getDocument");
		map.put(ServerParams.DOCUMENT_ID, document1.getID());

		requestJson = gson.toJson(map);

		writeToFile(new File("." + File.separator + "JsonExamples" + File.separator + "getDocument.json"), REQUEST_TITLE, requestJson);

		response = sendRequest(requestJson, token.token);

		assertEquals(200, response.getStatusCode());

		String responceJson = response.getBody().get().decodeBodyToString(StandardCharsets.UTF_8);

		writeToFile(new File("." + File.separator + "JsonExamples" + File.separator + "getDocument.json"), RESPONSE_TITLE, responceJson);

		Map<String, Object> responseDoc = gson.fromJson(responceJson, HashMap.class);

		assertEquals(document1.getID().toString(), responseDoc.get(Document.ID).toString());

		assertEquals(document1.getTitle(), responseDoc.get(Document.TITLE));

		assertEquals(document1.getText(), responseDoc.get(Document.TEXT));
		
		assertEquals(document1.getCreationDate().getTime(), ((Number)responseDoc.get(Document.CREATION_DATE)).longValue());

		//_______________getDocuments_________________//

		noteServer.getArbiter().getCurrentValue().getDocumentManager().setDocument(email, document2);

		map.clear();

		map.put(ServerParams.METHOD, "getDocuments");
		map.put(ServerParams.SCOPE, DocumentManager.MINE);

		requestJson = gson.toJson(map);

		writeToFile(new File("." + File.separator + "JsonExamples" + File.separator + "getDocuments.json"), REQUEST_TITLE, requestJson);

		response = sendRequest(requestJson, token.token);

		assertEquals(200, response.getStatusCode());

		responceJson = response.getBody().get().decodeBodyToString(StandardCharsets.UTF_8);

		writeToFile(new File("." + File.separator + "JsonExamples" + File.separator + "getDocuments.json"), RESPONSE_TITLE, responceJson);

		List<Map<String, Object>> responseDocs = gson.fromJson(responceJson, ArrayList.class);

		assertEquals(2, responseDocs.size());

		for(Map<String, Object> docMap : responseDocs) {

			String id = docMap.get(Document.ID).toString();
			String title = docMap.get(Document.TITLE).toString();
			String text = docMap.get(Document.TEXT).toString();
			long cd = ((Number)docMap.get(Document.CREATION_DATE)).longValue();

			assertTrue(document1.getID().toString().equals(id) || document2.getID().toString().equals(id));

			assertTrue(document1.getTitle().equals(title) || document2.getTitle().equals(title));

			assertTrue(document1.getText().equals(text) || document2.getText().equals(text));

			assertTrue(Objects.equals(document1.getCreationDate().getTime(), cd)|| Objects.equals(document2.getCreationDate().getTime(), cd));
		}

		//_______________deleteDocument_________________//

		map.clear();

		map.put(ServerParams.METHOD, "deleteDocument");
		map.put(ServerParams.DOCUMENT_ID, document1.getID());

		requestJson = gson.toJson(map);

		writeToFile(new File("." + File.separator + "JsonExamples" + File.separator + "deleteDocument.json"), REQUEST_TITLE, requestJson);

		response = sendRequest(requestJson, token.token);

		assertEquals(200, response.getStatusCode());

		writeToFile(new File("." + File.separator + "JsonExamples" + File.separator + "deleteDocument.json"), RESPONSE_TITLE, "NO CONTENT");

		map.put(ServerParams.METHOD, "getDocument");
		map.put(ServerParams.DOCUMENT, document1.getID());

		requestJson = gson.toJson(map);

		response = sendRequest(requestJson, token.token);

		assertEquals(404, response.getStatusCode());

		try {
			noteServer.getArbiter().getCurrentValue().getDocumentManager().getDocument(email, document1.getID());
			fail();
		}catch (Exception e) {
			assertTrue(e instanceof DocumentNotFoundException);
		}


	}
	/*
	setDocumentAccessors
	getDocumentAccessors
	getAllAccounts
	 */

	@Test
	public void accessorsTests() throws IOException, HandledServerException, DocumentNotFoundException {
		Token token1 = createAndAutho(1L, TimeUnit.MINUTES);
		Token token2 = createAndAutho(1L, TimeUnit.MINUTES);

		String email1 = noteServer.getArbiter().getCurrentValue().getAccountWithToken(token1.token);
		String email2 = noteServer.getArbiter().getCurrentValue().getAccountWithToken(token2.token);

		Document doc1 = createDocument();

		noteServer.getArbiter().getCurrentValue().getDocumentManager().setDocument(email1, doc1);

		Document doc2 = createDocument();

		noteServer.getArbiter().getCurrentValue().getDocumentManager().setDocument(email2, doc2);

		Map<String, Object> map = new HashMap<>();

		Map<String, Object> jsonMap;

		String responceJson;

		//________________setDocumentAccessors________________//

		map.put(ServerParams.METHOD, "setDocumentAccessors");
		map.put(ServerParams.ACCESSORS, Arrays.asList(email2.toLowerCase()));
		map.put(ServerParams.DOCUMENT_ID, doc1.getID());

		String requestJson = gson.toJson(map);

		writeToFile(new File("." + File.separator + "JsonExamples" + File.separator + "setDocumentAccessors.json"), REQUEST_TITLE, requestJson);

		RawHttpResponse<?> response = sendRequest(requestJson, token1.token);

		writeToFile(new File("." + File.separator + "JsonExamples" + File.separator + "setDocumentAccessors.json"), RESPONSE_TITLE, "NO CONTENT");

		//String responceJson = response.getBody().get().decodeBodyToString(StandardCharsets.UTF_8);

		//System.out.println(responceJson);

		assertEquals(200, response.getStatusCode());

		Set<String> accessors = noteServer.getArbiter().getCurrentValue().getDocumentManager().getDocumentAccess(email1, doc1.getID());

		assertEquals(1, accessors.size());

		assertTrue(accessors.contains(email2));
		

		//_______________getDocuments_________________//

		List<Document> docs = noteServer.getArbiter().getCurrentValue().getDocumentManager().getDocuments(email2,  DocumentManager.MINE_AND_SHARED);

		assertEquals(2, docs.size());

		assertTrue(docs.contains(doc1));

		assertTrue(docs.contains(doc2));

		map.clear();

		map.put(ServerParams.METHOD, "getDocuments");
		map.put(ServerParams.SCOPE, DocumentManager.MINE_AND_SHARED);

		requestJson = gson.toJson(map);

		response = sendRequest(requestJson, token2.token);

		assertEquals(200, response.getStatusCode());

		responceJson = response.getBody().get().decodeBodyToString(StandardCharsets.UTF_8);

		List<Map<String, Object>> responseDocs = gson.fromJson(responceJson, ArrayList.class);

		assertEquals(2, responseDocs.size());

		map.clear();

		map.put(ServerParams.METHOD, "getDocuments");
		map.put(ServerParams.SCOPE, DocumentManager.SHARED);

		requestJson = gson.toJson(map);

		response = sendRequest(requestJson, token2.token);

		assertEquals(200, response.getStatusCode());

		responceJson = response.getBody().get().decodeBodyToString(StandardCharsets.UTF_8);

		responseDocs = gson.fromJson(responceJson, ArrayList.class);

		assertEquals(1, responseDocs.size());

		//________________getDocumentAccessors________________//

		map.put(ServerParams.METHOD, "getDocumentAccessors");
		map.put(ServerParams.DOCUMENT_ID, doc1.getID());

		requestJson = gson.toJson(map);

		writeToFile(new File("." + File.separator + "JsonExamples" + File.separator + "getDocumentAccessors.json"), REQUEST_TITLE, requestJson);

		response = sendRequest(requestJson, token1.token);

		responceJson = response.getBody().get().decodeBodyToString(StandardCharsets.UTF_8);
		
		writeToFile(new File("." + File.separator + "JsonExamples" + File.separator + "getDocumentAccessors.json"), RESPONSE_TITLE, responceJson);

		//System.out.println(responceJson);

		assertEquals(200, response.getStatusCode());

		accessors = gson.fromJson(responceJson, HashSet.class);

		assertTrue(accessors.contains(email2));
		
		map.put(ServerParams.METHOD, "getDocumentAccessors");
		map.put(ServerParams.DOCUMENT_ID, doc2.getID());

		requestJson = gson.toJson(map);

		response = sendRequest(requestJson, token2.token);

		responceJson = response.getBody().get().decodeBodyToString(StandardCharsets.UTF_8);
		
		assertEquals(200, response.getStatusCode());

		accessors = gson.fromJson(responceJson, HashSet.class);

		assertTrue(accessors.isEmpty());

	}
	
	@Test
	public void getAllAccountsTests() throws IOException, HandledServerException, DocumentNotFoundException {
		Token token1 = createAndAutho(1L, TimeUnit.MINUTES);
		Token token2 = createAndAutho(1L, TimeUnit.MINUTES);

		String email1 = noteServer.getArbiter().getCurrentValue().getAccountWithToken(token1.token);
		String email2 = noteServer.getArbiter().getCurrentValue().getAccountWithToken(token2.token);

		Map<String, Object> map = new HashMap<>();

		Map<String, Object> jsonMap;

		String responceJson;
		
		map.put(ServerParams.METHOD, "getAllAccounts");

		String requestJson = gson.toJson(map);

		writeToFile(new File("." + File.separator + "JsonExamples" + File.separator + "getAllAccounts.json"), REQUEST_TITLE, requestJson);

		RawHttpResponse<?> response = sendRequest(requestJson, token1.token);
		
		assertEquals(200, response.getStatusCode());
		
		responceJson = response.getBody().get().decodeBodyToString(StandardCharsets.UTF_8);

		writeToFile(new File("." + File.separator + "JsonExamples" + File.separator + "getAllAccounts.json"), RESPONSE_TITLE, responceJson);
		
		Set<Map<String, Object>> accessors = gson.fromJson(responceJson, HashSet.class);

		assertEquals(2, accessors.size());
		
		for(Map<String, Object> accountMap : accessors) {
			assertTrue(Objects.equals(accountMap.get(ServerParams.EMAIL), email1) || Objects.equals(accountMap.get(ServerParams.EMAIL), email2));
		}
		
		map.clear();
		
		map.put(ServerParams.METHOD, "getAllAccounts");

		requestJson = gson.toJson(map);

		response = sendRequest(requestJson, "HAPPY");
		
		assertEquals(401, response.getStatusCode());

	}
	
	@Test
	public void saveShutDownTests() throws Exception {
		Account account = createAccount(Account.CONFIRMED);
		
		Thread.sleep(TimeUnit.SECONDS.toMillis(1));
		
		assertTrue(noteServer.getArbiter().getCurrentValue().accountExists(account.getEmail()));
		
		noteServer.stop();
		
		noteServer = new NoteServer(12345, 1, TimeUnit.SECONDS);
		
		noteServer.setVerbrose(false);

		assertTrue(noteServer.getArbiter().getCurrentValue().accountExists(account.getEmail()));
		
		Map<String, Object> map = new HashMap<>();
		
		map.put(ServerParams.METHOD, "authenticate");
		map.put(ServerParams.EMAIL, EMAIL);
		map.put(ServerParams.PWD, GOOD_PASSWORD);
		map.put(ServerParams.TIME_SPAN, 1);
		map.put(ServerParams.TIME_UNIT, TimeUnit.SECONDS.name());

		String mapJson = gson.toJson(map);

		writeToFile(new File("." + File.separator + "JsonExamples" + File.separator + "authenticate.json"), REQUEST_TITLE, mapJson);

		RawHttpResponse<?> response = sendRequest(mapJson);

		String json = response.getBody().get().decodeBodyToString(StandardCharsets.UTF_8);

		assertEquals(200, response.getStatusCode());

		writeToFile(new File("." + File.separator + "JsonExamples" + File.separator + "authenticate.json"), RESPONSE_TITLE, json);

		Map<String, Object> jsonMap = gson.fromJson(json, HashMap.class);

		assertNotNull(jsonMap.get("token"));

		assertNotNull(jsonMap.get("owner"));

		assertNotNull(jsonMap.get("ttl"));

		assertNotNull(jsonMap.get("creation_date"));

		assertTrue(noteServer.getArbiter().getCurrentValue().getAuthoManager().getTokenManager().isTokenValid(jsonMap.get("token").toString()));

	}

}
