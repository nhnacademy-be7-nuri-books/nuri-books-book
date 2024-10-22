package shop.nuribooks.books.service.book.impl;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import shop.nuribooks.books.dto.book.BookRegisterReq;
import shop.nuribooks.books.dto.book.BookRegisterRes;
import shop.nuribooks.books.entity.BookStates;
import shop.nuribooks.books.entity.Books;
import shop.nuribooks.books.entity.Publishers;
import shop.nuribooks.books.exception.BadRequestException;
import shop.nuribooks.books.exception.book.BookStatesIdNotFoundException;
import shop.nuribooks.books.exception.book.PublisherIdNotFoundException;
import shop.nuribooks.books.exception.book.ResourceAlreadyExistIsbnException;
import shop.nuribooks.books.repository.book.BookRepository;
import shop.nuribooks.books.repository.book.BookStateRepository;
import shop.nuribooks.books.repository.book.PublisherRepository;
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

		if (booksRepository.existsByIsbn(reqDto.getIsbn())) {
			throw new ResourceAlreadyExistIsbnException(reqDto.getIsbn());
		}

		BookStates bookState = bookStatesRepository.findById(reqDto.getStateId())
			.orElseThrow(() -> new BookStatesIdNotFoundException(reqDto.getStateId()));

		Publishers publisher = publishersRepository.findById(reqDto.getPublisherId())
			.orElseThrow(() -> new PublisherIdNotFoundException(reqDto.getPublisherId()));

		Books books = Books.builder()
			.stateId(bookState)
			.publisherId(publisher)
			.title(reqDto.getTitle())
			.thumbnailImageUrl(reqDto.getThumbnailImageUrl())
			.detailImageUrl(reqDto.getDetailImageUrl())
			.publicationDate(reqDto.getPublicationDate())
			.price(reqDto.getPrice())
			.discountRate(reqDto.getDiscountRate())
			.description(reqDto.getDescription())
			.contents(reqDto.getContents())
			.isbn(reqDto.getIsbn())
			.isPackageable(reqDto.isPackageable())
			.likeCount(0)
			.stock(reqDto.getStock())
			.viewCount(0L)
			.build();

		booksRepository.save(books);

		return BookRegisterRes.builder()
			.id(books.getId())
			.stateId(books.getStateId().getId())
			.publisherId(books.getPublisherId().getId())
			.title(books.getTitle())
			.thumbnailImageUrl(books.getThumbnailImageUrl())
			.publicationDate(books.getPublicationDate())
			.price(books.getPrice())
			.discountRate(books.getDiscountRate())
			.description(books.getDescription())
			.stock(books.getStock())
			.build();
	}
}
