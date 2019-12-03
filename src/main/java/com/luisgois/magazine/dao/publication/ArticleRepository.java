/**
 * 
 */
package com.luisgois.magazine.dao.publication;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.luisgois.magazine.models.publication.Article;

/**
 * @author luisgois
 *
 */
public interface ArticleRepository extends JpaRepository<Article,Long> {

	List<Article> findByTitle(String title);
	
}
