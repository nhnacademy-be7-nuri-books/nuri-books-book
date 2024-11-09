package shop.nuribooks.books.book.book.mapper;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import shop.nuribooks.books.book.book.dto.BookResponse;
import shop.nuribooks.books.book.book.entity.Book;
import shop.nuribooks.books.book.book.utility.BookUtils;
import shop.nuribooks.books.book.bookcontributor.dto.BookContributorInfoResponse;
import shop.nuribooks.books.book.bookcontributor.repository.BookContributorRepository;
import shop.nuribooks.books.book.booktag.repository.BookTagRepository;

@Component
@RequiredArgsConstructor
public class BookMapper {
	private final BookTagRepository bookTagRepository;
	private final BookContributorRepository bookContributorRepository;

	public BookResponse toBookResponse(Book book) {
		BigDecimal salePrice = BookUtils.calculateSalePrice(book.getPrice(), book.getDiscountRate());

		List<BookContributorInfoResponse> contributorsResponses = bookContributorRepository.findContributorsAndRolesByBookId(book.getId());
		Map<String, List<String>> contributorsByRole = BookUtils.groupContributorsByRole(contributorsResponses);

		List<String> tagNames = bookTagRepository.findTagNamesByBookId(book.getId());

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
			tagNames,
			contributorsByRole
		);
	}
}
