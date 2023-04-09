package authentication;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.google.api.client.util.Objects.ToStringHelper;

import httpServer.HTTPServer;
import httpServer.HandledServerException;

public class Token {
	
	public String token, owner;
	public Long ttl, creation_date;
	
	public Token(String owner, long lifeSpan, TimeUnit unit) {
		
		if(StringUtils.isBlank(owner)) {
			throw new HandledServerException(HTTPServer.BAD_REQUEST, "Token creation failure. Owner may not be blank.");
		}
		
		if (lifeSpan <= 0) {
			throw new HandledServerException(HTTPServer.BAD_REQUEST, "Token creation failure. Life span must be > 0.");
		}
		
		if (Objects.isNull(unit)) {
			throw new HandledServerException(HTTPServer.BAD_REQUEST, "Token creation failure. Time unit cannot be null.");
		}
		
		this.owner = owner;
		ttl = unit.toMillis(lifeSpan);
		creation_date = System.currentTimeMillis();
		token = RandomStringUtils.randomAlphanumeric(128);
	}
	
	public boolean isExpired() {
		return (creation_date + ttl) <= System.currentTimeMillis();
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
	}

	@Override
	public int hashCode() {
		return token.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj, false);
	}
}
