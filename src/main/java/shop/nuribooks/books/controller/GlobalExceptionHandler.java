package shop.nuribooks.books.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import shop.nuribooks.books.dto.errordto.response.ErrorResponseDto;
import shop.nuribooks.books.exception.InvalidDataException;
import shop.nuribooks.books.exception.ResourceNotFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {
	private ResponseEntity<ErrorResponseDto> buildErrorResponse(HttpStatus status, String message, WebRequest request) {
		ErrorResponseDto errorResponse = new ErrorResponseDto(
			status.value(),
			message,
			request.getDescription(false)
		);
		return new ResponseEntity<>(errorResponse, status);
	}

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ErrorResponseDto> handleResourceNotFoundException(ResourceNotFoundException ex,
		WebRequest request) {
		return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request);
	}

	@ExceptionHandler(InvalidDataException.class)
	public ResponseEntity<ErrorResponseDto> handleInvalidDataException(InvalidDataException ex, WebRequest request) {
		return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponseDto> handleGlobalException(Exception ex, WebRequest request) {
		return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred", request);
	}
}
