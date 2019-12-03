/**
 * 
 */
package com.luisgois.magazine.dto.session.registration;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.luisgois.magazine.models.session.User;

/**
 * @author luisgois
 *
 */
public class RegistrationResponse {
		
	private Long id;
	private String username;	
	private boolean enabled;
	private List<Permission> authorities = new ArrayList<>();
	
	public RegistrationResponse(){}

	/**
	 * @param username
	 * @param enabled
	 * @param authorities
	 */
	public RegistrationResponse(User user) {
		this.id = user.getId();
		this.username = user.getUsername();
		this.enabled = user.isEnabled();
		this.authorities = user.getRoles().stream()
								.map(role -> role.getAuthority())
									.collect(Collectors.toList());
	}

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
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
	public List<Permission> getAuthorities() {
		return authorities;
	}

	/**
	 * @param authorities the authorities to set
	 */
	public void setAuthorities(List<Permission> authorities) {
		this.authorities = authorities;
	}

	@Override
	public boolean equals(Object obj) {
		try {
			RegistrationResponse other = (RegistrationResponse) obj;
			return this.id == other.getId() ? true : false;
		} catch (Exception e) {
			return false;
		}
	}
	
}
