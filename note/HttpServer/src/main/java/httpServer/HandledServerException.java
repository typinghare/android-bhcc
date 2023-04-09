package httpServer;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;

public class HandledServerException extends RuntimeException {

	private static final long serialVersionUID = -6885501991274618356L;
	private final static Gson GSON = new Gson();

	
	private String code;

	public HandledServerException(String code, String message) {
		super(createMessageJson(message));
		this.code = code;
	}

	public String getCode() {
		return code;
	}
	
	public static String createMessageJson(String message) {
		Map<String, String> map = new HashMap<>();
		
		map.put("message", message);
				
		return GSON.toJson(map);
	}

}
