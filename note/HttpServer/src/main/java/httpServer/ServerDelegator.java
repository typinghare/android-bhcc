package httpServer;

import java.util.List;
import java.util.Map;

public interface ServerDelegator {	
	public String put(Map<String, List<String>> params, Map<String, List<String>> headersMap, String body);

	public String get(Map<String, List<String>> params, Map<String, List<String>> headersMap, String body);
	
	public String post(Map<String, List<String>> params, Map<String, List<String>> headersMap, String body);
	
	public String connect(Map<String, List<String>> params, Map<String, List<String>> headersMap, String body);
	
	public String delete(Map<String, List<String>> params, Map<String, List<String>> headersMap, String body);
}
