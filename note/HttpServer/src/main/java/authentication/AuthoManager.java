package authentication;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import httpServer.HandledServerException;

public class AuthoManager {
	
	private final AccountManager ACOUNT_MANAGER;
	private final TokenManager TOKEN_MANAGER;
	
	public AuthoManager() {
		ACOUNT_MANAGER = new AccountManager();
		TOKEN_MANAGER = new TokenManager();
	}
	
	public AccountManager getAccountManager() {
		return ACOUNT_MANAGER;
	}
	
	public TokenManager getTokenManager() {
		return TOKEN_MANAGER;
	}
	
	public void setAccount(Account account) throws HandledServerException {
		ACOUNT_MANAGER.setAccount(account);
	}
	
	public Account getAccount(String email) throws UserAccountNotFoundException {
		return ACOUNT_MANAGER.getAccount(email);
	}
	
	public String getTokenOwner(String token) {
		return TOKEN_MANAGER.getTokenOwner(token);
	}
	
	public boolean accountExists(String email) {
		return ACOUNT_MANAGER.accountExists(email);
	}
	
	public void deleteAccount(String email) {
		TOKEN_MANAGER.signOutWithUserName(email);
		ACOUNT_MANAGER.deleteAccount(email);
	}
	
	public Account createAccount(String email) throws HandledServerException {
		return ACOUNT_MANAGER.createAccount(email);
	}
	
	public Token authenticate(Account account, String password, long lifeSpan, TimeUnit unit) throws HandledServerException {
		return TOKEN_MANAGER.authenticate(account, password, lifeSpan, unit);
	}
	
	public Token refresh(String tokenValue, long lifeSpan, TimeUnit unit) {
		return TOKEN_MANAGER.refresh(tokenValue, lifeSpan, unit);
	}
	
	public void signOutWithToken(String tokenValue) {
		TOKEN_MANAGER.signOutWithToken(tokenValue);
	}
	
	public void signOutWithUserName(String email) {
		TOKEN_MANAGER.signOutWithUserName(email);
	}
	
	public boolean isTokenValid(String tokenValue) {
		return TOKEN_MANAGER.isTokenValid(tokenValue);
	}
	
	public void registerAccount(String email, String tempPassword, String newPassword) throws HandledServerException, UserAccountNotFoundException{
		ACOUNT_MANAGER.registerAccount(email, tempPassword, newPassword);
	}
	
	public String resetTempPassword(String email) throws UserAccountNotFoundException {
		return ACOUNT_MANAGER.resetTempPassword(email);
	}
	
	public List<AccountPreview> getAllAccounts() {
		return ACOUNT_MANAGER.getAllAccounts();
	}

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();
		
		builder.append(ACOUNT_MANAGER);
		builder.append(TOKEN_MANAGER);
		
		return builder.hashCode();
	}
	

}
