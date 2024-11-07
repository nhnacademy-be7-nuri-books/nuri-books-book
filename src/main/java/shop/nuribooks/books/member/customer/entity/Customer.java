package shop.nuribooks.books.member.customer.entity;

import static jakarta.persistence.GenerationType.*;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
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
	private String name;

	@NotNull
	private String password;

	@NotNull
	@Column(unique = true)
	private String phoneNumber;

	@NotNull
	@Column(unique = true)
	private String email;

	public void changeCustomerInformation(String name, String password, String phoneNumber) {
		this.name = name;
		this.password = password;
		this.phoneNumber = phoneNumber;
	}

	public void changeToSoftDeleted() {
		this.name = "";
		this.password = "";
		this.phoneNumber = "";
		this.email = "";
	}
}
