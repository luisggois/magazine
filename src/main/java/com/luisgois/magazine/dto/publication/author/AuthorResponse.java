/**
 * 
 */
package com.luisgois.magazine.dto.publication.author;

import java.util.ArrayList;
import java.util.List;

import com.luisgois.magazine.models.publication.Article;
import com.luisgois.magazine.models.publication.Author;

/**
 * @author luisgois
 *
 */
public class AuthorResponse {
	
	private long id;
	private String name;
	private String company;
	private String job;
	private List<Article> articles = new ArrayList<Article>();
	
	public AuthorResponse() {}
	
	/**
	 * @param author
	 */
	public AuthorResponse(Author author) {
		this.id = author.getId();
		this.name = author.getName();
		this.company = author.getCompany();
		this.job = author.getJob();
		this.articles = author.getArticles();
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
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the company
	 */
	public String getCompany() {
		return company;
	}
	/**
	 * @param company the company to set
	 */
	public void setCompany(String company) {
		this.company = company;
	}
	/**
	 * @return the job
	 */
	public String getJob() {
		return job;
	}
	/**
	 * @param job the job to set
	 */
	public void setJob(String job) {
		this.job = job;
	}
	/**
	 * @return the articles
	 */
	public List<Article> getArticles() {
		return articles;
	}
	/**
	 * @param articles the articles to set
	 */
	public void setArticles(List<Article> articles) {
		this.articles = articles;
	}

}
