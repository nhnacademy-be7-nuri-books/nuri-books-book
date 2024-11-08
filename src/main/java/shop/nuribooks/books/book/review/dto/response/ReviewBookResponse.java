package shop.nuribooks.books.book.review.dto.response;

import java.util.LinkedList;
import java.util.List;

import lombok.Builder;
import shop.nuribooks.books.book.book.dto.BookBriefResponse;
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
		ReviewBookResponse response = ReviewBookResponse.builder()
			.id(review.getId())
			.title(review.getTitle())
			.content(review.getContent())
			.score(review.getScore())
			.book(BookBriefResponse.of(review.getBook()))
			.reviewImages(new LinkedList<>())
			.build();
		return response;
	}
}
