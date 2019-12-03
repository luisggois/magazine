/**
 * 
 */
package com.luisgois.magazine.dto.publication.article;

import com.luisgois.magazine.models.publication.Article;
import com.luisgois.magazine.models.publication.Author;

/**
 * @author luisgois
 *
 */
public class ArticleResponse {
	
	private long id;
	private String title;
	private String subject;
	private String body;
	private Author author;
	
	public ArticleResponse() {}
	
	/**
	 * @param article
	 */
	public ArticleResponse(Article article) {
		this.id = article.getId();
		this.title = article.getTitle();
		this.subject = article.getSubject();
		this.body = article.getBody();
		this.author = article.getAuthor();
	}
	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(long id) {
		this.id = id;
	}
	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}
	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	/**
	 * @return the subject
	 */
	public String getSubject() {
		return subject;
	}
	/**
	 * @param subject the subject to set
	 */
	public void setSubject(String subject) {
		this.subject = subject;
	}
	/**
	 * @return the body
	 */
	public String getBody() {
		return body;
	}
	/**
	 * @param body the body to set
	 */
	public void setBody(String body) {
		this.body = body;
	}
	/**
	 * @return the author
	 */
	public Author getAuthor() {
		return author;
	}
	/**
	 * @param author the author to set
	 */
	public void setAuthor(Author author) {
		this.author = author;
	}
	
}
