/**
 * 
 */
package com.luisgois.magazine.dto.session.authentication;

/**
 * @author luisgois
 *
 */
public class AuthenticationResponse {
	
	private String message;
	private String jwt;
	
	/**
	 * @param message
	 * @param jwt
	 */
	public AuthenticationResponse(String message,String jwt) {
		this.message = message;
		this.jwt = jwt;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}
	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}
		
	/**
	 * @return the jwt
	 */
	public String getJwt() {
		return jwt;
	}
	/**
	 * @param jwt the jwt to set
	 */
	public void setJwt(String jwt) {
		this.jwt = jwt;
	}


}
