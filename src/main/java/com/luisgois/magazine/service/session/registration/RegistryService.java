/**
 * 
 */
package com.luisgois.magazine.service.session.registration;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.luisgois.magazine.dao.session.RoleRepository;
import com.luisgois.magazine.dao.session.UserRepository;
import com.luisgois.magazine.dto.session.registration.Permission;
import com.luisgois.magazine.dto.session.registration.RegistrationRequest;
import com.luisgois.magazine.dto.session.registration.RegistrationResponse;
import com.luisgois.magazine.exception.custom.UserNotFoundException;
import com.luisgois.magazine.exception.enumeration.DataIntegrity;
import com.luisgois.magazine.models.session.Role;
import com.luisgois.magazine.models.session.User;
import com.luisgois.magazine.utils.reflection.MapperReflection;

/**
 * @author luisgois
 *
 */
@Service
public class RegistryService {

	@Autowired
	UserRepository userRepository;
	
	@Autowired
	RoleRepository roleRepository;

	@Autowired
	PasswordEncoder passwordEncoder;
	
	@Autowired
	MapperReflection processorReflection;

	public RegistrationResponse create(RegistrationRequest registrationRequest) {
		
		// create new user
		User user = new User(registrationRequest.getUsername(),
				passwordEncoder.encode(registrationRequest.getPassword()),registrationRequest.isEnabled());
		
		// create or retrieve roles
		List<Role> roles = Stream.of(registrationRequest.getAuthorities())
								.map(this::getRole)
									.collect(Collectors.toList());

		// set roles for this user
		user.setRoles(roles); 		
		
		// save new user
		userRepository.save(user);	
	
		return new RegistrationResponse(user);		
	}
	
	public RegistrationResponse read(Long id) {	
				
		Optional<User> user = userRepository.findById(id);
				
		return new RegistrationResponse(user.orElseThrow(() -> 
			new UserNotFoundException(DataIntegrity.NONEXISTENT_USER)
		));			
	}
	
	public RegistrationResponse read(String username) {	
		
		Optional<User> user = userRepository.findByUsername(username);
		
		return new RegistrationResponse(user.orElseThrow(() -> 
			new UserNotFoundException(DataIntegrity.NONEXISTENT_USER)
		));		
	}
	
	public List<RegistrationResponse> readAll() {	
		
		List<User> users = userRepository.findAll();
		
		return users
				.stream()
					.map(user -> new RegistrationResponse(user))
						.collect(Collectors.toList());
	}
	
	public RegistrationResponse patch(Long id, RegistrationRequest registrationRequest) throws IllegalArgumentException, IllegalAccessException {	
		
		Optional<User> userOptional = userRepository.findById(id);
		
		userOptional.orElseThrow(() -> new
		  UserNotFoundException(DataIntegrity.NONEXISTENT_USER));
		
		// replace user details
		User user = processorReflection.mapper(registrationRequest, userOptional.get());
		
		// check if authorities also need to be updated
		if(registrationRequest.getAuthorities() != null) {			
			
			// create or retrieve roles
			List<Role> roles = Stream.of(registrationRequest.getAuthorities())
									.map(this::getRole)
										.collect(Collectors.toList());
			
			// set roles for this user
			user.setRoles(roles); 							
		}
		
		// save updated user
		userRepository.save(user);	
		
		return new RegistrationResponse(user);
	}
	
	public void delete(Long id) {	
		
		Optional<User> user = userRepository.findById(id);

		userRepository.delete(user.orElseThrow(() -> 
			new UserNotFoundException(DataIntegrity.NONEXISTENT_USER)
		));		
	}
	
	private Role getRole(Permission authority) {
		Optional<Role> role = roleRepository.findByAuthority(authority);			
		return role.orElseGet(() -> new Role(authority));
	}

}
