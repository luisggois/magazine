/**
 * 
 */
package com.luisgois.magazine.dto.publication.article;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.luisgois.magazine.exception.constant.PublicationValidators;

/**
 * @author luisgois
 *
 */
public class ArticleRequest {
		
	@Size(min=3, message = PublicationValidators.TITLE_VALUE)
	@NotBlank(message = PublicationValidators.REQUIRED_VALUE)
	private String title;
	
	@Size(min=5, message = PublicationValidators.SUBJECT_VALUE)
	@NotBlank(message = PublicationValidators.REQUIRED_VALUE)
	private String subject;
	
	@Size(min=20, message = PublicationValidators.BODY_VALUE)
	@NotBlank(message = PublicationValidators.REQUIRED_VALUE)
	private String body;
	
	@NotNull(message = PublicationValidators.REQUIRED_VALUE)
	private long authorId;

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
	 * @return the authorId
	 */
	public long getAuthorId() {
		return authorId;
	}

	/**
	 * @param authorId the authorId to set
	 */
	public void setAuthorId(long authorId) {
		this.authorId = authorId;
	}
	
}
