/**
 * 
 */
package com.luisgois.magazine.controller.publication;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.Size;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.luisgois.magazine.dto.publication.author.AuthorRequest;
import com.luisgois.magazine.dto.publication.author.AuthorResponse;
import com.luisgois.magazine.exception.constant.PublicationValidators;
import com.luisgois.magazine.service.publication.author.AuthorService;

/**
 * @author luisgois
 *
 */
@RestController
public class AuthorController {
	
	@Autowired
	AuthorService authorService;
	
	/**
	 * 
	 * @description place new author in the database
	 * 
	 * @authorization admin role required
	 * 
	 * @bodyparam name
	 * @bodyparam company
	 * @bodyparam job
	 * 
	 * @success 201 Created 
	 * @return @json relevant details about the new created author
	 * 
	 */	
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@PostMapping(path = "/authors", consumes = "application/json", produces = "application/json")
	public ResponseEntity<?> create(@Valid @RequestBody AuthorRequest authorRequest) {		
		
		AuthorResponse authorResponse = authorService.create(authorRequest);			
			
		return ResponseEntity
				.status(HttpStatus.CREATED)
					.body(authorResponse);
	}	
	
	/**
	 * 
	 * @description read authors in the database with specific name
	 * 
	 * @authorization authentication required
	 * 
	 * @queryparam author name
	 * 
	 * @success 200 Ok 
	 * @return @json relevant details about the authors with that name
	 * 
	 */	
	@GetMapping(path = "/author", produces = "application/json")
	public ResponseEntity<?> readByUsername(@RequestParam @Size(min=3, 
													message = PublicationValidators.NAME_VALUE) String name) {		

		List<AuthorResponse> authorResponses = authorService.read(name);
		
		return ResponseEntity
				.status(HttpStatus.OK)
					.body(authorResponses);
	}	
	
	/**
	 * 
	 * @description read author in the database
	 * 
	 * @authorization authentication required
	 * 
	 * @pathparam id
	 * 
	 * @success 200 Ok 
	 * @return @json relevant details about the author
	 * 
	 */	
	@GetMapping(path = "/author/{id}", produces = "application/json")
	public ResponseEntity<?> readById(@PathVariable long id) {		

		AuthorResponse authorResponse = authorService.read(id);
		
		return ResponseEntity
				.status(HttpStatus.OK)
					.body(authorResponse);
	}	
	
	/**
	 * 
	 * @description read all authors in the database
	 * 
	 * @authorization authentication required
	 * 
	 * 
	 * @success 200 Ok 
	 * @return @json relevant details about the authors
	 * 
	 */	
	@GetMapping(path = "/authors", produces = "application/json")
	public ResponseEntity<?> readAll() {		

		List<AuthorResponse> authorResponses = authorService.readAll();
		
		return ResponseEntity
				.status(HttpStatus.OK)
					.body(authorResponses);
	}
	
	/**
	 * 
	 * @description update author in the database
	 * 
	 * @authorization admin role required
	 * 
	 * @pathparam id
	 * 
	 * @bodyparam name
	 * @bodyparam company
	 * @bodyparam job
	 * 
	 * @success 200
	 * @return @json relevant details about updated author
	 * 
	 * @throws SecurityException 
	 * @throws NoSuchFieldException 
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 * 
	 */	
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@PatchMapping(path = "/author/{id}", consumes = "application/json")
	public ResponseEntity<?> patch(@PathVariable long id, @RequestBody AuthorRequest authorRequest) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException  {			

			AuthorResponse authorResponse = authorService.patch(id, authorRequest);
			
			return ResponseEntity
					.status(HttpStatus.OK)
						.body(authorResponse);
		
	}	
	
	/**
	 * 
	 * @description delete author in the database
	 * 
	 * @authorization admin role required
	 * 
	 * @pathparam id
	 * 
	 * @success 204 No Content 
	 * 
	 */	
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@DeleteMapping(path = "/author/{id}")
	public ResponseEntity<?> delete(@PathVariable long id) {		

		authorService.delete(id);
		
		return ResponseEntity
				.status(HttpStatus.NO_CONTENT)
					.build();
	}	

}
