package shop.nuribooks.books.order.wrapping.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import shop.nuribooks.books.order.wrapping.entity.WrappingPaper;

public record WrappingPaperRequest(
	@NotBlank(message = "포장지 이름은 필수입니다.")
	String title,
	@NotBlank(message = "포장지 이미지 경로는 필수입니다.")
	String imageUrl,
	@NotNull(message = "포장지 가격은 필수입니다.")
	@Min(value = 0, message = "포장지 가격은 0원 이상이어야합니다.")
	BigDecimal wrappingPrice
) {
	public WrappingPaper toEntity() {
		return WrappingPaper.builder()
			.title(title)
			.imageUrl(imageUrl)
			.wrappingPrice(wrappingPrice)
			.build();
	}
}
