/**
 * 
 */
package com.luisgois.magazine.exception.handler;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.luisgois.magazine.dto.exception.ExceptionResponse;

/**
 * @author luisgois
 *
 */
abstract class BaseExceptionHandler extends ResponseEntityExceptionHandler {
	
	protected final Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
		String message = "Malformed JSON request";
		logger.error(message);
		return ResponseEntity
				.status(HttpStatus.BAD_REQUEST)
				.body(new ExceptionResponse(HttpStatus.BAD_REQUEST, message, ex));
	}  	

	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers,HttpStatus status, WebRequest request) {      	   
		String message = "Validation failed for JSON request";
		Map<String, String> errors = new HashMap<>();
		ex.getBindingResult().getAllErrors().forEach((error) -> {
			String fieldName = ((FieldError) error).getField();
			String errorMessage = error.getDefaultMessage();
			errors.put(fieldName, errorMessage);
		});
		logger.error(errors.toString());
		return ResponseEntity
				.status(HttpStatus.BAD_REQUEST)
				.body(new ExceptionResponse(HttpStatus.BAD_REQUEST, message, errors));
	}

}

