package shop.nuribooks.books.service.bookstate.Impl;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import shop.nuribooks.books.dto.bookstate.BookStateRequest;
import shop.nuribooks.books.entity.book.BookState;
import shop.nuribooks.books.exception.ResourceAlreadyExistException;
import shop.nuribooks.books.exception.bookstate.BookStateDetailAlreadyExistException;
import shop.nuribooks.books.repository.bookstate.BookStateRepository;
import shop.nuribooks.books.service.bookstate.BookStateService;

@RequiredArgsConstructor
@Service
public class BookStateServiceImpl implements BookStateService {

	private final BookStateRepository bookStateRepository;

	@Override
	public void registerState(String adminId, BookStateRequest bookStateRequest) {
		if(bookStateRepository.existsBookStatesByDetail(bookStateRequest.detail())){
			throw new BookStateDetailAlreadyExistException(bookStateRequest.detail());
		}

		BookState bookState = BookState.of(bookStateRequest.detail());
		bookStateRepository.save(bookState);
	}
}
