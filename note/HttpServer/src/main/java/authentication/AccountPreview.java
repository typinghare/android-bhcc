package authentication;

public class AccountPreview {
	
	public String email, first_name, last_name;
	public Object extra;
	
	
	public AccountPreview(Account account) {
		super();
		this.email = account.getEmail();
		this.first_name = account.getFirstName();
		this.last_name = account.getLastName();
		this.extra = account.getExtra();
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setFirstName(String firstName) {
		this.first_name = firstName;
	}

	public void setLastName(String lastName) {
		this.last_name = lastName;
	}

	public String getEmail() {
		return email;
	}

	public String getFirstName() {
		return first_name;
	}

	public String getLastName() {
		return last_name;
	}
	
	public Object getExtra() {
		return extra;
	}
	
	public void setExtra(Object extra) {
		this.extra = extra;
	}
	
}
