package document;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;
import java.util.stream.Collectors;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import authentication.Account;
import httpServer.HTTPServer;
import httpServer.HandledServerException;

public class DocumentManager {

	private Map<String, Set<UUID>> OWNER_MAP;
	private Map<UUID, Document> DOC_MAP;
	private Map<UUID, Set<String>> SHARE_MAP;

	private transient final ReadLock READ_LOCK;
	private transient final WriteLock WRITE_LOCK;
	private transient final ReentrantReadWriteLock READ_WRITE_LOCK;

	public static final int MINE = 0, SHARED = 1, MINE_AND_SHARED = 2;

	public DocumentManager() {
		OWNER_MAP = new HashMap<>();
		DOC_MAP = new HashMap<>();
		SHARE_MAP = new HashMap<>();

		READ_WRITE_LOCK = new ReentrantReadWriteLock();
		READ_LOCK = READ_WRITE_LOCK.readLock();
		WRITE_LOCK = READ_WRITE_LOCK.writeLock();
	}
	
	public void formatAccessors() {
		for(UUID id: SHARE_MAP.keySet()) {
			Set<String> emails = SHARE_MAP.get(id);
			
			Set<String> formatedEmails = new HashSet<>();
			
			for(String email : emails) {
				formatedEmails.add(Account.formateEmail(email));
			}
			
			SHARE_MAP.put(id, formatedEmails);
			
		}
	}

	public Document getDocument(String email, UUID id) throws DocumentNotFoundException, HandledServerException{
		READ_LOCK.lock();
		try {
			
			email = Account.formateEmail(email);
			
			if(DOC_MAP.containsKey(id) && !isAccessable(email, id)) 
				throw new HandledServerException(HTTPServer.UNAUTHORIZED, "You are not authorized to edit Document ["+id+"]");

			Document document = DOC_MAP.get(id);

			if(Objects.isNull(document)) throw new DocumentNotFoundException(id);

			return document;

		} finally {
			READ_LOCK.unlock();
		}
	}

	public List<Document> getDocuments(String inputEmail, int scope){

		READ_LOCK.lock();
		try {
			
			final String email = Account.formateEmail(inputEmail);
			
			if(scope != MINE && scope == SHARED && scope == MINE_AND_SHARED) {
				throw new HandledServerException(HTTPServer.BAD_REQUEST, "The scope may only be a number value between 0 - 2. The value: "+scope+" was received. [MINE = 0, SHARED = 1, MINE_AND_SHARED = 2]");
			}

			HashSet<Document> documents = new HashSet<>();

			if(scope == MINE || scope == MINE_AND_SHARED) {

				Set<UUID> docsIDs = OWNER_MAP.computeIfAbsent(email, e -> new HashSet<UUID>());

				documents.addAll(docsIDs.stream().map(id -> DOC_MAP.get(id)).collect(Collectors.toSet()));

			}

			if(scope == SHARED || scope == MINE_AND_SHARED) {

				SHARE_MAP.forEach((id, emails) -> {

					if(emails.contains(email)) {

						Document document = DOC_MAP.get(id);

						documents.add(document);

					}
				});
			}

			return new ArrayList<>(documents);

		} finally {
			READ_LOCK.unlock();
		}

	}

	public void deleteDocument(String email, UUID id) throws HandledServerException{

		WRITE_LOCK.lock();
		try {
			
			email = Account.formateEmail(email);

			if(DOC_MAP.containsKey(id) && !isOwner(email, id)) 
				throw new HandledServerException(HTTPServer.UNAUTHORIZED, "You are not authorized to delete Document ["+id+"]");

			DOC_MAP.remove(id);

			SHARE_MAP.remove(id);

			OWNER_MAP.forEach((owner, docs) -> {
				docs.remove(id);
			});

		} finally {
			WRITE_LOCK.unlock();
		}

	}

	public void setDocument(String email, Document document) throws HandledServerException{
		WRITE_LOCK.lock();
		try {
			
			email = Account.formateEmail(email);

			if(DOC_MAP.containsKey(document.getID()) && !isAccessable(email, document.getID())) 
				throw new HandledServerException(HTTPServer.UNAUTHORIZED, "You are not authorized to edit Document ["+document.getID()+"]");

			if(DOC_MAP.put(document.getID(), document) == null) {

				OWNER_MAP.computeIfAbsent(email, e -> new HashSet<UUID>()).add(document.getID());

			}			

			SHARE_MAP.computeIfAbsent(document.getID(), doc -> new HashSet<String>());

		} finally {
			WRITE_LOCK.unlock();
		}
	}

	private boolean isAccessable(String email, UUID id) {
		READ_LOCK.lock();
		try {
			
			email = Account.formateEmail(email);
			
			return isOwner(email, id) || (SHARE_MAP.containsKey(id) && SHARE_MAP.get(id).contains(email));
		} finally {
			READ_LOCK.unlock();
		}
	}

	private boolean isOwner(String email, UUID id) {
		READ_LOCK.lock();
		try {
			
			email = Account.formateEmail(email);
			
			return OWNER_MAP.containsKey(email) && OWNER_MAP.get(email).contains(id);
		} finally {
			READ_LOCK.unlock();
		}
	}

	public void setDocumentAccess(String email, UUID id, Collection<String> emails)throws DocumentNotFoundException, HandledServerException{
		WRITE_LOCK.lock();
		try {
			
			email = Account.formateEmail(email);

			if(!isOwner(email, id)) 
				throw new HandledServerException(HTTPServer.UNAUTHORIZED, "You are not authorized to edit Document ["+id+"]");			
			
			HashSet<String> shareSet = new HashSet<>();
			
			emails.forEach(tempEmail -> shareSet.add(Account.formateEmail(tempEmail)));
			
			SHARE_MAP.put(id, shareSet);

		} finally {
			WRITE_LOCK.unlock();
		}
	}

	public Set<String> getDocumentAccess(String email, UUID id)throws DocumentNotFoundException, HandledServerException{
		READ_LOCK.lock();
		try {
			
			email = Account.formateEmail(email);
			
			if(!isAccessable(email, id)) 
				throw new HandledServerException(HTTPServer.UNAUTHORIZED, "You are not authorized to edit Document ["+id+"]");
			if(!DOC_MAP.containsKey(id))
				throw new DocumentNotFoundException(id);
			
			return new HashSet<>(SHARE_MAP.computeIfAbsent(id, doc -> new HashSet<String>()));
			
		} finally {
			READ_LOCK.unlock();
		}
	}
	
	@Override
	public int hashCode() {
		
		HashCodeBuilder builder = new HashCodeBuilder();
		
		builder.append(OWNER_MAP);
		builder.append(DOC_MAP);
		builder.append(SHARE_MAP);
		
		return builder.hashCode();
	}

}
