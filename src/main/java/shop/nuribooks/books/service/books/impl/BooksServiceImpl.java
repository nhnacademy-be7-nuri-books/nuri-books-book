package shop.nuribooks.books.service.books.impl;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import shop.nuribooks.books.dto.books.BooksRegisterReqDto;
import shop.nuribooks.books.dto.books.BooksRegisterResDto;
import shop.nuribooks.books.entity.BookStates;
import shop.nuribooks.books.entity.Books;
import shop.nuribooks.books.entity.Publishers;
import shop.nuribooks.books.exception.BadRequestException;
import shop.nuribooks.books.exception.ResourceNotFoundException;
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

		BookStates bookState = bookStatesRepository.findById(reqDto.getStateId())
			.orElseThrow(() -> new ResourceNotFoundException("ID: {stateId}에 해당하는 도서 상태를 찾을 수 없습니다."));

		Publishers publisher = publishersRepository.findById(reqDto.getPublisherId())
			.orElseThrow(() -> new ResourceNotFoundException("ID: {stateId}에 해당하는 출판사 id를 찾을 수 없습니다."));

		Books books = new Books(
			null,
			bookState,
			publisher,
			reqDto.getTitle(),
			reqDto.getThumbnailImageUrl(),
			reqDto.getDetailImageUrl(),
			reqDto.getPublicationDate(),
			reqDto.getPrice(),
			reqDto.getDiscountRate(),
			reqDto.getDescription(),
			reqDto.getContents(),
			reqDto.getIsbn(),
			reqDto.isPackageable(),
			0,
			reqDto.getStock(),
			0L
		);

		booksRepository.save(books);

		return new BooksRegisterResDto(
			books.getId(),
			books.getStateId().getId(),
			books.getPublisherId().getId(),
			books.getTitle(),
			books.getThumbnailImageUrl(),
			books.getPublicationDate(),
			books.getPrice(),
			books.getDiscountRate(),
			books.getDescription(),
			books.getStock()
		);
	}
}
