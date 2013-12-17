package org.ohmage.auth.provider;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.ohmage.domain.exception.InvalidArgumentException;
import org.ohmage.domain.user.ProviderUserInformation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.MappingJsonFactory;

/**
 * <p>
 * The provider implementation for Google.
 * </p>
 *
 * @author John Jenkins
 */
public class GoogleProvider implements Provider {
	/**
	 * The unique identifier for this provider.
	 */
	public static final String PROVIDER_ID = "google";
	
	/**
	 * The header key for authorization used by us to authenticate our request.
	 */
	public static final String HEADER_AUTHORIZATION = "Authorization";
	/**
	 * The portion of the Authorization header that indicates how we are
	 * authenticating.
	 */
	public static final String HEADER_AUTHORIZATION_BEARER = "Bearer";
	
	/**
	 * The JSON factory that will parse the response from Google.
	 */
	public static final JsonFactory JSON_FACTORY = new MappingJsonFactory();

	/**
	 * <p>
	 * A private Java class to represent the response from Google.
	 * </p>
	 *
	 * @author John Jenkins
	 */
	@JsonIgnoreProperties(ignoreUnknown = true)
	private static class Response {
		/**
		 * The JSON key for the user's unique identifier.
		 */
		public static final String JSON_KEY_ID = "id";
		/**
		 * The JSON key for the user's email address.
		 */
		public static final String JSON_KEY_EMAIL = "email";
		/**
		 * The JSON key for whether or not the user's email address has been
		 * verified.
		 */
		public static final String JSON_KEY_EMAIL_VERIFIED = "verified_email";
		
		/**
		 * The unique identifier for this user generated by Google.
		 */
		@JsonProperty(JSON_KEY_ID)
		public String id;
		/**
		 * The email address for this user according to Google.
		 */
		@JsonProperty(JSON_KEY_EMAIL)
		public String email;
		/**
		 * Whether or not Google has independently verified the email address.
		 */
		@JsonProperty(JSON_KEY_EMAIL_VERIFIED)
		public boolean emailVerified;
	}
	
	/**
	 * @see GoogleProvider#PROVIDER_ID
	 */
	@Override
	public String getId() {
		return PROVIDER_ID;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.ohmage.auth.provider.Provider#getUserInformation(java.lang.String)
	 */
	@Override
	public ProviderUserInformation getUserInformation(
		final String accessToken)
		throws
			IllegalArgumentException,
			IllegalStateException,
			InvalidArgumentException{
		
		if(accessToken == null) {
			throw new IllegalArgumentException("The token is null.");
		}
		
		// Create the client to manage the request.
		HttpClient client = HttpClientBuilder.create().build();
		
		// Build the request.
		HttpGet request =
			new HttpGet("https://www.googleapis.com/oauth2/v2/userinfo");
		request
			.addHeader(
				new BasicHeader(
					HEADER_AUTHORIZATION,
					HEADER_AUTHORIZATION_BEARER + " " + accessToken));
		
		// Make the request and capture the response.
		HttpResponse response;
		try {
			response = client.execute(request);
		}
		catch(ClientProtocolException e) {
			throw
				new IllegalStateException(
					"There was a HTTP protocol exceptoin.",
					e);
		}
		catch(IOException e) {
			throw
				new IllegalStateException(
					"The connection was abruptly severed.",
					e);
		}
		
		// Make sure the request didn't fail.
		int statusCode = response.getStatusLine().getStatusCode();
		if(statusCode == HttpServletResponse.SC_UNAUTHORIZED) {
			throw
				new InvalidTokenException("Google did not approve the token.");
		}
		if((statusCode % 100) == 4) {
			throw new IllegalStateException("Google rejected the request.");
		}
		if((statusCode % 100) == 5) {
			throw new IllegalStateException("Google threw an exception.");
		}
		
		// Parse the response.
		JsonParser parser;
		try {
			parser =
				JSON_FACTORY.createParser(response.getEntity().getContent());
		}
		catch(IllegalStateException | IOException e) {
			throw
				new IllegalStateException(
					"The response from Google was malformed.",
					e);
		}
		
		// Create the Response object.
		Response responsePojo;
		try {
			responsePojo = parser.readValueAs(Response.class);
		}
		catch(IOException e) {
			throw
				new IllegalStateException(
					"The response from Google could not be parsed.",
					e);
		}
		
		// Create the result.
		return
			new ProviderUserInformation(
				PROVIDER_ID,
				responsePojo.id,
				responsePojo.email);
	}
}