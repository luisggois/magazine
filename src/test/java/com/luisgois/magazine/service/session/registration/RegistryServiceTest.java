package com.luisgois.magazine.service.session.registration;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.luisgois.magazine.dao.session.RoleRepository;
import com.luisgois.magazine.dao.session.UserRepository;
import com.luisgois.magazine.dto.session.registration.Permission;
import com.luisgois.magazine.dto.session.registration.RegistrationRequest;
import com.luisgois.magazine.dto.session.registration.RegistrationResponse;
import com.luisgois.magazine.exception.custom.UserNotFoundException;
import com.luisgois.magazine.models.session.Role;
import com.luisgois.magazine.models.session.User;
import com.luisgois.magazine.utils.reflection.MapperReflection;

@ExtendWith(SpringExtension.class)
class RegistryServiceTest {

	@TestConfiguration
	static class TestConfig{

		@Bean
		RegistryService getRegistryService() {
			return new RegistryService();
		}

	}

	@Autowired
	RegistryService registryService;

	@MockBean
	PasswordEncoder passwordEncoder;

	@MockBean
	UserRepository userRepository;

	@MockBean
	RoleRepository roleRepository;

	@MockBean
	MapperReflection processorReflection;

	private String expectedUsername;
	private String expectedPassword;
	private boolean expectedIsEnabled;
	private Permission[] expectedPermissions;	
	private User expectedUser;

	@BeforeEach
	void setUp() {		
		expectedUsername = "strong-user";
		expectedPassword = "Str0ng@Password";
		expectedIsEnabled = true;		
		expectedPermissions = new Permission[] {Permission.ROLE_USER};

		expectedUser = new User(expectedUsername,passwordEncoder.encode(expectedPassword),expectedIsEnabled);
		expectedUser.setRoles(Stream
				.of(expectedPermissions)
				.map(Role::new)
				.collect(Collectors.toList()));			
	}
	
	@Test
	@DisplayName("when create(RegistrationRequest registrationRequest) should create user and return RegistrationResponse entity")
	@WithMockUser(username = "test", password = "test", roles = "ADMIN")
	void createUser() throws Exception {	
		
		//given
		RegistrationRequest registrationRequest = new RegistrationRequest(
				expectedUsername,expectedPassword,expectedIsEnabled,expectedPermissions);
		
		// when
		when(userRepository.save(Mockito.isA(User.class)))
		.thenReturn(expectedUser);
		
		// then
		RegistrationResponse actual = registryService.create(registrationRequest);
		
		System.out.println(actual.getId());
		System.out.println(expectedUser.getId());

		assertAll(
				() -> assertNotEquals(expectedUser.getId(), actual.getId()),
				() -> assertEquals(expectedUsername, actual.getUsername()),
				() -> assertEquals(expectedIsEnabled, actual.isEnabled()),
				() -> assertTrue(Arrays.stream(expectedPermissions).anyMatch(actual.getAuthorities()::contains))
				);			
	}
	
	@Test
	@DisplayName("when read(String username) should read user based on username and return RegistrationResponse entity")
	@WithMockUser(username = "test", password = "test", roles = "ADMIN")
	void readUserByUsername() throws Exception {

		// expected
		Optional<User> userOptional = Optional.ofNullable(expectedUser);

		// when
		when(userRepository.findByUsername(expectedUser.getUsername()))
		.thenReturn(userOptional);

		// then
		RegistrationResponse actual = registryService.read(expectedUser.getUsername());
		verify(userRepository).findByUsername(Mockito.isA(String.class));	

		assertAll(
				() -> assertEquals(expectedUsername, actual.getUsername()),
				() -> assertEquals(expectedIsEnabled, actual.isEnabled()),
				() -> assertTrue(Arrays.stream(expectedPermissions).anyMatch(actual.getAuthorities()::contains))
				);	

	}

	@Test
	@DisplayName("when read(Long id) should read user based on id and return RegistrationResponse entity")
	@WithMockUser(username = "test", password = "test", roles = "ADMIN")
	void readUserById() throws Exception {

		// expected
		Optional<User> userOptional = Optional.ofNullable(expectedUser);

		// when
		when(userRepository.findById(expectedUser.getId()))
		.thenReturn(userOptional);

		// then
		RegistrationResponse actual = registryService.read(expectedUser.getId());
		verify(userRepository).findById(Mockito.isA(Long.class));	

		assertAll(
				() -> assertEquals(expectedUsername, actual.getUsername()),
				() -> assertEquals(expectedIsEnabled, actual.isEnabled()),
				() -> assertTrue(Arrays.stream(expectedPermissions).anyMatch(actual.getAuthorities()::contains))
				);	

	}
	
	@Test
	@DisplayName("when readAll() should read all existing users and return RegistrationResponse entity")
	@WithMockUser(username = "test", password = "test", roles = "ADMIN")
	void readAllUsers() throws Exception {

		// expected
		List<User> users = Arrays.asList(expectedUser);

		List<RegistrationResponse> expectedUsers = users
				.stream()
				.map(user -> new RegistrationResponse(user))
				.collect(Collectors.toList());

		// when
		when(userRepository.findAll())
		.thenReturn(users);

		// then
		List<RegistrationResponse> actual = registryService.readAll();
		verify(userRepository).findAll();	

		assertTrue(expectedUsers.stream().allMatch(actual::contains));
	}

	@Test
	@DisplayName("when delete(Long id) should delete user based on id and return nothing")
	@WithMockUser(username = "test", password = "test", roles = "ADMIN")
	void deleteUserById() throws Exception {

		// expected
		Optional<User> userOptional = Optional.ofNullable(expectedUser);

		// when
		when(userRepository.findById(expectedUser.getId()))
		.thenReturn(userOptional);

		doNothing()
		.when(userRepository)
		.delete(userOptional.get());

		// then
		registryService.delete(expectedUser.getId());

		verify(userRepository).findById(Mockito.isA(Long.class));	
		verify(userRepository).delete(Mockito.isA(User.class));	
	}

	@Nested
	class ExceptionThrowTest {

		@BeforeEach
		public void setUp() {			
			// reset stubbing to avoid conflicts on verify 
			Mockito.reset(userRepository);
			
			// when
			Optional<User> userOptional = Optional.empty();
			when(userRepository.findByUsername(expectedUser.getUsername()))
			.thenReturn(userOptional);
			when(userRepository.findById(expectedUser.getId()))
			.thenReturn(userOptional);
		}

		@Test
		@DisplayName("when read(String username) should throw UserNotFoundException because user not in database")
		@WithMockUser(username = "test", password = "test", roles = "ADMIN")
		void readUserByUsername() throws Exception {
			assertThrows(UserNotFoundException.class,() -> registryService.read(expectedUser.getUsername()));
			verify(userRepository).findByUsername(Mockito.isA(String.class));
		}

		@Test
		@DisplayName("when read(Long id) should throw UserNotFoundException because user not in database")
		@WithMockUser(username = "test", password = "test", roles = "ADMIN")
		void readUserById() throws Exception {
			assertThrows(UserNotFoundException.class,() -> registryService.read(expectedUser.getId()));
			verify(userRepository).findById(Mockito.isA(Long.class));
		}		

		@Test
		@DisplayName("when patch(Long id, RegistrationRequest registrationRequest) should throw UserNotFoundException because user not in database")
		@WithMockUser(username = "test", password = "test", roles = "ADMIN")
		void patchUserById() throws Exception {
			assertThrows(UserNotFoundException.class,() -> registryService.patch(expectedUser.getId(),new RegistrationRequest()));
			verify(userRepository).findById(Mockito.isA(Long.class));
		}	

		@Test
		@DisplayName("when delete(Long id) should throw UserNotFoundException because user not in database")
		@WithMockUser(username = "test", password = "test", roles = "ADMIN")
		void deleteUserById() throws Exception {
			assertThrows(UserNotFoundException.class,() -> registryService.delete(expectedUser.getId()));
			verify(userRepository).findById(Mockito.isA(Long.class));			
		}

	}
}
