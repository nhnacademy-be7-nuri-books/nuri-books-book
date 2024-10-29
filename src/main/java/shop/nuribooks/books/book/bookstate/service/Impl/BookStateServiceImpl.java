package shop.nuribooks.books.book.bookstate.service.Impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import shop.nuribooks.books.book.bookstate.dto.BookStateRequest;
import shop.nuribooks.books.book.bookstate.dto.BookStateResponse;
import shop.nuribooks.books.book.book.entitiy.Book;
import shop.nuribooks.books.book.bookstate.entitiy.BookState;
import shop.nuribooks.books.exception.DefaultStateDeletionException;
import shop.nuribooks.books.exception.bookstate.BookStateDetailAlreadyExistException;
import shop.nuribooks.books.exception.bookstate.BookStateIdNotFoundException;
import shop.nuribooks.books.book.book.repository.BookRepository;
import shop.nuribooks.books.book.bookstate.repository.BookStateRepository;
import shop.nuribooks.books.book.bookstate.service.BookStateService;

@RequiredArgsConstructor
@Service
public class BookStateServiceImpl implements BookStateService {

	private final BookStateRepository bookStateRepository;
	private final BookRepository bookRepository;

	public static final Integer DEFAULT_STATE_ID = 1;

	@Transactional
	@Override
	public void registerState(BookStateRequest bookStateRequest) {
		if(bookStateRepository.existsBookStatesByDetail(bookStateRequest.detail())){
			throw new BookStateDetailAlreadyExistException(bookStateRequest.detail());
		}

		BookState bookState = BookState.builder().detail(bookStateRequest.detail()).build();
		bookStateRepository.save(bookState);
	}

	@Override
	public BookStateResponse getBookState(Integer id) {
		BookState bookState = bookStateRepository.findById(id)
			.orElseThrow(BookStateIdNotFoundException::new);

		return BookStateResponse.of(bookState);
	}

	@Override
	public List<BookStateResponse> getAllBookStates() {
		List<BookState> bookStateList = bookStateRepository.findAll();

		return bookStateList.stream().map(bookState -> new BookStateResponse(bookState.getId(), bookState.getDetail()))
			.collect(Collectors.toList());
	}

	@Transactional
	@Override
	public void updateState(Integer id, BookStateRequest bookStateRequest) {
		BookState bookState = bookStateRepository.findById(id).
			orElseThrow(BookStateIdNotFoundException::new);

		if (bookStateRepository.existsBookStatesByDetail(bookStateRequest.detail())) {
			throw new BookStateDetailAlreadyExistException(bookStateRequest.detail());
		}

		bookState.updateDetail(bookStateRequest.detail());

		bookStateRepository.save(bookState);
	}

	@Transactional
	@Override
	public void deleteState(Integer id) {
		BookState bookState = bookStateRepository.findById(id)
			.orElseThrow(BookStateIdNotFoundException::new);

		if(id.equals(DEFAULT_STATE_ID)) {
			throw new DefaultStateDeletionException();
		}

		BookState defaultState = bookStateRepository.findById(DEFAULT_STATE_ID)
			.orElseThrow(BookStateIdNotFoundException::new);

		List<Book> books = bookRepository.findByStateId(bookState);

		if (!books.isEmpty()) {
			for (Book book : books) {
				book.updateStateId(defaultState);
			}
			bookRepository.saveAll(books);
		}

		bookStateRepository.deleteById(id);
	}
}
