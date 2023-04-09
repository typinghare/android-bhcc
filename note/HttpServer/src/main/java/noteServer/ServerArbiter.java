package noteServer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;


import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.api.client.googleapis.util.Utils;
import com.google.api.client.util.StreamingContent;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import authentication.Account;
import authentication.AccountPreview;
import authentication.Token;
import authentication.TokenManager;
import authentication.UserAccountNotFoundException;
import document.Document;
import document.DocumentNotFoundException;
import email.EmailSender;
import httpServer.HTTPServer;
import httpServer.HandledServerException;
import io.AbstractDiskResource;

public class ServerArbiter extends AbstractDiskResource<ServerData>{
	
	private ServerData DATA;
	private EmailSender emailSender;
	
	public ServerArbiter() throws ClassNotFoundException, IOException {
		super(ServerData.class);
				
		emailSender = new EmailSender(EmailSender.DEFUALT_FROM_EMAIL, EmailSender.DEFUALT_PASSWORD);
	}
	
	@Override
	public int hashCode() {
		getLock().readLock().lock();
		try {
			return DATA.hashCode();
		} finally {
			getLock().readLock().unlock();
		}
	}
	
	@Override
	public void update(ServerData newValue) {
		getLock().writeLock().lock();
		try {
			DATA = newValue;			
		} finally {
			getLock().writeLock().unlock();
		}
		
	}
	@Override
	public ServerData getCurrentValue() {
		getLock().readLock().lock();
		try {
			
			if(Objects.isNull(DATA)) {
				DATA  = new ServerData();
			}
			
			return DATA;
		} finally {
			getLock().readLock().unlock();
		}
	}
	
	@Override
	public ServerData load() throws ClassNotFoundException, IOException {
		ServerData data = super.load();
		
		if(data != null) {
			data.getDocumentManager().formatAccessors();
		}
		
		return data;
	}
	
	public void deleteAccount(String token) {
		
		token = testTokenValidity(token);
		
		String email = DATA.getAccountWithToken(token);
		
		getLock().readLock().lock();
		try {
			
			DATA.deleteAccount(email);
			
		} finally {
			getLock().readLock().unlock();
		}
	}
	
	public AccountPreview createAccount(String email, String firstName, String lastName, Object extra) throws HandledServerException {
		
		email = Account.formateEmail(email);
		
		getLock().readLock().lock();
		try {
			
			Account account = DATA.createAccount(email);
			
			account.setFirstName(firstName);
			
			account.setLastName(lastName);
			
			account.setExtra(extra);
			
			AccountPreview toRet = new AccountPreview(account);
			
			//Send email with temp Password
			emailSender.sendEmail("New User Account", "Thank you for creating an account. Your temporary password is: "+account.getTempPassword(), account.getEmail());
			
			return toRet;
			
		} finally {
			getLock().readLock().unlock();
		}
	}
	
	public void forgotPassword(String email) {
		
		email = Account.formateEmail(email);
		
		try {
			
			String tempPassword = DATA.resetTempPassword(email);
			
			Account account = DATA.getAccount(email);
			
			//Send email with temp Password
			emailSender.sendEmail("Forgot Password", "Your temporary password is: "+account.getTempPassword(), account.getEmail());

			
		} catch (UserAccountNotFoundException e) {
			//System.out.println("Caught UserAccountNotFoundException for "+email+" will not throw exception b/c its used for forgot password.");
		}
	}
		
	public void registerAccount(String email, String tempPassword, String newPassword) {
		try {
			
			email = Account.formateEmail(email);
			
			DATA.registerAccount(email, tempPassword, newPassword);
			
		} catch (UserAccountNotFoundException e) {
			throw new HandledServerException(HTTPServer.BAD_REQUEST, "There is no account associated with the email "+email);
		}
	}
	
	public AccountPreview getAccount(String token, String email) throws HandledServerException {
		
		token = testTokenValidity(token);
		
		email = Account.formateEmail(email);
		
		getLock().readLock().lock();
		try {
			
			try {
				Account account = DATA.getAccount(email);
				
				AccountPreview toRet = new AccountPreview(account);
				
				return toRet;
				
			} catch (UserAccountNotFoundException e) {
				throw new HandledServerException(HTTPServer.BAD_REQUEST, "Account cannot be found");
			}
			
		} finally {
			getLock().readLock().unlock();
		}
	}
	
	public void setAccount(String token, Account account) throws HandledServerException {
		
		token = testTokenValidity(token);
		
		String email = getEmailWithToken(token);
		
		/*if(!email.equals(account.getEmail())) {
			throw new HandledServerException(HTTPServer.UNAUTHORIZED, "Token owner and account do not match");
		}*/
		
		getLock().readLock().lock();
		try {
			
			Account og;
			
			try {
				og = DATA.getAccount(email);
			} catch (UserAccountNotFoundException e) {
				//This should not happen
				throw new HandledServerException(HTTPServer.SERVER_ERROR, "Valid token but account cannot be found");
			}
			
			og.setFirstName(account.getFirstName());
			
			og.setLastName(account.getLastName());
			
			og.setExtra(account.getExtra());
						
			DATA.setAccount(og);
			
		} finally {
			getLock().readLock().unlock();
		}
	}
	
	public Token authenticate(String email, String password, long lifeSpan, String unitStr) throws HandledServerException {
		
		email = Account.formateEmail(email);
		
		password = StringUtils.defaultString(password);
		
		Account account;
		
		try {
			
			account = DATA.getAccount(email);
			
		} catch (UserAccountNotFoundException e) {
			throw new HandledServerException(HTTPServer.UNAUTHORIZED, TokenManager.WRONG_USER_OR_PW_MSG);
		}
		
		testLifeSpanValidity(lifeSpan);
		
		TimeUnit unit = getValidTimeUnit(unitStr);
		
		getLock().readLock().lock();
		try {
			
			return DATA.authenticate(account, password, lifeSpan, unit);
			
		} finally {
			getLock().readLock().unlock();
		}
	}
	
	public Token refresh(String token, long lifeSpan, String unitStr) {
		token = testTokenValidity(token);
		
		testLifeSpanValidity(lifeSpan);
		
		TimeUnit unit = getValidTimeUnit(unitStr);
		
		getLock().readLock().lock();
		try {
			return DATA.refresh(token, lifeSpan, unit);
		} finally {
			getLock().readLock().unlock();
		}
	}
	
	public void signOutWithToken(String token) {
		
		token = testTokenValidity(token);
		
		getLock().readLock().lock();
		
		try {
			
			DATA.signOutWithToken(token);
			
		} finally {
			getLock().readLock().unlock();
		}
	}
	
	public void signOutWithUserName(String token) {
		
		String email = getEmailWithToken(token);
		
		getLock().readLock().lock();
		
		try {
			
			DATA.signOutWithUserName(email);
			
		} finally {
			getLock().readLock().unlock();
		}
	}
	
	public Document getDocument(String token, String idStr) throws DocumentNotFoundException, HandledServerException {
		
		String email = getEmailWithToken(token);
		
		UUID id = testUUIDValidity(idStr);
		
		getLock().readLock().lock();
		try {
			
			return DATA.getDocument(email, id);
			
		} finally {
			getLock().readLock().unlock();
		}
	}
	
	public List<Document> getDocuments(String token, int scope) {
		
		String email = getEmailWithToken(token);
		
		getLock().readLock().lock();
		try {
			return DATA.getDocuments(email, scope);
		} finally {
			getLock().readLock().unlock();
		}
	}
	
	public void deleteDocument(String token, String idStr) throws HandledServerException {
		
		String email = getEmailWithToken(token);
		
		UUID id = testUUIDValidity(idStr);
		
		getLock().readLock().lock();
		try {
			DATA.deleteDocument(email, id);
		} finally {
			getLock().readLock().unlock();
		}
	}
	
	public void setDocument(String token, Document document) throws HandledServerException {
		
		String email = getEmailWithToken(token);
		
		if(document == null) {
			throw new HandledServerException(HTTPServer.BAD_REQUEST, "A document must be specified");
		}
		
		try {
			document.getID();
		} catch (NullPointerException e) {
			throw new HandledServerException(HTTPServer.BAD_REQUEST, "The document does not have an ID");
		}catch (IllegalArgumentException e) {
			throw new HandledServerException(HTTPServer.BAD_REQUEST, "The ID of the document does not to conform the string representation of an proper document ID. Please use java.util.UUID.randomUUID() to create your document ID.");
		}
		
		getLock().readLock().lock();
		try {
			DATA.setDocument(email, document);
		} finally {
			getLock().readLock().unlock();
		}
	}
	
	public void setDocumentAccess(String token, String idStr, Collection<String> emails) throws DocumentNotFoundException, HandledServerException {
		
		String email = getEmailWithToken(token);
		
		UUID id = testUUIDValidity(idStr);
		
		if(emails == null) {
			emails = new ArrayList<String>();
		}
		
		emails.removeAll(Collections.singletonList(null));
		
		getLock().readLock().lock();
		try {
			DATA.setDocumentAccess(email, id, emails);
		} finally {
			getLock().readLock().unlock();
		}
	}
	
	public Set<String> getDocumentAccess(String token, String idStr) throws DocumentNotFoundException, HandledServerException {
		
		String email = getEmailWithToken(token);
		
		UUID id = testUUIDValidity(idStr);
		
		getLock().readLock().lock();
		try {
			return DATA.getDocumentAccess(email, id);
		} finally {
			getLock().readLock().unlock();
		}
	}
	
	
	
	public List<AccountPreview> getAllAccounts(String token) {
		token = testTokenValidity(token);
		
		getLock().readLock().lock();
		try {
			
			return DATA.getAllAccounts();
			
		} finally {
			getLock().readLock().unlock();
		}
		
	}

	private String getEmailWithToken(String token) {
		
		token = testTokenValidity(token);
		
		String email;
		
		getLock().readLock().lock();
		try {
			email = DATA.getAccountWithToken(token);
		} finally {
			getLock().readLock().unlock();
		}
		
		return StringUtils.defaultString(email);
	}
	
	private UUID testUUIDValidity(String uid) {
		
		uid = StringUtils.defaultString(uid);
		
		UUID uuid;
		
		try {
			uuid = UUID.fromString(uid);
		} catch (IllegalArgumentException  e) {
			throw new HandledServerException(HTTPServer.BAD_REQUEST, "The document ID specified does not conform to the string representation of an proper document ID.");
		}
		
		return uuid;
	}
	
	private String testTokenValidity(String token) {
		token = StringUtils.defaultString(token);
		
		getLock().readLock().lock();
		try {
			if(!DATA.isTokenValid(token)) {
				throw new HandledServerException(HTTPServer.UNAUTHORIZED, "Invalid token");
			}
		} finally {
			getLock().readLock().unlock();
		}
		
		return token;
	}
	
	private TimeUnit getValidTimeUnit(String unitStr) {
		if(!EnumUtils.isValidEnumIgnoreCase(TimeUnit.class, StringUtils.defaultString(unitStr))) {
			throw new HandledServerException(HTTPServer.BAD_REQUEST, "The time unit \""+unitStr+"\" is not a valid value. Please use only the names of the enums specified in the java.util.concurrent.TimeUnit enumeration set.");
		}
		
		return EnumUtils.getEnumIgnoreCase(TimeUnit.class, unitStr);
	}
	
	private void testLifeSpanValidity(long lifeSpan) {
		if(lifeSpan < 1) {
			throw new HandledServerException(HTTPServer.BAD_REQUEST, "The LifeSpan must be greater than one.");
		}
	}

}
