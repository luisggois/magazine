/**
 * 
 */
package com.luisgois.magazine.dto.publication.author;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.luisgois.magazine.exception.constant.PublicationValidators;

/**
 * @author luisgois
 *
 */
public class AuthorRequest {
	
	@Size(min=3, message = PublicationValidators.NAME_VALUE)
	@NotBlank(message = PublicationValidators.REQUIRED_VALUE)
	private String name;
	
	@Size(min=2, message = PublicationValidators.COMPANY_VALUE)
	@NotBlank(message = PublicationValidators.REQUIRED_VALUE)
	private String company;
	
	@Size(min=3, message = PublicationValidators.JOB_VALUE)
	@NotBlank(message = PublicationValidators.REQUIRED_VALUE)
	private String job;

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

}
