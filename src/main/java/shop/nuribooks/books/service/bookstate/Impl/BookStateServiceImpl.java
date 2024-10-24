package shop.nuribooks.books.service.bookstate.Impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import shop.nuribooks.books.dto.bookstate.BookStateRequest;
import shop.nuribooks.books.dto.bookstate.BookStateResponse;
import shop.nuribooks.books.entity.book.Book;
import shop.nuribooks.books.entity.book.BookEditor;
import shop.nuribooks.books.entity.book.BookState;
import shop.nuribooks.books.exception.ResourceAlreadyExistException;
import shop.nuribooks.books.exception.bookstate.BookStateDetailAlreadyExistException;
import shop.nuribooks.books.exception.bookstate.BookStateIdNotFoundException;
import shop.nuribooks.books.repository.book.BookRepository;
import shop.nuribooks.books.repository.bookstate.BookStateRepository;
import shop.nuribooks.books.service.bookstate.BookStateService;

@RequiredArgsConstructor
@Service
public class BookStateServiceImpl implements BookStateService {

	private final BookStateRepository bookStateRepository;
	private final BookRepository bookRepository;

	public static final Integer DEFAULT_STATE_ID = 1;

	@Override
	public void registerState(String adminId, BookStateRequest bookStateRequest) {
		if(bookStateRepository.existsBookStatesByDetail(bookStateRequest.detail())){
			throw new BookStateDetailAlreadyExistException(bookStateRequest.detail());
		}

		BookState bookState = BookState.builder().detail(bookStateRequest.detail()).build();
		bookStateRepository.save(bookState);
	}

	@Override
	public List<BookStateResponse> getAllBooks() {
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
		BookState defaultState = bookStateRepository.findById(DEFAULT_STATE_ID)
			.orElseThrow(BookStateIdNotFoundException::new);

		BookState bookState = bookStateRepository.findById(id)
			.orElseThrow(BookStateIdNotFoundException::new);

		List<Book> books = bookRepository.findByStateId(bookState);

		for (Book book : books) {
			BookEditor bookEditor = BookEditor.builder()
				.stateId(defaultState)
				.build();
			book.edit(bookEditor);
		}

		bookRepository.saveAll(books);
		bookStateRepository.deleteById(id);
	}
}
