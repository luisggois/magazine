package com.luisgois.magazine.service.publication.article;


import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.luisgois.magazine.dao.publication.ArticleRepository;
import com.luisgois.magazine.dao.publication.AuthorRepository;
import com.luisgois.magazine.dto.publication.article.ArticleRequest;
import com.luisgois.magazine.dto.publication.article.ArticleResponse;
import com.luisgois.magazine.exception.custom.ArticleNotFoundException;
import com.luisgois.magazine.exception.custom.AuthorNotFoundException;
import com.luisgois.magazine.exception.enumeration.DataIntegrity;
import com.luisgois.magazine.models.publication.Article;
import com.luisgois.magazine.models.publication.Author;
import com.luisgois.magazine.utils.reflection.MapperReflection;

@Service
public class ArticleService {

	@Autowired
	AuthorRepository authorRepository;

	@Autowired
	ArticleRepository articleRepository;

	@Autowired
	MapperReflection processorReflection;

	public ArticleResponse create(ArticleRequest articleRequest) {
		
		// validate author id
		Optional<Author> author = authorRepository.findById(articleRequest.getAuthorId());
		
		author.orElseThrow(() -> new AuthorNotFoundException(DataIntegrity.NONEXISTENT_AUTHOR));

		// create new article
		Article article = new Article(articleRequest.getTitle(),articleRequest.getSubject(),articleRequest.getBody(),author.get());

		// save new author
		articleRepository.save(article);		

		return new ArticleResponse(article);		
	}

	public ArticleResponse read(Long id) {	

		// find article by id
		Optional<Article> article = articleRepository.findById(id);

		return new ArticleResponse(article.orElseThrow(() -> 
			new ArticleNotFoundException(DataIntegrity.NONEXISTENT_ARTICLE)));	
	}

	public List<ArticleResponse> read(String title) {	

		// find article(s) by title
		List<Article> articles = articleRepository.findByTitle(title);

		return articles
				.stream()
				.map(article -> new ArticleResponse(article))
				.collect(Collectors.toList());
	}

	public List<ArticleResponse> readAll() {	

		// find all articles
		List<Article> articles = articleRepository.findAll();

		return articles
				.stream()
				.map(article -> new ArticleResponse(article))
				.collect(Collectors.toList());
	}

	public ArticleResponse patch(Long id, ArticleRequest articleRequest) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException{	

		// find article by id
		Optional<Article> articleOptional = articleRepository.findById(id);

		articleOptional.orElseThrow(() -> new ArticleNotFoundException(DataIntegrity.NONEXISTENT_ARTICLE));

		// replace article details
		Article article = processorReflection.mapper(articleRequest, articleOptional.get());
		
		// check if user also wants to update author of the post
		if((Long) articleRequest.getAuthorId() != null) {			
			
			// validate author id
			Optional<Author> author = authorRepository.findById(articleRequest.getAuthorId());
			
			article.setAuthor(author
					.orElseThrow(() -> new AuthorNotFoundException(DataIntegrity.NONEXISTENT_AUTHOR)));
		}
		
		// save updated article
		articleRepository.save(article);	

		return new ArticleResponse(article);
	}

	public void delete(Long id) {

		// find article by id
		Optional<Article> article = articleRepository.findById(id);
		
		articleRepository.delete(article.orElseThrow(() -> 
			new ArticleNotFoundException(DataIntegrity.NONEXISTENT_ARTICLE)	
		));		
	}



}
