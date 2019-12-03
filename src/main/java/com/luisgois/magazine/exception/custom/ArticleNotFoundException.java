/**
 * 
 */
package com.luisgois.magazine.exception.custom;

import org.springframework.dao.DataIntegrityViolationException;

import com.luisgois.magazine.exception.enumeration.DataIntegrity;

/**
 * @author luisgois
 *
 */
public class ArticleNotFoundException extends DataIntegrityViolationException {
	
	private static final long serialVersionUID = 1L;
	
	public ArticleNotFoundException(DataIntegrity message) {
		super(message.toString());
	}	
}
