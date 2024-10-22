package shop.nuribooks.books.controller;

import static org.springframework.http.HttpStatus.*;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import shop.nuribooks.books.dto.errordto.response.ErrorResDto;
import shop.nuribooks.books.exception.BadRequestException;
import shop.nuribooks.books.exception.DuplicateException;
import shop.nuribooks.books.exception.ResourceNotFoundException;
import shop.nuribooks.books.exception.member.EmailAlreadyExistsException;
import shop.nuribooks.books.exception.member.InvalidPasswordException;
import shop.nuribooks.books.exception.member.UserIdAlreadyExistsException;
import shop.nuribooks.books.exception.member.UserIdNotFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {
	private ResponseEntity<ErrorResDto> buildErrorResponse(HttpStatus status, String message, WebRequest request) {
		ErrorResDto errorResponse = new ErrorResDto(
			status.value(),
			message,
			request.getDescription(false)
		);
		return new ResponseEntity<>(errorResponse, status);
	}

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ErrorResDto> handleResourceNotFoundException(ResourceNotFoundException ex,
		WebRequest request) {
		return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request);
	}

	@ExceptionHandler(BadRequestException.class)
	public ResponseEntity<ErrorResDto> handleInvalidDataException(BadRequestException ex, WebRequest request) {
		return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResDto> handleValidationException(MethodArgumentNotValidException ex,
		WebRequest request) {
		return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
	}

	@ExceptionHandler(DuplicateException.class)
	public ResponseEntity<ErrorResDto> handleDuplicateException(DuplicateException ex, WebRequest request) {
		return buildErrorResponse(HttpStatus.CONFLICT, ex.getMessage(), request);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResDto> handleGlobalException(Exception ex, WebRequest request) {
		return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred", request);
	}

	/**
	 * 입력된 이메일이 중복될 때의 예외 처리
	 * @param ex 예외 메시지
	 */
	@ExceptionHandler(EmailAlreadyExistsException.class)
	public ResponseEntity<ErrorResDto> handleEmailAlreadyExistsException(
		EmailAlreadyExistsException ex, WebRequest request) {

		return buildErrorResponse(CONFLICT, ex.getMessage(), request);
	}

	/**
	 * 입력된 아이디가 중복될 때의 예외 처리
	 * @param ex 예외 메시지
	 */
	@ExceptionHandler(UserIdAlreadyExistsException.class)
	public ResponseEntity<ErrorResDto> handleUserIdAlreadyExistsException(
		UserIdAlreadyExistsException ex, WebRequest request) {

		return buildErrorResponse(CONFLICT, ex.getMessage(), request);
	}

	/**
	 * 입력된 아이디가 존재하지 않을 때의 예외 처리
	 * @param ex 예외 메시지
	 */
	@ExceptionHandler(UserIdNotFoundException.class)
	public ResponseEntity<ErrorResDto> handleUserIdNotFoundException(
		UserIdNotFoundException ex, WebRequest request) {

		return buildErrorResponse(NOT_FOUND, ex.getMessage(), request);
	}

	/**
	 * 입력된 비밀번호가 일치하지 않을 때의 예외 처리
	 * @param ex 예외 메시지
	 */
	@ExceptionHandler(InvalidPasswordException.class)
	public ResponseEntity<ErrorResDto> handleInvalidPasswordException(
		InvalidPasswordException ex, WebRequest request) {

		return buildErrorResponse(BAD_REQUEST, ex.getMessage(), request);
	}

}
