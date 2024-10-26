package shop.nuribooks.books.book.book.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record BookUpdateRequest(
	@NotNull Integer stateId,
	@NotNull Long publisherId,
	@NotNull @Size(min = 1, max = 50) String title,
	@NotBlank String thumbnailImageUrl,
	String detailImageUrl,
	@NotNull LocalDate publicationDate,
	@NotNull @DecimalMin(value = "0.0", inclusive = false) BigDecimal price,
	@NotNull @Min(0) @Max(100) int discountRate,
	@NotBlank String description,
	@NotBlank String contents,
	@NotBlank String isbn,
	boolean isPackageable,
	@NotNull int likeCount,
	@NotNull @Min(0) int stock,
	@NotNull Long viewCount
) {
}
