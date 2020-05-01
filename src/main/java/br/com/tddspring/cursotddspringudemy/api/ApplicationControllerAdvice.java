package br.com.tddspring.cursotddspringudemy.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import br.com.tddspring.cursotddspringudemy.api.exception.ApiErrors;
import br.com.tddspring.cursotddspringudemy.exception.BusinessException;

@RestControllerAdvice
public class ApplicationControllerAdvice {
	
	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ApiErrors handleValidationException(MethodArgumentNotValidException ex) {
		BindingResult bidingResult = ex.getBindingResult();
		return new ApiErrors(bidingResult);
	}

	@ExceptionHandler(BusinessException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ApiErrors handlerBusinessException(BusinessException ex) {
		return new ApiErrors(ex);
	}
	
	@ExceptionHandler(ResponseStatusException.class)
	public ResponseEntity handleReponseStatusException(ResponseStatusException ex) {
		return new ResponseEntity(new ApiErrors(ex), ex.getStatus());
	}

}
