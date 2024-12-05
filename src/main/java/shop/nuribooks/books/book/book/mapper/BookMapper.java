package shop.nuribooks.books.book.book.mapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import shop.nuribooks.books.book.book.dto.response.BookResponse;
import shop.nuribooks.books.book.book.entity.Book;
import shop.nuribooks.books.book.book.utility.BookUtils;
import shop.nuribooks.books.book.bookcontributor.dto.BookContributorInfoResponse;
import shop.nuribooks.books.book.bookcontributor.repository.BookContributorRepository;
import shop.nuribooks.books.book.booktag.repository.BookTagRepository;
import shop.nuribooks.books.book.category.dto.SimpleCategoryResponse;
import shop.nuribooks.books.book.category.repository.BookCategoryRepository;

@Component
@RequiredArgsConstructor
public class BookMapper {
	private final BookTagRepository bookTagRepository;
	private final BookContributorRepository bookContributorRepository;
	private final BookCategoryRepository bookCategoryRepository;

	public BookResponse toBookResponse(Book book) {
		BigDecimal salePrice = BookUtils.calculateSalePrice(book.getPrice(), book.getDiscountRate());

		List<BookContributorInfoResponse> contributorsResponses = bookContributorRepository.findContributorsAndRolesByBookId(
			book.getId());
		Map<String, List<String>> contributorsByRole = BookUtils.groupContributorsByRole(contributorsResponses);

		List<String> tagNames = bookTagRepository.findTagNamesByBookId(book.getId());

		List<List<SimpleCategoryResponse>> simpleCategories = bookCategoryRepository.findCategoriesByBookId(
			book.getId());

		return new BookResponse(
			book.getId(),
			book.getPublisherId().getName(),
			book.getState().getKorName(),
			book.getTitle(),
			book.getThumbnailImageUrl(),
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
			contributorsByRole,
			simpleCategories
		);
	}
}
