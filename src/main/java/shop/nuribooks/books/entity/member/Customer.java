package shop.nuribooks.books.entity.member;

import static jakarta.persistence.GenerationType.*;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
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

	@NotBlank
	private String name;

	@NotBlank
	private String password;

	@NotBlank
	private String phoneNumber;

	@NotBlank
	private String email;

	public Customer(String name, String password, String phoneNumber, String email) {
		this.name = name;
		this.password = password;
		this.phoneNumber = phoneNumber;
		this.email = email;
	}
}
