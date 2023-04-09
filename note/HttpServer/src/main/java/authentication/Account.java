package authentication;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.passay.*;

import httpServer.HTTPServer;
import httpServer.HandledServerException;
import noteServer.ServerParams;

public class Account {

	private String first_name, last_name, password, temp_password;
	private final String email;
	private Object extra;
	private int account_status;
	
	public static final int CONFIRMED = 0;
	public static final int UNREGISTERED = 1;
	public static final List<Rule> PASSWOR_VALIVATION_RULES = Arrays.asList(
			new LengthRule(6, 128), 
			
			// at least one upper-case character
			new CharacterRule(EnglishCharacterData.UpperCase, 1),

			// at least one lower-case character
			new CharacterRule(EnglishCharacterData.LowerCase, 1),

			// at least one digit character
			new CharacterRule(EnglishCharacterData.Digit, 1));
	
	public static final List<CharacterRule> PASSWOR_GENERATION_RULES = Arrays.asList(
			// at least one upper-case character
			new CharacterRule(EnglishCharacterData.UpperCase, 1),

			// at least one lower-case character
			new CharacterRule(EnglishCharacterData.LowerCase, 1),

			// at least one digit character
			new CharacterRule(EnglishCharacterData.Digit, 1));

	public Account(String email) {

		String formated = formateEmail(email);

		if(StringUtils.isBlank(formated) || !EmailValidator.getInstance().isValid(formated)) {
			throw new HandledServerException(HTTPServer.BAD_REQUEST, "User Account creation failure. The email: \""+email+"\" is not a valid email.");
		}

		this.email = formated;
		
		this.account_status = UNREGISTERED;
		
		this.temp_password = createTemporyPassword();
	}
	
	public Account(Map<String, Object> accountMap) {
		if(accountMap.containsKey(ServerParams.FIRST_NAME))
			first_name = accountMap.get(ServerParams.FIRST_NAME).toString();
		
		if(accountMap.containsKey(ServerParams.LAST_NAME))
			last_name = accountMap.get(ServerParams.LAST_NAME).toString();
		
		if(accountMap.containsKey(ServerParams.PWD))
			password = accountMap.get(ServerParams.PWD).toString();
		
		if(accountMap.containsKey(ServerParams.TEMP_PWD))
			temp_password = accountMap.get(ServerParams.TEMP_PWD).toString();
		
		if(accountMap.containsKey(ServerParams.EMAIL))
			email = accountMap.get(ServerParams.EMAIL).toString();
		else
			throw new HandledServerException(HTTPServer.BAD_REQUEST, "User Account creation failure, map creation. The email was not specified.");
		
		if(accountMap.containsKey(ServerParams.EXTRA))
			extra = accountMap.get(ServerParams.EXTRA).toString();
		
		if(accountMap.containsKey(ServerParams.ACCOUNT_STATUS) && NumberUtils.isParsable(accountMap.get(ServerParams.ACCOUNT_STATUS).toString()))
			account_status = NumberUtils.toInt(accountMap.get(ServerParams.ACCOUNT_STATUS).toString());
	}
	
	
	public static String createTemporyPassword() {
		
		PasswordGenerator generator = new PasswordGenerator();
		
		String tempPassword = generator.generatePassword(6, PASSWOR_GENERATION_RULES);
		
		return tempPassword;
	}
	
	public static String formateEmail(String email) {
		
		email = StringUtils.defaultString(email);
		
		String formated;
		
		formated = StringUtils.normalizeSpace(email);

		formated = StringUtils.trimToEmpty(formated);

		formated = formated.toUpperCase();
		
		return formated;
	}

	private static String normalStringValidity(String value) {

		value = ObjectUtils.defaultIfNull(value, "");

		if(value.length() > 256) {
			throw new HandledServerException(HTTPServer.BAD_REQUEST, "Setter validation failure. The value specified is greater then 256 characters.");
		}

		value = StringUtils.normalizeSpace(value);

		value = StringUtils.trimToEmpty(value);

		return value;
	}

	private static void testPasswordValidity(String value) {

		final PasswordValidator validator = new PasswordValidator(PASSWOR_VALIVATION_RULES);

		RuleResult result = validator.validate(new PasswordData(value));
		

		if(!result.isValid()) {
			StringBuilder message = new StringBuilder();
			
			message.append("Password setter validation failure. Error Codes: ");
			
			result.getDetails().forEach(rule -> {
				message.append(rule.getErrorCode());
				message.append(", ");
			});
			
			String realMessage = message.substring(0, message.length()-2);
			
			throw new HandledServerException(HTTPServer.BAD_REQUEST, realMessage);
		}
	}
	
	public void registerAccount(String tempPassword, String newPassword) throws HandledServerException{
		if(!getTempPassword().equals(tempPassword)) {
			throw new HandledServerException(HTTPServer.UNAUTHORIZED, TokenManager.WRONG_USER_OR_PW_MSG);
		}
		
		if(StringUtils.isBlank(getTempPassword())) {
			throw new HandledServerException(HTTPServer.BAD_REQUEST, "The account has already been registered.");
		}
		
		setPassword(newPassword);
		
		setAccountStatus(CONFIRMED);
		
	}
	
	public String getTempPassword() {
		return temp_password;
	}
	
	public String resetTempPassword() throws HandledServerException{
		
		if(!isConfirmed()) {
			throw new HandledServerException(HTTPServer.BAD_REQUEST, "The account has not been registered yet.");
		}
		
		return temp_password = createTemporyPassword();
	}
	
	public String clearTempPassword() {
		return temp_password = "";
	}
	
	public boolean isConfirmed() {
		return getAccountStatus() == CONFIRMED;
	}
	
	public int getAccountStatus() {
		return account_status;
	}
	
	public void setAccountStatus(int accountStatus) {
		this.account_status = accountStatus;
	}

	public void setAs(Account otherAccount) {

		setFirstName(otherAccount.getFirstName());
		
		setLastName(otherAccount.getLastName());
		
		setPassword(otherAccount.getPassword());
		
		setExtra(otherAccount.getExtra());
		
		setAccountStatus(otherAccount.getAccountStatus());

	}

	public String getFirstName() {
		return ObjectUtils.defaultIfNull(first_name, "");
	}

	public void setFirstName(String firstName) {
		this.first_name = normalStringValidity(firstName);
	}

	public String getLastName() {
		return ObjectUtils.defaultIfNull(last_name, "");
	}

	public void setLastName(String lastName) {
		this.last_name = normalStringValidity(lastName);
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		
		password = StringUtils.defaultString(password);
		
		testPasswordValidity(password);
		
		if(password.equalsIgnoreCase(this.password) || password.equalsIgnoreCase(this.temp_password) ) {
			throw new HandledServerException(HTTPServer.BAD_REQUEST, "The new password is too similar to the current password.");
		}
		
		this.password = password;
	}
	
	public void erasePassword() {
		this.password = "";
	}

	public String getEmail() {
		return email;
	}

	public Object getExtra() {
		return extra;
	}

	public void setExtra(Object extra) {
		this.extra = extra;
	}

	@Override
	public int hashCode() {
		return email.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof Account ? email.equals(((Account)obj).email) : false;
	}

}
