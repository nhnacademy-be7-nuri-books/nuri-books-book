package shop.nuribooks.books.order.wrapping.entity;

import java.math.BigDecimal;

import org.hibernate.annotations.Comment;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.nuribooks.books.order.wrapping.dto.WrappingPaperRequest;
import shop.nuribooks.books.order.wrapping.dto.WrappingPaperResponse;

/**
 * 포장지 entity
 *
 * @author nuri
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Comment("포장지 정보")
@Table(name = "wrapping_papers")
@Entity
public class WrappingPaper {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Comment("포장지 아이디")
	private Long id;

	@Column(length = 100, nullable = false)
	@NotBlank
	@Comment("제목")
	private String title;

	@Column(nullable = false)
	@NotBlank
	@Comment("이미지 경로")
	private String imageUrl;

	@Column(precision = 9, nullable = false)
	@Comment("포장지 가격")
	private BigDecimal wrappingPrice = BigDecimal.ZERO;

	/**
	 * 포장지 생성자 (Builder)
	 *
	 * @param title 포장지 제목
	 * @param imageUrl 이미지 경로
	 * @param wrappingPrice 포장 비용
	 */
	@Builder
	public WrappingPaper(String title, String imageUrl, BigDecimal wrappingPrice) {
		this.title = title;
		this.imageUrl = imageUrl;
		this.wrappingPrice = wrappingPrice;
	}

	public WrappingPaperResponse toResponseDto() {
		return WrappingPaperResponse.builder()
			.id(id)
			.title(title)
			.imageUrl(imageUrl)
			.wrappingPrice(wrappingPrice)
			.build();
	}

	public void update(WrappingPaperRequest request) {
		this.title = request.title();
		this.imageUrl = request.imageUrl();
		this.wrappingPrice = request.wrappingPrice();
	}
}
