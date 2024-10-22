package shop.nuribooks.books.dto.contributorrole;

import org.hibernate.validator.constraints.Length;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ContributorRoleReq {

	@NotNull
	@Length(max = 50)
	private String name;
}

