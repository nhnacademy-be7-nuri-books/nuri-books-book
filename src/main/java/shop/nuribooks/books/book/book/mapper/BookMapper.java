package shop.nuribooks.books.book.book.mapper;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import shop.nuribooks.books.book.book.dto.BookResponse;
import shop.nuribooks.books.book.book.entity.Book;
import shop.nuribooks.books.book.booktag.repository.BookTagRepository;

@Component
@RequiredArgsConstructor
public class BookMapper {
	private final BookTagRepository bookTagRepository;

	public BookResponse toBookResponse(Book book) {
		List<String> tagNames = bookTagRepository.findTagNamesByBookId(book.getId());

		BigDecimal salePrice = calculateSalePrice(book);

		return new BookResponse(
			book.getId(),
			book.getPublisherId().getName(),
			book.getState().getKorName(),
			book.getTitle(),
			book.getThumbnailImageUrl(),
			book.getDetailImageUrl(),
			book.getPublicationDate(),
			book.getPrice(),
			book.getDiscountRate(),
			salePrice,
			book.getDescription(),
			book.getContents(),
			book.getIsbn(),
			book.isPackageable(),
			book.getLikeCount(),
			book.getStock(),
			book.getViewCount(),
			tagNames
		);
	}


	private BigDecimal calculateSalePrice(Book book) {
		return book.getPrice()
			.multiply(BigDecimal.valueOf(100 - book.getDiscountRate()))
			.divide(BigDecimal.valueOf(100), 0, RoundingMode.DOWN);
	}
}
