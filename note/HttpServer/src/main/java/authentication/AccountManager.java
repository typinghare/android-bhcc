package authentication;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import httpServer.HTTPServer;
import httpServer.HandledServerException;

public class AccountManager {

	private HashMap<String, Account> accountMap;
	private transient final ReadLock READ_LOCK;
	private transient final WriteLock WRITE_LOCK;
	private transient final ReentrantReadWriteLock READ_WRITE_LOCK;

	public AccountManager() {
		accountMap = new HashMap<>();
		
		READ_WRITE_LOCK = new ReentrantReadWriteLock();

		READ_LOCK = READ_WRITE_LOCK.readLock();

		WRITE_LOCK = READ_WRITE_LOCK.writeLock();
	}
	
	public void setAccount(Account account) throws HandledServerException{
		WRITE_LOCK.lock();
		try {
			
			if(StringUtils.isBlank(account.getEmail())) {
				throw new HandledServerException(HTTPServer.SERVER_ERROR, "Account email is missing");
			}
			
			accountMap.put(account.getEmail(), account);
			
		} finally {
			WRITE_LOCK.unlock();
		}
	}

	public Account getAccount(String email) throws UserAccountNotFoundException{

		email = Account.formateEmail(email);

		if(!accountExists(email)) {
			throw new UserAccountNotFoundException(email);
		}
				
		READ_LOCK.lock();
		try {
			return accountMap.get(email);
		} finally {
			READ_LOCK.unlock();
		}
	}

	public boolean accountExists(String email) {
		
		email = Account.formateEmail(email);
				
		READ_LOCK.lock();
		try {
			return accountMap.containsKey(email);
		} finally {
			READ_LOCK.unlock();
		}
	}

	public void deleteAccount(String email) {
		
		email = Account.formateEmail(email);

		WRITE_LOCK.lock();
		try {
			accountMap.remove(email);
		} finally {
			WRITE_LOCK.unlock();
		}

	}
	
	public String resetTempPassword(String email) throws UserAccountNotFoundException{
		
		email = Account.formateEmail(email);
		
		READ_LOCK.lock();
		try {
			
			Account account = getAccount(email);
			
			return account.resetTempPassword();
						
		} finally {
			READ_LOCK.unlock();
		}
	}

	public Account createAccount(String email) throws HandledServerException{
		
		email = Account.formateEmail(email);

		WRITE_LOCK.lock();
		try {

			if(accountExists(email)) {
				throw new HandledServerException(HTTPServer.BAD_REQUEST, "User account already exists");
			}

			Account userAccount = new Account(email);

			accountMap.put(email, userAccount);

			return userAccount;

		} finally {
			WRITE_LOCK.unlock();
		}
	}
	
	public List<AccountPreview> getAllAccounts(){
		READ_LOCK.lock();
		try {
			
			return accountMap.values().stream().filter(account -> account.isConfirmed()).map(account -> new AccountPreview(account)).collect(Collectors.toList());
			
		} finally {
			READ_LOCK.unlock();
		}
	}
	
	public void registerAccount(String email, String tempPassword, String newPassword) throws HandledServerException, UserAccountNotFoundException{
		READ_LOCK.lock();
		try {
			
			Account account = getAccount(email);
			
			account.registerAccount(tempPassword, newPassword);
			
		} finally {
			READ_LOCK.unlock();
		}
	}

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();
		
		builder.append(accountMap);
		
		return builder.hashCode();
	}

}
