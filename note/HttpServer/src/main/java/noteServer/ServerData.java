package noteServer;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import authentication.Account;
import authentication.AccountPreview;
import authentication.AuthoManager;
import authentication.Token;
import authentication.UserAccountNotFoundException;
import document.Document;
import document.DocumentManager;
import document.DocumentNotFoundException;
import httpServer.HandledServerException;

public class ServerData {
	
	private AuthoManager AUTHO_MANAGER;
	private DocumentManager DOC_MANAGER;
	
	public ServerData(){
		AUTHO_MANAGER = new AuthoManager();
		DOC_MANAGER = new DocumentManager();
	}
	
	public AuthoManager getAuthoManager() {
		return AUTHO_MANAGER;
	}
	
	public DocumentManager getDocumentManager() {
		return DOC_MANAGER;
	}
	
	public void setAccount(Account account) throws HandledServerException  {
		AUTHO_MANAGER.setAccount(account);
	}
	public String getAccountWithToken(String token) {
		return AUTHO_MANAGER.getTokenOwner(token);
	}
	public Account getAccount(String email) throws UserAccountNotFoundException {
		return AUTHO_MANAGER.getAccount(email);
	}
	public boolean accountExists(String email) {
		return AUTHO_MANAGER.accountExists(email);
	}
	public void deleteAccount(String email) {
		AUTHO_MANAGER.deleteAccount(email);
	}
	public Account createAccount(String email) throws HandledServerException {
		return AUTHO_MANAGER.createAccount(email);
	}
	public Token authenticate(Account account, String password, long lifeSpan, TimeUnit unit) throws HandledServerException {
		return AUTHO_MANAGER.authenticate(account, password, lifeSpan, unit);
	}
	public Token refresh(String tokenValue, long lifeSpan, TimeUnit unit) {
		return AUTHO_MANAGER.refresh(tokenValue, lifeSpan, unit);
	}
	public void signOutWithToken(String tokenValue) {
		AUTHO_MANAGER.signOutWithToken(tokenValue);
	}
	public void signOutWithUserName(String email) {
		AUTHO_MANAGER.signOutWithUserName(email);
	}
	public boolean isTokenValid(String tokenValue) {
		return AUTHO_MANAGER.isTokenValid(tokenValue);
	}
	public Document getDocument(String email, UUID id) throws DocumentNotFoundException, HandledServerException {
		return DOC_MANAGER.getDocument(email, id);
	}
	public List<Document> getDocuments(String email, int scope) {
		return DOC_MANAGER.getDocuments(email, scope);
	}
	public void deleteDocument(String email, UUID id) throws HandledServerException {
		DOC_MANAGER.deleteDocument(email, id);
	}
	public void setDocument(String email, Document document) throws HandledServerException {
		DOC_MANAGER.setDocument(email, document);
	}
	public void setDocumentAccess(String email, UUID id, Collection<String> emails) throws DocumentNotFoundException, HandledServerException {
		DOC_MANAGER.setDocumentAccess(email, id, emails);
	}
	public Set<String> getDocumentAccess(String email, UUID id) throws DocumentNotFoundException, HandledServerException {
		return DOC_MANAGER.getDocumentAccess(email, id);
	}
	public void registerAccount(String email, String tempPassword, String newPassword) throws HandledServerException, UserAccountNotFoundException {
		AUTHO_MANAGER.registerAccount(email, tempPassword, newPassword);
	}
	public List<AccountPreview> getAllAccounts() {
		return AUTHO_MANAGER.getAllAccounts();
	}
	
	public String resetTempPassword(String email) throws UserAccountNotFoundException {
		return AUTHO_MANAGER.resetTempPassword(email);
	}

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();
		
		builder.append(AUTHO_MANAGER);
		builder.append(DOC_MANAGER);
		
		return builder.hashCode();
	}

}
