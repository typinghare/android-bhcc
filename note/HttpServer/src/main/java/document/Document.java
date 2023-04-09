package document;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.LongToDoubleFunction;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import httpServer.HTTPServer;
import httpServer.HandledServerException;

public class Document {

	private static final long serialVersionUID = 5212223495973071712L;

	public final static String ID = "id";
	public final static String TITLE = "title";
	public final static String TEXT = "text";
	public final static String CREATION_DATE = "creation_date";

	private UUID id;
	private String title, text;
	private Long creation_date;

	public Document(String title){
		this();

		setTitle(title);
	}


	public Document(String title, String document){
		this();

		setTitle(title);

		setText(document);
	}

	public Document(Map<String, Object> map) {
		this();
		this.setText(map.getOrDefault(TEXT, "").toString());
		this.setTitle(map.getOrDefault(TITLE, "").toString());

		Object creationDateObj = map.get(CREATION_DATE);

		if(creationDateObj instanceof Number) {
			
			creation_date = ((Number)creationDateObj).longValue();
			
		}else if(creationDateObj != null && NumberUtils.isParsable(creationDateObj.toString())) {

			Number number = NumberUtils.createNumber(creationDateObj.toString());

			creation_date = number.longValue();
			
		}else {
			throw new HandledServerException(HTTPServer.BAD_REQUEST, "Document creation date is not properly formated");
		}

		String idObj = map.getOrDefault(ID, "").toString();

		try {
			id = UUID.fromString(idObj);
		}catch (IllegalArgumentException e) {
			throw new HandledServerException(HTTPServer.BAD_REQUEST, "Document ID is not properly formated");
		}

	}

	public Document() {
		id = UUID.randomUUID();
		title = "Unnamed";
		text = "";
		creation_date = createCreationDate();
	}

	private Long createCreationDate(){
		return System.currentTimeMillis();
	}


	public UUID getID(){

		Objects.requireNonNull(id, "The document does not have an ID!");

		return id;
	}

	public String getTitle(){
		return StringUtils.defaultIfBlank(title, "Unnamed");
	}

	public void setTitle(String newTitle){
		title = StringUtils.defaultIfBlank(newTitle, "Unnamed");
	}

	public String getText(){
		return StringUtils.defaultIfBlank(text, "");
	}

	public void setText(String newDoc){
		text = StringUtils.defaultIfBlank(newDoc, "");
	}

	public Date getCreationDate(){
		return new Date(creation_date);
	}

	public boolean isSavable(){
		return StringUtils.isNotBlank(getTitle());
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof Document && Document.class.cast(o).getID().equals(getID());
	}
}
