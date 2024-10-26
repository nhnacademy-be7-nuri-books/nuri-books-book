package shop.nuribooks.books.book.book.service.impl;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import shop.nuribooks.books.book.book.dto.BookRegisterRequest;
import shop.nuribooks.books.book.book.dto.BookRegisterResponse;
import shop.nuribooks.books.book.book.dto.BookUpdateRequest;
import shop.nuribooks.books.book.book.entitiy.Book;
import shop.nuribooks.books.book.bookstate.entitiy.BookState;
import shop.nuribooks.books.book.publisher.entitiy.Publisher;
import shop.nuribooks.books.exception.BadRequestException;
import shop.nuribooks.books.exception.book.BookIdNotFoundException;
import shop.nuribooks.books.exception.book.BookStatesIdNotFoundException;
import shop.nuribooks.books.exception.book.PublisherIdNotFoundException;
import shop.nuribooks.books.exception.book.ResourceAlreadyExistIsbnException;
import shop.nuribooks.books.book.book.repository.BookRepository;
import shop.nuribooks.books.book.bookstate.repository.BookStateRepository;
import shop.nuribooks.books.book.publisher.repository.PublisherRepository;
import shop.nuribooks.books.book.book.service.BookService;
import shop.nuribooks.books.exception.bookstate.BookStateIdNotFoundException;

@RequiredArgsConstructor
@Service
public class BookServiceImpl implements BookService {
	private final BookRepository bookRepository;
	private final BookStateRepository bookStateRepository;
	private final PublisherRepository publisherRepository;

	@Override
	public BookRegisterResponse registerBook(BookRegisterRequest reqDto) {
		if (reqDto == null) {
			throw new BadRequestException("요청 본문이 비어있습니다.");
		}

		if (bookRepository.existsByIsbn(reqDto.isbn())) {
			throw new ResourceAlreadyExistIsbnException(reqDto.isbn());
		}

		BookState bookState = bookStateRepository.findById(reqDto.stateId())
			.orElseThrow(() -> new BookStatesIdNotFoundException(reqDto.stateId()));

		Publisher publisher = publisherRepository.findById(reqDto.publisherId())
			.orElseThrow(() -> new PublisherIdNotFoundException(reqDto.publisherId()));

		Book book = Book.builder()
			.stateId(bookState)
			.publisherId(publisher)
			.title(reqDto.title())
			.thumbnailImageUrl(reqDto.thumbnailImageUrl())
			.detailImageUrl(reqDto.detailImageUrl())
			.publicationDate(reqDto.publicationDate())
			.price(reqDto.price())
			.discountRate(reqDto.discountRate())
			.description(reqDto.description())
			.contents(reqDto.contents())
			.isbn(reqDto.isbn())
			.isPackageable(reqDto.isPackageable())
			.likeCount(0)
			.stock(reqDto.stock())
			.viewCount(0L)
			.build();

		bookRepository.save(book);

		return BookRegisterResponse.of(book);
	}

	@Override
	public void updateBook(Long bookId, BookUpdateRequest bookUpdateReq) {
		Book book = bookRepository.findById(bookId)
			.orElseThrow(BookIdNotFoundException::new);

		BookState bookState = bookStateRepository.findById(bookUpdateReq.stateId())
			.orElseThrow(BookStateIdNotFoundException::new);

		Publisher publisher = publisherRepository.findById(bookUpdateReq.publisherId())
			.orElseThrow(() -> new PublisherIdNotFoundException(bookUpdateReq.publisherId()));

		book.updateBookDetails(bookUpdateReq, bookState, publisher);

		bookRepository.save(book);
	}
}
