package shop.nuribooks.books.book.book.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import shop.nuribooks.books.book.book.entity.Book;
import shop.nuribooks.books.book.book.entity.BookStateEnum;
import shop.nuribooks.books.book.publisher.entity.Publisher;

@AllArgsConstructor
@Getter
public abstract class BaseBookRegisterRequest {
	@NotBlank(message = "제목은 필수입니다.")
	@Size(min = 1, max = 100, message = "제목은 1자 이상 100자 이하여야 합니다.")
	private final String title;

	@NotBlank(message = "저자명과 역할은 필수입니다.")
	@Size(max = 100, message = "저자명과 역할은 최대 100자 입니다.")
	private final String author;

	@NotBlank(message = "출판사명은 필수입니다.")
	@Size(max = 100, message = "출판사명은 최대 100자 입니다.")
	private final String publisherName;

	@NotNull(message = "출판 날짜는 필수입니다.")
	private final LocalDate publicationDate;

	@NotNull(message = "가격은 필수입니다.")
	@DecimalMin(value = "0.0", inclusive = false, message = "가격은 0보다 커야 합니다.")
	private final BigDecimal price;

	@NotNull(message = "할인율은 필수입니다.")
	@Min(value = 0, message = "할인율은 0 이상이어야 합니다.")
	@Max(value = 100, message = "할인율은 100 이하여야 합니다.")
	private final int discountRate;

	@NotNull(message = "재고는 필수입니다.")
	@Min(value = 0, message = "재고는 0 이상이어야 합니다.")
	private final int stock;

	@NotNull(message = "도서 상태는 필수 입력 항목입니다.")
	private final String state;

	@NotBlank(message = "썸네일 이미지 URL은 필수입니다.")
	private final String thumbnailImageUrl;

	private final String detailImageUrl;

	@NotBlank(message = "설명은 필수입니다.")
	private final String description;

	@NotBlank(message = "목차는 필수입니다.")
	private final String contents;

	@NotBlank(message = "ISBN은 필수입니다.")
	private final String isbn;

	private final boolean isPackageable;

	@Size(max = 5, message = "최대 5개의 태그를 등록할 수 있습니다.")
	private final List<Long> tagIds;

	public Book toEntity(Publisher publisher, BookStateEnum bookStateEnum) {
		return Book.builder()
			.publisherId(publisher)
			.state(bookStateEnum)
			.title(this.title)
			.thumbnailImageUrl(this.thumbnailImageUrl)
			.detailImageUrl(this.detailImageUrl)
			.publicationDate(this.publicationDate)
			.price(this.price)
			.discountRate(this.discountRate)
			.description(this.description)
			.contents(this.contents)
			.isbn(this.isbn)
			.isPackageable(this.isPackageable)
			.likeCount(0)
			.stock(this.stock)
			.viewCount(0L)
			.build();
	}
}
