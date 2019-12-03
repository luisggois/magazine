/**
 * 
 */
package com.luisgois.magazine.dto.session.authentication;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import com.luisgois.magazine.exception.constant.SessionValidators;

/**
 * @author luisgois
 *
 */
public class AuthenticationRequest {
	
	@Pattern(regexp = "^[a-z0-9_-]{5,}$",
			 message = SessionValidators.USERNAME_VALUE)
    @NotBlank(message = SessionValidators.REQUIRED_VALUE)
	private String username;
	
	@Pattern(regexp = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{8,}$",
			 message = SessionValidators.PASSWORD_VALUE)
    @NotBlank(message = SessionValidators.REQUIRED_VALUE)
	private String password;
	
	/**
	 * @param username
	 * @param password
	 */
	
	public AuthenticationRequest(String username, String password) {
		this.username = username;
		this.password = password;
	}
	
	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}
	
	/**
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}
	
	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}
	
	/**
	 * @param password the password to set
	 */
	
	public void setPassword(String password) {
		this.password = password;
	}	

}
