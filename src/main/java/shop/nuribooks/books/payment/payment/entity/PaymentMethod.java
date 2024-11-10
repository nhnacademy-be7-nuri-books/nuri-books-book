package shop.nuribooks.books.payment.payment.entity;

import org.hibernate.annotations.Comment;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 결제 수단 정보 entity
 *
 * @author nuri
 */
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "payment_methods")
@Comment("결제 수단 정보")
public class PaymentMethod {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Comment("결제 수단 아이디")
	private Long id;

	@Column(nullable = false)
	@Comment("결제 수단 명")
	@NotBlank
	private String name;

	/**
	 * 결제 수단 생성자 (Builder)
	 *
	 * @param name 결제 수단 명
	 */
	public PaymentMethod(String name) {
		this.name = name;
	}
}
