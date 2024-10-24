package shop.nuribooks.books.controller.bookstate;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import shop.nuribooks.books.dto.bookstate.BookStateRequest;
import shop.nuribooks.books.dto.member.ResponseMessage;
import shop.nuribooks.books.service.bookstate.BookStateService;

@RequestMapping("/api/book-state")
@RequiredArgsConstructor
@RestController
public class BookStateController {

	private final BookStateService bookStateService;

	@PostMapping
	public ResponseEntity<ResponseMessage> registerBookState(@Valid @RequestBody BookStateRequest bookStateReq) {
		bookStateService.registerState(bookStateReq);
		ResponseMessage responseMessage = new ResponseMessage(HttpStatus.CREATED.value(), "도서상태가 등록되었습니다.");
		return ResponseEntity.status(HttpStatus.CREATED).body(responseMessage);
	}
}
