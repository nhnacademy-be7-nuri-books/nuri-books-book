package shop.nuribooks.books.book.review.dto.response;

import java.util.List;

import lombok.Builder;
import shop.nuribooks.books.book.book.entitiy.Book;
import shop.nuribooks.books.book.review.entity.Review;

/**
 * 도서 정보를 함께 담은 review dto
 * @param id
 * @param title
 * @param content
 * @param score
 * @param book
 */
@Builder
public record ReviewBookResponse(
	long id,
	String title,
	String content,
	int score,
	BookBriefResponse book,
	List<ReviewImageResponse> reviewImages
) {
	public static ReviewBookResponse of(Review review) {
		return ReviewBookResponse.builder()
			.id(review.getId())
			.title(review.getTitle())
			.content(review.getContent())
			.score(review.getScore())
			.book(BookBriefResponse.of(review.getBook()))
			.build();
	}

	@Builder
	public record BookBriefResponse(Long id,
									String title,
									String thumbnailImageUrl) {

		public static BookBriefResponse of(Book book) {
			return BookBriefResponse.builder()
				.id(book.getId())
				.title(book.getTitle())
				.thumbnailImageUrl(book.getThumbnailImageUrl())
				.build();
		}
	}
}
