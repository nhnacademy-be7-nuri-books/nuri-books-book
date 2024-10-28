package shop.nuribooks.books.book.book.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import shop.nuribooks.books.book.book.dto.BookRegisterRequest;
import shop.nuribooks.books.book.book.dto.BookRegisterResponse;
import shop.nuribooks.books.book.book.dto.BookResponse;
import shop.nuribooks.books.book.book.dto.BookUpdateRequest;
import shop.nuribooks.books.book.book.entitiy.Book;
import shop.nuribooks.books.book.bookstate.entitiy.BookState;
import shop.nuribooks.books.book.publisher.entitiy.Publisher;
import shop.nuribooks.books.exception.BadRequestException;
import shop.nuribooks.books.exception.InvalidPageRequestException;
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

	@Transactional(readOnly = true)
	@Override
	public BookResponse getBookById(Long bookId) {
		Book book = bookRepository.findById(bookId)
			.orElseThrow(BookIdNotFoundException::new);
		return BookResponse.of(book);
	}

	@Transactional(readOnly = true)
	@Override
	public Page<BookResponse> getBooks(Pageable pageable) {
		Page<Book> bookPage = bookRepository.findAll(pageable);
		if(bookPage.isEmpty() && pageable.getPageNumber() > 0 && pageable.getPageNumber() >= bookPage.getTotalPages()) {
			throw new InvalidPageRequestException();
		}
		return bookPage.map(BookResponse::of);
	}

	//TODO: 좋아요나 조회수에 대한 업데이트는 따로 메서드를 구현할 계획입니다.
	/**
	 * 주어진 책 ID에 해당하는 책 정보를 업데이트합니다.
	 * <p>
	 *     관리자페이지에서 도서에 대한 정보를 수정하기 위한 메서드 입니다.
	 * </p>
	 * @param bookId 업데이트할 책의 ID
	 * @param bookUpdateReq 책 업데이트 요청 정보를 포함한 객체
	 * @throws BookIdNotFoundException 책 ID가 존재하지 않는 경우 발생
	 * @throws BookStateIdNotFoundException 도서상태 ID가 존재하지 않는 경우 발생
	 * @throws PublisherIdNotFoundException 주어진 출판사 ID가 존재하지 않는 경우 발생
	 */
	@Transactional
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

	@Transactional
	@Override
	public void deleteBook(Long bookId) {
		if(!bookRepository.existsById(bookId)) {
			throw new BookIdNotFoundException();
		}
		bookRepository.deleteById(bookId);
	}
}
