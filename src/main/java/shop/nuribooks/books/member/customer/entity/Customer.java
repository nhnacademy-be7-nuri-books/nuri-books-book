package shop.nuribooks.books.member.customer.entity;

import static jakarta.persistence.GenerationType.*;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Customer {

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "customer_id")
	private Long id;

	@NotNull
	@Column(length = 30)
	@Size(min = 2, max = 30, message = "이름은 반드시 2자 이상 30자 이하로 입력해야 합니다.")
	private String name;

	@NotNull
	private String password;

	@NotNull
	@Column(unique = true, length = 11)
	@Pattern(regexp = "^010\\d{8}$",
		message = "전화번호는 '-' 없이 '010'으로 시작하는 11자리의 숫자로 입력해야 합니다.")
	private String phoneNumber;

	@NotNull
	@Column(unique = true, length = 30)
	@Pattern(regexp = "^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,4}$",
		message = "유효한 이메일 형식으로 입력해야 합니다.")
	private String email;

	public void changeCustomerPassword(String password) {
		this.password = password;
	}

	public void changeToSoftDeleted() {
		this.name = "";
		this.password = "";
		this.phoneNumber = "";
		this.email = "";
	}
}
