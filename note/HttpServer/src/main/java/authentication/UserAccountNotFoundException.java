package authentication;

import org.apache.commons.lang3.StringUtils;

public class UserAccountNotFoundException extends Exception {

	private static final long serialVersionUID = 1L;
	private String email;
	
	public UserAccountNotFoundException(String email) {
		setEmail(email);
	}
	
	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		this.email = StringUtils.defaultString(email);
	}

}
