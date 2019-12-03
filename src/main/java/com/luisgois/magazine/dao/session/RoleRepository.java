/**
 * 
 */
package com.luisgois.magazine.dao.session;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.luisgois.magazine.dto.session.registration.Permission;
import com.luisgois.magazine.models.session.Role;

/**
 * @author luisgois
 *
 */
public interface RoleRepository extends JpaRepository<Role, Long> {
	
	Optional<Role> findByAuthority(Permission authority);

}
