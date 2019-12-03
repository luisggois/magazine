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

import com.luisgois.magazine.dto.publication.article.ArticleRequest;
import com.luisgois.magazine.dto.publication.article.ArticleResponse;
import com.luisgois.magazine.exception.constant.PublicationValidators;
import com.luisgois.magazine.service.publication.article.ArticleService;

/**
 * @author luisgois
 *
 */

@RestController
public class ArticleController {
	
	@Autowired
	ArticleService articleService;
	
	/**
	 * 
	 * @description place new article in the database
	 * 
	 * @authorization admin role required
	 * 
	 * @bodyparam title
	 * @bodyparam subject
	 * @bodyparam body
	 * @bodyparam authorId
	 * 
	 * @success 201 Created 
	 * @return @json relevant details about the new created article
	 * 
	 */	
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@PostMapping(path = "/articles", consumes = "application/json", produces = "application/json")
	public ResponseEntity<?> create(@Valid @RequestBody ArticleRequest articleRequest) {		
			
		ArticleResponse articleResponse = articleService.create(articleRequest);			
			
		return ResponseEntity
				.status(HttpStatus.CREATED)
					.body(articleResponse);
	}	
	
	/**
	 * 
	 * @description read articles in the database with specific title
	 * 
	 * @authorization authentication required
	 * 
	 * @queryparam title name
	 * 
	 * @success 200 Ok 
	 * @return @json relevant details about the articles with that title
	 * 
	 */	
	@GetMapping(path = "/article", produces = "application/json")
	public ResponseEntity<?> readByUsername(@RequestParam @Size(min=3,
													message = PublicationValidators.TITLE_VALUE) String title) {		

		List<ArticleResponse> articleResponses = articleService.read(title);
		
		return ResponseEntity
				.status(HttpStatus.OK)
					.body(articleResponses);
	}	
	
	/**
	 * 
	 * @description read article in the database
	 * 
	 * @authorization authentication required
	 * 
	 * @pathparam id
	 * 
	 * @success 200 Ok 
	 * @return @json relevant details about the article
	 * 
	 */	
	@GetMapping(path = "/article/{id}", produces = "application/json")
	public ResponseEntity<?> readById(@PathVariable long id) {		

		ArticleResponse articleResponse = articleService.read(id);
		
		return ResponseEntity
				.status(HttpStatus.OK)
					.body(articleResponse);
	}	
	
	/**
	 * 
	 * @description read all articles in the database
	 * 
	 * @authorization authentication required
	 * 
	 * 
	 * @success 200 Ok 
	 * @return @json relevant details about the articles
	 * 
	 */	
	@GetMapping(path = "/articles", produces = "application/json")
	public ResponseEntity<?> readAll() {		

		List<ArticleResponse> articleResponses = articleService.readAll();
		
		return ResponseEntity
				.status(HttpStatus.OK)
					.body(articleResponses);
	}
	
	/**
	 * 
	 * @description update article in the database
	 * 
	 * @authorization admin role required
	 * 
	 * @pathparam id
	 * 
	 * @bodyparam title
	 * @bodyparam subject
	 * @bodyparam body
	 * @bodyparam authorId
	 * 
	 * @success 200
	 * 
	 * @return @json relevant details about updated article
	 * 
	 * @throws SecurityException 
	 * @throws NoSuchFieldException 
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 * 
	 */	
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@PatchMapping(path = "/article/{id}", consumes = "application/json")
	public ResponseEntity<?> patch(@PathVariable long id, @RequestBody ArticleRequest articleRequest) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {			

			ArticleResponse articleResponse = articleService.patch(id, articleRequest);
			
			return ResponseEntity
					.status(HttpStatus.OK)
						.body(articleResponse);
		
	}	
	
	/**
	 * 
	 * @description delete article in the database
	 * 
	 * @authorization admin role required
	 * 
	 * @pathparam id
	 * 
	 * @success 204 No Content 
	 * 
	 */	
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@DeleteMapping(path = "/article/{id}")
	public ResponseEntity<?> delete(@PathVariable long id) {		

		articleService.delete(id);
		
		return ResponseEntity
				.status(HttpStatus.NO_CONTENT)
					.build();
	}	

}
