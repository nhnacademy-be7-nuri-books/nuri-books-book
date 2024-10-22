package shop.nuribooks.books.controller.book;

import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import shop.nuribooks.books.dto.book.request.BookRegisterReq;
import shop.nuribooks.books.dto.book.response.BookRegisterRes;
import shop.nuribooks.books.service.book.BookService;

@RequestMapping("/api/books")
@RequiredArgsConstructor
@RestController
public class BookController {
	private final BookService booksService;

	@Operation(summary = "Register a new book", description = "This endpoint allows administrators to register a new book in the system.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "201", description = "Book registration successful"),
		@ApiResponse(responseCode = "400", description = "Invalid request data"),
		@ApiResponse(responseCode = "404", description = "Book state or publisher not found"),
		@ApiResponse(responseCode = "409", description = "ISBN already exists"),
	})
	@PostMapping
	public ResponseEntity<BookRegisterRes> registerBooks(@Valid @RequestBody BookRegisterReq reqDto, BindingResult bindingResult) throws
		NoSuchMethodException, MethodArgumentNotValidException {
		if(bindingResult.hasErrors()){
			MethodParameter methodParameter = new MethodParameter(
				this.getClass().getMethod("registerBooks", BookRegisterReq.class, BindingResult.class),0
			);
			throw new MethodArgumentNotValidException(methodParameter, bindingResult);
		}

		BookRegisterRes resDto = booksService.registerBook(reqDto);
		return ResponseEntity.status(HttpStatus.CREATED).body(resDto);
	}
}
