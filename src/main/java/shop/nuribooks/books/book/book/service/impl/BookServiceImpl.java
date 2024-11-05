package shop.nuribooks.books.book.book.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import shop.nuribooks.books.book.book.dto.AdminBookListResponse;
import shop.nuribooks.books.book.book.dto.BookRegisterRequest;
import shop.nuribooks.books.book.book.dto.BookRegisterResponse;
import shop.nuribooks.books.book.book.dto.BookResponse;
import shop.nuribooks.books.book.book.dto.BookUpdateRequest;
import shop.nuribooks.books.book.book.entitiy.Book;
import shop.nuribooks.books.book.book.entitiy.BookStateEnum;
import shop.nuribooks.books.book.publisher.entitiy.Publisher;
import shop.nuribooks.books.common.message.PagedResponse;
import shop.nuribooks.books.exception.InvalidPageRequestException;
import shop.nuribooks.books.exception.book.BookIdNotFoundException;
import shop.nuribooks.books.exception.book.PublisherIdNotFoundException;
import shop.nuribooks.books.exception.book.ResourceAlreadyExistIsbnException;
import shop.nuribooks.books.book.book.repository.BookRepository;
import shop.nuribooks.books.book.publisher.repository.PublisherRepository;
import shop.nuribooks.books.book.book.service.BookService;

@RequiredArgsConstructor
@Service
public class BookServiceImpl implements BookService {
	private final BookRepository bookRepository;
	private final PublisherRepository publisherRepository;

	//관리자 페이지에서 관리자의 직접 도서 등록을 위한 메서드
	//TODO: 외부 api를 이용한 도서 등록기능 별도 구현 예정
	@Transactional
	@Override
	public BookRegisterResponse registerBook(BookRegisterRequest reqDto) {
		if (bookRepository.existsByIsbn(reqDto.isbn())) {
			throw new ResourceAlreadyExistIsbnException(reqDto.isbn());
		}

		Publisher publisher = publisherRepository.findById(reqDto.publisherId())
			.orElseThrow(() -> new PublisherIdNotFoundException(reqDto.publisherId()));

		BookStateEnum bookStateEnum = BookStateEnum.fromString(String.valueOf(reqDto.state()));

		Book book = Book.builder()
			.publisherId(publisher)
			.state(bookStateEnum)
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

	//도서 상세 조회 시 조회수 증가 추가
	//TODO:성능 고려한 비동기 업데이트나 조회기능과 독립적으로 관리하는 방안 생각중
	@Transactional
	@Override
	public BookResponse getBookById(Long bookId) {
		Book book = bookRepository.findById(bookId)
			.orElseThrow(BookIdNotFoundException::new);

		book.incrementViewCount();
		bookRepository.save(book);

		return BookResponse.of(book);
	}

	//TODO: 도서 목록조회를 하여 도서를 관리하기 위한 메서드 (관리자로 로그인 했을때는 수정버튼을 보이게해서 수정하도록 하는게 좋을까?)
	//TODO: 추후 엘라스틱 서치 적용 시 사용자를 위한 도서 검색 기능을 따로 구현 예정
	@Transactional(readOnly = true)
	@Override
	public PagedResponse<AdminBookListResponse> getBooks(Pageable pageable) {
		if(pageable.getPageNumber() < 0) {
			throw new InvalidPageRequestException("페이지 번호는 0 이상이어야 합니다.");
		}

		Page<Book> bookPage = bookRepository.findAll(pageable);

		if(pageable.getPageNumber() > bookPage.getTotalPages() - 1) {
			throw new InvalidPageRequestException("조회 가능한 페이지 범위를 초과했습니다.");
		}

		//소수점 버리기 (1원 단위로 계산 위해)
		List<AdminBookListResponse> bookListResponses = bookPage.stream()
			.map(book -> {
				BigDecimal salePrice = book.getPrice()
					.multiply(BigDecimal.valueOf(100 - book.getDiscountRate()))
					.divide(BigDecimal.valueOf(100), 0, RoundingMode.DOWN);
				return AdminBookListResponse.of(book, salePrice);
			})
			.toList();

		return new PagedResponse<>(
			bookListResponses,
			bookPage.getNumber(),
			bookPage.getSize(),
			bookPage.getTotalPages(),
			bookPage.getTotalElements()
		);
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
	 * @throws shop.nuribooks.books.exception.book.InvalidBookStateException BookStateEnum에 존재하지 않는 도서상태가 입력된 경우 발생
	 * @throws PublisherIdNotFoundException 주어진 출판사 ID가 존재하지 않는 경우 발생
	 */
	@Transactional
	@Override
	public void updateBook(Long bookId, BookUpdateRequest bookUpdateReq) {
		Book book = bookRepository.findById(bookId)
			.orElseThrow(BookIdNotFoundException::new);

		Publisher publisher = publisherRepository.findById(bookUpdateReq.publisherId())
			.orElseThrow(() -> new PublisherIdNotFoundException(bookUpdateReq.publisherId()));

		BookStateEnum bookStateEnum = BookStateEnum.fromString(String.valueOf(bookUpdateReq.state()));

		book.updateBookDetails(bookUpdateReq, bookStateEnum, publisher);

		bookRepository.save(book);
	}

	//관리자페이지에서 관리자의 도서 삭제 기능
	@Transactional
	@Override
	public void deleteBook(Long bookId) {
		if(!bookRepository.existsById(bookId)) {
			throw new BookIdNotFoundException();
		}
		bookRepository.deleteById(bookId);
	}
}
