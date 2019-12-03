/**
 * 
 */
package com.luisgois.magazine.controller.session;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.luisgois.magazine.dto.session.authentication.AuthenticationRequest;
import com.luisgois.magazine.dto.session.authentication.AuthenticationResponse;
import com.luisgois.magazine.dto.session.registration.RegistrationRequest;
import com.luisgois.magazine.dto.session.registration.RegistrationResponse;
import com.luisgois.magazine.exception.constant.SessionValidators;
import com.luisgois.magazine.exception.custom.UserAlreadyExistsException;
import com.luisgois.magazine.exception.enumeration.DataIntegrity;
import com.luisgois.magazine.service.session.authentication.CustomUserDetailsService;
import com.luisgois.magazine.service.session.registration.RegistryService;
import com.luisgois.magazine.utils.jwt.JwtHelper;

/**
 * @author luisgois
 *
 */

@RestController
public class SessionController {
		
	@Autowired
	AuthenticationManager authenticationManager;
	
	@Autowired
	CustomUserDetailsService newUserDetailsService;
	
	@Autowired
	JwtHelper jwtHelper;
	
	@Autowired
	RegistryService registrationService;
		
	/**
	 * 
	 * @description authenticate and receive jwt to access protected routes in the api
	 * 
	 * @bodyparam username
	 * @bodyparam password
	 * 
	 * 
	 * @success 200 Ok
	 * @return @json jwt and message of success
	 * 
	 */		
	@PostMapping(path = "/authenticate", consumes = "application/json", produces = "application/json")
	public ResponseEntity<?> authentication(@Valid @RequestBody AuthenticationRequest authenticationRequest) {							
		Authentication authenticationObj = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword()));

		UserDetails userDetails = (UserDetails) authenticationObj.getPrincipal();
		
		String token = jwtHelper.generateToken(userDetails);

		return ResponseEntity
				.status(HttpStatus.OK)
					.body(new AuthenticationResponse("Authentication has been successful",token));		
	}
		
	/**
	 * 
	 * @description place new user in the database
	 * 
	 * @authorization admin role required
	 * 
	 * @bodyparam username
	 * @bodyparam password
	 * @bodyparam enabled
	 * @bodyparam authorities
	 * 
	 * @success 201 Created 
	 * @return @json relevant details about the new created user
	 * 
	 */	
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@PostMapping(path = "/users", consumes = "application/json", produces = "application/json")
	public ResponseEntity<?> create(@Valid @RequestBody RegistrationRequest registrationRequest) {		
		try {	
						
			RegistrationResponse registrationResponse = registrationService.create(registrationRequest);			
			
			return ResponseEntity
					.status(HttpStatus.CREATED)
						.body(registrationResponse);	
			
		} catch (DataIntegrityViolationException error) {
			throw new UserAlreadyExistsException(DataIntegrity.DUPLICATE_USER);
		} 
	}	
	
	/**
	 * 
	 * @description read user in the database
	 * 
	 * @authorization admin role required
	 * 
	 * @queryparam username
	 * 
	 * @success 200 Ok 
	 * @return @json relevant details about the user
	 * 
	 */	
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@GetMapping(path = "/user", produces = "application/json")
	public ResponseEntity<?> readByUsername(@RequestParam @Pattern(regexp = "^[a-z0-9_-]{5,}$",
													message = SessionValidators.USERNAME_VALUE) String name) {		

		RegistrationResponse registrationResponse = registrationService.read(name);
		
		return ResponseEntity
				.status(HttpStatus.OK)
					.body(registrationResponse);
	}	
	
	/**
	 * 
	 * @description read user in the database
	 * 
	 * @authorization admin role required
	 * 
	 * @pathparam id
	 * 
	 * @success 200 Ok 
	 * @return @json relevant details about the user
	 * 
	 */	
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@GetMapping(path = "/user/{id}", produces = "application/json")
	public ResponseEntity<?> readById(@PathVariable long id) {		

		RegistrationResponse registrationResponse = registrationService.read(id);
		
		return ResponseEntity
				.status(HttpStatus.OK)
					.body(registrationResponse);
	}	
	
	/**
	 * 
	 * @description read all users in the database
	 * 
	 * @authorization admin role required
	 * 
	 * 
	 * @success 200 Ok 
	 * @return @json relevant details about the users
	 * 
	 */	
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@GetMapping(path = "/users", produces = "application/json")
	public ResponseEntity<?> readAll() {		

		List<RegistrationResponse> registrationResponses = registrationService.readAll();
		
		return ResponseEntity
				.status(HttpStatus.OK)
					.body(registrationResponses);
	}	
	
	/**
	 * 
	 * @description update user in the database
	 * 
	 * @authorization admin role required
	 * 
	 * @pathparam id
	 * 
	 * @bodyparam enabled
	 * @bodyparam authorities
	 * 
	 * @success 200
	 * @return @json relevant details about updated user
	 * 
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 * 
	 */	
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@PatchMapping(path = "/user/{id}", consumes = "application/json")
	public ResponseEntity<?> patch(@PathVariable long id, @RequestBody RegistrationRequest registrationRequest) throws IllegalArgumentException, IllegalAccessException {		

		RegistrationResponse registrationResponse = registrationService.patch(id, registrationRequest);
		
		return ResponseEntity
				.status(HttpStatus.OK)
					.body(registrationResponse);
	}	

	/**
	 * 
	 * @description delete user in the database
	 * 
	 * @authorization admin role required
	 * 
	 * @pathparam id
	 * 
	 * @success 204 No Content 
	 * 
	 */	
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@DeleteMapping(path = "/user/{id}")
	public ResponseEntity<?> delete(@PathVariable long id) {		

		registrationService.delete(id);
		
		return ResponseEntity
				.status(HttpStatus.NO_CONTENT)
					.build();
	}	
}
