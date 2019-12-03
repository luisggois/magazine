package com.luisgois.magazine.exception.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.luisgois.magazine.dto.exception.ExceptionResponse;
import com.luisgois.magazine.exception.custom.ArticleNotFoundException;
import com.luisgois.magazine.exception.custom.AuthorNotFoundException;
import com.luisgois.magazine.exception.custom.UserAlreadyExistsException;
import com.luisgois.magazine.exception.custom.UserNotFoundException;

@ControllerAdvice
public class RestExceptionHandler extends BaseExceptionHandler {
   
   @ExceptionHandler(Throwable.class)
   protected ResponseEntity<Object> handleUnexpectedErrors(Throwable ex) {
	   logger.error(ex.getMessage());
       return ResponseEntity
				.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new ExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR,ex.getMessage()));
   }   

   @ExceptionHandler({IllegalArgumentException.class,AuthorNotFoundException.class,
	   UserNotFoundException.class,ArticleNotFoundException.class})
   protected ResponseEntity<Object> handleNotFound(Exception ex) {
	   logger.error(ex.getMessage());
	   return ResponseEntity
			   .status(HttpStatus.BAD_REQUEST)
			   .body(new ExceptionResponse(HttpStatus.BAD_REQUEST,ex.getMessage()));
   }
    
   
   @ExceptionHandler(UserAlreadyExistsException.class)
   protected ResponseEntity<Object> handleUserAlreadyExists(UserAlreadyExistsException ex) {
	   logger.error(ex.getMessage());
       return ResponseEntity
				.status(HttpStatus.CONFLICT)
					.body(new ExceptionResponse(HttpStatus.CONFLICT,ex.getMessage()));
   }
   
   @ExceptionHandler(BadCredentialsException.class)
   protected ResponseEntity<Object> handleBadCredentials(BadCredentialsException ex) {
	   logger.error(ex.getMessage());
       return ResponseEntity
				.status(HttpStatus.UNAUTHORIZED)
					.body(new ExceptionResponse(HttpStatus.UNAUTHORIZED,ex.getMessage()));
   }

}