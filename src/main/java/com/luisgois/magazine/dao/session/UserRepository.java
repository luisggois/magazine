/**
 * 
 */
package com.luisgois.magazine.dao.session;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.luisgois.magazine.models.session.User;

/**
 * @author luisgois
 *
 */
public interface UserRepository extends JpaRepository<User, Long>{
	
	Optional<User> findByUsername(String username);
	
}
