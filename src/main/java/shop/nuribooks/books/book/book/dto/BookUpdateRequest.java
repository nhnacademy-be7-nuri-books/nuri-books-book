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

public record BookUpdateRequest(
	@NotNull(message = "가격은 필수 입력 항목입니다.")
	@DecimalMin(value = "0.0", inclusive = false, message = "가격은 0보다 커야 합니다.")
	BigDecimal price,

	@NotNull(message = "할인율은 필수 입력 항목입니다.")
	@Min(value = 0, message = "할인율은 0 이상이어야 합니다.")
	@Max(value = 100, message = "할인율은 100 이하이어야 합니다.")
	int discountRate,

	@NotNull(message = "재고는 필수 입력 항목입니다.")
	@Min(value = 0, message = "재고는 0 이상이어야 합니다.")
	int stock,

	@NotNull(message = "도서 상태는 필수 입력 항목입니다.")
	String state,

	@NotBlank(message = "썸네일 이미지 URL은 필수 입력 항목입니다.")
	String thumbnailImageUrl,

	String detailImageUrl,

	@NotBlank(message = "도서 설명은 필수 입력 항목입니다.")
	String description,

	@NotBlank(message = "도서 내용은 필수 입력 항목입니다.")
	String contents,

	boolean isPackageable,

	@Size(max = 5, message = "최대 5개의 태그를 등록할 수 있습니다.")
	List<Long> tagIds,

	@Size(min = 1, max = 10, message = "카테고리는 최대 10개까지 선택 가능합니다.")
	List<Long> categoryIds
) {}
