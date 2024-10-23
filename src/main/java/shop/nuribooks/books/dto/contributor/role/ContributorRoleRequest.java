package shop.nuribooks.books.dto.contributor.role;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ContributorRoleRequest {

	@NotNull
	@Length(min = 1, max = 50)
	private String name;
}

