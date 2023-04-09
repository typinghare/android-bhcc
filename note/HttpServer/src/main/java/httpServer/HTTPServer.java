package httpServer;

import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import com.google.api.client.util.Data;
import com.google.gson.Gson;

import io.IOUtils;
import rawhttp.core.RawHttp;
import rawhttp.core.RawHttpHeaders;
import rawhttp.core.RawHttpRequest;
import rawhttp.core.RawHttpResponse;
import rawhttp.core.body.BodyReader;
import rawhttp.core.server.Router;
import rawhttp.core.server.TcpRawHttpServer;

public class HTTPServer implements Router{

	public static final String SUCCESS = "HTTP/1.1 200 OK";
	public static final String SUCCESS_NO_RETURN = "HTTP/1.1 204 No Content";
	public static final String BAD_REQUEST = "HTTP/1.1 400 Bad Request";
	public static final String NOT_FOUND = "HTTP/1.1 404 Not Found";
	public static final String NOT_IMPLEMENTED = "HTTP/1.1 501 Not Implemented";
	public static final String SERVER_ERROR = "HTTP/1.1 500 Internal Server Error";
	public static final String UNAUTHORIZED = "HTTP/1.1 401 Unauthorized";

	private RawHttp http;
	public final int PORT;
	private final TcpRawHttpServer SERVER;

	public final static String NEW_LINE = "\r\n";
	private String serverName = "Mobile App Example Server";
	private boolean isVerbrose;
	
	private ServerDelegator delegator;

	public HTTPServer(int port) {
		PORT = port;
				
		http = new RawHttp();

		SERVER = new TcpRawHttpServer(PORT);

		SERVER.start(this);
		
		System.out.println("Server started: "+getEndPoint());
		
		isVerbrose = true;
		
	}

	public HTTPServer(int port, ServerDelegator delegator) {
		this(port);
		this.delegator = delegator;
	}
	
	public void stop() {
		SERVER.stop();
		System.out.println("Server Terminated");
	}
	
	public String getEndPoint() {
		try {
			return InetAddress.getLocalHost().getHostAddress()+":"+PORT;
		} catch (UnknownHostException e) {
			return "UNKNOWN";
		}
	}

	@Override
	public Optional<RawHttpResponse<?>> route(RawHttpRequest request) {
		String method = request.getMethod();

		HttpMethod methodEnum = HttpMethod.valueOf(method);

		String body = "";
		String status = "";
		Map<String, List<String>> headersMap = new HashMap<String, List<String>>();
		boolean isHealthy = true;
		Map<String, List<String>> params = new HashMap<String, List<String>>();

		try {

			try {
				URI uri = request.getUri();

				params = IOUtils.splitQuery(uri.toURL());
				
			} catch (Exception e) {
				isHealthy = false;
				
				status = BAD_REQUEST;

				body = HandledServerException.createMessageJson("Malformed URI");

				if(isVerbrose)
					System.err.println("Client Error "+e.getMessage());

				e.printStackTrace();
			}
			
			try {
				Optional<? extends BodyReader> bodyOp = request.getBody();
				
				if(bodyOp.isPresent()) {
					body = bodyOp.get().decodeBodyToString(Charset.forName("UTF-8"));
				}
				
			} catch (Exception e) {
				isHealthy = false;
				
				status = BAD_REQUEST;

				body = HandledServerException.createMessageJson("Body Parsing Error");

				if(isVerbrose)
					System.err.println("Client Error "+e.getMessage());

				if(isVerbrose)
					e.printStackTrace();
			}
			
			try {
				RawHttpHeaders headers = request.getHeaders();
				
				headersMap = headers.asMap();
								
			} catch (Exception e) {
				isHealthy = false;
				
				status = BAD_REQUEST;

				body = HandledServerException.createMessageJson("Header Parsing Error");

				if(isVerbrose)
					System.err.println("Client Error "+e.getMessage());

				if(isVerbrose)
					e.printStackTrace();
			}

			if(isHealthy) {
				
				if(isVerbrose)
					System.out.println("Server: "+methodEnum.name()+" Request Recieved");
				
				switch (methodEnum) {
				case CONNECT:
					body = delegator.connect(params, headersMap, body);
					break;
				case DELETE:
					body = delegator.delete(params, headersMap, body);
					break;
				case GET:
					body = delegator.get(params, headersMap, body);
					break;
				case POST:
					body = delegator.post(params, headersMap, body);
					break;
				case PUT:
					body = delegator.put(params, headersMap, body);
					break;
				default:
					body = "Method is not implemented";
					status = NOT_IMPLEMENTED;
					isHealthy = false;
				}
			}
		} catch (HandledServerException e) {
			isHealthy = false;
			
			status = e.getCode(); 
			
			body = e.getMessage();

			if(isVerbrose)
				System.err.println("Request Error: ["+e.getCode()+"] "+e.getMessage());
											
		} catch (Exception e) {
			isHealthy = false;
			
			status = SERVER_ERROR; 

			body = HandledServerException.createMessageJson(e.getClass().getSimpleName());
			
			if(isVerbrose)
				System.err.println("Server Error "+e.getMessage());

			if(isVerbrose)
				e.printStackTrace();
		}

		if(isHealthy) {
			status = SUCCESS;
			
			if(StringUtils.isBlank(body)) {
				body = "{\"message\" : \"\"}";
			}
		}

		RawHttpResponse<?> response = http.parseResponse(status+NEW_LINE+
	    	    "Content-Type: application/json"+NEW_LINE+
	    	    "Content-Length: " + body.length()+NEW_LINE+
	    	    "Server: "+serverName+NEW_LINE+
	    	    "Date: " + new Data() +NEW_LINE+
	    	    NEW_LINE +
	    	    body);
		
		return Optional.of(response);
	}
	
	public ServerDelegator getDelegator() {
		return delegator;
	}


	public void setDelegator(ServerDelegator delegator) {
		Objects.requireNonNull(delegator);
		
		this.delegator = delegator;
	}
	
	public void setVerbrose(boolean isVerbrose) {
		this.isVerbrose = isVerbrose;
	}

}
