package shop.nuribooks.books.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import shop.nuribooks.books.dto.errordto.response.ErrorRes;
import shop.nuribooks.books.exception.BadRequestException;
import shop.nuribooks.books.exception.ResourceAlreadyExistException;
import shop.nuribooks.books.exception.ResourceNotFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ErrorRes> handleResourceNotFoundException(ResourceNotFoundException ex,
		WebRequest request) {
		return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request);
	}

	@ExceptionHandler(BadRequestException.class)
	public ResponseEntity<ErrorRes> handleInvalidDataException(BadRequestException ex, WebRequest request) {
		return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorRes> handleValidationException(MethodArgumentNotValidException ex,
		WebRequest request) {
		return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
	}

	@ExceptionHandler(ResourceAlreadyExistException.class)
	public ResponseEntity<ErrorRes> handleDuplicateException(ResourceAlreadyExistException ex, WebRequest request) {
		return buildErrorResponse(HttpStatus.CONFLICT, ex.getMessage(), request);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorRes> handleGlobalException(Exception ex, WebRequest request) {
		return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred", request);
	}

	private ResponseEntity<ErrorRes> buildErrorResponse(HttpStatus status, String message, WebRequest request) {
		ErrorRes errorResponse = new ErrorRes(
			status.value(),
			message,
			request.getDescription(false)
		);
		return new ResponseEntity<>(errorResponse, status);
	}

}
