/**
 * 
 */
package com.luisgois.magazine.dto.session.registration;

import java.util.Arrays;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import com.luisgois.magazine.exception.constant.SessionValidators;

/**
 * @author luisgois
 *
 */
public class RegistrationRequest {
		
	@Pattern(regexp = "^[a-z0-9_-]{5,}$",
			 message = SessionValidators.USERNAME_VALUE)
    @NotBlank(message = SessionValidators.REQUIRED_VALUE)
	private String username;
	
	@Pattern(regexp = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{8,}$",
			 message = SessionValidators.PASSWORD_VALUE)
    @NotBlank(message = SessionValidators.REQUIRED_VALUE)
	private String password;
	
	private boolean enabled = true;
	private Permission[] authorities = {Permission.ROLE_USER};
	
	public RegistrationRequest(){}

	/**
	 * @param username
	 * @param password
	 * @param enabled
	 * @param authorities
	 */
	public RegistrationRequest(String username, String password, boolean enabled, Permission[] authorities) {
		this.username = username;
		this.password = password;
		this.enabled = enabled;
		this.authorities = authorities;
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

	/**
	 * @return the enabled
	 */
	public boolean isEnabled() {
		return enabled;
	}

	/**
	 * @param enabled the enabled to set
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	/**
	 * @return the authorities
	 */
	public Permission[] getAuthorities() {
		return authorities;
	}

	/**
	 * @param authorities the authorities to set
	 */
	public void setAuthorities(Permission[] authorities) {
		this.authorities = authorities;
	}

	@Override
	public String toString() {
		return "RegistrationRequest [username=" + username + ", enabled=" + enabled + ", authorities="
				+ Arrays.toString(authorities) + "]";
	}

}
