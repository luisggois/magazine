/**
 * 
 */
package com.luisgois.magazine.models.session;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.luisgois.magazine.dto.session.registration.Permission;

/**
 * @author luisgois
 *
 */
@Entity
@Table(name = "auth_roles")
public class Role {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(nullable = false, unique = true)
	private Permission authority;
	
	@JsonIgnore
	@ManyToMany(mappedBy = "roles",fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
	private List<User> users = new ArrayList<User>();
	
	public Role() {}

	/**
	 * @param authority
	 */
	public Role(Permission authority) {
		this.authority = authority;
	}

	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * @return the authority
	 */
	public Permission getAuthority() {
		return authority;
	}

	/**
	 * @param authority the authority to set
	 */
	public void setAuthority(Permission authority) {
		this.authority = authority;
	}

	/**
	 * @return the users
	 */
	public List<User> getUsers() {
		return users;
	}

	/**
	 * @param users the users to set
	 */
	public void setUsers(List<User> users) {
		this.users = users;
	}

	@Override
	public String toString() {
		return "Role [id=" + id + ", authority=" + authority + ", users=" + users + "]";
	}
	
}
