/**
 * 
 */
package com.luisgois.magazine.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.luisgois.magazine.service.session.authentication.CustomUserDetailsService;
import com.luisgois.magazine.utils.jwt.JwtHelper;

/**
 * @author luisgois
 *
 */

@Component
public class JwtAuthentication extends OncePerRequestFilter {

	private static final String PREFIX = "Bearer ";

	@Autowired
	JwtHelper jwtHelper;

	@Autowired
	CustomUserDetailsService newUserDetailsService;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		String jwtToken = null;
		String username = null;
				
		final String authorizationHeader = request.getHeader("Authorization");
				
		try {
			if (authorizationHeader != null) {
				jwtToken = getToken(authorizationHeader);
				username = getUsername(jwtToken);	
			}
		} catch (Exception e) {
			System.err.println("Authorization header does not contain a valid jwt. Error throwed: " + e);
		}
				
		if (username != null && SecurityContextHolder.getContext().getAuthentication() == null){
			
			UserDetails userDetails = newUserDetailsService.loadUserByUsername(username);

			if (jwtHelper.validateToken(jwtToken, userDetails)) {			
				UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = 
						new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());
				usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
			}
		}
		
		filterChain.doFilter(request, response);

	}

	private String getToken(@NonNull String token){			
		if (token.startsWith(PREFIX)){	
			return token.split(PREFIX)[1];		
		}
		throw new IllegalArgumentException("Unexpected authorization header: " + token);		
	}

	private String getUsername(@NonNull String token){			
		return jwtHelper.extractUsername(token);
	}

}
