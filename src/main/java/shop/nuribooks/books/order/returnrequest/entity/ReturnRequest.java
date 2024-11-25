package shop.nuribooks.books.order.returnrequest.entity;

import org.hibernate.annotations.Comment;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.nuribooks.books.order.order.entity.Order;

/**
 * 포장지 entity
 *
 * @author nuri
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Comment("반품 요청")
@Table(name = "return_requests")
@Entity
public class ReturnRequest {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Comment("반품 요청 아이디")
	private Long id;

	private String contents;

	@Column(nullable = false)
	@NotBlank
	@Comment("이미지 경로")
	private String imageUrl;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_id")
	private Order order;

	@Builder
	private ReturnRequest(String contents, String imageUrl, Order order) {
		this.contents = contents;
		this.imageUrl = imageUrl;
		this.order = order;
	}
}
