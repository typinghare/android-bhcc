package authentication;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Table;

import httpServer.HTTPServer;
import httpServer.HandledServerException;

public class TokenManager {

	/*
	 * ROW    = User Name
	 * COLUMN = Token String
	 * VALUE  = Token Object 
	 */
	private Table<String, String, Token> authoTable;
	private transient final ReadLock READ_LOCK;
	private transient final WriteLock WRITE_LOCK;
	private transient final ReentrantReadWriteLock READ_WRITE_LOCK;
	
	public static final String WRONG_USER_OR_PW_MSG = "Incorrect user name or password";

	public TokenManager() {
		authoTable = HashBasedTable.create();
		
		READ_WRITE_LOCK = new ReentrantReadWriteLock();
		
		READ_LOCK = READ_WRITE_LOCK.readLock();
		
		WRITE_LOCK = READ_WRITE_LOCK.writeLock();
	}

	/**
	 * Authenticates the specified user against the specified password 
	 * @param userAccount The user to be signed in
	 * @param password The password of the specified user
	 * @param lifeSpan The duration of the token to be created
	 * @param unit The time unit of the life span
	 * @return A new Token with the specified life span
	 * @throws HandledServerException if the userAccount password does not match the specified password
	 */
	public Token authenticate(Account account, String password, long lifeSpan, TimeUnit unit) throws HandledServerException{
		if(account.getPassword().equals(password)) {

			String email = account.getEmail();

			Token token = newToken(email, lifeSpan, unit);

			return token;
		}
		else {

			throw new HandledServerException(HTTPServer.UNAUTHORIZED, WRONG_USER_OR_PW_MSG);

		}

	}

	public Token refresh(String tokenValue, long lifeSpan, TimeUnit unit) {
		if(isTokenValid(tokenValue)) {

			String email = getTokenUser(tokenValue);
			
			WRITE_LOCK.lock();
			try {
				authoTable.remove(email, tokenValue);
			} finally {
				WRITE_LOCK.unlock();
			}
			
			return newToken(email, lifeSpan, unit);
		}

		throw new HandledServerException(HTTPServer.UNAUTHORIZED, "The specified token is invalid");
	}

	public void signOutWithToken(String tokenValue) {
		String userName = getTokenUser(tokenValue);

		if(Objects.isNull(userName))return;

		WRITE_LOCK.lock();
		try {
			authoTable.remove(userName, tokenValue);
		} finally {
			WRITE_LOCK.unlock();
		}
	}

	public void signOutWithUserName(String email) {
		Map<String, Token> tokenTokenMap;
		
		email = Account.formateEmail(email);

		WRITE_LOCK.lock();
		try {
			
			tokenTokenMap = authoTable.row(email);
			
			tokenTokenMap.clear();
			
		} finally {
			WRITE_LOCK.unlock();
		}
	}
	
	public boolean isTokenValid(String tokenValue) {

		String email = getTokenUser(tokenValue);

		if(StringUtils.isBlank(email))return false;

		Token token;

		READ_LOCK.lock();
		try {

			token = authoTable.get(email, tokenValue);

		} finally {
			READ_LOCK.unlock();
		}

		return Objects.nonNull(token) && !token.isExpired();
	}
	
	private Token newToken(String owner, long lifeSpan, TimeUnit unit) {
		Token token = new Token(owner, lifeSpan, unit);

		addToken(owner, token);

		return token;
	}

	private void addToken(String email, Token token) {
		
		email = Account.formateEmail(email);
		
		WRITE_LOCK.lock();
		try {
			authoTable.put(email, token.token, token);
		} finally {
			WRITE_LOCK.unlock();
		}

	}
	
	public String getTokenOwner(String token) {
		if(isTokenValid(token)) {
			return getTokenUser(token);
		}
		
		return "";
	}

	private String getTokenUser(String token) {

		Map<String, Token> userTokenMap;

		READ_LOCK.lock();
		try {
			userTokenMap = authoTable.column(token);
			
			if(!userTokenMap.isEmpty()) {
				return userTokenMap.keySet().iterator().next();
			}
		} finally {
			READ_LOCK.unlock();
		}

		return "";
	}
	
	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();
		
		builder.append(authoTable);
		
		return builder.hashCode();
	}
	

}
