package document;

import java.util.UUID;

public class DocumentNotFoundException extends Exception {
	
	private UUID uuid;
	
	public DocumentNotFoundException(UUID uid) {
		super("Document ["+uid+"] was not found");
		uuid = uid;
	}
	
	public UUID getUuid() {
		return uuid;
	}
	
	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}

}
