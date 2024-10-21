package shop.nuribooks.books.dto.books;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class BooksRegisterReqDto {
	@NotNull
	private Long stateId;

	@NotNull
	private Long publisherId;

	@NotNull
	@Size(min = 1, max = 50)
	private String title;

	@NotBlank
	private String thumbnailImageUrl;

	private String detailImageUrl;

	@NotNull
	private LocalDate publicationDate;

	@NotNull
	@PositiveOrZero
	private BigDecimal price;

	@NotNull
	@Min(0)
	@Max(100)
	private int discountRate;

	@NotBlank
	private String description;

	@NotBlank
	private String contents;

	@NotBlank
	private String isbn;

	private boolean isPackageable;

	@NotNull
	@Min(0)
	private int stock;
}
