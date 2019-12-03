/**
 * 
 */
package com.luisgois.magazine.controller.session;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.luisgois.magazine.dto.session.authentication.AuthenticationRequest;
import com.luisgois.magazine.dto.session.authentication.AuthenticationResponse;
import com.luisgois.magazine.dto.session.registration.Permission;
import com.luisgois.magazine.dto.session.registration.RegistrationRequest;
import com.luisgois.magazine.dto.session.registration.RegistrationResponse;
import com.luisgois.magazine.models.session.Role;
import com.luisgois.magazine.models.session.User;
import com.luisgois.magazine.service.session.authentication.CustomUserDetails;
import com.luisgois.magazine.service.session.authentication.CustomUserDetailsService;
import com.luisgois.magazine.service.session.registration.RegistryService;
import com.luisgois.magazine.utils.jwt.JwtHelper;

/**
 * @author luisgois
 *
 */
@ExtendWith(SpringExtension.class)
@WebMvcTest(SessionController.class)
class SessionControllerTest {

	@Autowired
	MockMvc mockMvc;

	@Autowired
	ObjectMapper objectMapper;
	
	@Autowired
	PasswordEncoder passwordEncoder;

	@MockBean
	AuthenticationManager authenticationManager;
	
	@Mock
	Authentication authentication;

	@MockBean
	CustomUserDetailsService newUserDetailsService;

	@MockBean
	JwtHelper jwtHelper;

	@MockBean
	RegistryService registrationService;

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
	@DisplayName("when post('/authenticate') should return 200 Ok & AuthenticationResponse entity")
	void authenticate() throws Exception {

		// expected
		String expectedMessage = "Authentication has been successful";
		String expectedToken = "notreal";
		UserDetails expectedPrincipal = new CustomUserDetails(expectedUser);

		// given		
		AuthenticationRequest authenticationRequest = new AuthenticationRequest(expectedUsername,expectedPassword);
		String json = objectMapper.writeValueAsString(authenticationRequest);

		// when		
		when(authenticationManager.authenticate(Mockito.isA(UsernamePasswordAuthenticationToken.class)))
		.thenReturn(authentication);
		
        when(authentication.getPrincipal())
        .thenReturn(expectedPrincipal);

		when(jwtHelper.generateToken(Mockito.isA(UserDetails.class)))
		.thenReturn(expectedToken);

		// then
		String route = "/authenticate";
		String responseBody = mockMvc
				.perform(post(route)
						.contentType(MediaType.APPLICATION_JSON)
						.content(json))
				.andExpect(status().isOk())
				.andExpect(content().contentType("application/json"))
				.andReturn().getResponse().getContentAsString();	

		verify(authenticationManager).authenticate(Mockito.isA(UsernamePasswordAuthenticationToken.class));
		verify(authentication).getPrincipal();
		verify(jwtHelper).generateToken(Mockito.isA(UserDetails.class));

		AuthenticationResponse actual = objectMapper.readValue(responseBody, AuthenticationResponse.class);
		assertAll(
				() -> assertEquals(expectedMessage, actual.getMessage()),
				() -> assertEquals(expectedToken, actual.getJwt())
				);				
	}

	@Test
	@DisplayName("when post('/users') should return 201 Created & RegistrationResponse entity")
	@WithMockUser(username = "test", password = "test", roles = "ADMIN")
	void createUser() throws Exception {

		// given		
		RegistrationRequest registrationRequest = new RegistrationRequest(
				expectedUsername,expectedPassword,expectedIsEnabled,expectedPermissions);
		String json = objectMapper.writeValueAsString(registrationRequest);

		// when			
		when(registrationService.create(Mockito.isA(RegistrationRequest.class)))
		.thenReturn(new RegistrationResponse(expectedUser));

		// then
		String route = "/users";		
		String responseBody = mockMvc
				.perform(post(route)
						.contentType(MediaType.APPLICATION_JSON)
						.content(json))
				.andExpect(status().isCreated())
				.andExpect(content().contentType("application/json"))
				.andReturn().getResponse().getContentAsString();	

		verify(registrationService).create(Mockito.isA(RegistrationRequest.class));

		RegistrationResponse actual = objectMapper.readValue(responseBody, RegistrationResponse.class);
		assertAll(
				() -> assertEquals(expectedUser.getId(), actual.getId()),
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
		when(registrationService.read(Mockito.isA(String.class)))
		.thenReturn(new RegistrationResponse(expectedUser));

		// then
		String route = String.format("/user?name=%s",expectedUser.getUsername());
		String responseBody = mockMvc
				.perform(get(route))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andReturn().getResponse().getContentAsString();

		verify(registrationService).read(Mockito.isA(String.class));

		RegistrationResponse actual = objectMapper.readValue(responseBody, RegistrationResponse.class);
		assertAll(
				() -> assertEquals(expectedUser.getId(), actual.getId()),
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
		when(registrationService.read(Mockito.isA(Long.class)))
		.thenReturn(new RegistrationResponse(expectedUser));

		// then
		String route = String.format("/user/%d",expectedUser.getId());
		String responseBody = mockMvc
				.perform(get(route))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andReturn().getResponse().getContentAsString();

		verify(registrationService).read(Mockito.isA(Long.class));

		RegistrationResponse actual = objectMapper.readValue(responseBody, RegistrationResponse.class);
		assertAll(
				() -> assertEquals(expectedUser.getId(), actual.getId()),
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
		when(registrationService.patch(Mockito.isA(Long.class),Mockito.isA(RegistrationRequest.class)))
		.thenReturn(new RegistrationResponse(expectedUser));

		// then
		String route = String.format("/user/%d",expectedUser.getId());
		String responseBody = mockMvc
				.perform(patch(route)
						.contentType(MediaType.APPLICATION_JSON)
						.content(json))
				.andExpect(status().isOk())
				.andExpect(content().contentType("application/json"))
				.andReturn().getResponse().getContentAsString();

		verify(registrationService).patch(Mockito.isA(Long.class),Mockito.isA(RegistrationRequest.class));

		RegistrationResponse actual = objectMapper.readValue(responseBody, RegistrationResponse.class);
		assertAll(
				() -> assertEquals(expectedUser.getId(), actual.getId()),
				() -> assertEquals(expectedUsername, actual.getUsername()),
				() -> assertEquals(expectedIsEnabled, actual.isEnabled()),
				() -> assertTrue(Arrays.stream(expectedPermissions).anyMatch(actual.getAuthorities()::contains))
				);		

	}

	@Test
	@DisplayName("when delete('/user/{id}') should return 204 No Content")
	@WithMockUser(username = "test", password = "test", roles = "ADMIN")
	void deleteUserById() throws Exception {
		
		// when
		doNothing()
		.when(registrationService)
		.delete(Mockito.isA(Long.class));

		// then
		String route = String.format("/user/%d",expectedUser.getId());		
		mockMvc
		.perform(delete(route))
		.andExpect(status().isNoContent());

		verify(registrationService).delete(Mockito.isA(Long.class));	
	}

}
