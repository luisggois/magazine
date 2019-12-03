package com.luisgois.magazine.dao.session;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
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
class RoleRepositoryIntegrationTest {

	@Autowired
	TestEntityManager entityManager;

	@Autowired
	RoleRepository roleRepository;

	/**
	 * @description place two users in the DB with ROLE_ADMIN and test the role retrieval
	 */
	@Test
	@DisplayName("when authority exists findByAuthority should return Role entity")
	void whenFindByAuthority_thenReturnRole(){
		
		Permission[] expectedPermissions = {Permission.ROLE_ADMIN};
		List<Role> expectedRoles = Stream.of(expectedPermissions)
				.map(Role::new)
				.collect(Collectors.toList());	
		
		String expectedUsernameOne = "strong-user-1";
		String expectedPasswordOne = "Str0ng@Password1";
		boolean expectedIsEnabledOne = true;

		String expectedUsernameTwo = "strong-user-2";
		String expectedPasswordTwo = "Str0ng@Password2";
		boolean expectedIsEnabledTwo = true;

		// given
		User persistedUserOne = new User(expectedUsernameOne,expectedPasswordOne,expectedIsEnabledOne); 	
		persistedUserOne.setRoles(expectedRoles);
		User persistedUserTwo = new User(expectedUsernameTwo,expectedPasswordTwo,expectedIsEnabledTwo);
		persistedUserTwo.setRoles(expectedRoles);

		entityManager.persist(persistedUserOne);
		entityManager.persist(persistedUserTwo);
		entityManager.flush();
						
		// when
		Optional<Role> roleOptional = roleRepository.findByAuthority(Permission.ROLE_ADMIN);
		
		// then		
		assertTrue(roleOptional.isPresent());
		Role retrievedRole =  roleOptional.get();
		entityManager.refresh(retrievedRole);
		
		assertAll( 
			() -> assertTrue(Arrays.stream(expectedPermissions)
								.anyMatch(auth -> auth == retrievedRole.getAuthority())),
			() -> assertTrue(retrievedRole.getUsers().contains(persistedUserOne)),
			() -> assertTrue(retrievedRole.getUsers().contains(persistedUserTwo))	
		);
	}

	@Test
	@DisplayName("when authority doesnt exist findByAuthority should return null value")
	void whenFindByAuthority_thenReturnEmpty(){
		
		// given
		Permission nonExistentAuthorityYet = Permission.ROLE_ADMIN;

		// when
		Optional<Role> roleOptional = roleRepository.findByAuthority(nonExistentAuthorityYet);
		
		// then
		assertFalse(roleOptional.isPresent());
	}


}
