package com.luisgois.magazine.dao.publication;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.luisgois.magazine.models.publication.Author;

/**
 * @author luisgois
 *
 */
public interface AuthorRepository extends JpaRepository<Author, Long>{

	List<Author> findByName(String name);
	
}