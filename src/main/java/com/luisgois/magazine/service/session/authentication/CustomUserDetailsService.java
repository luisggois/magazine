/**
 * 
 */
package com.luisgois.magazine.service.session.authentication;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.luisgois.magazine.dao.session.UserRepository;
import com.luisgois.magazine.models.session.User;

/**
 * @author luisgois
 *
 */

@Service
public class CustomUserDetailsService implements UserDetailsService {
	
	@Autowired
	UserRepository authRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		Optional<User> user = authRepository.findByUsername(username);
		
		return new CustomUserDetails(user.orElseThrow(() -> 
			new UsernameNotFoundException("No user found with username " + username))	
		);				
	}
}
