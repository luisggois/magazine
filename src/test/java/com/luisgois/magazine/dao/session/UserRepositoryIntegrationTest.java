/**
 * 
 */
package com.luisgois.magazine.dao.session;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.luisgois.magazine.dto.session.registration.Permission;
import com.luisgois.magazine.models.session.Role;
import com.luisgois.magazine.models.session.User;

/**
 * @author luisgois
 */
@ExtendWith(SpringExtension.class)
@DataJpaTest
class UserRepositoryIntegrationTest {
	
    @Autowired
    TestEntityManager entityManager;

	@Autowired
	UserRepository userRepository;

	/**
	 * @description place user in the DB with ROLE_USER and test its retrieval
	 */
	@Test
	@DisplayName("when username exists findByUsername should return User entity")
	void whenFindByUsername_thenReturnUser(){
		
		// expected
		String expectedUsername = "strong-user";
		String expectedPassword = "Str0ng@Password";
		boolean expectedIsEnabled = true;
		
		Permission[] expectedPermissions = {Permission.ROLE_USER};
		List<Role> expectedRoles = Stream.of(expectedPermissions)
										.map(Role::new)
											.collect(Collectors.toList());	
		
		// given
		User persistedUser = new User(expectedUsername,expectedPassword,expectedIsEnabled); 	
		persistedUser.setRoles(expectedRoles);
		
		entityManager.persist(persistedUser);
		entityManager.flush();
		
		// when
		Optional<User> userOptional = userRepository.findByUsername("strong-user");
		
		// then
		assertTrue(userOptional.isPresent());
		User retrievedUser =  userOptional.get();
		entityManager.refresh(retrievedUser);
		
		assertAll(
			() -> assertEquals(expectedUsername, retrievedUser.getUsername()),
			() -> assertEquals(expectedPassword, retrievedUser.getPassword()),
			() -> assertEquals(expectedIsEnabled, retrievedUser.isEnabled()),
			() -> assertEquals(expectedRoles, retrievedUser.getRoles())
		);			
	}
	
	@Test
	@DisplayName("when username doesnt exist findByUsername should return null value")
	void whenFindByUsername_thenReturnEmpty(){
		
		// given
		String nonExistentUsername = "strong-user";
		
		// when
		Optional<User> userOptional = userRepository.findByUsername(nonExistentUsername);
		
		// then
		assertFalse(userOptional.isPresent());
	}
	
	

}
