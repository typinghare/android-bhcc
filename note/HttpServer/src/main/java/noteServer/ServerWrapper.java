package noteServer;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import org.apache.commons.beanutils.MethodUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.checkerframework.checker.i18nformatter.qual.I18nFormat;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.j2objc.annotations.ReflectionSupport;

import authentication.Account;
import authentication.AccountPreview;
import authentication.Token;
import authentication.UserAccountNotFoundException;
import document.Document;
import document.DocumentNotFoundException;
import httpServer.HTTPServer;
import httpServer.HandledServerException;
import httpServer.ServerDelegator;

public class ServerWrapper implements ServerDelegator {

	private final ServerArbiter serverArbiter;
	private Gson jsonParser;

	public ServerWrapper(ServerArbiter serverArbiter) {
		this.serverArbiter = serverArbiter;
		jsonParser = new Gson();
	}

	public static String getToken(Map<String, List<String>> headersMap) {

		if(headersMap == null) {
			throw new HandledServerException(HTTPServer.SERVER_ERROR, "Null map used in map get token test.");
		}

		List<String> tokens = headersMap.get(ServerParams.TOKEN.toUpperCase());

		if(tokens == null || tokens.isEmpty()) {
			throw new HandledServerException(HTTPServer.BAD_REQUEST, "Missing token. An authentication token must be added to the header of the request to invoke this function.");
		}

		return tokens.get(0);
	}

	public static void mustContain(Map<String, ? extends Object> map, Function<List<String>, String> msgCreator, String...params) {
		if(map == null) {
			throw new HandledServerException(HTTPServer.SERVER_ERROR, "Null map used in map contains test.");
		}

		List<String> parmsList = new ArrayList<String>();

		for(String param : params) {
			if(!map.containsKey(param) || map.get(param) == null) {
				parmsList.add(param);
			}
		}

		if(!parmsList.isEmpty()) {
			throw new HandledServerException(HTTPServer.BAD_REQUEST, msgCreator.apply(parmsList));
		}
	}


	public static <T> T toType(Class<T> clazz, Map<String, ? extends Object> map, String key) {
		return toType(t -> "The value mapped to the \""+key+ "\" parameter is not properly formated.", clazz, map, key);
	}
	public static <T> T toType(Function<Class<?>, String> msgCreator, Class<T> clazz, Map<String, ? extends Object> map, String key) {

		Object object = map.get(key);

		if(object == null) {
			throw new HandledServerException(HTTPServer.SERVER_ERROR, "Null value is toType test.");
		}

		try{
			return clazz.cast(object);
		}catch (ClassCastException e) {
			throw new HandledServerException(HTTPServer.BAD_REQUEST, msgCreator.apply(object.getClass()));
		}
	}

	public static void mustContain(Map<String, Object> map, String...params) {
		mustContain(map, missingParamsList -> "Missing parameter in request body. The missing parameters are: "+missingParamsList, params);
	}

	private void deleteAccount(Map<String, List<String>> headersMap, Map<String, Object> bodyMap) {

		String token = getToken(headersMap);

		serverArbiter.deleteAccount(token);
	}

	private String createAccount(Map<String, List<String>> headersMap, Map<String, Object> bodyMap) throws HandledServerException {

		mustContain(bodyMap, ServerParams.EMAIL, ServerParams.FIRST_NAME, ServerParams.LAST_NAME);

		Object extra = ObjectUtils.defaultIfNull(bodyMap.getOrDefault(ServerParams.EXTRA, ""), "");

		AccountPreview accountPreview = serverArbiter.createAccount(
				bodyMap.get(ServerParams.EMAIL).toString(), 
				bodyMap.get(ServerParams.FIRST_NAME).toString(),
				bodyMap.get(ServerParams.LAST_NAME).toString(),
				extra
				);

		try {

			Account accountActual = serverArbiter.getCurrentValue().getAccount(accountPreview.getEmail());

			System.out.println("User Account Created Email: ["+accountActual.getEmail()+"]  Temporary Password: ["+accountActual.getTempPassword()+"]");

		} catch (UserAccountNotFoundException e) {
			System.err.println("Error retrieving account");
		}


		String toRetJson = jsonParser.toJson(accountPreview);

		return toRetJson;
	}

	private void forgotPassword(Map<String, List<String>> headersMap, Map<String, Object> bodyMap) {

		mustContain(bodyMap, ServerParams.EMAIL);
		
		String email = bodyMap.get(ServerParams.EMAIL).toString();

		serverArbiter.forgotPassword(email);

		try {

			Account accountActual = serverArbiter.getCurrentValue().getAccount(email);

			System.out.println("Forgot Password Invoked Email: ["+accountActual.getEmail()+"]  Temporary Password: ["+accountActual.getTempPassword()+"]");

		} catch (UserAccountNotFoundException e) {
			System.err.println("Error retrieving account");
		}
	}

	private void registerAccount(Map<String, List<String>> headersMap, Map<String, Object> bodyMap) {

		mustContain(bodyMap, ServerParams.EMAIL, ServerParams.TEMP_PWD, ServerParams.PWD);

		serverArbiter.registerAccount(
				bodyMap.get(ServerParams.EMAIL).toString(), 
				bodyMap.get(ServerParams.TEMP_PWD).toString(),
				bodyMap.get(ServerParams.PWD).toString());
		
		String email = bodyMap.get(ServerParams.EMAIL).toString();
		
		try {

			Account accountActual = serverArbiter.getCurrentValue().getAccount(email);

			System.out.println("User Account Registered Email: ["+accountActual.getEmail()+"] Actual Password: ["+accountActual.getPassword()+"]");

		} catch (UserAccountNotFoundException e) {
			System.err.println("Error retrieving account");
		}
	}

	private String getAccount(Map<String, List<String>> headersMap, Map<String, Object> bodyMap) throws HandledServerException {

		String token = getToken(headersMap);

		mustContain(bodyMap, ServerParams.EMAIL);

		AccountPreview accountPreview = serverArbiter.getAccount(token, bodyMap.get(ServerParams.EMAIL).toString());

		String toRetJson = jsonParser.toJson(accountPreview);

		return toRetJson;
	}

	private void setAccount(Map<String, List<String>> headersMap, Map<String, Object> bodyMap) throws HandledServerException {

		String token = getToken(headersMap);

		mustContain(bodyMap, ServerParams.ACCOUNT);

		@SuppressWarnings("unchecked")
		Map<String, Object> accountMap = toType(Map.class, bodyMap, ServerParams.ACCOUNT);

		accountMap.put(ServerParams.EMAIL, "");

		Account account = new Account(accountMap);

		serverArbiter.setAccount(token, account);
	}

	private String refresh(Map<String, List<String>> headersMap, Map<String, Object> bodyMap) throws HandledServerException {

		String tokenStr = getToken(headersMap);

		mustContain(bodyMap, ServerParams.TIME_SPAN, ServerParams.TIME_UNIT);

		if(!NumberUtils.isParsable(bodyMap.get(ServerParams.TIME_SPAN).toString())) {
			throw new HandledServerException(HTTPServer.BAD_REQUEST, "The time span specified is not a properly formated number value.");
		}

		long timeSpan = NumberUtils.createNumber(bodyMap.get(ServerParams.TIME_SPAN).toString()).longValue();

		Token token = serverArbiter.refresh(tokenStr, timeSpan, bodyMap.get(ServerParams.TIME_UNIT).toString());

		String toRetJson = jsonParser.toJson(token);

		return toRetJson;
	}

	private String authenticate(Map<String, List<String>> headersMap, Map<String, Object> bodyMap) throws HandledServerException {

		mustContain(bodyMap, ServerParams.EMAIL, ServerParams.PWD, ServerParams.TIME_SPAN, ServerParams.TIME_UNIT);

		if(!NumberUtils.isParsable(bodyMap.get(ServerParams.TIME_SPAN).toString())) {
			throw new HandledServerException(HTTPServer.BAD_REQUEST, "The time span specified is not a properly formated number value.");
		}

		long timeSpan = NumberUtils.createNumber(bodyMap.get(ServerParams.TIME_SPAN).toString()).longValue();

		Token token = serverArbiter.authenticate(
				bodyMap.get(ServerParams.EMAIL).toString(), 
				bodyMap.get(ServerParams.PWD).toString(), 
				timeSpan, 
				bodyMap.get(ServerParams.TIME_UNIT).toString());

		String toRetJson = jsonParser.toJson(token);

		return toRetJson;
	}

	private void signOut(Map<String, List<String>> headersMap, Map<String, Object> bodyMap) {

		String token = getToken(headersMap);

		serverArbiter.signOutWithToken(token);
	}

	private String getDocument(Map<String, List<String>> headersMap, Map<String, Object> bodyMap) throws DocumentNotFoundException, HandledServerException {

		String token = getToken(headersMap);

		mustContain(bodyMap, ServerParams.DOCUMENT_ID);

		String docID = bodyMap.get(ServerParams.DOCUMENT_ID).toString();

		try {

			Document document = serverArbiter.getDocument(token, docID);

			String toRetJson = jsonParser.toJson(document);

			return toRetJson;

		}catch (DocumentNotFoundException e) {
			throw new HandledServerException(HTTPServer.NOT_FOUND, "A document associated with the ID : "+docID+" was not found.");
		}
	}

	private String getDocuments(Map<String, List<String>> headersMap, Map<String, Object> bodyMap) {

		String token = getToken(headersMap);

		mustContain(bodyMap, ServerParams.SCOPE);

		if(!NumberUtils.isParsable(bodyMap.get(ServerParams.SCOPE).toString())) {
			throw new HandledServerException(HTTPServer.BAD_REQUEST, "The scope specified is not a properly formated number value.");
		}

		int scope = NumberUtils.createNumber(bodyMap.get(ServerParams.SCOPE).toString()).intValue();

		List<Document> documents = serverArbiter.getDocuments(token, scope);

		String toRetJson = jsonParser.toJson(documents);

		return toRetJson;
	}

	private void deleteDocument(Map<String, List<String>> headersMap, Map<String, Object> bodyMap) throws HandledServerException {

		String token = getToken(headersMap);

		mustContain(bodyMap, ServerParams.DOCUMENT_ID);

		serverArbiter.deleteDocument(token, bodyMap.get(ServerParams.DOCUMENT_ID).toString());
	}

	private void setDocument(Map<String, List<String>> headersMap, Map<String, Object> bodyMap) throws HandledServerException {
		String token = getToken(headersMap);

		mustContain(bodyMap, ServerParams.DOCUMENT);

		@SuppressWarnings("unchecked")
		Map<String, Object> docMap = toType(Map.class, bodyMap, ServerParams.DOCUMENT);

		mustContain(docMap, missingParamsList -> "Missing parameter in document body. The missing parameters are: "+missingParamsList, 
				Document.CREATION_DATE, 
				Document.TEXT, 
				Document.ID, 
				Document.TITLE);

		Document document = new Document(docMap);

		serverArbiter.setDocument(token, document);
	}

	private void setDocumentAccessors(Map<String, List<String>> headersMap, Map<String, Object> bodyMap) throws HandledServerException {
		String token = getToken(headersMap);

		mustContain(bodyMap, ServerParams.DOCUMENT_ID, ServerParams.ACCESSORS);

		@SuppressWarnings("unchecked")
		Collection<String> emails = toType(Collection.class, bodyMap, ServerParams.ACCESSORS);

		String docID = bodyMap.get(ServerParams.DOCUMENT_ID).toString();

		try {

			serverArbiter.setDocumentAccess(token, docID, emails);

		}catch (DocumentNotFoundException e) {
			throw new HandledServerException(HTTPServer.NOT_FOUND, "A document associated with the ID : "+docID+" was not found.");
		}
	}

	private String getDocumentAccessors(Map<String, List<String>> headersMap, Map<String, Object> bodyMap)throws HandledServerException {
		String token = getToken(headersMap);

		mustContain(bodyMap, ServerParams.DOCUMENT_ID);

		String docID = bodyMap.get(ServerParams.DOCUMENT_ID).toString();

		try {

			Set<String> emails = serverArbiter.getDocumentAccess(token, docID);

			String toRetJson = jsonParser.toJson(emails);

			return toRetJson;

		}catch (DocumentNotFoundException e) {
			throw new HandledServerException(HTTPServer.NOT_FOUND, "A document associated with the ID : "+docID+" was not found.");
		}
	}

	private String getAllAccounts(Map<String, List<String>> headersMap, Map<String, Object> bodyMap) {
		String token = getToken(headersMap);

		List<AccountPreview> accountPreviews = serverArbiter.getAllAccounts(token);

		String toRetJson = jsonParser.toJson(accountPreviews);

		return toRetJson;
	}

	@Override
	public String put(Map<String, List<String>> params, Map<String, List<String>> headersMap, String body) {
		throw new HandledServerException(HTTPServer.NOT_IMPLEMENTED, "Only post methods allowed");
	}

	@Override
	public String get(Map<String, List<String>> params, Map<String, List<String>> headersMap, String body) {
		throw new HandledServerException(HTTPServer.NOT_IMPLEMENTED, "Only post methods allowed");
	}

	@Override
	public String post(Map<String, List<String>> params, Map<String, List<String>> headersMap, String body) {

		String methodName = "";

		try {
			@SuppressWarnings("unchecked")
			Map<String, Object> bodyMap = jsonParser.fromJson(body, HashMap.class);

			mustContain(bodyMap, ServerParams.METHOD);

			methodName = bodyMap.get(ServerParams.METHOD).toString();

			Method method;

			method = getClass().getDeclaredMethod(methodName, new Class<?>[] {Map.class, Map.class});

			method.setAccessible(true);

			Object toRet = method.invoke(this, headersMap, bodyMap);

			return toRet == null ? "" : toRet.toString();

		}catch (JsonSyntaxException  e) {
			throw new HandledServerException(HTTPServer.BAD_REQUEST, "The massage body is not a properly formated JSON string. "+e.getMessage());
		} catch (NoSuchMethodException e) {
			throw new HandledServerException(HTTPServer.BAD_REQUEST, "There is no method by the name of \""+methodName+"\"");
		} catch (Exception e) {
			if(e.getCause() instanceof HandledServerException) {
				throw (HandledServerException)e.getCause();
			}else {
				throw new RuntimeException(e.getCause() != null ? e.getCause() : e);
			}
		}
	}

	@Override
	public String connect(Map<String, List<String>> params, Map<String, List<String>> headersMap, String body) {
		throw new HandledServerException(HTTPServer.NOT_IMPLEMENTED, "Only post methods allowed");
	}

	@Override
	public String delete(Map<String, List<String>> params, Map<String, List<String>> headersMap, String body) {
		throw new HandledServerException(HTTPServer.NOT_IMPLEMENTED, "Only post methods allowed");
	}

}
