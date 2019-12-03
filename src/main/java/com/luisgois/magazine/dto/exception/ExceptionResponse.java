/**
 * 
 */
package com.luisgois.magazine.dto.exception;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.http.HttpStatus;

/**
 * @author luisgois
 *
 */

public class ExceptionResponse {

	private HttpStatus status;
	private LocalDateTime timestamp;
	private String message;
	private Map<String, String> subErrors;
	private Throwable debugMessage;

	private ExceptionResponse() {
		timestamp = LocalDateTime.now();
	}

	public ExceptionResponse(HttpStatus status) {
		this();
		this.message = "Unexpected error";
		this.status = status;
	}
	
	public ExceptionResponse(HttpStatus status, String message) {
		this();
		this.status = status;
		this.message = message;
	}
	
	public ExceptionResponse(HttpStatus status, String message, Throwable debugMessage) {
		this();
		this.status = status;
		this.message = message;
		this.debugMessage = debugMessage;
	}

	public ExceptionResponse(HttpStatus status, String message, Map<String, String> subErrors) {
		this();
		this.status = status;
		this.message = message;
		this.subErrors = subErrors;
	}

	/**
	 * @return the status
	 */
	public HttpStatus getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(HttpStatus status) {
		this.status = status;
	}

	/**
	 * @return the timestamp
	 */
	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	/**
	 * @param timestamp the timestamp to set
	 */
	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * @return the subErrors
	 */
	public Map<String, String> getSubErrors() {
		return subErrors;
	}

	/**
	 * @param subErrors the subErrors to set
	 */
	public void setSubErrors(Map<String, String> subErrors) {
		this.subErrors = subErrors;
	}

	/**
	 * @return the debugMessage
	 */
	public Throwable getDebugMessage() {
		return debugMessage;
	}

	/**
	 * @param debugMessage the debugMessage to set
	 */
	public void setDebugMessage(Throwable debugMessage) {
		this.debugMessage = debugMessage;
	}
	
}