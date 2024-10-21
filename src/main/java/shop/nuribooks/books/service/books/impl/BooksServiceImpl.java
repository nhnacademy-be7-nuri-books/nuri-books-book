package shop.nuribooks.books.service.books.impl;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import shop.nuribooks.books.dto.books.BooksRegisterReqDto;
import shop.nuribooks.books.dto.books.BooksRegisterResDto;
import shop.nuribooks.books.entity.BookStates;
import shop.nuribooks.books.entity.Books;
import shop.nuribooks.books.entity.Publishers;
import shop.nuribooks.books.exception.BadRequestException;
import shop.nuribooks.books.exception.books.BookStatesIdNotFoundException;
import shop.nuribooks.books.exception.books.DuplicateIsbnException;
import shop.nuribooks.books.exception.books.PublisherIdNotFoundException;
import shop.nuribooks.books.repository.books.BookStatesRepository;
import shop.nuribooks.books.repository.books.BooksRepository;
import shop.nuribooks.books.repository.books.PublishersRepository;
import shop.nuribooks.books.service.books.BooksService;

@RequiredArgsConstructor
@Service
public class BooksServiceImpl implements BooksService {
	private final BooksRepository booksRepository;
	private final BookStatesRepository bookStatesRepository;
	private final PublishersRepository publishersRepository;

	@Override
	public BooksRegisterResDto registerBook(BooksRegisterReqDto reqDto) {
		if(reqDto == null){
			throw new BadRequestException("요청 본문이 비어있습니다.");
		}

		if(booksRepository.existsByIsbn(reqDto.getIsbn())){
			throw new DuplicateIsbnException(reqDto.getIsbn());
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

		return BooksRegisterResDto.builder()
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
