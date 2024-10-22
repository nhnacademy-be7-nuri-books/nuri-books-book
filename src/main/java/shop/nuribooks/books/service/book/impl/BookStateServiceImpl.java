package shop.nuribooks.books.service.book.impl;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import shop.nuribooks.books.dto.book.request.BookStateReq;
import shop.nuribooks.books.dto.common.ResponseMessage;
import shop.nuribooks.books.entity.book.BookStates;
import shop.nuribooks.books.exception.BadRequestException;
import shop.nuribooks.books.exception.book.IsAlreadyExistsBookStateException;
import shop.nuribooks.books.repository.book.BookStateRepository;
import shop.nuribooks.books.service.book.BookStateService;

@RequiredArgsConstructor
@Service
public class BookStateServiceImpl implements BookStateService {
	private final BookStateRepository bookStateRepository;

	@Override
	public ResponseMessage registerState(BookStateReq bookStateReq) {
		if(bookStateReq == null){
			throw new BadRequestException("요청 본문이 비어있습니다.");
		}

		if(bookStateRepository.existsBookStatesByDetail(bookStateReq.getDetail())){
			throw new IsAlreadyExistsBookStateException(bookStateReq.getDetail());
		}

		BookStates bookStates = new BookStates();
		bookStates.setDetail(bookStateReq.getDetail());
		bookStateRepository.save(bookStates);
		return new ResponseMessage(HttpStatus.CREATED.value(), "도서상태 등록 성공");
	}
}
