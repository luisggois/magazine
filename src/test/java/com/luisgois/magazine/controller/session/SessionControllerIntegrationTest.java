/**
 * 
 */
package com.luisgois.magazine.controller.session;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.luisgois.magazine.dao.session.UserRepository;
import com.luisgois.magazine.dto.exception.ExceptionResponse;
import com.luisgois.magazine.dto.session.authentication.AuthenticationRequest;
import com.luisgois.magazine.dto.session.authentication.AuthenticationResponse;
import com.luisgois.magazine.dto.session.registration.Permission;
import com.luisgois.magazine.dto.session.registration.RegistrationRequest;
import com.luisgois.magazine.dto.session.registration.RegistrationResponse;
import com.luisgois.magazine.exception.enumeration.DataIntegrity;
import com.luisgois.magazine.models.session.Role;
import com.luisgois.magazine.models.session.User;
import com.luisgois.magazine.utils.jwt.JwtHelper;

/**
 * @author luisgois
 * @database h2, with hibernate FlushMode set to ALWAYS
 *
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional //rollback changes to the database after every test
class SessionControllerIntegrationTest {

	@Autowired
	MockMvc mockMvc;

	@Autowired
	ObjectMapper objectMapper;

	@Autowired
	UserRepository userRepository;

	@Autowired
	JwtHelper jwtHelper;

	@Autowired
	PasswordEncoder passwordEncoder;

	@Autowired
	SessionController sessionController;

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
	@DisplayName("when application context loads SessionController should be created")
	public void contexLoads() throws Exception {
		assertThat(sessionController).isNotNull();
	}

	@Test
	@DisplayName("when post('/authenticate') should return 200 Ok & AuthenticationResponse entity")
	void authenticate() throws Exception {

		// expected
		String expectedMessage = "Authentication has been successful";

		// given		
		AuthenticationRequest authenticationRequest = new AuthenticationRequest(expectedUsername,expectedPassword);
		String json = objectMapper.writeValueAsString(authenticationRequest);

		// when
		userRepository.save(expectedUser);

		// then
		String route = "/authenticate";
		String responseBody = mockMvc
				.perform(post(route)
						.contentType(MediaType.APPLICATION_JSON)
						.content(json))
				.andExpect(status().isOk())
				.andExpect(content().contentType("application/json"))
				.andReturn().getResponse().getContentAsString();	

		AuthenticationResponse actual = objectMapper.readValue(responseBody, AuthenticationResponse.class);
		assertEquals(expectedMessage, actual.getMessage());			
	}

	@Test
	@DisplayName("when post('/users') should return 201 Created & RegistrationResponse entity")
	@WithMockUser(username = "test", password = "test", roles = "ADMIN")
	void createUser() throws Exception {

		// given		
		RegistrationRequest registrationRequest = new RegistrationRequest(
				expectedUsername,expectedPassword,expectedIsEnabled,expectedPermissions);
		String json = objectMapper.writeValueAsString(registrationRequest);

		// then
		String route = "/users";		
		String responseBody = mockMvc
				.perform(post(route)
						.contentType(MediaType.APPLICATION_JSON)
						.content(json))
				.andExpect(status().isCreated())
				.andExpect(content().contentType("application/json"))
				.andReturn().getResponse().getContentAsString();	

		RegistrationResponse actual = objectMapper.readValue(responseBody, RegistrationResponse.class);
		assertAll(
				() -> assertEquals(expectedUsername, actual.getUsername()),
				() -> assertEquals(expectedIsEnabled, actual.isEnabled()),
				() -> assertTrue(Arrays.stream(expectedPermissions).anyMatch(actual.getAuthorities()::contains))
				);				
	}

	@Test
	@DisplayName("when get('/user?name=username') should return 200 Ok & RegistrationResponse entity")
	@WithMockUser(username = "test", password = "test", roles = "ADMIN")
	void readUserByUsername() throws Exception {

		// when
		userRepository.save(expectedUser);

		// then
		String route = String.format("/user?name=%s",expectedUser.getUsername());
		String responseBody = mockMvc
				.perform(get(route))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andReturn().getResponse().getContentAsString();

		RegistrationResponse actual = objectMapper.readValue(responseBody, RegistrationResponse.class);
		assertAll(
				() -> assertEquals(expectedUsername, actual.getUsername()),
				() -> assertEquals(expectedIsEnabled, actual.isEnabled()),
				() -> assertTrue(Arrays.stream(expectedPermissions).anyMatch(actual.getAuthorities()::contains))
				);		

	}

	@Test
	@DisplayName("when get('/user/{id}') should return 200 Ok & RegistrationResponse entity")
	@WithMockUser(username = "test", password = "test", roles = "ADMIN")
	void readUserById() throws Exception {

		// when
		userRepository.save(expectedUser);

		// then
		String route = String.format("/user/%d",expectedUser.getId());
		String responseBody = mockMvc
				.perform(get(route))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andReturn().getResponse().getContentAsString();

		RegistrationResponse actual = objectMapper.readValue(responseBody, RegistrationResponse.class);
		assertAll(
				() -> assertEquals(expectedUsername, actual.getUsername()),
				() -> assertEquals(expectedIsEnabled, actual.isEnabled()),
				() -> assertTrue(Arrays.stream(expectedPermissions).anyMatch(actual.getAuthorities()::contains))
				);		

	}

	@Test
	@DisplayName("when patch('/user/{id}') should return 200 Ok & RegistrationResponse entity")
	@WithMockUser(username = "test", password = "test", roles = "ADMIN")
	void patchUserById() throws Exception {

		// given		
		RegistrationRequest registrationRequest = new RegistrationRequest(
				expectedUsername,expectedPassword,expectedIsEnabled,expectedPermissions);
		String json = objectMapper.writeValueAsString(registrationRequest);

		// when
		userRepository.save(expectedUser);

		// then
		String route = String.format("/user/%d",expectedUser.getId());
		String responseBody = mockMvc
				.perform(patch(route)
						.contentType(MediaType.APPLICATION_JSON)
						.content(json))
				.andExpect(status().isOk())
				.andExpect(content().contentType("application/json"))
				.andReturn().getResponse().getContentAsString();

		RegistrationResponse actual = objectMapper.readValue(responseBody, RegistrationResponse.class);
		assertAll(
				() -> assertEquals(expectedUsername, actual.getUsername()),
				() -> assertEquals(expectedIsEnabled, actual.isEnabled()),
				() -> assertTrue(Arrays.stream(expectedPermissions).anyMatch(actual.getAuthorities()::contains))
				);		

	}

	@Test
	@DisplayName("when delete('/user/{id}') should return 204 No Content")
	@WithMockUser(username = "test", password = "test", roles = "ADMIN")
	void deleteUserById() throws Exception {

		// given
		userRepository.save(expectedUser);

		// then
		String route = String.format("/user/%d",expectedUser.getId());
		mockMvc
		.perform(delete(route))
		.andExpect(status().isNoContent());
	}

	/**
	 * 
	 * @description:
	 * 	- Test exception handling behaviour. 
	 * 	- RestExceptionHandler expected to be called as @ControllerAdvice
	 */	
	@Nested
	@ExtendWith(SpringExtension.class)
	@SpringBootTest
	@AutoConfigureMockMvc
	@Transactional //rollback changes to the database after every test
	class ExceptionHandlingTest {
		
		@Test
		@DisplayName("when post('/authenticate') should return 401 Unauthorized & ExceptionResponse entity")
		void authenticate() throws Exception {

			// expected
			String expectedMessage = "Bad credentials";
			HttpStatus expectedStatus = HttpStatus.UNAUTHORIZED;

			// given		
			AuthenticationRequest authenticationRequest = new AuthenticationRequest(expectedUsername,expectedPassword);
			String json = objectMapper.writeValueAsString(authenticationRequest);

			// then
			String route = "/authenticate";
			String responseBody = mockMvc
					.perform(post(route)
							.contentType(MediaType.APPLICATION_JSON)
							.content(json))
					.andExpect(status().isUnauthorized())
					.andExpect(content().contentType("application/json"))
					.andReturn().getResponse().getContentAsString();	

			ExceptionResponse actual = objectMapper.readValue(responseBody, ExceptionResponse.class);
			assertAll(
					() -> assertEquals(expectedMessage, actual.getMessage()),
					() -> assertEquals(expectedStatus, actual.getStatus())
					);				
		}

		@Test
		@DisplayName("when post('/users') should return 409 Conflict & ExceptionResponse entity")
		@WithMockUser(username = "test", password = "test", roles = "ADMIN")
		void createUser() throws Exception {
			
			// expected
			DataIntegrity expectedMessage = DataIntegrity.DUPLICATE_USER;
			HttpStatus expectedStatus = HttpStatus.CONFLICT;
		
			// given		
			RegistrationRequest registrationRequest = new RegistrationRequest(
					expectedUsername,expectedPassword,expectedIsEnabled,expectedPermissions);
			String json = objectMapper.writeValueAsString(registrationRequest);

			// when
			userRepository.save(expectedUser);
			
			// then
			String route = "/users";		
			String responseBody = mockMvc
					.perform(post(route)
							.contentType(MediaType.APPLICATION_JSON)
							.content(json))
					.andExpect(status().isConflict())
					.andExpect(content().contentType(MediaType.APPLICATION_JSON))
					.andReturn().getResponse().getContentAsString();

			ExceptionResponse actual = objectMapper.readValue(responseBody, ExceptionResponse.class);
			assertAll(
					() -> assertEquals(expectedMessage.toString(), actual.getMessage()),
					() -> assertEquals(expectedStatus, actual.getStatus())
					);	
			
		}

		@Test
		@DisplayName("when get('/user?name={non-existent-username}') should return 400 Bad Request & ExceptionResponse entity")
		@WithMockUser(username = "test", password = "test", roles = "ADMIN")
		void readUserByUsername() throws Exception {

			// expected
			DataIntegrity expectedMessage = DataIntegrity.NONEXISTENT_USER;
			HttpStatus expectedStatus = HttpStatus.BAD_REQUEST;

			// then
			String route = String.format("/user?name=%s",expectedUser.getUsername());
			String responseBody = mockMvc
					.perform(get(route))
					.andExpect(status().isBadRequest())
					.andExpect(content().contentType(MediaType.APPLICATION_JSON))
					.andReturn().getResponse().getContentAsString();

			ExceptionResponse actual = objectMapper.readValue(responseBody, ExceptionResponse.class);
			assertAll(
					() -> assertEquals(expectedMessage.toString(), actual.getMessage()),
					() -> assertEquals(expectedStatus, actual.getStatus())
					);		

		}

		@Test
		@DisplayName("when get('/user/{non-existent-id}') should return 400 Bad Request & ExceptionResponse entity")
		@WithMockUser(username = "test", password = "test", roles = "ADMIN")
		void readUserById() throws Exception {

			// expected
			DataIntegrity expectedMessage = DataIntegrity.NONEXISTENT_USER;
			HttpStatus expectedStatus = HttpStatus.BAD_REQUEST;

			// then
			String route = String.format("/user/%d",expectedUser.getId());
			String responseBody = mockMvc
					.perform(get(route))
					.andExpect(status().isBadRequest())
					.andExpect(content().contentType(MediaType.APPLICATION_JSON))
					.andReturn().getResponse().getContentAsString();

			ExceptionResponse actual = objectMapper.readValue(responseBody, ExceptionResponse.class);
			assertAll(
					() -> assertEquals(expectedMessage.toString(), actual.getMessage()),
					() -> assertEquals(expectedStatus, actual.getStatus())
					);				
		}
		
		@Test
		@DisplayName("when patch('/user/{id}') should return 400 Bad Request & ExceptionResponse entity")
		@WithMockUser(username = "test", password = "test", roles = "ADMIN")
		void patchUserById() throws Exception {
			
			// expected
			DataIntegrity expectedMessage = DataIntegrity.NONEXISTENT_USER;
			HttpStatus expectedStatus = HttpStatus.BAD_REQUEST;

			// given		
			RegistrationRequest registrationRequest = new RegistrationRequest(
					expectedUsername,expectedPassword,expectedIsEnabled,expectedPermissions);
			String json = objectMapper.writeValueAsString(registrationRequest);

			// then
			String route = String.format("/user/%d",expectedUser.getId());
			String responseBody = mockMvc
					.perform(patch(route)
							.contentType(MediaType.APPLICATION_JSON)
							.content(json))
					.andExpect(status().isBadRequest())
					.andExpect(content().contentType(MediaType.APPLICATION_JSON))
					.andReturn().getResponse().getContentAsString();

			ExceptionResponse actual = objectMapper.readValue(responseBody, ExceptionResponse.class);
			assertAll(
					() -> assertEquals(expectedMessage.toString(), actual.getMessage()),
					() -> assertEquals(expectedStatus, actual.getStatus())
					);					

		}

		@Test
		@DisplayName("when delete('/user/{non-existent-id}') should return 400 Bad Request & ExceptionResponse entity")
		@WithMockUser(username = "test", password = "test", roles = "ADMIN")
		void deleteUserById() throws Exception {

			// expected
			DataIntegrity expectedMessage = DataIntegrity.NONEXISTENT_USER;
			HttpStatus expectedStatus = HttpStatus.BAD_REQUEST;

			// then
			String route = String.format("/user/%d",expectedUser.getId());
			String responseBody = mockMvc
					.perform(delete(route))
					.andExpect(status().isBadRequest())
					.andExpect(content().contentType(MediaType.APPLICATION_JSON))
					.andReturn().getResponse().getContentAsString();

			ExceptionResponse actual = objectMapper.readValue(responseBody, ExceptionResponse.class);
			assertAll(
					() -> assertEquals(expectedMessage.toString(), actual.getMessage()),
					() -> assertEquals(expectedStatus, actual.getStatus())
					);				
		}
	}

}
