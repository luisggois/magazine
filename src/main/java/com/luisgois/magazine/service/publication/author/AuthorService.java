package com.luisgois.magazine.service.publication.author;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.luisgois.magazine.dao.publication.AuthorRepository;
import com.luisgois.magazine.dto.publication.author.AuthorRequest;
import com.luisgois.magazine.dto.publication.author.AuthorResponse;
import com.luisgois.magazine.exception.custom.AuthorNotFoundException;
import com.luisgois.magazine.exception.enumeration.DataIntegrity;
import com.luisgois.magazine.models.publication.Author;
import com.luisgois.magazine.utils.reflection.MapperReflection;

@Service
public class AuthorService {

	@Autowired
	AuthorRepository authorRepository;
	
	@Autowired
	MapperReflection processorReflection;

	public AuthorResponse create(AuthorRequest authorRequest) {
		
		// create a new author
		Author author = new Author(authorRequest.getName(),authorRequest.getCompany(),authorRequest.getJob());
		
		// save new author
		authorRepository.save(author);		
								
		return new AuthorResponse(author);		
	}

	public AuthorResponse read(Long id) {	

		// find author by id
		Optional<Author> author = authorRepository.findById(id);

		return new AuthorResponse(author.orElseThrow(() -> 
			new AuthorNotFoundException(DataIntegrity.NONEXISTENT_AUTHOR)));	
	}

	public List<AuthorResponse> read(String name) {	

		// find author(s) by username
		List<Author> authors = authorRepository.findByName(name);

		return authors
				.stream()
					.map(author -> new AuthorResponse(author))
						.collect(Collectors.toList());
	}

	public List<AuthorResponse> readAll() {	

		// find all authors
		List<Author> authors = authorRepository.findAll();

		return authors
				.stream()
					.map(author -> new AuthorResponse(author))
						.collect(Collectors.toList());	
	}

	public AuthorResponse patch(Long id, AuthorRequest authorRequest) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException{	

		// find author by id
		Optional<Author> authorOptional = authorRepository.findById(id);

		authorOptional.orElseThrow(() -> new AuthorNotFoundException(DataIntegrity.NONEXISTENT_AUTHOR));

		// replace author details
		Author author = processorReflection.mapper(authorRequest, authorOptional.get());
		
		// save updated author
		authorRepository.save(author);	
				
		return new AuthorResponse(author);
	}

	public void delete(Long id) {

		// find author by id
		Optional<Author> author = authorRepository.findById(id);

		authorRepository.delete(author.orElseThrow(() -> 
			new AuthorNotFoundException(DataIntegrity.NONEXISTENT_AUTHOR)	
		));		
	}

}
