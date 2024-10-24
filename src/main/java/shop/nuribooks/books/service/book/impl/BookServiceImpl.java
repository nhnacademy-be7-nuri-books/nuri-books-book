package shop.nuribooks.books.service.book.impl;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import shop.nuribooks.books.dto.book.BookRegisterReq;
import shop.nuribooks.books.dto.book.BookRegisterRes;
import shop.nuribooks.books.entity.book.Book;
import shop.nuribooks.books.entity.book.BookState;
import shop.nuribooks.books.entity.book.Publisher;
import shop.nuribooks.books.exception.BadRequestException;
import shop.nuribooks.books.exception.book.BookStatesIdNotFoundException;
import shop.nuribooks.books.exception.book.PublisherIdNotFoundException;
import shop.nuribooks.books.exception.book.ResourceAlreadyExistIsbnException;
import shop.nuribooks.books.repository.book.BookRepository;
import shop.nuribooks.books.repository.bookstate.BookStateRepository;
import shop.nuribooks.books.repository.publisher.PublisherRepository;
import shop.nuribooks.books.service.book.BookService;

@RequiredArgsConstructor
@Service
public class BookServiceImpl implements BookService {
	private final BookRepository booksRepository;
	private final BookStateRepository bookStatesRepository;
	private final PublisherRepository publishersRepository;

	@Override
	public BookRegisterRes registerBook(BookRegisterReq reqDto) {
		if (reqDto == null) {
			throw new BadRequestException("요청 본문이 비어있습니다.");
		}

		if (booksRepository.existsByIsbn(reqDto.isbn())) {
			throw new ResourceAlreadyExistIsbnException(reqDto.isbn());
		}

		BookState bookState = bookStatesRepository.findById(reqDto.stateId())
			.orElseThrow(() -> new BookStatesIdNotFoundException(reqDto.stateId()));

		Publisher publisher = publishersRepository.findById(reqDto.publisherId())
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

		booksRepository.save(book);

		return BookRegisterRes.of(book);
	}
}
