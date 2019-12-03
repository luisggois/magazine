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
public class UserAlreadyExistsException extends DataIntegrityViolationException {

	private static final long serialVersionUID = 1L;
	
	public UserAlreadyExistsException(DataIntegrity message) {
		super(message.toString());
	}
}
